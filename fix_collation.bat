@echo off
echo ========================================
echo Fix Vault Database Collation
echo ========================================
echo.
echo Opening MySQL Workbench...
echo Please execute database\fix_collation.sql manually
echo.
start "" "D:\MySQL\MySQLWorkbench.exe"
echo.
echo Instructions:
echo 1. Connect to your database
echo 2. File - Open SQL Script
echo 3. Select: database\fix_collation.sql
echo 4. Click lightning icon to execute
echo.
pause
