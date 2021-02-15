package com.lucasalgus.windows;

import com.lucasalgus.Client;
import com.lucasalgus.controllers.ClientController;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatWindow {	
	JFrame frame;

	JTextField messagesTextField;
	JButton leaveButton;
	JButton sendButton;

	JList<String> usersList;
	JList<String> messagesList;

	public ChatWindow() {
		frame = new JFrame("Sala 1");
		
		frame.add(mainPanel());
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		bindEvents();
	}

	public void bindEvents() {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				ClientController.leaveRoom(Client.currentRoom.getId(), Client.username, success -> {
					// do nothing
				});
			}
		});

		leaveButton.addActionListener(event -> {
			ClientController.leaveRoom(Client.currentRoom.getId(), Client.username, success -> {
				this.close();
			});
		});

		sendButton.addActionListener(event -> {
			if (messagesTextField.getText().length() == 0 ) {
				return;
			}

			ClientController.sendMessage(
				Client.currentRoom.getId(),
				Client.username,
				messagesTextField.getText(),
				success -> {
					messagesTextField.setText("");
				}
			);
		});
	}

	public void open() {
		frame.setVisible(true);
		ClientController.showRoomInfo(Client.currentRoom.getId(), room -> {
			Client.currentRoom = room;
			var usersModel = new DefaultListModel<String>();
			room.getUsers().forEach(user -> {
				usersModel.addElement(user);
			});

			usersList.setModel(usersModel);

			var messagesModel = new DefaultListModel<String>();
			room.getMessages().forEach(message -> {
				messagesModel.addElement(message);
			});

			messagesList.setModel(messagesModel);
		});
	}

	public void close() {
		frame.setVisible(false);
	}

	private JPanel mainPanel() {
		var panel = new JPanel();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(chatPanel());

		return panel;
	}

	private JPanel chatPanel() {
		var chatPanel = new JPanel();
		chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.X_AXIS));
		
		chatPanel.add(userListPanel());
		chatPanel.add(messagesPanel());

		return chatPanel;
	}

	private JPanel userListPanel() {
		var panel = new JPanel();
		var label = new JLabel("Usuários online:");
		leaveButton = new JButton("Sair da sala");

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(userListScrollPane());
		panel.add(leaveButton);

		return panel;
	}

	private JPanel messagesPanel() {
		var panel = new JPanel();
		var label = new JLabel("Mensagens:");
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(messagesTextArea());
		panel.add(messageInputPanel());
		
		return panel;
	}

	private JPanel messageInputPanel() {
		var panel = new JPanel();
		messagesTextField = new JTextField();
		sendButton = new JButton("Enviar");
		
		panel.setMaximumSize(new DimensionUIResource(999999, 20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(messagesTextField);
		
		panel.add(sendButton);

		return panel;
	}

	private JScrollPane messagesTextArea() {
		messagesList = new JList<>();
		var listScroller = new JScrollPane(messagesList);

		return listScroller;
	}

	private JScrollPane userListScrollPane() {
		usersList = new JList<>();
		var listScroller = new JScrollPane(usersList);

		return listScroller;
	}
}
