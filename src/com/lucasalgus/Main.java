package com.lucasalgus;

import com.lucasalgus.model.Room;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class Main {
    static int roomId = 0;
    static MulticastConnection socket;

    static ArrayList<Room> rooms;

    public static void main(String[] args) {
        rooms = new ArrayList<>();

        try {
            socket = new MulticastConnection("228.5.6.7:6789");

            socket.listen((String message) -> {
                System.out.println(message);

                try {
                    handleRequests(message);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void handleRequests(String message) throws Exception {
        var messageArray = message.split(";");

        var identifier = messageArray[0];
        var vars = Arrays.copyOfRange(messageArray, 1, messageArray.length);

        switch (identifier) {
            case "createRoom":
                createRoom(vars);
                break;
            case "showRooms":
                showRooms(vars);
                break;
            case "joinRoom":
                joinRoom(vars);
                break;
            case "leaveRoom":
                leaveRoom(vars);
                break;
            case "showRoomInfo":
                showRoomInfo(vars);
                break;
            case "sendMessage":
                sendMessage(vars);
                break;
            default:
                break;
        }
    }

    public static void createRoom(String[] requestVars) throws Exception {
        var room = new Room(roomId);
        var requestToken = requestVars[0];
        roomId++;

        rooms.add(room);
        socket.sendMessage("createRoom:success;" + requestToken + ";");
    }

    public static void showRooms(String[] requestVars) throws Exception {
        var requestToken = requestVars[0];
        var roomsString = new StringBuffer();

        rooms.forEach(room -> {
            roomsString.append(room.toSimpleString());
            roomsString.append(";");
        });

        socket.sendMessage("showRooms:success;" + requestToken + ";" + roomsString.toString() + ";");
    }

    public static void joinRoom(String[] requestVars) throws Exception {
        var requestToken = requestVars[0];
        var roomId = Integer.parseInt(requestVars[1]);
        var username = requestVars[2];

        var room = rooms
                .stream()
                .filter((Room r) -> r.getId() == roomId)
                .collect(Collectors.toList()).get(0);

        var userExists = room.findUser(username);

        if (!userExists) {
            room.addUser(username);
            socket.sendMessage("joinRoom:success;" + requestToken + ";");

        } else {
            socket.sendMessage("joinRoom:fail;" + requestToken + ";");
        }
    }

    public static void leaveRoom(String[] requestVars) throws Exception {
        var requestToken = requestVars[0];
        var roomId = Integer.parseInt(requestVars[1]);
        var username = requestVars[2];

        var room = rooms
                .stream()
                .filter((Room r) -> r.getId() == roomId)
                .collect(Collectors.toList()).get(0);

        room.removeUser(username);
        socket.sendMessage("leaveRoom:success;" + requestToken + ";");
    }

    public static void showRoomInfo(String[] requestVars) throws Exception {
        var requestToken = requestVars[0];
        var roomId = Integer.parseInt(requestVars[1]);

        var room = rooms
                .stream()
                .filter((Room r) -> r.getId() == roomId)
                .collect(Collectors.toList()).get(0);

        socket.sendMessage("showRoomInfo:success;" + requestToken + ";" + room + ";");
    }

    public static void sendMessage(String[] requestVars) throws Exception {
        var requestToken = requestVars[0];
        var roomId = Integer.parseInt(requestVars[1]);
        var username = requestVars[2];
        var message = requestVars[3];

        var room = rooms
                .stream()
                .filter((Room r) -> r.getId() == roomId)
                .collect(Collectors.toList()).get(0);

        room.addMessage(username + ": " + message);
        socket.sendMessage("sendMessage:success;" + requestToken + ";");
    }
}
