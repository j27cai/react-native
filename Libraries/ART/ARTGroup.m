/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

#import "ARTGroup.h"

@implementation ARTGroup

- (void)renderLayerTo:(CGContextRef)context
{
// TO-DO: Clipping rectangle

  for (ARTNode *node in self.subviews) {
    [node renderTo:context];
  }
}

@end
