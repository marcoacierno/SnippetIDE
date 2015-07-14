package com.besaba.revonline.snippetide.plugins;

import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.plugins.UnableToLoadPluginException;
import com.besaba.revonline.snippetide.api.plugins.Version;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarPluginManager implements PluginManager {
  private final ConcurrentMap<String, Plugin> plugins = new ConcurrentHashMap<>();
  private final static Logger logger = Logger.getLogger(JarPluginManager.class);

  @NotNull
  @Override
  public Plugin loadPlugin(@NotNull final Path file, @NotNull final Version ideVersion) {
    if (Files.isDirectory(file)) {
      throw new UnableToLoadPluginException(file + " is a directory", file, this);
    }

    if (!com.google.common.io.Files.getFileExtension(file.getFileName().toString()).equals("jar")) {
      throw new UnableToLoadPluginException(file + " not a jar file", file, this);
    }

    final JarFile jarFile;

    try {
      jarFile = new JarFile(file.toFile());
    } catch (IOException e) {
      throw new UnableToLoadPluginException(e, file, this);
    }

    final ZipEntry manifestEntry = jarFile.getEntry("manifest.json");

    if (manifestEntry == null) {
      throw new UnableToLoadPluginException("Missing manifest.json file!", file, this);
    }

    try {
      final Plugin plugin = parseManifest(file, jarFile, manifestEntry);

      if (ideVersion.compareTo(plugin.getMinIdeVersion()) == -1) {
        logger.warn("Plugin " + plugin.getName() + " is not compatible with the IDE version");
        throw new UnableToLoadPluginException("Plugin " + plugin.getName() + " doesn't support ide version " + ideVersion, file, this);
      }

      plugins.put(plugin.getName().toLowerCase(), plugin);
      return plugin;
    } catch (IOException e) {
      throw new UnableToLoadPluginException("Unable to create an input stream from the manifest", e, file, this);
    }
  }

  @NotNull
  private Plugin parseManifest(@NotNull final Path file,
                               @NotNull final JarFile jarFile,
                               @NotNull final ZipEntry manifestEntry) throws IOException {
    final JsonObject root = new JsonParser().parse(
        new InputStreamReader(jarFile.getInputStream(manifestEntry))
    ).getAsJsonObject();

    final String name = root.get("name").getAsString();

    if (plugins.containsKey(name.toLowerCase())) {
      throw new UnableToLoadPluginException("Another plugin with the same name (" + name +") already loaded", file, this);
    }

    final String description = root.get("description").getAsString();
    final Version pluginVersion = Version.parse(root.get("version").getAsString());
    final Version minIdeVersion = Version.parse(root.get("minSupportedVersion").getAsString());

    final List<String> authors = getAuthors(root.get("authors").getAsJsonArray());
    final ImmutableList<Language> languages = initializeLanguages(file, root.get("languages").getAsJsonArray());

    return new Plugin(
        name,
        description,
        pluginVersion,
        minIdeVersion,
        authors.toArray(new String[authors.size()]),
        languages
    );
  }

  @NotNull
  private List<String> getAuthors(@NotNull final JsonArray authorsJsonArray) {
    final List<String> authors = new ArrayList<>(authorsJsonArray.size());
    authorsJsonArray.forEach(author ->  authors.add(author.getAsString()));
    return authors;
  }

  @NotNull
  private ImmutableList<Language> initializeLanguages(@NotNull final Path file,
                                                      @NotNull final JsonArray languagesJsonArray) throws MalformedURLException {
    final URLClassLoader jarClassLoader = new URLClassLoader(new URL[] {file.toUri().toURL()});
    final ImmutableList.Builder<Language> languages = ImmutableList.builder();

    languagesJsonArray.forEach(language -> {
      final String fullName = language.getAsString();
      final Class<?> languageClass;

      try {
        languageClass = Class.forName(fullName, false, jarClassLoader);
      } catch (ClassNotFoundException e) {
        throw new UnableToLoadPluginException("Unable to search class language " + fullName, e, file, this);
      }

      if (!Language.class.isAssignableFrom(languageClass)) {
        throw new UnableToLoadPluginException("Class " + fullName + " should implement Language interface", file, this);
      }

      final Language instance;

      try {
        instance = (Language) languageClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new UnableToLoadPluginException("Unable to create an instance of the class language " + fullName, e, file, this);
      }

      languages.add(instance);
    });

    return languages.build();
  }

  @NotNull
  @Override
  public Optional<Plugin> searchPluginByName(@NotNull final String pluginName) {
    return Optional.ofNullable(plugins.get(pluginName.toLowerCase()));
  }

  @Override
  public long getPluginsCount() {
    return plugins.size();
  }

  @NotNull
  @Override
  public List<Plugin> getPlugins() {
    return Collections.unmodifiableList(new ArrayList<>(plugins.values()));
  }
}
