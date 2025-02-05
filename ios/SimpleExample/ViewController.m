#import "ViewController.h"

@import ProgrammaticAccessLibrary;

// UI constants.
static NSString *const PALTitleAlert = @"Programmatic Access Nonce";
static NSString *const PALTitleAlertOKAction = @"OK";

@interface ViewController () <PALNonceLoaderDelegate>
/** The nonce loader to use for nonce requests. */
@property(nonatomic) PALNonceLoader *nonceLoader;
/** The nonce manager result from the last successful nonce request. */
@property(nonatomic) PALNonceManager *nonceManager;
/** The view in which a video would play. In this sample, it is mocked for simplification. */
@property(nonatomic, weak) IBOutlet UIView *videoView;
@end

@implementation ViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  /**
   * The default value for 'allowStorage' and
   * 'directedForChildOrUnknownAge' is 'NO', but should be updated once the
   * appropriate consent has been gathered. Publishers should either
   * integrate with a CMP or use a different method to handle storage consent.
   */
  PALSettings *settings = [[PALSettings alloc] init];
  settings.allowStorage = YES;
  settings.directedForChildOrUnknownAge = NO;

  self.nonceLoader = [[PALNonceLoader alloc] initWithSettings:settings];
  self.nonceLoader.delegate = self;
}

#pragma mark - UI Callback methods

/**
 * Requests a new nonce manager with a request containing arbitrary test values like a (sane) user
 * might supply. Displays the nonce or error on success. This should be called once per stream.
 *
 * The PALNonceRequest parameters set here are example parameters.
 * You should set your parameters based on your own app characteristics.
 */
- (IBAction)requestNonceManager {
  PALNonceRequest *request = [[PALNonceRequest alloc] init];
  request.continuousPlayback = PALFlagOff;
  request.descriptionURL = [NSURL URLWithString:@"https://example.com/desc?key=val"];
  request.iconsSupported = YES;
  request.playerType = @"AwesomePlayer";
  request.playerVersion = @"4.2.1";
  request.PPID = @"123987456";
  request.videoPlayerHeight = 480;
  request.videoPlayerWidth = 640;
  request.willAdAutoPlay = PALFlagOn;
  request.willAdPlayMuted = PALFlagOff;
#if TARGET_OS_IOS
  // OM SDK is only available on iOS.
  request.OMIDPartnerName = @"SamplePartner";
  request.OMIDPartnerVersion = @"6.2.1";
#endif

  if (self.nonceManager) {
    // Detach the old nonce manager's gesture recognizer before destroying it.
    [self.videoView removeGestureRecognizer:self.nonceManager.gestureRecognizer];
    self.nonceManager = nil;
  }
  [self.nonceLoader loadNonceManagerWithRequest:request];
}

/** Reports an ad view for the current nonce manager, if not nil. */
- (IBAction)sendAdView {
  [self.nonceManager sendPlaybackStart];
}

/** Reports an ad click for the current nonce manager, if not nil. */
- (IBAction)sendAdClick {
  [self.nonceManager sendAdClick];
}

/** Displays the given message in an alert view for the user to inspect. */
- (void)presentMessage:(nonnull NSString *)message {
  UIAlertController *alert =
      [UIAlertController alertControllerWithTitle:PALTitleAlert
                                          message:message
                                   preferredStyle:UIAlertControllerStyleAlert];
  UIAlertAction *OKAction = [UIAlertAction actionWithTitle:PALTitleAlertOKAction
                                                     style:UIAlertActionStyleCancel
                                                   handler:nil];
  [alert addAction:OKAction];
  [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - PALNonceLoaderDelegate methods

- (void)nonceLoader:(PALNonceLoader *)nonceLoader
            withRequest:(PALNonceRequest *)request
    didLoadNonceManager:(PALNonceManager *)nonceManager {
  // Capture the created nonce manager and attach its gesture recognizer to the video view.
  self.nonceManager = nonceManager;
  [self.videoView addGestureRecognizer:self.nonceManager.gestureRecognizer];

  NSLog(@"Programmatic access nonce: %@", nonceManager.nonce);
  [self presentMessage:nonceManager.nonce];
}

- (void)nonceLoader:(PALNonceLoader *)nonceLoader
         withRequest:(PALNonceRequest *)request
    didFailWithError:(NSError *)error {
  NSLog(@"Error generating programmatic access nonce: %@", error);
  [self presentMessage:error.debugDescription];
}

@end
