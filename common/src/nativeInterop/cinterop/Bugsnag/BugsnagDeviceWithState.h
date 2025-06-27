//
//  BugsnagDeviceWithState.h
//  Bugsnag
//
//  Created by Jamie Lynch on 01/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <BugsnagDefines.h>
#import <BugsnagDevice.h>

/**
 * Stateful information set by the notifier about the device on which the event occurred can be
 * found on this class. These values can be accessed and amended if necessary.
 */
BUGSNAG_EXTERN
@interface BugsnagDeviceWithState : BugsnagDevice

/**
 * The number of free bytes of storage available on the device.
 *
 * Note: This field is no longer auto-populated on iOS, watchOS, or tvOS in order to
 * comply with Apple "grey API" guidelines. If you populate it manually, it will be reported.
 */
@property (strong, nullable, nonatomic) NSNumber *freeDisk;

/**
 * The number of free bytes of memory available on the device
 */
@property (strong, nullable, nonatomic) NSNumber *freeMemory;

/**
 * The orientation of the device when the event occurred
 */
@property (copy, nullable, nonatomic) NSString *orientation;

/**
 * The timestamp on the device when the event occurred
 */
@property (strong, nullable, nonatomic) NSDate *time;

@end
