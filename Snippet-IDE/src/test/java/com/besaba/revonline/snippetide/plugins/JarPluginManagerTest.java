package com.besaba.revonline.snippetide.plugins;

import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.UnableToLoadPluginException;
import com.besaba.revonline.snippetide.api.plugins.Version;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class JarPluginManagerTest {

  private Path pluginWithoutManifest;
  private Path pluginWhichRequireIdeVersion31;

  /**
   * If you need to test findPluginByName
   * let the JarPluginManager load this
   * plugin which is correct and contains
   * one language named "TestLanguage"
   */
  private Path correctPluginWithALanguage;

  private JarPluginManager jarPluginManager = new JarPluginManager();

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    pluginWithoutManifest = Paths.get(JarPluginManagerTest.class.getResource("WrongPluginWithoutManifest.jar").toURI());
    correctPluginWithALanguage = Paths.get(JarPluginManagerTest.class.getResource("PluginWithAManifest.jar").toURI());
    pluginWhichRequireIdeVersion31 = Paths.get(JarPluginManagerTest.class.getResource("PluginWhichRequiresIdeVersion31.jar").toURI());

    Files.deleteIfExists(Paths.get(correctPluginWithALanguage.toAbsolutePath().getParent().toString(), correctPluginWithALanguage.getFileName().toString() + "._"));
  }

  @Test
  public void testLoadingPluginWithoutManifestFile() throws Exception {
    exception.expect(UnableToLoadPluginException.class);
    exception.expectMessage("Missing manifest.json file!");

    jarPluginManager.loadPlugin(pluginWithoutManifest, Version.parse("0.1"));
  }

  @Test
  public void testPluginLoadCorrect() throws Exception {
    final Plugin plugin = jarPluginManager.loadPlugin(correctPluginWithALanguage, Version.parse("0.1"));

    assertEquals("My plugin name", plugin.getName());
    assertEquals("My description", plugin.getDescription());
    assertEquals(Version.parse("1.0"), plugin.getVersion());
    assertEquals(Version.parse("0.1"), plugin.getMinIdeVersion());
    assertArrayEquals(new String[]{"Marco Acierno"}, plugin.getAuthors());

    assertNotNull(plugin.getLanguages());
    assertThat(plugin.getLanguages().size(), is(1));

    assertThat(plugin.getLanguages().get(0).getName(), is("TestLanguage"));
    assertArrayEquals(new String[] {"test", "language"}, plugin.getLanguages().get(0).getExtensions());
  }

  @Test
  public void testLoadSamePluginSameTimes() throws Exception {
    final Plugin plugin1 = jarPluginManager.loadPlugin(correctPluginWithALanguage, Version.parse("0.1"));

    exception.expect(UnableToLoadPluginException.class);
    exception.expectMessage("Another plugin with the same name (My plugin name) already loaded");

    final Plugin plugin2 = jarPluginManager.loadPlugin(correctPluginWithALanguage, Version.parse("0.1"));
  }

  @Test
  public void testLoadPluginPassingADirectoryAsFile() throws Exception {
    exception.expect(UnableToLoadPluginException.class);
//    exception.expectMessage(JarPluginManagerTest.class.getResource(".").toURI().toString() + " is a directory");

    jarPluginManager.loadPlugin(Paths.get(JarPluginManagerTest.class.getResource(".").toURI()), Version.parse("0.1"));
  }

  @Test
  public void testLoadPluginPassingATxtFileAsFile() throws Exception {
    exception.expect(UnableToLoadPluginException.class);
    exception.expectMessage(containsString("not a jar file"));

    jarPluginManager.loadPlugin(
        Paths.get(JarPluginManagerTest.class.getResource("myfile.txt").toURI()), Version.parse("0.1")
    );
  }

  @Test
  public void testFindTestLanguage() throws Exception {
    final Plugin testPlugin = jarPluginManager.loadPlugin(correctPluginWithALanguage, Version.parse("0.1"));
    final Plugin foundPlugin = jarPluginManager.searchPluginByName("My plugin name").get();

    assertEquals(testPlugin, foundPlugin);
  }

  @Test
  public void testLoadPluginWhichRequiresIde31PassingVersion10() throws Exception {
    exception.expect(UnableToLoadPluginException.class);
    exception.expectMessage("Plugin My plugin name is not compatible with running IDE");

    jarPluginManager.loadPlugin(pluginWhichRequireIdeVersion31, Version.parse("1.0"));
  }

  @Test
  public void testLoadPluginWhichRequiredIde31PassingVersion31() throws Exception {
    final Plugin plugin = jarPluginManager.loadPlugin(pluginWhichRequireIdeVersion31, Version.parse("3.1"));
  }

  @Test
  public void testLoadPluginWhichRequiresIde31PassingVersion35() throws Exception {
    jarPluginManager.loadPlugin(pluginWhichRequireIdeVersion31, Version.parse("3.5"));
  }

  @Test
  public void testSearchPluginByNameWithAPluginThatDoesntExists() throws Exception {
    final Optional<Plugin> wrongPlugin = jarPluginManager.searchPluginByName("MyPlugin");
    assertFalse(wrongPlugin.isPresent());
  }

  @Test
  public void testDisablePlugin() throws Exception {
    final Plugin plugin = jarPluginManager.loadPlugin(correctPluginWithALanguage, Version.parse("0.1"));
    assertTrue(jarPluginManager.disablePlugin(plugin));

    assertTrue(Files.exists(Paths.get(correctPluginWithALanguage.toAbsolutePath().getParent().toString(), correctPluginWithALanguage.getFileName().toString() + "._")));
  }

  @Test
  public void testEnablePlugin() throws Exception {
    final Plugin plugin = jarPluginManager.loadPlugin(correctPluginWithALanguage, Version.parse("0.1"));
    assertTrue(jarPluginManager.disablePlugin(plugin));

    assertTrue(Files.exists(Paths.get(correctPluginWithALanguage.toAbsolutePath().getParent().toString(), correctPluginWithALanguage.getFileName().toString() + "._")));

    assertTrue(jarPluginManager.enablePlugin(plugin));

    assertFalse(Files.exists(Paths.get(correctPluginWithALanguage.toAbsolutePath().getParent().toString(), correctPluginWithALanguage.getFileName().toString() + "._")));
  }
}