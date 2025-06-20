package com.example.todoapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TodoController {

    private final List<Todo> todoList = new ArrayList<>();

    public TodoController() {
        todoList.add(new Todo(1, "Create a Spring Boot application", true));
        todoList.add(new Todo(2, "Dockerize the application", false));
    }

    @GetMapping("/todos")
    public List<Todo> getTodos() {
        return todoList;
    }
}
