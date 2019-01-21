package nspirep2p.application.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by robmroi03 on 09.11.2018.
 * User interface for logging into a specific server
 */
class UserInterface extends JFrame {
    private JFrame frame;
    private JButton submit;
    private JTextField usernameField;
    private JTextField ipField;
    private JLabel usernameLabel;
    private JLabel ipLabel;
    private JPanel userDetails;

    /**
     * sets settings for the JFrame frame
     */
    void start() {
        setObjects();
        userDetails.setLayout(new GridLayout(2, 2, 1, 1));
        frame.setLayout(new GridLayout(2, 1, 1, 1));
        frame.setSize(350,200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        addObjects();
        makeVisible("Please enter your data!");
    }

    /**
     * sets texts for the textfields
     * @param username the text for the username textField
     * @param IPAddress the text for the IP-Address textField
     */
    void startWithText(String username, String IPAddress) {
        start();
        usernameField.setText(username);
        ipField.setText(IPAddress);
    }

    /**
     * creates the objects visible on the JFrame
     */
    private void setObjects() {
        frame = new JFrame();
        usernameField = new JTextField();
        ipField = new JTextField();
        userDetails = new JPanel();
        usernameLabel = new JLabel("Username:");
        ipLabel = new JLabel("Server IP:");
        submit = new JButton("Connect!");
        submit.addActionListener(actionListener);
        frame.setSize(350,200);
    }

    /**
     * adds the objects to the frame (and the JPanel)
     */
    private void addObjects() {
        userDetails.add(ipLabel);
        userDetails.add(ipField);
        userDetails.add(usernameLabel);
        userDetails.add(usernameField);
        frame.add(userDetails);
        frame.add(submit);
    }

    /**
     * makes the frame visible
     * @param message the title of the screen
     */
    private void makeVisible(String message) {
        frame.setTitle(message);
        frame.setVisible(true);
    }

    /**
     * sets the username and IPAddress and gives them to the connectionHandler
     */
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            if (username.isEmpty()) { return; }
            String ipAddress = ipField.getText();
            if (ipAddress.isEmpty()) { return; }
            if (username.contains(" ")) {
                makeVisible("You can't use spaces in your username!");
                return;
            }
            if (ipAddress.contains(" ")) {
                makeVisible("You can't use spaces in the IP-Address!");
                return;
            }
            frame.setVisible(false);
            boolean hasSucceeded = Main.mainClass.connectionHandler.connect(ipAddress, username);
            if (!hasSucceeded) { makeVisible("Error when connecting to the server!"); }
            Main.mainClass.mainInterface.start();
        }
    };
}
