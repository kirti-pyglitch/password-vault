#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# Locate JDK
if [ -x "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home/bin/java" ]; then
    JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
elif [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_HOME="$JAVA_HOME"
else
    JAVA_HOME="$(/usr/libexec/java_home 2>/dev/null || echo "")"
fi

JAVAC="$JAVA_HOME/bin/javac"
JAVA="$JAVA_HOME/bin/java"

if [ ! -x "$JAVAC" ] || [ ! -x "$JAVA" ]; then
    echo "Error: Java Development Kit (JDK) not found."
    exit 1
fi

echo "==> Compiling SecureVault Java application..."
mkdir -p bin
"$JAVAC" -d bin $(find src/main/java -name "*.java")

echo "==> Launching SecureVault..."
"$JAVA" -cp bin com.securevault.Main

