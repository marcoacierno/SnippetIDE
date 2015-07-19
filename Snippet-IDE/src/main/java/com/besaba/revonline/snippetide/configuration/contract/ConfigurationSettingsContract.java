package com.besaba.revonline.snippetide.configuration.contract;

import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConfigurationSettingsContract {
  public static class Keymap {
    public static final String SECTION_NAME = "keymap";

    public static final String COMPILE_ENTRY = "compile";
    public static final String RUN_ENTRY = "run";

    public static final String COMPILE_QUERY = SECTION_NAME + "." + COMPILE_ENTRY;
    public static final String RUN_QUERY = SECTION_NAME + "." + RUN_ENTRY;
  }

  public static class RunConfigurations {
    public static final String SECTION_NAME = "runconfigurations";

    public static String generateRunConfigurationsLanguageQuery(@NotNull final Plugin plugin,
                                                                @NotNull final Language language) {
      return SECTION_NAME + "." + plugin.getPluginId() + "." + language.getName().hashCode();
    }

    public static String generateLanguageDefaultRunConfigurationQuery(@NotNull final Plugin plugin,
                                                                      @NotNull final Language language) {
      return generateRunConfigurationsLanguageQuery(plugin, language) + ".default";
    }
  }
}
