#!/bin/bash

# Compile and Run Script for Message Spammer
# Educational Tool - Use Responsibly

echo "==================================="
echo "Message Spammer - Compilation"
echo "Educational Purpose Only"
echo "==================================="
echo ""

# Create output directory
mkdir -p out

# Compile the Java source
echo "Compiling MessageSpammer.java..."
javac -d out src/main/java/com/messagespammer/MessageSpammer.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "==================================="
    echo "Starting Message Spammer..."
    echo "==================================="
    echo ""
    
    # Run the application
    java -cp out com.messagespammer.MessageSpammer
else
    echo "✗ Compilation failed!"
    echo "Please check for errors above."
    exit 1
fi
