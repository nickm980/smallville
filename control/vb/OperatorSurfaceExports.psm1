Set-StrictMode -Version Latest

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceSlice1.psm1") -Force

function Get-ExportValue {
    param([AllowNull()]$Value)

    if ($null -eq $Value) {
        return ""
    }

    if ($Value -is [string]) {
        return $Value
    }

    if ($Value -is [System.Collections.IEnumerable] -and -not ($Value -is [string])) {
        return (@($Value | ForEach-Object { if ($null -ne $_) { [string]$_ } }) -join " | ")
    }

    return [string]$Value
}

function Get-ViewProperty {
    param(
        [AllowNull()]$Object,
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    if ($null -eq $Object) {
        return $null
    }

    $property = $Object.PSObject.Properties[$Name]
    if ($null -eq $property) {
        return $null
    }

    return $property.Value
}

function Get-AbsolutePath {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if ([System.IO.Path]::IsPathRooted($Path)) {
        return [System.IO.Path]::GetFullPath($Path)
    }

    return [System.IO.Path]::GetFullPath((Join-Path (Get-Location).Path $Path))
}

function Test-IsPathWithin {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$Root
    )

    $fullPath = [System.IO.Path]::GetFullPath($Path).TrimEnd('\')
    $fullRoot = [System.IO.Path]::GetFullPath($Root).TrimEnd('\')

    return $fullPath.Equals($fullRoot, [System.StringComparison]::OrdinalIgnoreCase) -or
        $fullPath.StartsWith($fullRoot + "\", [System.StringComparison]::OrdinalIgnoreCase)
}

function Resolve-ExportOutputDirectory {
    param(
        [Parameter(Mandatory = $true)]
        [string]$OutputDirectory
    )

    $repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\\..")).Path
    $resolvedOutput = Get-AbsolutePath -Path $OutputDirectory
    $protectedRoots = @(
        (Join-Path $repoRoot "runs"),
        (Join-Path $repoRoot "baselines")
    )

    foreach ($root in $protectedRoots) {
        if (Test-IsPathWithin -Path $resolvedOutput -Root $root) {
            throw "Output directory [$resolvedOutput] must stay outside source evidence roots [$root]."
        }
    }

    if (-not (Test-Path -LiteralPath $resolvedOutput)) {
        New-Item -ItemType Directory -Path $resolvedOutput -Force | Out-Null
    }

    return (Resolve-Path -LiteralPath $resolvedOutput).Path
}

function ConvertTo-SafeFileStem {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Value
    )

    $safe = $Value
    foreach ($char in [System.IO.Path]::GetInvalidFileNameChars()) {
        $safe = $safe.Replace([string]$char, "-")
    }

    return $safe
}

function Get-WarningColumnMap {
    param(
        [AllowEmptyCollection()]
        [object[]]$Warnings,

        [Parameter(Mandatory = $true)]
        [string]$Prefix
    )

    $warningsList = @($Warnings)
    $codes = @($warningsList | ForEach-Object { Get-ViewProperty -Object $_ -Name "code" } | Where-Object { -not [string]::IsNullOrWhiteSpace([string]$_) })
    $owners = @($warningsList | ForEach-Object { Get-ViewProperty -Object $_ -Name "owner" } | Where-Object { -not [string]::IsNullOrWhiteSpace([string]$_) })
    $messages = @($warningsList | ForEach-Object { Get-ViewProperty -Object $_ -Name "message" } | Where-Object { -not [string]::IsNullOrWhiteSpace([string]$_) })
    $paths = @(
        $warningsList | ForEach-Object {
            $warningPaths = Get-ViewProperty -Object $_ -Name "paths"
            foreach ($path in @($warningPaths)) {
                if (-not [string]::IsNullOrWhiteSpace([string]$path)) {
                    [string]$path
                }
            }
        } | Select-Object -Unique
    )

    return [ordered]@{
        ($Prefix + "_warning_count") = $warningsList.Count
        ($Prefix + "_warning_codes") = (Get-ExportValue -Value $codes)
        ($Prefix + "_warning_owners") = (Get-ExportValue -Value $owners)
        ($Prefix + "_warning_messages") = (Get-ExportValue -Value $messages)
        ($Prefix + "_warning_paths") = (Get-ExportValue -Value $paths)
    }
}

function New-ExportRow {
    param(
        [Parameter(Mandatory = $true)]
        [hashtable]$Base,

        [Parameter(Mandatory = $true)]
        [hashtable]$WarningColumns,

        [hashtable]$Extra = @{}
    )

    $row = [ordered]@{}
    foreach ($pair in $Base.GetEnumerator()) {
        $row[$pair.Key] = $pair.Value
    }
    foreach ($pair in $Extra.GetEnumerator()) {
        $row[$pair.Key] = $pair.Value
    }
    foreach ($pair in $WarningColumns.GetEnumerator()) {
        $row[$pair.Key] = $pair.Value
    }

    return [pscustomobject]$row
}

function Write-ExportCsv {
    param(
        [Parameter(Mandatory = $true)]
        [object[]]$Rows,

        [Parameter(Mandatory = $true)]
        [string]$OutputDirectory,

        [Parameter(Mandatory = $true)]
        [string]$FileName
    )

    $resolvedOutput = Resolve-ExportOutputDirectory -OutputDirectory $OutputDirectory
    $exportPath = Join-Path $resolvedOutput $FileName
    @($Rows) | Export-Csv -LiteralPath $exportPath -NoTypeInformation -Encoding UTF8
    return $exportPath
}

function Export-RunBundleSummaryCsv {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath,

        [Parameter(Mandatory = $true)]
        [string]$OutputDirectory
    )

    $view = Get-RunBundleSummaryView -BundlePath $BundlePath
    $warningColumns = Get-WarningColumnMap -Warnings @($view.warnings) -Prefix "bundle"
    $row = New-ExportRow -Base ([ordered]@{
            bundle_path = $view.bundle_path
            run_id = $view.run_id
            scenario_name = $view.scenario_name
            launch_mode = $view.launch_mode
            frozen_model = $view.frozen_model
            smallville_port = $view.smallville_port
            launch_command = $view.launch_command
            startup_result = $view.startup_result
            startup_healthy_at_utc = $view.startup_healthy_at_utc
            tick_count_captured = $view.tick_count_captured
            highest_world_tick = $view.highest_world_tick
            highest_state_post_tick = $view.highest_state_post_tick
            restart_captured = $view.restart_captured
            shutdown_port_closed = $view.shutdown_port_closed
            shutdown_process_gone = $view.shutdown_process_gone
            proposal_count_raw = $view.proposal_count_raw
            proposal_count = $view.proposal_count
            artifact_manifest = $view.artifact_paths.manifest
            artifact_proposal_review = $view.artifact_paths.proposal_review
            artifact_operator_notes = $view.artifact_paths.operator_notes
            artifact_endpoint_world_cold = $view.artifact_paths.endpoint_world_cold
            artifact_latest_world_tick = $view.artifact_paths.latest_world_tick
            artifact_endpoint_world_restart = $view.artifact_paths.endpoint_world_restart
        }) -WarningColumns $warningColumns

    $fileName = "$(ConvertTo-SafeFileStem -Value $view.run_id)-run-summary.csv"
    $exportPath = Write-ExportCsv -Rows @($row) -OutputDirectory $OutputDirectory -FileName $fileName

    return [pscustomobject][ordered]@{
        export_type = "run-summary"
        bundle_path = $view.bundle_path
        run_id = $view.run_id
        output_directory = (Resolve-Path -LiteralPath (Split-Path -Parent $exportPath)).Path
        export_path = $exportPath
        row_count = 1
    }
}

function Export-ProposalSummaryCsv {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath,

        [Parameter(Mandatory = $true)]
        [string]$OutputDirectory
    )

    $view = Get-ProposalReviewView -BundlePath $BundlePath
    $warningColumns = Get-WarningColumnMap -Warnings @($view.warnings) -Prefix "bundle"
    $base = [ordered]@{
        bundle_path = $view.bundle_path
        run_id = $view.run_id
        scenario_name = $view.scenario_name
        tick_observed = $view.tick_observed
        proposal_count_raw = $view.proposal_count_raw
        proposal_count = $view.proposal_count
        approved_count = $view.approved_count
        rejected_count = $view.rejected_count
        invalid_target_count = $view.invalid_target_count
        source_proposal_review = $view.source_paths.proposal_review
        source_endpoint_world_proposals_cold = $view.source_paths.endpoint_world_proposals_cold
        source_latest_world_tick = $view.source_paths.latest_world_tick
    }

    $notes = @($view.review_notes)
    $parsedEntries = @($view.parsed_entries)
    $latestPending = @($view.latest_pending_proposals)
    $rowCount = [Math]::Max(1, [Math]::Max($notes.Count, [Math]::Max($parsedEntries.Count, $latestPending.Count)))
    $rows = @()

    for ($i = 0; $i -lt $rowCount; $i++) {
        $note = if ($i -lt $notes.Count) { $notes[$i] } else { $null }
        $parsed = if ($i -lt $parsedEntries.Count) { $parsedEntries[$i] } else { $null }
        $pending = if ($i -lt $latestPending.Count) { $latestPending[$i] } else { $null }
        $rowKind = if ($note -or $parsed -or $pending) { "proposal-evidence" } else { "summary-only" }

        $rows += New-ExportRow -Base $base -WarningColumns $warningColumns -Extra ([ordered]@{
                row_index = $i + 1
                row_kind = $rowKind
                review_note = $note
                parsed_agent = Get-ViewProperty -Object $parsed -Name "agent"
                parsed_type = Get-ViewProperty -Object $parsed -Name "type"
                parsed_parent = Get-ViewProperty -Object $parsed -Name "parent"
                parsed_name = Get-ViewProperty -Object $parsed -Name "name"
                parsed_proposed_state = Get-ViewProperty -Object $parsed -Name "proposed_state"
                parsed_reason = Get-ViewProperty -Object $parsed -Name "reason"
                pending_id = Get-ViewProperty -Object $pending -Name "id"
                pending_agent = Get-ViewProperty -Object $pending -Name "agent"
                pending_type = Get-ViewProperty -Object $pending -Name "type"
                pending_parent_location = Get-ViewProperty -Object $pending -Name "parentLocation"
                pending_name = Get-ViewProperty -Object $pending -Name "name"
                pending_proposed_state = Get-ViewProperty -Object $pending -Name "proposedState"
                pending_reason = Get-ViewProperty -Object $pending -Name "reason"
                pending_status = Get-ViewProperty -Object $pending -Name "status"
                pending_created_at_tick = Get-ViewProperty -Object $pending -Name "createdAtTick"
            })
    }

    $fileName = "$(ConvertTo-SafeFileStem -Value $view.run_id)-proposal-summary.csv"
    $exportPath = Write-ExportCsv -Rows $rows -OutputDirectory $OutputDirectory -FileName $fileName

    return [pscustomobject][ordered]@{
        export_type = "proposal-summary"
        bundle_path = $view.bundle_path
        run_id = $view.run_id
        output_directory = (Resolve-Path -LiteralPath (Split-Path -Parent $exportPath)).Path
        export_path = $exportPath
        row_count = @($rows).Count
    }
}

function Export-ChecklistOutcomeCsv {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath,

        [Parameter(Mandatory = $true)]
        [string]$OutputDirectory,

        [string]$ChecklistPath = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..\\..")) "docs\\evals\\two-house-garden-v1-review-checklist.md")
    )

    $view = Get-ChecklistStatusView -BundlePath $BundlePath -ChecklistPath $ChecklistPath
    $bundleWarnings = Get-WarningColumnMap -Warnings @($view.warnings) -Prefix "bundle"
    $base = [ordered]@{
        bundle_path = $view.bundle_path
        run_id = $view.run_id
        scenario_name = $view.scenario_name
        checklist_source_path = $view.checklist_source_path
    }

    $rows = @()
    foreach ($category in @($view.categories)) {
        $categoryWarnings = Get-WarningColumnMap -Warnings @(Get-ViewProperty -Object $category -Name "warnings") -Prefix "category"
        $rows += New-ExportRow -Base $base -WarningColumns $bundleWarnings -Extra ([ordered]@{
                category_id = $category.id
                category_name = $category.name
                category_status = $category.status
                category_rationale = $category.rationale
                category_prompts = Get-ExportValue -Value @($category.prompts)
                category_evidence_paths = Get-ExportValue -Value @($category.evidence_paths)
                category_warning_count = $categoryWarnings["category_warning_count"]
                category_warning_codes = $categoryWarnings["category_warning_codes"]
                category_warning_owners = $categoryWarnings["category_warning_owners"]
                category_warning_messages = $categoryWarnings["category_warning_messages"]
                category_warning_paths = $categoryWarnings["category_warning_paths"]
            })
    }

    $fileName = "$(ConvertTo-SafeFileStem -Value $view.run_id)-checklist-outcome.csv"
    $exportPath = Write-ExportCsv -Rows $rows -OutputDirectory $OutputDirectory -FileName $fileName

    return [pscustomobject][ordered]@{
        export_type = "checklist-outcome"
        bundle_path = $view.bundle_path
        run_id = $view.run_id
        output_directory = (Resolve-Path -LiteralPath (Split-Path -Parent $exportPath)).Path
        export_path = $exportPath
        row_count = @($rows).Count
    }
}

function Export-RunComparisonSummaryCsv {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePathA,

        [Parameter(Mandatory = $true)]
        [string]$BundlePathB,

        [Parameter(Mandatory = $true)]
        [string]$OutputDirectory,

        [string]$ChecklistPath = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..\\..")) "docs\\evals\\two-house-garden-v1-review-checklist.md")
    )

    $view = Get-RunComparisonView -BundlePathA $BundlePathA -BundlePathB $BundlePathB -ChecklistPath $ChecklistPath
    $warningComparison = $view.comparison_sections.warning_comparison
    $warningColumns = [ordered]@{
        warning_codes_a_only = Get-ExportValue -Value @(Get-ViewProperty -Object $warningComparison -Name "warning_codes_a_only")
        warning_codes_b_only = Get-ExportValue -Value @(Get-ViewProperty -Object $warningComparison -Name "warning_codes_b_only")
        warning_codes_shared = Get-ExportValue -Value @(Get-ViewProperty -Object $warningComparison -Name "warning_codes_shared")
    }
    $base = [ordered]@{
        bundle_path_a = $view.bundle_a.bundle_path
        bundle_path_b = $view.bundle_b.bundle_path
        run_id_a = $view.bundle_a.run_id
        run_id_b = $view.bundle_b.run_id
        scenario_name_a = $view.bundle_a.scenario_name
        scenario_name_b = $view.bundle_b.scenario_name
    }

    $rows = @()
    foreach ($row in @($view.comparison_sections.bundle_identity)) {
        $rows += New-ExportRow -Base $base -WarningColumns $warningColumns -Extra ([ordered]@{
                comparison_section = "bundle_identity"
                item_id = $row.field
                item_name = $row.field
                status = $row.status
                value_a = Get-ExportValue -Value $row.value_a
                value_b = Get-ExportValue -Value $row.value_b
                notes = $row.notes
                warnings_a = ""
                warnings_b = ""
            })
    }
    foreach ($row in @($view.comparison_sections.bundle_completeness)) {
        $rows += New-ExportRow -Base $base -WarningColumns $warningColumns -Extra ([ordered]@{
                comparison_section = "bundle_completeness"
                item_id = $row.field
                item_name = $row.field
                status = $row.status
                value_a = Get-ExportValue -Value $row.value_a
                value_b = Get-ExportValue -Value $row.value_b
                notes = $row.notes
                warnings_a = ""
                warnings_b = ""
            })
    }
    foreach ($row in @($view.comparison_sections.proposal_review)) {
        $rows += New-ExportRow -Base $base -WarningColumns $warningColumns -Extra ([ordered]@{
                comparison_section = "proposal_review"
                item_id = $row.field
                item_name = $row.field
                status = $row.status
                value_a = Get-ExportValue -Value $row.value_a
                value_b = Get-ExportValue -Value $row.value_b
                notes = $row.notes
                warnings_a = ""
                warnings_b = ""
            })
    }
    foreach ($row in @($view.comparison_sections.checklist_status)) {
        $rows += New-ExportRow -Base $base -WarningColumns $warningColumns -Extra ([ordered]@{
                comparison_section = "checklist_status"
                item_id = $row.id
                item_name = $row.name
                status = $row.status
                value_a = Get-ExportValue -Value $row.status_a
                value_b = Get-ExportValue -Value $row.status_b
                notes = ""
                warnings_a = Get-ExportValue -Value @($row.warnings_a)
                warnings_b = Get-ExportValue -Value @($row.warnings_b)
            })
    }

    $rows += New-ExportRow -Base $base -WarningColumns $warningColumns -Extra ([ordered]@{
            comparison_section = "warning_comparison"
            item_id = "warning_codes"
            item_name = "warning_codes"
            status = $warningComparison.status
            value_a = $warningColumns["warning_codes_a_only"]
            value_b = $warningColumns["warning_codes_b_only"]
            notes = ""
            warnings_a = ""
            warnings_b = ""
        })

    $fileName = "$(ConvertTo-SafeFileStem -Value $view.bundle_a.run_id)__vs__$(ConvertTo-SafeFileStem -Value $view.bundle_b.run_id)-comparison-summary.csv"
    $exportPath = Write-ExportCsv -Rows $rows -OutputDirectory $OutputDirectory -FileName $fileName

    return [pscustomobject][ordered]@{
        export_type = "comparison-summary"
        bundle_path_a = $view.bundle_a.bundle_path
        bundle_path_b = $view.bundle_b.bundle_path
        run_id_a = $view.bundle_a.run_id
        run_id_b = $view.bundle_b.run_id
        output_directory = (Resolve-Path -LiteralPath (Split-Path -Parent $exportPath)).Path
        export_path = $exportPath
        row_count = @($rows).Count
    }
}

function Export-LedgerBundle {
    <#
    .SYNOPSIS
    Reads a ledger export JSON and writes decomposed bundle artifacts.
    .PARAMETER LedgerExportPath
    Path to the endpoint_ledger_export_*.json capture file.
    .PARAMETER OutputDirectory
    Directory to write ledger bundle artifacts into.
    #>
    param(
        [Parameter(Mandatory = $true)]
        [string]$LedgerExportPath,

        [Parameter(Mandatory = $true)]
        [string]$OutputDirectory
    )

    if (-not (Test-Path -LiteralPath $LedgerExportPath)) {
        throw "Ledger export file not found: $LedgerExportPath"
    }

    $resolvedOutput = Resolve-ExportOutputDirectory -OutputDirectory $OutputDirectory

    $raw = Get-Content -Path $LedgerExportPath -Raw -Encoding UTF8
    $capture = $raw | ConvertFrom-Json
    $export = if ($capture.body) { $capture.body } else { $capture }

    if (-not (Test-Path -LiteralPath $resolvedOutput)) {
        New-Item -ItemType Directory -Path $resolvedOutput -Force | Out-Null
    }

    # Full ledger export
    $export | ConvertTo-Json -Depth 100 | Set-Content -Path (Join-Path $resolvedOutput 'ledger_export.json') -Encoding UTF8

    # Governance ledger + proposal history
    $governance = [ordered]@{
        governanceLedger = @(Get-ViewProperty -Object $export -Name 'governanceLedger')
        proposalHistoryFull = @(Get-ViewProperty -Object $export -Name 'proposalHistoryFull')
    }
    $governance | ConvertTo-Json -Depth 100 | Set-Content -Path (Join-Path $resolvedOutput 'governance_ledger.json') -Encoding UTF8

    # Memory index
    $memoryIndex = Get-ViewProperty -Object $export -Name 'memoryIndex'
    if ($memoryIndex) {
        $memoryIndex | ConvertTo-Json -Depth 100 | Set-Content -Path (Join-Path $resolvedOutput 'memory_index.json') -Encoding UTF8
    }

    # Per-agent memory ledger and dream packs (from memoryIndex)
    if ($memoryIndex) {
        foreach ($prop in $memoryIndex.PSObject.Properties) {
            $agentName = $prop.Name
            $safeName = $agentName -replace '[^a-zA-Z0-9_-]', '_'
            $agentDir = Join-Path $resolvedOutput "agents\$safeName"
            if (-not (Test-Path -LiteralPath $agentDir)) {
                New-Item -ItemType Directory -Path $agentDir -Force | Out-Null
            }
            $prop.Value | ConvertTo-Json -Depth 100 | Set-Content -Path (Join-Path $agentDir 'memory-ledger.json') -Encoding UTF8
        }
    }

    return [pscustomobject]@{
        source_path = $LedgerExportPath
        output_directory = $resolvedOutput
        has_governance = ($null -ne (Get-ViewProperty -Object $export -Name 'governanceLedger'))
        has_memory_index = ($null -ne $memoryIndex)
    }
}

Export-ModuleMember -Function Export-RunBundleSummaryCsv, Export-ProposalSummaryCsv, Export-ChecklistOutcomeCsv, Export-RunComparisonSummaryCsv, Export-LedgerBundle
