package com.bizo_mobile.server.thread;

import java.io.ByteArrayOutputStream;

public interface IImageContainer {
	boolean isFull();
	ByteArrayOutputStream getPhoto();
}
