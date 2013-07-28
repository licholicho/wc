package com.bizo_mobile.server.thread;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ImageContainer implements IImageContainer {
	private BlockingQueue<ByteArrayOutputStream> photos;
	private static final int MAX_SIZE = 5;
	private int currentSize = 0;
	
	public ImageContainer() {
		photos = new LinkedBlockingQueue<ByteArrayOutputStream>();
	}
	
	public void addPhoto(ByteArrayOutputStream out) {		
		if(isFull()) {
			photos.remove();
			currentSize--;
		}
		photos.add(out);
		currentSize++;
	}
	
	public boolean isFull() {
		return currentSize >= MAX_SIZE  ? true : false;
	}
	
	public ByteArrayOutputStream getPhoto() {
		return photos.peek();
	}	
}