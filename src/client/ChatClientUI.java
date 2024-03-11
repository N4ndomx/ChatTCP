package client;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import client.observer.EventListener;

public class ChatClientUI implements EventListener {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private HiloChatTCP client;
    private String username;

    public ChatClientUI(HiloChatTCP client) {
        this.client = client;
        username = System.getProperty("user.name");
        createUI();
        login();

    }

    private void createUI() {
        frame = new JFrame("Chat Client");
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        messageField = new JTextField(25);
        sendButton = new JButton("Send");

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(chatArea));
        panel.add(messageField);
        panel.add(sendButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        sendButton.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> comandoMsg = new ArrayList<>();
                comandoMsg.add("G");
                comandoMsg.add(username + ": " + messageField.getText());
                client.sendMessage(comandoMsg);
                messageField.setText("");
            }
        });

    }

    public void login() {
        List<String> paqueteMsg = new ArrayList<>();
        paqueteMsg.add("NU");
        paqueteMsg.add(username);
        client.sendMessage(paqueteMsg);
    }

    public void desconectar() {

        try {
            client.desconectar();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(-1);
    }

    @Override
    public void update(String eventType, String msg, String nameUser) {
        chatArea.append(msg + "\n");
    }

}