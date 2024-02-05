@echo off
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
echo it worked somehow!!!
java howitzer.java

Echo Shutting Down

Echo Cleanup time
:: Cleanup
Del Names *.class

Echo Thank you come again
timeout /t 5