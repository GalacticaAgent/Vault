@echo off
echo ========================================
echo Vault Backend Startup Script
echo ========================================
echo.

cd /d "%~dp0vault-backend"

echo [1/4] Checking Maven...
call mvn --version
if errorlevel 1 (
    echo ERROR: Maven not found in PATH
    echo Please restart your command prompt after installing Maven
    pause
    exit /b 1
)

echo.
echo [2/4] Cleaning and compiling...
call mvn clean install -DskipTests
if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo [3/4] Starting backend service...
echo Backend will start at: http://localhost:8080/api
echo API Documentation: http://localhost:8080/api/doc.html
echo.
echo Press Ctrl+C to stop the server
echo.

call mvn spring-boot:run

pause
