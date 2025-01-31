package minha_midia_fisica.dto;

import java.util.List;

public record AddToWishlistRequestDTO(Integer filmId, String title, String releaseDate, String originalTitle, String director, String overview, String posterPath, List<String> categories) {
}
