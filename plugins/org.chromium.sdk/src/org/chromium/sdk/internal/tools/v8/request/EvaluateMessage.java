// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.sdk.internal.tools.v8.request;

import org.chromium.sdk.internal.tools.v8.DebuggerCommand;

/**
 * Represents an "evaluate" V8 request message.
 */
public class EvaluateMessage extends DebuggerMessage {

  /**
   * @param expression to evaluate
   * @param frame number (top is 0).
   * @param global nullable. Default is false
   * @param disableBreak nullable. Default is true
   */
  public EvaluateMessage(String expression, Integer frame,
      Boolean global, Boolean disableBreak) {
    super(DebuggerCommand.EVALUATE.value);
    putArgument("expression", expression);
    if (frame != null) {
      putArgument("frame", frame);
    }
    putArgument("global", global);
    putArgument("disable_break", disableBreak);
    putArgument("inlineRefs", Boolean.TRUE);
  }
}
