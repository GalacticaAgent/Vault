@echo off
echo Initializing Vault Database...

REM Try to find mysql in common locations
set MYSQL_PATH=

REM First try mysql from PATH
where mysql >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set MYSQL_PATH=mysql
    goto :found
)

if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
    goto :found
)

if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" (
    set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe
    goto :found
)

if exist "D:\zentao\ZenTao\zbox\bin\mysql.exe" (
    set MYSQL_PATH=D:\zentao\ZenTao\zbox\bin\mysql.exe
    goto :found
)

if exist "D:\MySQL\bin\mysql.exe" (
    set MYSQL_PATH=D:\MySQL\bin\mysql.exe
    goto :found
)

if exist "D:\zentao\ZenTao\bin\mysql\bin\mysql.exe" (
    set MYSQL_PATH=D:\zentao\ZenTao\bin\mysql\bin\mysql.exe
    goto :found
)

REM If not found, ask user for path
echo.
echo ERROR: MySQL not found in expected locations!
echo.
echo Please enter the full path to mysql.exe:
echo Example: D:\zentao\ZenTao\zbox\bin\mysql.exe
set /p MYSQL_PATH="MySQL Path: "

if "%MYSQL_PATH%"=="" (
    echo ERROR: No path provided!
    pause
    exit /b 1
)

if not exist "%MYSQL_PATH%" (
    echo ERROR: File not found at: %MYSQL_PATH%
    pause
    exit /b 1
)

:found
echo Found MySQL at: %MYSQL_PATH%
echo.

REM Ask for MySQL root password
echo Please enter MySQL root password (press Enter if no password):
set /p MYSQL_PASSWORD="Password: "

echo.
echo Initializing database...
cd /d "%~dp0\database"

if "%MYSQL_PASSWORD%"=="" (
    "%MYSQL_PATH%" -u root < 00_install_all.sql
) else (
    "%MYSQL_PATH%" -u root -p%MYSQL_PASSWORD% < 00_install_all.sql
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Database initialization SUCCESS!
    echo ========================================
    echo.
    echo Test accounts created:
    echo   Student: student001 / student123
    echo   Teacher: teacher001 / teacher123
    echo   Admin:   admin / admin123
    echo.
) else (
    echo.
    echo ERROR: Database initialization failed!
    echo Error code: %ERRORLEVEL%
    echo.
)

echo Database initialization complete!
pause
