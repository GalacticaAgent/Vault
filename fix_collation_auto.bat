@echo off
echo ========================================
echo Auto Fix Vault Database Collation
echo ========================================
echo.

set MYSQL_PATH=D:\MySQL\mysql.exe
set DB_USER=root
set DB_NAME=vault

if not exist "%MYSQL_PATH%" (
    echo MySQL not found at: %MYSQL_PATH%
    echo Please update MYSQL_PATH in this script
    pause
    exit /b 1
)

set /p DB_PASS=Enter MySQL password:
echo.
echo Executing fix_collation.sql...
"%MYSQL_PATH%" -u %DB_USER% -p%DB_PASS% %DB_NAME% < database\fix_collation.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Success!
    echo ========================================
    echo All tables now use utf8mb4_unicode_ci
) else (
    echo.
    echo ========================================
    echo Failed!
    echo ========================================
)

echo.
pause
