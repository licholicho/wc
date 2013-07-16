package com.bizo_mobile.ip_camera;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity2 extends Activity implements CamView.CameraReadyCallback {

	boolean inProc = false;
	final int maxVideoNum = 3;
	VideoFrame[] videoFrames = new VideoFrame[maxVideoNum];
	byte[] preFrame = new byte[1024*1024*8];
	int camOrient = 90;
	private int size[] = new int[2];
	private int fps[] = new int[2];
	String port;
	MyServer webServer;
	private CamView cameraView;
	private Button backButton;
	private Button connectButton;
	private OnClickListener backButtonListener;
	private OnClickListener connectButtonListener;
	
	 @Override
	    public void onCameraReady() {
		 Log.i("oncamera","ready");
	        if ( initWebServer() ) {
	            int wid = cameraView.getWidth();
	            int hei = cameraView.getHeight();
	            cameraView.stopPreview();
	            cameraView.setupCam(wid, hei, previewCb);
	            cameraView.startPreview();
	        }
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
	  // 	else 
	    //		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

	    	setContentView(R.layout.main_layout);
	    	backButton = (Button)findViewById(R.id.backButton);
	    	connectButton = (Button)findViewById(R.id.howButton);
	    	backButtonListener = new OnClickListener() {
			    public void onClick(View view) {
			    	// run in background
			    }
	        };
	        backButton.setOnClickListener(backButtonListener);
	        
	        final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        String s = getLocalIpAddress();
	        // set the message to display
	        alertbox.setMessage("Connect to: "+s+":"+port); // roboczos
	         
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

	    		    	//initWebServer();
	    }

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    
    @Override
    public void onPause(){
        super.onPause();
        inProc = true;
        if ( webServer != null)
            webServer.stop();
        cameraView.stopPreview();
        //cameraView_.Release();
 
    
        //System.exit(0);
        finish();
    }

	private void initCamera(){
		SurfaceView cameraSurface = (SurfaceView)findViewById(R.id.surview);
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
	  
	  private boolean initWebServer() {
	        String ipAddr = getLocalIpAddress();
	        Log.i("ip address", ipAddr);
	        Log.i("start","start");
	        if ( ipAddr != null ) {
	            try{
	                webServer = new MyServer(8080, this);
	                webServer.registerCGI("/cgi/query", doQuery);
	                webServer.registerCGI("/cgi/setup", doSetup);
	                webServer.registerCGI("/stream/live.jpg", doCapture);
	              //  webServer.registerCGI("/stream/live.mp3", doBroadcast);
	            }catch (IOException e){
	                webServer = null;
	            }
	        }
	        if ( webServer != null) {
	        	Log.i("web","not null");
	         //   tv1.setText( getString(R.string.msg_access_local) + " http://" + ipAddr + ":8080" );
	            //tvMessage2.setText( getString(R.string.msg_access_query));
	            //tvMessage2.setVisibility(View.VISIBLE);
	     //*****      NatPMPClient natQuery = new NatPMPClient();
	     //*****      natQuery.start();
	            return true;
	        } else {
	        //    tv1.setText( getString(R.string.msg_error) );
	         //   tv2.setVisibility(View.GONE);
	            return false;
	        }
	          
	    }

	  private PreviewCallback previewCb = new PreviewCallback() {
	        public void onPreviewFrame(byte[] frame, Camera c) {
	            if ( !inProc ) {
	                inProc = true;
	           
	                int picWidth = cameraView.getWidth();
	                int picHeight = cameraView.getHeight();
	                ByteBuffer bbuffer = ByteBuffer.wrap(frame);
	                bbuffer.get(preFrame, 0, picWidth*picHeight + picWidth*picHeight/2);

	                inProc = false;
	            }
	        }
	    };
	
	    private MyServer.CommonGatewayInterface doQuery = new MyServer.CommonGatewayInterface () {
	        @Override
	        public String run(Properties parms) {
	            String ret = "";
	            List<Camera.Size> supportSize = cameraView.getSupportedPreviewSize();
	            ret = ret + "" + cameraView.getWidth() + "x" + cameraView.getHeight() + "|";
	            for(int i = 0; i < supportSize.size() - 1; i++) {
	                ret = ret + "" + supportSize.get(i).width + "x" + supportSize.get(i).height + "|";
	            }
	            int i = supportSize.size() - 1;
	            ret = ret + "" + supportSize.get(i).width + "x" + supportSize.get(i).height ;
	            return ret;
	        }
	        
	        @Override
	        public InputStream streaming(Properties parms) {
	            return null;
	        }
	    };

	    private MyServer.CommonGatewayInterface doSetup = new MyServer.CommonGatewayInterface () {
	        @Override
	        public String run(Properties parms) {
	            int wid = Integer.parseInt(parms.getProperty("wid"));
	            int hei = Integer.parseInt(parms.getProperty("hei"));
	            Log.d("TEAONLY", ">>>>>>>run in doSetup wid = " + wid + " hei=" + hei);
	            cameraView.stopPreview();
	            cameraView.setupCam(wid, hei, previewCb);
	            cameraView.startPreview();
	            return "OK";
	        }
																												
	        @Override
	        public InputStream streaming(Properties parms) {
	            return null;
	        }
	    };


	    private MyServer.CommonGatewayInterface doCapture = new MyServer.CommonGatewayInterface () {
	        @Override
	        public String run(Properties parms) {
	           return null;
	        }
	        
	        @Override
	        public InputStream streaming(Properties parms) {
	            VideoFrame targetFrame = null;
	            for(int i = 0; i < maxVideoNum; i++) {
	                if ( videoFrames[i].acquire() ) {
	                    targetFrame = videoFrames[i];
	                    break;
	                }
	            }
	            // return 503 internal error
	            if ( targetFrame == null) {
	                Log.d("TEAONLY", "No free videoFrame found!");
	                return null;
	            }

	            // compress yuv to jpeg
	            int picWidth = cameraView.getWidth();
	            int picHeight = cameraView.getHeight();
	            YuvImage newImage = new YuvImage(preFrame, ImageFormat.NV21, picWidth, picHeight, null);
	            targetFrame.reset();
	            boolean ret;
	            inProc = true;
	            try{
	                ret = newImage.compressToJpeg( new Rect(0,0,picWidth,picHeight), 30, targetFrame);
	            } catch (Exception ex) {
	                ret = false;
	            }
	            inProc = false;

	            // compress success, return ok
	            if ( ret == true) {
	                parms.setProperty("mime", "image/jpeg");
	                InputStream ins = targetFrame.getInputStream();
	                return ins;
	            }
	            // send 503 error
	            targetFrame.release();

	            return null;
	        }
	        
	    };
	    
	    static private native String nativeQueryInternet();
	    private class NatPMPClient extends Thread {
	        String queryResult;
	        Handler handleQueryResult = new Handler(getMainLooper());
	    
	        @Override
            public void run(){
	            queryResult = nativeQueryInternet();
	            if ( queryResult.startsWith("error:") ) {
	                handleQueryResult.post( new Runnable() {
	                    @Override
	                    public void run() {
	                    	Log.i("error","aaaaaa");
	                       // tv2.setText( getString(R.string.msg_access_query_error));
	                    }
	                });
	            } else {
	                handleQueryResult.post( new Runnable() {
	                    @Override
	                    public void run() {
	                    	Log.i("xD",queryResult);
	              //         tv2.setText( getString(R.string.msg_access_internet) + " " + queryResult );
	                    }
	                });
	            }
	            }
	    }
	            

	    }
	    


