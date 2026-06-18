import { createHash } from 'node:crypto'
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const resultsDir = path.join(root, 'allure-results')
const stagingDir = path.join(root, process.env.ALLURE_STAGING_DIR ?? 'allure-results-staging')
const projectName = process.env.ALLURE_PROJECT_NAME ?? 'rest-assured-automation-suite'

const SKIP_FILES = new Set(['environment.properties', 'executor.json'])

function walkFiles(dir) {
  if (!fs.existsSync(dir)) return []

  const files = []
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const fullPath = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      files.push(...walkFiles(fullPath))
    } else if (entry.isFile()) {
      files.push(fullPath)
    }
  }
  return files
}

function resolveArtifactRoot(dir) {
  const nested = path.join(dir, 'allure-results')
  if (fs.existsSync(nested) && fs.statSync(nested).isDirectory()) {
    return nested
  }
  return dir
}

function suiteFromStagingDir(dirName) {
  const match = dirName.match(/^allure-results-(.+)$/)
  return match?.[1] ?? dirName
}

function scopeResultToSuite(result, suite) {
  if (!Array.isArray(result.parameters)) {
    result.parameters = []
  }

  const existing = result.parameters.find((param) => param.name === 'suite')
  if (existing) {
    existing.value = `'${suite}'`
  } else {
    result.parameters.push({ name: 'suite', value: `'${suite}'` })
  }

  const labels = Array.isArray(result.labels) ? result.labels : []
  const label = labels.find((entry) => entry.name === 'suite')
  if (label) {
    label.value = suite
  } else {
    labels.push({ name: 'suite', value: suite })
  }
  result.labels = labels

  const identity = [
    result.historyId ?? '',
    result.testCaseId ?? '',
    result.fullName ?? '',
    result.name ?? '',
    suite,
  ].join('::')

  result.historyId = createHash('md5').update(identity).digest('hex')
  result.testCaseId = createHash('md5')
    .update(`${result.testCaseId ?? result.uuid ?? identity}::${suite}`)
    .digest('hex')

  if (result.name && !result.name.includes(`[${suite}]`)) {
    result.name = `${result.name} [${suite}]`
  }
}

function uniqueTargetPath(targetPath) {
  if (!fs.existsSync(targetPath)) return targetPath

  const dir = path.dirname(targetPath)
  const ext = path.extname(targetPath)
  const base = path.basename(targetPath, ext)
  let index = 1

  while (fs.existsSync(path.join(dir, `${base}-${index}${ext}`))) {
    index += 1
  }

  return path.join(dir, `${base}-${index}${ext}`)
}

function copyArtifactTree(sourceDir, targetDir, suite) {
  const artifactRoot = resolveArtifactRoot(sourceDir)
  if (!fs.existsSync(artifactRoot)) return 0

  let copied = 0
  for (const filePath of walkFiles(artifactRoot)) {
    const rel = path.relative(artifactRoot, filePath)
    const fileName = path.basename(filePath)
    if (SKIP_FILES.has(fileName)) continue

    const targetPath = uniqueTargetPath(path.join(targetDir, rel))
    fs.mkdirSync(path.dirname(targetPath), { recursive: true })

    if (fileName.endsWith('-result.json')) {
      const result = JSON.parse(fs.readFileSync(filePath, 'utf8'))
      scopeResultToSuite(result, suite)
      fs.writeFileSync(targetPath, `${JSON.stringify(result)}\n`)
    } else {
      fs.copyFileSync(filePath, targetPath)
    }

    copied += 1
  }

  return copied
}

function mergeFromStaging() {
  if (!fs.existsSync(stagingDir)) return false

  const suiteDirs = fs
    .readdirSync(stagingDir, { withFileTypes: true })
    .filter((entry) => entry.isDirectory())
    .map((entry) => entry.name)
    .sort()

  if (suiteDirs.length === 0) return false

  fs.rmSync(resultsDir, { recursive: true, force: true })
  fs.mkdirSync(resultsDir, { recursive: true })

  let copied = 0
  for (const dirName of suiteDirs) {
    const suite = suiteFromStagingDir(dirName)
    const count = copyArtifactTree(path.join(stagingDir, dirName), resultsDir, suite)
    console.log(`  ${dirName}: ${count} file(s) tagged as ${suite}`)
    copied += count
  }

  console.log(`Merged ${copied} file(s) from ${suiteDirs.length} artifact(s)`)
  return copied > 0
}

function summarizeResults() {
  const resultFiles = walkFiles(resultsDir).filter((filePath) => filePath.endsWith('-result.json'))
  const suites = new Map()

  for (const filePath of resultFiles) {
    const result = JSON.parse(fs.readFileSync(filePath, 'utf8'))
    const suite =
      result.labels?.find((entry) => entry.name === 'suite')?.value ??
      result.parameters?.find((entry) => entry.name === 'suite')?.value?.replace(/^'+|'+$/g, '') ??
      'api'
    suites.set(suite, (suites.get(suite) ?? 0) + 1)
  }

  return { total: resultFiles.length, suites }
}

function writeEnvironment({ total, suites }) {
  const lines = [
    `Project=${projectName}`,
    `Total.tests=${total}`,
    `Suites=${[...suites.keys()].sort().join(', ')}`,
  ]

  for (const [suite, count] of [...suites.entries()].sort(([a], [b]) => a.localeCompare(b))) {
    lines.push(`Tests.${suite}=${count}`)
  }

  const githubSha = process.env.GITHUB_SHA
  if (githubSha) lines.push(`GitHub.SHA=${githubSha.slice(0, 7)}`)

  const githubRef = process.env.GITHUB_REF_NAME
  if (githubRef) lines.push(`GitHub.Ref=${githubRef}`)

  fs.writeFileSync(path.join(resultsDir, 'environment.properties'), `${lines.join('\n')}\n`)
}

function main() {
  const mergedFromStaging = mergeFromStaging()
  if (!mergedFromStaging && !fs.existsSync(resultsDir)) {
    console.log('No allure-results to merge')
    process.exit(0)
  }

  const summary = summarizeResults()
  if (summary.total === 0) {
    console.log('No Allure result files found after merge')
    process.exit(1)
  }

  writeEnvironment(summary)
  console.log(`Allure merge summary: ${summary.total} test(s) across ${summary.suites.size} suite(s)`)
  for (const [suite, count] of [...summary.suites.entries()].sort(([a], [b]) => a.localeCompare(b))) {
    console.log(`  - ${suite}: ${count}`)
  }
}

main()
