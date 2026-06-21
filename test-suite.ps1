#!/usr/bin/env pwsh
param(
    [Parameter(Position = 0)]
    [ValidateSet('unit', 'integration', 'e2e', 'architecture', 'all')]
    [string]$Suite = 'unit',

    [switch]$Open,

    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArgs
)

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$logsDir = Join-Path $PSScriptRoot "test\logs"
New-Item -ItemType Directory -Force -Path $logsDir | Out-Null

function Write-LogLine {
    param(
        [string]$Text,
        [string]$LogPath,
        [switch]$Append
    )

    if ($Append) {
        $Text | Tee-Object -FilePath $LogPath -Append | Out-Host
    } else {
        $Text | Tee-Object -FilePath $LogPath | Out-Host
    }
}

function Invoke-SingleTestSuite {
    param(
        [Parameter(Mandatory = $true)]
        [ValidateSet('unit', 'integration', 'e2e', 'architecture')]
        [string]$SuiteName,

        [string[]]$ExtraMavenArgs = @(),
        [string]$Timestamp,
        [string]$AppendLogPath
    )

    $definition = & "$PSScriptRoot\test\scripts\Get-TestSuiteDefinition.ps1" -Suite $SuiteName
    if (-not $Timestamp) {
        $Timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
    }

    $logFile = Join-Path $logsDir "$SuiteName-$Timestamp.log"
    $htmlFile = Join-Path $logsDir "$SuiteName-$Timestamp.html"
    $latestLog = Join-Path $logsDir "latest-$SuiteName.log"
    $latestHtml = Join-Path $logsDir "latest-$SuiteName.html"

    $mavenCommand = @($definition.MavenArgs + $ExtraMavenArgs) -join ' '
    $header = @"
================================================================================
 Youtlix - $($definition.Title)
 Started: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
 Command: mvnw.cmd $mavenCommand
================================================================================

"@

    if ($AppendLogPath) {
        Write-LogLine -Text $header -LogPath $logFile
        $header | Out-File -FilePath $AppendLogPath -Append -Encoding utf8
        $header | Out-Host
    } else {
        Write-LogLine -Text $header -LogPath $logFile
    }

    $start = Get-Date
    $prevErrorAction = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'

    if ($AppendLogPath) {
        & "$PSScriptRoot\mvnw.cmd" @($definition.MavenArgs + $ExtraMavenArgs) 2>&1 | ForEach-Object {
            $_ | Tee-Object -FilePath $logFile -Append | Out-Host
            $_ | Out-File -FilePath $AppendLogPath -Append -Encoding utf8
        }
    } else {
        & "$PSScriptRoot\mvnw.cmd" @($definition.MavenArgs + $ExtraMavenArgs) 2>&1 | Tee-Object -FilePath $logFile -Append
    }

    $exitCode = $LASTEXITCODE
    $ErrorActionPreference = $prevErrorAction
    $duration = (Get-Date) - $start

    $footer = @"

================================================================================
 Finished: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
 Duration: $($duration.ToString('hh\:mm\:ss\.fff'))
 Exit code: $exitCode
================================================================================
"@

    if ($AppendLogPath) {
        Write-LogLine -Text $footer -LogPath $logFile -Append
        $footer | Out-File -FilePath $AppendLogPath -Append -Encoding utf8
        $footer | Out-Host
    } else {
        Write-LogLine -Text $footer -LogPath $logFile -Append
    }

    $reportDir = Join-Path $PSScriptRoot $definition.ReportDir
    & "$PSScriptRoot\test\scripts\Write-TestReport.ps1" `
        -Suite $SuiteName `
        -Title $definition.Title `
        -Subtitle $definition.Subtitle `
        -PackageSegment $definition.PackageSegment `
        -ReportDir $reportDir `
        -OutputPath $htmlFile `
        -LogPath $logFile `
        -StartedAt $start `
        -Duration $duration `
        -ExitCode $exitCode

    Copy-Item $logFile $latestLog -Force
    Copy-Item $htmlFile $latestHtml -Force

    if ($SuiteName -eq 'unit') {
        Copy-Item $logFile (Join-Path $logsDir "latest.log") -Force
        Copy-Item $htmlFile (Join-Path $logsDir "latest.html") -Force
    }

    Write-Host ""
    if ($exitCode -eq 0) {
        Write-Host "Result [$SuiteName]: SUCCESS ($($duration.ToString('mm\:ss')))" -ForegroundColor Green
    } else {
        Write-Host "Result [$SuiteName]: FAILED (exit $exitCode)" -ForegroundColor Red
    }
    Write-Host "Log:    $logFile" -ForegroundColor Cyan
    Write-Host "Report: $htmlFile" -ForegroundColor Cyan

    return [pscustomobject]@{
        Suite    = $SuiteName
        Title    = $definition.Title
        ExitCode = $exitCode
        Duration = $duration
        LogFile  = $logFile
        HtmlFile = $htmlFile
    }
}

function Write-AllTestReport {
    param(
        [object[]]$Results,
        [string]$OutputPath,
        [string]$LogPath,
        [datetime]$StartedAt,
        [timespan]$Duration,
        [int]$ExitCode
    )

    $rows = $Results | ForEach-Object {
        $status = if ($_.ExitCode -eq 0) { 'SUCCESS' } else { "FAILED ($($_.ExitCode))" }
        $statusClass = if ($_.ExitCode -eq 0) { 'ok' } else { 'fail' }
        $latestHtml = "latest-$($_.Suite).html"
        @"
        <tr>
          <td>$($_.Suite)</td>
          <td class="$statusClass">$status</td>
          <td>$($_.Duration.ToString('mm\:ss'))</td>
          <td><a href="$latestHtml">raport</a></td>
        </tr>
"@
    }

    $finishedAt = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $startedAtText = $StartedAt.ToString("yyyy-MM-dd HH:mm:ss")
    $durationText = $Duration.ToString('hh\:mm\:ss')
    $overallClass = if ($ExitCode -eq 0) { 'ok' } else { 'fail' }
    $overallStatus = if ($ExitCode -eq 0) { 'SUCCESS' } else { "FAILED (exit $ExitCode)" }
    $logName = [System.IO.Path]::GetFileName($LogPath)

    $html = @"
<!DOCTYPE html>
<html lang="pl">
<head>
  <meta charset="utf-8">
  <title>Youtlix — all test suites</title>
  <style>
    body { font-family: system-ui, sans-serif; margin: 2rem; color: #1a1a1a; }
    h1 { margin-bottom: 0.25rem; }
    .meta { color: #555; margin-bottom: 1.5rem; }
    .summary { font-size: 1.1rem; margin-bottom: 1.5rem; }
    .summary .ok { color: #0a7a2f; font-weight: 600; }
    .summary .fail { color: #b00020; font-weight: 600; }
    table { border-collapse: collapse; width: 100%; max-width: 720px; }
    th, td { border: 1px solid #ddd; padding: 0.5rem 0.75rem; text-align: left; }
    th { background: #f5f5f5; }
    td.ok { color: #0a7a2f; font-weight: 600; }
    td.fail { color: #b00020; font-weight: 600; }
    footer { margin-top: 2rem; color: #777; font-size: 0.9rem; }
  </style>
</head>
<body>
  <h1>all test suites</h1>
  <p class="meta">Started: $startedAtText &middot; Finished: $finishedAt &middot; Duration: $durationText</p>
  <p class="summary">Overall: <span class="$overallClass">$overallStatus</span></p>
  <table>
    <thead>
      <tr><th>Suite</th><th>Status</th><th>Duration</th><th>Report</th></tr>
    </thead>
    <tbody>
$($rows -join "`n")
    </tbody>
  </table>
  <footer>Combined log: $logName &middot; Generated by test-suite all &middot; Youtlix</footer>
</body>
</html>
"@

    $html | Out-File -FilePath $OutputPath -Encoding utf8
}

if ($Suite -eq 'all') {
    $timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
    $combinedLog = Join-Path $logsDir "all-$timestamp.log"
    $combinedHtml = Join-Path $logsDir "all-$timestamp.html"
    $latestCombinedLog = Join-Path $logsDir "latest-all.log"
    $latestCombinedHtml = Join-Path $logsDir "latest-all.html"
    $suiteOrder = @('unit', 'architecture', 'integration', 'e2e')
    $overallStart = Get-Date

    $allHeader = @"
================================================================================
 Youtlix - all test suites
 Started: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
 Suites: $($suiteOrder -join ', ')
================================================================================

"@

    Write-LogLine -Text $allHeader -LogPath $combinedLog

    $results = @()
    foreach ($suiteName in $suiteOrder) {
        $section = @"

################################################################################
# Suite: $suiteName
################################################################################

"@
        Write-LogLine -Text $section -LogPath $combinedLog -Append
        $results += Invoke-SingleTestSuite -SuiteName $suiteName -ExtraMavenArgs $MavenArgs -Timestamp $timestamp -AppendLogPath $combinedLog
    }

    $overallDuration = (Get-Date) - $overallStart
    $exitCode = 0
    foreach ($result in $results) {
        if ($result.ExitCode -ne 0) {
            $exitCode = $result.ExitCode
            break
        }
    }

    $allFooter = @"

================================================================================
 All suites finished: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
 Total duration: $($overallDuration.ToString('hh\:mm\:ss\.fff'))
 Exit code: $exitCode
================================================================================
"@

    Write-LogLine -Text $allFooter -LogPath $combinedLog -Append

    Write-AllTestReport `
        -Results $results `
        -OutputPath $combinedHtml `
        -LogPath $combinedLog `
        -StartedAt $overallStart `
        -Duration $overallDuration `
        -ExitCode $exitCode

    Copy-Item $combinedLog $latestCombinedLog -Force
    Copy-Item $combinedHtml $latestCombinedHtml -Force

    Write-Host ""
    if ($exitCode -eq 0) {
        Write-Host "Result [all]: SUCCESS ($($overallDuration.ToString('mm\:ss')))" -ForegroundColor Green
    } else {
        Write-Host "Result [all]: FAILED (exit $exitCode)" -ForegroundColor Red
    }
    Write-Host "Log:    $combinedLog" -ForegroundColor Cyan
    Write-Host "Report: $combinedHtml" -ForegroundColor Cyan
    Write-Host "Latest: test\logs\latest-all.html" -ForegroundColor DarkGray

    if ($Open) {
        Start-Process $latestCombinedHtml
    }

    exit $exitCode
}

$result = Invoke-SingleTestSuite -SuiteName $Suite -ExtraMavenArgs $MavenArgs
Write-Host "Latest: test\logs\latest-$Suite.html" -ForegroundColor DarkGray

if ($Open) {
    Start-Process (Join-Path $logsDir "latest-$Suite.html")
}

exit $result.ExitCode
