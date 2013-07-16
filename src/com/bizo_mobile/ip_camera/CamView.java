package com.bizo_mobile.ip_camera;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamView implements SurfaceHolder.Callback {
	
	public static interface CameraReadyCallback {
		public void onCameraReady();

		void onCreate();
	}	

	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	CameraReadyCallback camReadyCback;
	private int orientation;
	private int fps[];
	private int size[];
	
	private List<Camera.Size> supportedSizes;
	private Camera.Size procSize;
	private boolean inProc = false;
	
	public CamView(SurfaceView sv, int orient) {
		surfaceView = sv;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		orientation = orient;
	}
	
	public CamView(SurfaceView sv, int orient, int[] fps, int[] size) {
		surfaceView = sv;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		orientation = orient;
		this.fps = fps;
		this.size = size;
	}
	
	public List<Camera.Size> getSupportedPreviewSize () {
		return supportedSizes;
	}
	
	public int getWidth() {
		return procSize.width;
	}
	

	public int getHeight() {
		return procSize.height;
	}
	
	public void setCameraReadyCallback(CameraReadyCallback cb) {
		camReadyCback = cb;
	}
	
	public void startPreview(){
		if (camera == null)  return;
		camera.startPreview();
	}
	
	public void stopPreview(){
		if (camera == null)  return;
		camera.stopPreview();
	}
	
	public void release() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}
	
	public void setupCam(int wid, int hei, PreviewCallback pvcb) {
		procSize.width = wid;
		procSize.height = hei;
		
		Camera.Parameters p = camera.getParameters();
		p.setPreviewSize(procSize.width, procSize.height);
		camera.setParameters(p);
		camera.setPreviewCallback(pvcb);
	}
	
	public void setupCam(int[] size, int[] fps,  PreviewCallback pvcb) {
		camera = Camera.open();
		procSize = camera.new Size(0,0);
		procSize.width = size[0];
		procSize.height = size[1];
	
		Camera.Parameters p = camera.getParameters();
		p.setPreviewSize(procSize.width, procSize.height);
		Log.i("ok","");
		p.setPreviewFpsRange(fps[0],fps[1]);
		Log.i("ok","");
		camera.setParameters(p);
		Log.i("ok","");
		camera.setPreviewCallback(pvcb);
		try {
			camera.setPreviewDisplay(surfaceHolder);
			} catch(Exception e) {
				e.printStackTrace();
			}
		camera.startPreview();
	}
	
	private void setupCam() {
		/******************/
		/*camera = Camera.open();
		procSize = camera.new Size(0,0);
		Camera.Parameters p = camera.getParameters();
		supportedSizes = p.getSupportedPreviewSizes();
		procSize = supportedSizes.get(supportedSizes.size()/2);
		p.setPreviewSize(procSize.width, procSize.height);
		*/
		/******************/
		camera = Camera.open();
		procSize = camera.new Size(0,0);
		Camera.Parameters p = camera.getParameters();
		procSize.width = size[0];
		procSize.height = size[1];
		p.setPreviewFpsRange(fps[0],fps[1]);
		camera.setParameters(p);
		Log.i("sss","res:"+procSize.width+" "+procSize.height+" fps:"+fps[0]+"-"+fps[1]);
		camera.setDisplayOrientation(orientation);
		
		try {
			camera.setPreviewDisplay(surfaceHolder);
			} catch(Exception e) {
				e.printStackTrace();
			}
		camera.startPreview();
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setupCam();
		if (camReadyCback == null) camReadyCback.onCameraReady();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		release();
	}

}
