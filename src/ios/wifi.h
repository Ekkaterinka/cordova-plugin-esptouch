#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVPluginResult.h>
#import <CoreLocation/CoreLocation.h>
#import "reachability.h"
#import "ESPTools.h"

@interface wifi : CDVPlugin

- (void)checkLocation:(CDVInvokedUrlCommand*)command;
- (void)getConnectedInfo:(CDVInvokedUrlCommand*)command;

@end