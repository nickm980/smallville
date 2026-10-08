[CmdletBinding()]
param(
  [int]$Port = 8093,
  [string]$RuntimeRoot = (Join-Path (Split-Path -Parent $PSScriptRoot) 'tmp\verify-tiny-world-offline'),
  [string]$ExportRoot = '',
  [switch]$SkipBuild
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Set-YamlScalar {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Content,

    [Parameter(Mandatory = $true)]
    [string]$Key,

    [Parameter(Mandatory = $true)]
    [string]$Value
  )

  $pattern = "(?m)^$([regex]::Escape($Key))\s*:\s*.*$"
  if ($Content -match $pattern) {
    return [regex]::Replace($Content, $pattern, "$Key: $Value")
  }

  return ($Content.TrimEnd() + [Environment]::NewLine + "$Key: $Value" + [Environment]::NewLine)
}

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
$runtimeRoot = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($RuntimeRoot)
$exportRoot = if ([string]::IsNullOrWhiteSpace($ExportRoot)) {
  Join-Path $runtimeRoot 'exports'
} else {
  $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($ExportRoot)
}
$configRoot = Join-Path $runtimeRoot 'offline-config'

New-Item -ItemType Directory -Force -Path $runtimeRoot | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $runtimeRoot 'logs') | Out-Null
New-Item -ItemType Directory -Force -Path $exportRoot | Out-Null
New-Item -ItemType Directory -Force -Path $configRoot | Out-Null

$configText = Get-Content (Join-Path $scenarioDir 'config.yaml') -Raw
$configText = Set-YamlScalar -Content $configText -Key 'cloudSupportEnabled' -Value 'false'
$configText = Set-YamlScalar -Content $configText -Key 'askShadowBridgeEnabled' -Value 'false'
$configText = Set-YamlScalar -Content $configText -Key 'offlineMode' -Value 'true'
$configText = Set-YamlScalar -Content $configText -Key 'loopbackOnly' -Value 'true'

$utf8NoBom = [System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText((Join-Path $configRoot 'config.yaml'), $configText, $utf8NoBom)
Copy-Item (Join-Path $scenarioDir 'simulation.yaml') (Join-Path $configRoot 'simulation.yaml') -Force

$env:SMALLVILLE_LOCAL_EXPORT_ROOT = $exportRoot
Write-Host "Offline tiny-world runtime root: $runtimeRoot"
Write-Host "Offline tiny-world export root: $exportRoot"
Write-Host "Offline tiny-world config root: $configRoot"

Push-Location $runtimeRoot
try {
  & $toolchain.Java '-jar' $jarPath.FullName '--port' $Port '--config-root' $configRoot
}
finally {
  Pop-Location
}
