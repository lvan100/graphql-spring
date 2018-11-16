package com.lvan100.graphql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lvan100.graphql.annotations.GraphQLDataFetcher;
import com.lvan100.graphql.annotations.GraphQLField;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeName;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;

public final class GraphQLInitializer {

    private static final Map<Object, GraphQL> graphQLMap = new ConcurrentHashMap<>();

    public static boolean isJavaSimpleType(Class<?> returnType) {
        return returnType.isPrimitive() || (returnType == String.class);
    }

    /**
     * 初始化使用 GraphQL 环境
     */
    public static void init(Object target) {

        ObjectTypeDefinition.Builder queryTypeBuilder = ObjectTypeDefinition.newObjectTypeDefinition();
        queryTypeBuilder.name("Query");

        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        Map<String, DataFetcher> dataFetchersMap = new HashMap<>();

        Class<?> clazz = target.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            GraphQLDataFetcher dataFetcher = method.getDeclaredAnnotation(GraphQLDataFetcher.class);
            if (dataFetcher != null) {

                dataFetchersMap.put(dataFetcher.fieldName(), (env) -> {
                    method.setAccessible(true);
                    return method.invoke(target, env);
                });

                registerTypeDefinition(typeRegistry, queryTypeBuilder, dataFetcher, method.getReturnType());
            }
        }

        typeRegistry.add(queryTypeBuilder.build());

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
            .type("Query", (builder) -> builder.dataFetchers(dataFetchersMap)).build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        graphQLMap.put(target, graphQL);
    }

    private static void registerTypeDefinition(TypeDefinitionRegistry typeRegistry,
                                               ObjectTypeDefinition.Builder queryTypeBuilder,
                                               GraphQLDataFetcher dataFetcher, Class<?> returnType) {

        FieldDefinition.Builder fieldBuilder = FieldDefinition.newFieldDefinition();
        fieldBuilder.type(new TypeName(returnType.getSimpleName()));
        fieldBuilder.name(dataFetcher.fieldName());

        queryTypeBuilder.fieldDefinition(fieldBuilder.build());

        if (!isJavaSimpleType(returnType)) {
            registerTypeDefinition(typeRegistry, returnType);
        }
    }

    private static void registerTypeDefinition(TypeDefinitionRegistry typeRegistry, Class<?> returnType) {

        ObjectTypeDefinition.Builder typeBuilder = ObjectTypeDefinition.newObjectTypeDefinition();
        typeBuilder.name(returnType.getSimpleName());

        for (Field field : returnType.getDeclaredFields()) {
            GraphQLField graphQLField = field.getDeclaredAnnotation(GraphQLField.class);
            if (graphQLField != null) {

                FieldDefinition.Builder fieldBuilder = FieldDefinition.newFieldDefinition();
                fieldBuilder.type(new TypeName(field.getType().getSimpleName()));
                fieldBuilder.name(field.getName());
                typeBuilder.fieldDefinition(fieldBuilder.build());

                if (!isJavaSimpleType(field.getType())) {
                    registerTypeDefinition(typeRegistry, field.getType());
                }
            }
        }

        typeRegistry.add(typeBuilder.build());
    }

    /**
     * 运行 GraphQL 语句
     */
    public static Map<String, Object> execute(Object target, String query) {

        GraphQL graphQL = graphQLMap.get(target);
        if (graphQL == null) {
            throw new NullPointerException();
        }

        ExecutionResult executionResult = graphQL.execute(query);
        return executionResult.getErrors().size() > 0 ? null : executionResult.getData();
    }

}