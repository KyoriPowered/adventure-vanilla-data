/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.data.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import net.minecraft.SharedConstants;

final class Context {
  private final String packageBase = "net.kyori.adventure.data";
  private final Path outputDirectory;

  Context(final Path outputDirectory) {
    this.outputDirectory = outputDirectory;
  }

  public String gameVersion() {
    return SharedConstants.getCurrentVersion().getName();
  }

  /**
   * Write the provided type to a file in the defined base package.
   *
   * @param spec type to write out to file
   * @throws IOException if thrown by javapoet
   */
  public void write(final TypeSpec spec) throws IOException {
    final var file = JavaFile.builder(this.packageBase, spec)
      .skipJavaLangImports(true)
      .build();

    file.writeTo(this.outputDirectory, StandardCharsets.UTF_8);
  }
}
