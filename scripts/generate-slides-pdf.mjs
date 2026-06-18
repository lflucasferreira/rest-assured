#!/usr/bin/env node
/**
 * Generates docs/slides/rest-assured-intro-slides.pdf from Reveal.js deck via decktape.
 */
import { spawn } from 'node:child_process';
import { createConnection } from 'node:net';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import fs from 'node:fs';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const port = 3335;
const slidesUrl = `http://127.0.0.1:${port}/docs/slides/index.html`;
const outputPath = path.join(root, 'docs/slides/rest-assured-intro-slides.pdf');

function waitForPort(targetPort, timeoutMs = 30000) {
  const started = Date.now();
  return new Promise((resolve, reject) => {
    const attempt = () => {
      const socket = createConnection({ port: targetPort, host: '127.0.0.1' });
      socket.once('connect', () => {
        socket.end();
        resolve();
      });
      socket.once('error', () => {
        socket.destroy();
        if (Date.now() - started > timeoutMs) {
          reject(new Error(`Timed out waiting for port ${targetPort}`));
          return;
        }
        setTimeout(attempt, 300);
      });
    };
    attempt();
  });
}

function run(command, args, options = {}) {
  return new Promise((resolve, reject) => {
    const child = spawn(command, args, { stdio: 'inherit', ...options });
    child.on('error', reject);
    child.on('close', (code) => {
      if (code === 0) {
        resolve();
      } else {
        reject(new Error(`${command} ${args.join(' ')} exited with code ${code}`));
      }
    });
  });
}

const server = spawn('npx', ['serve', '.', '-p', String(port), '--no-clipboard'], {
  cwd: root,
  stdio: 'ignore',
});

try {
  await waitForPort(port);
  console.log(`Exporting slides from ${slidesUrl}`);
  await run('npx', [
    '--yes',
    'decktape',
    'reveal',
    slidesUrl,
    outputPath,
    '--chrome-arg=--no-sandbox',
    '--chrome-arg=--disable-gpu',
  ], { cwd: root });

  if (!fs.existsSync(outputPath)) {
    throw new Error(`PDF was not created at ${outputPath}`);
  }
  const stats = fs.statSync(outputPath);
  console.log(`PDF generated: ${outputPath} (${Math.round(stats.size / 1024)} KB)`);
} finally {
  server.kill('SIGTERM');
}
