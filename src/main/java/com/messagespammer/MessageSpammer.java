package com.messagespammer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

/**
 * Message Spammer Tool
 * 
 * EDUCATIONAL PURPOSE ONLY
 * This tool is created for educational purposes to demonstrate automation 
 * and GUI programming in Java. The author is not responsible for any misuse 
 * of this tool. Use responsibly and only with proper authorization.
 * 
 * @author el211
 */
public class MessageSpammer extends JFrame {
    
    private JTextArea messageArea;
    private JTextField countField;
    private JTextField delayField;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private volatile boolean running = false;
    private Thread spamThread;
    
    public MessageSpammer() {
        setTitle("Message Spammer - Educational Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        initComponents();
        
        setVisible(true);
    }
    
    private void initComponents() {
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Warning panel
        JPanel warningPanel = new JPanel();
        warningPanel.setLayout(new BorderLayout());
        warningPanel.setBackground(new Color(255, 243, 205));
        warningPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        
        JLabel warningLabel = new JLabel("<html><center><b>⚠ EDUCATIONAL PURPOSE ONLY ⚠</b><br>" +
                "Use responsibly and only with proper authorization.<br>" +
                "Author is not responsible for misuse.</center></html>");
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        warningPanel.add(warningLabel, BorderLayout.CENTER);
        
        mainPanel.add(warningPanel, BorderLayout.NORTH);
        
        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Message input
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(new JLabel("Message:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        messageArea = new JTextArea(3, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        inputPanel.add(scrollPane, gbc);
        
        // Count input
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Count:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        countField = new JTextField("10");
        inputPanel.add(countField, gbc);
        
        // Delay input
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Delay (ms):"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        delayField = new JTextField("100");
        inputPanel.add(delayField, gbc);
        
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        startButton = new JButton("Start Spamming");
        startButton.setBackground(new Color(76, 175, 80));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startSpamming());
        
        stopButton = new JButton("Stop");
        stopButton.setBackground(new Color(244, 67, 54));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopSpamming());
        
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusLabel = new JLabel("Ready to spam. Click 'Start Spamming' to begin.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void startSpamming() {
        String message = messageArea.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a message to spam!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int count;
        int delay;
        
        try {
            count = Integer.parseInt(countField.getText().trim());
            if (count <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid positive number for count!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            delay = Integer.parseInt(delayField.getText().trim());
            if (delay < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid non-negative number for delay!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        messageArea.setEnabled(false);
        countField.setEnabled(false);
        delayField.setEnabled(false);
        
        spamThread = new Thread(() -> {
            for (int i = 0; i < count && running; i++) {
                final int currentCount = i + 1;
                
                // Copy message to clipboard
                StringSelection stringSelection = new StringSelection(message);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                
                // Simulate paste action (Ctrl+V)
                try {
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyPress(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                    
                    // Small delay for paste to complete
                    Thread.sleep(50);
                    
                    // Press Enter to send
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                } catch (AWTException | InterruptedException e) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error: " + e.getMessage());
                    });
                    running = false;
                    break;
                }
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Sent message " + currentCount + " of " + count);
                });
                
                if (i < count - 1 && running) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        running = false;
                        break;
                    }
                }
            }
            
            SwingUtilities.invokeLater(() -> {
                if (running) {
                    statusLabel.setText("Completed! Sent all messages.");
                } else {
                    statusLabel.setText("Stopped by user.");
                }
                resetUI();
            });
        });
        
        // Give user 5 seconds to switch to the target application
        statusLabel.setText("Starting in 5 seconds... Switch to your target application!");
        Timer timer = new Timer(5000, e -> {
            if (running) {
                spamThread.start();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void stopSpamming() {
        running = false;
        if (spamThread != null && spamThread.isAlive()) {
            spamThread.interrupt();
        }
        statusLabel.setText("Stopping...");
    }
    
    private void resetUI() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        messageArea.setEnabled(true);
        countField.setEnabled(true);
        delayField.setEnabled(true);
        running = false;
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        // Show educational warning dialog
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.showConfirmDialog(null,
                "⚠ EDUCATIONAL PURPOSE ONLY ⚠\n\n" +
                "This tool is designed for educational purposes only.\n" +
                "The author is not responsible for any misuse of this tool.\n\n" +
                "By clicking 'Yes', you agree to:\n" +
                "• Use this tool responsibly\n" +
                "• Only use with proper authorization\n" +
                "• Accept full responsibility for your actions\n\n" +
                "Do you agree to these terms?",
                "Educational Tool - Disclaimer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                new MessageSpammer();
            } else {
                System.exit(0);
            }
        });
    }
}
