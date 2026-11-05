@echo off
REM Smart Powerbank Lending System - Startup Script (Windows)
cd /d "%~dp0"

echo ==========================================================
echo  Starting Smart Powerbank Lending System Desktop Suite...
echo ==========================================================

if exist SmartPowerbankSystem.jar (
    java -jar SmartPowerbankSystem.jar %*
) else (
    if exist bin (
        java -cp bin com.powerbank.Main %*
    ) else (
        echo Compiling system...
        if not exist bin mkdir bin
        javac -d bin src\com\powerbank\model\*.java src\com\powerbank\data\*.java src\com\powerbank\service\*.java src\com\powerbank\ui\*.java src\com\powerbank\*.java
        java -cp bin com.powerbank.Main %*
    )
)
pause
