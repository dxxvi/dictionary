package home.servlet;

import home.service.WordDownloadingService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * The url to download an mp3 from this app is /sound/*.mp3, but we cannot use this pattern with <url-pattern> (see the
 * Servlet Specification for more information). So we'll use a filter to make sure that the uri has the format
 * /sound/*.mp3
 */
@WebServlet(urlPatterns = {"/sound/*"})
public class SoundDownloadServlet extends HttpServlet {
    @Inject
    private WordDownloadingService wordDownloadingService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String word = requestURI.substring(requestURI.lastIndexOf("/") + 1, requestURI.lastIndexOf(".mp3"));

        response.setContentType("audio/mpeg");
        response.getOutputStream().write(wordDownloadingService.getSound(word));
        response.getOutputStream().flush();
    }
}
