package edu.sfsu.cs.orange.ocr;

import edu.sfsu.cs.orange.ocr.CaptureActivity;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 
 */
final class DecodeThread extends Thread {

  private final CaptureActivity activity;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;

  DecodeThread(CaptureActivity activity) {
    this.activity = activity;
    handlerInitLatch = new CountDownLatch(1);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(activity);
    handlerInitLatch.countDown();
    Looper.loop();
  }
}
