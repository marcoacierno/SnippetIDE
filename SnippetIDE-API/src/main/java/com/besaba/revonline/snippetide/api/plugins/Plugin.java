package com.besaba.revonline.snippetide.api.plugins;

import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import com.google.common.collect.ImmutableList;

import java.nio.file.Path;
import java.util.List;

/**
 *
 *
 * Two plugins are equals if they have the same name (case-insensitive).
 */
public class Plugin {
  private final String name;
  private final String description;
  private final Version version;
  private final Version minIdeVersion;
  private final String[] authors;
  private final ImmutableList<Language> languages;
  private final ImmutableList<ShareService> shareServices;
  private final int pluginId;
  private final Path location;
  private final boolean enabled;

  public Plugin(final String name, final String description, final Version version, final Version minIdeVersion, final String[] authors, final List<Language> languages, final ImmutableList<ShareService> shareServices, final Path location, final boolean enabled) {
    this.name = name;
    this.description = description;
    this.version = version;
    this.minIdeVersion = minIdeVersion;
    this.authors = authors;
    this.shareServices = shareServices;
    this.location = location;
    this.enabled = enabled;
    this.languages = ImmutableList.copyOf(languages);
    this.pluginId = name.hashCode();
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Path getLocation() {
    return location;
  }

  public int getPluginId() {
    return pluginId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Version getVersion() {
    return version;
  }

  public Version getMinIdeVersion() {
    return minIdeVersion;
  }

  public ImmutableList<Language> getLanguages() {
    return languages;
  }

  public ImmutableList<ShareService> getShareServices() {
    return shareServices;
  }

  public String[] getAuthors() {
    return authors.clone();
  }

  public boolean supports(final Version ideVersion) {
    return ideVersion.compareTo(minIdeVersion) >= 0;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Plugin)) {
      return false;
    }

    return ((Plugin)obj).name.toLowerCase().equals(name.toLowerCase());
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
