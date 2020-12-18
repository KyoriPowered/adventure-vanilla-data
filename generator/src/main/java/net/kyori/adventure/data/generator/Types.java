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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.lang.model.element.Modifier;

final class Types {
  private static final Pattern ILLEGAL_FIELD_CHARACTERS = Pattern.compile("[.-]");

  /**
   * {@code KeyedAndNamedImpl(String idKey, String translationKey)}.
   */
  public static final TypeName KEYED_NAMED_HOLDER = ClassName.get("net.kyori.adventure.data", "KeyedAndNamed");

  /**
   * {@code KeyedAndNamedImpl(String idKey, String translationKey)}.
   */
  public static final TypeName KEYED_NAMED_HOLDER_IMPL = ClassName.get("net.kyori.adventure.data", "KeyedAndNamedImpl");

  private Types() {
  }

  /**
   * Create a utility class that is final and has a private constructor.
   *
   * @param name class name
   * @param classJd javadoc to apply to the class
   * @param args arguments for the Javadoc
   * @return a configured type builder
   */
  public static TypeSpec.Builder utilityClass(final String name, final String classJd, final Object... args) {
    return TypeSpec.classBuilder(name)
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addJavadoc(classJd, args)
      .addMethod(MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addCode("throw new $T();", AssertionError.class)
        .build());
  }

  static String keyToFieldName(final String key) {
    return ILLEGAL_FIELD_CHARACTERS.matcher(key.toUpperCase(Locale.ROOT)).replaceAll("_");
  }
}
