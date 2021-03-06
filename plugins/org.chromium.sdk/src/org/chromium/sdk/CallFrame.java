// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.sdk;

import java.util.Collection;
import java.util.List;

/**
 * An object that represents a browser JavaScript VM call frame.
 */
public interface CallFrame {

  /**
   * @return the variables known in this frame, including the receiver variable
   * @deprecated in favor of {@link #getVariableScopes()}
   */
  @Deprecated
  Collection<? extends JsVariable> getVariables();

  /**
   * @return the scopes known in this frame; ordered, innermost first, global scope last
   */
  List<? extends JsScope> getVariableScopes();

  /**
   * @return the receiver variable known in this frame
   */
  JsVariable getReceiverVariable();

  /**
   * @return the current line number in the Script corresponding to this frame
   *         (0-based) or {@code -1} if unknown
   * TODO(peter.rybin): consider returning absolute line number here, not in-script number.
   */
  int getLineNumber();

  /**
   * @return the start character position in the line corresponding to the
   *         current statement of this frame or {@code -1} if unknown
   */
  int getCharStart();

  /**
   * @return the end character position in the line corresponding to the current
   *         statement of this frame or {@code -1} if unknown
   */
  int getCharEnd();

  /**
   * @return the source script this call frame is associated with. {@code null}
   *         if no script is associated with the call frame (e.g. an exception
   *         could have been thrown in a native script)
   */
  Script getScript();

  /**
   * @return the name of the current function of this frame
   */
  String getFunctionName();

  /**
   * @return context for evaluating expressions in scope of this frame
   */
  JsEvaluateContext getEvaluateContext();
}
