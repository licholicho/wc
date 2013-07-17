package com.bizo_mobile.ip_camera;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import trash.WebServerService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnItemSelectedListener {

	private Camera camera;
		//private Button startButton;
	//private OnClickListener startListener;
	private Button webButton;
	private OnClickListener webListener;
	private TextView selectedOrientation;
	private int orientation;
	private static final String[] orientationOptions = {"portrait","landscape"};	
	private List<Camera.Size> supportedSizes;
	private List<String> resolutionOptions = new ArrayList<String>();
	private int[] selectedSize = new int[2];
	private List<int[]> fpsRange;
	private List<String> fpsOptions = new ArrayList<String>();
	private int[] selectedFps = new int[2];
	private WebServerService s;
	private EditText port;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (camera == null) camera = Camera.open();
        supportedSizes = camera.getParameters().getSupportedPreviewSizes();
        fpsRange = camera.getParameters().getSupportedPreviewFpsRange();
        setContentView(R.layout.settings_layout);
        initialize();
        
        port = (EditText)findViewById(R.id.port);
        final String portNumber = port.getText().toString();
       /* final ServiceConnection mConnection = new ServiceConnection() {

            public void onServiceConnected(ComponentName className, IBinder binder) {
              s = ((WebServerService.MyBinder) binder).getService();
              Toast.makeText(SettingsActivity.this, "Connected", Toast.LENGTH_SHORT)
                  .show();
            }

            public void onServiceDisconnected(ComponentName className) {
              s = null;
            }
          };*/
        webButton = (Button) findViewById(R.id.web_button);
        webListener = new OnClickListener() {
		    public void onClick(View view) {
		    
		    	if(camera != null)
		    	camera.release();
		  	Intent intent = new Intent();
		    	intent.setClass(view.getContext(),MainActivity2.class);
		    	intent.putExtra("orientation", orientation);
		    	intent.putExtra("resolution", selectedSize);
		    	intent.putExtra("fps",selectedFps);
		    	intent.putExtra("port", portNumber);
		    	startActivity(intent);
		      String s = getLocalIpAddress();
		        Log.i("IP",s);

		       /* Thread t = new Thread(){
		        	public void run(){
		        		Log.i("run","");
		        		// startService(new Intent(getApplicationContext(),WebServerService.class));
		        		getApplicationContext().bindService(
		        	        new Intent(getApplicationContext(), WebServerService.class),
		        	        mConnection,
		        	        Context.BIND_AUTO_CREATE
		        	    );
		        	}
		        	};*/
		     /*   
		       Thread t = new Thread(new Runnable()
		        {
		        @Override
		        public void run()
		        {
		        	startService(new Intent(getApplicationContext(),WebServerService.class)); 
		        }
		        });
		        	t.start();
		   */
		    }
        };
        webButton.setOnClickListener(webListener);
        
        selectedOrientation=(TextView)findViewById(R.id.orientation);
       
  
        Spinner orientSpin=(Spinner)findViewById(R.id.orientation_spinner);
        orientSpin.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
        	    @Override
        	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
        	    {
        	    	selectedOrientation.setText(orientationOptions[position]);
        	    	if(position==0) orientation = 90;
        	    	else orientation = 0;
        	    }

        	    @Override
        	    public void onNothingSelected(AdapterView<?> parentView) {
        	    	selectedOrientation.setText("");
        	    }
        	});
        ArrayAdapter aa=new ArrayAdapter(this, android.R.layout.simple_spinner_item,orientationOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orientSpin.setAdapter(aa);
      
        /****************************/
        
        Spinner fpsSpin=(Spinner)findViewById(R.id.fps_spinner);
        fpsSpin.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {
    	    	 	selectedFps[0] = fpsRange.get(position)[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
    	    	 	selectedFps[1] = fpsRange.get(position)[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
    	    	 	Log.i("fff",selectedFps[0]+"-"+selectedFps[1]);
    	 // todo
    	    }

    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	    //
    	    }
    	});
        ArrayAdapter aa3=new ArrayAdapter(this, android.R.layout.simple_spinner_item,fpsOptions);
        aa3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fpsSpin.setAdapter(aa3);
        
        /************************************/
        
        Spinner resSpin=(Spinner)findViewById(R.id.resolution_spinner);
        resSpin.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
    	    {
    	    	Camera.Size result = (Camera.Size) supportedSizes.get(position);
    	    	selectedSize[0] = result.width;
    	    	selectedSize[1] = result.height;
    	    }

    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	    	selectedOrientation.setText("");
    	    }
    	});
        ArrayAdapter aa2=new ArrayAdapter(this, android.R.layout.simple_spinner_item,resolutionOptions);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resSpin.setAdapter(aa2);
        
    
        
    } 

	public void initialize(){
		Camera.Size result = null;
		String res = null;
	    for (int i=0;i<supportedSizes.size();i++){
	        result = (Camera.Size) supportedSizes.get(i);
	        res = result.width + " x " + result.height;
	        resolutionOptions.add(res);
	    }
	    for (int j=0;j<fpsRange.size();j++){
	    	fpsOptions.add(fpsRange.get(j)[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]/1000
	    			+"-"+fpsRange.get(j)[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]/1000);
	    	 Log.i("fps", fpsOptions.get(j));
	    }
	}


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
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
	
	  
	  
	
		  
		  

	  
	  
    }
	

