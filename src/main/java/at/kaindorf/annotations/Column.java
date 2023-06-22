package at.kaindorf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
//um beim gewissen Spalten die richtige Namensgebung zu gewährleisten
public @interface Column {
    String name() default "";
}
