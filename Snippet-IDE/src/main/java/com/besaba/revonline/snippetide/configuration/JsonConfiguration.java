package com.besaba.revonline.snippetide.configuration;

import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.configuration.ConfigurationLoadFailedException;
import com.besaba.revonline.snippetide.api.configuration.ConfigurationSaveFailedException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JsonConfiguration implements Configuration {
  @NotNull
  private ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();

  @Override
  public void load(@NotNull final InputStream inputStream) throws ConfigurationLoadFailedException {
    values = new ConcurrentHashMap<>();

    final JsonParser parser = new JsonParser();
    final JsonObject root = parser.parse(new InputStreamReader(inputStream)).getAsJsonObject();

    for (final Map.Entry<String, JsonElement> entry : root.entrySet()) {
      final String key = entry.getKey();
      final JsonElement value = entry.getValue();

      if (value.isJsonPrimitive()) {
        final JsonPrimitive primitive = value.getAsJsonPrimitive();
        values.put(key, primitive.getAsString());
      } else if (value.isJsonNull()) {
        throw new IllegalArgumentException("null is not a valid parameter");
      } else if (value.isJsonArray()) {
        final JsonArray array = value.getAsJsonArray();
        final String[] result = new String[array.size()];

        for (int i = 0; i < array.size(); i++) {
          result[i] = array.get(i).getAsString();
        }

        values.put(key, result);
      } else if (value.isJsonObject()) {
        final JsonObject object = value.getAsJsonObject();

        final JsonConfiguration subSection = new JsonConfiguration();
        subSection.load(
            new ByteArrayInputStream(
                object
                    .toString()
                    .getBytes(StandardCharsets.UTF_8))
        );

        values.put(key, subSection);
      }
    }
  }

  @Override
  public void save(@NotNull final OutputStream outputStream) throws ConfigurationSaveFailedException {
    final Type type = new TypeToken<Map<String, Object>>() {}.getType();
    final OutputStreamWriter out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
    final JsonWriter writer = new JsonWriter(out);

    new GsonBuilder().registerTypeAdapter(JsonConfiguration.class, new JsonConfigurationSerializer())
        .create()
        .toJson(values, type, writer);

    try {
      writer.flush();
      out.flush();
    } catch (IOException ex) {
      throw new ConfigurationSaveFailedException(ex);
    }
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

  @NotNull
  @Override
  public <T> Optional<T> get(@NotNull final String name) {
    if (name.contains(".")) {
      return getSubsection(name);
    }

    final Object value = values.get(name);

    if (value instanceof JsonConfiguration) {
      final Map<Object, Object> values = new HashMap<>();

      final JsonConfiguration section = (JsonConfiguration) value;
      section.getValues().forEach((k, v) -> values.put(k, v.toString()));

      return Optional.of((T) values);
    }

    return Optional.ofNullable((T) value);
  }

  @NotNull
  private <T> Optional<T> getSubsection(final String name) {
    final int dotPosition = name.indexOf('.');

    if (dotPosition == -1) {
      throw new IllegalArgumentException(name + " is not a valid section query");
    }

    final String sectionName = name.substring(0, dotPosition);
    final String entry = name.substring(dotPosition + 1);

    final Object tempObject = values.get(sectionName);

    if (tempObject == null || !JsonConfiguration.class.isAssignableFrom(tempObject.getClass())) {
      return Optional.empty();
    }

    final JsonConfiguration subSection = (JsonConfiguration) tempObject;
    return subSection.get(entry);
  }

  @Override
  public <T> void set(@NotNull final String name,
                      @NotNull final T value) {
    if (name.contains(".")) {
      setSubsection(name, value);
      return;
    }

    if (Map.class.isAssignableFrom(value.getClass())) {
      final Map<?, ?> map = (Map<?, ?>) value;
      final JsonConfiguration subsectionFromMapValues = createSubsectionFromMapValues(map);
      values.put(name, subsectionFromMapValues);
    } else if (value.getClass().isArray()) {
      values.put(name, JsonConfigurationUtils.transformAnyArrayToStringArray(value));
    } else {
      values.put(name, value.toString());
    }
  }

  private JsonConfiguration createSubsectionFromMapValues(final Map<?, ?> map) {
    final JsonConfiguration configuration = new JsonConfiguration();
    map.forEach((key, value) -> configuration.set(key.toString(), value));
    return configuration;
  }

  private <T> void setSubsection(@NotNull final String name,
                                 @NotNull final T value) {
    final int dotPosition = name.indexOf('.');

    if (dotPosition == -1) {
      throw new IllegalArgumentException(name + " is not a valid query");
    }

    final String sectionName = name.substring(0, dotPosition);
    final String entry = name.substring(dotPosition + 1);

    final Object tempObject = values.get(sectionName);

    final JsonConfiguration subsection;

    if (tempObject == null || !JsonConfiguration.class.isAssignableFrom(tempObject.getClass())) {
      subsection = new JsonConfiguration();
      values.put(sectionName, subsection);
    } else {
      subsection = (JsonConfiguration) values.get(sectionName);
    }

    subsection.set(entry, value);
  }

  @Override
  public boolean remove(@NotNull final String name) {
    if (name.contains(".")) {
      return removeFromSubSection(name);
    }

    return values.remove(name) != null;
  }

  @Override
  public boolean isPresent(final String entry) {
    final int dotPosition = entry.indexOf('.');

    if (dotPosition != -1) {
      return isPresentSubsection(entry);
    }

    return values.get(entry) != null;
  }

  private boolean isPresentSubsection(final String value) {
    final int dotPosition = value.indexOf('.');

    final String sectionName = value.substring(0, dotPosition);
    final String entry = value.substring(dotPosition + 1);


    final Object tempObject = values.get(sectionName);

    if (tempObject == null || !JsonConfiguration.class.isAssignableFrom(tempObject.getClass())) {
      return false;
    }

    final JsonConfiguration subSection = (JsonConfiguration) tempObject;
    return subSection.isPresent(entry);
  }

  private boolean removeFromSubSection(@NotNull final String name) {
    final int dotPosition = name.indexOf('.');

    if (dotPosition == -1) {
      throw new IllegalArgumentException(name + " is not a valid query");
    }

    final String sectionName = name.substring(0, dotPosition);
    final String entry = name.substring(dotPosition + 1);

    final Object tempObject = values.get(sectionName);
    if (tempObject == null || !JsonConfiguration.class.isAssignableFrom(tempObject.getClass())) {
      return false;
    }

    final JsonConfiguration subSection = (JsonConfiguration) tempObject;
    return subSection.remove(entry);
  }

  @NotNull
  Map<String, Object> getValues() {
    return Collections.unmodifiableMap(values);
  }
}
