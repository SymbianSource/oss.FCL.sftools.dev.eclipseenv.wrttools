// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.sdk.internal.tools.v8.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.chromium.sdk.Breakpoint;
import org.chromium.sdk.ExceptionData;
import org.chromium.sdk.internal.ContextBuilder;
import org.chromium.sdk.internal.DebugSession;
import org.chromium.sdk.internal.ExceptionDataImpl;
import org.chromium.sdk.internal.InternalContext;
import org.chromium.sdk.internal.InternalContext.ContextDismissedCheckedException;
import org.chromium.sdk.internal.protocol.BreakEventBody;
import org.chromium.sdk.internal.protocol.EventNotification;
import org.chromium.sdk.internal.protocol.data.SomeHandle;
import org.chromium.sdk.internal.protocol.data.ValueHandle;
import org.chromium.sdk.internal.protocolparser.JsonProtocolParseException;
import org.chromium.sdk.internal.tools.v8.BreakpointManager;
import org.chromium.sdk.internal.tools.v8.V8Helper;
import org.chromium.sdk.internal.tools.v8.V8Protocol;
import org.chromium.sdk.internal.tools.v8.request.DebuggerMessage;
import org.chromium.sdk.internal.tools.v8.request.DebuggerMessageFactory;

/**
 * Handles the suspension-related V8 command replies and events.
 */
public class BreakpointProcessor extends V8EventProcessor {

  /** The name of the "exception" object to report as a variable name. */
  private static final String EXCEPTION_NAME = "exception";

  public BreakpointProcessor(DebugSession debugSession) {
    super(debugSession);
  }

  @Override
  public void messageReceived(EventNotification eventMessage) {
    final boolean isEvent = true;
    if (isEvent) {
      String event = eventMessage.getEvent();
      DebugSession debugSession = getDebugSession();

      ContextBuilder contextBuilder = debugSession.getContextBuilder();

      ContextBuilder.ExpectingBreakEventStep step1 = contextBuilder.buildNewContext();

      InternalContext internalContext = step1.getInternalContext();

      BreakEventBody breakEventBody;
      try {
        breakEventBody = eventMessage.getBody().asBreakEventBody();
      } catch (JsonProtocolParseException e) {
        throw new RuntimeException(e);
      }

      ContextBuilder.ExpectingBacktraceStep step2;
      if (V8Protocol.EVENT_BREAK.key.equals(event)) {
        Collection<Breakpoint> breakpointsHit = getBreakpointsHit(eventMessage, breakEventBody);
        step2 = step1.setContextState(breakpointsHit, null);
      } else if (V8Protocol.EVENT_EXCEPTION.key.equals(event)) {
        ExceptionData exception = createException(eventMessage, breakEventBody,
            internalContext);
        step2 = step1.setContextState(Collections.<Breakpoint> emptySet(), exception);
      } else {
        contextBuilder.buildSequenceFailure();
        throw new RuntimeException();
      }

      processNextStep(step2);
    }
  }

  public void processNextStep(ContextBuilder.ExpectingBacktraceStep step2) {
    BacktraceProcessor backtraceProcessor = new BacktraceProcessor(step2);
    InternalContext internalContext = step2.getInternalContext();

    DebuggerMessage message = DebuggerMessageFactory.backtrace(null, null, true);
    try {
      // Command is not immediate because we are supposed to be suspended.
      internalContext.sendV8CommandAsync(message, false, backtraceProcessor, null);
    } catch (ContextDismissedCheckedException e) {
      // Can't happen -- we are just creating context, it couldn't have become invalid
      throw new RuntimeException(e);
    }
  }

  private Collection<Breakpoint> getBreakpointsHit(EventNotification response,
      BreakEventBody breakEventBody) {
    List<Long> breakpointIdsArray = breakEventBody.getBreakpoints();
    BreakpointManager breakpointManager = getDebugSession().getBreakpointManager();
    if (breakpointIdsArray == null) {
      // Suspended on step end.
      return Collections.<Breakpoint> emptySet();
    }
    Collection<Breakpoint> breakpointsHit = new ArrayList<Breakpoint>(breakpointIdsArray.size());
    for (int i = 0, size = breakpointIdsArray.size(); i < size; ++i) {
      Breakpoint existingBp = breakpointManager.getBreakpoint(breakpointIdsArray.get(i));
      if (existingBp != null) {
        breakpointsHit.add(existingBp);
      }
    }
    return breakpointsHit;
  }

  private ExceptionData createException(EventNotification response, BreakEventBody body,
      InternalContext internalContext) {
    List<SomeHandle> refs = response.getRefs();
    ValueHandle exception = body.getException();
    List<SomeHandle> handles = new ArrayList<SomeHandle>(refs.size() + 1);
    handles.addAll(refs);
    handles.add(exception.getSuper());
    internalContext.getHandleManager().putAll(handles);

    // source column is not exposed ("sourceColumn" in "body")
    String sourceText = body.getSourceLineText();

    return new ExceptionDataImpl(internalContext,
        V8Helper.createMirrorFromLookup(exception).getValueMirror(),
        EXCEPTION_NAME,
        body.isUncaught(),
        sourceText,
        exception.text());
  }

}
