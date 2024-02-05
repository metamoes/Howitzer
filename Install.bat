@echo off

:: Check for MariaDB installation
reg query "HKLM\SOFTWARE\MariaDB" /v InstalledVersion >nul 2>&1
if %errorlevel% equ 0 goto check_csv_files

:: Unzip MariaDB installer
cd installers
7z x mariadb-11.4.0-winx64.zip

:: Install MariaDB
start /wait msiexec.exe /i mariadb-11.4.0-winx64.msi

:: Check for CSV files
cd ..\databases
if not exist "service-names-port-numbers.csv" goto error
if not exist "files_exploits.csv" goto error
if not exist "allitems.csv" goto error

:: Launch Howitzer.java
cd ..
java Howitzer
goto end

:error
msg * "Something went wrong!"
exit /b 1

:check_csv_files
cd databases
if not exist "service-names-port-numbers.csv" goto error
if not exist "files_exploits.csv" goto error
if not exist "allitems.csv" goto error
cd ..
java Howitzer
goto end

:end
echo Installation complete.
