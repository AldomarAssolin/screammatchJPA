package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=c8d95696";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private  List<DadosEpisodio> dadosEpisodios = new ArrayList<>();

    private SerieRepository repositorio;
    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {

        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    **********MENU PRINCIPAL*************
                    1 - Buscar séries
                    2 - Buscar todos episódios
                    3 - Pesquisar episodio por temporada
                    4 - Listar séries pesquisadas
                    5 - Listar episodios pesquisados
                                    
                    0 - Sair   
                    *********###############**************                              
                    """;

            System.out.println("Digite a opção desejada");
            System.out.println(menu);

            opcao = leitura.nextInt();
            leitura.nextLine();


            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    getDadosEpisodio();
                    break;
                case 4:
                    ListarSeriesPesquisadas();
                    break;
                case 5:
                    ListarEpisodioPesquisados();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private DadosSerie buscarEpisodioPorSerie(){
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
        return dadosSerie;
    }

    private void getEpisodio() {
        DadosEpisodio dados = getDadosEpisodio();
        dadosEpisodios.add(dados);
        System.out.println(dados);
    }


    private DadosEpisodio getDadosEpisodio() throws InputMismatchException {

        var menu = """
                ********MENU EPISODIOS************
                [0] Digite 0 para voltar
                [1] Digite 1 para escolher a episodio
                ********##############************
                """;

        var numTemporada = 1;
        var numEpisodio = 1;

        System.out.println(menu);

        System.out.println("Digite a opção desejada");

        var opcao = leitura.nextInt();
        leitura.nextLine();

        if(opcao == 1) {

            //String dadosSerie = buscarEpisodioPorSerie().titulo().replace(" ", "+");
            String dadosSerie = getDadosSerie().titulo().replace(" ", "+");
            System.out.println("\nDigite o numero da temporada: ");
            numTemporada = Integer.parseInt(leitura.nextLine());
            System.out.println("\nDigite o numero do episodio: ");
            numEpisodio = Integer.parseInt(leitura.nextLine());

            var json = consumo.obterDados(ENDERECO + dadosSerie + "&season=" + numTemporada + "&Episode=" + numEpisodio + API_KEY);
            DadosEpisodio dadosEpisodio = conversor.obterDados(json, DadosEpisodio.class);


            dadosEpisodios.add(dadosEpisodio);
            dadosEpisodios.forEach(System.out::println);


        }else{
            System.out.println("Voltar");
        }

        return null;

    }

    private void ListarEpisodioPesquisados(){
        dadosEpisodios.forEach(System.out::println);
    }


    private void ListarSeriesPesquisadas(){
        List<Serie> series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getTitulo))
                .forEach(System.out::println);
        //dadosSeries.forEach(System.out::println);
    }

//    public static Categoria fromString(String text) {
//        for (Categoria categoria : Categoria.values()) {
//            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
//                return categoria;
//            }
//        }
//        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
//    }

}