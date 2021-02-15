package com.lucasalgus.controllers;

import com.lucasalgus.MulticastConnection;
import com.lucasalgus.model.Listener;
import com.lucasalgus.model.Room;
import com.lucasalgus.utils.MessageFactory;
import com.lucasalgus.utils.MessageUtils;

import java.util.ArrayList;

public class ClientController {
    static MulticastConnection connection;

    static String currentToken;
    static Listener currentListener;

    public static void initializeListener() {
		connection.listen(message -> {
			var token = MessageUtils.getTokenFromRequest(message);
			var identifier = MessageUtils.getIdentifierFromRequest(message);

			if (token != null && token.equals(currentToken)) {
				var status = MessageUtils.getStatusFromRequest(message);
				var vars = MessageUtils.getVarsFromRequest(message);

				if (status == null) {
					return;
				}

				switch (identifier) {
					case "createRoom":
						createRoomResponse(status.equals("success"));
						break;
					case "showRooms":
						showRoomsResponse(vars);
						break;
					case "joinRoom":
						joinRoomResponse(status.equals("success"));
						break;
					case "leaveRoom":
						leaveRoomResponse(status.equals("success"));
						break;
					case "showRoomInfo":
						showRoomInfoResponse(vars);
						break;
					case "sendMessage":
						sendMessageResponse(status.equals("success"));
						break;
					default:
						break;
				}
			}
		});
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
    		currentListener = listener;
		} catch(Exception e) {
    		listener.callback(false);
		}
	}

	public static void createRoomResponse(Boolean success) {
		currentListener.callback(success);
	}

	public static void showRooms(Listener<ArrayList<Room>> listener) {
		try {
			var request = MessageFactory.showRooms();
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			currentListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void showRoomsResponse(String[] roomIds) {
    	var rooms = new ArrayList<Room>();

		for (String roomId: roomIds) {
			rooms.add(new Room(Integer.parseInt(roomId)));
		}

		currentListener.callback(rooms);
	}

	public static void joinRoom(int roomId, String username, Listener<Boolean> listener) {
		try {
			var request = MessageFactory.joinRoom(roomId, username);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			currentListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void joinRoomResponse(Boolean success) {
    	currentListener.callback(success);
	}

	public static void leaveRoom(int roomId, String username, Listener<Boolean> listener) {
		try {
			var request = MessageFactory.leaveRoom(roomId, username);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			currentListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void leaveRoomResponse(Boolean success) {
    	currentListener.callback(success);
	}

	public static void showRoomInfo(int roomId, Listener<Room> listener) {
		try {
			var request = MessageFactory.showRoomInfo(roomId);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			currentListener = listener;
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

		currentListener.callback(new Room(roomId, users, messages));
	}

	public static void sendMessage(int roomId, String username, String message, Listener<Boolean> listener) {
		try {
			var request = MessageFactory.sendMessage(roomId, username, message);
			var token = MessageUtils.getTokenFromRequest(request);

			connection.sendMessage(request);

			currentToken = token;
			currentListener = listener;
		} catch(Exception e) {
			listener.callback(null);
		}
	}

	public static void sendMessageResponse(Boolean success) {
		currentListener.callback(success);
	}
}
