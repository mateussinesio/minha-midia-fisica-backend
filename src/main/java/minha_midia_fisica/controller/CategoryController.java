package minha_midia_fisica.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping
public class CategoryController {

    @GetMapping("/categories")
    public Mono<List<String>> getCategories() {
        List<String> categories = List.of("VHS", "DVD", "Blu-Ray");
        return Mono.just(categories);
    }
}