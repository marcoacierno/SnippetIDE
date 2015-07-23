package com.besaba.revonline.snippetide.configuration;

import com.google.gson.Gson;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class JsonConfigurationSection implements ConfigurationSection {
  @NotNull
  private ConcurrentMap<String, Object> values = new ConcurrentHashMap<>();

  @Override
  public void load(@NotNull final InputStream inputStream) {
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

        final JsonConfigurationSection subSection = new JsonConfigurationSection();
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
  public void save(@NotNull final OutputStream outputStream) throws IOException {
    final Type type = new TypeToken<Map<String, Object>>() {}.getType();
    final OutputStreamWriter out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
    final JsonWriter writer = new JsonWriter(out);

    new Gson().toJson(values, type, writer);

    writer.flush();
    out.flush();
  }

  @NotNull
  @Override
  public <T> Optional<T> get(@NotNull final String name) {
    if (name.contains(".")) {
      return getSubsection(name);
    }

    final Object value = values.get(name);

    if (value instanceof JsonConfigurationSection) {
      final Map<Object, Object> values = new HashMap<>();

      final JsonConfigurationSection section = (JsonConfigurationSection) value;
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

    if (tempObject == null || !JsonConfigurationSection.class.isAssignableFrom(tempObject.getClass())) {
      return Optional.empty();
    }

    final JsonConfigurationSection subSection = (JsonConfigurationSection) tempObject;
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
      final JsonConfigurationSection subsectionFromMapValues = createSubsectionFromMapValues(map);
      values.put(name, subsectionFromMapValues);
    } else if (value.getClass().isArray()) {
      values.put(name, JsonConfigurationUtils.transformAnyArrayToStringArray(value));
    } else {
      values.put(name, value.toString());
    }
  }

  private JsonConfigurationSection createSubsectionFromMapValues(final Map<?, ?> map) {
    final JsonConfigurationSection configuration = new JsonConfigurationSection();
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

    final JsonConfigurationSection subsection;

    if (tempObject == null || !JsonConfigurationSection.class.isAssignableFrom(tempObject.getClass())) {
      subsection = new JsonConfigurationSection();
      values.put(sectionName, subsection);
    } else {
      subsection = (JsonConfigurationSection) values.get(sectionName);
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

    if (tempObject == null || !JsonConfigurationSection.class.isAssignableFrom(tempObject.getClass())) {
      return false;
    }

    final JsonConfigurationSection subSection = (JsonConfigurationSection) tempObject;
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
    if (tempObject == null || !JsonConfigurationSection.class.isAssignableFrom(tempObject.getClass())) {
      return false;
    }

    final JsonConfigurationSection subSection = (JsonConfigurationSection) tempObject;
    return subSection.remove(entry);
  }

  @NotNull
  Map<String, Object> getValues() {
    return Collections.unmodifiableMap(values);
  }
}
