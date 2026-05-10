@echo off
REM Maven Wrapper - Télécharge et utilise Maven automatiquement

setlocal enabledelayedexpansion

REM Variables
set MAVEN_HOME=%~dp0.mvn\maven
set MAVEN_VERSION=3.9.6
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_ZIP=%~dp0.mvn\apache-maven-%MAVEN_VERSION%-bin.zip

REM Créer le répertoire .mvn s'il n'existe pas
if not exist "%~dp0.mvn" mkdir "%~dp0.mvn"

REM Télécharger Maven s'il n'existe pas
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Téléchargement de Maven %MAVEN_VERSION%...
    
    REM Utiliser PowerShell pour télécharger
    powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor [System.Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('%MAVEN_URL%', '%MAVEN_ZIP%')"
    
    if %ERRORLEVEL% NEQ 0 (
        echo Erreur : Impossible de télécharger Maven
        exit /b 1
    )
    
    echo Extraction de Maven...
    REM Extraire le ZIP
    powershell -Command "Add-Type -AssemblyName System.IO.Compression.FileSystem; [System.IO.Compression.ZipFile]::ExtractToDirectory('%MAVEN_ZIP%', '%~dp0.mvn\')"
    
    REM Renommer le répertoire
    ren "%~dp0.mvn\apache-maven-%MAVEN_VERSION%" "maven"
    
    REM Supprimer le ZIP
    del "%MAVEN_ZIP%"
    
    echo Maven %MAVEN_VERSION% installé
)

REM Exécuter Maven
"%MAVEN_HOME%\bin\mvn.cmd" %*
