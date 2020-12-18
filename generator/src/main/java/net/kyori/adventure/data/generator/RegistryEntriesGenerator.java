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
import com.squareup.javapoet.FieldSpec;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.lang.model.element.Modifier;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

// Generates a constants file based on registry entries
class RegistryEntriesGenerator<V> implements Generator {
  private final String className;
  private final String classJd;
  private final Registry<V> registry;
  private final Function<V, String> localizationKeyGetter;
  private final Predicate<V> filter;

  RegistryEntriesGenerator(final String className, final Registry<V> registry, final String documentation, final Function<V, String> localizationKeyGetter) {
    this(className, registry, documentation, localizationKeyGetter, v -> true);
  }

  RegistryEntriesGenerator(final String className, final Registry<V> registry, final String documentation, final Function<V, String> localizationKeyGetter, final Predicate<V> filter) {
    this.className = className;
    this.classJd = documentation;
    this.registry = registry;
    this.localizationKeyGetter = localizationKeyGetter;
    this.filter = filter;
  }

  @Override
  public String name() {
    return "elements of registry " + this.registry.key().location();
  }

  @Override
  public void generate(final Context ctx) throws IOException {
    final var clazz = Types.utilityClass(this.className, this.classJd, SharedConstants.getCurrentVersion().getName());
    for(final var element : this.registry) {
      if(this.filter.test(element)) {
        clazz.addField(this.makeField(element));
      }
    }

    ctx.write(clazz.build());
  }

  private FieldSpec makeField(final V element) {
    final ResourceLocation id = this.registry.getKey(element);
    final String localizationKey = this.localizationKeyGetter.apply(element);
    return FieldSpec.builder(Types.KEYED_NAMED_HOLDER, Types.keyToFieldName(id.getPath()), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer("new $T($S, $S)", Types.KEYED_NAMED_HOLDER_IMPL, id.toString(), localizationKey)
      .build();
  }

}
