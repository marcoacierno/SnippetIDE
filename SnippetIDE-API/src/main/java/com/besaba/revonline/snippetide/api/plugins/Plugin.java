package com.besaba.revonline.snippetide.api.plugins;

import com.besaba.revonline.snippetide.api.language.Language;
import com.google.common.collect.ImmutableList;

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

  public Plugin(final String name, final String description, final Version version, final Version minIdeVersion, final String[] authors, final List<Language> languages) {
    this.name = name;
    this.description = description;
    this.version = version;
    this.minIdeVersion = minIdeVersion;
    this.authors = authors;
    this.languages = ImmutableList.copyOf(languages);
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
