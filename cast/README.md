# Cast PAL Basic Sample App

This is a sample Cast custom web receiver app for testing Cast PAL. Follow the
below instructions to set up the app, assuming your laptop and Cast device are
on the same WiFi network and can connect to each other

1.  [Set up your Cast device for development]

1.  [Register a new Cast application]

    1.  Follow the instructions for a "Custom Receiver".
    1.  For the "Receiver Application URL" field, enter
        `http://[YOUR.LAPTOP.LOCAL.IP.ADDRESS]:8000/app.html`.
    1.  Don't bother with the steps necessary to publish the application.
    1.  Copy the application ID for later.

1.  Start a development server on your laptop.

    1.  Run `python3 -m http.server 8000` in this folder, to start a simple
        static server.
    1.  Test that everything is working properly by navigating to
        `http://[YOUR.LAPTOP.LOCAL.IP.ADDRESS]:8000/app.html` in your laptop
        browser. You should see the receiver app (it will not work properly
        though since it's not running on Cast).

1.  Navigate to [Cactool] in your laptop browser to start the custom receiver
    app on your Cast device.

    1.  Paste the application ID into the "Receiver App ID" field in the "Cast
        Connect & Logger Options" tab.
    1.  If your Cast device is recognized by your laptop (i.e. same Wifi network
        and the device was properly registed for development), you should see a
        Cast icon in the top left of the page by the logo. Consult the
        [Cactool documentation] if you're not seeing this.
    1.  Click this button to cast, and the receiver app should load onto the
        Cast device.
    1.  In your laptop browser, navigate to `chrome://inspect`, and you should
        be able to inspect the Cast device to see console messages and network
        requests.

1.  Load a media object in [Cactool] to trigger a nonce request.

    1.  In [Cactool], go to the "Load Media" tab, and in the "Custom Load
        Request" panel, select the "LOAD" request type radio button.
    1.  In the textarea containing the custom load request JSON, paste the
        following object:```
        {
            "media": {
                "contentId": "bbb",
                "contentUrl": "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/mp4/BigBuckBunny.mp4",
                "entity": "myapp://playlist/1",
                "streamType": "BUFFERED",
                "vmapAdsRequest": {
                    "adTagURL": "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpremidpostpod&ciu_szs=300x250&gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&cmsid=496&vid=short_onecue&correlator="
                }
            }
        }
        ```
    1.  Click the "Send Request" button, and you should shortly see Big Buck
        Bunny playing on the Cast device with periodic VMAP ads interspersed.
    1.  At the same time, in the Network tab, you should see that the
        `paln=[NONCE.GOES.HERE]` URL parameter was attached to the ad request.

1.  Observe and interact with playback to trigger PAL lifecycle events.

    1.  Once playback has started, you should see a ping for the `playbackStart`
        event.
    1.  In [Cactool], go to the "Media Control" tab, and pause, resume, and
        seek. These interactions should trigger pings for the `adTouch` event.
    1.  When playback ends (from error, quitting, or finishing), you should see
        a ping for the `playbackEnd` event.

[Set up your Cast device for development]: https://developers.google.com/cast/docs/registration#devices
[Cast Developer Console]: https://cast.google.com/publish/#/overview
[Charles Proxy]: https://sites.google.com/corp/google.com/charles-proxy/charles-proxy-home
[Register a new Cast application]:https://developers.google.com/cast/docs/registration
[SrcFS on Mac]: https://g3doc.corp.google.com/devtools/citc/g3doc/mac.md
[Cactool]: https://casttool.appspot.com/cactool/
[Cactool documentation]: https://developers.google.com/cast/docs/debugging/cac_tool
