package com.besaba.revonline.snippetide.internal.deserializers;

import com.besaba.revonline.snippetide.api.plugins.Version;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class VersionDeserializer implements JsonDeserializer<Version> {
  @Override
  public Version deserialize(final JsonElement json,
                             final Type typeOfT,
                             final JsonDeserializationContext context) throws JsonParseException {
    final String versionString = json.getAsString();
    return Version.parse(versionString);
  }
}
