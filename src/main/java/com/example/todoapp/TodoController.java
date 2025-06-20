package com.example.todoapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public String home() {
        return "Todo App is running! Try /todos to see all todos.";
    }

    @GetMapping("/todos")
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    @PostMapping("/todos")
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }

    @PutMapping("/todos/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        todo.setTitle(todoDetails.getTitle());
        todo.setCompleted(todoDetails.isCompleted());
        return todoRepository.save(todo);
    }

    @DeleteMapping("/todos/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
    }
}
