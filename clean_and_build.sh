#!/bin/bash
# Script to clean build and uninstall old app

echo "Cleaning project..."
./gradlew clean

echo "Uninstalling old app from all connected devices..."
adb uninstall com.example.fetchdata 2>/dev/null || echo "App not installed or no device connected"

echo "Building new APK..."
./gradlew assembleDebug

echo "Installing fresh app..."
./gradlew installDebug

echo "Done! The app should now show the correct name 'FetchData' and work properly."

