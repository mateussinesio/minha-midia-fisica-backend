package minha_midia_fisica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FilmResponseDTO(
        int page,
        @JsonProperty("results") List<FilmRequestDTO> results
) {
}