package minha_midia_fisica.controller;

import minha_midia_fisica.dto.FilmRequestDTO;
import minha_midia_fisica.service.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class TmdbController {

    private final TmdbService tmdbService;

    @Autowired
    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/films/search")
    public Flux<FilmRequestDTO> searchFilms(@RequestParam String query) {
        return tmdbService.searchFilms(query);
    }
}