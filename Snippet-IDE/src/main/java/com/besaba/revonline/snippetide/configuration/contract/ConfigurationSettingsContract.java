package com.besaba.revonline.snippetide.configuration.contract;

public class ConfigurationSettingsContract {
  public static class Keymap {
    public static final String SECTION_NAME = "keymap";

    public static final String COMPILE_ENTRY = "compile";
    public static final String RUN_ENTRY = "run";

    public static final String COMPILE_QUERY = SECTION_NAME + "." + COMPILE_ENTRY;
    public static final String RUN_QUERY = SECTION_NAME + "." + RUN_ENTRY;
  }
}
