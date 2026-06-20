param(
    [Parameter(Mandatory = $true)]
    [string]$Suite,

    [Parameter(Mandatory = $true)]
    [string]$Title,

    [Parameter(Mandatory = $true)]
    [string]$Subtitle,

    [Parameter(Mandatory = $true)]
    [string]$PackageSegment,

    [Parameter(Mandatory = $true)]
    [string]$ReportDir,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath,

    [string]$LogPath,

    [Parameter(Mandatory = $true)]
    [datetime]$StartedAt,

    [Parameter(Mandatory = $true)]
    [timespan]$Duration,

    [Parameter(Mandatory = $true)]
    [int]$ExitCode
)

function Get-ModuleName {
    param([string]$ClassName)
    if ($ClassName -match "\.$PackageSegment\.([^.]+)\.") {
        return $Matches[1]
    }
    if ($ClassName -match "\.$PackageSegment\.") {
        return $PackageSegment
    }
    return 'other'
}

function Get-ModuleLabel {
    param([string]$Module)
    switch ($Module) {
        'authentication' { return 'Authentication' }
        'contentlibrary' { return 'Content Library' }
        'recommendation' { return 'Recommendation' }
        'videoplayback' { return 'Video Playback' }
        'architecture' { return 'Architecture' }
        'common' { return 'Common' }
        'rules' { return 'Rules' }
        'scenario' { return 'Scenarios' }
        'support' { return 'Support' }
        default { return (Get-Culture).TextInfo.ToTitleCase($Module) }
    }
}

function Escape-Html {
    param([string]$Text)
    if ($null -eq $Text) { return '' }
    return [System.Net.WebUtility]::HtmlEncode($Text)
}

$packagePattern = "\.$PackageSegment\."
$classes = @()
$failures = @()
$totalTests = 0
$totalFailures = 0
$totalErrors = 0
$totalSkipped = 0
$totalTime = 0.0

if (Test-Path $ReportDir) {
    Get-ChildItem -Path $ReportDir -Filter 'TEST-*.xml' | ForEach-Object {
        [xml]$xml = Get-Content $_.FullName -Encoding UTF8
        $suiteNode = $xml.testsuite
        if (-not $suiteNode) { return }

        $className = [string]$suiteNode.name
        if ($className -notmatch $packagePattern) { return }

        $tests = [int]$suiteNode.tests
        $failCount = [int]$suiteNode.failures
        $errorCount = [int]$suiteNode.errors
        $skipped = [int]$suiteNode.skipped
        $time = [double]$suiteNode.time

        $totalTests += $tests
        $totalFailures += $failCount
        $totalErrors += $errorCount
        $totalSkipped += $skipped
        $totalTime += $time

        $passed = $tests - $failCount - $errorCount - $skipped
        $status = if ($failCount -gt 0 -or $errorCount -gt 0) { 'failed' } elseif ($skipped -gt 0) { 'skipped' } else { 'passed' }

        $classes += [pscustomobject]@{
            Module    = Get-ModuleName $className
            ClassName = $className
            ShortName = ($className -split '\.')[-1]
            Tests     = $tests
            Passed    = $passed
            Failures  = $failCount
            Errors    = $errorCount
            Skipped   = $skipped
            Time      = $time
            Status    = $status
        }

        foreach ($case in $suiteNode.testcase) {
            $failure = $case.failure
            $error = $case.error
            if ($failure -or $error) {
                $detail = if ($failure) { $failure } else { $error }
                $failures += [pscustomobject]@{
                    ClassName = [string]$case.classname
                    TestName  = [string]$case.name
                    Message   = [string]$detail.message
                    Detail    = [string]$detail.'#text'
                }
            }
        }
    }
}

$success = ($ExitCode -eq 0) -and ($totalFailures -eq 0) -and ($totalErrors -eq 0)
$statusLabel = if ($success) { 'SUCCESS' } else { 'FAILED' }
$statusClass = if ($success) { 'status-success' } else { 'status-failed' }

$moduleGroups = $classes | Group-Object Module | Sort-Object Name
$moduleCards = ($moduleGroups | ForEach-Object {
    $module = $_.Name
    $label = Get-ModuleLabel $module
    $items = $_.Group | Sort-Object ClassName
    $moduleTests = ($items | Measure-Object -Property Tests -Sum).Sum
    $modulePassed = ($items | Measure-Object -Property Passed -Sum).Sum
    $moduleFailed = ($items | Measure-Object -Property Failures -Sum).Sum + ($items | Measure-Object -Property Errors -Sum).Sum
    $moduleStatus = if ($moduleFailed -gt 0) { 'failed' } else { 'passed' }

    $rows = ($items | ForEach-Object {
        $rowStatus = $_.Status
        @"
            <tr class="row-$rowStatus">
              <td class="class-name">$(Escape-Html $_.ShortName)</td>
              <td class="num">$($_.Tests)</td>
              <td class="num ok">$($_.Passed)</td>
              <td class="num">$($_.Failures)</td>
              <td class="num">$($_.Errors)</td>
              <td class="num">$($_.Skipped)</td>
              <td class="num">$([math]::Round($_.Time, 3))s</td>
              <td><span class="badge badge-$rowStatus">$rowStatus</span></td>
            </tr>
"@
    }) -join "`n"

    @"
    <section class="module-card">
      <header class="module-header module-$moduleStatus">
        <h2>$label</h2>
        <div class="module-stats">
          <span>$moduleTests tests</span>
          <span class="ok">$modulePassed passed</span>
          $(if ($moduleFailed -gt 0) { "<span class='bad'>$moduleFailed failed</span>" })
        </div>
      </header>
      <table>
        <thead>
          <tr>
            <th>Class</th>
            <th>Tests</th>
            <th>Passed</th>
            <th>Fail</th>
            <th>Error</th>
            <th>Skip</th>
            <th>Time</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
$rows
        </tbody>
      </table>
    </section>
"@
}) -join "`n"

$failureSection = if ($failures.Count -gt 0) {
    $blocks = ($failures | ForEach-Object {
        @"
    <article class="failure">
      <h3>$(Escape-Html ($_.ClassName + ' :: ' + $_.TestName))</h3>
      $(if ($_.Message) { "<p class='failure-message'>$(Escape-Html $_.Message)</p>" })
      <pre>$(Escape-Html $_.Detail)</pre>
    </article>
"@
    }) -join "`n"
    @"
  <section class="failures">
    <h2>Failures &amp; errors</h2>
$blocks
  </section>
"@
} else { '' }

$emptySection = if ($classes.Count -eq 0) {
    @"
    <section class="module-card">
      <header class="module-header">
        <h2>Brak wynikow</h2>
      </header>
      <p style="padding: 1rem 1.25rem; margin: 0; color: var(--muted);">
        Nie znaleziono testow w pakiecie <code>.$PackageSegment.</code>.
        Dodaj klasy do odpowiedniego katalogu i uruchom suite ponownie.
      </p>
    </section>
"@
} else { '' }

$logLink = if ($LogPath) { Split-Path $LogPath -Leaf } else { '' }
$durationText = $Duration.ToString('mm\:ss\.fff')
$reportEngine = if ($Suite -eq 'e2e') { 'Failsafe' } else { 'Surefire' }

$html = @"
<!DOCTYPE html>
<html lang="pl">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Youtlix - $Title - $statusLabel</title>
  <style>
    :root {
      --bg: #f4f6f9;
      --card: #ffffff;
      --text: #1a1f2e;
      --muted: #5c6578;
      --border: #e2e8f0;
      --success: #0f9d58;
      --success-bg: #e8f7ef;
      --failed: #d93025;
      --failed-bg: #fdecea;
      --skipped: #b06000;
      --accent: #3b5bdb;
    }
    * { box-sizing: border-box; }
    body {
      margin: 0;
      font-family: "Segoe UI", system-ui, sans-serif;
      background: linear-gradient(180deg, #eef2ff 0%, var(--bg) 220px);
      color: var(--text);
      line-height: 1.5;
    }
    .container { max-width: 1100px; margin: 0 auto; padding: 2rem 1.25rem 3rem; }
    .hero {
      background: var(--card);
      border: 1px solid var(--border);
      border-radius: 16px;
      padding: 1.75rem 2rem;
      box-shadow: 0 10px 30px rgba(30, 41, 59, 0.06);
      margin-bottom: 1.5rem;
    }
    .hero-top { display: flex; flex-wrap: wrap; gap: 1rem; align-items: center; justify-content: space-between; }
    h1 { margin: 0; font-size: 1.75rem; }
    .subtitle { color: var(--muted); margin: 0.35rem 0 0; }
    .suite-tag {
      display: inline-block;
      margin-top: 0.5rem;
      padding: 0.2rem 0.55rem;
      border-radius: 6px;
      background: #eef2ff;
      color: var(--accent);
      font-size: 0.78rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
    .status-pill {
      font-weight: 700;
      letter-spacing: 0.04em;
      padding: 0.55rem 1rem;
      border-radius: 999px;
      font-size: 0.85rem;
    }
    .status-success { background: var(--success-bg); color: var(--success); }
    .status-failed { background: var(--failed-bg); color: var(--failed); }
    .summary-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
      gap: 0.75rem;
      margin-top: 1.5rem;
    }
    .summary-item {
      background: #f8fafc;
      border: 1px solid var(--border);
      border-radius: 12px;
      padding: 0.9rem 1rem;
    }
    .summary-item .label { color: var(--muted); font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.05em; }
    .summary-item .value { font-size: 1.5rem; font-weight: 700; margin-top: 0.15rem; }
    .summary-item.ok .value { color: var(--success); }
    .summary-item.bad .value { color: var(--failed); }
    .meta { margin-top: 1rem; color: var(--muted); font-size: 0.92rem; }
    .meta a { color: var(--accent); }
    .module-card {
      background: var(--card);
      border: 1px solid var(--border);
      border-radius: 14px;
      overflow: hidden;
      margin-bottom: 1rem;
      box-shadow: 0 4px 16px rgba(30, 41, 59, 0.04);
    }
    .module-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
      padding: 1rem 1.25rem;
      border-bottom: 1px solid var(--border);
      background: #fafbfc;
    }
    .module-header h2 { margin: 0; font-size: 1.1rem; }
    .module-header.module-failed { background: var(--failed-bg); }
    .module-stats { display: flex; gap: 0.75rem; font-size: 0.9rem; color: var(--muted); }
    .module-stats .ok { color: var(--success); font-weight: 600; }
    .module-stats .bad { color: var(--failed); font-weight: 600; }
    table { width: 100%; border-collapse: collapse; font-size: 0.92rem; }
    th, td { padding: 0.65rem 1rem; text-align: left; border-bottom: 1px solid var(--border); }
    th { color: var(--muted); font-size: 0.78rem; text-transform: uppercase; letter-spacing: 0.04em; background: #fcfdff; }
    tr:last-child td { border-bottom: none; }
    .class-name { font-family: Consolas, "Courier New", monospace; font-size: 0.86rem; }
    .num { text-align: right; font-variant-numeric: tabular-nums; }
    .num.ok { color: var(--success); font-weight: 600; }
    .badge {
      display: inline-block;
      padding: 0.15rem 0.55rem;
      border-radius: 999px;
      font-size: 0.75rem;
      font-weight: 700;
      text-transform: uppercase;
    }
    .badge-passed { background: var(--success-bg); color: var(--success); }
    .badge-failed { background: var(--failed-bg); color: var(--failed); }
    .badge-skipped { background: #fff4e5; color: var(--skipped); }
    .row-failed { background: #fffafa; }
    .failures {
      background: var(--card);
      border: 1px solid var(--failed);
      border-radius: 14px;
      padding: 1.25rem 1.5rem;
      margin-top: 1.5rem;
    }
    .failures h2 { margin-top: 0; color: var(--failed); }
    .failure { margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--border); }
    .failure:first-of-type { border-top: none; padding-top: 0; }
    .failure h3 { margin: 0 0 0.5rem; font-size: 0.95rem; font-family: Consolas, monospace; }
    .failure-message { margin: 0 0 0.5rem; color: var(--failed); }
    pre {
      margin: 0;
      padding: 0.85rem;
      background: #0f172a;
      color: #e2e8f0;
      border-radius: 8px;
      overflow: auto;
      font-size: 0.8rem;
      line-height: 1.45;
    }
    footer { margin-top: 2rem; text-align: center; color: var(--muted); font-size: 0.85rem; }
  </style>
</head>
<body>
  <div class="container">
    <section class="hero">
      <div class="hero-top">
        <div>
          <h1>Youtlix - $Title</h1>
          <p class="subtitle">$Subtitle</p>
          <span class="suite-tag">$Suite</span>
        </div>
        <span class="status-pill $statusClass">$statusLabel</span>
      </div>
      <div class="summary-grid">
        <div class="summary-item"><div class="label">Tests</div><div class="value">$totalTests</div></div>
        <div class="summary-item ok"><div class="label">Passed</div><div class="value">$($totalTests - $totalFailures - $totalErrors - $totalSkipped)</div></div>
        <div class="summary-item $(if ($totalFailures -gt 0) { 'bad' })"><div class="label">Failures</div><div class="value">$totalFailures</div></div>
        <div class="summary-item $(if ($totalErrors -gt 0) { 'bad' })"><div class="label">Errors</div><div class="value">$totalErrors</div></div>
        <div class="summary-item"><div class="label">Skipped</div><div class="value">$totalSkipped</div></div>
        <div class="summary-item"><div class="label">Duration</div><div class="value">$durationText</div></div>
      </div>
      <div class="meta">
        <div>Start: $($StartedAt.ToString('yyyy-MM-dd HH:mm:ss'))</div>
        <div>Classes: $($classes.Count) &middot; $reportEngine time: $([math]::Round($totalTime, 2))s &middot; Exit code: $ExitCode</div>
        $(if ($logLink) { "<div>Pelny log Maven: <a href=`"$logLink`">$logLink</a></div>" })
      </div>
    </section>

$emptySection
$moduleCards
$failureSection

    <footer>Generated by test-suite $Suite &middot; Youtlix</footer>
  </div>
</body>
</html>
"@

$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($OutputPath, $html, $utf8NoBom)
