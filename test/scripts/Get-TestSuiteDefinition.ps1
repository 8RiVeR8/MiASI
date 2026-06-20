param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('unit', 'integration', 'e2e', 'architecture')]
    [string]$Suite
)

$definitions = @{
    unit = @{
        Title       = 'unit tests'
        Subtitle    = 'Raport z uruchomienia suite jednostkowej'
        MavenArgs   = @('test', '-Punit')
        ReportDir   = 'target\surefire-reports'
        PackageSegment = 'unit'
    }
    integration = @{
        Title       = 'integration tests'
        Subtitle    = 'Raport z uruchomienia testow integracyjnych'
        MavenArgs   = @('test', '-Pintegration')
        ReportDir   = 'target\surefire-reports'
        PackageSegment = 'integration'
    }
    architecture = @{
        Title       = 'architecture tests'
        Subtitle    = 'Raport z uruchomienia testow architektury (ArchUnit)'
        MavenArgs   = @('test', '-Parchitecture')
        ReportDir   = 'target\surefire-reports'
        PackageSegment = 'architecture'
    }
    e2e = @{
        Title       = 'e2e tests'
        Subtitle    = 'Raport z uruchomienia testow end-to-end'
        MavenArgs   = @('verify', '-Pe2e-tests')
        ReportDir   = 'target\failsafe-reports'
        PackageSegment = 'e2e'
    }
}

return $definitions[$Suite]
