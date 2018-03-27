
package edu.sfsu.cs.orange.ocr;

import android.graphics.Bitmap;

/**
 * This object extends LuminanceSource around an array of YUV data returned from the camera driver,
 * with the option to crop to a rectangle within the full data. This can be used to exclude
 * superfluous pixels around the perimeter and speed up decoding.
 *
 * It works for any pixel format where the Y channel is planar and appears first, including
 * YCbCr_420_SP and YCbCr_422_SP.
 *
*/
public final class PlanarYUVLuminanceSource extends LuminanceSource {

  private final byte[] yuvData;
  private final int dataWidth;
  private final int dataHeight;
  private final int left;
  private final int top;

 
  
  public PlanarYUVLuminanceSource(byte[] yuvData,
                                  int dataWidth,
                                  int dataHeight,
                                  int left,
                                  int top,
                                  int width,
                                  int height,
                                  boolean reverseHorizontal) {
    super(width, height);

    if (left + width > dataWidth || top + height > dataHeight) {
      throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
    }

    this.yuvData = yuvData;
    this.dataWidth = dataWidth;
    this.dataHeight = dataHeight;
    this.left = left;
    this.top = top;
    if (reverseHorizontal) {
      reverseHorizontal(width, height);
    }
  }

  @Override
  public byte[] getRow(int y, byte[] row) {
    if (y < 0 || y >= getHeight()) {
      throw new IllegalArgumentException("Requested row is outside the image: " + y);
    }
    int width = getWidth();
    if (row == null || row.length < width) {
      row = new byte[width];
    }
    int offset = (y + top) * dataWidth + left;
    System.arraycopy(yuvData, offset, row, 0, width);
    return row;
  }

  @Override
  public byte[] getMatrix() {
    int width = getWidth();
    int height = getHeight();

    // If the caller asks for the entire underlying image, save the copy and give them the
    // original data. The docs specifically warn that result.length must be ignored.
    if (width == dataWidth && height == dataHeight) {
      return yuvData;
    }

    int area = width * height;
    byte[] matrix = new byte[area];
    int inputOffset = top * dataWidth + left;

    // If the width matches the full width of the underlying data, perform a single copy.
    if (width == dataWidth) {
      System.arraycopy(yuvData, inputOffset, matrix, 0, area);
      return matrix;
    }

    // Otherwise copy one cropped row at a time.
    byte[] yuv = yuvData;
    for (int y = 0; y < height; y++) {
      int outputOffset = y * width;
      System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
      inputOffset += dataWidth;
    }
    return matrix;
  }

  @Override
  public boolean isCropSupported() {
    return true;
  }

  @Override
  public LuminanceSource crop(int left, int top, int width, int height) {
    return new PlanarYUVLuminanceSource(yuvData,
                                        dataWidth,
                                        dataHeight,
                                        this.left + left,
                                        this.top + top,
                                        width,
                                        height,
                                        false);
  }

  public Bitmap renderCroppedGreyscaleBitmap() {
	 
    int width = getWidth();
    int height = getHeight();
    System.out.println("width vallll"+width);
    System.out.println("height valll"+height);
    int[] pixels = new int[width * height];
    byte[] yuv = yuvData;
    int inputOffset = top * dataWidth + left;
   
    for (int y = 0; y < height; y++) {
      int outputOffset = y * width;
      for (int x = 0; x < width; x++) {
        int grey = yuv[inputOffset + x] & 0xff;
        pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
    	 
      }
      inputOffset += dataWidth;
    }

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
  }
  
  
  


  
  //new code
  
  public void mar_iamge() {
	  

	
	      
	      
	      
	      
	      
	

     
	 
	 /* 
	
	   
	    byte[] yuv1 = yuvData;
	   Bitmap bitmap;
	   bitmap= BitmapFactory.decodeByteArray(yuv1, 0, yuv1.length);
	 return bitmap;
	
	
	  
	
	  int width = getWidth();
	    int height = getHeight();
	      byte[] yuv1 = yuvData;
	    YuvImage yuv = new YuvImage(yuv1,ImageFormat.NV21, width, height, null);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

	    byte[] bytes = out.toByteArray();
	    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	  

	  */  
	   
	  }
  
  
  
 
  
  public Bitmap onPreviewFrame(byte[] data, int width, int height) {
	  int frameSize = width*height;
	  
	  int[] rgba = new int[frameSize+1];
	    // Convert YUV to RGB
	    for (int i = 0; i < height; i++)
	        for (int j = 0; j < width; j++) {
	            int y = (0xff & ((int) data[i * width + j]));
	            int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
	            int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
	            y = y < 16 ? 16 : y;

	            int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
	            int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
	            int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

	            r = r < 0 ? 0 : (r > 255 ? 255 : r);
	            g = g < 0 ? 0 : (g > 255 ? 255 : g);
	            b = b < 0 ? 0 : (b > 255 ? 255 : b);

	            rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
	        }
	    
	    
	    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    bmp.setPixels(rgba, 0/* offset */, width /* stride */, 0, 0, width, height);
	    return bmp;
  }
  
  
  
  
  
  
  
  
  
  

//we tackle the conversion two pixels at a time for greater speed
private void toRGB565(byte[] yuvs, int width, int height, byte[] rgbs) {
    //the end of the luminance data
    final int lumEnd = width * height;
    //points to the next luminance value pair
    int lumPtr = 0;
    //points to the next chromiance value pair
    int chrPtr = lumEnd;
    //points to the next byte output pair of RGB565 value
    int outPtr = 0;
    //the end of the current luminance scanline
    int lineEnd = width;

    while (true) {

        //skip back to the start of the chromiance values when necessary
        if (lumPtr == lineEnd) {
            if (lumPtr == lumEnd) break; //we've reached the end
            //division here is a bit expensive, but's only done once per scanline
            chrPtr = lumEnd + ((lumPtr  >> 1) / width) * width;
            lineEnd += width;
        }

        //read the luminance and chromiance values
        final int Y1 = yuvs[lumPtr++] & 0xff; 
        final int Y2 = yuvs[lumPtr++] & 0xff; 
        final int Cr = (yuvs[chrPtr++] & 0xff) - 128; 
        final int Cb = (yuvs[chrPtr++] & 0xff) - 128;
        int R, G, B;

        //generate first RGB components
        B = Y1 + ((454 * Cb) >> 8);
        if(B < 0) B = 0; else if(B > 255) B = 255; 
        G = Y1 - ((88 * Cb + 183 * Cr) >> 8); 
        if(G < 0) G = 0; else if(G > 255) G = 255; 
        R = Y1 + ((359 * Cr) >> 8); 
        if(R < 0) R = 0; else if(R > 255) R = 255; 
        //NOTE: this assume little-endian encoding
        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));

        //generate second RGB components
        B = Y2 + ((454 * Cb) >> 8);
        if(B < 0) B = 0; else if(B > 255) B = 255; 
        G = Y2 - ((88 * Cb + 183 * Cr) >> 8); 
        if(G < 0) G = 0; else if(G > 255) G = 255; 
        R = Y2 + ((359 * Cr) >> 8); 
        if(R < 0) R = 0; else if(R > 255) R = 255; 
        //NOTE: this assume little-endian encoding
        rgbs[outPtr++]  = (byte) (((G & 0x3c) << 3) | (B >> 3));
        rgbs[outPtr++]  = (byte) ((R & 0xf8) | (G >> 5));
    }
}
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
	    final int frameSize = width * height;

	    for (int j = 0, yp = 0; j < height; j++) {
	        int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
	        for (int i = 0; i < width; i++, yp++) {
	            int y = (0xff & ((int) yuv420sp[yp])) - 16;
	            if (y < 0) y = 0;
	            if ((i & 1) == 0) {
	                v = (0xff & yuv420sp[uvp++]) - 128;
	                u = (0xff & yuv420sp[uvp++]) - 128;
	            }
	            int y1192 = 1192 * y;
	            int r = (y1192 + 1634 * v);
	            int g = (y1192 - 833 * v - 400 * u);
	            int b = (y1192 + 2066 * u);

	            if (r < 0) r = 0; else if (r > 262143) r = 262143;
	            if (g < 0) g = 0; else if (g > 262143) g = 262143;
	            if (b < 0) b = 0; else if (b > 262143) b = 262143;

	            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
	        }
	    }
	}
  
  
  

  private void reverseHorizontal(int width, int height) {
    byte[] yuvData = this.yuvData;
    for (int y = 0, rowStart = top * dataWidth + left; y < height; y++, rowStart += dataWidth) {
      int middle = rowStart + width / 2;
      for (int x1 = rowStart, x2 = rowStart + width - 1; x1 < middle; x1++, x2--) {
        byte temp = yuvData[x1];
        yuvData[x1] = yuvData[x2];
        yuvData[x2] = temp;
      }
    }
  }

}
