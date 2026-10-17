// Copyright 2010-2018, Google Inc.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.mozc.android.inputmethod.japanese;

import org.mozc.android.inputmethod.japanese.protobuf.ProtoCommands.Request;

import com.google.common.base.Preconditions;

import java.io.Closeable;
import java.io.IOException;
/**
 * Utility class
 *
 */
public final class MozcUtil {

  /**
   * Simple interface to use mock of TelephonyManager for testing purpose.
   */
  public interface TelephonyManagerInterface {
      public String getNetworkOperator();
  }

  // Tag for logging.
  // This constant value affects only the logs printed by Java layer.
  // If you want to change the tag name, see also kProductPrefix in base/const.h.
  public static final String LOGTAG = "Mozc";

  public static void setSoftwareKeyboardRequest(Request.Builder builder) {
      builder.setMixedConversion(true)
              .setZeroQuerySuggestion(true)
              .setUpdateInputModeFromSurroundingText(false)
              .setAutoPartialSuggestion(true);
  }

  /**
   * Simple utility to close {@link Closeable} instance.
   * <p>
   * A typical usage is as follows:
   * <pre>{@code
   *   Closeable stream = ...;
   *   boolean succeeded = false;
   *   try {
   *     // Read data from stream here.
   *     ...
   *
   *     succeeded = true;
   *   } finally {
   *     close(stream, !succeeded);
   *   }
   * }</pre>
   *
   * @param closeable
   * @param ignoreException
   * @throws IOException
   */
  public static void close(Closeable closeable, boolean ignoreException) throws IOException {
      Preconditions.checkNotNull(closeable);
      try {
          closeable.close();
      } catch (IOException e) {
          if (!ignoreException) {
              throw e;
          }
      }
  }
  /**
   * If value &gt;= max returns max. If value &lt;= min returns min. Otherwise returns value.
   */
  public static int clamp(int value, int min, int max) {
    return Math.max(Math.min(value, max), min);
  }
}
