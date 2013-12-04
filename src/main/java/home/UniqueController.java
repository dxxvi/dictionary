package home;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.repository.EmployeeRepository;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class UniqueController {
    public static final Map<String, byte[]> soundDictionary = new ConcurrentHashMap<>();

    public static final Set<String> internalWords =
            new TreeSet<>(Arrays.asList("correct", "lovely", "beautiful", "yes", "try", "again"));

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/receive-word-list-map", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> receiveWordListMapFromBrowser(@RequestBody String requestBody) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        StringBuilder unknownWords = new StringBuilder();

        TypeReference typeReference = new TypeReference<Map<String, String[]>>() {};
        Map<String, String[]> map = objectMapper.readValue(requestBody, typeReference);

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            for (String word : entry.getValue()) {
                if (soundDictionary.get(word) == null) {
                    try {
                        byte[] sound = fetchWordSound(word);
                        if (sound == null) {  // cannot find the sound for this word
                            unknownWords.append(word).append(' ');
                        }
                        else {
                            soundDictionary.put(word, sound);
                        }
                    }
                    catch (Throwable throwable) {
                        unknownWords.append(throwable.getMessage()).append(' ');
                    }
                }
            }
        }

        if (unknownWords.length() == 0) {
            result.put("message", "done");
        }
        else {
            result.put("error", unknownWords.toString());
        }

        return result;
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> saveSoundsToFiles(@RequestParam String directory) {
        Map<String, String> result = new TreeMap<>();
        StringBuilder error = new StringBuilder();
        boolean errorFound = false;
        for (Map.Entry<String, byte[]> entry : soundDictionary.entrySet()) {
            try {
                FileOutputStream fileOutputStream =
                        new FileOutputStream(directory + File.separator + entry.getKey() + ".mp3");
                fileOutputStream.write(entry.getValue());
                fileOutputStream.close();
            }
            catch (Throwable throwable) {
                errorFound = true;
                error.append(throwable.getMessage()).append('\n');
            }
        }

        if (errorFound) {
            result.put("error", error.toString());
        }
        else {
            result.put("message", "all is saved");
        }
        return result;
    }

    /*
     * screenshots captured from raz kids are renamed and cropped
     */
    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> rename(@RequestParam String directory, @RequestParam String separator,
                                      @RequestParam int readingX1, @RequestParam int readingX2,
                                      @RequestParam int readingX3,
                                      @RequestParam int readingY1, @RequestParam int readingY2,
                                      @RequestParam int quizX1, @RequestParam int quizX2,
                                      @RequestParam int quizY1, @RequestParam int quizY2) {
        final Map<String, String> result = new HashMap<>();
        final String backPrevImg = "back-prev.png";
        final String tmpImg = "tmp.png";

        if (!separator.endsWith(".png")) {
            separator += ".png";
        }

        File[] pngFiles = new File(directory).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {  // name: just the name, no path info
                return name.startsWith("Screenshot") && name.endsWith(".png");
            }
        });

        Set<File> pngFileSet = new TreeSet<>(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        pngFileSet.addAll(Arrays.asList(pngFiles));

        try {
            StringBuilder sb = new StringBuilder();
            String parent = null;
            int i = 0;
            for (File file : pngFileSet) {
                if (parent == null) {
                    parent = file.getParent() + File.separator;
                }
                Path source = Paths.get(file.getAbsolutePath());

                boolean reading = file.getName().compareTo(separator) < 0;
                String destinationName = String.format("%s%02d.png", reading ? "h" : "v" ,i);
                String croppedDestinationName = String.format("%s%02d.png", reading ? "r" : "q" ,i);

                Path destination = Paths.get(parent + destinationName);
                Files.move(source, destination);

                if (reading) {
                    sb.append(String.format("\nconvert -crop %dx%d+%d+%d %s %s",
                            readingX2 - readingX1, readingY2 - readingY1, readingX1, readingY1,
                            destinationName, croppedDestinationName));
                    sb.append("\nrm -f ").append(backPrevImg);
                    sb.append(String.format("\nconvert -crop %dx%d+%d+%d %s %s",
                            readingX3 - readingX2, readingY2 - readingY1, readingX2, readingY1,
                            destinationName, backPrevImg));
                    sb.append(String.format("\ncomposite -geometry +%d+%d %s %s %s",
                            2*readingX2 - readingX1 - readingX3 - 4, 0, backPrevImg, croppedDestinationName, tmpImg));
                    sb.append(String.format("\nmv %s %s", tmpImg, croppedDestinationName));
                }
                else {
                    sb.append(String.format("\nconvert -crop %dx%d+%d+%d %s %s",
                            quizX2 - quizX1, quizY2 - quizY1, quizX1, quizY1,
                            destinationName, croppedDestinationName));
                }
                sb.append(String.format("\nmv %s %s", croppedDestinationName, destinationName));
                sb.append(String.format("\nnice -n 19 optipng -o3 %s", destinationName));
                i++;
            }
            sb.insert(0, "cd \"" + parent + "\"");
            Path test = Paths.get("/dev/shm/test" + new Date().getTime() + ".sh");
            if (test.toFile().exists()) {
                test.toFile().delete();
            }
            test = Files.createFile(test,
                    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")));

            boolean fileCreated = false;
            try (BufferedWriter writer = Files.newBufferedWriter(test, Charset.defaultCharset())) {
                writer.append(sb.toString());
                writer.flush();
                fileCreated = true;
            }
            catch (IOException ioex) {
                result.put("error", ioex.getMessage());
            }

/*
            if (fileCreated) {
                Runtime.getRuntime().exec(test.toString());
            }
*/

            result.put("message", "done");
        }
        catch (IOException ioex) {
            result.put("error", ioex.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/jsp/{fileName}")
    public String gotoPage(@PathVariable String fileName) {
        return fileName;
    }

    public static byte[] fetchWordSound(String word) throws Throwable {
        Document document = Jsoup.connect("http://dictionary.reference.com/browse/" + word + "?s=t").get();
        Elements elements = document.select("span[audio$=mp3]");

        for (Element element : elements) {
            String audio = element.attr("audio");
            if ("".equals(audio)) {
                continue;
            }

            URLConnection urlConnection = new URL(audio).openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            return IOUtils.toByteArray(inputStream);
        }

        return null;
    }
}
