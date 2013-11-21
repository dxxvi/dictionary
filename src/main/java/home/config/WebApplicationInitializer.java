package home.config;

import home.SoundDownload;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebApplicationInitializer/* implements org.springframework.web.WebApplicationInitializer */{
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(ApplicationConfig.class);

        servletContext.addListener(new ContextLoaderListener(context));

        context = new AnnotationConfigWebApplicationContext();
        context.register(DispatcherConfig.class);

        ServletRegistration.Dynamic registration =
                servletContext.addServlet("dictionary", new DispatcherServlet(context));
        registration.setLoadOnStartup(2);
        registration.addMapping("/services/*");

        registration = servletContext.addServlet("soundDownload", new SoundDownload());
        registration.setLoadOnStartup(1);
        registration.addMapping("*.mp3");
    }
}
