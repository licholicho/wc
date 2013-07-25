package com.bizo_mobile.server.thread;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.bizo_mobile.server.IServer;

public class ThreadServer implements Runnable {
	private ServerSocket ss;
	private int port = 8000;
	private ExecutorService executor;
	private int maxThreads = 10;
	private boolean stopped = false;
	private String password;
	private ImageContainer imageContainer;

	// public static void main(String args[]) throws InterruptedException {
	// Thread serverThread = new Thread(new Server());
	// serverThread.start();
	// serverThread.join();
	// }
	public ThreadServer(ImageContainer imageContainer, int port, String password) {
		this.imageContainer = imageContainer;
	}

	public ImageContainer getImageContainer() {
		return imageContainer;
	}

	public void setImageContainer(ImageContainer imageContainer) {
		this.imageContainer = imageContainer;
	}

	public ThreadServer(int port) {
		this.port = port;
		this.password = "";
	}

//	public ThreadServer(int port, String password) {
//		this.port = port;
//		this.password = password;
//	}

	@Override
	public void run() {
		final int TIME_TO_FULL = 100;
		while (!imageContainer.isFull()) {
			try {
				Thread.sleep(TIME_TO_FULL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(ThreadServer.class.getName(), "Server started on port " + port + " on ip: " + " ");
		executor = Executors.newFixedThreadPool(maxThreads);
		while (!isStopped()) {
			try {
				Socket clientSocket = ss.accept();
				executor.execute(new ThreadHandler(clientSocket, this));
				Log.i(ThreadServer.class.getName(), "Client connected from: " + clientSocket.getInetAddress().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isStopped() {
		return stopped;
	}
}

class ThreadHandler implements Runnable {
	private Socket client;
	private DataInputStream in;
	private DataOutputStream out;
	private static final String BOUNDARY = "arflebarfle";
	private static final int TIME_TO_NEXT = 25;
	private ThreadServer server;

	ThreadHandler(Socket clientSocket, ThreadServer server) {
		this.client = clientSocket;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(new BufferedOutputStream(
					client.getOutputStream()));
		} catch (IOException io) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		try {
			sendWelcomeMsg();
			while (true) {
				out.writeBytes("Content-type: image/jpg\n\n");
				server.getImageContainer().getPhoto().writeTo(out);
				out.writeBytes("--" + BOUNDARY + "\n");
				out.flush();
				try {
					Thread.sleep(TIME_TO_NEXT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendWelcomeMsg() throws IOException {
		out.writeBytes("HTTP/1.0 200 OK\r\n");
		out.writeBytes("com.bizo_mobile.ip_camera.Server: Elwira test server\r\n");
		out.writeBytes("Content-Type: multipart/x-mixed-replace;boundary="
				+ BOUNDARY + "\r\n");
		out.writeBytes("\r\n");
		out.writeBytes("--" + BOUNDARY + "\n");
	}

}
