# Script de compilation et test sans Maven
# Ce script utilise javac et java pour compiler et exécuter le projet

$projectPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcMain = Join-Path $projectPath "src\main\java"
$srcTest = Join-Path $projectPath "src\test\java"
$target = Join-Path $projectPath "target"
$testResources = Join-Path $projectPath "src\test\resources"

Write-Host "Cleaning..." -ForegroundColor Green
Remove-Item $target -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Creating directories..." -ForegroundColor Green
New-Item -ItemType Directory -Path "$target\classes" -Force | Out-Null
New-Item -ItemType Directory -Path "$target\test-classes" -Force | Out-Null

Write-Host "Compilation des sources..." -ForegroundColor Green

# Récupérer tous les fichiers Java
$javaFiles = Get-ChildItem -Path $srcMain -Filter "*.java" -Recurse

if ($javaFiles.Count -eq 0) {
    Write-Host "Aucun fichier Java trouvé!" -ForegroundColor Red
    exit 1
}

Write-Host "Fichiers trouvés: $($javaFiles.Count)" -ForegroundColor Cyan

# Compiler les sources
$compileArgs = @($javaFiles.FullName) + "-d", "$target\classes", "-encoding", "UTF-8"

Write-Host "Exécution de javac..." -ForegroundColor Cyan
& javac @compileArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erreur de compilation!" -ForegroundColor Red
    exit 1
}

Write-Host "Compilation réussie!" -ForegroundColor Green

# Copier les ressources
Copy-Item "$srcMain\..\resources\*" "$target\classes\" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Projet compilé avec succès dans $target\classes" -ForegroundColor Green
