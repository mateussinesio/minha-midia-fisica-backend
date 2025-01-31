package minha_midia_fisica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FilmRequestDTO(Integer id,
                             String title,
                             @JsonProperty("original_title") String originalTitle,
                             @JsonProperty("release_date") String releaseDate,
                             String director,
                             String overview,
                             @JsonProperty("poster_path") String posterPath) {
}