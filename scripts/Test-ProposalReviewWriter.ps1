[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$writerPath = Join-Path $PSScriptRoot 'Write-ProposalReview.ps1'
$testRoot = Join-Path $env:TEMP ('proposal-review-writer-' + [guid]::NewGuid().ToString('N'))
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

function New-Proposal {
  param(
    [string]$Agent,
    [string]$Type,
    [string]$ParentLocation,
    [string]$Name,
    [string]$ProposedState,
    [string]$Reason
  )

  return [pscustomobject]@{
    agent = $Agent
    type = $Type
    parentLocation = $ParentLocation
    name = $Name
    proposedState = $ProposedState
    reason = $Reason
  }
}

$results = @()

try {
  $zeroPath = Join-Path $testRoot 'zero.md'
  & $writerPath -RunId 'run-zero' -TickObserved 1 -OutputPath $zeroPath -Proposals $null
  $zeroContent = Get-Content $zeroPath
  Assert-ContainsLine -Content $zeroContent -Expected 'proposal_count: 0'
  Assert-ContainsLine -Content $zeroContent -Expected '- none observed during review'
  $results += '0 proposals: proposal_count = 0 and notes rendered'

  $onePath = Join-Path $testRoot 'one.md'
  $oneProposal = New-Proposal -Agent 'Jamie' -Type 'add_location' -ParentLocation 'Garden' -Name 'Tool Shelf' -ProposedState 'ready' -Reason 'To keep tools orderly between watering rounds.'
  & $writerPath -RunId 'run-one' -TickObserved 2 -OutputPath $onePath -Proposals $oneProposal
  $oneContent = Get-Content $onePath
  Assert-ContainsLine -Content $oneContent -Expected 'proposal_count: 1'
  Assert-ContainsLine -Content $oneContent -Expected '- pending: [Jamie] add_location parent=[Garden] name=[Tool Shelf] state=[ready] reason=[To keep tools orderly between watering rounds.]'
  $results += '1 proposal: proposal_count = 1 and note rendered'

  $manyPath = Join-Path $testRoot 'many.md'
  $manyProposals = @(
    (New-Proposal -Agent 'Alex' -Type 'add_location' -ParentLocation 'Green House' -Name 'Tray Shelf' -ProposedState 'tidy' -Reason 'To stage propagation trays for review.'),
    (New-Proposal -Agent 'Jamie' -Type 'change_state' -ParentLocation 'Garden: Gate' -Name 'Garden: Gate' -ProposedState 'Closed' -Reason 'To keep the path settled during evening checks.')
  )
  & $writerPath -RunId 'run-many' -TickObserved 3 -OutputPath $manyPath -Proposals $manyProposals
  $manyContent = Get-Content $manyPath
  Assert-ContainsLine -Content $manyContent -Expected 'proposal_count: 2'
  Assert-ContainsLine -Content $manyContent -Expected '- pending: [Alex] add_location parent=[Green House] name=[Tray Shelf] state=[tidy] reason=[To stage propagation trays for review.]'
  Assert-ContainsLine -Content $manyContent -Expected '- pending: [Jamie] change_state parent=[Garden: Gate] name=[Garden: Gate] state=[Closed] reason=[To keep the path settled during evening checks.]'
  $results += '2 proposals: proposal_count = 2 and notes rendered'

  $results | ForEach-Object { Write-Output $_ }
} finally {
  if (Test-Path $testRoot) {
    Remove-Item -Path $testRoot -Recurse -Force
  }
}
