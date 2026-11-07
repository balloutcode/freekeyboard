package com.freekeyboardballoutcode.ime

import android.content.ClipboardManager
import android.graphics.drawable.ColorDrawable
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InlineSuggestionsRequest
import android.view.inputmethod.InlineSuggestionsResponse
import android.widget.FrameLayout
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.freekeyboardballoutcode.data.ClipboardHistoryStore
import com.freekeyboardballoutcode.data.ClipboardSensitivity
import com.freekeyboardballoutcode.data.RejectedSuggestionsStore
import com.freekeyboardballoutcode.data.ThemePreference
import com.freekeyboardballoutcode.data.UserPreferencesRepository
import com.freekeyboardballoutcode.ime.autofill.InlineAutofillSupport
import com.freekeyboardballoutcode.ime.input.InputConnectionFacade
import com.freekeyboardballoutcode.ime.keyboard.KeyboardController
import com.freekeyboardballoutcode.ime.ui.GboardMetrics
import com.freekeyboardballoutcode.ime.ui.KeyboardRoot
import com.freekeyboardballoutcode.ui.theme.DarkBackground
import com.freekeyboardballoutcode.ui.theme.LightBackground
import com.freekeyboardballoutcode.ui.theme.OledBackground
import com.freekeyboardballoutcode.data.UserPreferences
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FreekeyboardImeService : InputMethodService() {
    private val owners = ImeLifecycleOwner()
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.Main.immediate)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val mainExecutor = Executor { mainHandler.post(it) }
    private val inlineInflateGeneration = AtomicInteger(0)
    /** Theme written from Features menu but not yet visible in DataStore emission. */
    private val pendingTheme = AtomicReference<ThemePreference?>(null)

    private lateinit var prefs: UserPreferencesRepository
    private lateinit var clipboardStore: ClipboardHistoryStore
    private lateinit var rejectedStore: RejectedSuggestionsStore
    private lateinit var controller: KeyboardController
    private var prefsJob: Job? = null
    private var keyboardRoot: FrameLayout? = null
    private var clipboardListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onCreate() {
        super.onCreate()
        owners.onCreate()
        prefs = UserPreferencesRepository(applicationContext)
        clipboardStore = ClipboardHistoryStore(applicationContext)
        rejectedStore = RejectedSuggestionsStore(applicationContext)
        val input = InputConnectionFacade(
            connectionProvider = { currentInputConnection },
            editorInfoProvider = { currentInputEditorInfo },
        )
        controller = KeyboardController(input)
        prefsJob = serviceScope.launch {
            prefs.preferences.collectLatest {
                val snapshot = withPendingTheme(it)
                val wasClipboard = controller.state.value.clipboardHistoryEnabled
                controller.applyPreferences(snapshot)
                when {
                    wasClipboard && !snapshot.clipboardHistoryEnabled -> {
                        clipboardStore.clearAll()
                        uninstallClipboardCapture()
                    }
                    !wasClipboard && snapshot.clipboardHistoryEnabled && isInputViewShown -> {
                        installClipboardCapture()
                    }
                }
                keyboardRoot?.let { root -> applyImeNavChrome(root, snapshot.theme) }
            }
        }
        serviceScope.launch {
            rejectedStore.rejected.collectLatest { controller.applyRejectedSuggestions(it) }
        }
        serviceScope.launch {
            clipboardStore.prunePersisted()
            clipboardStore.entries.collectLatest { controller.applyClipboardEntries(it) }
        }
        installViewTreeOwners(window?.window?.decorView)
        configureImeWindow()
    }

    override fun onCreateInputView(): View {
        configureImeWindow()
        installViewTreeOwners(window?.window?.decorView)
        owners.onResume()

        val root = FrameLayout(this).also { keyboardRoot = it }
        // Allow press-preview circles to draw into the suggestion-bar region (top row).
        root.clipChildren = false
        root.clipToPadding = false
        installViewTreeOwners(root)
        applyImeNavChrome(root, controller.state.value.theme)

        val composeView = ComposeView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            clipToOutline = false
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(owners))
            installViewTreeOwners(this)
            setContent {
                KeyboardRoot(
                    controller = controller,
                    scope = serviceScope,
                    onPersistLanguage = { prefs.setLastActiveLanguage(it) },
                    onPersistEmoji = { prefs.recordEmoji(it) },
                    onPersistTheme = { theme ->
                        pendingTheme.set(theme)
                        keyboardRoot?.let { root -> applyImeNavChrome(root, theme) }
                        try {
                            prefs.setTheme(theme)
                        } finally {
                            pendingTheme.compareAndSet(theme, null)
                        }
                    },
                    onPersistOneHanded = { enabled, side ->
                        prefs.setThemeAndOneHanded(oneHanded = enabled, side = side)
                    },
                    onRejectSuggestion = { word ->
                        val st = controller.state.value
                        // Session UI already hides the chip; only persist outside sensitive fields.
                        if (!st.suppressSuggestions && !st.isPasswordField && !st.incognito) {
                            rejectedStore.reject(word)
                        }
                    },
                    onPinClipboard = { entry ->
                        clipboardStore.setPinned(entry.id, !entry.pinned)
                    },
                    onRemoveClipboard = { entry ->
                        clipboardStore.remove(entry.id)
                    },
                )
            }
        }
        root.addView(composeView)
        return root
    }

    override fun onComputeInsets(outInsets: Insets) {
        super.onComputeInsets(outInsets)
        val input = keyboardRoot ?: return
        if (!isInputViewShown) return
        val loc = IntArray(2)
        input.getLocationInWindow(loc)
        // content/visible top = top of keyboard in the IME window. Apps pan/resize
        // content above this line. Using frameBottom made coverHeight=0 (field stayed under IME).
        // FRAME keeps the whole keyboard touchable (CONTENT + mid contentTop emptied touches).
        val top = if (input.height > 0) {
            loc[1]
        } else {
            // First frame: Compose may still report height 0 — estimate so hosts don't
            // leave the field under the eventual keyboard for a frame.
            val density = resources.displayMetrics.density
            val landscape = resources.configuration.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE
            val ui = if (::controller.isInitialized) controller.state.value else null
            // Toolbar/Features strip is shown even when word suggestions are suppressed.
            val contentDp = GboardMetrics.letterLayerContentHeightDp(
                landscape = landscape,
                numberRow = ui?.effectiveNumberRow == true,
                includeSuggestionStrip = true,
            )
            val estimated =
                (contentDp * density).toInt() + imeNavStripHeightPx()
            val windowH = window?.window?.decorView?.height
                ?: resources.displayMetrics.heightPixels
            // Recompute once Compose lays out so the estimate does not stick for a frame.
            input.post {
                if (isInputViewShown && input.height > 0) {
                    updateInputViewShown()
                }
            }
            (windowH - estimated).coerceAtLeast(0)
        }
        outInsets.contentTopInsets = top
        outInsets.visibleTopInsets = top
        outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_FRAME
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        if (::controller.isInitialized) {
            inlineInflateGeneration.incrementAndGet()
            controller.clearInlineSuggestions()
            controller.onStartInput()
        }
    }

    override fun onCreateInlineSuggestionsRequest(uiExtras: Bundle): InlineSuggestionsRequest? {
        if (!::controller.isInitialized) return null
        return InlineAutofillSupport.createInlineSuggestionsRequest(
            context = this,
            theme = controller.state.value.theme,
        )
    }

    override fun onInlineSuggestionsResponse(response: InlineSuggestionsResponse): Boolean {
        if (!::controller.isInitialized) return false
        val suggestions = response.inlineSuggestions
        if (suggestions.isEmpty()) {
            controller.clearInlineSuggestions()
            return false
        }
        val generation = inlineInflateGeneration.incrementAndGet()
        val density = resources.displayMetrics.density
        val inflateSize = InlineAutofillSupport.chipMinSizePx(density).toAndroidSize()
        val collected = arrayOfNulls<View>(suggestions.size)
        val remaining = AtomicInteger(suggestions.size)
        suggestions.forEachIndexed { index, suggestion ->
            suggestion.inflate(this, inflateSize, mainExecutor) { view ->
                if (generation != inlineInflateGeneration.get()) return@inflate
                if (view != null) {
                    collected[index] = view
                }
                if (remaining.decrementAndGet() == 0 && generation == inlineInflateGeneration.get()) {
                    controller.setInlineSuggestions(collected.filterNotNull())
                }
            }
        }
        return true
    }

    override fun onFinishInput() {
        inlineInflateGeneration.incrementAndGet()
        if (::controller.isInitialized) {
            controller.clearInlineSuggestions()
        }
        super.onFinishInput()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        configureImeWindow()
        installViewTreeOwners(window?.window?.decorView)
        installViewTreeOwners(keyboardRoot)
        keyboardRoot?.let { applyImeNavChrome(it, controller.state.value.theme) }
        owners.onResume()
        // DataStore is authoritative for one-hand/theme/etc. Re-sync when the keyboard
        // becomes visible so Settings toggles win over any stale in-memory runtime state
        // (e.g. Features menu enabled one-hand but a later Settings OFF must undock).
        syncPreferencesFromStore()
        installClipboardCapture()
        serviceScope.launch { runCatching { clipboardStore.prunePersisted() } }
        // Field state is reset in onStartInput only (avoid double reset / autofill flicker).
        super.onStartInputView(info, restarting)
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int,
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd,
        )
        if (::controller.isInitialized) {
            controller.onUpdateSelection()
        }
    }

    override fun onWindowShown() {
        configureImeWindow()
        installViewTreeOwners(window?.window?.decorView)
        installViewTreeOwners(keyboardRoot)
        keyboardRoot?.let { applyImeNavChrome(it, controller.state.value.theme) }
        owners.onResume()
        syncPreferencesFromStore()
        installClipboardCapture()
        super.onWindowShown()
    }

    /** Pull the latest prefs snapshot into the controller (single source of truth). */
    private fun syncPreferencesFromStore() {
        if (!::prefs.isInitialized || !::controller.isInitialized) return
        serviceScope.launch {
            runCatching {
                controller.applyPreferences(withPendingTheme(prefs.preferences.first()))
            }
        }
    }

    private fun withPendingTheme(snapshot: UserPreferences): UserPreferences {
        val pending = pendingTheme.get() ?: return snapshot
        return snapshot.copy(theme = pending)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        uninstallClipboardCapture()
        super.onFinishInputView(finishingInput)
        owners.onPause()
    }

    override fun onWindowHidden() {
        uninstallClipboardCapture()
        super.onWindowHidden()
        owners.onPause()
    }

    override fun onEvaluateFullscreenMode(): Boolean = false

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        updateFullscreenMode()
        updateInputViewShown()
        keyboardRoot?.let { applyImeNavChrome(it, controller.state.value.theme) }
    }

    override fun onDestroy() {
        uninstallClipboardCapture()
        prefsJob?.cancel()
        serviceScope.cancel()
        keyboardRoot = null
        owners.onDestroy()
        super.onDestroy()
    }

    /**
     * Capture only while the keyboard is visible (not for the whole IME process lifetime).
     * Skips sensitive clips, password/suppressed/incognito fields, and when history is off.
     */
    private fun installClipboardCapture() {
        if (clipboardListener != null) return
        if (!controller.state.value.clipboardHistoryEnabled) return
        val cm = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        val listener = ClipboardManager.OnPrimaryClipChangedListener {
            val st = controller.state.value
            if (!st.clipboardHistoryEnabled) return@OnPrimaryClipChangedListener
            if (st.incognito || st.isPasswordField || st.suppressSuggestions) {
                return@OnPrimaryClipChangedListener
            }
            val clip = cm.primaryClip ?: return@OnPrimaryClipChangedListener
            if (ClipboardSensitivity.isSensitive(clip.description)) {
                return@OnPrimaryClipChangedListener
            }
            val text = clip.takeIf { it.itemCount > 0 }
                ?.getItemAt(0)
                ?.coerceToText(this)
                ?.toString()
                .orEmpty()
            if (text.isNotBlank()) {
                serviceScope.launch { clipboardStore.capture(text) }
            }
        }
        clipboardListener = listener
        cm.addPrimaryClipChangedListener(listener)
    }

    private fun uninstallClipboardCapture() {
        clipboardListener?.let { listener ->
            val cm = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
            cm?.removePrimaryClipChangedListener(listener)
        }
        clipboardListener = null
    }

    /**
     * Reserve space for the system IME navigation strip (hide keyboard + switcher).
     * With mImeDrawsImeNavBar the framework overlays NavigationBarFrame on the bottom
     * of the input window — without this pad the switcher sits on the last key row
     * (Gboard avoids that by keeping keys above the 48dp strip). Always re-apply:
     * IMS may reset padding when attaching the input view.
     */
    private fun applyImeNavChrome(root: FrameLayout, theme: ThemePreference) {
        val navH = imeNavStripHeightPx()
        root.setPadding(0, 0, 0, navH)
        val bg = themeBackgroundArgb(theme)
        root.background = ColorDrawable(bg)
        configureImeWindow(bg, theme)
        // Framework draws NavigationBarFrame after our input view; tint once laid out.
        val tint = Runnable { tintNavigationBarFrame(bg) }
        root.removeCallbacks(tint)
        root.post(tint)
        root.viewTreeObserver.addOnGlobalLayoutListener(
            object : android.view.ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    tintNavigationBarFrame(bg)
                    if (root.viewTreeObserver.isAlive) {
                        root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            },
        )
    }

    private fun tintNavigationBarFrame(bg: Int) {
        val decor = window?.window?.decorView as? ViewGroup ?: return
        findNavigationBarFrame(decor)?.let { frame ->
            tintViewTreeBackground(frame, bg)
        }
    }

    private fun tintViewTreeBackground(view: View, bg: Int) {
        view.setBackgroundColor(bg)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                view.getChildAt(i)?.let { tintViewTreeBackground(it, bg) }
            }
        }
    }

    private fun configureImeWindow(
        navColor: Int = themeBackgroundArgb(controller.state.value.theme),
        theme: ThemePreference = controller.state.value.theme,
    ) {
        val w = window?.window ?: return
        WindowCompat.setDecorFitsSystemWindows(w, false)
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        w.navigationBarColor = navColor
        WindowCompat.getInsetsController(w, w.decorView).isAppearanceLightNavigationBars =
            theme == ThemePreference.LIGHT
    }

    private fun imeNavStripHeightPx(): Int {
        val density = resources.displayMetrics.density
        val fromDimen = runCatching {
            val id = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (id != 0) resources.getDimensionPixelSize(id) else 0
        }.getOrDefault(0)
        return maxOf(
            fromDimen,
            (GboardMetrics.IME_NAV_STRIP_DP * density).toInt(),
            GboardMetrics.IME_NAV_STRIP_MIN_PX,
        )
    }

    private fun themeBackgroundArgb(theme: ThemePreference): Int = when (theme) {
        ThemePreference.LIGHT -> LightBackground
        ThemePreference.DARK -> DarkBackground
        ThemePreference.OLED_DARK -> OledBackground
    }.toArgb()

    private fun findNavigationBarFrame(parent: ViewGroup): ViewGroup? {
        findNavigationBarByClassName(parent)?.let { return it }
        // Fallback when OEMs rename the frame: bottom full-width strip ≈ IME nav height.
        return findBottomNavStrip(parent, imeNavStripHeightPx())
    }

    private fun findNavigationBarByClassName(parent: ViewGroup): ViewGroup? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i) ?: continue
            val name = child.javaClass.name
            if (nameContainsImeNavHint(name)) {
                return child as? ViewGroup ?: continue
            }
            if (child is ViewGroup) {
                findNavigationBarByClassName(child)?.let { return it }
            }
        }
        return null
    }

    private fun nameContainsImeNavHint(name: String): Boolean {
        val n = name.lowercase()
        return n.contains("navigationbarframe") ||
            n.contains("inputmethodnav") ||
            n.contains("imenav") ||
            n.contains("navbuttonarea") ||
            (n.contains("navigation") && n.contains("ime"))
    }

    private fun findBottomNavStrip(parent: ViewGroup, navHeightPx: Int): ViewGroup? {
        if (navHeightPx <= 0) return null
        val parentH = parent.height
        if (parentH <= 0) return null
        var best: ViewGroup? = null
        var bestScore = Int.MAX_VALUE
        fun visit(group: ViewGroup) {
            for (i in 0 until group.childCount) {
                val child = group.getChildAt(i) ?: continue
                if (child is ViewGroup) {
                    val h = child.height
                    val w = child.width
                    if (h in (navHeightPx / 2)..(navHeightPx * 2) &&
                        w >= parent.width * 3 / 4
                    ) {
                        val loc = IntArray(2)
                        child.getLocationInWindow(loc)
                        val parentLoc = IntArray(2)
                        parent.getLocationInWindow(parentLoc)
                        val bottomGap = (parentLoc[1] + parentH) - (loc[1] + h)
                        if (bottomGap in 0..navHeightPx) {
                            val score = kotlin.math.abs(h - navHeightPx) + bottomGap
                            if (score < bestScore) {
                                bestScore = score
                                best = child
                            }
                        }
                    }
                    visit(child)
                }
            }
        }
        visit(parent)
        return best
    }

    private fun installViewTreeOwners(target: View?) {
        if (target == null) return
        target.setViewTreeLifecycleOwner(owners)
        target.setViewTreeViewModelStoreOwner(owners)
        target.setViewTreeSavedStateRegistryOwner(owners)
    }

}
