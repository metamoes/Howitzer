@echo off

cd /d "%~dp0"

:Determine if run as administrator:
if defined SessionName (
  echo You are not running as administrator.
  msg * "You are not running as administrator. Please check installation settings"
  goto :error
) else (
  echo You are running as administrator.
)


:Metasploit_Install_Check:

set "Meta_download_link=https://windows.metasploit.com/metasploitframework-latest.msi"
set "Meta_filename=metasploitframework-latest.msi"

:Check for Metasploit installation: 
IF EXIST "C:\metasploit-framework\bin" echo Metasploit is installed && goto Java_Install_Check

:Download Metasploit installer: 
echo Metasploit is not installed
msg * "Ensure that your antivirus is disabled for this installation else the install may fail and the software will not work properly"
cd installers
curl -L "%Meta_download_link%" -o "./%Meta_filename%"

if exist "./%Meta_filename%" (
  echo Download complete! File saved to "\%Meta_filename%".
) else (
  echo Error: Download failed! Please check the download link and try again.
)

:Install Metasploit: 
start /wait msiexec.exe /i %Meta_filename%





cd /d "%~dp0"

:Java_Install_Check:

set "Java_download_link=https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.msi"
set "Java_filename=jdk-21_windows-x64_bin.msi"


:Check if Java is installed:
java -version 2>NUL

:If Java is not installed, install it
if ERRORLEVEL 1 (

:Download Java installer: 
cd installers
curl -L "%Java_download_link%" -o "./%Java_filename%"

if exist "/%Java_filename%" (
  echo Download complete! File saved to "\%Java_filename%".
) else (
  echo Error: Download failed! Please check the download link and try again.
)

:Install Java: 
start /wait msiexec.exe /i %Java_filename%
cd ..
)

echo Java is installed.


set "Maria_download_link=https://mirrors.gigenet.com/mariadb//mariadb-11.4.0/winx64-packages/mariadb-11.4.0-winx64.msi"
set "Maria_filename=mariadb-11.4.0-winx64.msi"

cd /d "%~dp0"

:Check for MariaDB installation: 
IF EXIST "C:\Program Files\MariaDB 11.4" echo Maria is installed && goto Launch_Howitzer

:Download MariaDB installer: 
cd installers
curl -L "%Maria_download_link%" -o "./%Maria_filename%"

if exist "/%Maria_filename%" (
  echo Download complete! File saved to "\%Maria_filename%".
) else (
  echo Error: Download failed! Please check the download link and try again.
)

:Install MariaDB: 
start /wait msiexec.exe /i %Maria_filename%
cd ..

:Launch_Howitzer:
cd /d "%~dp0"

echo Compiling and launching Howitzer. Thank you for using our software
echo Loading: [           ]
javac CrossReference.java
echo Loading: [#          ]
javac DatabaseSetup.java
echo Loading: [##         ]
javac Penetrate.java
echo Loading: [###        ]
javac DatabaseSetup.java
echo Loading: [####       ]
javac Reporting.java
echo Loading: [#####      ]
javac ScanNetwork.java
echo Loading: [######     ]
javac SearchableDataViews.java
echo Loading: [#######    ]
javac SeeTraffic.java
echo Loading: [########   ]
javac SelectScope.java
echo Loading: [#########  ]
javac ViewCVE.java
echo Loading: [########## ]
javac VulnTab.java
echo Loading: [###########]
javac Howitzer.java
echo it worked somehow!!!

java Howitzer.java

Echo Shutting Down

Echo Cleanup time
:Cleanup: 
Del Names *.class

Echo Thank you come again
timeout /t 5
exit

:error:
msg * "Something went wrong! Please check installation settings"
exit /b 1

:check_csv_files:
cd databases
if not exist "service-names-port-numbers.csv" goto error
if not exist "files_exploits.csv" goto error
if not exist "allitems.csv" goto error
cd ..
java Howitzer
goto end

:end
