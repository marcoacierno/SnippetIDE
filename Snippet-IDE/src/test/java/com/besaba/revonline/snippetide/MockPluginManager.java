package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.plugins.Version;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MockPluginManager implements PluginManager {
  private Plugin plugin
      = new Plugin("Mock", "Mock", Version.parse("0.1"), Version.parse("0.1"), new String[] {"Mock"}, Collections.singletonList(MockLanguage.INSTANCE));

  @NotNull
  @Override
  public Plugin loadPlugin(@NotNull final Path file) {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public Optional<Plugin> searchPluginByName(@NotNull final String pluginName) {
    return Optional.ofNullable(
        plugin.getName().toLowerCase().equals(pluginName.toLowerCase()) ? plugin : null
    );
  }

  @Override
  public long getPluginsCount() {
    return 1;
  }

  @NotNull
  @Override
  public List<Plugin> getPlugins() {
    return Collections.singletonList(plugin);
  }
}
