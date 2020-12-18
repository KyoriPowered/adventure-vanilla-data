/*
 * This file is part of adventure-vanilla-data, licensed under the MIT License.
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

import com.google.common.html.HtmlEscapers;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.squareup.javapoet.FieldSpec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.lang.model.element.Modifier;
import net.minecraft.locale.Language;

public class LocalizationGenerator implements Generator {

  @Override
  public String name() {
    return "localization";
  }

  @Override
  public void generate(final Context ctx) throws IOException {
    final var type = Types.utilityClass("Translations", """
        Localization strings available in the default resource pack of <em>Minecraft: Java Edition</em> version $L.
        """, ctx.gameVersion());

    // Language doesn't directly expose information, so we have to read the file ourself
    // The file is a single JSON object composed of string -> string entries, of key -> English value
    try(final var is = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
        final JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      reader.beginObject();
      while(reader.peek() != JsonToken.END_OBJECT) {
        final var key = reader.nextName();
        final var value = reader.nextString();
        type.addField(makeField(key, value));
      }
    }

    ctx.write(type.build());
  }

  private static FieldSpec makeField(final String key, final String defaultValue) {
    return FieldSpec.builder(String.class, Types.keyToFieldName(key), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer("$S", key)
      .build();
  }
}
