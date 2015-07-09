package com.besaba.revonline.snippetide.boot;


import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.events.manager.impl.EventBusEventManager;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.plugins.UnableToLoadPluginException;
import com.besaba.revonline.snippetide.application.IDEApplicationImpl;
import com.besaba.revonline.snippetide.plugins.JarPluginManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Prepare the application
 */
public class Boot {
  private volatile static boolean booted;
  private static final Logger logger = Logger.getLogger(Boot.class);

  /**
   * Prepare and initialize everything inside the application.
   *
   * @param applicationPath The path where are all the application files (plugins, etc)
   * @return The booted application
   * @throws IllegalStateException This method can be invoked only one time, if you try to invoke
   *                               it again it will throw this exception
   */
  public static IDEApplication boot(final Path applicationPath) {
    if (booted) {
      throw new IllegalStateException("Application already started");
    }

    logger.info("Boot phase started");

    createDirectories(applicationPath);

    final EventManager eventManager = new EventBusEventManager();
    final PluginManager pluginManager = new JarPluginManager();
    final IDEApplication application = new IDEApplicationImpl(eventManager, pluginManager);
    IDEApplicationLauncher.createApplication(application);

    loadPlugins(pluginManager, applicationPath);

    booted = true;
    return application;
  }

  private static void createDirectories(final Path applicationPath) {
    final Path pluginPath = Paths.get(applicationPath.toAbsolutePath().toString(), "plugins");

    try {
      Files.createDirectories(pluginPath);
    } catch (IOException e) {
      throw new BootFailedException(e);
    }
  }

  private static void loadPlugins(@NotNull final PluginManager pluginManager,
                                  @NotNull final Path applicationPath) {
    final Path pluginPath = Paths.get(applicationPath.toAbsolutePath().toString(), "plugins");
    try {
      Files.walkFileTree(pluginPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

          try {
            pluginManager.loadPlugin(file);
          } catch (UnableToLoadPluginException e) {
            logger.fatal("Unable to load plugin " + e.getFileLocation() + "! The manager is " + e.getPluginManager(), e);
          }

          logger.info("Correctly loaded plugin " + file + "!");

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      logger.fatal("Something went wrong during parsing the plugin directory", e);
    }

    logger.info("Loaded " + pluginManager.getPluginsCount() + " plugin(s)");
  }
}