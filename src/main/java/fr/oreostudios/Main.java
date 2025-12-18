package fr.oreostudios;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

public class Main {

    private static ScheduledExecutorService scheduler;
    private static ScheduledFuture<?> scheduledTask;
    private static List<String> discordTokens = new ArrayList<>();
    private static Map<String, Integer> tokenMessageCount = new HashMap<>();
    private static Map<String, Long> tokenLastReset = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Message Auto-Sender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 480);

        JPanel panel = new JPanel(null);
        frame.setContentPane(panel);

        // Mode selection
        JLabel modeLabel = new JLabel("Send Mode:");
        modeLabel.setBounds(10, 20, 120, 25);
        panel.add(modeLabel);

        ButtonGroup modeGroup = new ButtonGroup();
        JRadioButton keyboardMode = new JRadioButton("Keyboard (Paste)", true);
        keyboardMode.setBounds(140, 20, 150, 25);
        modeGroup.add(keyboardMode);
        panel.add(keyboardMode);

        JRadioButton discordMode = new JRadioButton("Discord API");
        discordMode.setBounds(300, 20, 120, 25);
        modeGroup.add(discordMode);
        panel.add(discordMode);

        // Message input
        JLabel messageLabel = new JLabel("Enter Message:");
        messageLabel.setBounds(10, 55, 120, 25);
        panel.add(messageLabel);

        JTextField messageText = new JTextField();
        messageText.setBounds(140, 55, 440, 25);
        panel.add(messageText);

        // Discord Channel ID
        JLabel channelLabel = new JLabel("Discord Channel ID:");
        channelLabel.setBounds(10, 90, 120, 25);
        channelLabel.setVisible(false);
        panel.add(channelLabel);

        JTextField channelIdText = new JTextField();
        channelIdText.setBounds(140, 90, 440, 25);
        channelIdText.setVisible(false);
        panel.add(channelIdText);

        // Discord Tokens
        JLabel tokensLabel = new JLabel("Discord Tokens:");
        tokensLabel.setBounds(10, 125, 120, 25);
        tokensLabel.setVisible(false);
        panel.add(tokensLabel);

        JTextArea tokensArea = new JTextArea();
        tokensArea.setLineWrap(true);
        tokensArea.setWrapStyleWord(false);
        JScrollPane tokensScroll = new JScrollPane(tokensArea);
        tokensScroll.setBounds(140, 125, 440, 80);
        tokensScroll.setVisible(false);
        panel.add(tokensScroll);

        JLabel tokensNote = new JLabel("(One token per line - more tokens = better rate limit bypass)");
        tokensNote.setBounds(140, 208, 400, 20);
        tokensNote.setFont(new Font(tokensNote.getFont().getName(), Font.ITALIC, 10));
        tokensNote.setVisible(false);
        panel.add(tokensNote);

        // Discord Security Checkbox
        JCheckBox discordSecurityCheck = new JCheckBox("Discord Security (smart rate limit bypass)");
        discordSecurityCheck.setBounds(140, 230, 300, 20);
        discordSecurityCheck.setFont(new Font(discordSecurityCheck.getFont().getName(), Font.PLAIN, 11));
        discordSecurityCheck.setVisible(false);
        discordSecurityCheck.setSelected(true);
        panel.add(discordSecurityCheck);

        // Note label
        JLabel noteLabel = new JLabel("(Enter key will be pressed automatically)");
        noteLabel.setBounds(140, 90, 300, 25);
        noteLabel.setFont(new Font(noteLabel.getFont().getName(), Font.ITALIC, 11));
        panel.add(noteLabel);

        // Interval controls
        JLabel intervalLabel = new JLabel("Interval (seconds):");
        intervalLabel.setBounds(10, 260, 120, 25);
        panel.add(intervalLabel);

        JLabel intervalValueLabel = new JLabel("1.00");
        intervalValueLabel.setBounds(140, 260, 60, 25);
        intervalValueLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        intervalValueLabel.setOpaque(true);
        intervalValueLabel.setBackground(Color.WHITE);
        intervalValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(intervalValueLabel);

        JButton decreaseBtn = new JButton("-");
        decreaseBtn.setBounds(200, 260, 45, 25);
        panel.add(decreaseBtn);

        JButton increaseBtn = new JButton("+");
        increaseBtn.setBounds(245, 260, 45, 25);
        panel.add(increaseBtn);

        final double[] intervalValue = {1.0};

        JButton typeButton = new JButton("Send Once (3s delay)");
        typeButton.setBounds(140, 295, 240, 30);
        panel.add(typeButton);

        JButton repeatButton = new JButton("Start Repeat");
        repeatButton.setBounds(140, 330, 240, 30);
        panel.add(repeatButton);

        JLabel countLabel = new JLabel("Messages sent: 0");
        countLabel.setBounds(10, 370, 200, 25);
        panel.add(countLabel);

        JLabel status = new JLabel("Select mode and configure settings.");
        status.setBounds(10, 400, 580, 25);
        panel.add(status);

        final int[] messageCount = {0};

        // Mode change listener
        ActionListener modeListener = e -> {
            boolean isDiscord = discordMode.isSelected();
            channelLabel.setVisible(isDiscord);
            channelIdText.setVisible(isDiscord);
            tokensLabel.setVisible(isDiscord);
            tokensScroll.setVisible(isDiscord);
            tokensNote.setVisible(isDiscord);
            discordSecurityCheck.setVisible(isDiscord);
            noteLabel.setVisible(!isDiscord);

            // Adjust interval label position
            if (isDiscord) {
                intervalLabel.setBounds(10, 260, 120, 25);
                intervalValueLabel.setBounds(140, 260, 60, 25);
                decreaseBtn.setBounds(200, 260, 45, 25);
                increaseBtn.setBounds(245, 260, 45, 25);
                typeButton.setBounds(140, 295, 240, 30);
                repeatButton.setBounds(140, 330, 240, 30);
                countLabel.setBounds(10, 370, 200, 25);
                status.setBounds(10, 400, 580, 25);
                typeButton.setText("Send Once");
                status.setText("Enter Channel ID and tokens, then click Send.");
            } else {
                intervalLabel.setBounds(10, 85, 120, 25);
                intervalValueLabel.setBounds(140, 85, 60, 25);
                decreaseBtn.setBounds(200, 85, 45, 25);
                increaseBtn.setBounds(245, 85, 45, 25);
                typeButton.setBounds(140, 120, 240, 30);
                repeatButton.setBounds(140, 155, 240, 30);
                countLabel.setBounds(10, 195, 200, 25);
                status.setBounds(10, 225, 500, 25);
                typeButton.setText("Send Once (3s delay)");
                status.setText("Tip: Click button, then focus target app & place cursor.");
            }
            frame.setSize(isDiscord ? 600 : 520, isDiscord ? 480 : 310);
        };
        keyboardMode.addActionListener(modeListener);
        discordMode.addActionListener(modeListener);

        decreaseBtn.addActionListener(e -> {
            if (intervalValue[0] > 0.01) {
                intervalValue[0] = Math.round((intervalValue[0] - 0.1) * 100.0) / 100.0;
                if (intervalValue[0] < 0.01) intervalValue[0] = 0.01;
                intervalValueLabel.setText(String.format("%.2f", intervalValue[0]));
            }
        });

        increaseBtn.addActionListener(e -> {
            if (intervalValue[0] < 3600) {
                intervalValue[0] = Math.round((intervalValue[0] + 0.1) * 100.0) / 100.0;
                intervalValueLabel.setText(String.format("%.2f", intervalValue[0]));
            }
        });

        intervalValueLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                String input = JOptionPane.showInputDialog(frame,
                        "Enter interval in seconds (0.01 to 3600):\n(Use . or , for decimals)",
                        String.format("%.2f", intervalValue[0]));
                if (input != null) {
                    try {
                        double newValue = Double.parseDouble(input.replace(',', '.'));
                        if (newValue >= 0.01 && newValue <= 3600) {
                            intervalValue[0] = newValue;
                            intervalValueLabel.setText(String.format("%.2f", newValue));
                        } else {
                            JOptionPane.showMessageDialog(frame, "Value must be between 0.01 and 3600");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid number format");
                    }
                }
            }
        });

        typeButton.addActionListener((ActionEvent e) -> {
            String msg = messageText.getText();
            if (msg == null || msg.isBlank()) {
                status.setText("Please enter a message first.");
                return;
            }

            if (discordMode.isSelected()) {
                String channelId = channelIdText.getText().trim();
                String[] tokens = tokensArea.getText().trim().split("\\n");

                if (channelId.isEmpty()) {
                    status.setText("Please enter a Discord Channel ID.");
                    return;
                }
                if (tokens.length == 0 || tokens[0].isEmpty()) {
                    status.setText("Please enter at least one Discord token.");
                    return;
                }

                discordTokens.clear();
                tokenMessageCount.clear();
                tokenLastReset.clear();
                for (String token : tokens) {
                    if (!token.trim().isEmpty()) {
                        String cleanToken = token.trim();
                        discordTokens.add(cleanToken);
                        tokenMessageCount.put(cleanToken, 0);
                        tokenLastReset.put(cleanToken, System.currentTimeMillis());
                    }
                }

                typeButton.setEnabled(false);
                status.setText("Sending message to Discord...");

                new Thread(() -> {
                    sendDiscordMessage(msg, channelId, discordTokens.get(0),
                            discordSecurityCheck.isSelected(), status, typeButton, countLabel, messageCount);
                }).start();
            } else {
                status.setText("Switch to target app... (3 seconds)");
                typeButton.setEnabled(false);
                frame.setState(Frame.ICONIFIED);
                sendMessage(msg, frame, status, typeButton, countLabel, messageCount);
            }
        });

        repeatButton.addActionListener((ActionEvent e) -> {
            String msg = messageText.getText();
            if (msg == null || msg.isBlank()) {
                status.setText("Please enter a message first.");
                return;
            }

            if (discordMode.isSelected()) {
                String channelId = channelIdText.getText().trim();
                String[] tokens = tokensArea.getText().trim().split("\\n");

                if (channelId.isEmpty()) {
                    status.setText("Please enter a Discord Channel ID.");
                    return;
                }
                if (tokens.length == 0 || tokens[0].isEmpty()) {
                    status.setText("Please enter at least one Discord token.");
                    return;
                }

                discordTokens.clear();
                tokenMessageCount.clear();
                tokenLastReset.clear();
                for (String token : tokens) {
                    if (!token.trim().isEmpty()) {
                        String cleanToken = token.trim();
                        discordTokens.add(cleanToken);
                        tokenMessageCount.put(cleanToken, 0);
                        tokenLastReset.put(cleanToken, System.currentTimeMillis());
                    }
                }
            }

            double intervalSeconds = intervalValue[0];
            long intervalMillis = (long) (intervalSeconds * 1000);

            if (scheduledTask != null && !scheduledTask.isCancelled()) {
                // Stop repeating
                scheduledTask.cancel(false);
                if (scheduler != null) {
                    scheduler.shutdown();
                }
                repeatButton.setText("Start Repeat");
                typeButton.setEnabled(true);
                decreaseBtn.setEnabled(true);
                increaseBtn.setEnabled(true);
                intervalValueLabel.setEnabled(true);
                keyboardMode.setEnabled(true);
                discordMode.setEnabled(true);
                status.setText("Repeat stopped.");
            } else {
                // Start repeating
                messageCount[0] = 0;
                countLabel.setText("Messages sent: 0");
                scheduler = Executors.newScheduledThreadPool(1);

                typeButton.setEnabled(false);
                decreaseBtn.setEnabled(false);
                increaseBtn.setEnabled(false);
                intervalValueLabel.setEnabled(false);
                keyboardMode.setEnabled(false);
                discordMode.setEnabled(false);
                repeatButton.setText("Stop Repeat");

                if (discordMode.isSelected()) {
                    String channelId = channelIdText.getText().trim();
                    boolean useSecurity = discordSecurityCheck.isSelected();
                    status.setText("Repeating Discord messages with " +
                            (useSecurity ? "security enabled" : "no rate limit protection") + "...");

                    final int[] tokenIndex = {0};
                    scheduledTask = scheduler.scheduleAtFixedRate(() -> {
                        String token = getNextAvailableToken(useSecurity);
                        if (token != null) {
                            sendDiscordMessage(msg, channelId, token, useSecurity, status, null, countLabel, messageCount);
                        } else {
                            SwingUtilities.invokeLater(() ->
                                    status.setText("All tokens rate-limited, waiting..."));
                        }
                    }, 0, intervalMillis, TimeUnit.MILLISECONDS);
                } else {
                    status.setText("Switch to target app... (3 seconds until first message)");
                    frame.setState(Frame.ICONIFIED);

                    scheduledTask = scheduler.scheduleAtFixedRate(() -> {
                        sendMessage(msg, frame, status, null, countLabel, messageCount);
                    }, 3000, intervalMillis, TimeUnit.MILLISECONDS);
                }
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (scheduler != null) {
                    scheduler.shutdownNow();
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String getNextAvailableToken(boolean useSecurity) {
        if (!useSecurity) {
            // No security - just rotate tokens
            String token = discordTokens.get(0);
            discordTokens.add(discordTokens.remove(0));
            return token;
        }

        // Security enabled - find a token that hasn't hit rate limit
        long currentTime = System.currentTimeMillis();

        for (String token : discordTokens) {
            long lastReset = tokenLastReset.getOrDefault(token, 0L);
            int messagesSent = tokenMessageCount.getOrDefault(token, 0);

            // Reset counter if 5 seconds have passed
            if (currentTime - lastReset >= 5000) {
                tokenMessageCount.put(token, 0);
                tokenLastReset.put(token, currentTime);
                messagesSent = 0;
            }

            // If this token can still send messages (less than 4 per 5 seconds for safety)
            if (messagesSent < 4) {
                return token;
            }
        }

        // All tokens are rate-limited, return null to skip this message
        return null;
    }

    private static void sendDiscordMessage(String message, String channelId, String token,
                                           boolean useSecurity, JLabel status, JButton typeButton,
                                           JLabel countLabel, int[] messageCount) {
        try {
            URL url = new URL("https://discord.com/api/v10/channels/" + channelId + "/messages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonPayload = "{\"content\":\"" + escapeJson(message) + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (useSecurity && (responseCode == 200 || responseCode == 201)) {
                // Update token usage count
                int count = tokenMessageCount.getOrDefault(token, 0);
                tokenMessageCount.put(token, count + 1);
            }

            messageCount[0]++;
            SwingUtilities.invokeLater(() -> {
                countLabel.setText("Messages sent: " + messageCount[0]);
                if (typeButton != null) {
                    if (responseCode == 200 || responseCode == 201) {
                        status.setText("Message sent successfully!");
                    } else if (responseCode == 429) {
                        status.setText("Rate limited! Enable Discord Security or add more tokens.");
                    } else {
                        status.setText("Failed to send. Response code: " + responseCode);
                    }
                    typeButton.setEnabled(true);
                } else {
                    if (responseCode == 200 || responseCode == 201) {
                        status.setText("Repeating... (message sent - " + discordTokens.size() + " tokens)");
                    } else if (responseCode == 429) {
                        status.setText("Repeating... (rate limited - add more tokens!)");
                    } else {
                        status.setText("Repeating... (error: " + responseCode + ")");
                    }
                }
            });

        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                status.setText("Error: " + ex.getMessage());
                if (typeButton != null) {
                    typeButton.setEnabled(true);
                }
            });
        }
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void sendMessage(String msg, JFrame frame,
                                    JLabel status, JButton typeButton, JLabel countLabel, int[] messageCount) {
        new Thread(() -> {
            try {
                if (typeButton != null) {
                    Thread.sleep(3000);
                }

                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new StringSelection(msg), null);

                Robot robot = new Robot();
                robot.setAutoDelay(10);

                boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");

                if (isMac) {
                    robot.keyPress(KeyEvent.VK_META);
                    robot.keyPress(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_META);
                } else {
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyPress(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                }

                Thread.sleep(50);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);

                messageCount[0]++;
                SwingUtilities.invokeLater(() -> {
                    countLabel.setText("Messages sent: " + messageCount[0]);
                    if (typeButton != null) {
                        status.setText("Done.");
                        typeButton.setEnabled(true);
                    } else {
                        status.setText("Repeating... (message sent)");
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    status.setText("Error: " + ex.getMessage());
                    if (typeButton != null) {
                        typeButton.setEnabled(true);
                    }
                });
            }
        }).start();
    }
}