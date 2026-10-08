[CmdletBinding()]
param(
  [int]$Port = 8090,
  [switch]$SkipBuild
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$toolchain = & (Join-Path $PSScriptRoot 'Use-SmallvilleToolchain.ps1')

if (-not $SkipBuild) {
  & (Join-Path $PSScriptRoot 'build-server.ps1')
}

$jarPath = Get-ChildItem (Join-Path $toolchain.RepoRoot 'smallville\target') -Filter '*.jar' -ErrorAction SilentlyContinue |
  Where-Object { $_.Name -notlike 'original-*' } |
  Sort-Object LastWriteTime -Descending |
  Select-Object -First 1

if (-not $jarPath) {
  throw 'No Smallville server jar was found. Run .\scripts\build-server.ps1 first.'
}

$scenarioDir = Join-Path $toolchain.RepoRoot 'scenarios\tiny-world'

if (-not (Test-Path (Join-Path $scenarioDir 'config.yaml'))) {
  throw "Tiny world config not found in $scenarioDir"
}

Push-Location $scenarioDir
try {
  & $toolchain.Java '-jar' $jarPath.FullName '--port' $Port
}
finally {
  Pop-Location
}
