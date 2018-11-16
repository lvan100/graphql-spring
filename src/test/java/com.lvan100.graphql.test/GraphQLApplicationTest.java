package com.lvan100.graphql.test;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.lvan100.graphql.test.app.GraphQLApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GraphQLApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class GraphQLApplicationTest {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void test() {
        String url = "http://127.0.0.1:8080/query";
        String query = "{hello{world,tom{lastName}},echo}";
        System.out.println(restTemplate.postForObject(url, query, Map.class));
    }

}