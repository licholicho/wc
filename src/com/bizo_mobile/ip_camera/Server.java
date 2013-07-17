package com.bizo_mobile.ip_camera;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class Server implements Runnable {
	private ServerSocket ss;
	private int port = 8000;
	private ExecutorService executor;
	private int maxThreads = 10;
	private boolean stopped = false;
	static ByteArrayOutputStream photo;
	//public static DataOutputStream serverOut;
//	static byte[] bytes;
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
	
	public void addToOos(byte [] b){
//		Server.bytes = b;
	}
	
	public void addPhoto(ByteArrayOutputStream out) {
		Server.photo = out;
		Log.i("addPhoto","add");
	}

	@Override
	public void run() {
		Log.i("Server", "Started");
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		Log.i("Server", "Started");
		executor = Executors.newFixedThreadPool(maxThreads);
		Log.i("Server", "Executor passed");
		while (!isStopped()) {
			try {
				Socket clientSocket = ss.accept();
				Log.i("Server", "Duck: Server accepted");
				executor.execute(new ThreadHandler(clientSocket));
			} catch (IOException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
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
//	private ObjectOutputStream oos;
	private static final String BOUNDARY = "arflebarfle";

	ThreadHandler(Socket clientSocket) {
		this.client = clientSocket;
	}

	@Override
	public void run() {
		System.out.println("hej");
		Log.i("lece", "");
		try {
			in = new DataInputStream(client.getInputStream());
			/*Server.serverOut = new DataOutputStream(new BufferedOutputStream(
						client.getOutputStream()));*/
			out = new DataOutputStream(new BufferedOutputStream(
					client.getOutputStream()));
			//oos = new ObjectOutputStream(new BufferedOutputStream(
				//	client.getOutputStream()));
			
		} catch (IOException io) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
			return;
		}

		try {
			sendWelcomeMsg();
			int bytesRead;
			byte[] barr = new byte[1024];
			out.writeBytes("Content-type: image/jpg\n\n");
//			String fname = "test.jpeg";
			// DataInputStream fis = new DataInputStream(new
			// BufferedInputStream(new FileInputStream(fname)));
//			DataInputStream fis = new DataInputStream(new BufferedInputStream(
//					this.getClass().getResourceAsStream(fname)));
//			DataInputStream fis = new DataInputStream(in)
//			while ((bytesRead = fis.read(barr)) != -1) {
//				out.write(barr, 0, bytesRead);
//			}
//			fis.close();
			Server.photo.writeTo(out);
			out.writeBytes("--" + BOUNDARY + "\n");
			out.flush();
			//oos.writeObject(Server.bytes);
			//oos.flush();*/
			
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
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
