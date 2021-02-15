package com.lucasalgus.windows;

import com.lucasalgus.Client;
import com.lucasalgus.controllers.ClientController;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatWindow {	
	JFrame frame;

	JButton leaveButton;
	JButton sendButton;

	public ChatWindow() {
		frame = new JFrame("Sala 1");
		
		frame.add(mainPanel());
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		bindEvents();
	}

	public void bindEvents() {
		var that = this;

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
	}

	public void open() {
		frame.setVisible(true);
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
		var messagesTextField = new JTextField();
		var sendButton = new JButton("Enviar");
		
		panel.setMaximumSize(new DimensionUIResource(999999, 20));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(messagesTextField);
		
		panel.add(sendButton);

		return panel;
	}

	private JTextArea messagesTextArea() {
		var component = new JTextArea("test");
		component.setEditable(false);

		return component;
	}

	private JScrollPane userListScrollPane() {
		var list = new JList<>(new String[]{"test", "test1"});
		var listScroller = new JScrollPane(list);

		return listScroller;
	}
}
