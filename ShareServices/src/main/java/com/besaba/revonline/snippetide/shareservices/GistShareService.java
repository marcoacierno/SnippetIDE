package com.besaba.revonline.snippetide.shareservices;

import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.datashare.StructureFieldInfo;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.events.share.ShareCompletedEvent;
import com.besaba.revonline.snippetide.api.events.share.ShareFailedEvent;
import com.besaba.revonline.snippetide.api.events.share.ShareRequestEvent;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import com.google.common.eventbus.Subscribe;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GistShareService implements ShareService {
  private static final int SHARE_WITH_DESCRIPTION = 1;
  private final EventManager eventManager = IDEApplicationLauncher.getIDEApplication().getEventManager();

  private final StructureDataContainer[] shareParameters = new StructureDataContainer[] {
      new StructureDataContainer.Builder(SHARE_WITH_DESCRIPTION)
        .setName("Share")
        .addField("Description", new StructureFieldInfo<>(
            String.class, "", "Gist description", text -> text != null
        ))
        .create()
  };

  @NotNull
  public String getServiceName() {
    return "GitHub Gist";
  }

  @NotNull
  public Image getImage() {
    return new Image(GistShareService.class.getResourceAsStream("github_logo.png"));
  }

  @Subscribe
  public void share(@NotNull final ShareRequestEvent event) throws IOException {
    if (event.getTarget() != this) {
      return;
    }

    final String fileName = event.getFileName();
    final String code = event.getCode();
    final String languageName = event.getLanguage().getName();
    final String description = (String) event.getParameters().getValues().get("Description");

    final HttpURLConnection httpURLConnection
        = ((HttpURLConnection) new URL("https://api.github.com/gists").openConnection());
    final String completeJson = prepareJsonResponse(fileName, code, languageName, description);

    httpURLConnection.setDoOutput(true);
    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.connect();

    try(final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8)) {
      outputStreamWriter.write(completeJson);
      outputStreamWriter.flush();

      final int response = httpURLConnection.getResponseCode();

      if (response != 201) {
        eventManager.post(new ShareFailedEvent(null, "Code: " + response, this));
        return;
      }

      final String location = httpURLConnection.getHeaderField("Location");
      final String gistId = location.substring(location.lastIndexOf('/') + 1);
      final String webUrl = "https://gist.github.com/anonymous/" + gistId;
      eventManager.post(new ShareCompletedEvent(webUrl, this));
    } finally {
      httpURLConnection.disconnect();
    }
  }

  @NotNull
  @Override
  public StructureDataContainer[] getShareParameters() {
    return shareParameters;
  }

  private String prepareJsonResponse(@NotNull final String fileName,
                                     @NotNull final String code,
                                     @NotNull final String languageName,
                                     @NotNull final String description) {
    // the quote method adds the "
    final String rawJson = "{\n" +
        "  \"description\": %s, \n" +
        "  \"public\": true, \n" +
        "  \"files\": {\n" +
        "    %s: {\n" +
        "      \"content\": %s,\n" +
        "      \"language\": %s\n" +
        "    }\n" +
        "  }\n" +
        "}   ";

    final String escapedDescription = escapeForJson(description);
    final String escapedFileNameForJson = escapeForJson(fileName);
    final String escapedCodeForJson = escapeForJson(code);
    final String escapedLanguageNameForJson = escapeForJson(languageName);

    return String.format(rawJson, escapedDescription, escapedFileNameForJson, escapedCodeForJson, escapedLanguageNameForJson);
  }

  public static String escapeForJson(final String text) {
    return JsonParserQuote.quote(text);
  }
}
