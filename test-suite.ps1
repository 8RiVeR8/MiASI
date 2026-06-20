#!/usr/bin/env pwsh
param(
    [Parameter(Position = 0)]
    [ValidateSet('unit', 'integration', 'e2e', 'architecture')]
    [string]$Suite = 'unit',

    [switch]$Open,

    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArgs
)

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$definition = & "$PSScriptRoot\test\scripts\Get-TestSuiteDefinition.ps1" -Suite $Suite
$logsDir = Join-Path $PSScriptRoot "test\logs"
New-Item -ItemType Directory -Force -Path $logsDir | Out-Null

$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$logFile = Join-Path $logsDir "$Suite-$timestamp.log"
$htmlFile = Join-Path $logsDir "$Suite-$timestamp.html"
$latestLog = Join-Path $logsDir "latest-$Suite.log"
$latestHtml = Join-Path $logsDir "latest-$Suite.html"

$mavenCommand = @($definition.MavenArgs + $MavenArgs) -join ' '
$header = @"
================================================================================
 Youtlix - $($definition.Title)
 Started: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
 Command: mvnw.cmd $mavenCommand
================================================================================

"@

$header | Tee-Object -FilePath $logFile | Out-Host

$start = Get-Date
$prevErrorAction = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
& "$PSScriptRoot\mvnw.cmd" @($definition.MavenArgs + $MavenArgs) 2>&1 | Tee-Object -FilePath $logFile -Append
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

$footer | Tee-Object -FilePath $logFile -Append | Out-Host

$reportDir = Join-Path $PSScriptRoot $definition.ReportDir
& "$PSScriptRoot\test\scripts\Write-TestReport.ps1" `
    -Suite $Suite `
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

if ($Suite -eq 'unit') {
    Copy-Item $logFile (Join-Path $logsDir "latest.log") -Force
    Copy-Item $htmlFile (Join-Path $logsDir "latest.html") -Force
}

Write-Host ""
if ($exitCode -eq 0) {
    Write-Host "Result: SUCCESS ($($duration.ToString('mm\:ss')))" -ForegroundColor Green
} else {
    Write-Host "Result: FAILED (exit $exitCode)" -ForegroundColor Red
}
Write-Host "Log:    $logFile" -ForegroundColor Cyan
Write-Host "Report: $htmlFile" -ForegroundColor Cyan
Write-Host "Latest: test\logs\latest-$Suite.html" -ForegroundColor DarkGray

if ($Open) {
    Start-Process $latestHtml
}

exit $exitCode
