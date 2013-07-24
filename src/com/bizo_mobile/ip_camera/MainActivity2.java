package com.bizo_mobile.ip_camera;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import trash.WebServerService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity2 extends Activity implements
		CamView.CameraReadyCallback {

	boolean inProc = false;
	int camOrient = 90;
	private int size[] = new int[2];
	private int fps[] = new int[2];
	private String port;
	private String password;
	private CamView cameraView;
	private Button backButton;
	private Button connectButton;
	private OnClickListener backButtonListener;
	private OnClickListener connectButtonListener;
	private Server server;
	private int portNum = 8000;
	private Thread t;
	private ByteArrayOutputStream output_stream;
	
	@Override
	public void onCameraReady() {
		Log.i("oncamera", "ready");

		// if ( initWebServer() ) {
		// int wid = cameraView.getWidth();
		// int hei = cameraView.getHeight();
		// cameraView.stopPreview();
		// cameraView.setupCam(wid, hei, previewCb);
		// cameraView.startPreview();
		// }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		final int orient = extras.getInt("orientation");
		camOrient = orient;
		size = extras.getIntArray("resolution");
		fps = extras.getIntArray("fps");
		port = extras.getString("port");
		password = extras.getString("password");
		if(!port.equals("")){
			portNum = Integer.valueOf(port);
		} else {
			port = "8000";
		}
	
		if (camOrient >= 90)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			
		}
		
		if (password.equals("")) {
			server = new Server(portNum);
		} else {
			server = new Server(portNum, password);
		}
			Log.i("password","pass: "+password);
			t = new Thread(server);
		
	//	server = new Server(8000);
	//**	
	//	 if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { camera.setDisplayOrientation(90); }
		// else
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.main_layout);
		backButton = (Button) findViewById(R.id.backButton);
		connectButton = (Button) findViewById(R.id.howButton);
 
		backButtonListener = new OnClickListener() {
			public void onClick(View view) {
				createNotification();
				appExit();
			}
		};
		backButton.setOnClickListener(backButtonListener);

		final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		String s = getLocalIpAddress();
		alertbox.setMessage("Connect to: " + s + ":" + port); 
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		});
		connectButtonListener = new OnClickListener() {
			public void onClick(View view) {
				alertbox.show();
				if(!t.isAlive()) t.start();
				
			}
		};
		connectButton.setOnClickListener(connectButtonListener);
		initCamera();
		startService(new Intent(this, WebServerService.class));
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("DESTROY", "ON DESTROY");
		if (cameraView != null) {
			cameraView.stopPreview();
			cameraView.release();

		}

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("PAUSE", "ON PAUSE");
		inProc = true;
		// if ( webServer != null)
		// webServer.stop();
		// cameraView.stopPreview(); *
		// cameraView_.Release();
		// cameraView.release();
		// System.exit(0);
		// finish();
	}

	private void initCamera() {
		SurfaceView cameraSurface = (SurfaceView) findViewById(R.id.surview);
		cameraView = new CamView(cameraSurface, camOrient, fps, size, previewCb);
		if (cameraView != null)
		cameraView.setCameraReadyCallback(this);
		
		// previewFormat = cameraView.getPreviewFormat();
		// cameraView.stopPreview();
		// cameraView.startPreview();
	}

	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			return "ERROR Obtaining IP";
		}
		return "No IP Available";
	}

	private PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] frame, Camera c) {
			if (!inProc) {
				inProc = true;
				int picWidth = size[0];// cameraView.getWidth();
				int picHeight = size[1];// cameraView.getHeight();
				YuvImage image = new YuvImage(frame,
						cameraView.getPreviewFormat(), picWidth, picHeight,
						null);
				output_stream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, picWidth, picHeight), 90,
						output_stream);
				server.addPhoto(output_stream);
				inProc = false;
			}
		}
	};

	
	public void createNotification() {
		Log.i("notification","notification");
	    Intent intent = new Intent(this, SettingsActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

	    Notification notification = new NotificationCompat.Builder(this)
	        .setContentTitle("Camera IP")
	        .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .addAction(R.drawable.ic_launcher, "Call", pIntent)
	        .addAction(R.drawable.ic_launcher, "More", pIntent)
	        .addAction(R.drawable.ic_launcher, "And more", pIntent).build();
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    notificationManager.notify(0, notification);
	  }
	
	public void appExit() {
		Log.i("app","exit");
	    this.finish();
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	}
	
	
/*	public boolean checkPort(String s){
		return s.matches("[");
	}*/
}
