package com.bizo_mobile.server.thread;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class ThreadServer implements Runnable {
	private ServerSocket ss;
	private int port = 8000;
	private ExecutorService executor;
	private int maxThreads = 10;
	private boolean stopped = false;
	private String password;
	private IImageContainer imageContainer;

	public static void main(String args[]) throws InterruptedException {
		Thread serverThread = new Thread(new ThreadServer(
				new ImageContainerMock()));
		serverThread.start();
		serverThread.join();
	}

	public ThreadServer(IImageContainer imageContainer, int port,
			String password) {
		this.imageContainer = imageContainer;
	}

	public ThreadServer(IImageContainer imageContainer) {
		this.imageContainer = imageContainer;
	}

	public IImageContainer getImageContainer() {
		return imageContainer;
	}

	public void setImageContainer(IImageContainer imageContainer) {
		this.imageContainer = imageContainer;
	}

	public ThreadServer(int port) {
		this.port = port;
		this.password = "";
	}

	@Override
	public void run() {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(ThreadServer.class.getName(), "Server started on port " + port
				+ " on ip: " + " ");
		executor = Executors.newFixedThreadPool(maxThreads);
		while (!isStopped()) {
			try {
				Socket clientSocket = ss.accept();
				executor.execute(new ThreadHandler(clientSocket, this));
				Log.i(ThreadServer.class.getName(), "Client connected from: "
						+ clientSocket.getInetAddress().toString());
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
			sendWelcomeMsg();
			sendPhotos();
		} catch (IOException e) {
			try {
				client.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
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

	private void sendPhotos() throws IOException {
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
	}

	private void askForPasssword() throws IOException {
		final String ServerName = "MJPEGoHTTP-SERVER";
		out.writeBytes("HTTP/1.1 401 Access Denied");
		out.writeBytes("WWW-Authenticate: Basic realm=" + ServerName);
		out.writeBytes("Content-Length: 0");
	}

}
