[CmdletBinding()]
param(
  [string]$ApiKey = $env:OPENAI_API_KEY,
  [int]$Port = 8080,
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

$arguments = @(
  '-jar',
  $jarPath.FullName,
  '--port',
  $Port
)

if (-not [string]::IsNullOrWhiteSpace($ApiKey)) {
  $arguments += @('--api-key', $ApiKey)
}

& $toolchain.Java @arguments
