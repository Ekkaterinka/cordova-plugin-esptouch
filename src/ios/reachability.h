#import <Foundation/Foundation.h>
#import <SystemConfiguration/SystemConfiguration.h>
#import <netinet/in.h>


typedef enum {
    NotReachable = 0,
    ReachableViaWWAN, // this value has been swapped with ReachableViaWiFi for Cordova backwards compat. reasons
    ReachableViaWiFi  // this value has been swapped with ReachableViaWWAN for Cordova backwards compat. reasons
} NetworkStatus;

#define kReachabilityChangedNotification @"kNetworkReachabilityChangedNotification"

@interface ESPReachability : NSObject
{
    SCNetworkReachabilityRef reachabilityRef;
}

// reachabilityWithAddress- Use to check the reachability of a particular IP address.
+ (ESPReachability*)reachabilityWithAddress:(const struct sockaddr*)hostAddress;

// reachabilityForInternetConnection- checks whether the default route is available.
//  Should be used by applications that do not connect to a particular host
+ (ESPReachability*)reachabilityForInternetConnection;

- (NetworkStatus)currentReachabilityStatus;

@end
