
package edu.sfsu.cs.orange.ocr.language;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import edu.sfsu.cs.orange.ocr.CaptureActivity;

/**
 * Class to perform translations in the background for live translation.
 */
public final class trans extends AsyncTask<String, String, Boolean> {

  private static final String TAG = TranslateAsyncTask.class.getSimpleName();

  private CaptureActivity activity;
  //private TextView textView;
  //private View progressView;
  private TextView targetLanguageTextView;
  private String sourceLanguageCode;
  private String targetLanguageCode;
  private String sourceText;
  private String translatedText = "";

  public trans(CaptureActivity activity, String sourceLanguageCode, String targetLanguageCode, 
      String sourceText) {
    this.activity = activity;
    this.sourceLanguageCode = sourceLanguageCode;
    this.targetLanguageCode = targetLanguageCode;
    this.sourceText = sourceText;
  
    
    
    
  //   textView = (TextView) activity.findViewById(R.id.translation_text_view);
  //  progressView = (View) activity.findViewById(R.id.indeterminate_progress_indicator_view);
  //  targetLanguageTextView = (TextView) activity.findViewById(R.id.translation_language_text_view);
 
  
  
  }
  
  @Override
  protected Boolean doInBackground(String... arg0) {
    translatedText = Translator.translate(activity, sourceLanguageCode, targetLanguageCode, sourceText);

    // Check for failed translations.
    if (translatedText.equals(Translator.BAD_TRANSLATION_MSG)) {
      return false;
    }
    
    return true;
  }

  @Override
  protected synchronized void onPostExecute(Boolean result) {
    super.onPostExecute(result);
    
    if (result) {
      //Log.i(TAG, "SUCCESS");
     if (targetLanguageTextView != null) {
        targetLanguageTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
      }
     //new start
      CaptureActivity.translat=translatedText;
      //new end
     
      
     // remove all comment
      
  //  textView.setText(translatedText);
   //   textView.setVisibility(View.VISIBLE);
   // textView.setTextColor(activity.getResources().getColor(R.color.translation_text));

      // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
  // int scaledSize = Math.max(22, 32 - translatedText.length() / 4);
  //  textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

    } else {
      Log.e(TAG, "FAILURE");
  // targetLanguageTextView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
  //   targetLanguageTextView.setText("Unavailable");

    }
    
    // Turn off the indeterminate progress indicator
 // if (progressView != null) {
 //    progressView.setVisibility(View.GONE);
//   }
  }
}
