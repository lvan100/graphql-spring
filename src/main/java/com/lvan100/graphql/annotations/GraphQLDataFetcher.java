package com.lvan100.graphql.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册一个 DataFetcher 方法
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLDataFetcher {

    /**
     * root field's name
     */
    String fieldName();

}