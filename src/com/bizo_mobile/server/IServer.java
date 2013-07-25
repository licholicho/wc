package com.bizo_mobile.server;

import java.io.ByteArrayOutputStream;

public interface IServer {
	void addPhoto(ByteArrayOutputStream out);

	public abstract ByteArrayOutputStream getNextPhoto();
	
}
