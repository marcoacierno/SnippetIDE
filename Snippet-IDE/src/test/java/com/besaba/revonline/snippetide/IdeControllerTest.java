package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEventBuilder;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.boot.Boot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit.ApplicationTest;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

// TODO: Rewrite tests
public class IdeControllerTest extends ApplicationTest {
  private static IDEApplication application;
  private static final Boot boot = new Boot();


  @Override
  public void start(final Stage stage) throws Exception {
    if (!boot.isBooted()) {
      application = boot.boot(
          Paths.get(System.getenv("user.dir"), "test-dir"),
          null,
          new MockPluginManager()
      );
      application.getEventManager().registerListener(MockLanguage.INSTANCE);
    }

    final Language randomLanguage = MockLanguage.INSTANCE;
    final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ide.fxml"));
    loader.setControllerFactory(param -> param == IdeController.class ? new IdeController(randomLanguage) : null);

    final Scene scene = new Scene(loader.load(Main.class.getResourceAsStream("ide.fxml")));
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testTypeTextInsideCodeAreaPressCompileAndCheckIfFileIsWrittenCorrectly() throws Exception {
    clickOn("#codeArea")
        .write("hello world")
        .press(KeyCode.F5);

    final Path savedSourceFile = Paths.get(application.getTemporaryDirectory().toString(), "temp_source.pwn");
    assertTrue(Files.exists(savedSourceFile));

    final StringBuilder content = new StringBuilder();
    try(final BufferedReader reader = Files.newBufferedReader(savedSourceFile)) {
      content.append(reader.readLine()); // we could add System.lineSeparator but
    }

    assertEquals("hello world", content.toString());

    MockLanguage.INSTANCE.compileStartEvent = null;
    MockLanguage.INSTANCE.compileCalled = false;
  }

  @Test
  public void testThatThePluginReceiveCorrectlyTheCompileStartEvent() throws Exception {
    // this test should be improved by using Mockito

    assertFalse(MockLanguage.INSTANCE.compileCalled);

    clickOn("#codeArea")
        .write("Test")
        .press(KeyCode.F5);

    assertTrue(MockLanguage.INSTANCE.compileCalled);
    assertNotNull(MockLanguage.INSTANCE.compileStartEvent);

    assertEquals(MockLanguage.INSTANCE, MockLanguage.INSTANCE.compileStartEvent.getTarget());
    assertEquals(application.getTemporaryDirectory(), MockLanguage.INSTANCE.compileStartEvent.getOutputDirectory());
    assertEquals(Paths.get(application.getTemporaryDirectory().toString(), "temp_source.pwn"), MockLanguage.INSTANCE.compileStartEvent.getSourceFile());

    MockLanguage.INSTANCE.compileCalled = false;
    MockLanguage.INSTANCE.compileStartEvent = null;
  }
}