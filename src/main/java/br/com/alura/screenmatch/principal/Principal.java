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
    private final String API_KEY = "&apikey=c8d95696";
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
                    2 - Buscar todos episódios
                    3 - Pesquisar episodio por temporada
                    4 - Listar séries pesquisadas
                    5 - Listar episodios pesquisados
                                   \s
                    0 - Sair
                    *********###############**************
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
        } catch (InputMismatchException e) {
            //throw new RuntimeException(e);
            System.out.printf("\n*******Digite uma opção válida.");
            System.out.printf("\n******Erro: ");
        }


    }

    private void buscarSerieWeb() {


        try {
            DadosSerie dados = getDadosSerie();
            //Serie serie = new Serie(dados);

            ListarSeriesPesquisadas();


            Optional<Serie> serie = series.stream()
                    .filter(s -> s.getTitulo().toLowerCase().contains(dados.titulo().toLowerCase()))
                    .findFirst();


            serie.get();
            if(serie.isPresent()){
                System.out.printf("Série já existe");
            }else {
                System.out.printf("Save");
                //repositorio.save(serie);
            }


            //Adicionar teste para verificar se a serie ja existe no banco de dados
//            if (dadosSeries.contains(serie.getTitulo())){
//                System.out.printf("*****Esta série já existe.*****\n");
//            }else{
//                //repositorio.save(serie);
//            }
        }catch (NullPointerException i){
            System.out.printf("####ERRO: Série não encontrada, Verifique se digitou corretamente.\n");
        }

        //System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {

            System.out.println("Digite o nome da série para busca");
            var nomeSerie = leitura.nextLine();
            var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
            DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

//            if(dados.titulo() == null){
//                System.out.printf("Esta série não foi encontrada, verifique se digitou corretamente.");
//            }
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

        var numTemporada = 1;
        var numEpisodio = 1;

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

                    var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+"));
                    DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                    temporadas.add(dadosTemporada);
                }
                temporadas.forEach(System.out::println);

                List<Episodio> episodios = temporadas.stream()
                        .flatMap(d -> d.episodios().stream()
                                .map(e -> new Episodio(d.numero(), e)))
                        .collect(Collectors.toList());
                serieEncontrada.setEpisodios(episodios);
                repositorio.save(serieEncontrada);
            }else{
                System.out.printf("Série não encontrada!");
            }


        }else{
            System.out.println("Voltar");
        }

        return null;

    }

    private void ListarEpisodioPesquisados(){



    }


    private void ListarSeriesPesquisadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getTitulo))
                .forEach(System.out::println);
        dadosSeries.forEach(System.out::println);
    }

}