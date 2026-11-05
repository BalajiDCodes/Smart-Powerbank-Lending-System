#!/usr/bin/env bash
# Automated Verification Test Runner (33 Tests)

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

echo "Compiling and running comprehensive verification suite..."
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin com.powerbank.SystemVerificationTest
