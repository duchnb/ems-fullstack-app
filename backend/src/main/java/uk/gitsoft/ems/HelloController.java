package uk.gitsoft.ems;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // Base path for all endpoints in this controller
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from your Spring Boot Backend! 👋 I am impressed";
    }
}