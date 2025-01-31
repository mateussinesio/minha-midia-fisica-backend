package minha_midia_fisica.service;

import minha_midia_fisica.dto.CreditsResponseDTO;
import minha_midia_fisica.dto.CrewMemberDTO;
import minha_midia_fisica.dto.FilmRequestDTO;
import minha_midia_fisica.dto.FilmResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TmdbService {

    @Value("${tmdb.api.token}")
    private String tmdbApiToken;

    private final WebClient webClient;

    public TmdbService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<FilmRequestDTO> searchFilms(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", query)
                        .queryParam("api_key", tmdbApiToken)
                        .queryParam("language", "pt-BR")
                        .build())
                .retrieve()
                .bodyToMono(FilmResponseDTO.class)
                .flatMapMany(response -> Flux.fromIterable(response.results() != null ? response.results() : List.of()))
                .flatMap(this::addDirectorInfo);
    }

    private Mono<FilmRequestDTO> addDirectorInfo(FilmRequestDTO film) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}/credits")
                        .queryParam("api_key", tmdbApiToken)
                        .queryParam("language", "pt-BR")
                        .build(film.id() != null ? film.id() : 0))
                .retrieve()
                .bodyToMono(CreditsResponseDTO.class)
                .map(credits -> {
                    String director = (credits.crew() != null ? credits.crew().stream()
                            .filter(member -> "Director".equalsIgnoreCase(member.job()))
                            .map(CrewMemberDTO::name)
                            .findFirst()
                            .orElse("Não encontrado.") : "Não encontrado.");

                    String releaseDate = film.releaseDate() != null && !film.releaseDate().isEmpty()
                            ? "(" + film.releaseDate().substring(0, 4) + ")"
                            : "";

                    return new FilmRequestDTO(
                            film.id() != null ? film.id() : 0,
                            film.title() != null ? film.title() : "Título não disponível.",
                            film.originalTitle() != null ? film.originalTitle() : "Título original não disponível.",
                            releaseDate,
                            director,
                            film.overview() != null ? film.overview() : "Sinopse não disponível.",
                            film.posterPath() != null ? film.posterPath() : "Caminho do poster não disponível."
                    );
                })
                .onErrorResume(e -> {
                    String releaseDate = film.releaseDate() != null && !film.releaseDate().isEmpty()
                            ? "(" + film.releaseDate().substring(0, 4) + ")"
                            : "";

                    return Mono.just(new FilmRequestDTO(
                            film.id() != null ? film.id() : 0,
                            film.title() != null ? film.title() : "Título não disponível.",
                            film.originalTitle() != null ? film.originalTitle() : "Título original não disponível.",
                            releaseDate,
                            "Não encontrado.",
                            film.overview() != null ? film.overview() : "Sinopse não disponível.",
                            film.posterPath() != null ? film.posterPath() : "Caminho do poster não disponível."
                    ));
                });
    }
}