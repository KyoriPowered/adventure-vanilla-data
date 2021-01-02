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

import java.nio.file.Path;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * A generator that will output source code containing constants used in <em>Minecraft: Java Edition</em>.
 */
public final class GameDataGenerator {
  private static final System.Logger LOGGER = System.getLogger(GameDataGenerator.class.getName());

  private GameDataGenerator() {
  }

  /**
   * The entry point.
   *
   * @param args arguments, expected to be {@code <output directory> }
   */
  public static void main(final String[] args) {
    Bootstrap.bootStrap();
    LOGGER.log(System.Logger.Level.INFO, "Generating data for Minecraft version {0}", SharedConstants.getCurrentVersion().getName());

    // Create a generator context based on arguments
    final var outputDir = Path.of(args[0]);
    final var context = new Context(outputDir);

    // Prepare a set of generators
    final var generators = List.of(
      // new TranslationGenerator(), // This creates a 33k-line source file that is a large fraction of the output jar... let's expose different things instead.
      new RegistryEntriesGenerator<>("VanillaBlocks",
        Registry.BLOCK,
        "Block types present in <em>Minecraft: Java Edition</em> $L.",
        Block::getDescriptionId),
      new RegistryEntriesGenerator<>("VanillaItems",
        Registry.ITEM,
        """
          Item types present in <em>Minecraft: Java Edition</em> $L.
          
          <p>This list does not include items that have a block associated.</p>
          
          @see VanillaBlocks for block items
          """,
        Item::getDescriptionId,
        it -> !(it instanceof BlockItem)),
      new RegistryEntriesGenerator<>("VanillaEntities",
        Registry.ENTITY_TYPE,
        "Entity types present in <em>Minecraft: Java Edition</em> $L.",
        EntityType::getDescriptionId),
      new RegistryEntriesGenerator<>("VanillaSounds",
        Registry.SOUND_EVENT,
        "Sound events present in vanilla <em>Minecraft: Java Edition</em> $L.",
        ev -> "subtitles." + ev.getLocation().getPath()),
      new KeybindGenerator()
    );

    // Execute every generator
    boolean failed = false;
    for(final Generator generator : generators) {
      try {
        generator.generate(context);
      } catch(final Exception ex) {
        LOGGER.log(System.Logger.Level.ERROR, "An expected error occurred while generating " + generator.name() + " data", ex);
        failed = true;
      }
    }

    if(failed) {
      LOGGER.log(System.Logger.Level.INFO, "A failure occurred earlier in generating data. See your log for details.");
      System.exit(1);
    }
    // Success!
  }
}
