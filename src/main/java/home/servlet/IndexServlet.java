package home.servlet;

import home.util.Constants;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/index"})
public class IndexServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(IndexServlet.class);

    // http://stackoverflow.com/questions/12540282/is-joda-times-datetimeformatter-thread-safe: yes
    DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.dateFormat);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("");
        request.setAttribute("currentTime", dtf.print(new DateTime()));
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/jsp/index.jsp");
        requestDispatcher.forward(request, response);


    }
}
