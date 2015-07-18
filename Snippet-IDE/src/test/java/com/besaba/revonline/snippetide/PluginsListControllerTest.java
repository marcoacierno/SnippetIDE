package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.boot.Boot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.*;

public class PluginsListControllerTest extends ApplicationTest {

  private static final Boot boot = new Boot();

  @Before
  public void setUp() throws Exception {
    if (!boot.isBooted()) {
      final EventManager eventManager = new EventManager() {
        @Override
        public void registerListener(@NotNull final Object listener) {

        }

        @Override
        public void unregisterListener(@NotNull final Object listener) {

        }

        @Override
        public void post(@NotNull final Event<?> event) {

        }
      };

      final PluginManager pluginManager = new MockPluginManager();

      boot.boot(Paths.get(System.getenv("user.dir"), "test-dir"), eventManager, pluginManager, null);
    }
  }

  @Override
  public void start(final Stage stage) throws Exception {
    final Scene scene = new Scene(FXMLLoader.load(PluginsListController.class.getResource("pluginslist.fxml")));
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testPluginInformationShouldBeEscaped() throws Exception {


  }
}