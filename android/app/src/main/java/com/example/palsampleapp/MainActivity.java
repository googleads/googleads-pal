package com.example.palsampleapp;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ads.interactivemedia.pal.ConsentSettings;
import com.google.ads.interactivemedia.pal.NonceLoader;
import com.google.ads.interactivemedia.pal.NonceManager;
import com.google.ads.interactivemedia.pal.NonceRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/** Demonstrating usage of the PAL NonceLoader when making an ad request. */
public class MainActivity extends AppCompatActivity {

  // The ad tag for your ad request.
  // Example ad tag:
  // https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=
  private static final String DEFAULT_AD_TAG = "Your ad tag.";

  // The log tag for android studio logs.
  private static final String LOG_TAG = "PALSample";

  private NonceLoader nonceLoader;

  private NonceManager nonceManager;

  // The textview containing logs for the sample app.
  private TextView logView;

  private Button adClickButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // The default value for allowStorage() is false, but can be
    // changed once the appropriate consent has been gathered. The
    // getConsentToStorage() method is a placeholder for the publisher's own
    // method of obtaining user consent, either by integrating with a CMP or
    // based on other methods the publisher chooses to handle storage consent.
    boolean isStorageAllowed = getConsentToStorage();

    ConsentSettings consentSettings =
        ConsentSettings.builder().allowStorage(isStorageAllowed).build();

    // It is important to instantiate the NonceLoader as early as possible to
    // allow it to initialize and preload data for a faster experience when
    // loading the NonceManager. A new NonceLoader will need to be instantiated
    // if the ConsentSettings change for the user.
    nonceLoader = new NonceLoader(this, consentSettings);

    adClickButton = findViewById(R.id.send_click_button);

    logView = findViewById(R.id.log_view);
    logView.setMovementMethod(new ScrollingMovementMethod());
  }

  /**
   * Makes a call to the SDK to create a nonce manager. In a typical PAL app, this would be called
   * at the start of the media stream.
   *
   * <p>The NonceRequest parameters set here are example parameters. You should set your parameters
   * based on your own app characteristics.
   */
  public void generateNonceForAdRequest(View view) {
    logMessage("Generate Nonce Request");
    NonceRequest nonceRequest =
        NonceRequest.builder()
            .descriptionURL("https://example.com/content1")
            .omidVersion("1.0.0")
            .omidPartnerName("Example Publisher")
            .playerType("ExamplePlayerType")
            .playerVersion("1.0.0")
            .ppid("testPpid")
            .videoPlayerHeight(480)
            .videoPlayerWidth(640)
            .willAdAutoPlay(true)
            .willAdPlayMuted(false)
            .build();

    nonceLoader
        .loadNonceManager(nonceRequest)
        .addOnSuccessListener(
            new OnSuccessListener<NonceManager>() {
              @Override
              public void onSuccess(NonceManager manager) {
                nonceManager = manager;
                String nonceString = manager.getNonce();
                logMessage("Nonce generated");
                logMessage(nonceString.substring(0, 20) + "...");
                Log.i(LOG_TAG, "Generated nonce: " + nonceString);

                // From here you would trigger your ad request and move on to initialize content.
                exampleMakeAdRequest(DEFAULT_AD_TAG + "&givn=" + nonceString);

                adClickButton.setEnabled(true);
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(Exception error) {
                logMessage("Nonce generation failed");
                Log.e(LOG_TAG, "Nonce generation failed: " + error.getMessage());
              }
            });
  }

  // Triggered when a user clicks-through on an ad which was requested using a PAL nonce.
  public void sendAdClick(View view) {
    logMessage("Ad click sent");
    if (nonceManager != null) {
      nonceManager.sendAdClick();
    }
  }

  // In a typical PAL app, this is called when a user touch or click is detected,
  // on the ad other than an ad click-through.
  public void onVideoViewTouch(MotionEvent e) {
    if (nonceManager != null) {
      nonceManager.sendAdTouch(e);
    }
  }

  // In a typical PAL app, this is called when a content playback session starts.
  public void sendPlaybackStart() {
    logMessage("Playback start");
    if (nonceManager != null) {
      nonceManager.sendPlaybackStart();
    }
  }

  // In a typical PAL app, this is called when a content playback session ends.
  public void sendPlaybackEnd() {
    logMessage("Playback end");
    if (nonceManager != null) {
      nonceManager.sendPlaybackEnd();
    }
  }

  private void exampleMakeAdRequest(String adTagUrl) {
    // Code to make your ad request.
  }

  private boolean getConsentToStorage() {
    // Code to ask the user for storage consent.
    return false;
  }

  private void logMessage(String message) {
    logView.append(message + "\n");
    final int scrollAmt =
        logView.getLayout().getLineTop(logView.getLineCount()) - logView.getHeight();
    if (scrollAmt > 0) {
      logView.scrollTo(0, scrollAmt);
    } else {
      logView.scrollTo(0, 0);
    }
  }
}
