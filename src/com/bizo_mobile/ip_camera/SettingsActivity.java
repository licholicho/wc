package com.bizo_mobile.ip_camera;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnItemSelectedListener {

	private Camera camera = null;
	private Button webButton;
	private OnClickListener webListener;
	private TextView selectedOrientation;
	private int orientation;
	private static final String[] orientationOptions = {"Portrait","Landscape"};	
	private List<Camera.Size> supportedSizes;
	private List<String> resolutionOptions = new ArrayList<String>();
	private int[] selectedSize = new int[2];
	private List<int[]> fpsRange;
	private List<String> fpsOptions = new ArrayList<String>();
	private int[] selectedFps = new int[2];
	//private WebServerService s;
//	final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	private EditText port;
	private String portNumber;
	
	private EditText password;
	private String pass;
	private int min, max;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.settings_layout);
        initialize();
        port = (EditText)findViewById(R.id.portsel);
        password = (EditText)findViewById(R.id.pass);
      
        port.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				port.setHint("Enter 3-6 digits");
			}
        	});
        
        password.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				password.setHint("Leave blank if anyone can access");
			}
        	});
        
     /*   alertbox.setMessage("Wrong port!"); 
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});*/
        
        webButton = (Button) findViewById(R.id.web_button);
        webListener = new OnClickListener() {
		    public void onClick(View view) {
		    	 portNumber = port.getText().toString();
			   	 pass = password.getText().toString();
		    	Intent intent = new Intent();
		    	intent.setClass(view.getContext(),MainActivity2.class);
		    	intent.putExtra("orientation", orientation);
		    	intent.putExtra("resolution", selectedSize);
		    	intent.putExtra("fps",selectedFps);
		    	intent.putExtra("port", portNumber);
		    	intent.putExtra("password", pass);
		    	startActivity(intent);
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
        	    	if(position==0) orientation = 90;
        	    	else orientation = 0;
        	    }

        	    @Override
        	    public void onNothingSelected(AdapterView<?> parentView) {
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
    	    	 	selectedFps[0] = fpsRange.get(position)[min];
    	    	 	selectedFps[1] = fpsRange.get(position)[max];
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
		/* if (camera != null){
			 Log.i("camera","!= null");
			 camera.release();
			 camera = null;
		 }*/
		 if(camera == null)  {
			 camera = Camera.open();
		 }
		 min = Camera.Parameters.PREVIEW_FPS_MIN_INDEX;
		 max = Camera.Parameters.PREVIEW_FPS_MAX_INDEX;
		 supportedSizes = camera.getParameters().getSupportedPreviewSizes();
	     fpsRange = camera.getParameters().getSupportedPreviewFpsRange();
		Camera.Size result = null;
		String res = null;
	    for (int i=0;i<supportedSizes.size();i++){
	        result = (Camera.Size) supportedSizes.get(i);
	        res = result.width + " x " + result.height;
	        resolutionOptions.add(res);
	    }
	    for (int j=0;j<fpsRange.size();j++){
	    	//fpsOptions.add(fpsRange.get(j)[min]/1000+"-"+fpsRange.get(j)[max]/1000);
	    	String s = fpsRange.get(j)[max]/1000+"";
	    	if (!fpsOptions.contains(s)) {
	    		fpsOptions.add(s);
	    	}

	    	
	    }
	    if(camera != null) {
	    	camera.release();
	    	Log.i("initialize","released");
	    	camera = null;
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
		
		@Override
	    protected void onPause() {
			  super.onPause();
			  Log.i("PAUSE S","PAUSE S");
			    if (camera!=null)
			    {
			        camera.stopPreview();
			        camera.release();
			        camera = null;
			    }
		}

		@Override
	    protected void onDestroy() {
			  super.onDestroy();
			    if (camera!=null)
			    {
			        camera.stopPreview();
			        camera.release();
			        camera=null;
			    }
		}

			  

	  
	  
    }
	

