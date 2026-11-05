#!/usr/bin/env bash
# Smart Powerbank Lending System - Startup Script (macOS / Linux)

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

echo "=========================================================="
echo " Starting Smart Powerbank Lending System Desktop Suite... "
echo "=========================================================="

if [ -f "SmartPowerbankSystem.jar" ]; then
    java -jar SmartPowerbankSystem.jar "$@"
elif [ -d "bin" ]; then
    java -cp bin com.powerbank.Main "$@"
else
    echo "Compiling system..."
    mkdir -p bin
    javac -d bin $(find src -name "*.java")
    java -cp bin com.powerbank.Main "$@"
fi
