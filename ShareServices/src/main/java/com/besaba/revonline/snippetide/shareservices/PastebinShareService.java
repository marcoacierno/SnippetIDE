package com.besaba.revonline.snippetide.shareservices;

import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.datashare.StructureFieldInfo;
import com.besaba.revonline.snippetide.api.events.share.ShareRequestEvent;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PastebinShareService implements ShareService {
  private static final int SHARE_WITH_DEV_KEY = 1;

  private final static StructureDataContainer[] parameters = new StructureDataContainer[] {
      new StructureDataContainer.Builder(SHARE_WITH_DEV_KEY)
        .setName("Parameters")
        .addField(
            "Pastebin DevKey",
            new StructureFieldInfo<>(String.class, "", "Your pastebin DevKey, go to pastebim.com/api", key -> key != null)
        )
        .create()
  };

  @NotNull
  @Override
  public String getServiceName() {
    return "Pastebin";
  }

  @NotNull
  @Override
  public Image getImage() {
    return new Image(PastebinShareService.class.getResourceAsStream("pastebin.png"));
  }

  @Override
  public void share(@NotNull final ShareRequestEvent event) throws IOException {
    if (event.getTarget() != this) {
      return;
    }

    final HttpURLConnection httpURLConnection
        = ((HttpURLConnection) new URL("http://pastebin.com/api/api_post.php").openConnection());
    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.connect();

    final String devKey = event.getParameters().getValues().get("Pastebin DevKey").toString();
    final String data = generateData(event.getFileName(), event.getCode(), devKey);

    try(final BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8))
    ) {
      writer.append(data);
    } finally {
      httpURLConnection.disconnect();
    }
  }

  @NotNull
  @Override
  public StructureDataContainer[] getShareParameters() {
    return parameters;
  }

  private static String generateData(final String fileName, final String code, final String devkey) {
    return ImmutableMap.<String, String>builder()
        .put("api_option", "paste")
        .put("api_paste_private", String.valueOf(1))
        .put("api_paste_name", UrlEscapers.urlPathSegmentEscaper().escape(fileName))
        .put("api_paste_expire_date", "N")
        .put("api_paste_code", UrlEscapers.urlPathSegmentEscaper().escape(code))
        .put("api_dev_key", devkey)
        .build()
        .entrySet()
        .stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .reduce("", (acc, nxt) -> acc + "&" + nxt);
  }
}
