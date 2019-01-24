package nspirep2p.application.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainInterface extends JFrame implements AWTEventListener {
    private JFrame frame;
    private JButton sendButton, changeUsernameButton, enterGroup;
    private JLabel messages;
    private JTextField userInput;
    private HashMap<String, JLabel> channel, users;
    private JPanel channelPanel, userPanel;

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
        setButtonPane();
        setUserPanel();
        frame.setVisible(true);
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
        JLabel label = new JLabel("Channel: ");
        channelPanel = new JPanel();
        channelPanel.setLayout(new BoxLayout(channelPanel, BoxLayout.PAGE_AXIS));
        channelPanel.add(label);
        channel = new HashMap<>();
        JScrollPane jScrollPane = new JScrollPane(channelPanel);
        frame.add(jScrollPane, BorderLayout.LINE_START);
    }

    private void setUserPanel() {
        JLabel label = new JLabel("User: ");
        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.PAGE_AXIS));
        users = new HashMap<>();
        userPanel.add(label);
        JScrollPane jScrollPane = new JScrollPane(userPanel);
        frame.add(jScrollPane, BorderLayout.LINE_END);
    }

    private void setButtonPane() {
        JPanel buttonPanel = new JPanel();
        changeUsernameButton = new JButton("Change username");
        changeUsernameButton.addActionListener(actionListener);
        buttonPanel.add(changeUsernameButton);

        enterGroup = new JButton("Enter Group");
        enterGroup.addActionListener(actionListener);
        buttonPanel.add(enterGroup);

        frame.add(buttonPanel, BorderLayout.PAGE_START);

    }

    public void setNewMessage(String from, String time, String message) {
        String text = messages.getText();
        text = text.replace("</html>", "");
        messages.setText(text + "<br>[" + time + "] <b>" + from + ":</b> " + message + "</html>");
        frame.setVisible(false);
        frame.setVisible(true);
    }

    public void setNewServerMessage(String message) {
        String text = messages.getText();
        text = text.replace("</html>", "");
        messages.setText(text + message + "</html>");
        frame.setVisible(false);
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = userInput.getText();
        if (message.isEmpty()) return;
        userInput.setText("");
        Main.mainClass.connectionHandler.createAMessage(message);
        //setNewMessage(Main.mainClass.getUsername(), Main.mainClass.getTime(), message);
    }

    private void changeUsername() {
        String username = "";
        try {
            while (username.isEmpty() || username.contains(" ")) {
                username = JOptionPane.showInputDialog("Enter your new username.", Main.mainClass.getUsername());
            }
        } catch (NullPointerException e) { return; }
        try {
            Main.mainClass.userPropetySave.generateConfigFile(Main.mainClass.getIP(), username);
        } catch (IOException e) {
            e.printStackTrace();
        }
           Main.mainClass.connectionHandler.changeUsername(username);
    }

    private void redrawChannel(){
        for (Component component : channelPanel.getComponents()){
            channelPanel.remove(component);
        }
        channelPanel.add(new JLabel("Channel: "));
        for (JLabel label : channel.values()){
            channelPanel.add(label);
            label.setVisible(true);
        }

        for (Component component : userPanel.getComponents()){
            userPanel.remove(component);
        }
        userPanel.add(new JLabel("User: "));
        for (JLabel label : users.values()){
            userPanel.add(label);
            label.setVisible(true);
        }
        frame.setVisible(false);
        frame.setVisible(true);
    }

    public void reload() {
        users = new HashMap<>();
        ArrayList<String> temp_userCreate = new ArrayList<>(Arrays.asList(Main.mainClass.mainInterfaceData.getUsers()));
        for (String username : users.keySet()){
            if (!temp_userCreate.contains(username)){
                users.remove(username);
            }
        }
        for (String username : temp_userCreate){
            if (!users.containsKey(username)){
                JLabel userLabel = new JLabel(username);
                userLabel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Main.mainClass.connectionHandler.invite(username, Main.mainClass.mainInterfaceData.getHasCreatedTempChannel());
                        if (Main.mainClass.mainInterfaceData.getHasCreatedTempChannel()) {
                            Main.mainClass.mainInterfaceData.setHasCreatedTempChannel();
                        }
                    }
                    @Override
                    public void mousePressed(MouseEvent e) { }
                    @Override
                    public void mouseReleased(MouseEvent e) { }
                    @Override
                    public void mouseEntered(MouseEvent e) { }
                    @Override
                    public void mouseExited(MouseEvent e) { }
                });
                users.put(username, userLabel);
            }
        }

        channel = new HashMap<>();
        ArrayList<String> temp_channelCreate = new ArrayList<>(Arrays.asList(Main.mainClass.mainInterfaceData.getChannel()));
        for (String channelName : channel.keySet()){
            if (!temp_channelCreate.contains(channelName)){
                channel.remove(channelName);
            }
        }
        for (String channelName : temp_channelCreate){
            if (!channel.containsKey(channelName)){
                JLabel channelLabel = new JLabel(channelName);
                channelLabel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Main.mainClass.connectionHandler.move(channelName);
                        Main.mainClass.mainInterfaceData.setCurrentChannel(channelName);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) { }
                    @Override
                    public void mouseReleased(MouseEvent e) { }
                    @Override
                    public void mouseEntered(MouseEvent e) { }
                    @Override
                    public void mouseExited(MouseEvent e) { }
                });
                channel.put(channelName, channelLabel);
            }
        }
        redrawChannel();
    }

    private void setEnterGroup() {
        Main.mainClass.connectionHandler.setGroup(JOptionPane.showInputDialog("Enter the key!"));
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!(e.getSource() instanceof JButton)) return;
            JButton button = (JButton) e.getSource();
            if (button==sendButton) {
                sendMessage();
            } else if (button == enterGroup) {
                setEnterGroup();
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
