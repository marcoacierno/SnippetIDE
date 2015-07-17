package com.besaba.revonline.snippetide.keymap;

import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Keymap {
  private final static Logger logger = Logger.getLogger(Keymap.class);

  private static LoadingCache<String, KeyCodeCombination> keymap = CacheBuilder.newBuilder()
      .maximumSize(20)
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .build(new CacheLoader<String, KeyCodeCombination>() {
        @Override
        public KeyCodeCombination load(final String actionName) throws Exception {
          final Configuration configuration = IDEApplicationLauncher.getIDEApplication().getConfiguration();
          final Optional<String> combinationString = configuration.getAsString(
              ConfigurationSettingsContract.Keymap.SECTION_NAME + "." + actionName
          );

          if (!combinationString.isPresent()) {
            throw new IllegalArgumentException(actionName + " doesn't exists");
          }

          final String combination = combinationString.get();

          final int separatorPosition = combination.indexOf('|');
          final boolean hasSeparator = separatorPosition != -1;

          if (hasSeparator) {
            final Iterator<String> parts = Splitter.on('|').split(combination).iterator();

            final KeyCode code = KeyCode.getKeyCode(parts.next());
            final KeyCombination.ModifierValue shift = KeyCombination.ModifierValue.valueOf(parts.next());
            final KeyCombination.ModifierValue control = KeyCombination.ModifierValue.valueOf(parts.next());
            final KeyCombination.ModifierValue alt = KeyCombination.ModifierValue.valueOf(parts.next());
            final KeyCombination.ModifierValue meta = KeyCombination.ModifierValue.valueOf(parts.next());
            final KeyCombination.ModifierValue shortcut = KeyCombination.ModifierValue.valueOf(parts.next());

            return new KeyCodeCombination(code, shift, control, alt, meta, shortcut);
          } else {
            return new KeyCodeCombination(KeyCode.getKeyCode(combination));
          }
        }
      });


  @Nullable
  public static Action match(@NotNull final KeyEvent event) {
    for (final Action action : Action.values()) {
      try {
        final KeyCodeCombination combination = keymap.get(action.getSettingsEntry());

        if (combination == null) {
          logger.info("action " + action + " seems to don't have an associated keymap");
          continue;
        }

        if (combination.match(event)) {
          return action;
        }
      } catch (ExecutionException e) {
        logger.error("skipping " + action + " because lookup thrown an exception", e);
      }
    }

    logger.debug("no association found for event: " + event);
    return null;
  }

  public static void invalidate(@NotNull final String key) {
    keymap.invalidate(key);
  }

  public static void invalidateAll() {
    keymap.invalidateAll();
  }
}
