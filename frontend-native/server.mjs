import { createServer } from 'node:http';
import { createReadStream, existsSync, statSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const PORT = Number(process.env.PORT || 5501);
const BACKEND_BASE_URL = process.env.BACKEND_BASE_URL || 'http://127.0.0.1:8080';

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
};

const NO_CACHE_HEADERS = {
  'Cache-Control': 'no-store, no-cache, must-revalidate',
  Pragma: 'no-cache',
  Expires: '0'
};

function safeFilePath(urlPathname) {
  const pathname = decodeURIComponent(urlPathname.split('?')[0]);
  const relativePath = pathname === '/' ? '/index.html' : pathname;
  const resolvedPath = path.normalize(path.join(__dirname, relativePath));
  if (!resolvedPath.startsWith(__dirname)) {
    return null;
  }
  return resolvedPath;
}

async function readRequestBody(req) {
  const chunks = [];
  for await (const chunk of req) {
    chunks.push(chunk);
  }
  return chunks.length > 0 ? Buffer.concat(chunks) : undefined;
}

function filterResponseHeaders(headers) {
  const result = {};
  for (const [key, value] of headers.entries()) {
    if (key.toLowerCase() === 'content-encoding') {
      continue;
    }
    result[key] = value;
  }
  return result;
}

async function proxyApi(req, res) {
  const url = new URL(req.url, BACKEND_BASE_URL);
  const body = req.method === 'GET' || req.method === 'HEAD' ? undefined : await readRequestBody(req);
  const headers = { ...req.headers };
  delete headers.host;
  delete headers.connection;
  delete headers['content-length'];

  try {
    console.log(`[proxy] ${req.method} ${req.url} -> ${url}`);
    const upstream = await fetch(url, {
      method: req.method,
      headers,
      body
    });

    const responseHeaders = filterResponseHeaders(upstream.headers);
    console.log(`[proxy] ${req.method} ${req.url} <- ${upstream.status}`);
    res.writeHead(upstream.status, responseHeaders);

    if (req.method === 'HEAD') {
      res.end();
      return;
    }

    const arrayBuffer = await upstream.arrayBuffer();
    res.end(Buffer.from(arrayBuffer));
  } catch (error) {
    console.error(`[proxy] ${req.method} ${req.url} !! ${error.message}`);
    res.writeHead(502, {
      'Content-Type': 'application/json; charset=utf-8',
      ...NO_CACHE_HEADERS
    });
    res.end(JSON.stringify({
      code: 502,
      message: `代理后端失败：${error.message}`,
      data: null
    }));
  }
}

function serveStatic(req, res) {
  const filePath = safeFilePath(req.url || '/');
  if (!filePath || !existsSync(filePath) || !statSync(filePath).isFile()) {
    res.writeHead(404, {
      'Content-Type': 'text/plain; charset=utf-8',
      ...NO_CACHE_HEADERS
    });
    res.end('Not Found');
    return;
  }

  const ext = path.extname(filePath).toLowerCase();
  res.writeHead(200, {
    'Content-Type': MIME_TYPES[ext] || 'application/octet-stream',
    ...NO_CACHE_HEADERS
  });
  createReadStream(filePath).pipe(res);
}

const server = createServer(async (req, res) => {
  console.log(`[http] ${req.socket.remoteAddress} ${req.method} ${req.url}`);
  if (!req.url) {
    res.writeHead(400, {
      'Content-Type': 'text/plain; charset=utf-8',
      ...NO_CACHE_HEADERS
    });
    res.end('Bad Request');
    return;
  }

  if (req.url.startsWith('/api/')) {
    await proxyApi(req, res);
    return;
  }

  if (req.method !== 'GET' && req.method !== 'HEAD') {
    res.writeHead(405, {
      'Content-Type': 'text/plain; charset=utf-8',
      Allow: 'GET, HEAD',
      ...NO_CACHE_HEADERS
    });
    res.end('Method Not Allowed');
    return;
  }

  serveStatic(req, res);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`frontend-native server listening on http://0.0.0.0:${PORT}`);
  console.log(`proxying /api to ${BACKEND_BASE_URL}`);
});
