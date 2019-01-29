/********* aliPushClient.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>


@implementation aliPushClient

- (void)initCloudPush {
  // SDK初始化
  [CloudPushSDK asyncInit:@"*****" appSecret:@"*****" callback:^(CloudPushCallbackResult *res) {
    if (res.success) {
        NSLog(@"Push SDK init success, deviceId: %@.", [CloudPushSDK getDeviceId]);
    } else {
      NSLog(@"Push SDK init failed, error: %@", res.error);
    }
  }];
}

  /**
  *    注册苹果推送，获取deviceToken用于推送
  *
  *    @param     application
  */
  - (void)registerAPNS:(UIApplication *)application {
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.0) {
      // iOS 8 Notifications
      [application registerUserNotificationSettings:
      [UIUserNotificationSettings settingsForTypes:
          (UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge)
      categories:nil]];
      [application registerForRemoteNotifications];
    }
    else {
    // iOS < 8 Notifications
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
        (UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound)];
    }
  }

  /*
  *  苹果推送注册成功回调，将苹果返回的deviceToken上传到CloudPush服务器
  */
  - (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [CloudPushSDK registerDevice:deviceToken withCallback:^(CloudPushCallbackResult *res) {
      if (res.success) {
          NSLog(@"Register deviceToken success.");
      } else {
          NSLog(@"Register deviceToken failed, error: %@", res.error);
      }
    }];
  }

  /*
  *  苹果推送注册失败回调
  */
  - (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"didFailToRegisterForRemoteNotificationsWithError %@", error);
  }
@end
