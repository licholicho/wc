package com.bizo_mobile.ip_camera;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity2 extends Activity implements
		CamView.CameraReadyCallback {

	boolean inProc = false;
	final int maxVideoNum = 3;
	// VideoFrame[] videoFrames = new VideoFrame[maxVideoNum];
	byte[] preFrame = new byte[1024 * 1024 * 8];
	int camOrient = 90;
	private int size[] = new int[2];
	private int fps[] = new int[2];
	String port;
	private CamView cameraView;
	private Button backButton;
	private Button connectButton;
	private OnClickListener backButtonListener;
	private OnClickListener connectButtonListener;
	private Thread imageServerThread;

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

		if (camOrient >= 90)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// else
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.main_layout);
		backButton = (Button) findViewById(R.id.backButton);
		connectButton = (Button) findViewById(R.id.howButton);
		backButtonListener = new OnClickListener() {
			public void onClick(View view) {
				// run in background
			}
		};
		backButton.setOnClickListener(backButtonListener);

		final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		String s = getLocalIpAddress();
		// set the message to display
		alertbox.setMessage("Connect to: " + s + ":" + port); // roboczos

		// add a neutral button to the alert box and assign a click listener
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

			// click listener on the alert box
			public void onClick(DialogInterface arg0, int arg1) {
				// the button was clicked

			}
		});
		connectButtonListener = new OnClickListener() {
			public void onClick(View view) {
				alertbox.show();
			}
		};
		connectButton.setOnClickListener(connectButtonListener);
		initCamera();
		Log.i("Activity2", "prepare for start of server");
		this.imageServerThread=new Thread(new Server());
		imageServerThread.start();
		
		//Log.i("Activity2", imageServerThread.getState().toString());
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		inProc = true;
		// if ( webServer != null)
		// webServer.stop();
		cameraView.stopPreview();
		// cameraView_.Release();

		// System.exit(0);
		finish();
	}

	private void initCamera() {
		SurfaceView cameraSurface = (SurfaceView) findViewById(R.id.surview);
		cameraView = new CamView(cameraSurface, camOrient, fps, size);
		cameraView.setCameraReadyCallback(this);
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

				int picWidth = cameraView.getWidth();
				int picHeight = cameraView.getHeight();
				ByteBuffer bbuffer = ByteBuffer.wrap(frame);
				bbuffer.get(preFrame, 0, picWidth * picHeight + picWidth
						* picHeight / 2);

				inProc = false;
			}
		}
	};

}
