package ru.weather.context;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.weather.config.SpringConfig;

public class ApplicationContext {
    AnnotationConfigApplicationContext applicationContext = new  AnnotationConfigApplicationContext(SpringConfig.class);
}
