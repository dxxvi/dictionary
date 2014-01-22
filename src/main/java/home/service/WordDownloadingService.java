package home.service;


import home.entity.Word;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Stateless
public class WordDownloadingService {
    // this must match with the url pattern of the SoundDownload servlet
    private static final String soundUriPrefix = "/sound/";

    private static final Map<String, byte[]> sounds = new ConcurrentHashMap<>();

    @Asynchronous
    public Future<Word> fetchSoundAsynchronously(String text) {
        return new AsyncResult<>(fetchSound(text));
    }

    public Word fetchSound(String text) {
        text = text.toLowerCase();

        if (sounds.containsKey(text)) {
            return new Word(text, soundUriPrefix + text + ".mp3");
        }

        try {
            Document document = Jsoup.connect("http://dictionary.reference.com/browse/" + text + "?s=t").get();
            Elements elements = document.select("span[audio$=mp3]");

            for (Element element : elements) {
                String audio = element.attr("audio");
                if ("".equals(audio)) {
                    continue;
                }

                URLConnection urlConnection = new URL(audio).openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                byte[] sound = IOUtils.toByteArray(inputStream);
                sounds.put(text, sound);
                return new Word(text, soundUriPrefix + text + ".mp3");
            }
            return new Word(text, null, "not found");
        }
        catch (IOException exception) {
            return new Word(text, null, exception.getMessage());
        }
    }

    public byte[] getSound(String text) {
        return sounds.get(text.toLowerCase());
    }
}
