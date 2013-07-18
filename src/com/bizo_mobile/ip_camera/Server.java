package com.bizo_mobile.ip_camera;

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

public class Server implements Runnable {
	private ServerSocket ss;
	private int port = 8000;
	private ExecutorService executor;
	private int maxThreads = 10;
	private boolean stopped = false;
	static ConcurrentLinkedQueue<ByteArrayOutputStream> photos = new ConcurrentLinkedQueue<ByteArrayOutputStream>();

	// public static void main(String args[]) throws InterruptedException {
	// Thread serverThread = new Thread(new Server());
	// serverThread.start();
	// serverThread.join();
	// }

	public Server(ByteArrayOutputStream out) {
		addPhoto(out);
	}

	public Server() {

	}

	public void addPhoto(ByteArrayOutputStream out) {
		photos.add(out);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		executor = Executors.newFixedThreadPool(maxThreads);
		while (!isStopped()) {
			try {
				Socket clientSocket = ss.accept();
				executor.execute(new ThreadHandler(clientSocket));
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
	private Iterator<ByteArrayOutputStream> iterator = Server.photos.iterator();

	ThreadHandler(Socket clientSocket) {
		this.client = clientSocket;
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
				while (iterator.hasNext()) {
					out.writeBytes("Content-type: image/jpg\n\n");
					iterator.next().writeTo(out);
					iterator.remove();
					out.writeBytes("--" + BOUNDARY + "\n");
					out.flush();
				}
				iterator = Server.photos.iterator();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.i("protos", "size" + Server.photos.size());
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
