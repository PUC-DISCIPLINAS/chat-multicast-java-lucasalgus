package com.lucasalgus.windows;

import com.lucasalgus.Client;
import com.lucasalgus.controllers.ClientController;
import com.lucasalgus.model.Room;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoomsWindow {
    // Basic stuff
    JFrame frame;

    // Action components
    JTextField addressTextField;

    JButton connectButton;
    JButton createRoomButton;
    JButton updateRoomsButton;

    JList<String> roomsList;

    // Validation
    String address;

    public RoomsWindow() {
        frame = new JFrame("Chat: <sem conexão>");

        frame.add(mainPanel());
        frame.setSize(300, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        bindEvents();
    }

    private void bindEvents() {
        connectButton.addActionListener(event -> {
            address = addressTextField.getText();
            var success = ClientController.connect(address);

            if (success) {
                frame.setTitle(String.format("Chat: <%s>", address));

                JOptionPane.showMessageDialog(frame,
                    "Conectado ao endereço " + address,
                    "Conectado!",
                    JOptionPane.INFORMATION_MESSAGE
                );
                connectButton.setEnabled(false);
            } else {
                frame.setTitle("Chat: <sem conexão>");
                address = null;

                JOptionPane.showMessageDialog(frame,
                        "Ocorreu um erro. Verifique se o endereço foi informado corretamente, no formato ip:porta. (Exemplo: 228.5.6.7:6789)",
                        "Erro!",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        createRoomButton.addActionListener(event -> {
            if (address == null) {
                JOptionPane.showMessageDialog(frame,
                    "Informe o endereço de IP com a porta antes de criar uma sala. (Exemplo: 228.5.6.7:6789)",
                    "Erro!",
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            ClientController.createRoom(success -> {
                if (success) {
                    JOptionPane.showMessageDialog(frame,
                            "Sala criada com sucesso! Clique no botão 'Atualizar Lista' para visualiza-la.",
                            "Sucesso!",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            });
        });

        updateRoomsButton.addActionListener(event -> {
            if (address == null) {
                JOptionPane.showMessageDialog(frame,
                        "Informe o endereço de IP com a porta antes de visualizar as salas. (Exemplo: 228.5.6.7:6789)",
                        "Erro!",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            showRooms();
        });

        roomsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    roomsList.setEnabled(false);

                    var index = roomsList.locationToIndex(e.getPoint());
                    var id = index;

                    var username = (String)JOptionPane.showInputDialog(
                            null,
                            "Digite o seu nome de usuário que será exibido na sala:",
                            "Entrar na sala",
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (username == null) {
                        roomsList.setEnabled(true);
                        return;
                    }

                    ClientController.joinRoom(id, username, success -> {
                        if (success) {
                            Client.currentRoom = new Room(id);
                            Client.username = username;
                            Client.chatWindow.open();

                        } else {
                            roomsList.setEnabled(true);

                            JOptionPane.showMessageDialog(frame,
                                    "Ocorreu um erro ao entrar na sala. Tente novamente com um nome de usuário diferente e se o erro persisitir, verifique se o servidor está online.",
                                    "Erro!",
                                    JOptionPane.ERROR_MESSAGE
                            );

                        }
                    });
                }
            }
        });
    }

    private void showRooms() {
        ClientController.showRooms(rooms -> {
            Client.rooms = rooms;
            var model = new DefaultListModel<String>();

            rooms.forEach(room -> {
                model.addElement("Sala " + (room.getId() + 1));
            });

            roomsList.setModel(model);
        });
    }

    private JPanel mainPanel() {
        var panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(connectionPanel());
        panel.add(roomButtons());
        panel.add(roomsListPanel());

        return panel;
    }

    private JPanel connectionPanel() {
        var panel = new JPanel();
        addressTextField = new JTextField();
        connectButton = new JButton("Conectar");

        panel.setMaximumSize(new DimensionUIResource(999999, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(addressTextField);
        panel.add(connectButton);

        return panel;
    }

    private JPanel roomButtons() {
        var panel = new JPanel();
        createRoomButton = new JButton("Criar nova sala");
        updateRoomsButton = new JButton("Atualizar Lista");

        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(createRoomButton);
        panel.add(updateRoomsButton);

        return panel;
    }

    private JPanel roomsListPanel() {
        var panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(roomsListScrollPane());

        return panel;
    }

    private JScrollPane roomsListScrollPane() {
        roomsList = new JList<>();

        return new JScrollPane(roomsList);
    }
}
