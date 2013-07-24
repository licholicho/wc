package com.bizo_mobile.ip_camera;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoopServer {

	private List<Socket> clients = new ArrayList<Socket>();
	private int maxThreads;
	
	static ConcurrentLinkedQueue<ByteArrayOutputStream> photos = new ConcurrentLinkedQueue<ByteArrayOutputStream>();
	
}
