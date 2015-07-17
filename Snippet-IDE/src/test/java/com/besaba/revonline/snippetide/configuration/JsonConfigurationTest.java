package com.besaba.revonline.snippetide.configuration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.*;
import static org.junit.Assert.*;

public class JsonConfigurationTest {
  public static final double DELTA = 1e-15;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testGetUserAgeInsideUserProperty() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"age\": 17\n" +
        "  }\n" +
        "}";
    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals(17, jsonConfiguration.getAsInt("user.age").getAsInt());
  }

  @Test
  public void testGetValueOfAElementWhichDoesntExists() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertFalse(jsonConfiguration.getAsLong("user.my").isPresent());
  }

  @Test
  public void testWithAnEmptySection() throws Exception {
    final String json = "{\n" +
        "  \"hello\": {},\n" +
        "  \"world\": {},\n" +
        "  \"user\": {}\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("hello.my", 5);

    assertEquals(5, configuration.getAsInt("hello.my").getAsInt());
  }

  @Test
  public void testGetValueAsString() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals("world", configuration.getAsString("user.hello").get());
  }

  @Test
  public void testGetValueAsInt() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"my\": 50.3,\n" +
        "    \"world\": 10\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals(10, configuration.getAsInt("user.world").getAsInt());
  }

  @Test
  public void testGetValueAsLong() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"my\": 50.3,\n" +
        "    \"world\": 10,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals(9223372036854775207L, configuration.getAsLong("user.long").getAsLong());
  }

  @Test
  public void testGetValueAsDouble() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"my\": 50.87,\n" +
        "    \"world\": 10,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals(50.87d, configuration.getAsDouble("user.my").getAsDouble(), DELTA);
  }

  @Test
  public void testNullValueNotSupportedYet() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("null is not a valid parameter");

    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"hello\",\n" +
        "    \"world\": null\n" +
        "  }\n" +
        "}";
    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));
  }

  @Test
  public void testArrayNotSupportedAsValueYet() throws Exception {
    expectedException.expect(UnsupportedOperationException.class);
    expectedException.expectMessage("Arrays not supported yet");

    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": [\n" +
        "      \"M\",\n" +
        "      \"D\",\n" +
        "      \"E\",\n" +
        "      \"F\",\n" +
        "      \"G\",\n" +
        "      \"D\"\n" +
        "    ]\n" +
        "  }\n" +
        "}";
    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));
  }

  @Test
  public void testGetAsBoolean() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"hello\",\n" +
        "    \"world\": true\n" +
        "  }\n" +
        "}";

    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertTrue(jsonConfiguration.getAsBoolean("user.world").get());
  }

  @Test
  public void testGetSectionWhichDoesntExists() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Section keymap doesn't exists.");

    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"hello\",\n" +
        "    \"world\": true\n" +
        "  }\n" +
        "}";

    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    jsonConfiguration.getAsString("keymap.compile");
  }

  @Test
  public void testGetWithAWrongQuery() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("world is not a correct user preference setting name");

    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"hello\",\n" +
        "    \"world\": true\n" +
        "  }\n" +
        "}";

    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    jsonConfiguration.getAsString("world");
  }

  @Test
  public void testSetWithAWrongQuery() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("world is not a correct user preference setting name");

    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"hello\",\n" +
        "    \"world\": true\n" +
        "  }\n" +
        "}";

    final JsonConfiguration jsonConfiguration = new JsonConfiguration();
    jsonConfiguration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    jsonConfiguration.set("world", 5);
  }

  @Test
  public void testSetAEntryWhichDoesntExists() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"hello\",\n" +
        "    \"world\": true\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertFalse(configuration.getAsInt("user.my").isPresent());

    configuration.set("user.my", 10);

    assertEquals(10, configuration.getAsInt("user.my").getAsInt());
  }

  @Test
  public void testSetASectionWhichDoesntExists() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"hello\",\n" +
        "    \"world\": true\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("keymap.compile", "F5");

    assertEquals("F5", configuration.getAsString("keymap.compile").get());
  }

  @Test
  public void testObjectsNotSupportedYet() throws Exception {
    expectedException.expect(UnsupportedOperationException.class);
    expectedException.expectMessage("Objects not supported yet");

    final String json = "{\n" +
        "  \"keymap\": {\n" +
        "    \"compile\": {\n" +
        "      \"code\": \"F5\",\n" +
        "      \"shift\": \"DOWN\",\n" +
        "      \"control\": \"DOWN\",\n" +
        "      \"alt\": \"ANY\",\n" +
        "      \"meta\": \"ANY\",\n" +
        "      \"shortcut\": \"UP\"\n" +
        "    }\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));
  }

  @Test
  public void testSetAgeOfTheUser() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"age\": 84,\n" +
        "    \"world\": 10,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals(84, configuration.getAsInt("user.age").getAsInt());

    configuration.set("user.age", 20);

    assertEquals(20, configuration.getAsInt("user.age").getAsInt());
  }

  @Test
  public void testSetString() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"age\": 84,\n" +
        "    \"world\": 10,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals("world", configuration.getAsString("user.hello").get());

    configuration.set("user.hello", "die");

    assertEquals("die", configuration.getAsString("user.hello").get());
  }

  @Test
  public void testSave() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"age\": 84,\n" +
        "    \"world\": 10,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    configuration.save(outputStream);

    final String output = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("{\"user\":{\"world\":\"10\",\"hello\":\"world\",\"age\":\"84\",\"long\":\"9223372036854775207\"}}", output);
  }

  @Test
  public void testAlterValuesAndSave() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"age\": 84,\n" +
        "    \"world\": 10,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";

    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("user.world", 50);
    configuration.set("user.hello", "Ciao");

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    configuration.save(outputStream);

    final String output = new String(outputStream.toByteArray(), UTF_8);
    assertEquals("{\"user\":{\"world\":\"50\",\"hello\":\"Ciao\",\"age\":\"84\",\"long\":\"9223372036854775207\"}}", output);
  }

  @Test
  public void testSaveAndRecreateFromSaveOutput() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"World\",\n" +
        "    \"age\": 1,\n" +
        "    \"world\": 1,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";
    final JsonConfiguration original = new JsonConfiguration();
    original.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    original.save(outputStream);

    final JsonConfiguration saveResult = new JsonConfiguration();
    final String result = new String(outputStream.toByteArray(), UTF_8);
    saveResult.load(new ByteArrayInputStream(result.getBytes(UTF_8)));

    assertEquals(original.getAsInt("user.age").getAsInt(), saveResult.getAsInt("user.age").getAsInt());
    assertEquals(original.getAsString("user.hello").get(), saveResult.getAsString("user.hello").get());
  }

  @Test
  public void testSaveAnIntegerWhereBeforeThereWasAString() throws Exception {
    final String json = "{\"user\": {\"name\": \"ReVo_\"}}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("user.name", 10);

    assertEquals(10, configuration.getAsInt("user.name").getAsInt());
  }

  public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
    final int numThreads = runnables.size();
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
    try {
      final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
      final CountDownLatch afterInitBlocker = new CountDownLatch(1);
      final CountDownLatch allDone = new CountDownLatch(numThreads);
      for (final Runnable submittedTestRunnable : runnables) {
        threadPool.submit(new Runnable() {
          public void run() {
            allExecutorThreadsReady.countDown();
            try {
              afterInitBlocker.await();
              submittedTestRunnable.run();
            } catch (final Throwable e) {
              exceptions.add(e);
            } finally {
              allDone.countDown();
            }
          }
        });
      }
      // wait until all threads are ready
      assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
      // start all test runners
      afterInitBlocker.countDown();
      assertTrue(message +" timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
    } finally {
      threadPool.shutdownNow();
    }
    assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
  }
}