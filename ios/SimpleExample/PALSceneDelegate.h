@import UIKit;

NS_ASSUME_NONNULL_BEGIN

/**
 * Scene delegate for Simple Example.
 *
 * Manages the application's UIWindow and root view controller lifecycle.
 */
@interface PALSceneDelegate : UIResponder <UIWindowSceneDelegate>

@property(nonatomic, strong, nullable) UIWindow *window;

@end

NS_ASSUME_NONNULL_END