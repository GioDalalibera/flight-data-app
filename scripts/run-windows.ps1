<# 
  Usage:
    .\run-windows.ps1 dev
    .\run-windows.ps1 prod
#>

[CmdletBinding()]
param(
  [Parameter(Mandatory = $true, Position = 0)]
  [ValidateSet('dev','prod')]
  [string] $Profile
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version 3

# Paths
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
$mvnw     = Join-Path $repoRoot 'mvnw.cmd'
$pomPath  = Join-Path $repoRoot 'pom.xml'
if (-not (Test-Path $mvnw)) { throw "mvnw.cmd not found at $mvnw" }
if (-not (Test-Path $pomPath)) { throw "pom.xml not found at $pomPath" }

# Load .env
function Load-DotEnv {
  param([string]$Path)
  if (-not (Test-Path $Path)) { return }

  # Read line-by-line; strip BOM on the very first line and on the key if present
  $isFirst = $true
  Get-Content -Path $Path -Encoding UTF8 | ForEach-Object {
    $line = $_
    if ($null -eq $line) { return }

    # Strip a UTF-8 BOM at the start of the file
    if ($isFirst) {
      $line = $line -replace "^\uFEFF", ""
      $isFirst = $false
    }

    $line = $line.Trim()
    if ($line.Length -eq 0 -or $line.StartsWith('#')) { return }

    $eq = $line.IndexOf('=')
    if ($eq -lt 1) { return }

    $key = $line.Substring(0, $eq).Trim()
    $val = $line.Substring($eq + 1).Trim()

    # Strip quotes if the value is quoted
    if ($val.Length -ge 2 -and (
        ($val.StartsWith('"') -and $val.EndsWith('"')) -or
        ($val.StartsWith("'") -and $val.EndsWith("'")))) {
      $val = $val.Substring(1, $val.Length - 2)
    }

    # Defensively strip BOM off the key as well
    $key = $key -replace "^\uFEFF", ""

    if ($key) {
      Set-Item -Path ("Env:{0}" -f $key) -Value $val
    }
  }
}


# --- Load env then profile-specific overrides ---
Load-DotEnv (Join-Path $repoRoot '.env')
Load-DotEnv (Join-Path $repoRoot (".env-{0}" -f $Profile))

$env:SPRING_PROFILES_ACTIVE = $Profile

Push-Location $repoRoot
try {
  & $mvnw -f $pomPath spring-boot:run
  exit $LASTEXITCODE
} finally {
  Pop-Location
}