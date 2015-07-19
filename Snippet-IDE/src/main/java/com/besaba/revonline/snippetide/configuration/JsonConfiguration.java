package com.besaba.revonline.snippetide.configuration;

import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.configuration.ConfigurationLoadFailedException;
import com.besaba.revonline.snippetide.api.configuration.ConfigurationSaveFailedException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class JsonConfiguration implements Configuration {
  private Map<String, ConfigurationSection> configurations = new HashMap<>();

  @Override
  public void load(@NotNull final InputStream inputStream) throws ConfigurationLoadFailedException {
    final JsonParser parser = new JsonParser();
    final JsonObject root = parser.parse(new InputStreamReader(inputStream)).getAsJsonObject();

    for (final Map.Entry<String, JsonElement> entry : root.entrySet()) {
      final String confName = entry.getKey();

      final ConfigurationSection configuration = new JsonConfigurationSection();
      configuration.load(new ByteArrayInputStream(
              entry
                  .getValue()
                  .toString()
                  .getBytes(StandardCharsets.UTF_8))
      );

      configurations.put(confName, configuration);
    }
  }

  @Override
  public void save(@NotNull final OutputStream outputStream) throws ConfigurationSaveFailedException {
    final Type type = new TypeToken<Map<String, Configuration>>() {}.getType();
    final OutputStreamWriter out = new OutputStreamWriter(outputStream);
    final JsonWriter writer = new JsonWriter(out);

    new GsonBuilder()
        .registerTypeAdapter(JsonConfigurationSection.class, new JsonConfigurationSectionSerializer())
        .create()
        .toJson(configurations, type, writer);

    try {
      writer.flush();
      out.flush();
    } catch (IOException e) {
      throw new ConfigurationSaveFailedException(e);
    }
  }

  @Override
  @NotNull
  public <T> Optional<T> get(@NotNull final String name) {
    final int dotSeparatorPosition = name.indexOf('.');

    if (dotSeparatorPosition == -1) {
      throw new IllegalArgumentException(name + " is not a correct user preference setting name");
    }

    final String section = name.substring(0, dotSeparatorPosition);
    final String element = name.substring(dotSeparatorPosition + 1);

    if (!configurations.containsKey(section)) {
      throw new IllegalArgumentException("Section " + section + " doesn't exists.");
    }

    return configurations.get(section).get(element);
  }

  @NotNull
  @Override
  public OptionalInt getAsInt(@NotNull final String name) {
    final Optional<String> optional = get(name);
    return !optional.isPresent() ? OptionalInt.empty() : OptionalInt.of(Integer.parseInt(optional.get()));
  }

  @NotNull
  @Override
  public OptionalDouble getAsDouble(@NotNull final String name) {
    final Optional<String> optional = get(name);
    return !optional.isPresent() ? OptionalDouble.empty() : OptionalDouble.of(Double.parseDouble(optional.get()));
  }

  @NotNull
  @Override
  public OptionalLong getAsLong(@NotNull final String name) {
    final Optional<String> optional = get(name);
    return !optional.isPresent() ? OptionalLong.empty() : OptionalLong.of(Long.parseLong(optional.get()));
  }

  @NotNull
  @Override
  public Optional<String> getAsString(@NotNull final String name) {
    return get(name);
  }

  @NotNull
  @Override
  public Optional<Boolean> getAsBoolean(@NotNull final String name) {
    final Optional<String> optional = get(name);
    return !optional.isPresent() ? Optional.empty() : Optional.of(Boolean.parseBoolean(optional.get()));
  }

  @NotNull
  @Override
  public Optional<Float> getAsFloat(@NotNull final String name) {
    final Optional<String> optional = get(name);
    return !optional.isPresent() ? Optional.empty() : Optional.of(Float.parseFloat(optional.get()));
  }

  @Override
  public Optional<String[]> getAsArray(@NotNull final String name) {
    return get(name);
  }

  @Override
  public boolean remove(@NotNull final String name) {
    final int dotSeparatorPosition = name.indexOf('.');

    if (dotSeparatorPosition == -1) {
      return removeSection(name);
    }

    final String section = name.substring(0, dotSeparatorPosition);
    final String element = name.substring(dotSeparatorPosition + 1);

    if (!configurations.containsKey(section)) {
      throw new IllegalArgumentException("Section " + section + " doesn't exists.");
    }

    return configurations.get(section).remove(element);
  }

  private boolean removeSection(final String name) {
    return configurations.remove(name) != null;
  }

  /**
   * Sets the value of a section. The name
   * is composed of: sectionName.entryName
   *
   * If the section doesn't exists it will
   * be created.
   *
   * If the entryName doesn't exists it will
   * be created.
   *
   * @param name The name of the setting
   * @param value The value to save
   * @param <T> The type of the value
   * @throws IllegalArgumentException If the name passed is not of the format: sectionName.entryName
   */
  @Override
  public <T> void set(@NotNull final String name,
                      @NotNull final T value) {
    final int dotSeparatorPosition = name.indexOf('.');

    if (dotSeparatorPosition == -1) {
      throw new IllegalArgumentException(name + " is not a correct user preference setting name");
    }

    final String sectionName = name.substring(0, dotSeparatorPosition);
    final String element = name.substring(dotSeparatorPosition + 1);

    ConfigurationSection section = configurations.get(sectionName);

    if (section == null) {
      section = new JsonConfigurationSection();

      configurations.put(sectionName, section);
    }

    section.set(element, value);
  }
}
