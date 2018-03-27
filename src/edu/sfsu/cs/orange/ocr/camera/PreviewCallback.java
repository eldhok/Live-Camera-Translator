

package edu.sfsu.cs.orange.ocr.camera;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.sfsu.cs.orange.ocr.CaptureActivity;

/**
 * Called when the next preview frame is received.
 * 
 */
final class PreviewCallback implements Camera.PreviewCallback {

  private static final String TAG = PreviewCallback.class.getSimpleName();

  private final CameraConfigurationManager configManager;
  private Handler previewHandler;
  private int previewMessage;

  PreviewCallback(CameraConfigurationManager configManager) {
    this.configManager = configManager;
  }

  void setHandler(Handler previewHandler, int previewMessage) {
    this.previewHandler = previewHandler;
    this.previewMessage = previewMessage;
  }

  // Since we're not calling setPreviewFormat(int), the data arrives here in the YCbCr_420_SP 
  // (NV21) format.
  @Override
  public synchronized void onPreviewFrame(byte[] data, Camera camera) {
  
	  Point cameraResolution = configManager.getCameraResolution();
	    Handler thePreviewHandler = previewHandler;
//new from here
	  Camera.Parameters parameters = camera.getParameters();
	   int width = cameraResolution.x;
	   int height =cameraResolution.y;
	   System.out.println("width===="+width);
	   System.out.println("height===="+height);
 

	   YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

	    byte[] bytes = out.toByteArray();
	 
	    
	    
	    
	  
	    
	    
	    
	    
	    
	   CaptureActivity.bitm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
	 //new ends here 
	  
	  
	  
	 
   
    //edited code
  //  Camera.Parameters parameters = camera.getParameters();
  // parameters.setPreviewFormat( ImageFormat.RGB_565 );
  // camera.setParameters( parameters );
    
  // System.out.println("format  "+camera.getParameters().getPictureFormat());
 // CaptureActivity.imgformatcamera=camera.getParameters().getPictureFormat();
    
    if (cameraResolution != null && thePreviewHandler != null) {
      Message message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x,
          cameraResolution.y, data);
      System.out.println("width    "+ cameraResolution.x);
      System.out.println("height    "+ cameraResolution.y);
      message.sendToTarget();
      previewHandler = null;
    } else {
      Log.d(TAG, "Got preview callback, but no handler or resolution available");
    }
  }

}
