package com.bizo_mobile.server.loop;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bizo_mobile.server.IServer;

import android.util.Log;

public class LoopServer implements IServer, Runnable {
	private int port = 8000;
	private int capacity;
	private boolean stopped = false;
	private String password;
	
	// moze wyleciec
	private ServerSocket ss;
	private ConcurrentLinkedQueue<ByteArrayOutputStream> photos = new ConcurrentLinkedQueue<ByteArrayOutputStream>();
	private Iterator<ByteArrayOutputStream> photoIterator;

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public void run() {
		LoopThreadHandler handler = new LoopThreadHandler(this);
		Thread hanlderThread = new Thread(handler);
		hanlderThread.setPriority(Thread.MAX_PRIORITY);
		hanlderThread.start();
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (!isStopped()) {
			try {
				Socket socket = ss.accept();
				handler.addSocket(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isStopped() {
		return stopped;
	}

	@Override
	public void addPhoto(ByteArrayOutputStream out) {
		photos.add(out);
	}

	@Override
	public ByteArrayOutputStream getNextPhoto() {
		photoIterator = photos.iterator();
		ByteArrayOutputStream photo = photoIterator.next();
		photoIterator.remove();
		return photo;
	}

	public boolean hasNextPhoto() {
		photoIterator = photos.iterator();
		return photoIterator.hasNext();
	}

}

class LoopThreadHandler implements Runnable {
	// private ConcurrentLinkedQueue<Socket> sockets;
	private ConcurrentLinkedQueue<DataOutputStream> outs;
	private LoopServer server;
	private static final String BOUNDARY = "arflebarfle";

	LoopThreadHandler(LoopServer loopServer) {
		server = loopServer;
		outs = new ConcurrentLinkedQueue<DataOutputStream>();
	}

	public void addSocket(Socket socket) throws IOException {
		// sockets.add(socket);
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
		outs.add(out);
		// sendWelcomeMsg(out);
		sendWelcomeMsg(out);
		Log.i("vgkl", "Vegeta: Socket added");
	}

	@Override
	public void run() {
		while (true) {
			if (server.hasNextPhoto()) {
				ByteArrayOutputStream nextPhoto = server.getNextPhoto();
				for (DataOutputStream out : outs) {
					Log.i("vgkl", "Vegeta: photo send");
					try {
						sendPhoto(out, nextPhoto);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void sendWelcomeMsg(DataOutputStream out) throws IOException {
		out.writeBytes("HTTP/1.0 200 OK\r\n");
		out.writeBytes("com.bizo_mobile.ip_camera.Server: Elwira test server\r\n");
		out.writeBytes("Content-Type: multipart/x-mixed-replace;boundary="
				+ BOUNDARY + "\r\n");
		out.writeBytes("\r\n");
		out.writeBytes("--" + BOUNDARY + "\n");
		out.flush();

	}

	private void sendTestMsg(DataOutputStream out) throws IOException {
		out.writeBytes("HTTP/1.0 200 OK\r\n");
		out.writeBytes("Lalala");
		out.flush();
	}

	private void sendPhoto(DataOutputStream out, ByteArrayOutputStream nextPhoto)
			throws IOException {
		out.writeBytes("Content-type: image/jpg\n\n");
		nextPhoto.writeTo(out);
		out.writeBytes("--" + BOUNDARY + "\n");
		out.flush();
	}

}
