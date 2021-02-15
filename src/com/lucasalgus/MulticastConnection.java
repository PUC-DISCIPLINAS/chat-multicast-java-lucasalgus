package com.lucasalgus;

import com.lucasalgus.model.Listener;
import com.lucasalgus.utils.MessageUtils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastConnection extends Thread {
	private MulticastSocket socket;
	private Listener<String> listener;
	private String id;
	private String port;
	private String beforeLastToken;
	private String beforeLastHeader;
	private String lastToken;
	private String lastHeader;

	public MulticastConnection(String address) throws Exception {
		var addressArray = address.split(":");

		id = addressArray[0];
		port = addressArray[1];
		socket = new MulticastSocket(Integer.parseInt(port));
	}

	public void listen(Listener<String> listener) {
		this.listener = listener;
		this.start();
	}

	public void sendMessage(String message) throws Exception {
		var data = message.getBytes();
		var id = InetAddress.getByName(this.id);
		var port = Integer.parseInt(this.port);

		var request = new DatagramPacket(data, data.length, id, port);
		
		socket.send(request);
	}

	@Override
	public void run() {
		if (port == null) {
			return;
		}

		try {
			var buffer = new byte[1000];
			var id = InetAddress.getByName(this.id);
			var port = Integer.parseInt(this.port);
			
			socket = new MulticastSocket(port);
			socket.joinGroup(id);

			while (true) {
				var request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);

				var message = new String(request.getData(), request.getOffset(), request.getLength());
				var token = MessageUtils.getTokenFromRequest(message);
				var header = MessageUtils.getHeaderFromRequest(message);

				if (
					(!header.equals(lastHeader) || !token.equals(lastToken)) &&
					(!header.equals(beforeLastHeader) || !token.equals(beforeLastToken))
				){
					this.listener.callback(message);

					this.beforeLastToken = this.lastToken;
					this.beforeLastHeader = this.lastHeader;

					this.lastToken = token;
					this.lastHeader = header;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

