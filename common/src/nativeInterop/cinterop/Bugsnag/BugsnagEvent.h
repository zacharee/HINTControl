// The following are snippets from the Bugsnag Cocoa SDK used to generate Kotlin stubs.
//
// https://github.com/bugsnag/bugsnag-cocoa/blob/6bcd46f5f8dc06ac26537875d501f02b27d219a9/Bugsnag/include/Bugsnag/BugsnagEvent.h
//
// Copyright (c) 2012 Bugsnag, https://bugsnag.com/
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software
// is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.

#import <Foundation/Foundation.h>
#import <BugsnagError.h>
#import <BugsnagFeatureFlag.h>

typedef NS_ENUM(NSUInteger, BSGSeverity) {
    BSGSeverityError,
    BSGSeverityWarning,
    BSGSeverityInfo,
};

@interface BugsnagEvent : NSObject

@property (readwrite, nonatomic) BSGSeverity severity;
@property (readwrite, copy, nonnull, nonatomic) NSArray<BugsnagError *> *errors;
@property (readonly, strong, nonnull, nonatomic) NSArray<BugsnagFeatureFlag *> *featureFlags;
@property (readwrite, nonatomic) BOOL unhandled;

@end