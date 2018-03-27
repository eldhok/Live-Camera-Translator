
package edu.sfsu.cs.orange.ocr;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import edu.sfsu.cs.orange.ocr.camera.CameraManager;
import edu.sfsu.cs.orange.ocr.language.trans;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the result text.
 *
 */
public final class ViewfinderView extends View{
  //private static final long ANIMATION_DELAY = 80L;

  /** Flag to draw boxes representing the results from TessBaseAPI::GetRegions(). */
  static final boolean DRAW_REGION_BOXES = false;

  /** Flag to draw boxes representing the results from TessBaseAPI::GetTextlines(). */
  static final boolean DRAW_TEXTLINE_BOXES = true;

  /** Flag to draw boxes representing the results from TessBaseAPI::GetStrips(). */
  static final boolean DRAW_STRIP_BOXES = false;

  /** Flag to draw boxes representing the results from TessBaseAPI::GetWords(). */
  static final boolean DRAW_WORD_BOXES = true;

  /** Flag to draw word text with a background varying from transparent to opaque. */
  static final boolean DRAW_TRANSPARENT_WORD_BACKGROUNDS = false;

  /** Flag to draw boxes representing the results from TessBaseAPI::GetCharacters(). */
  static final boolean DRAW_CHARACTER_BOXES = false;

  /** Flag to draw the text of words within their respective boxes from TessBaseAPI::GetWords(). */
  static final boolean DRAW_WORD_TEXT = false;

  /** Flag to draw each character in its respective box from TessBaseAPI::GetCharacters(). */
  static final boolean DRAW_CHARACTER_TEXT = false;

  private CameraManager cameraManager;
  public static  Paint paint,p1;
  private final int maskColor;
  private final int frameColor;
  private final int cornerColor;
  private OcrResultText resultText;
  private String[] words;
  private List<Rect> regionBoundingBoxes;
  private List<Rect> textlineBoundingBoxes;
  private List<Rect> stripBoundingBoxes;
  private List<Rect> wordBoundingBoxes;
  private List<Rect> characterBoundingBoxes;
  //  Rect bounds;
  private  Rect previewFrame;
  private Rect rect;
public static Bitmap b,bala;


CaptureActivity capt;


int flag=0;


  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.viewfinder_mask);
    frameColor = resources.getColor(R.color.viewfinder_frame);
    cornerColor = resources.getColor(R.color.viewfinder_corners);

    //    bounds = new Rect();
    previewFrame = new Rect();
    rect = new Rect();
  }

  
  
  
  
  
  /*
  
  public static synchronized Boolean pall()
  {
	  Palette.from(b).generate(new Palette.PaletteAsyncListener() {
          public void onGenerated(Palette palette) {
              Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
              if (vibrantSwatch != null) {
                 // outerLayout.setBackgroundColor(vibrantSwatch.getRgb());
                 // titleText.setTextColor(vibrantSwatch.getTitleTextColor());
                 // bodyText.setTextColor(vibrantSwatch.getBodyTextColor());
            	 // Log.w("myApp", "green"+Color.GREEN); 
            	  Log.w("myApp", "vibrantSwatch.getRgb()"+vibrantSwatch.getRgb()); 
            	  Log.w("myApp", "vibrantSwatch.getTitleTextColor()"+vibrantSwatch.getTitleTextColor()); 
            	  Log.w("myApp", "vibrantSwatch.getBodyTextColor()"+vibrantSwatch.getBodyTextColor()); 
            		 System.out.println("inside pallete");
                    paint.setStyle(Style.FILL);
                    paint.setStrokeWidth(1);
               //	 p1.setColor(Color.BLACK);
                    paint.setColor(vibrantSwatch.getBodyTextColor());
               	
                  p1.setColor(vibrantSwatch.getRgb());	
                  System.out.println("rgb  "+vibrantSwatch.getRgb());  
                  System.out.println("body "+vibrantSwatch.getBodyTextColor());  
                //  paint.setColor(vibrantSwatch.getRgb());	  
                 
              }
              else 
            	  System.out.println("no pallete found"); 
          }
      });  
	  return true;
  }
  
  
  
  
  */
  
  
  
  
  
  
  public void setCameraManager(CameraManager cameraManager,CaptureActivity capt) {
    this.cameraManager = cameraManager;
    this.capt = capt;
  }

  @SuppressWarnings("unused")
  @Override
  public void onDraw(Canvas canvas) {
    Rect frame = cameraManager.getFramingRect();
    if (frame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight(); 

    // Draw the exterior (i.e. outside the framing rect) darkened
    paint.setColor(maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    // If we have an OCR result, overlay its information on the viewfinder.
    if (resultText != null) {

      // Only draw text/bounding boxes on viewfinder if it hasn't been resized since the OCR was requested.
      Point bitmapSize = resultText.getBitmapDimensions();
      previewFrame = cameraManager.getFramingRectInPreview();
      if (bitmapSize.x == previewFrame.width() && bitmapSize.y == previewFrame.height()) {


        float scaleX = frame.width() / (float) previewFrame.width();
        float scaleY = frame.height() / (float) previewFrame.height();

      //new commented area statrs
        
      /*  
        if (DRAW_REGION_BOXES) {
          regionBoundingBoxes = resultText.getRegionBoundingBoxes();
          for (int i = 0; i < regionBoundingBoxes.size(); i++) {
            paint.setAlpha(0xA0);
            paint.setColor(Color.MAGENTA);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(1);
            rect = regionBoundingBoxes.get(i);
            canvas.drawRect(frame.left + rect.left * scaleX,
                frame.top + rect.top * scaleY, 
                frame.left + rect.right * scaleX, 
                frame.top + rect.bottom * scaleY, paint);
          }      
        }

        if (DRAW_TEXTLINE_BOXES) {
          // Draw each textline
          textlineBoundingBoxes = resultText.getTextlineBoundingBoxes();
          paint.setAlpha(0xA0);
          paint.setColor(Color.RED);
          paint.setStyle(Style.STROKE);
          paint.setStrokeWidth(1);
          for (int i = 0; i < textlineBoundingBoxes.size(); i++) {
            rect = textlineBoundingBoxes.get(i);
            canvas.drawRect(frame.left + rect.left * scaleX,
                frame.top + rect.top * scaleY, 
                frame.left + rect.right * scaleX, 
                frame.top + rect.bottom * scaleY, paint);
          }
        }

        if (DRAW_STRIP_BOXES) {
          stripBoundingBoxes = resultText.getStripBoundingBoxes();
          paint.setAlpha(0xFF);
          paint.setColor(Color.YELLOW);
          paint.setStyle(Style.STROKE);
          paint.setStrokeWidth(1);
          for (int i = 0; i < stripBoundingBoxes.size(); i++) {
            rect = stripBoundingBoxes.get(i);
            canvas.drawRect(frame.left + rect.left * scaleX,
                frame.top + rect.top * scaleY, 
                frame.left + rect.right * scaleX, 
                frame.top + rect.bottom * scaleY, paint);
          }        	
        }



*/

//commented area ends




        if (DRAW_WORD_BOXES || DRAW_WORD_TEXT) {
          // Split the text into words
          wordBoundingBoxes = resultText.getWordBoundingBoxes();
          //      for (String w : words) {
          //        Log.e("ViewfinderView", "word: " + w);
          //      }
          //Log.d("ViewfinderView", "There are " + words.length + " words in the string array.");
          //Log.d("ViewfinderView", "There are " + wordBoundingBoxes.size() + " words with bounding boxes.");
        }

        if (DRAW_WORD_BOXES) {
       
        	
        	 flag=0;	
        	
        	
        	
     	Rect recti = CameraManager.getFramingRectInPreview();
        b=Bitmap.createBitmap(CaptureActivity.bitm,recti.left,recti.top,recti.width(), recti.height());
       
       
        rect = wordBoundingBoxes.get(0);
        	
       	 System.out.println("called recttt");   	
        	
       
       	 bala=Bitmap.createBitmap(b,rect.left,rect.top,rect.width(), rect.height());
       p1.setTextAlign(Paint.Align.CENTER);
        // int c;
          if(bala!=null){
        	  System.out.println("inside b not null");   
        	flag=1;
        	
        	 
             	 int cccc=bala.getPixel((int)Math.round(bala.getWidth()/2),1);
             	 p1.setColor(Color.WHITE);
             	
             	 paint.setColor(cccc);
           
        	
        	
        /*	
      	
        	int redBucket = 0;
        	int greenBucket = 0;
        	int blueBucket = 0;
        	int pixelCount = 0;
        	//  Log.w("myApp", "width"+b.getWidth()); 
        	 // Log.w("myApp", "height"+b.getHeight()); 
        	for(int gf=0;gf<5;gf++)
        	{
        		for(int ff=0;ff<5;ff++){
        			 pixelCount++;
        			 int c1 =b.getPixel(gf, ff);

        		        pixelCount++;
        		        redBucket += Color.red(c1);
        		        greenBucket += Color.green(c1);
        		        blueBucket += Color.blue(c1);
        		}
        	}
        	int averageColor = Color.rgb(redBucket / pixelCount,
                    greenBucket / pixelCount,
                    blueBucket / pixelCount);
                    
                    
                    
                    
        	//int c2 =b.getPixel((int)Math.round( b.getWidth()/2),(int)Math.round(b.getHeight()/2));
        	
        	 System.out.println("red = "+redBucket / pixelCount);
        	 System.out.println("green = "+greenBucket / pixelCount);
        	 System.out.println("blue = "+blueBucket / pixelCount);
        	 
        	 
        	 
        	    paint.setColor(averageColor);	
        	    
        	    */
        	 paint.setStrokeWidth(1);
        	 paint.setStyle(Style.FILL);
        	// p1.setColor(Color.BLACK);
        	 
        	
       // int hh=(int)Math.round( b.getHeight()/2);
         	
        	 /*
        	 for(int ff=0;ff<hh;ff++)
         		{
         			
         			 int c1 =b.getPixel(gf, ff);
         			 
         			 
         			 
if(Math.abs(Color.red(c1)-Color.red(averageColor))>=20 || Math.abs(Color.green(c1)-Color.green(averageColor))>=20 || Math.abs(Color.blue(c1)-Color.blue(averageColor))>=20)
{
	
	 p1.setColor(Color.WHITE);
	 
	 paint.setColor(c1);
	 break;

}

         		    
         		}
         
    /*   	
        
        	 
        	 
        	 System.out.println("going to enter pallete");   
       
        
        	 
        	 
        	 
        	
       
        /*	
        	  
        	  paint.setStyle(Style.FILL);
        	   c=b.getPixel(1, 4);
        	  paint.setColor(c);
        	  int h=b.getHeight(),w=b.getWidth();
        	  outerloop:
              for(int y=0;y<h;y++)
      		{
      			for(int x=0;x<w;x++)
      			{
      			  
      				if(b.getPixel(x, y)>c){
      					 p1.setColor(b.getPixel(x, y));	
      					break outerloop;
      				}
              
      			}
      		} 
      		
      	*/ 
          
          }
          else
          {
        	  System.out.println("inside elseeee");
        		//paint.setAlpha(0xFF);
               // paint.setColor(0xFF00CCFF);
        	// paint.setColor(Color.WHITE);
        	 paint.setColor(Color.TRANSPARENT);
               p1.setColor(Color.BLACK);
          }
        
    
          
          
        //  String stext=resultText.getText();
         
 
         
         //new ColorDrawable(Color.parseColor("#ffffff")) 
          //code change start
          
            //p1.setTextSize(50);
            //p1.setTextAlign(Paint.Align.CENTER);
String wo= resultText.getText();
             
              //code change ends
           
            
if (CaptureActivity.isTranslationActive) {
	 System.out.println("calling translate");
	 new trans(capt, CaptureActivity.sourceLanguageCodeTranslation, CaptureActivity.targetLanguageCodeTranslation, 
   			wo).execute();
	 words =CaptureActivity.translat.replace("\n"," ").split(" "); 
}
else{
	 words =wo.replace("\n"," ").split(" "); 
}
	
      	 
      	
            
            
      	  
        
         
            
   	/*	 
   		 View rootView = findViewById(android.R.id.content).getRootView();
   	     rootView.setDrawingCacheEnabled(true);
   	      b=rootView.getDrawingCache(); 
         
       */     
           
           
float testTextSize; 
           
System.out.println("going to enter loop");
            
            for (int i = 0; i < wordBoundingBoxes.size() && i<words.length; i++) {
            // Draw a bounding box around the word
            rect = wordBoundingBoxes.get(i);
            
            
           
         
            System.out.println("inside loop");   
            
           
       if(flag==1){
            //flag to be set
            canvas.drawRect(
                frame.left + rect.left * scaleX,
                frame.top + rect.top * scaleY, 
                frame.left + rect.right * scaleX, 
                frame.top + rect.bottom * scaleY, paint);
       } 
               
               
                
            testTextSize =(frame.top + rect.bottom * scaleY)-(frame.top + rect.top * scaleY)+(rect.height()/4);
       
     //  float testTextSize =(frame.top + rect.bottom * scaleY)-(frame.top + rect.top * scaleY);
       
           //nw
    
            p1.setTextSize(testTextSize);
           // Rect bounds = new Rect();
          //  p1.getTextBounds(words[i], 0, words[i].length(), bounds);
        
            // Calculate the desired size as a proportion of our testTextSize.
        //    float desiredTextSize = testTextSize * ((frame.left + rect.right * scaleX)-(frame.left + rect.left * scaleX)) / bounds.width();
        //    float w = p1.measureText(words[i])/2;
           // float textSize = p1.getTextSize();
        //    p1.setTextSize(desiredTextSize);
        
         
            
       
            
            
            
            
          // canvas.drawText(words[i],(frame.left + rect.left * scaleX)+w, (frame.top + rect.top * scaleY)+textSize,p1);  
           canvas.drawText(words[i],(int)Math.round((frame.left + rect.left * scaleX)+(rect.width()/2)), (frame.top + rect.bottom * scaleY),p1);
           /*  
           //new start
         
          // p1.setTextSize(wordBoundingBoxes.get(i).height());
           if(words[i]!=null && !words[i].equals("")){
           	
            p1.setTextSize(testTextSize);
              
            p1.getTextBounds(words[i], 0, words[i].length(), rect);
           float desiredTextSize = testTextSize * rect.width() / rect.width();
           p1.setTextSize(desiredTextSize);
           float w = p1.measureText(words[i])/2;
           float textSize = p1.getTextSize();
      // canvas.drawText(words[i],frame.left + rect.left * scaleX, frame.top + rect.bottom * scaleY ,p1);
           canvas.drawText(words[i],(frame.left + rect.left * scaleX)+w, (frame.top + rect.top * scaleY)+textSize ,p1);
           	
           	//new end
           	  
           	  */ 
            
      
            	 
           
          }
        }  

       
        
        
        
        
        //new comment to stop popup
        
      /*
        
        
        
        
        if (DRAW_WORD_TEXT) { 
          words = resultText.getText().replace("\n"," ").split(" ");
          int[] wordConfidences = resultText.getWordConfidences();          
          for (int i = 0; i < wordBoundingBoxes.size(); i++) {
            boolean isWordBlank = true;
            try {
              if (!words[i].equals("")) {
                isWordBlank = false;
              }
            } catch (ArrayIndexOutOfBoundsException e) {
              e.printStackTrace();
            }

            // Only draw if word has characters
            if (!isWordBlank) {
              // Draw a white background around each word
              rect = wordBoundingBoxes.get(i);
              paint.setColor(Color.WHITE);
              paint.setStyle(Style.FILL);
              if (DRAW_TRANSPARENT_WORD_BACKGROUNDS) {
                // Higher confidence = more opaque, less transparent background
                paint.setAlpha(wordConfidences[i] * 255 / 100);
              } else {
                paint.setAlpha(255);
              }
              canvas.drawRect(frame.left + rect.left * scaleX,
                  frame.top + rect.top * scaleY, 
                  frame.left + rect.right * scaleX, 
                  frame.top + rect.bottom * scaleY, paint);

              // Draw the word in black text
              paint.setColor(Color.BLACK);
              paint.setAlpha(0xFF);
              paint.setAntiAlias(true);
              paint.setTextAlign(Align.LEFT);

              // Adjust text size to fill rect
              paint.setTextSize(100);
              paint.setTextScaleX(1.0f);
              // ask the paint for the bounding rect if it were to draw this text
              Rect bounds = new Rect();
              paint.getTextBounds(words[i], 0, words[i].length(), bounds);
              // get the height that would have been produced
              int h = bounds.bottom - bounds.top;
              // figure out what textSize setting would create that height of text
              float size  = (((float)(rect.height())/h)*100f);
              // and set it into the paint
              paint.setTextSize(size);
              // Now set the scale.
              // do calculation with scale of 1.0 (no scale)
              paint.setTextScaleX(1.0f);
              // ask the paint for the bounding rect if it were to draw this text.
              paint.getTextBounds(words[i], 0, words[i].length(), bounds);
              // determine the width
              int w = bounds.right - bounds.left;
              // calculate the baseline to use so that the entire text is visible including the descenders
              int text_h = bounds.bottom-bounds.top;
              int baseline =bounds.bottom+((rect.height()-text_h)/2);
              // determine how much to scale the width to fit the view
              float xscale = ((float) (rect.width())) / w;
              // set the scale for the text paint
              paint.setTextScaleX(xscale);
              canvas.drawText(words[i], frame.left + rect.left * scaleX, frame.top + rect.bottom * scaleY - baseline, paint);
            }

          }
        }  





*/

//closing ends here




//        if (DRAW_CHARACTER_BOXES || DRAW_CHARACTER_TEXT) {
//          characterBoundingBoxes = resultText.getCharacterBoundingBoxes();
//        }
//
//        if (DRAW_CHARACTER_BOXES) {
//          // Draw bounding boxes around each character
//          paint.setAlpha(0xA0);
//          paint.setColor(0xFF00FF00);
//          paint.setStyle(Style.STROKE);
//          paint.setStrokeWidth(1);
//          for (int c = 0; c < characterBoundingBoxes.size(); c++) {
//            Rect characterRect = characterBoundingBoxes.get(c);
//            canvas.drawRect(frame.left + characterRect.left * scaleX,
//                frame.top + characterRect.top * scaleY, 
//                frame.left + characterRect.right * scaleX, 
//                frame.top + characterRect.bottom * scaleY, paint);
//          }
//        }
//
//        if (DRAW_CHARACTER_TEXT) {
//          // Draw letters individually
//          for (int i = 0; i < characterBoundingBoxes.size(); i++) {
//            Rect r = characterBoundingBoxes.get(i);
//
//            // Draw a white background for every letter
//            int meanConfidence = resultText.getMeanConfidence();
//            paint.setColor(Color.WHITE);
//            paint.setAlpha(meanConfidence * (255 / 100));
//            paint.setStyle(Style.FILL);
//            canvas.drawRect(frame.left + r.left * scaleX,
//                frame.top + r.top * scaleY, 
//                frame.left + r.right * scaleX, 
//                frame.top + r.bottom * scaleY, paint);
//
//            // Draw each letter, in black
//            paint.setColor(Color.BLACK);
//            paint.setAlpha(0xFF);
//            paint.setAntiAlias(true);
//            paint.setTextAlign(Align.LEFT);
//            String letter = "";
//            try {
//              char c = resultText.getText().replace("\n","").replace(" ", "").charAt(i);
//              letter = Character.toString(c);
//
//              if (!letter.equals("-") && !letter.equals("_")) {
//
//                // Adjust text size to fill rect
//                paint.setTextSize(100);
//                paint.setTextScaleX(1.0f);
//
//                // ask the paint for the bounding rect if it were to draw this text
//                Rect bounds = new Rect();
//                paint.getTextBounds(letter, 0, letter.length(), bounds);
//
//                // get the height that would have been produced
//                int h = bounds.bottom - bounds.top;
//
//                // figure out what textSize setting would create that height of text
//                float size  = (((float)(r.height())/h)*100f);
//
//                // and set it into the paint
//                paint.setTextSize(size);
//
//                // Draw the text as is. We don't really need to set the text scale, because the dimensions
//                // of the Rect should already be suited for drawing our letter. 
//                canvas.drawText(letter, frame.left + r.left * scaleX, frame.top + r.bottom * scaleY, paint);
//              }
//            } catch (StringIndexOutOfBoundsException e) {
//              e.printStackTrace();
//            } catch (Exception e) {
//              e.printStackTrace();
//            }
//          }
//        }
      }

    }
    
    
    
    
    //also here to avoid popup
    
    
    
    
    /*
    
    
    
    // Draw a two pixel solid border inside the framing rect
    paint.setAlpha(0);
    paint.setStyle(Style.FILL);
    paint.setColor(frameColor);
    canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
    canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
    canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
    canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

    // Draw the framing rect corner UI elements
    paint.setColor(cornerColor);
    canvas.drawRect(frame.left - 15, frame.top - 15, frame.left + 15, frame.top, paint);
    canvas.drawRect(frame.left - 15, frame.top, frame.left, frame.top + 15, paint);
    canvas.drawRect(frame.right - 15, frame.top - 15, frame.right + 15, frame.top, paint);
    canvas.drawRect(frame.right, frame.top - 15, frame.right + 15, frame.top + 15, paint);
    canvas.drawRect(frame.left - 15, frame.bottom, frame.left + 15, frame.bottom + 15, paint);
    canvas.drawRect(frame.left - 15, frame.bottom - 15, frame.left, frame.bottom, paint);
    canvas.drawRect(frame.right - 15, frame.bottom, frame.right + 15, frame.bottom + 15, paint);
    canvas.drawRect(frame.right, frame.bottom - 15, frame.right + 15, frame.bottom + 15, paint);  


    // Request another update at the animation interval, but don't repaint the entire viewfinder mask.
    //postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    
     
     
     
     
     */
    
    
    
    //comment ends here
    
    
  }

  public void drawViewfinder() {
    invalidate();
  }

  /**
   * Adds the given OCR results for drawing to the view.
   * 
   * @param text Object containing OCR-derived text and corresponding data.
   */
  public void addResultText(OcrResultText text) {
    resultText = text; 
  }

  /**
   * Nullifies OCR text to remove it at the next onDraw() drawing.
   */
  public void removeResultText() {
    resultText = null;
  }
}
