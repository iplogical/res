@echo off
call %~dp0\gradlew.bat xjc
call %~dp0\gradlew.bat %*