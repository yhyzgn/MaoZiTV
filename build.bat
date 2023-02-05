@echo off

echo building ...

.\gradlew.bat :app:clean :app:assembleRelease

echo Build Successful
