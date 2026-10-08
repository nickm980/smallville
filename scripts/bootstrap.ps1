[CmdletBinding()]
param(
  [switch]$ForceNpmInstall
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$toolchain = & (Join-Path $PSScriptRoot 'Use-SmallvilleToolchain.ps1')

$projects = @(
  'dashboard',
  'javascript-client',
  'examples\javascript-phaser'
)

foreach ($project in $projects) {
  $projectPath = Join-Path $toolchain.RepoRoot $project
  $nodeModulesPath = Join-Path $projectPath 'node_modules'

  if ($ForceNpmInstall -or -not (Test-Path $nodeModulesPath)) {
    Write-Host "Installing npm dependencies in $project..."
    Push-Location $projectPath
    try {
      npm ci
    }
    finally {
      Pop-Location
    }
  }
  else {
    Write-Host "Dependencies already present in $project."
  }
}

$dashboardEnv = Join-Path $toolchain.RepoRoot 'dashboard\.env.local'
$dashboardEnvExample = Join-Path $toolchain.RepoRoot 'dashboard\.env.local.example'
if (-not (Test-Path $dashboardEnv) -and (Test-Path $dashboardEnvExample)) {
  Copy-Item $dashboardEnvExample $dashboardEnv
  Write-Host 'Created dashboard/.env.local from the example file.'
}

Write-Host ''
Write-Host "JAVA_HOME: $($toolchain.JavaHome)"
Write-Host "Maven:     $($toolchain.Maven)"
try {
  $ollamaVersion = (& ollama --version)
  Write-Host "Ollama:    $ollamaVersion"
}
catch {
  Write-Warning 'Ollama was not found on PATH. The default Smallville config now expects a local Ollama server.'
}
Write-Host 'Bootstrap complete.'
