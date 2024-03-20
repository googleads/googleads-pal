/*
Copyright 2024 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

let videoElement;
let nonceLoader;
let managerPromise;
let nonceManager;
let storageConsent = true;
let playbackStarted = false;

/**
 * A placeholder for the publisher's own method of obtaining user
 * consent, either by integrating with a CMP or based on other
 * methods the publisher chooses to handle storage consent.
 * @return {boolean} Whether storage consent has been given.
 */
function getConsentToStorage() {
  return storageConsent;
}

/**
 * Initializes the PAL loader.
 */
function init() {
  const videoElement = document.getElementById('placeholder-video');
  videoElement.addEventListener('mousedown', onVideoTouch);
  videoElement.addEventListener('touchstart', onVideoTouch);
  videoElement.addEventListener('play', function() {
    if (!playbackStarted) {
      sendPlaybackStart();
      playbackStarted = true;
    }
  });
  videoElement.addEventListener('ended', sendPlaybackEnd);
  videoElement.addEventListener('error', function() {
    log("Video error: " + videoElement.error.message);
    sendPlaybackEnd();
  });

  document.addEventListener('keydown', function(e) {
    // Fake ad click, video touch, playback start and end events using remote.
    switch(e.keyCode) {
      case 37: // LEFT arrow
        log('sendAdClick');
        sendAdClick();
        break;
      case 38: // UP arrow
        log('onVideoTouch');
        onVideoTouch();
        break;
      case 39: // RIGHT arrow
        log('sendPlaybackStart');
        sendPlaybackStart();
        break;
      case 40: // DOWN arrow
        log('sendPlaybackEnd');
        sendPlaybackEnd();
        break;
      case 10009: // RETURN button
        tizen.application.getCurrentApplication().exit();
        break;
      case 13: // OK button
        // Clear log.
        document.getElementById('logOutput').innerHTML = '';
        break;
      default:
        log('Key code : ' + e.keyCode);
        break;
    }
  });
  generateNonce();
}

/**
 * Generates a nonce with sample arguments and logs it to the console.
 */
function generateNonce() {
  // The default value for `allowStorage` is false, but can be
  // changed once the appropriate consent has been gathered.
  const consentSettings = new goog.ctv.pal.ConsentSettings();
  consentSettings.allowStorage = getConsentToStorage();
  nonceLoader = new goog.ctv.pal.NonceLoader(consentSettings);
  const request = new goog.ctv.pal.NonceRequest();
  request.adWillAutoPlay = true;
  request.adWillPlayMuted = true;
  request.continuousPlayback = false;
  request.descriptionUrl = 'https://example.com';
  request.iconsSupported = true;
  request.playerType = 'Sample Player Type';
  request.playerVersion = '1.0';
  request.ppid = 'Sample PPID';
  request.sessionId = 'Sample SID';
  // Player support for VPAID 2.0, OMID 1.0, and SIMID 1.1
  request.supportedApiFrameworks = '2,7,9';
  request.url = 'https://developers.google.com/ad-manager/pal/ctv';
  request.videoHeight = 480;
  request.videoWidth = 640;

  managerPromise = nonceLoader.loadNonceManager(request);
  managerPromise
      .then(function(manager) {
        nonceManager = manager;
        log('Nonce generated: ' + manager.getNonce());
      });
}

/**
 * Informs PAL that an ad click has occurred. How this function is
 * called will vary depending on your ad implementation.
 */
function sendAdClick() {
  if (nonceManager) {
    nonceManager.sendAdClick();
  }
}

/**
 * Handles the user touching on the video element, passing it to PAL.
 * @param {!TouchEvent|!MouseEvent} touchEvent
 */
function onVideoTouch(touchEvent) {
  if (nonceManager) {
    nonceManager.sendAdTouch(touchEvent);
  }
}

/** Informs PAL that playback has started. */
function sendPlaybackStart() {
  if (nonceManager) {
    nonceManager.sendPlaybackStart();
  }
}

/** Informs PAL that playback has ended. */
function sendPlaybackEnd() {
  if (nonceManager) {
    nonceManager.sendPlaybackEnd();
  }
}

/**
 * Outputs log.
 * @param {string} msg
 */
function log(msg) {
  const logOutput = document.getElementById('logOutput');
  logOutput.innerHTML = msg + '<br>' + logOutput.innerHTML;
}

window.addEventListener("load", function(event) {
  init();
});
