package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=" + System.getenv("API_KEY_OMDB");
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private  List<DadosEpisodio> dadosEpisodios = new ArrayList<>();

    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public void exibeMenu() {

        int opcao = -1;

        try {
            while (opcao != 0) {
                var menu = """
                    \n**********MENU PRINCIPAL*************
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries pesquisadas
                    4 - Buscar serie por titulo
                    5 - Buscar séries por ator
                    6 - Top 5 séries
                    7 - Buscar por gênero
                    8 - Buscar por quantidade máxima de temporadas
                    9 - Buscar episódios
                    10 - Top 5 episódios por série
                    11 - Buscar episódio apartir de uma data
                                   \s
                    0 - Sair
                    *********###############**************'
                   """;

                System.out.println(menu);
                System.out.println("Digite a opção desejada");

                opcao = leitura.nextInt();
                leitura.nextLine();


                switch (opcao) {
                    case 1:
                        buscarSerieWeb();
                        break;
                    case 2:
                        buscarEpisodio();
                        break;
                    case 3:
                        ListarSeriesPesquisadas();
                        break;
                    case 4:
                        buscarSeriePorTitulo();
                        break;
                    case 5:
                        buscarSeriesPorAtor();
                        break;
                    case 6:
                        buscarTop5Series();
                        break;
                    case 7:
                        buscarSeriePorCategoria();
                        break;
                    case 8:
                        buscarPorQtdeMaxTemporadas();
                        break;
                    case 9:
                        buscarEpisodioPorTrecho();
                        break;
                    case 10:
                        topEpisodiosPorserie();
                        break;
                    case 11:
                        buscarEpisodioAposUmaData();
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida");
                }
            }
        } catch (InputMismatchException e) {
            //throw new RuntimeException(e);
            System.out.printf("\n*******Digite uma opção válida.");
            System.out.printf("\n******Erro: ");
        }


    }

    private void buscarSerieWeb() {

        try {
            DadosSerie dados = getDadosSerie();
            Serie serie = new Serie(dados);

            var tituloSerie = serie.getTitulo();
            series = repositorio.findAll();

            Optional<Serie> novaserie = series.stream()
                    .filter(s -> s.getTitulo().toLowerCase().contains(tituloSerie.toLowerCase()))
                    .findFirst();

            if(novaserie.isPresent()){
                System.out.printf("\n*****Serie já existe*****");
            }else{
                repositorio.save(serie);
            }

        }catch (NullPointerException i){
            System.out.printf("-####ERRO: Série não encontrada, Verifique se digitou corretamente.\n");
        }catch (NumberFormatException n){
            System.out.printf("####ERRO: Série não encontrada, Verifique se digitou corretamente.\n");
        }
    }

    private DadosSerie getDadosSerie() {

            System.out.println("Digite o nome da série para busca");
            var nomeSerie = leitura.nextLine();
            var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
            DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

            return dados;

    }

    private void getEpisodio() {
        DadosEpisodio dados = buscarEpisodio();
        dadosEpisodios.add(dados);
        System.out.println(dados);
    }


    private DadosEpisodio buscarEpisodio() throws InputMismatchException {

            ListarSeriesPesquisadas();
            System.out.printf("Escolha uma série pelo nome: ");
            var nomeSerie = leitura.nextLine();

            Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

            if(serie.isPresent()){
                var serieEncontrada = serie.get();
                List<DadosTemporada> temporadas = new ArrayList<>();

                for (int i = 1; i <= serieEncontrada.getTotalTemporadas() ; i++) {

                    var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                    DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                    temporadas.add(dadosTemporada);
                }
                temporadas.forEach(System.out::println);

                List<Episodio> episodios = temporadas.stream()
                        .flatMap(d -> d.episodios().stream()
                                .map(e -> new Episodio(d.numeroTemporada(), e)))
                        .collect(Collectors.toList());
                serieEncontrada.setEpisodios(episodios);
                repositorio.save(serieEncontrada);
            }else{
                System.out.printf("Série não encontrada!");
            }

        return null;

    }

    private void ListarEpisodioPesquisados(){ }


    private void ListarSeriesPesquisadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getTitulo))
                .forEach(System.out::println);
        dadosSeries.forEach(System.out::println);
    }

    private void buscarSeriePorTitulo(){
        System.out.printf("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBusca.isPresent()){
            System.out.printf("Dados da Serie: " + serieBusca.get());
        }else{
            System.out.printf("Série não encontrada");
        }
    }

    private void buscarSeriesPorAtor(){
        System.out.println("Qual nome do ator para busca?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliação mínima: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        System.out.println("Séries em que " + nomeAtor + " atuou: ");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " - " + "Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series(){
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();

        seriesTop.forEach(s ->
                System.out.println(s.getTitulo() + " - " + "Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Digite o genero desejado: ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromBr(nomeGenero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarPorQtdeMaxTemporadas(){
        System.out.println("Digite a quantidade máxima de temporadas desejada: ");
        var totalTemporadas = leitura.nextInt();
        System.out.println("Digite a avaliação mínima desejada: ");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesMaxTemporadas = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);

        seriesMaxTemporadas.forEach(s ->
                System.out.println(s.getTitulo() + " - " + "avaliacao: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Qual nome do episódio para busca?");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(),
                        e.getTemporada(),
                        e.getNumeroEpisodio(),
                        e.getTitulo()));

    }

    private void topEpisodiosPorserie(){
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodioPorSerie(serie);

            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada: %s - Episódio: %s - %s - Avaliacao: %s\n",
                            e.getSerie().getTitulo(),
                            e.getTemporada(),
                            e.getNumeroEpisodio(),
                            e.getTitulo(),
                            e.getAvaliacao()));
        }

    }

    private void buscarEpisodioAposUmaData(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("\nDigite o ano de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodioAno = repositorio.episodioPorSerieEAno(serie, anoLancamento);
            episodioAno.forEach(System.out::println);
        }

    }

}