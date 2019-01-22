package nspirep2p.application.client;

import nspirep2p.application.client.connection.ConnectionHandler;
import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainInterface extends JFrame implements AWTEventListener {
    private JFrame frame;
    private JButton sendButton, changeUsernameButton;
    private JButton sendMessage, createTempChannel, deleteTempChannel;
    private JLabel messages;
    private JTextField userInput;
    private JTextArea users;
    private HashMap<String, JLabel> channel;
    private JPanel channelPanel;

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
        JPanel userPanel = new JPanel();
        users = new JTextArea("Users");
        userPanel.add(users);
        frame.add(userPanel, BorderLayout.LINE_END);
    }

    private void setButtonPane() {
        JPanel buttonPanel = new JPanel();
        changeUsernameButton = new JButton("Change username");
        changeUsernameButton.addActionListener(actionListener);
        buttonPanel.add(changeUsernameButton);

        sendMessage = new JButton("message");
        sendMessage.addActionListener(actionListener);
        buttonPanel.add(sendMessage);

        createTempChannel = new JButton("CreateTempChannel");
        createTempChannel.addActionListener(actionListener);
        buttonPanel.add(createTempChannel);

        deleteTempChannel = new JButton("RemoveTempChannel");
        deleteTempChannel.addActionListener(actionListener);
        buttonPanel.add(deleteTempChannel);

        frame.add(buttonPanel, BorderLayout.PAGE_START);

    }

    public void setNewMessage(String from, String time, String message) {
        String text = messages.getText();
        text = text.replace("</html>", "");
        messages.setText(text + "<br>[" + time + "] <b>" + from + ":</b> " + message + "</html>");
    }

    public void setNewServerMessage(String message) {
        String text = messages.getText();
        text = text.replace("</html>", "");
        messages.setText(text + message + "</html>");
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
    }

    public void reload() {
        users.setText("User:\n" + Main.mainClass.mainInterfaceData.getUsers());

        channel = new HashMap<>();
        ArrayList<String> temp_channelCreate = new ArrayList<>(Arrays.asList(Main.mainClass.mainInterfaceData.getChannel()));
        //ArrayList<String> temp_channelCreate = (ArrayList<String>) Arrays.asList(Main.mainClass.mainInterfaceData.getChannel());
        for (String channelName : channel.keySet()){
            if (!temp_channelCreate.contains(channelName)){
                channel.remove(channelName);
            }
        }
        for (String channelName : temp_channelCreate){
            System.out.println("channelName: " + channelName);
            if (!channel.containsKey(channelName)){
                JLabel channelLabel = new JLabel(channelName);
                System.out.println(channelName);
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


    private void setSendMessage() {
        CommunicationParser com = new CommunicationParser(ClientType.SERVER);
        Client[] clients = new Client[10];
        for (int i = 0; i<clients.length; i++) {
            clients[i] = new Client();
            clients[i].username = "Client" + i;
            clients[i].uuid = "1234" + i;
        }

        String[] message = com.sendMessage(clients[1], "Channel", "My message");
        //String[] message = com.getClients(clients[0],clients,false);
        try {
            Main.mainClass.connectionHandler.parsePackage(message);
        } catch (Exception e) {

        }
    }

    void setCreateTempChannel() {
        CommunicationParser com = new CommunicationParser(ClientType.SERVER);
        Client clients = new Client();
        clients.username = "Client1" + ((int) (Math.random()*100));
        clients.uuid = "1234";

        String[] message = com.createTempChannel(clients);
        try {
            Main.mainClass.connectionHandler.parsePackage(message);
        } catch (Exception e) {

        }
    }

    void setDeleteTempChannel() {
        CommunicationParser com = new CommunicationParser(ClientType.SERVER);
        Client clients = new Client();
        clients.username = "Client1";
        clients.uuid = "1234";

        String[] message = com.removeTempChannel(clients);
        try {
            Main.mainClass.connectionHandler.parsePackage(message);
        } catch (Exception e) {

        }
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
            } else if(button == sendMessage) {
                setSendMessage();
            } else if(button == createTempChannel) {
                setCreateTempChannel();
            } else if(button == deleteTempChannel) {
                setDeleteTempChannel();
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
