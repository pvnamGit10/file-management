package io.holistics.filesystemmanagement.config.facade;

import io.holistics.filesystemmanagement.config.transactional.ReadTransaction;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
@ReadTransaction
public @interface Facade {
}
