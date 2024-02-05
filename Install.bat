@echo off

set "download_link=https://mirrors.gigenet.com/mariadb//mariadb-11.4.0/winx64-packages/mariadb-11.4.0-winx64.msi"
set "filename=mariadb-11.4.0-winx64.msi"

:: Determine if run as administrator
if defined SessionName (
  echo You are not running as administrator.
  msg * "You are not running as administrator. Please check installation settings"
  goto :error
) else (
  echo You are running as administrator.
)


:: Check for MariaDB installation
reg query "HKLM\SOFTWARE\MariaDB" /v InstalledVersion >nul 2>&1
if %errorlevel% equ 0 goto check_csv_files

:: Download MariaDB installer
cd installers
curl -L "%download_link%" -o "\%filename%"

if exist "%home_dir%\%filename%" (
  echo Download complete! File saved to "\%filename%".
) else (
  echo Error: Download failed! Please check the download link and try again.
)

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
msg * "Something went wrong! Please check installation settings"
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
