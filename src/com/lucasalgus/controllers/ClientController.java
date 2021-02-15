package com.lucasalgus.controllers;

import com.lucasalgus.Client;
import com.lucasalgus.MulticastConnection;
import com.lucasalgus.model.Listener;
import com.lucasalgus.model.Room;
import com.lucasalgus.utils.MessageFactory;
import com.lucasalgus.utils.MessageUtils;

import java.util.ArrayList;

public class ClientController {
    static MulticastConnection connection;

    static String currentToken;

    static Listener responseListener;

	static Listener<String[]> createRoom;
	static Listener<String[]> joinRoom;
	static Listener<String[]> leaveRoom;
	static Listener<String[]> sendMessage;

    public static void initializeListener() {
		connection.listen(message -> {
			var token = MessageUtils.getTokenFromRequest(message);
			var identifier = MessageUtils.getIdentifierFromRequest(message);
			var vars = MessageUtils.getVarsFromRequest(message);

			if (token != null && token.equals(currentToken)) {
				var status = MessageUtils.getStatusFromRequest(message);

				if (status == null) {
					return;
				}

				switch (identifier) {
					case "createRoom":
						createRoomResponse(status.equals("success"));
						createRoomReceived(vars);
						break;
					case "showRooms":
						showRoomsResponse(vars);
						break;
					case "joinRoom":
						joinRoomResponse(status.equals("success"));
						joinRoomReceived(vars);
						break;
					case "leaveRoom":
						leaveRoomResponse(status.equals("success"));
						leaveRoomReceived(vars);
						break;
					case "showRoomInfo":
						showRoomInfoResponse(vars);
						break;
					case "sendMessage":
						sendMessageResponse(status.equals("success"));
						sendMessageReceived(vars);
						break;
				}
			} else {
				var status = MessageUtils.getStatusFromRequest(message);

				if (status == null || !status.equals("success")) {
					return;
				}

				switch(identifier) {
					case "createRoom":
						createRoomReceived(vars);
						break;
					case "joinRoom":
						joinRoomReceived(vars);
						break;
					case "leaveRoom":
						leaveRoomReceived(vars);
						break;
					case "sendMessage":
						sendMessageReceived(vars);
						break;
				}
			}
		});
	}

	public static void addCreateRoomListener(Listener<String[]> listener) {
		createRoom = listener;
	}

	public static void addJoinRoomListener(Listener<String[]> listener) {
		joinRoom = listener;
	}

	public static void addLeaveRoomListener(Listener<String[]> listener) {
		leaveRoom = listener;
	}

	public static void addSendMessageListener(Listener<String[]> listener) {
		sendMessage = listener;
	}

    public static boolean connect(String address) {
        try {
			connection = new MulticastConnection(address);
			initializeListener();
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
    }

    public static void createRoom(Listener<Boolean> listener) {
    	try {
    		var request = MessageFactory.createRoom();
    		var token = MessageUtils.getTokenFromRequest(request);

    		connection.sendMessage(request);

    		currentToken = token;
    		responseListener = listener;
		} catch(Exception e) {
    		listener.callback(false);
		}
	}

	public static void createRoomResponse(Boolean success) {
		responseListener.callback(success);
	}

	public static void showRooms(Listener<ArrayList<Room>> listener) {
		try {
			var request = MessageFactory.showRooms();
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			responseListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void showRoomsResponse(String[] roomIds) {
    	var rooms = new ArrayList<Room>();

		for (String roomId: roomIds) {
			rooms.add(new Room(Integer.parseInt(roomId)));
		}

		responseListener.callback(rooms);
	}

	public static void joinRoom(int roomId, String username, Listener<Boolean> listener) {
		try {
			var request = MessageFactory.joinRoom(roomId, username);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			responseListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void joinRoomResponse(Boolean success) {
    	responseListener.callback(success);
	}

	public static void leaveRoom(int roomId, String username, Listener<Boolean> listener) {
		try {
			var request = MessageFactory.leaveRoom(roomId, username);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			responseListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void leaveRoomResponse(Boolean success) {
    	responseListener.callback(success);
	}

	public static void showRoomInfo(int roomId, Listener<Room> listener) {
		try {
			var request = MessageFactory.showRoomInfo(roomId);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			responseListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void showRoomInfoResponse(String[] roomVars) {
    	var roomId = Integer.parseInt(roomVars[0]);
    	var users = new ArrayList<String>();
    	var messages = new ArrayList<String>();

    	var roomUsersCountIndex = 1;
    	var roomUsersCount = Integer.parseInt(roomVars[roomUsersCountIndex]);

    	var roomMessagesCountIndex = 2 + roomUsersCount;

    	for (int i = 0; i < roomVars.length; i++) {
    		var element = roomVars[i];

    		if (i > roomMessagesCountIndex) {
				messages.add(element);
				continue;
			}
    		if (i > roomUsersCountIndex && i < roomMessagesCountIndex) {
    			users.add(element);
			}
		}

		responseListener.callback(new Room(roomId, users, messages));
	}

	public static void sendMessage(int roomId, String username, String message, Listener<Boolean> listener) {
		try {
			var request = MessageFactory.sendMessage(roomId, username, message);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			responseListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void sendMessageResponse(Boolean success) {
		responseListener.callback(success);
	}

	public static void createRoomReceived(String[] vars) {
    	if (createRoom == null) {
    		return;
		}

    	createRoom.callback(vars);
	}

	public static void joinRoomReceived(String[] vars) {
    	if (joinRoom == null) {
    		return;
		}

		var roomId = Integer.parseInt(vars[0]);

		if (Client.currentRoom.getId() == roomId) {
			joinRoom.callback(vars);
		}
	}

	public static void leaveRoomReceived(String[] vars) {
    	if (leaveRoom == null) {
    		return;
		}

		var roomId = Integer.parseInt(vars[0]);

		if (Client.currentRoom.getId() == roomId) {
			leaveRoom.callback(vars);
		}
	}

	public static void sendMessageReceived(String[] vars) {
    	if (sendMessage == null) {
    		return;
		}

		var roomId = Integer.parseInt(vars[0]);

		if (Client.currentRoom.getId() == roomId) {
			sendMessage.callback(vars);
		}
	}
}
