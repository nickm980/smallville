[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$writerPath = Join-Path $PSScriptRoot 'Write-GovernanceReview.ps1'
$testRoot = Join-Path $env:TEMP ('governance-review-writer-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $testRoot -Force | Out-Null

function Assert-ContainsLine {
  param(
    [string[]]$Content,
    [string]$Expected
  )

  if ($Content -notcontains $Expected) {
    throw "Expected line not found: $Expected"
  }
}

function New-Entry {
  param(
    [string]$Status,
    [string]$Agent,
    [string]$Type,
    [string]$ParentLocation,
    [string]$Name,
    [string]$State,
    [string]$Reason,
    [string]$Tick,
    [string]$Time
  )

  return [pscustomobject]@{
    status = $Status
    agent = $Agent
    type = $Type
    parentLocation = $ParentLocation
    name = $Name
    state = $State
    reason = $Reason
    tick = $Tick
    time = $Time
  }
}

$results = @()

try {
  $zeroPath = Join-Path $testRoot 'zero.md'
  & $writerPath -RunId 'run-zero' -TickObserved 3 -OutputPath $zeroPath -Entries $null -HistorySource '/world/governance/review?windowHours=24'
  $zeroContent = Get-Content $zeroPath
  Assert-ContainsLine -Content $zeroContent -Expected 'governance_count: 0'
  Assert-ContainsLine -Content $zeroContent -Expected '- no governance history entries were surfaced during the review window'
  $results += '0 entries: governance_count = 0 and note rendered'

  $onePath = Join-Path $testRoot 'one.md'
  $oneEntries = @(
    (New-Entry -Status 'applied' -Agent 'Alex' -Type 'add_location' -ParentLocation 'Garden' -Name 'Raised Planter' -State 'empty' -Reason 'To create a dedicated transplanting space.' -Tick '3' -Time '10:34 am'),
    (New-Entry -Status 'removed' -Agent 'Jamie' -Type 'add_location' -ParentLocation 'Garden: South Bed' -Name 'Raised Planter' -State 'empty' -Reason 'To keep the south bed tidy.' -Tick '3' -Time '10:35 am'),
    (New-Entry -Status 'blocked' -Agent 'Alex' -Type 'change_state' -ParentLocation 'Garden: Gate' -Name 'Garden: Gate' -State 'Closed' -Reason 'Blocked by governance review.' -Tick '3' -Time '10:36 am')
  )
  & $writerPath -RunId 'run-one' -TickObserved 3 -OutputPath $onePath -Entries $oneEntries -HistorySource '/world/governance/review?windowHours=24' -ReviewWindowHours 24 -ReviewWindowStartUtc '2026-03-27T00:00:00Z' -ReviewWindowEndUtc '2026-03-27T23:59:59Z'
  $oneContent = Get-Content $onePath
  Assert-ContainsLine -Content $oneContent -Expected 'review_window_hours: 24'
  Assert-ContainsLine -Content $oneContent -Expected 'governance_count: 3'
  Assert-ContainsLine -Content $oneContent -Expected 'applied_count: 1'
  Assert-ContainsLine -Content $oneContent -Expected 'removed_count: 1'
  Assert-ContainsLine -Content $oneContent -Expected 'blocked_count: 1'
  Assert-ContainsLine -Content $oneContent -Expected '- applied: [Alex] add_location parent=[Garden] name=[Raised Planter] state=[empty] tick=[3] time=[10:34 am] reason=[To create a dedicated transplanting space.]'
  Assert-ContainsLine -Content $oneContent -Expected '- removed: [Jamie] add_location parent=[Garden: South Bed] name=[Raised Planter] state=[empty] tick=[3] time=[10:35 am] reason=[To keep the south bed tidy.]'
  Assert-ContainsLine -Content $oneContent -Expected '- blocked: [Alex] change_state parent=[Garden: Gate] name=[Garden: Gate] state=[Closed] tick=[3] time=[10:36 am] reason=[Blocked by governance review.]'
  $results += '3 entries: applied/removed/blocked counts and notes rendered'

  $results | ForEach-Object { Write-Output $_ }
} finally {
  if (Test-Path $testRoot) {
    Remove-Item -Path $testRoot -Recurse -Force
  }
}
