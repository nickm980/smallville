[CmdletBinding()]
param(
  [int]$Port = 3000,
  [string]$SmallvilleUrl = 'http://localhost:8090'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $repoRoot 'dashboard\.env.local'
$envExample = Join-Path $repoRoot 'dashboard\.env.local.example'

if (-not (Test-Path $envFile) -and (Test-Path $envExample)) {
  Copy-Item $envExample $envFile
}

if (-not [string]::IsNullOrWhiteSpace($SmallvilleUrl)) {
  $env:NEXT_PUBLIC_SMALLVILLE_URL = $SmallvilleUrl
}

Push-Location (Join-Path $repoRoot 'dashboard')
try {
  npm run dev -- --port $Port
}
finally {
  Pop-Location
}
