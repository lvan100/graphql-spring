package com.lvan100.graphql.test.app.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lvan100.graphql.GraphQLInitializer;
import com.lvan100.graphql.annotations.GraphQLDataFetcher;
import com.lvan100.graphql.test.app.result.HelloResponse;
import com.lvan100.graphql.test.app.result.Tom;

import graphql.schema.DataFetchingEnvironment;

@Controller
public class GraphQLController {

    public GraphQLController() {
        GraphQLInitializer.init(this);
    }

    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Map<String, Object> query(@RequestBody String query) {
        return GraphQLInitializer.execute(this, query);
    }

    @GraphQLDataFetcher(fieldName = "echo")
    private String queryEcho(DataFetchingEnvironment env) {
        return "echo";
    }

    @GraphQLDataFetcher(fieldName = "hello")
    private HelloResponse queryHelloWorld(DataFetchingEnvironment env) {

        Tom tom = new Tom();
        tom.lastName = "Cruise";

        HelloResponse helloResponse = new HelloResponse();
        helloResponse.world = "cpp";
        helloResponse.tom = tom;

        return helloResponse;
    }

}