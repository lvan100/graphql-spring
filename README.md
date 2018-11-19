# graphql-spring

```java
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

```java
{hello{world,tom{lastName}},echo}
