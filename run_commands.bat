@echo off
start cmd.exe /c javac edu\mobileweb\projectone\DominoServerApp.java
start cmd.exe /c javac edu\mobileweb\projectone\dominoesClient\Client.java

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