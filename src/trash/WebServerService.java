package trash;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.bizo_mobile.ip_camera.Server;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WebServerService extends Service {

	//private WebServer server = null;
	private Server server;
	private Thread t;
	
	@Override
	public void onCreate() {
		Log.i("HTTPSERVICE", "Creating and starting httpService");
		
		super.onCreate();
	//	server = new WebServer(this);
		//server.startServer();
		
		server = new Server();
		t = new Thread(server);
		t.start();
	//	server.run();
	
	}

	@Override
	public void onDestroy() {
		Log.i("HTTPSERVICE", "Destroying httpService");
	//	server.stopServer();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	 public class MyBinder extends Binder {
		    WebServerService getService() {
		      return WebServerService.this;
		    }
		  }
	
	 @Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 Log.i("service","sss");
	   //  handleCommand(intent);
	     // We want this service to continue running until it is explicitly
	     // stopped, so return sticky.
	     return START_STICKY;
	 }
	
}
