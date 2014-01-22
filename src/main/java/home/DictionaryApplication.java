package home;

import home.resource.TeacherResource;
import home.resource.WordResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/resource")
public class DictionaryApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(WordResource.class);
        classes.add(TeacherResource.class);
        return classes;
    }
}
