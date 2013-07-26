package com.bizo_mobile.ip_camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoopServer implements Runnable {

	private ServerSocket serverSocket;
	private List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
	private int maxThreads = 5;
	private BlockingQueue<Connection> pool = new ArrayBlockingQueue<Connection>(maxThreads);
	static ConcurrentLinkedQueue<ByteArrayOutputStream> photos = new ConcurrentLinkedQueue<ByteArrayOutputStream>();
	private String password;
	private int port = 8000;
	private ExecutorService executor;
	private boolean stopped = false;
	private AtomicInteger threadCount = new AtomicInteger();

	/*public Connection getConnection() {
	    Connection conn = pool.poll(5, TimeUnit.SECONDS);
	    if (conn == null) {
	        synchronized (threadCount) {
	            if (threadCount.get() < 10) {
	          //      conn = getNewConnection();
	                pool.offer(conn);
	                threadCount.incrementAndGet();
	            }
	        }
	        if (conn == null) {
	            throw new ConnUnavailException();
	        } else {
	            return conn;
	        }
	    }
	}*/
	
	public LoopServer(int port){
		this.port = port;
		this.password = "";
	}
	
	public LoopServer(int port, String password){
		this.port = port;
		this.password = password;		
	}
	

	public void addPhoto(ByteArrayOutputStream out) {
		photos.add(out);
		try {
			Thread.sleep(50);
			photos.remove(out);//*****//
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		executor = Executors.newFixedThreadPool(maxThreads);
		while (!isStopped()) {
			try {
				if (!password.equals("")){
					
				}
				// popros o haslo
				Socket clientSocket = serverSocket.accept();
				executor.execute(new ThreadHandler(clientSocket));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	private boolean isStopped(){
		return stopped;
	}
	
	public void stop(){
		/*
		 *  public void Stop()
        {

            if (this.IsRunning)
            {
                try
                {
                    _Thread.Join();
                    _Thread.Abort();
                }
                finally
                {

                    lock (_Clients)
                    {
                        
                        foreach (var s in _Clients)
                        {
                            try
                            {
                                s.Close();
                            }
                            catch { }
                        }
                        _Clients.Clear();

                    }

                    _Thread = null;
                }
            }
        }
		 * */
		
	}
	
	
	class ClientHandler implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
