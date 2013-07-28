package com.bizo_mobile.server.thread;

import java.io.ByteArrayOutputStream;

public class ImageContainerMock implements IImageContainer {
	
	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public ByteArrayOutputStream getPhoto() {
		return null;
	}
	

	
}
