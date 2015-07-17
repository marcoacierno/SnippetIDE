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
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JsonConfigurationSection implements ConfigurationSection {
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
        throw new UnsupportedOperationException("Objects not supported yet");
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
    return Optional.ofNullable((T) values.get(name));
  }

  @Override
  public <T> void set(@NotNull final String name,
                      @Nullable final T value) {
    values.put(name, value);
  }

  @NotNull
  Map<String, Object> getValues() {
    return Collections.unmodifiableMap(values);
  }
}
