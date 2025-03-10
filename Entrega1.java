import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class Entrega1 {

    // ==================================================================================================
    // //
    // ======================================== MÉTODOS Criacao
    // ========================================= //
    // ==================================================================================================
    // //

    /**
     * Cria um arquivo binário a partir de um arquivo CSV.
     */
    private static void CriarArquivo() {
        String caminhoArquivo = "data/dados_modificados.csv";
        String caminhoArquivoBinario = "data/pokemon_bytes.bin";

        int ultimoId = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo));
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(caminhoArquivoBinario))) {
            br.readLine();
            dos.writeInt(0);
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = posicoesVazias(linha);
                List<String> Separado = SplitInteligente(linha);
                Pokemon p = criarPokemonDoSplit(Separado);
                p.setId(ultimoId);
                ultimoId++;
                escreverEntrada(dos, p);
            }
            AtualizarId(ultimoId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Pokemon criarPokemonDoSplit(List<String> Separado) {
        return new Pokemon(
                Integer.parseInt(Separado.get(0)),
                Separado.get(1),
                Separado.get(2),
                Separado.get(3).equals("X") ? "" : Separado.get(3),
                Separado.get(4),
                Integer.parseInt(Separado.get(5)),
                Integer.parseInt(Separado.get(6)),
                Integer.parseInt(Separado.get(7)),
                Integer.parseInt(Separado.get(8)),
                Integer.parseInt(Separado.get(9)),
                Integer.parseInt(Separado.get(10)),
                Integer.parseInt(Separado.get(11)),
                truncarDouble(Double.parseDouble(Separado.get(12)), 2),
                truncarDouble(Double.parseDouble(Separado.get(13)), 2),
                Separado.get(14),
                Integer.parseInt(Separado.get(15)),
                truncarDouble(Double.parseDouble(Separado.get(16)), 2),
                truncarDouble(Double.parseDouble(Separado.get(17)), 2),
                truncarDouble(Double.parseDouble(Separado.get(18)), 2),
                truncarDouble(Double.parseDouble(Separado.get(19)), 2),
                truncarDouble(Double.parseDouble(Separado.get(20)), 2));
    }

    private static Pokemon CriarPokemonDoArquivo(RandomAccessFile raf) throws IOException {
        raf.readInt();
        Pokemon p = new Pokemon(0, "", "", "", "", 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, "", 0, 0.0, 0.0, 0.0, 0.0, 0.0);
        p.setId(raf.readInt());
        p.setNumberPokedex(raf.readInt());
        p.setName(raf.readUTF());
        p.setType1(raf.readUTF());
        p.setType2(raf.readUTF());

        int habilidades = raf.readInt();
        StringBuilder habilidadesStr = new StringBuilder();
        for (int i = 0; i < habilidades; i++) {
            habilidadesStr.append(raf.readUTF());
            if (i < habilidades - 1)
                habilidadesStr.append(", ");
        }
        p.setAbilities(habilidadesStr.toString());
        p.setHp(raf.readInt());
        p.setAtt(raf.readInt());
        p.setDef(raf.readInt());
        p.setSpa(raf.readInt());
        p.setSpd(raf.readInt());
        p.setSpe(raf.readInt());
        p.setBst(raf.readInt());
        p.setMean(raf.readDouble());
        p.setStandardDeviation(raf.readDouble());
        p.setGeneration(converterEpochParaData(raf.readLong()));
        p.setCatchRate(raf.readInt());
        p.setLegendary(raf.readDouble());
        p.setMegaEvolution(raf.readDouble());
        p.setHeight(raf.readDouble());
        p.setWeight(raf.readDouble());
        p.setBmi(raf.readDouble());

        return p;
    }

    // ================================================================================================
    // //
    // ======================================== MÉTODOS Leitura
    // ======================================= //
    // ================================================================================================
    // //

    private static void verificarBuracos() {
        List<String> buracos = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile("data/pokemon_bytes.bin", "r")) {
            raf.seek(0);
            int idInicial = raf.readInt();
            System.out.println("ID inicial do arquivo: " + idInicial);

            while (raf.getFilePointer() < raf.length()) {
                long posicaoAntes = raf.getFilePointer();
                int cova = raf.readInt();
                int tamanhoEntrada = raf.readInt();

                if (cova == 0) {
                    long posicaoDepoisDoTamanho = raf.getFilePointer();
                    int idPokemon = raf.readInt();
                    int numeroPokedex = raf.readInt();
                    String nomePokemon = raf.readUTF();
                    String buracoInfo = "Buraco encontrado -> ID: " + idPokemon +
                            ", Pokédex: " + numeroPokedex +
                            ", Nome: " + nomePokemon +
                            ", Tamanho: " + tamanhoEntrada + " bytes.";
                    buracos.add(buracoInfo);
                    raf.seek(posicaoDepoisDoTamanho);
                    raf.seek(raf.getFilePointer() + tamanhoEntrada - 4);
                } else {
                    raf.seek(posicaoAntes + tamanhoEntrada + 4);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (buracos.isEmpty()) {
            System.out.println("Nenhum buraco encontrado no arquivo.");
        } else {
            System.out.println("\nLista de buracos encontrados:");
            for (String buraco : buracos) {
                System.out.println(buraco);
            }
        }
    }

    private static void lerTodasEntradas(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            int id = raf.readInt();
            System.out.println("ID inicial do arquivo: " + id);

            while (raf.getFilePointer() < (raf.length() - 4)) {
                lerPokemon(raf);
            }

            System.out.println("Fim do arquivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Pokemon lerPokemon(RandomAccessFile raf) throws IOException {
        int cova = raf.readInt();
        int tamanhoEntrada;

        if (cova == 0) {
            tamanhoEntrada = raf.readInt();
            long posicaoAntes = raf.getFilePointer();
            System.out.println(" Entrada removida. Posição antes do pulo: " + posicaoAntes + " bytes. Pulando "
                    + tamanhoEntrada + " bytes...");
            raf.seek(posicaoAntes + tamanhoEntrada - 4);
            long posicaoDepois = raf.getFilePointer();
            System.out.println(" Nova posição após pular: " + posicaoDepois + " bytes.");
            return null;
        }
        Pokemon p = CriarPokemonDoArquivo(raf);
        imprimirPokemon(p);
        System.out.println("Posição atual no arquivo: " + raf.getFilePointer() + " bytes.");
        return p;
    }

    private static Pokemon lerUltimoPokemon(String caminhoArquivoBinario) {
        Pokemon ultimoPokemon = new Pokemon(0, "", "", "", "", 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, "", 0, 0.0, 0.0, 0.0, 0.0,
                0.0);

        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            raf.seek(0);
            raf.readInt();
            while (raf.getFilePointer() < raf.length() - 4) {
                int cova = raf.readInt();
                int tamanhoEntrada;

                if (cova == 0) {
                    tamanhoEntrada = raf.readInt();
                    raf.seek(raf.getFilePointer() + tamanhoEntrada - 4);
                } else {
                    Pokemon p = CriarPokemonDoArquivo(raf);
                    ultimoPokemon = p;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ultimoPokemon;
    }

    private static Pokemon lerPokemonPorNumero(String caminhoArquivoBinario, int numeroEscolhido) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            raf.seek(0);
            raf.readInt();
            int contador = 0;

            for (int i = 0; i < numeroEscolhido; i++) {
                while (raf.getFilePointer() < raf.length()) {
                    long posicaoAntes = raf.getFilePointer();
                    int cova = raf.readInt();
                    int tamanhoEntrada = raf.readInt();

                    if (cova == 1) {
                        contador++;
                        if (contador == numeroEscolhido) {
                            System.out.println("Pokémon encontrado na posição: " + (raf.getFilePointer()));
                            raf.seek(raf.getFilePointer() - 8);

                            return lerPokemon(raf);

                        }
                    }
                    raf.seek(posicaoAntes + tamanhoEntrada + 4);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Nenhum Pokémon encontrado com esse número.");
        return null;
    }

    private static Pokemon lerPokemonPorNumeroeMatar(String caminhoArquivoBinario, int numeroEscolhido) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "rw")) {
            raf.seek(0);
            raf.readInt();
            int contador = 0;

            for (int i = 0; i < numeroEscolhido; i++) {
                while (raf.getFilePointer() < raf.length()) {
                    long posicaoOriginal = raf.getFilePointer(); 
                    int cova = raf.readInt();
                    int tamanhoEntrada = raf.readInt();

                    if (cova == 1) {
                        contador++;
                        if (contador == numeroEscolhido) {
                            System.out.println("Pokémon encontrado na posição: " + raf.getFilePointer());
                            raf.seek(raf.getFilePointer() - 8);
                            Pokemon p = lerPokemon(raf);
                            raf.seek(posicaoOriginal);
                            raf.writeInt(0);
                            return p;
                        }
                    }
                    raf.seek(posicaoOriginal + tamanhoEntrada + 4);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Nenhum Pokémon encontrado com esse número.");
        return null;
    }

    // ==================================================================================================
    // //
    // ======================================== MÉTODOS Auxiliares
    // ===================================== //
    // ==================================================================================================
    // //

    private static void atualizarPokemon(Scanner scanner) {
        String caminho = "data/pokemon_bytes.bin";
        System.out.print("Digite o ID do Pokémon que deseja atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Pokemon p = lerPokemonPorNumeroeMatar(caminho, id);

        if (p == null) {
            System.out.println("Erro: Pokémon não encontrado.");
            return;
        }

        System.out.println("\nPokémon encontrado:");
        System.out.println("1 - Nome: " + p.getName());
        System.out.println("2 - Tipo 1: " + p.getType1());
        System.out.println("3 - Tipo 2: " + p.getType2());
        System.out.println("4 - Habilidades: " + p.getAbilities());
        System.out.print("\nEscolha um atributo para atualizar (1-4): ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        switch (escolha) {
            case 1:
                System.out.print("Digite o novo nome: ");
                p.setName(scanner.nextLine());
                break;
            case 2:
                System.out.print("Digite o novo Tipo 1: ");
                p.setType1(scanner.nextLine());
                break;
            case 3:
                System.out.print("Digite o novo Tipo 2: ");
                p.setType2(scanner.nextLine());
                break;
            case 4:
                atualizarHabilidades(p, scanner);
                break;
            default:
                System.out.println("Opção inválida.");
                return;
        }
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/pokemon_bytes.bin", true))) {
            int idPokemon = PegarIdUltimo(caminho);
            p.setId(idPokemon);
            AtualizarId(idPokemon);
            imprimirPokemon(p);
            escreverEntrada(dos, p);
            System.out.println("Atualização concluída!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  private static void atualizarHabilidades(Pokemon p, Scanner scanner) {
    System.out.println("\nHabilidades atuais: " + p.getAbilities());
    System.out.println("1 - Adicionar habilidade");
    System.out.println("2 - Remover habilidade");
    System.out.print("Escolha uma opção: ");
    int escolha = scanner.nextInt();
    scanner.nextLine(); 
    List<String> habilidades = new ArrayList<>();
    if (!p.getAbilities().isEmpty()) {
        habilidades = new ArrayList<>(List.of(p.getAbilities().replace("'", "").split(", ")));
    }

    if (escolha == 1) {
        System.out.print("Digite a nova habilidade: ");
        String novaHabilidade = scanner.nextLine().trim();
        habilidades.add(novaHabilidade);
    } else if (escolha == 2) {
        if (habilidades.isEmpty()) {
            System.out.println("Este Pokémon não tem habilidades para remover.");
            return;
        }

        System.out.println("Selecione a habilidade para remover:");
        for (int i = 0; i < habilidades.size(); i++) {
            System.out.println((i + 1) + " - '" + habilidades.get(i) + "'"); 
        }
        System.out.print("Digite o número da habilidade: ");
        int remover = scanner.nextInt();
        scanner.nextLine(); 

        if (remover >= 1 && remover <= habilidades.size()) {
            habilidades.remove(remover - 1);
        } else {
            System.out.println("Opção inválida.");
            return;
        }
    } else {
        System.out.println("Opção inválida.");
        return;
    }

    p.setAbilities(habilidades.stream()
            .map(h -> "'" + h + "'") 
            .collect(Collectors.joining(", ")));

    System.out.println("Habilidades atualizadas: " + p.getAbilities());
}

    private static void imprimirPokemon(Pokemon p) {
        System.out.print("ID: " + p.getId() + " ");
        System.out.print("Number Pokedex: " + p.getNumberPokedex() + " ");
        System.out.print("Name: " + p.getName() + " ");
        System.out.print("Type1: " + p.getType1() + " ");
        System.out.print("Type2: " + p.getType2() + " ");
        System.out.print("Abilities: " + p.getAbilities() + " ");
        System.out.print("HP: " + p.getHp() + " ");
        System.out.print("ATT: " + p.getAtt() + " ");
        System.out.print("DEF: " + p.getDef() + " ");
        System.out.print("SPA: " + p.getSpa() + " ");
        System.out.print("SPD: " + p.getSpd() + " ");
        System.out.print("SPE: " + p.getSpe() + " ");
        System.out.print("BST: " + p.getBst() + " ");
        System.out.print("Mean: " + p.getMean() + " ");
        System.out.print("StdDev: " + p.getStandardDeviation() + " ");
        System.out.print("Generation: " + p.getGeneration() + " ");
        System.out.print("Catch Rate: " + p.getCatchRate() + " ");
        System.out.print("Legendary: " + p.getLegendary() + " ");
        System.out.print("Mega Evolution: " + p.getMegaEvolution() + " ");
        System.out.print("Height: " + p.getHeight() + " ");
        System.out.print("Weight: " + p.getWeight() + " ");
        System.out.println("BMI: " + p.getBmi() + " ");
    }

    private static int PegarIdUltimo(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            return raf.readInt() + 1;
        } catch (IOException e) {
            System.out.println("Erro ao ler o último ID. Retornando 1 como fallback.");
            return 1;
        }
    }

    private static void setIdArquivo(Pokemon p, int id) {
        p.setId(id);
    }

    private static void AtualizarId(int id) {
        try (RandomAccessFile raf = new RandomAccessFile("data/pokemon_bytes.bin", "rw")) {
            raf.seek(0);
            raf.writeInt(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String converterEpochParaData(long epoch) {
        LocalDate data = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("UTC")).toLocalDate();
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private static String posicoesVazias(String linha) {
        while (linha.contains(",,")) {
            linha = linha.replace(",,", ",X,");
        }
        return linha;
    }

    private static List<String> SplitInteligente(String linha) {
        List<String> Separado = new ArrayList<>();

        Matcher comparacao = Pattern.compile("\\[.*?\\]|\"[^\"]*\"|[^,]+").matcher(linha);

        while (comparacao.find()) {
            Separado.add(comparacao.group().replaceAll("^\"|\"$", ""));
        }
        return Separado;
    }

    private static double truncarDouble(double valor, int casasDecimais) {
        BigDecimal bd = new BigDecimal(valor);
        bd = bd.setScale(casasDecimais, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    // =================================================================================================
    // //
    // ======================================== MÉTODOS Escrita
    // ======================================== //
    // =================================================================================================
    // //

    private static void escreverPokemon(DataOutputStream dos, Pokemon p) throws IOException {
        long posicaoInicio = dos.size();
        dos.writeInt(0);
        int bytesEscritos = Integer.BYTES;
        dos.writeInt(p.getId());
        bytesEscritos += Integer.BYTES;

        dos.writeInt(p.getNumberPokedex());
        bytesEscritos += Integer.BYTES;

        dos.writeUTF(p.getName());
        bytesEscritos += p.getName().getBytes("UTF-8").length + 2;

        dos.writeUTF(p.getType1());
        bytesEscritos += p.getType1().getBytes("UTF-8").length + 2;

        dos.writeUTF(p.getType2());
        bytesEscritos += p.getType2().getBytes("UTF-8").length + 2;

        bytesEscritos += escreverHabilidades(dos, p.getAbilities());

        dos.writeInt(p.getHp());
        bytesEscritos += Integer.BYTES;

        dos.writeInt(p.getAtt());
        bytesEscritos += Integer.BYTES;

        dos.writeInt(p.getDef());
        bytesEscritos += Integer.BYTES;

        dos.writeInt(p.getSpa());
        bytesEscritos += Integer.BYTES;

        dos.writeInt(p.getSpd());
        bytesEscritos += Integer.BYTES;

        dos.writeInt(p.getSpe());
        bytesEscritos += Integer.BYTES;

        dos.writeInt(p.getBst());
        bytesEscritos += Integer.BYTES;

        dos.writeDouble(truncarDouble(p.getMean(), 2));
        bytesEscritos += Double.BYTES;

        dos.writeDouble(truncarDouble(p.getStandardDeviation(), 2));
        bytesEscritos += Double.BYTES;

        dos.writeLong(EscreverHoraBytes(p.getGeneration()));
        bytesEscritos += Long.BYTES;

        dos.writeInt(p.getCatchRate());
        bytesEscritos += Integer.BYTES;

        dos.writeDouble(truncarDouble(p.getLegendary(), 2));
        bytesEscritos += Double.BYTES;

        dos.writeDouble(truncarDouble(p.getMegaEvolution(), 2));
        bytesEscritos += Double.BYTES;

        dos.writeDouble(truncarDouble(p.getHeight(), 2));
        bytesEscritos += Double.BYTES;

        dos.writeDouble(truncarDouble(p.getWeight(), 2));
        bytesEscritos += Double.BYTES;

        dos.writeDouble(truncarDouble(p.getBmi(), 2));
        bytesEscritos += Double.BYTES;

        try (RandomAccessFile raf = new RandomAccessFile("data/pokemon_bytes.bin", "rw")) {
            raf.seek(posicaoInicio);
            raf.writeInt(bytesEscritos);
        }
    }

    private static void escreverEntrada(DataOutputStream dos, Pokemon p) throws IOException {
        escreverCova(dos);
        escreverPokemon(dos, p);
    }

    private static void escreverCova(DataOutputStream dos) throws IOException {
        Random random = new Random();
        int cova = (random.nextInt(100) < 5) ? 0 : 1;
        dos.writeInt(cova);
    }

    private static long EscreverHoraBytes(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(data, formatter);
        return localDate.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
    }

    private static int escreverHabilidades(DataOutputStream dos, String habilidades) throws IOException {
        List<String> listaHabilidades = new ArrayList<>();
        Matcher matcher = Pattern.compile("'(.*?)'").matcher(habilidades);

        while (matcher.find()) {
            listaHabilidades.add(matcher.group(1));
        }

        int bytesEscritos = 0;

        dos.writeInt(listaHabilidades.size());
        bytesEscritos += Integer.BYTES;

        for (String habilidade : listaHabilidades) {
            dos.writeUTF(habilidade);
            bytesEscritos += habilidade.getBytes("UTF-8").length + 2;
        }

        return bytesEscritos;
    }

    // ==================================================================================================
    // //
    // ======================================== MÉTODOS CRUD
    // ============================================ //
    // ==================================================================================================
    // //

    public static void CREATE() {
        String caminhoArquivoCSV = "data/dados_modificados.csv";
        String caminhoArquivoBinario = "data/pokemon_bytes.bin";

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivoCSV));
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(caminhoArquivoBinario, true))) {

            br.readLine();
            String linhaAleatoria = null;
            String linhaAtual = "";
            Random random = new Random();
            int linhaIndex = 0;
            while ((linhaAtual = br.readLine()) != null) {
                linhaIndex++;
                if (random.nextInt(linhaIndex) == 0) {
                    linhaAleatoria = linhaAtual;
                }
            }
            if (linhaAleatoria == null) {
                System.out.println("O arquivo CSV está vazio.");
                return;
            }
            linhaAleatoria = posicoesVazias(linhaAleatoria);
            List<String> Separado = SplitInteligente(linhaAleatoria);

            int ultimoId = PegarIdUltimo(caminhoArquivoBinario);
            Pokemon p = criarPokemonDoSplit(Separado);

            setIdArquivo(p, ultimoId);
            escreverEntrada(dos, p);
            AtualizarId(ultimoId);

            System.out.println("Novo Pokémon adicionado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void READ(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\nEscolha uma opção:");
            System.out.println("1 - Ler outro Pokémon");
            System.out.println("2 - Sair");
            System.out.print("Opção: ");

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    System.out.print("Digite um número entre 1 e 1000: ");
                    int numeroEscolhido = scanner.nextInt();

                    if (numeroEscolhido < 1) {
                        System.out.println("Número inválido. Deve ser maior que 0.");
                        break;
                    }

                    Pokemon p = lerPokemonPorNumero("data/pokemon_bytes.bin", numeroEscolhido);
                    break;

                case 2:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 2);
    }

    public static void UPDATE(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\n=== Menu de Atualização ===");
            System.out.println("1 - Atualizar um Pokémon");
            System.out.println("2 - Verificar buracos");
            System.out.println("3 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    System.out.println("\nOpção: Atualizar um Pokémon.");
                    atualizarPokemon(scanner);
                    break;
                case 2:
                    System.out.println("\nOpção: Verificar buracos.");
                    verificarBuracos();
                    break;
                case 3:
                    System.out.println("Saindo do menu de atualização...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 3);

    }

    // ==================================================================================================
    // //
    // ============================================ MAIN
    // ================================================ //
    // ==================================================================================================
    // //

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CriarArquivo();
        Pokemon ultimo = lerUltimoPokemon("data\\pokemon_bytes.bin");
        System.out.println("Último Pokémon: " + ultimo.getName());
        CREATE();
        ultimo = lerUltimoPokemon("data\\pokemon_bytes.bin");
        System.out.println("Último Pokémon: " + ultimo.getName());
        READ(scanner);
        UPDATE(scanner);
        lerTodasEntradas("data\\pokemon_bytes.bin");
        scanner.close();
    }
}
