package com.example.palsampleapp;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
// [START nonce_dependencies]
import com.google.ads.interactivemedia.pal.ConsentSettings;
import com.google.ads.interactivemedia.pal.NonceLoader;
import com.google.ads.interactivemedia.pal.NonceManager;
import com.google.ads.interactivemedia.pal.NonceRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.HashSet;
import java.util.Set;

// [END nonce_dependencies]

/** Demonstrating usage of the PAL NonceLoader when making an ad request. */
public class MainActivity extends AppCompatActivity {

  // The ad tag for your ad request.
  // Example ad tag:
  // https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=
  private static final String DEFAULT_AD_TAG = "Your ad tag.";

  // The log tag for android studio logs.
  private static final String LOG_TAG = "PALSample";

  // [START pal_variables]
  private NonceLoader nonceLoader;
  private NonceManager nonceManager;
  // [END pal_variables]

  // The textview containing logs for the sample app.
  private TextView logView;

  private Button adClickButton;

  // [START on_create]
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ConsentSettings consentSettings = ConsentSettings.builder().build();

    // It is important to instantiate the NonceLoader as early as possible to
    // allow it to initialize and preload data for a faster experience when
    // loading the NonceManager. A new NonceLoader will need to be instantiated
    // if the ConsentSettings change for the user.
    nonceLoader = new NonceLoader(this, consentSettings);

    adClickButton = findViewById(R.id.send_click_button);

    logView = findViewById(R.id.log_view);
    logView.setMovementMethod(new ScrollingMovementMethod());
  }

  // [END on_create]

  /**
   * Makes a call to the SDK to create a nonce manager. In a typical PAL app, this would be called
   * at the start of the media stream.
   *
   * <p>The NonceRequest parameters set here are example parameters. You should set your parameters
   * based on your own app characteristics.
   */
  // [START generate_nonce]
  public void generateNonceForAdRequest(View view) {
    logMessage("Generate Nonce Request");
    Set supportedApiFrameWorksSet = new HashSet();
    // The values 2, 7, and 9 correspond to player support for VPAID 2.0,
    // OMID 1.0, and SIMID 1.1.
    supportedApiFrameWorksSet.add(2);
    supportedApiFrameWorksSet.add(7);
    supportedApiFrameWorksSet.add(9);

    NonceRequest nonceRequest =
        NonceRequest.builder()
            .descriptionURL("https://example.com/content1")
            .iconsSupported(true)
            .omidPartnerVersion("6.2.1")
            .omidPartnerName("Example Publisher")
            .playerType("ExamplePlayerType")
            .playerVersion("1.0.0")
            .ppid("testPpid")
            .sessionId("Sample SID")
            .supportedApiFrameworks(supportedApiFrameWorksSet)
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

                // [START ad_request_example]
                // From here you would trigger your ad request and move on to initialize content.
                exampleMakeAdRequest(DEFAULT_AD_TAG + "&givn=" + nonceString);
                // [END ad_request_example]

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

  // [END generate_nonce]

  // [START event_handlers]
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

  // [END event_handlers]

  private void exampleMakeAdRequest(String adTagUrl) {
    // Code to make your ad request.
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
