# Message-Spammer

A Java-based message spamming tool with GUI interface for educational purposes.

## ⚠️ DISCLAIMER

**THIS TOOL IS FOR EDUCATIONAL PURPOSES ONLY**

This message spammer tool is coded in Java to spam any app with messages. It's made for **purely educational purposes**. Any other purpose this tool is used for, I am **not responsible for**.

By using this tool, you acknowledge that:
- You will use it responsibly and ethically
- You have proper authorization for any testing you perform
- The author bears no responsibility for misuse or damages
- You accept full responsibility for your actions

## 📋 Features

- **GUI Interface**: Easy-to-use graphical interface built with Java Swing
- **Configurable Messages**: Enter any custom message to send
- **Adjustable Count**: Set how many times to send the message
- **Delay Control**: Configure delay between messages (in milliseconds)
- **Visual Feedback**: Real-time status updates and progress tracking
- **Safety Timer**: 5-second countdown before spamming starts (time to switch to target app)
- **Stop Control**: Emergency stop button to halt spamming

## 🚀 How It Works

1. The application copies your message to the clipboard
2. Simulates `Ctrl+V` (paste) keyboard action
3. Simulates `Enter` key press to send the message
4. Waits for the specified delay
5. Repeats for the configured count

## 📦 Requirements

- Java Development Kit (JDK) 8 or higher
- Operating System: Windows, macOS, or Linux

## 🔧 Compilation

To compile the application, navigate to the project root directory and run:

```bash
# Compile the Java file
javac -d out src/main/java/com/messagespammer/MessageSpammer.java

# Or compile from src directory
cd src/main/java
javac com/messagespammer/MessageSpammer.java
```

## ▶️ Running the Application

After compilation, run the application:

```bash
# From project root (if compiled to 'out' directory)
java -cp out com.messagespammer.MessageSpammer

# Or from src/main/java directory
java com.messagespammer.MessageSpammer
```

## 📖 Usage Instructions

1. **Launch the application**: Run the compiled Java class
2. **Accept the disclaimer**: Read and accept the educational purpose disclaimer
3. **Enter your message**: Type the message you want to spam in the text area
4. **Set the count**: Enter how many times you want to send the message
5. **Set the delay**: Enter delay in milliseconds between each message (100ms recommended)
6. **Click "Start Spamming"**: You have 5 seconds to switch to your target application
7. **Switch to target app**: Click on the input field of the application you want to test
8. **The tool will automatically**: Paste and send messages with the configured delay
9. **Stop if needed**: Click the "Stop" button to halt the process

## ⚙️ Configuration Options

- **Message**: Any text you want to send (supports multi-line)
- **Count**: Number of times to send the message (positive integer)
- **Delay**: Milliseconds to wait between messages (minimum 0, recommended 100+)

## 🛡️ Ethical Use Guidelines

This tool should **ONLY** be used for:
- Educational purposes and learning automation
- Testing your own applications
- Demonstrating spam prevention mechanisms
- Research in controlled environments

**DO NOT use this tool for:**
- Harassing or annoying others
- Spamming public platforms or services
- Any malicious or harmful activities
- Violating terms of service of any platform

## ⚖️ Legal Notice

The creator of this tool provides it "as-is" without any warranties. The author is not liable for any damages, legal issues, or consequences arising from the use or misuse of this tool. Users are solely responsible for ensuring their use complies with all applicable laws and regulations.

## 🤝 Contributing

This is an educational project. If you want to improve it for educational purposes, feel free to fork and submit pull requests.

## 📄 License

This project is provided for educational purposes. Use at your own risk.

## 👤 Author

- **el211**

---

**Remember: With great power comes great responsibility. Use this tool wisely and ethically!**