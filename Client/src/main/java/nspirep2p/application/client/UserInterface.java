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
        frame.setSize(350,150);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Please enter your data!");
        addObjects();
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
        submit = new JButton("Log In");
        submit.addActionListener(actionListener);
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

    private void makeVisible(boolean connectionError) {
        if (!connectionError) {
            frame.setTitle("Error when connecting to the server!");
        } else {
            frame.setTitle("Please enter your data!");
        }
        frame.setVisible(true);
    }

    /**
     * sets the username and IPAddress and gives them to the connectionHandler
     */
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            if (username.isEmpty() || username.equals("")) { return; }
            String ipAddress = ipField.getText();
            if (ipAddress.isEmpty() || ipAddress.equals("")) { return; }
            boolean hasSucceeded = Main.mainClass.connectionHandler.connect(ipAddress, username);
            frame.setVisible(false);
            if (!hasSucceeded) { makeVisible(false); }
        }
    };
}
