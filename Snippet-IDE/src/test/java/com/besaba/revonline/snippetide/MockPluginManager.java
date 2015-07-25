package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.plugins.Version;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MockPluginManager implements PluginManager {
  public static final Plugin plugin
      = new Plugin("Mock", "Mock", Version.parse("0.1"), Version.parse("0.1"), new String[] {"Mock"}, Collections.singletonList(MockLanguage.INSTANCE), ImmutableList.<ShareService>of());

  public static final Plugin pluginWithRandomHtmlInDescription
      = new Plugin("Html", "<html><head><title>Lol?</title></head><body>" +
      "<b>Description<br />world<script " +
      "src=\"https://ajax.googleapis.com/ajax/libs/angularjs/1.3.15/angular.min.js\" " +
      "type=\"text/javascript\"></script></b>",
      Version.parse("3.0"),
      Version.parse("0.1"),
      new String[] {"You", "Me", "Hello", "<b>World</b>"},
      Collections.emptyList(),
      ImmutableList.<ShareService>of());

  @NotNull
  @Override
  public Plugin loadPlugin(@NotNull final Path file, @NotNull final Version ideVersion) {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public Optional<Plugin> searchPluginByName(@NotNull final String pluginName) {
    return Optional.ofNullable(
        plugin.getName().toLowerCase().equals(pluginName.toLowerCase()) ? plugin :
            (
                pluginWithRandomHtmlInDescription.getName().toLowerCase().equals(pluginName.toLowerCase()) ?
                pluginWithRandomHtmlInDescription : null
            )
    );
  }

  @Override
  public long getPluginsCount() {
    return 2;
  }

  @NotNull
  @Override
  public List<Plugin> getPlugins() {
    return Arrays.asList(plugin, pluginWithRandomHtmlInDescription);
  }
}
