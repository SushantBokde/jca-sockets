/*
 * Copyright 2009 Mark Jeffrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jcasockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class RemoteSocketSender implements SocketSender, SocketSenderFactory {

	public RemoteSocketSender() {
	}

	private Socket socket;
	private Integer port;
	private String ipAddress;
	private SocketAddress socketAddress;

	public RemoteSocketSender(String ipAddress, Integer port) {
		this.ipAddress = ipAddress;
		this.port = port;

	}

	@Override
	public String send(String sendMessage) {
		socket = new Socket();
		socketAddress = new InetSocketAddress(ipAddress, port);
		StringBuilder sb = new StringBuilder(sendMessage.length());
		OutputStream outputStream;
		int timeoutMs = 0;
		try {
			socket.connect(socketAddress, timeoutMs);
			outputStream = socket.getOutputStream();
			outputStream.write(sendMessage.getBytes());
			outputStream.flush();
			socket.shutdownOutput();

			final InputStream inputStream = socket.getInputStream();
			final BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

			String str;
			while ((str = rd.readLine()) != null) {
				sb.append(str);
			}
			rd.close();
		} catch (IOException e) {
			throw new RuntimeException("Exception while sending: " + ipAddress + ":" + port, e);
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException("Exception while closing: " + ipAddress + ":" + port, e);
			}
		}
		
		return sb.toString();
	}

	@Override
	public SocketSender createSocketSender(String ipAddress, Integer port) {
		return new RemoteSocketSender(ipAddress, port);
	}

}