package minha_midia_fisica.repository;

import minha_midia_fisica.model.WishlistFilm;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WishlistFilmRepository extends ReactiveCrudRepository<WishlistFilm, String> {

    Flux<WishlistFilm> findByUserId(String userId);

    Mono<WishlistFilm> findByUserIdAndFilmId(String userId, Integer filmId);
}