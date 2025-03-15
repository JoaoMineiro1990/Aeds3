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
    
    public static void atualizarPokemon(Scanner scanner,String caminhoArquivoBinario) {
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
        
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "rw")) {
            int idPokemon = PegarIdUltimo(caminhoArquivoBinario);
            p.setId(idPokemon);
            AtualizarId(idPokemon);
            raf.seek(raf.length());
            Escrita.escreverEntrada(raf, p,caminhoArquivoBinario);
        
            System.out.println("Atualização concluída!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public static void debugArquivo(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            System.out.print("Estado inicial do arquivo: ");
            while (raf.getFilePointer() < Math.min(raf.length(), 20)) { // Lendo apenas os primeiros bytes
                System.out.print(raf.readInt() + " ");
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
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

    public static int PegarIdUltimo(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            return raf.readInt() + 1;
        } catch (IOException e) {
            System.out.println("Erro ao ler o último ID. Retornando 1 como fallback.");
            return 1;
        }
    }

    public static void setIdArquivo(Pokemon p, int id) {
        p.setId(id);
    }

    public static void AtualizarId(int id) {
        try (RandomAccessFile raf = new RandomAccessFile("data/pokemon_bytes.bin", "rw")) {
            raf.seek(0);
            raf.writeInt(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String converterEpochParaData(long epoch) {
        LocalDate data = Instant.ofEpochSecond(epoch).atZone(ZoneId.of("UTC")).toLocalDate();
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    static String posicoesVazias(String linha) {
        while (linha.contains(",,")) {
            linha = linha.replace(",,", ",X,");
        }
        return linha;
    }

    public static List<String> SplitInteligente(String linha) {
        List<String> Separado = new ArrayList<>();

        Matcher comparacao = Pattern.compile("\\[.*?\\]|\"[^\"]*\"|[^,]+").matcher(linha);

        while (comparacao.find()) {
            Separado.add(comparacao.group().replaceAll("^\"|\"$", ""));
        }
        return Separado;
    }

    public static double truncarDouble(double valor, int casasDecimais) {
        BigDecimal bd = new BigDecimal(valor);
        bd = bd.setScale(casasDecimais, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

}
