# Inline autofill — emulator smoke (2026-11-07)

**Device**: `emulator-5554` (NoJunkie `Medium_Phone`)  
**APK**: debug `com.freekeyboardballoutcode` installed + set as default IME  
**Autofill service**: `com.google.android.gms/.autofill.service.AutofillService`

## Results

| Check | Result | Notes |
|-------|--------|--------|
| NoJunkie boot | **PASS** | `emulator-5554` device, `boot_completed=1` |
| APK install + IME select | **PASS** | `ime set` → freekeyboard |
| System sees IME inline support | **PASS** | `dumpsys input_method`: `mInlineSuggestionsEnabled=true` for freekeyboard (same flag as Gboard) |
| Platform autofill inline flag | **PASS** | `dumpsys autofill`: `Inline Suggestions Enabled: true` |
| Login page + freekeyboard shown | **PASS** | Chrome `http://127.0.0.1:8765/` form; IME bound (`mBoundToMethod=true`) |
| Inline chips from password manager | **BLOCKED** | AVD has **no** Proton Pass / Bitwarden and **Max visible datasets: 0** — Google Autofill has nothing to return as inline datasets |
| Chrome accessory | Observed | Chrome’s own bar («Show saved passwords…») above IME — host UI, not IME inline chips |

## Conclusion

IME-side wiring is live on device (system advertisement + request path). End-to-end chip UI needs a provider with saved credentials that supports inline (e.g. Proton Pass on a real phone, or Google Password Manager with saved logins). On this stock emulator image that provider data is absent, so chips cannot appear.

## Artifacts

- `autofill-login.html`, `autofill-05-page.png`, `autofill-06/07-*-focus.png`
- `autofill-ui-*.xml`
