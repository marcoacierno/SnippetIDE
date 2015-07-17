package com.besaba.revonline.snippetide.configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class JsonConfigurationSectionSerializer implements JsonSerializer<JsonConfigurationSection> {
  @Override
  public JsonElement serialize(final JsonConfigurationSection src,
                               final Type typeOfSrc,
                               final JsonSerializationContext context) {
    final JsonObject root = new JsonObject();

    for (final Map.Entry<String, Object> entry : src.getValues().entrySet()) {
      final Object value = entry.getValue();

      if (value.getClass().isArray()) {
        final JsonArray jsonArray = new JsonArray();
        final String[] elements = (String[]) value;

        for (final String element : elements) {
          jsonArray.add(new JsonPrimitive(element));
        }

        root.add(entry.getKey(), jsonArray);
      } else {
        root.add(entry.getKey(), new JsonPrimitive(value.toString()));
      }
    }

    return root;
  }
}
