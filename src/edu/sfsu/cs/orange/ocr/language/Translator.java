
package edu.sfsu.cs.orange.ocr.language;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import edu.sfsu.cs.orange.ocr.CaptureActivity;
import edu.sfsu.cs.orange.ocr.PreferencesActivity;

/**
 * Delegates translation requests to the appropriate translation service.
 */
public class Translator {

  public static final String BAD_TRANSLATION_MSG = "[Translation unavailable]";
  
  public Translator(Activity activity) {  
    // Private constructor to enforce noninstantiability
  }
  
 public static String translate(Activity activity, String sourceLanguageCode, String targetLanguageCode, String sourceText) {   
    
    // Check preferences to determine which translation API to use--Google, or Bing.
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    String api = prefs.getString(PreferencesActivity.KEY_TRANSLATOR, CaptureActivity.DEFAULT_TRANSLATOR);
    
    // Delegate the translation based on the user's preference.
    if (api.equals(PreferencesActivity.TRANSLATOR_BING)) {
      
      // Get the correct code for the source language for this translation service.
      sourceLanguageCode = TranslatorBing.toLanguage(
          LanguageCodeHelper.getTranslationLanguageName(activity.getBaseContext(), sourceLanguageCode));
      
      return TranslatorBing.translate(sourceLanguageCode, targetLanguageCode, sourceText);
    } else if (api.equals(PreferencesActivity.TRANSLATOR_GOOGLE)) {
      
      // Get the correct code for the source language for this translation service.
      sourceLanguageCode = TranslatorGoogle.toLanguage(
          LanguageCodeHelper.getTranslationLanguageName(activity.getBaseContext(), sourceLanguageCode));      
      
      return TranslatorGoogle.translate(sourceLanguageCode, targetLanguageCode, sourceText);
    }
    return BAD_TRANSLATION_MSG;
  }
}