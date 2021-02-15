package com.lucasalgus.utils;

public class MessageFactory {
    private static String generateToken() {
        return Double.toHexString(Math.random());
    }

    public static String createRoom() {
        return "createRoom;" + generateToken() + ";";
    }

    public static String showRooms() {
        return "showRooms;" + generateToken() + ";";
    }

    public static String joinRoom(int roomId, String username) {
        return "joinRoom;" + generateToken() + ";" + roomId + ";" + username + ";";
    }

    public static String leaveRoom(int roomId, String username) {
        return "leaveRoom;" + generateToken() + ";" + roomId + ";" + username + ";";
    }
}
