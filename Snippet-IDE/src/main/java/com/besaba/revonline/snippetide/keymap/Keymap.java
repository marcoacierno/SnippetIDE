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
          return (KeyCodeCombination) KeyCodeCombination.valueOf(combination);
        }
      });


  @Nullable
  public static Action match(@NotNull final KeyEvent event) {
    for (final Action action : Action.values()) {
      final KeyCodeCombination combination = getCombination(action);

      if (combination == null) {
        logger.info("action " + action + " seems to don't have an associated keymap");
        continue;
      }

      if (combination.match(event)) {
        return action;
      }
    }

    logger.debug("no association found for event: " + event);
    return null;
  }

  public static KeyCodeCombination getCombination(@NotNull final Action action) {
    try {
      return keymap.get(action.getSettingsEntry());
    } catch (ExecutionException e) {
      return null;
    }
  }

  public static void updateCombination(@NotNull final Action action, @NotNull final KeyCodeCombination newCombination) {
    keymap.put(action.getSettingsEntry(), newCombination);
    final Configuration configuration = IDEApplicationLauncher.getIDEApplication().getConfiguration();
    configuration.set(
        ConfigurationSettingsContract.Keymap.SECTION_NAME + "." + action.getSettingsEntry(),
        newCombination
    );
  }

  public static void invalidate(@NotNull final String key) {
    keymap.invalidate(key);
  }

  public static void invalidateAll() {
    keymap.invalidateAll();
  }
}
