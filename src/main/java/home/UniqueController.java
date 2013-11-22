package home;

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

/*
    @Inject
    private EmployeeRepository employeeRepository;
*/

    /*
     * Use wget to download from a url
     */
/*
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> upload(@RequestParam String word, @RequestParam String url) {
        Map<String, String> result = new HashMap<>();
        url = "wget -O /dev/shm/" + word + ".mp3 " + url;
//        System.out.printf("%s - %s\n", word, url);
        try {
            Runtime.getRuntime().exec(url);
        }
        catch (Throwable throwable) {
            System.err.println(throwable.getMessage());
        }
        result.put("message", "done");
        return result;
    }

    @RequestMapping(value = "/words", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> words(@RequestParam String words) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] wordArray = words.split("[ \n][ \n]*");
        if (wordArray.length == 0) {
            result.put("message", "didn't receive any word from you");
        }
        else {
            List<String> internalAndExternalWords = new LinkedList<>();
            internalAndExternalWords.addAll(Arrays.asList(wordArray));
            internalAndExternalWords.addAll(internalWords);

            StringBuilder sb = new StringBuilder();
            for (String word : internalAndExternalWords) {
                word = word.toLowerCase();
                boolean audioFound = false;
                try {
                    byte[] sound = fetchWordSound(word);
                    if (sound != null) {
                        soundDictionary.put(word, sound);
                        audioFound = true;
                    }
                }
                catch (Throwable throwable) {
                    System.err.printf("[ERROR] %s: %s\n", word, throwable.getMessage());
                }

                if (!audioFound) {
                    sb.append(word).append(' ');
                }
            }
            if (sb.length() == 0) {
                result.put("message", "done");
            }
            else {
                result.put("error", "didn't find audio for " + sb.toString());
            }
        }
        return result;
    }
*/

    @RequestMapping(value = "/receive-word-list-map", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> receiveWordListMapFromBrowser(@RequestBody MultiValueMap<String, String> requestBody,
                                                             HttpServletRequest request) {
        Map<String, String> result = new LinkedHashMap<>();
        StringBuilder unknownWords = new StringBuilder();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
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
    @RequestMapping(value = "/all-words", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> allWords() {
        final Set<String> result = new HashSet<>();
        for (String word : soundDictionary.keySet()) {
            if (!internalWords.contains(word)) {
                result.add(word);
            }
        }
        return result;
    }
*/

    /*
     * screenshots captured from raz kids are renamed and cropped
     */
    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> rename(@RequestParam String directory, @RequestParam String separator,
                                      @RequestParam int readingX1, @RequestParam int readingX2,
                                      @RequestParam int readingY1, @RequestParam int readingY2,
                                      @RequestParam int quizX1, @RequestParam int quizX2,
                                      @RequestParam int quizY1, @RequestParam int quizY2) {
        final Map<String, String> result = new HashMap<>();

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
                }
                else {
                    sb.append(String.format("\nconvert -crop %dx%d+%d+%d %s %s",
                            quizX2 - quizX1, quizY2 - quizY1, quizX1, quizY1,
                            destinationName, croppedDestinationName));
                }
                sb.append(String.format("\nmv %s %s", croppedDestinationName, destinationName));
                i++;
            }
            sb.insert(0, "cd \"" + parent + "\"");
            Path test = Paths.get("/dev/shm/test.sh");
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

            if (fileCreated) {
                Runtime.getRuntime().exec(test.toString());
            }

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
