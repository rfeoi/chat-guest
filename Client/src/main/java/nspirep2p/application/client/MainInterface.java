package nspirep2p.application.client;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MainInterface extends JFrame {
    private JFrame frame;

    private JTextArea textArea;

    void start() {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setSize(1000,500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        setPanel();
    }

    private void setPanel() {
        setChannelPanel();
        setChatPanel();
        setUserPanel();
    }

    private void setChannelPanel() {
        JPanel channelPanel = new JPanel();
        JLabel l1 = new JLabel("Channel: ");
        channelPanel.add(l1);
        frame.add(channelPanel, BorderLayout.LINE_START);
    }

    private void setChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        textArea = new JTextArea();
        textArea.append("Session started!");
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JTextField userInput = new JTextField();
        JButton sendButton = new JButton("Send!");
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(userInput, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.LINE_END);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.PAGE_END);
        frame.add(chatPanel, BorderLayout.CENTER);
    }

    private void setUserPanel() {
        JPanel userPanel = new JPanel();
        JLabel l3 = new JLabel("User");
        userPanel.add(l3);
        frame.add(userPanel, BorderLayout.LINE_END);
    }

    public void setNewMessage(String from, String time, String message) {
        textArea.append("\n[" + time + "] <b>" + from + ":</b> " + message);
    }

}
