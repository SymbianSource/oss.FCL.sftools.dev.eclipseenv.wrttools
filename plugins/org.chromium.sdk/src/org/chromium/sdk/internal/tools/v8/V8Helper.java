// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.sdk.internal.tools.v8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.chromium.sdk.CallbackSemaphore;
import org.chromium.sdk.SyncCallback;
import org.chromium.sdk.JsValue.Type;
import org.chromium.sdk.internal.DataWithRef;
import org.chromium.sdk.internal.DebugSession;
import org.chromium.sdk.internal.FunctionAdditionalProperties;
import org.chromium.sdk.internal.JsDataTypeUtil;
import org.chromium.sdk.internal.PropertyHoldingValueMirror;
import org.chromium.sdk.internal.PropertyReference;
import org.chromium.sdk.internal.ScopeMirror;
import org.chromium.sdk.internal.ScriptImpl;
import org.chromium.sdk.internal.ScriptManager;
import org.chromium.sdk.internal.SubpropertiesMirror;
import org.chromium.sdk.internal.ValueLoadException;
import org.chromium.sdk.internal.ValueMirror;
import org.chromium.sdk.internal.protocol.CommandResponse;
import org.chromium.sdk.internal.protocol.FrameObject;
import org.chromium.sdk.internal.protocol.ScopeRef;
import org.chromium.sdk.internal.protocol.SuccessCommandResponse;
import org.chromium.sdk.internal.protocol.data.FunctionValueHandle;
import org.chromium.sdk.internal.protocol.data.ObjectValueHandle;
import org.chromium.sdk.internal.protocol.data.PropertyObject;
import org.chromium.sdk.internal.protocol.data.RefWithDisplayData;
import org.chromium.sdk.internal.protocol.data.ScriptHandle;
import org.chromium.sdk.internal.protocol.data.ValueHandle;
import org.chromium.sdk.internal.protocolparser.JsonProtocolParseException;
import org.chromium.sdk.internal.tools.v8.request.ContextlessDebuggerMessage;
import org.chromium.sdk.internal.tools.v8.request.DebuggerMessageFactory;
import org.chromium.sdk.internal.tools.v8.request.ScriptsMessage;

/**
 * A helper class for performing complex V8-related operations.
 */
public class V8Helper {

  public V8Helper(DebugSession debugSession) {
  }

  public interface ScriptLoadCallback {
    void success();
    void failure(String message);
  }

  /**
   * Loads all scripts and stores them in ScriptManager.
   * @param callback to invoke when the script reloading has completed
   * @param syncCallback to invoke after callback whether it normally returned or threw an exception
   */
  public static void reloadAllScriptsAsync(final DebugSession debugSession,
      final ScriptLoadCallback callback, SyncCallback syncCallback) {
    reloadScriptAsync(debugSession, null, callback, syncCallback);
  }

  /**
   * Loads specified scripts or all existing scripts and stores them in ScriptManager.
   * @param ids ids of requested scripts or null for all scripts
   * @param callback to invoke when the script reloading has completed
   * @param syncCallback to invoke after callback, regardless of whether it has returned normally
   *        or thrown an exception
   */
  public static void reloadScriptAsync(final DebugSession debugSession, final List<Long> ids,
      final ScriptLoadCallback callback, SyncCallback syncCallback) {
    ContextlessDebuggerMessage message = DebuggerMessageFactory.scripts(ids, true);
    if (ids == null) {
      message = DebuggerMessageFactory.scripts(ScriptsMessage.SCRIPTS_NORMAL, true);
    } else {
      message = DebuggerMessageFactory.scripts(ids, true);
    }
    debugSession.sendMessageAsync(
        message,
        true,
        new V8CommandCallbackBase() {
          @Override
          public void failure(String message) {
            if (callback != null) {
              callback.failure(message);
            }
          }

          @Override
          public void success(SuccessCommandResponse successResponse) {
            List<ScriptHandle> body;
            try {
              body = successResponse.getBody().asScripts();
            } catch (JsonProtocolParseException e) {
              throw new RuntimeException(e);
            }
            ScriptManager scriptManager = debugSession.getScriptManager();
            for (int i = 0; i < body.size(); ++i) {
              ScriptHandle scriptHandle = body.get(i);
              if (ChromeDevToolSessionManager.JAVASCRIPT_VOID.equals(scriptHandle.source())) {
                continue;
              }
              Long id = V8ProtocolUtil.getScriptIdFromResponse(scriptHandle);
              ScriptImpl scriptById = scriptManager.findById(id);
              if (scriptById == null) {
                scriptManager.addScript(scriptHandle, successResponse.getRefs());
              } else {
                // A scrupulous refactoring note:
                // do not call setSource in a legacy case, when ids parameter is null.
                if (ids != null) {
                  scriptById.setSource(scriptHandle.source());
                }
              }
            }
            if (callback != null) {
              callback.success();
            }
          }
        },
        syncCallback);
  }

  /**
   * Gets all resolved locals for the call frame, caches scripts and objects in
   * the scriptManager and handleManager.
   *
   * @param frame to get the data for
   * @return the mirrors corresponding to the frame locals
   */
  public static List<PropertyReference> computeLocals(FrameObject frame) {
    List<PropertyObject> args = frame.getArguments();
    List<PropertyObject> locals = frame.getLocals();

    int maxLookups = args.size() + locals.size() + 1 /* "this" */;

    List<PropertyReference> localRefs = new ArrayList<PropertyReference>(maxLookups);

    {
      // Receiver ("this")
      RefWithDisplayData receiverObject = frame.getReceiver().asWithDisplayData();
      V8ProtocolUtil.putMirror(localRefs, receiverObject,
          V8ProtocolUtil.PropertyNameGetter.THIS);
    }

    // Arguments
    for (int i = 0; i < args.size(); i++) {
      PropertyObject arg = args.get(i);
      V8ProtocolUtil.putMirror(localRefs, arg, V8ProtocolUtil.PropertyNameGetter.SUBPROPERTY);
    }

    // Locals
    for (int i = 0; i < locals.size(); i++) {
      PropertyObject local = locals.get(i);
      V8ProtocolUtil.putMirror(localRefs, local, V8ProtocolUtil.PropertyNameGetter.SUBPROPERTY);
    }

    return localRefs;
  }

  public static List<ScopeMirror> computeScopes(FrameObject frame) {
    List<ScopeRef> scopes = frame.getScopes();

    final List<ScopeMirror> result = new ArrayList<ScopeMirror>(scopes.size());

    for (int i = 0; i < scopes.size(); i++) {
      ScopeRef scope = scopes.get(i);
      int type = (int) scope.type();
      int index = (int) scope.index();

      result.add(new ScopeMirror(type, index));
    }

    return result;
  }

  public static PropertyReference computeReceiverRef(FrameObject frame) {
    RefWithDisplayData receiverObject = frame.getReceiver().asWithDisplayData();
    return V8ProtocolUtil.extractProperty(receiverObject,
        V8ProtocolUtil.PropertyNameGetter.THIS);
  }

  /**
   * Constructs a ValueMirror given a V8 debugger object specification.
   *
   * @param jsonValue containing the object specification from the V8 debugger
   * @return a {@link PropertyHoldingValueMirror} instance, containing data
   *         from jsonValue; not null
   */
  public static PropertyHoldingValueMirror createMirrorFromLookup(ValueHandle valueHandle) {
    String text = valueHandle.text();
    if (text == null) {
      throw new ValueLoadException("Bad lookup result");
    }
    String typeString = valueHandle.type();
    String className = valueHandle.className();
    Type type = JsDataTypeUtil.fromJsonTypeAndClassName(typeString, className);
    if (type == null) {
      throw new ValueLoadException("Bad lookup result: type field not recognized: " + typeString);
    }
    return createMirrorFromLookup(valueHandle, type);
  }

  /**
   * Constructs a {@link ValueMirror} given a V8 debugger object specification if it's possible.
   * @return a {@link ValueMirror} instance, containing data
   *         from {@code jsonValue}; or {@code null} if {@code jsonValue} is not a handle
   */
  public static ValueMirror createValueMirrorOptional(DataWithRef handleFromProperty) {
    RefWithDisplayData withData = handleFromProperty.getWithDisplayData();
    if (withData == null) {
      return null;
    }
    return createValueMirror(withData);
  }
  public static ValueMirror createValueMirrorOptional(ValueHandle valueHandle) {
    return createValueMirror(valueHandle);
  }

  private static ValueMirror createValueMirror(ValueHandle valueHandle) {
    String className = valueHandle.className();
    Type type = JsDataTypeUtil.fromJsonTypeAndClassName(valueHandle.type(), className);
    if (type == null) {
      type = Type.TYPE_OBJECT;
    }
    String text = valueHandle.text();
    return createMirrorFromLookup(valueHandle, type).getValueMirror();
  }

  private static ValueMirror createValueMirror(RefWithDisplayData jsonValue) {
    String className = jsonValue.className();
    Type type = JsDataTypeUtil.fromJsonTypeAndClassName(jsonValue.type(), className);
    if (type == null) {
      throw new ValueLoadException("Bad value object");
    }
    { // try another format
      if (Type.isObjectType(type)) {
        int refId = (int) jsonValue.ref();
        return ValueMirror.createObjectUnknownProperties(refId, type, className);
      } else {
        // try another format
        Object valueObj = jsonValue.value();
        String valueStr;
        if (valueObj == null) {
          valueStr = jsonValue.type(); // e.g. "undefined"
        } else {
          valueStr = valueObj.toString();
        }
        return ValueMirror.createScalar(valueStr, type, className).getValueMirror();
      }
    }
  }

  private static PropertyHoldingValueMirror createMirrorFromLookup(ValueHandle valueHandle,
      Type type) {
    if (Type.isObjectType(type)) {
      ObjectValueHandle objectValueHandle = valueHandle.asObject();
      int refId = (int) valueHandle.handle();
      SubpropertiesMirror subpropertiesMirror;
      if (type == Type.TYPE_FUNCTION) {
        FunctionValueHandle functionValueHandle = objectValueHandle.asFunction();
        subpropertiesMirror = new SubpropertiesMirror.FunctionValueBased(functionValueHandle,
            FUNCTION_PROPERTY_FACTORY2);
      } else {
        subpropertiesMirror =
          new SubpropertiesMirror.ObjectValueBased(objectValueHandle, null);
      }
      return ValueMirror.createObject(refId, subpropertiesMirror, type, valueHandle.className());
    } else {
      return ValueMirror.createScalar(valueHandle.text(), type, valueHandle.className());
    }
  }

  // TODO(peter.rybin): Get rid of this monstrosity once we switched to type JSON interfaces.
  private static final
      SubpropertiesMirror.JsonBased.AdditionalPropertyFactory<FunctionValueHandle>
      FUNCTION_PROPERTY_FACTORY2 =
      new SubpropertiesMirror.JsonBased.AdditionalPropertyFactory<FunctionValueHandle>() {
    public Object createAdditionalProperties(FunctionValueHandle jsonWithProperties) {
      Long pos = jsonWithProperties.position();
      if (pos == null) {
        pos = Long.valueOf(FunctionAdditionalProperties.NO_POSITION);
      }
      Long scriptId = jsonWithProperties.scriptId();
      if (scriptId == null) {
        scriptId = Long.valueOf(FunctionAdditionalProperties.NO_SCRIPT_ID);
      }
      return new FunctionAdditionalProperties(pos.intValue(), scriptId.intValue());
    }
  };

  public static <MESSAGE, RES, EX extends Exception> RES callV8Sync(
      V8CommandSender<MESSAGE, EX> commandSender, MESSAGE message,
      V8BlockingCallback<RES> callback) throws EX {
    return callV8Sync(commandSender, message, callback,
        CallbackSemaphore.OPERATION_TIMEOUT_MS);
  }

  public static <MESSAGE, RES, EX extends Exception> RES callV8Sync(
      V8CommandSender<MESSAGE, EX> commandSender,
      MESSAGE message, final V8BlockingCallback<RES> callback, long timeoutMs) throws EX {
    CallbackSemaphore syncCallback = new CallbackSemaphore();
    final Exception [] exBuff = { null };
    // A long way of creating buffer for generic type without warnings.
    final List<RES> resBuff = new ArrayList<RES>(Collections.nCopies(1, (RES)null));
    V8CommandProcessor.V8HandlerCallback callbackWrapper =
        new V8CommandProcessor.V8HandlerCallback() {
      public void failure(String message) {
        exBuff[0] = new Exception("Failure: " + message);
      }

      public void messageReceived(CommandResponse response) {
        RES result = callback.messageReceived(response);
        resBuff.set(0, result);
      }
    };
    commandSender.sendV8CommandAsync(message, true, callbackWrapper, syncCallback);

    boolean waitRes;
    try {
      waitRes = syncCallback.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
    } catch (RuntimeException e) {
      throw new CallbackException(e);
    }

    if (!waitRes) {
      throw new CallbackException("Timeout");
    }

    if (exBuff[0] != null) {
      throw new CallbackException(exBuff[0]);
    }

    return resBuff.get(0);
  }

  /**
   * Special kind of exceptions for problems in receiving or waiting for the answer.
   * Clients may try to catch it.
   */
  public static class CallbackException extends RuntimeException {
    CallbackException() {
    }
    CallbackException(String message, Throwable cause) {
      super(message, cause);
    }
    CallbackException(String message) {
      super(message);
    }
    CallbackException(Throwable cause) {
      super(cause);
    }
  }
}
