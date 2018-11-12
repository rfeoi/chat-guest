package nspirep2p.application.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Frame class.
 * Created by robmroi03 on 09.11.2018.
 */
public class UserInterface extends JFrame {
    private JFrame frame;
    JButton submit;
    JTextField usernameField;
    JTextField ipField;
    JLabel usernameLabel;
    JLabel ipLabel;
    String username = "";
    String ipAddress = "";
    private JPanel userDetails;

    void start() {
        setObjects();
        userDetails.setLayout(new GridLayout(2, 2, 1, 1));
        frame.setSize(200,150);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 1, 1, 1));
        frame.setLocationRelativeTo(null);
        userDetails.add(usernameLabel);
        userDetails.add(usernameField);
        userDetails.add(ipLabel);
        userDetails.add(ipField);
        frame.add(userDetails);
        frame.add(submit);
    }

    void setObjects() {
        frame = new JFrame();
        usernameField = new JTextField();
        ipField = new JTextField();
        userDetails = new JPanel();
        usernameLabel = new JLabel("Username:");
        ipLabel = new JLabel("Server IP:");
        submit = new JButton("Log In");
        submit.addActionListener(actionListener);
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            username = usernameField.getText();
            ipAddress = ipField.getText();
            System.out.println(username + ipAddress);
            Main.mainClass.connectionHandler.connect(ipAddress, username);
        }
    };


    String getUsername() {
        return username;
    }

    String getIpAddress() {
        return ipAddress;
    }
}
