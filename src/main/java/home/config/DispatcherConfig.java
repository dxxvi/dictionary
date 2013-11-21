package home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.LinkedList;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"home"})
public class DispatcherConfig extends WebMvcConfigurerAdapter {
    // Spring 3.2+ only
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorPathExtension(true)
                .ignoreAcceptHeader(false)
                // returns HTML by default when not sure
                .defaultContentType(MediaType.TEXT_HTML)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }

    /*
     * Spring will inject the ContentNegotiationManager created form the configureContentNegotiation method above
     * Spring 3.2+ only
     */
    @Bean
    public ViewResolver getContentNegotiatingViewResolver(ContentNegotiationManager contentNegotiationManager) {
        List<ViewResolver> viewResolvers = new LinkedList<>();

        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
        internalResourceViewResolver.setSuffix(".jsp");

        viewResolvers.add(internalResourceViewResolver);

        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(contentNegotiationManager);
        viewResolver.setViewResolvers(viewResolvers);

        return viewResolver;
    }
}

/*
    <!-- View resolver that delegates to other view resolvers based on the content type -->
    <bean class="org.springframework.web.servlet.view.ContentNegotiatingViewResolver">
       <!-- All configuration is now done by the manager - since Spring V3.2 -->
       <property name="contentNegotiationManager" ref="cnManager"/>
    </bean>

    <!--
        Setup a simple strategy:
           1. Only path extension is taken into account, Accept headers are ignored.
           2. Return HTML by default when not sure.
     -->
    <bean id="cnManager" class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean">
        <property name="ignoreAcceptHeader" value="true"/>
        <property name="defaultContentType" value="text/html" />
    </bean>
 */