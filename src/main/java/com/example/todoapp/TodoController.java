package com.example.todoapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Todo> todos = todoRepository.findAll();
        model.addAttribute("todos", todos);
        model.addAttribute("newTodo", new Todo());
        return "index";
    }

    @PostMapping("/add")
    public String addTodo(@ModelAttribute Todo todo) {
        todoRepository.save(todo);
        return "redirect:/";
    }

    @PostMapping("/toggle/{id}")
    public String toggleTodo(@PathVariable Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        todo.setCompleted(!todo.isCompleted());
        todoRepository.save(todo);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
        return "redirect:/";
    }

    // API endpoints for REST access
    @ResponseBody
    @GetMapping("/api/todos")
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    @ResponseBody
    @PostMapping("/api/todos")
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }

    @ResponseBody
    @PutMapping("/api/todos/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
        todo.setTitle(todoDetails.getTitle());
        todo.setCompleted(todoDetails.isCompleted());
        return todoRepository.save(todo);
    }

    @ResponseBody
    @DeleteMapping("/api/todos/{id}")
    public void deleteTodoApi(@PathVariable Long id) {
        todoRepository.deleteById(id);
    }
}
