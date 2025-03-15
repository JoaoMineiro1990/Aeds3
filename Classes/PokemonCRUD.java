package Classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class PokemonCRUD {
     public static void CREATE(Scanner scanner, String caminhoArquivoCSV, String caminhoArquivoBinario) {
    
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivoCSV));
             RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "rw")) {
    
            String linhaAtual;
            List<String> todasAsLinhas = new ArrayList<>();
    
            br.readLine(); // Ignora o cabeçalho
            while ((linhaAtual = br.readLine()) != null) {
                todasAsLinhas.add(linhaAtual);
            }
    
            if (todasAsLinhas.isEmpty()) {
                System.out.println("❌ O arquivo CSV está vazio.");
                return;
            }
    
            boolean continuar = true;
            while (continuar) {
                // Seleciona uma linha aleatória do CSV
                String linhaAleatoria = todasAsLinhas.get(new Random().nextInt(todasAsLinhas.size()));
                linhaAleatoria = Auxiliares.posicoesVazias(linhaAleatoria);
                List<String> Separado = Auxiliares.SplitInteligente(linhaAleatoria);
                Pokemon p = Criacao.criarPokemonDoSplit(Separado);
    
                // Pega o último ID de forma segura
                int ultimoId = Auxiliares.PegarIdUltimo(caminhoArquivoBinario);
                Auxiliares.setIdArquivo(p, ultimoId);
    
                // Escreve no final do arquivo binário
                raf.seek(raf.length());
                Escrita.escreverEntrada(raf, p, caminhoArquivoBinario);
                Auxiliares.AtualizarId(ultimoId);
    
                System.out.println("✅ Novo Pokémon adicionado com sucesso!");
    
                // Pergunta ao usuário se deseja inserir outro Pokémon
                System.out.println("\nDeseja inserir outro Pokémon?");
                System.out.println("1 - Sim");
                System.out.println("2 - Não");
                System.out.print("Escolha: ");
                int escolha = scanner.nextInt();
                scanner.nextLine(); // Consumir a quebra de linha
    
                switch (escolha) {
                    case 1:
                        System.out.println("🔄 Adicionando outro Pokémon...");
                        break;
                    case 2:
                        System.out.println("🚪 Saindo da inserção de Pokémon.");
                        continuar = false;
                        break;
                    default:
                        System.out.println("❌ Opção inválida! Encerrando.");
                        continuar = false;
                        break;
                }
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void READ(Scanner scanner,String caminhoArquivoBinario) {
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

                    Pokemon p = Leitura.lerPokemonPorNumero(caminhoArquivoBinario, numeroEscolhido);
                    break;

                case 2:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 2);
    }

    public static void UPDATE(Scanner scanner,String caminhoArquivoBinario) {
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
                Auxiliares.atualizarPokemon(scanner,caminhoArquivoBinario);
                    break;
                case 2:
                    System.out.println("\nOpção: Verificar buracos.");
                    Leitura.verificarBuracos(caminhoArquivoBinario);
                    break;
                case 3:
                    System.out.println("Saindo do menu de atualização...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 3);

    }
}
