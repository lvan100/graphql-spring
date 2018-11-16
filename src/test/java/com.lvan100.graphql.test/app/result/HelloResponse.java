package com.lvan100.graphql.test.app.result;

import com.lvan100.graphql.annotations.GraphQLField;

public class HelloResponse {

    @GraphQLField
    public String world;

    @GraphQLField
    public Tom    tom;

}