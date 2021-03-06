package trash;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Preview extends SurfaceView implements SurfaceHolder.Callback 
{
    private static final String TAG = "Preview";

    SurfaceHolder mHolder;
    public Camera camera;

    Preview(Context context) 
    {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setFps() {
   // void	 setPreviewFpsRange(int min, int max)
    
    }
    
    public void setResolution(){
    	Parameters params = camera.getParameters();
    	List sizes = params.getSupportedPictureSizes();
    	Camera.Size result = null;
    	    for (int i=0;i<sizes.size();i++){
    	        result = (Size) sizes.get(i);
    	        Log.i("PictureSize", "Supported Size. Width: " + result.width + "height : " + result.height); 
    	    }
    }
    
    public void surfaceCreated(SurfaceHolder holder) 
    {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
         camera = Camera.open();
        try {
        camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
        camera.release();
        camera = null;
            // TODO: add more exception handling logic here
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) 
    {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        
        // You need to choose the most appropriate previewSize for your app
      //  Camera.Size previewSize = 
        
      //  parameters.setPreviewSize(previewSize.width, previewSize.height);
        camera.setParameters(parameters);
        camera.startPreview();
    }
    @Override
    public void draw(Canvas canvas) 
    {
        super.draw(canvas);
        Paint p= new Paint(Color.RED);
        Log.d(TAG,"draw");
        canvas.drawText("PREVIEW", canvas.getWidth()/2, canvas.getHeight()/2, p );
    }
    
    
}
