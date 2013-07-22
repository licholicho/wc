package trash;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bizo_mobile.ip_camera.Server;

public class WebServerService extends Service {

	private Server server;
	private Thread t;
	
	@Override
	public void onCreate() {
		Log.i("HTTPSERVICE", "Creating and starting httpService");
		
		super.onCreate();
		/*server = new Server();
		t = new Thread(server);
		t.start();*/
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
	     return START_STICKY;
	 }
	
}
