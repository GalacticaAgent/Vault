@echo off
echo ========================================
echo Vault Database Connection Check
echo ========================================
echo.

echo [1/3] Checking MySQL service...
sc query mysqlzt | findstr STATE
echo.

echo [2/3] Testing database connection...
mysql -u root -e "SELECT 'Database connection successful!' AS Status;" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Database connection: OK
) else (
    echo Database connection: FAILED
    echo Please check MySQL is running and credentials are correct
)
echo.

echo [3/3] Checking vault database and users table...
mysql -u root -e "USE vault; SELECT COUNT(*) AS user_count FROM users; SELECT username, role FROM users LIMIT 5;" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Vault database and users table: OK
) else (
    echo Vault database or users table: NOT FOUND
    echo Please run database initialization: database\00_install_all.sql
)
echo.

echo ========================================
echo Check Complete
echo ========================================
pause
