const http = require('http');
const fs = require('fs');
const path = require('path');
const root = process.argv[2] || '.';
http.createServer((req,res)=>{
  const p = path.join(root, decodeURIComponent((req.url||'/').split('?')[0]));
  const file = p.endsWith('/') || p.endsWith('\\') ? path.join(p,'field-test.html') : p;
  fs.readFile(file,(e,d)=>{
    if(e){res.writeHead(404);res.end('nf');return;}
    res.writeHead(200,{'Content-Type':'text/html; charset=utf-8'});
    res.end(d);
  });
}).listen(8766,'127.0.0.1',()=>console.log('listening 8766'));
