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

			if (token != null && token.equals(currentToken)) {
				var status = MessageUtils.getStatusFromRequest(message);
				var identifier = MessageUtils.getIdentifierFromRequest(message);
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
		currentListener = null;
		currentToken = null;
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
		currentListener = null;
		currentToken = null;
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
		currentListener = null;
		currentToken = null;
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
		currentListener = null;
		currentToken = null;
	}
}
