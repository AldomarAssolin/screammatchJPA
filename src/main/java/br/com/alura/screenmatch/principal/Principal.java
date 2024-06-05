package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
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

    public void exibeMenu() {

        int opcao = -1;

        try {
            while (opcao != 0) {
                var menu = """
                    **********MENU PRINCIPAL*************
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries pesquisadas
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
                        getDadosEpisodio();
                        break;
                    case 3:
                        ListarSeriesPesquisadas();
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
                [1] Digite 1 para escolher o episodio
                ********##############************
                """;

        System.out.println(menu);

        System.out.println("Digite a opção desejada");

        var opcao = leitura.nextInt();
        leitura.nextLine();

        if(opcao == 1) {

            ListarSeriesPesquisadas();
            System.out.printf("Escolha uma série pelo nome: ");
            var nomeSerie = leitura.nextLine();

            Optional<Serie> serie = series.stream()
                    .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                    .findFirst();

            if(serie.isPresent()){
                var serieEncontrada = serie.get();
                List<DadosTemporada> temporadas = new ArrayList<>();

                for (int i = 1; i <= serieEncontrada.getTotalTemporadas() ; i++) {

                    var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                    //System.out.printf("\nVarDump: " + json);
                    DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                    temporadas.add(dadosTemporada);
                    //System.out.printf("\nVarDump2: " + temporadas);
                }
                temporadas.forEach(System.out::println);

                List<Episodio> episodios = temporadas.stream()
                        .flatMap(d -> d.episodios().stream()
                                .map(e -> new Episodio(d.numero(), e)))
                        .collect(Collectors.toList());
                serieEncontrada.setEpisodios(episodios);
                repositorio.save(serieEncontrada);
                System.out.printf(String.valueOf("Episodio: " + episodios));
            }else{
                System.out.printf("Série não encontrada!");
            }


        }else{
            System.out.println("Voltar");
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

}