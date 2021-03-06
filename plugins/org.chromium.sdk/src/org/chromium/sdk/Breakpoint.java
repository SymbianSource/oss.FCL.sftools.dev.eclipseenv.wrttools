// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.sdk;

/**
 * A breakpoint in the browser JavaScript virtual machine. The {@code set*}
 * method invocations will not take effect until
 * {@link #flush(org.chromium.sdk.JavascriptVm.BreakpointCallback)} is called.
 */
public interface Breakpoint {

  /**
   * Known breakpoint types.
   */
  enum Type {
    FUNCTION,
    SCRIPT_NAME,
    SCRIPT_ID
  }

  /**
   * This value is used when the corresponding parameter is absent.
   *
   * @see #getIgnoreCount()
   * @see #setIgnoreCount(int)
   * @see JavascriptVm#setBreakpoint
   */
  int EMPTY_VALUE = -1;

  /**
   * A breakpoint has this ID if it does not reflect an actual breakpoint in a
   * JavaScript VM debugger.
   */
  long INVALID_ID = -1;

  /**
   * @return the breakpoint type
   */
  Type getType();

  /**
   * @return the breakpoint ID as reported by the JavaScript VM debugger
   */
  long getId();

  /**
   * @return scriptName as reported by the JavaScript VM debugger; may be null
   */
  String getScriptName();

  /**
   * @return scriptId as reported by the JavaScript VM debugger; may be null
   */
  Long getScriptId();

  /**
   * Returns line number of the breakpoint. As source is changed (typically with LiveEdit feature,
   * and particularly by calling {@link UpdatableScript#setSourceOnRemote}) this value
   * may become stale. It gets updated when {@link JavascriptVm#listBreakpoints} asynchronous
   * method completes.
   *
   * @return 1-based line number in script source
   */
  long getLineNumber();

  /**
   * @return whether this breakpoint is enabled
   */
  boolean isEnabled();

  /**
   * Sets whether this breakpoint is enabled.
   *
   * @param enabled whether the breakpoint should be enabled
   */
  void setEnabled(boolean enabled);

  /**
   * @return ignore count for this breakpoint or {@code EMPTY_VALUE} if none
   */
  int getIgnoreCount();

  /**
   * Sets the ignore count for this breakpoint ({@code EMPTY_VALUE} to clear).
   *
   * @param ignoreCount the new ignored hits count to set
   */
  void setIgnoreCount(int ignoreCount);

  /**
   * @return breakpoint condition as plain JavaScript or {@code null} if none
   */
  String getCondition();

  /**
   * Sets the breakpoint condition as plain JavaScript ({@code null} to clear).
   *
   * @param condition the new breakpoint condition
   */
  void setCondition(String condition);

  /**
   * Removes the breakpoint from the JS debugger and invokes the
   * callback once the operation has finished. This operation does not require
   * a flush() invocation.
   *
   * @param callback to invoke once the operation result is available
   */
  void clear(JavascriptVm.BreakpointCallback callback, SyncCallback syncCallback);

  /**
   * Flushes the breakpoint parameter changes (set* methods) into the browser
   * and invokes the callback once the operation has finished. This method must
   * be called for the set* method invocations to take effect.
   *
   * @param callback to invoke once the operation result is available
   */
  void flush(JavascriptVm.BreakpointCallback callback, SyncCallback syncCallback);
}
