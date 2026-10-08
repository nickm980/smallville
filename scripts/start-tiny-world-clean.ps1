[CmdletBinding()]
param(
  [int]$Port = 8092,
  [string]$RuntimeRoot = (Join-Path (Split-Path -Parent $PSScriptRoot) 'tmp\verify-tiny-world-clean'),
  [string[]]$LedgerSources,
  [switch]$SkipBuild
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Merge-GovernanceLedgers {
  param(
    [Parameter(Mandatory = $true)]
    [string[]]$SourcePaths,

    [Parameter(Mandatory = $true)]
    [string]$DestinationPath
  )

  $proposalById = [ordered]@{}
  $guidanceRules = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
  $blockedTargets = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)

  foreach ($sourcePath in $SourcePaths) {
    if ([string]::IsNullOrWhiteSpace($sourcePath) -or -not (Test-Path $sourcePath)) {
      continue
    }

    $ledger = Get-Content -Path $sourcePath -Raw | ConvertFrom-Json
    foreach ($proposal in @($ledger.proposals)) {
      $proposalId = [string]$proposal.id
      if (-not [string]::IsNullOrWhiteSpace($proposalId)) {
        $proposalById[$proposalId] = $proposal
      }
    }
    foreach ($rule in @($ledger.guidanceRules)) {
      $text = [string]$rule
      if (-not [string]::IsNullOrWhiteSpace($text)) {
        $guidanceRules.Add($text) | Out-Null
      }
    }
    foreach ($target in @($ledger.blockedProposalTargets)) {
      $text = [string]$target
      if (-not [string]::IsNullOrWhiteSpace($text)) {
        $blockedTargets.Add($text) | Out-Null
      }
    }
  }

  $merged = [ordered]@{
    proposals = @($proposalById.Values)
    guidanceRules = @($guidanceRules)
    blockedProposalTargets = @($blockedTargets)
  }

  $json = $merged | ConvertTo-Json -Depth 100
  $utf8NoBom = [System.Text.UTF8Encoding]::new($false)
  [System.IO.File]::WriteAllText($DestinationPath, $json, $utf8NoBom)
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
if (-not (Test-Path (Join-Path $scenarioDir 'config.yaml'))) {
  throw "Tiny world config not found in $scenarioDir"
}

$runtimeRoot = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($RuntimeRoot)
New-Item -ItemType Directory -Force -Path $runtimeRoot | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $runtimeRoot 'logs') | Out-Null

if (-not $LedgerSources -or $LedgerSources.Count -eq 0) {
  $defaultLedger = Join-Path $scenarioDir '.smallville-governance-ledger.json'
  if (Test-Path $defaultLedger) {
    $LedgerSources = @($defaultLedger)
  } else {
    $LedgerSources = @()
  }
}

$runtimeLedger = Join-Path $runtimeRoot '.smallville-governance-ledger.json'
if ($LedgerSources.Count -gt 0) {
  Merge-GovernanceLedgers -SourcePaths $LedgerSources -DestinationPath $runtimeLedger
  Write-Host "Seeded temp governance ledger at $runtimeLedger"
} elseif (Test-Path $runtimeLedger) {
  Remove-Item -LiteralPath $runtimeLedger -Force
}

Write-Host "Launching clean tiny-world host from $runtimeRoot on port $Port"

Push-Location $runtimeRoot
try {
  & $toolchain.Java '-jar' $jarPath.FullName '--port' $Port '--config-root' $scenarioDir
}
finally {
  Pop-Location
}
