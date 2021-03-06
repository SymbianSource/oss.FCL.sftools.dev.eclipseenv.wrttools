// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.sdk;

import java.io.IOException;

/**
 * Abstraction of a remote JavaScript virtual machine which is embedded into
 * some application and accessed via TCP/IP connection to a port opened by
 * DebuggerAgent. Clients can use it to conduct debugging process.
 */
public interface StandaloneVm extends JavascriptVm {
  /**
   * Connects to the target VM.
   *
   * @param listener to report the debug events to
   * @throws IOException if there was a transport layer error
   * @throws UnsupportedVersionException if the SDK protocol version is not
   *         compatible with that supported by the browser
   */
  void attach(DebugEventListener listener) throws IOException, UnsupportedVersionException;

  /**
   * @return name of embedding application as it wished to name itself; might be null
   */
  String getEmbedderName();

  /**
   * @return version of V8 implementation, format is unspecified; must not be null if
   *         {@link StandaloneVm} has been attached
   */
  String getVmVersion();

  /**
   * @return message explaining why VM is detached; may be null
   */
  String getDisconnectReason();
}
