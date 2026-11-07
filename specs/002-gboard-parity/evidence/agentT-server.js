const http=require("http");const fs=require("fs");const path=require("path");
const dir=path.join(process.cwd(),"specs/002-gboard-parity/evidence");
http.createServer((req,res)=>{
  const f=path.join(dir, decodeURIComponent((req.url||"/").split("?")[0].replace(/^\//,""))||"agentT-field-test.html");
  fs.readFile(f,(e,d)=>{ if(e){res.writeHead(404);res.end("missing");return;} 
    res.writeHead(200,{"Content-Type":"text/html; charset=utf-8"}); res.end(d); });
}).listen(8765,"0.0.0.0",()=>console.log("listening 8765"));
