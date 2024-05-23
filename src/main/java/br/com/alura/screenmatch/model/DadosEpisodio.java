package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(@JsonAlias("Title") String titulo,
                            @JsonAlias("Episode") Integer numero,
                            @JsonAlias("imdbRating") String avaliacao,
                            @JsonAlias("Released") String dataLancamento,
                            @JsonAlias("Actors") String autores,
                            @JsonAlias("Runtime") String duracao,
                            @JsonAlias("Plot") String sinopse,
                            @JsonAlias("Poster") String poster) {

}