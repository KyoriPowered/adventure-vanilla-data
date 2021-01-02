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

import com.squareup.javapoet.FieldSpec;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.lang.model.element.Modifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class KeybindGenerator implements Generator {

  @Override
  public String name() {
    return null;
  }

  @Override
  public void generate(final Context ctx) throws IOException {
    final var clazz = Types.utilityClass("VanillaKeyBindings", """
      Key binding IDs known by the vanilla <em>Minecraft: Java Edition</em> client, version $L.
      """, ctx.gameVersion());

    // Since Options needs quite a lot of initialization, we are just going to look at the bytecode for constants.
    final URL optionsClass = Options.class.getResource("Options.class");
    final List<String> knownKeyMappings = new ArrayList<>();
    try(final var in = optionsClass.openConnection().getInputStream()) {
      final var reader = new ClassReader(in);
      final var visitor = new FindConstructorVisitor(new FindKeybindNamesVisitor(knownKeyMappings::add));
      reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }

    // sort fields
    knownKeyMappings.sort(Comparator.naturalOrder());

    // and now generate a class
    for(final String key : knownKeyMappings) {
      clazz.addField(FieldSpec.builder(
        Types.KEYBIND_COMPONENT,
        keyIdToFieldName(key),
        Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer("$T.keybind($S)", Types.COMPONENT, key)
        .build());

    }

    ctx.write(clazz.build());
  }

  private static final String KEY_ID_PREFIX = "key.";
  private static final char UNDERSCORE = '_';

  static String keyIdToFieldName(final String keyName) {
    final StringBuilder builder = new StringBuilder(keyName.length());
    if(keyName.startsWith(KEY_ID_PREFIX)) {
      builder.append(keyName, KEY_ID_PREFIX.length(), keyName.length());
    } else {
      builder.append(keyName);
    }

    // Taken from org.spongepowered.configurate.util.NamingSchemes
    for(int i = 0; i < builder.length(); i++) {
      final int ch = builder.codePointAt(i);
      if(ch == '-' || ch == '.') { // not valid in Java identifiers
        if(i != 0 && i != builder.length() - 1) { // only convert actual separators
          builder.setCharAt(i, UNDERSCORE);
        }
      } else if(Character.isUpperCase(ch)) {
        builder.insert(i++, UNDERSCORE);
        final int lower = Character.toLowerCase(ch);
        if(Character.isBmpCodePoint(lower)) {
          builder.setCharAt(i, (char) lower);
        } else {
          builder.setCharAt(i++, Character.highSurrogate(lower));
          builder.setCharAt(i, Character.lowSurrogate(lower));
        }
      }
    }
    return builder.toString().toUpperCase(Locale.ROOT);
  }

  static final class FindConstructorVisitor extends ClassVisitor {
    private static final String CONSTRUCTOR = "<init>";
    private final MethodVisitor constructorVisitor;

    FindConstructorVisitor(final MethodVisitor constructorVisitor) {
      super(Opcodes.ASM9);
      this.constructorVisitor = constructorVisitor;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
      if(CONSTRUCTOR.equals(name)) {
        return this.constructorVisitor;
      } else {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
      }

    }
  }

  static final class FindKeybindNamesVisitor extends MethodVisitor {
    private static final Type KEY_MAPPING = Type.getType(KeyMapping.class);

    enum Expected {
      NEW_MAPPING,
      KEY_NAME
    }

    private final Consumer<String> handler;
    private Expected state = Expected.NEW_MAPPING;

    /**
     * Create a keybind name visitor.
     *
     * @param handler a callback that will receive every keybind name
     */
    FindKeybindNamesVisitor(final Consumer<String> handler) {
      super(Opcodes.ASM9);
      this.handler = handler;
    }

    /* (non-javadoc)
     * Example bytecode of an initializer:
     *
     * ALOAD 0
     * NEW net/minecraft/client/KeyMapping
     * DUP
     * LDC "key.chat"
     * BIPUSH 84
     * LDC "key.categories.multiplayer"
     * INVOKESPECIAL net/minecraft/client/KeyMapping.<init> (Ljava/lang/String;ILjava/lang/String;)V
     * PUTFIELD net/minecraft/client/Options.keyChat : Lnet/minecraft/client/KeyMapping;
     *
     * This visitor will emit values for the first LDC instruction after a NEW for a keyMapping.
     *
     * TODO: maybe add default values in (probably not worthwhile)
     */

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
      if(opcode == Opcodes.NEW
        && this.state == Expected.NEW_MAPPING
        && KEY_MAPPING.getInternalName().equals(type)) {
        this.state = Expected.KEY_NAME;
      }
      super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitLdcInsn(final Object value) {
      if(value instanceof String && this.state == Expected.KEY_NAME) {
        this.state = Expected.NEW_MAPPING;
        this.handler.accept((String) value);
      }
      super.visitLdcInsn(value);
    }
  }
}
