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
JAR="$JAVA_HOME/bin/jar"

echo "==> Cleaning old build artifacts..."
rm -rf bin dist
mkdir -p bin dist

echo "==> Compiling SecureVault sources..."
"$JAVAC" -d bin $(find src/main/java -name "*.java")

echo "==> Running automated verification tests..."
"$JAVA" -cp bin -Djava.awt.headless=true -ea com.securevault.BackendVerificationTest

echo "==> Packaging standalone executable JAR: dist/SecureVault.jar..."
echo "Main-Class: com.securevault.Main" > dist/manifest.txt
"$JAR" cfm dist/SecureVault.jar dist/manifest.txt -C bin .
rm -f dist/manifest.txt

echo "==> Build complete! You can run:"
echo "    ./run.sh  OR  java -jar dist/SecureVault.jar"

