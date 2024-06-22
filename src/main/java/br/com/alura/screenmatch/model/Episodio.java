package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "episodios")
public class Episodio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private Integer numero_temporada;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    public Episodio(){}

    public Episodio(Integer numero, DadosEpisodio dadosEpisodio) {

        this.numero_temporada = numero;
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero();
        this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());

        try {
            this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
        } catch (NumberFormatException ex) {
            this.avaliacao = 0.0;
        }

        try {
            this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        } catch (DateTimeParseException ex) {
            this.dataLancamento = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumero() {
        return numero_temporada;
    }

    public void setNumero(Integer numero) {
        this.numero_temporada = numero;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public String toString() {
        return  ", titulo='" + titulo + '\'' +
                ", Temporada='" + numero_temporada +
                ", numeroEpisodio=" + numeroEpisodio +
                ", avaliacao=" + avaliacao +
                ", dataLancamento=" + dataLancamento;
    }
}
