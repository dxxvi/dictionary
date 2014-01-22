package home;

import javax.lang.model.element.Element;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented                            // so that this appears in the java-doc
public @interface TestDataPrepare {
    String value() default "no value";
    String[] reviewers();
}
