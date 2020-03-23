@echo off
del edu\mobileweb\projectone\*.class
del edu\mobileweb\projectone\dominoesClient\*.class
del edu\mobileweb\projectone\dominoesServer\*.class
del edu\mobileweb\projectone\transferCodes\*.class

javac edu\mobileweb\projectone\DominoServerApp.java
javac edu\mobileweb\projectone\dominoesClient\Client.java

timeout /t 3 /nobreak 
start cmd.exe /c java edu.mobileweb.projectone.DominoServerApp
timeout /t 1 /nobreak
start cmd.exe /c java edu.mobileweb.projectone.dominoesClient.Client
timeout /t 1 /nobreak
start cmd.exe /c java edu.mobileweb.projectone.dominoesClient.Client
timeout /t 1 /nobreak
start cmd.exe /c java edu.mobileweb.projectone.dominoesClient.Client
timeout /t 1 /nobreak
start cmd.exe /c java edu.mobileweb.projectone.dominoesClient.Client