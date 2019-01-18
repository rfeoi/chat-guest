package nspirep2p.application.client;

import nspirep2p.application.client.fileHandling.UserPropetySave;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class MainInterface extends JFrame implements AWTEventListener {
    private JFrame frame;
    private JButton sendButton, changeUsernameButton;
    private JLabel messages;
    private JTextField userInput;
    private JTextArea users, channel;

    MainInterface() {
        //detects if a key is pressed
        long eventMask = AWTEvent.KEY_EVENT_MASK;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.addAWTEventListener(this, eventMask);
    }

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
        setButtonPane();
    }

    private void setChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        messages = new JLabel();
        messages.setText("<html>Session started!</html>");
        //messages.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messages);

        userInput = new JTextField();
        sendButton = new JButton("Send!");
        sendButton.addActionListener(actionListener);
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(userInput, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.LINE_END);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.PAGE_END);
        frame.add(chatPanel, BorderLayout.CENTER);
    }

    private void setChannelPanel() {
        JPanel channelPanel = new JPanel();
        channel = new JTextArea("Channel");
        channelPanel.add(channel);
        frame.add(channelPanel, BorderLayout.LINE_START);
    }

    private void setUserPanel() {
        JPanel userPanel = new JPanel();
        users = new JTextArea("Users");
        userPanel.add(users);
        frame.add(userPanel, BorderLayout.LINE_END);
    }

    private void setButtonPane() {
        JPanel buttonPanel = new JPanel();
        changeUsernameButton = new JButton("Nutzernamen aendern");
        changeUsernameButton.addActionListener(actionListener);
        buttonPanel.add(changeUsernameButton);
        frame.add(buttonPanel, BorderLayout.PAGE_START);

    }

    private void setNewMessage(String from, String time, String message) {
        String text = messages.getText();
        text = text.replace("</html>", "");
        messages.setText(text + "<br>[" + time + "] <b>" + from + ":</b> " + message + "</html>");
    }


    private void sendMessage() {
        String message = userInput.getText();
        if (message.isEmpty()) return;
        userInput.setText("");
        Main.mainClass.connectionHandler.sendMessage(new String[]{message});

        setNewMessage(Main.mainClass.getUsername(), Main.mainClass.getTime(), message);
    }

    private void changeUsername() {
        String username = "";
        while (username.isEmpty() || username.contains(" ")) {
            username = JOptionPane.showInputDialog("Geben Sie hier ihren neuen Benutzernamen ein.", Main.mainClass.getUsername());
            System.out.println(username);
        }
        try {
            Main.mainClass.userPropetySave.generateConfigFile(Main.mainClass.getIP(), username);
        } catch (IOException e) {
            e.printStackTrace();
        }
           Main.mainClass.connectionHandler.changeUsername(username);
    }

    public void reload() {
        users.setText(Main.mainClass.mainInterfaceData.getUsers());
        channel.setText(Main.mainClass.mainInterfaceData.getChannel());
    }






    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!(e.getSource() instanceof JButton)) return;
            JButton button = (JButton) e.getSource();
            if (button==sendButton) {
                sendMessage();
            } else if (button == changeUsernameButton) {
                changeUsername();
            }
        }
    };

    @Override
    public void eventDispatched(AWTEvent event) {
        int ID = event.getID();
        if (ID == KeyEvent.KEY_PRESSED) {
            if (event.paramString().contains("keyCode=10")) sendMessage();
        }
    }
}
