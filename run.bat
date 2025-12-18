@echo off
REM Compile and Run Script for Message Spammer
REM Educational Tool - Use Responsibly

echo ===================================
echo Message Spammer - Compilation
echo Educational Purpose Only
echo ===================================
echo.

REM Create output directory
if not exist "out" mkdir out

REM Compile the Java source
echo Compiling MessageSpammer.java...
javac -d out src\main\java\com\messagespammer\MessageSpammer.java

if %errorlevel% equ 0 (
    echo ✓ Compilation successful!
    echo.
    echo ===================================
    echo Starting Message Spammer...
    echo ===================================
    echo.
    
    REM Run the application
    java -cp out com.messagespammer.MessageSpammer
) else (
    echo ✗ Compilation failed!
    echo Please check for errors above.
    pause
    exit /b 1
)
