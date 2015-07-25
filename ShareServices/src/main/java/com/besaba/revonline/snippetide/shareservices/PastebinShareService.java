package com.besaba.revonline.snippetide.shareservices;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.paste.Paste;
import com.besaba.revonline.pastebinapi.paste.PasteExpire;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import com.besaba.revonline.pastebinapi.response.Response;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.datashare.StructureFieldInfo;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.events.share.ShareCompletedEvent;
import com.besaba.revonline.snippetide.api.events.share.ShareFailedEvent;
import com.besaba.revonline.snippetide.api.events.share.ShareRequestEvent;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import com.google.common.net.UrlEscapers;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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
  private final EventManager eventManager = IDEApplicationLauncher.getIDEApplication().getEventManager();

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

    final String devKey = event.getParameters().getValues().get("Pastebin DevKey").toString();

    final PastebinFactory factory = new PastebinFactory();
    final Pastebin pastebin = factory.createPastebin(devKey);

    final Paste paste = generateData(event.getFileName(), event.getCode(), devKey, event.getLanguage().getName(), factory);
    final Response<String> response = pastebin.post(paste);

    if (response.hasError()) {
      eventManager.post(new ShareFailedEvent(null, response.getError(), this));
    } else {
      eventManager.post(new ShareCompletedEvent(response.get(), this));
    }
  }

  @NotNull
  @Override
  public StructureDataContainer[] getShareParameters() {
    return parameters;
  }

  private static Paste generateData(final String fileName, final String code, final String devkey, final String languageName, final PastebinFactory factory) {
    return factory.createPaste()
        .setVisiblity(PasteVisiblity.Unlisted)
        .setExpire(PasteExpire.Never)
        .setMachineFriendlyLanguage(UrlEscapers.urlPathSegmentEscaper().escape(languageName))
        .setRaw(UrlEscapers.urlPathSegmentEscaper().escape(code))
        .setTitle(UrlEscapers.urlPathSegmentEscaper().escape(fileName))
        .build();
  }
}
