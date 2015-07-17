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
import static org.hamcrest.Matchers.*;
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
  public void testLoadArray() throws Exception {
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

    assertArrayEquals(new String[] {"M", "D", "E", "F", "G", "D"}, jsonConfiguration.getAsArray("user.name").get());
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
  public void testGetDoubleWithGetAsFloatMethod() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"world\",\n" +
        "    \"age\": 84,\n" +
        "    \"world\": 10,\n" +
        "    \"long\": 8686.54\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertEquals(8686.54f, configuration.getAsFloat("user.long").get(), DELTA);

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
  public void testSaveAnArray() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"arr\": [\n" +
        "      \"a\", \"b\", \"c\", \"d\", \"e\"\n" +
        "    ]\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    configuration.save(byteArrayOutputStream);

    final String output = byteArrayOutputStream.toString("UTF-8");
    assertEquals("{\"user\":{\"arr\":[\"a\",\"b\",\"c\",\"d\",\"e\"]}}", output);
  }

  @Test
  public void testSetWithAnArray() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"World\",\n" +
        "    \"age\": 1,\n" +
        "    \"world\": 1,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";

    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("user.age", new String[] {"a", "b"});

    assertArrayEquals(new String[] {"a", "b"}, configuration.getAsArray("user.age").get());
  }

  @Test
  public void testSetWithAnArrayOfIntegers() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"World\",\n" +
        "    \"age\": 1,\n" +
        "    \"world\": 1,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";

    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("user.age", new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

    assertArrayEquals(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}, configuration.getAsArray("user.age").get());

  }

  @Test
  public void testSetArrayOfIntsAndTrySave() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"World\",\n" +
        "    \"age\": 1,\n" +
        "    \"world\": 1,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";

    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("user.age", new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    configuration.save(outputStream);

    final String output = outputStream.toString("UTF-8");

    assertEquals("{\"user\":{\"world\":\"1\",\"hello\":\"World\",\"age\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\"],\"long\":\"9223372036854775207\"}}", output);
  }

  @Test
  public void testSetAnArrayOfBooleans() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"hello\": \"World\",\n" +
        "    \"age\": 1,\n" +
        "    \"world\": 1,\n" +
        "    \"long\": 9223372036854775207\n" +
        "  }\n" +
        "}";

    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.set("user.age", new boolean[] {true, false, true, true});

    assertArrayEquals(new String[] {"true", "false", "true", "true"}, configuration.getAsArray("user.age").get());
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

  @Test
  public void testReadAnArrayOfStringsAsValue() throws Exception {
    final String json = "{\"user\": {\"likes\": [\"Java\", \"C#\"]}}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertArrayEquals(new String[] {"Java", "C#"}, configuration.getAsArray("user.likes").get());
  }

  @Test
  public void testReadAnEmptyArrayAsValue() throws Exception {
    final String json = "{\"user\": {\"likes\": []}}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertThat(configuration.getAsArray("user.likes").get(), arrayWithSize(0));
  }

  @Test
  public void testReadAnInvalidFieldWithGetAsArray() throws Exception {
    final String json = "{\"user\": {\"likes\": \"me\"}}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertThat(configuration.getAsArray("user.me").isPresent(), is(false));
  }

  @Test
  public void testRemoveAnInt() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertTrue(configuration.remove("user.age"));
  }

  @Test
  public void testRemoveAString() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertTrue(configuration.remove("user.name"));
  }

  @Test
  public void testRemoveAnEntryWhichDoesntExists() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertFalse(configuration.remove("user.work"));
  }

  @Test
  public void testRemoveASection() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertTrue(configuration.remove("user"));
  }

  @Test
  public void testRemoveASectionWhichDoesntExists() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertFalse(configuration.remove("keymap"));
  }

  @Test
  public void testRemoveASectionAndSaveConfiguration() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertTrue(configuration.remove("user"));

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    configuration.save(byteArrayOutputStream);

    final String output = byteArrayOutputStream.toString("UTF-8");
    assertEquals("{}", output);
  }

  @Test
  public void testRemoveAnEntryAndSaveConfiguration() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertTrue(configuration.remove("user.age"));

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    configuration.save(byteArrayOutputStream);

    final String output = byteArrayOutputStream.toString("UTF-8");

    assertEquals("{\"user\":{\"name\":\"ReVo_\"}}", output);
  }

  @Test
  public void testRemoveAnEntryFromASectionWhichDoesntExists() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Section keymap doesn't exists.");

    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    configuration.remove("keymap.compile");
  }

  @Test
  public void testRemoveEntrySaveAndLoadAgain() throws Exception {
    final String json = "{\n" +
        "  \"user\": {\n" +
        "    \"name\": \"ReVo_\",\n" +
        "    \"age\": \"98\"\n" +
        "  }\n" +
        "}";
    final JsonConfiguration configuration = new JsonConfiguration();
    configuration.load(new ByteArrayInputStream(json.getBytes(UTF_8)));

    assertTrue(configuration.remove("user.name"));

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    configuration.save(byteArrayOutputStream);

    final String output = byteArrayOutputStream.toString("UTF-8");

    final JsonConfiguration newCopy = new JsonConfiguration();
    newCopy.load(new ByteArrayInputStream(output.getBytes(UTF_8)));

    assertEquals(98, newCopy.getAsInt("user.age").getAsInt());
    assertFalse(newCopy.getAsString("user.name").isPresent());
  }
}