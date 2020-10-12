# Live-Camera-Translator ##

An experimental app for Android that performs optical character recognition (OCR) on images captured using the device camera.

This is a Modification of the Original Project https://github.com/rmtheis/android-ocr

Runs the Tesseract OCR engine using [tess-two](https://github.com/rmtheis/tess-two), a fork of Tesseract Tools for Android.

Most of the code making up the core structure of this project has been adapted from the ZXing Barcode Scanner. Along with Tesseract-OCR and Tesseract Tools for Android (tesseract-android-tools), several open source projects have been used in this project, including leptonica, google-api-translate-java, microsoft-translator-java-api, and jtar.

## Requires ##

- A Windows Azure Marketplace Client ID and Client Secret (for translation) - [Documentation](https://docs.microsoft.com/en-us/azure/#pivot=products&panel=ai)
- A Google Translate API key (for translation) - [Documentation](https://console.developers.google.com/cloud-resource-manager)
    
    
## Training Data for OCR ##

A data file is required for every language you want to recognize. For English, this data file is included in the application assets and is automatically installed when the app is first run.

For other languages (Spanish, French, Chinese, etc.), the app will try to download the training data from an old Google Code repository that is no longer available. So if you want to use training data for other languages, you'll need to package the appropriate [training data files](https://github.com/tesseract-ocr/tessdata) in the app or change the code to point to your own download location.
Installation

To build and run the app, clone this project, open it as an existing project in Android Studio, and click Run.

##Screenshots##

![](https://github.com/eldhok/Live-Camera-Translator/blob/master/Page1.jpg)

![](https://github.com/eldhok/Live-Camera-Translator/blob/master/Page_2.jpg)


![Click to download the Brochure](https://github.com/eldhok/Live-Camera-Translator/files/1850847/L.VE.TRANSLATOR.1.pdf)
