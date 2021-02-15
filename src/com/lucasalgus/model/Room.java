package com.lucasalgus.model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Room {
	private final int id;
	private ArrayList<String> users;
	private ArrayList<String> messages;

	public Room(int id) {
		this.id = id;
		this.users = new ArrayList<>();
		this.messages = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void addUser(String username) {
		this.users.add(username);
	}

	public boolean findUser(String username) {
		AtomicReference<Boolean> found = new AtomicReference<>(false);

		users.forEach(user -> {
			if (user.equals(username)) {
				found.set(true);
			}
		});

		return found.get();
	}

	public void removeUser(String username) {
		var filteredUsers = users
			.stream()
			.filter(user -> !user.equals(username))
			.collect(Collectors.toCollection(ArrayList<String>::new));

		this.users = filteredUsers;
	}

	public void addMessage(String message) {
		messages.add(message);
	}

	public String toString() {
		var usersString = new StringBuffer();
		users.forEach(user -> {
			usersString.append(user.toString());
			usersString.append(";");
		});

		var messagesString = new StringBuffer();
		messages.forEach(message -> {
			messagesString.append(message);
			messagesString.append(";");
		});

		var string = String.format(
			"%d;%d;%s;%d;%s",
			id,
			users.size(),
			usersString.toString(),
			messages.size(),
			messagesString.toString()
		);

		return string;
	}

	public String toSimpleString() {
		return Integer.toString(this.id);
	}
}
