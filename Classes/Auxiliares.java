package Classes;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Auxiliares {
    /**
     * Atualiza um Pokémon no arquivo binário, fazendo sua exclusao depois
     * adicionando novamente
     * 
     * @param scanner               Scanner para entrada do usuário
     * @param caminhoArquivoBinario Caminho do arquivo binário
     */
    public static void atualizarPokemon(Scanner scanner, String caminhoArquivoBinario) {
        System.out.print("Digite o ID do Pokémon que deseja atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Pokemon p = Leitura.encontrarEExcluirPokemon(caminhoArquivoBinario, id);
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
        /*
         * depois de atualizar o pokemon esse codigo abaixo escreve ele no arquivo
         */
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "rw")) {
            int idPokemon = PegarIdUltimo(caminhoArquivoBinario);
            p.setId(idPokemon);
            AtualizarId(idPokemon);
            raf.seek(raf.length());
            Escrita.escreverEntrada(raf, p, caminhoArquivoBinario);

            System.out.println("Atualização concluída!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void debugArquivo(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            System.out.print("Estado inicial do arquivo: ");
            while (raf.getFilePointer() < Math.min(raf.length(), 20)) {
                System.out.print(raf.readInt() + " ");
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza as habilidades de um Pokémon, lendo todas abrindo uma lista de
     * habilidades e adicionando ou removendo habilidades
     * setando a habilidade no objeto pokemon e depois a funcao de atualizar ira
     * escrever
     * 
     * @param p       Pokemon a ser atualizado
     * @param scanner Scanner para entrada do usuário
     */
    public static void atualizarHabilidades(Pokemon p, Scanner scanner) {
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

    /**
     * Imprime um Pokémon no console
     * 
     * @param p
     */
    public static void imprimirPokemon(Pokemon p) {
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

    /**
     * Abre o arquivo e pega o primeiro int que eh o ultimo id do pokemon
     * 
     * @param caminhoArquivoBinario caminho do arquivo binario
     * @return retorna o ultimo id do pokemon
     */
    public static int PegarIdUltimo(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            return raf.readInt() + 1;
        } catch (IOException e) {
            System.out.println("Erro ao ler o último ID. Retornando 1 como fallback.");
            return 1;
        }
    }

    /**
     * Seta o id do pokemon pegando do arquivo para saber qual eh o ultimo
     * 
     * @param p  pokemon
     * @param id id do final do arquivo para ser setado
     */
    public static void setIdArquivo(Pokemon p, int id) {
        p.setId(id);
    }

    /**
     * Atualiza o id do arquivo binario
     * 
     * @param id id a ser atualizado
     */
    public static void AtualizarId(int id) {
        try (RandomAccessFile raf = new RandomAccessFile("data/pokemon_bytes.bin", "rw")) {
            raf.seek(0);
            raf.writeInt(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converte um epoch para uma data no formato dd/MM/yyyy
     * 
     * @param epoch Epoch a ser convertido
     * @return Data no formato dd/MM/yyyy
     */
    public static String converterEpochParaData(long epoch) {
        LocalDate data = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("UTC")).toLocalDate();
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * recebe uma linha de um pokemon e adiciona um caracter aonde esta vaziopara
     * futuras manipulacoes
     * 
     * @param linha linha que contem a informacao dos pokemons
     * @return string que contem a entrada modificada
     */
    static String posicoesVazias(String linha) {
        while (linha.contains(",,")) {
            linha = linha.replace(",,", ",X,");
        }
        return linha;
    }

    /**
     * Recebe uma linha de um pokemon e separa ela em uma lista de strings a partir
     * de uma expressao regular
     * 
     * @param linha linha que contem a informacao dos pokemons
     * @return lista de strings que contem a informacao separada
     */
    public static List<String> SplitInteligente(String linha) {
        List<String> Separado = new ArrayList<>();

        Matcher comparacao = Pattern.compile("\\[.*?\\]|\"[^\"]*\"|[^,]+").matcher(linha);

        while (comparacao.find()) {
            Separado.add(comparacao.group().replaceAll("^\"|\"$", ""));
        }
        return Separado;
    }

    /**
     * metodo que recebe um double e um inteiro e trunca o double para o numero de
     * casas decimais desejado
     * 
     * @param valor         valor a ser truncado
     * @param casasDecimais numero de casas decimais
     * @return double truncado
     */
    public static double truncarDouble(double valor, int casasDecimais) {
        BigDecimal bd = new BigDecimal(valor);
        bd = bd.setScale(casasDecimais, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

}
