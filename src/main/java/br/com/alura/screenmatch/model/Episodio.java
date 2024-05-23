package br.com.alura.screenmatch.model;

import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Episodio {
    private String poster;
    private String sinopse;
    private String duracao;
    private String autores;
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    public Episodio(DadosEpisodio dadosEpisodio) {

        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero();
        this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
        this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        this.autores = dadosEpisodio.autores();
        this.duracao = dadosEpisodio.duracao();
        this.sinopse = dadosEpisodio.sinopse();
        this.poster = dadosEpisodio.poster();

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


    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public DadosSerie getDados(){

        Scanner leitura = new Scanner(System.in);
        ConsumoApi consumo = new ConsumoApi();
        ConverteDados conversor = new ConverteDados();
        final String ENDERECO = "https://www.omdbapi.com/?t=";
        final String API_KEY = "&apikey=c8d95696";

        int numEpisodio = 0;
        int numTemporada = 0;

        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        System.out.println("\nDigite o numero da temporada: ");
        numTemporada = Integer.parseInt(leitura.nextLine());
        System.out.println("\nDigite o numero do episodio: ");
        numEpisodio = Integer.parseInt(leitura.nextLine());


        var json = consumo.obterDados(ENDERECO + nomeSerie + "&season=" + numTemporada + "&Episode=" + numEpisodio + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    @Override
    public String toString() {
        return "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", avaliacao=" + avaliacao +
                ", dataLancamento=" + dataLancamento ;
    }
}
