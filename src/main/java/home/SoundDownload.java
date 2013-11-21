package home;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/*
 * This servlet is mapped to *.mp3
 */
public class SoundDownload extends HttpServlet {
    @Override
    public void init() throws ServletException {
        for (String word : UniqueController.internalWords) {
            word = word.toLowerCase();
            try {
                byte[] sound = UniqueController.fetchWordSound(word);
                if (sound != null) {
                    UniqueController.soundDictionary.put(word, sound);
                }
                else {
                    System.err.printf("[ERROR] Unable to load the sound for %s.\n", word);
                }
            }
            catch (Throwable throwable) {
                System.err.printf("[ERROR] %s: %s\n", word, throwable.getMessage());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String word = requestURI.substring(requestURI.lastIndexOf("/") + 1, requestURI.lastIndexOf(".mp3"));

        response.setContentType("audio/mpeg");
        response.getOutputStream().write(UniqueController.soundDictionary.get(word));
        response.getOutputStream().flush();
    }
}
