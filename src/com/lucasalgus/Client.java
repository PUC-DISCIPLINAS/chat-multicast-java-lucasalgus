package com.lucasalgus;

import com.lucasalgus.model.Room;
import com.lucasalgus.windows.ChatWindow;
import com.lucasalgus.windows.RoomsWindow;

import java.util.ArrayList;

public class Client {
	public static ArrayList<Room> rooms;
	public static Room currentRoom;
	public static String username;

	public static ChatWindow chatWindow;
	public static RoomsWindow roomsWindow;

	public static void main(String[] args) {
		chatWindow = new ChatWindow();
		roomsWindow = new RoomsWindow();
	}
}
