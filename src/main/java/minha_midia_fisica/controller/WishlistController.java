package minha_midia_fisica.controller;

import minha_midia_fisica.dto.AddToWishlistRequestDTO;
import minha_midia_fisica.model.WishlistFilm;
import minha_midia_fisica.repository.UserRepository;
import minha_midia_fisica.repository.WishlistFilmRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final UserRepository userRepository;
    private final WishlistFilmRepository wishlistFilmRepository;

    public WishlistController(UserRepository userRepository, WishlistFilmRepository wishlistFilmRepository) {
        this.userRepository = userRepository;
        this.wishlistFilmRepository = wishlistFilmRepository;
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<String>> addToWishlist(@RequestBody AddToWishlistRequestDTO request) {
        if (request.categories() == null || request.categories().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("É necessário fornecer pelo menos uma categoria."));
        }

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    String username = context.getAuthentication().getName();
                    return userRepository.findByUsername(username)
                            .flatMap(user -> {
                                WishlistFilm film = new WishlistFilm(
                                        user.getId(),
                                        request.filmId(),
                                        request.title(),
                                        request.releaseDate(),
                                        request.originalTitle(),
                                        request.director(),
                                        request.overview(),
                                        request.posterPath(),
                                        request.categories()
                                );
                                return wishlistFilmRepository.save(film)
                                        .thenReturn(ResponseEntity.ok("Filme adicionado à lista de desejos com sucesso!"));
                            });
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @DeleteMapping("/remove/{filmId}")
    public Mono<ResponseEntity<String>> removeFromWishlist(@PathVariable Integer filmId) {
        if (filmId == null) {
            return Mono.just(ResponseEntity.badRequest().body("ID do filme é inválido."));
        }

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    String username = context.getAuthentication().getName();
                    return userRepository.findByUsername(username)
                            .flatMap(user -> wishlistFilmRepository.findByUserIdAndFilmId(user.getId(), filmId)
                                    .flatMap(film -> wishlistFilmRepository.delete(film)
                                            .then(Mono.just(ResponseEntity.ok("Filme removido com sucesso!"))))
                                    .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                            .body("Filme não encontrado na lista de desejos."))));
                })
                .doOnError(error -> {
                    System.err.println("Erro ao remover filme: " + error.getMessage());
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuário não autenticado.")));
    }

    @PutMapping("/update-categories/{filmId}")
    public Mono<ResponseEntity<String>> updateCategories(
            @PathVariable Integer filmId,
            @RequestBody Map<String, Object> updates) {

        if (filmId == null || updates == null || !updates.containsKey("categories")) {
            return Mono.just(ResponseEntity.badRequest().body("Dados inválidos na requisição."));
        }

        List<String> updatedCategories = (List<String>) updates.get("categories");

        if (updatedCategories == null || updatedCategories.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("É necessário fornecer pelo menos uma categoria."));
        }

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    String username = context.getAuthentication().getName();
                    return userRepository.findByUsername(username)
                            .flatMap(user -> wishlistFilmRepository.findByUserIdAndFilmId(user.getId(), filmId)
                                    .flatMap(film -> {
                                        film.setCategories(updatedCategories);
                                        return wishlistFilmRepository.save(film)
                                                .then(Mono.just(ResponseEntity.ok("Categorias atualizadas com sucesso!")));
                                    })
                                    .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                            .body("Filme não encontrado na lista de desejos."))));

                })
                .doOnError(error -> {
                    System.err.println("Erro ao atualizar categorias: " + error.getMessage());
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuário não autenticado.")));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<WishlistFilm>>> getWishlist() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    String username = context.getAuthentication().getName();
                    return userRepository.findByUsername(username)
                            .flatMapMany(user -> wishlistFilmRepository.findByUserId(user.getId()))
                            .collectList()
                            .map(films -> ResponseEntity.ok(Flux.fromIterable(films)));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @GetMapping("/check")
    public Mono<ResponseEntity<Map<String, Boolean>>> checkFilmInWishlist(@RequestParam Integer filmId) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> {
                    String username = context.getAuthentication().getName();
                    return userRepository.findByUsername(username)
                            .flatMap(user -> wishlistFilmRepository.findByUserIdAndFilmId(user.getId(), filmId)
                                    .map(film -> {
                                        Map<String, Boolean> response = new HashMap<>();
                                        response.put("exists", true);
                                        return ResponseEntity.ok(response);
                                    })
                                    .switchIfEmpty(Mono.just(ResponseEntity.ok(Map.of("exists", false)))));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}