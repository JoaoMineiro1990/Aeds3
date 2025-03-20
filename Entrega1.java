import java.util.Scanner;
import Classes.Auxiliares;
import Classes.Criacao;
import Classes.Leitura;
import Classes.Ordenacao;
import Classes.PokemonCRUD;

public class Entrega1 {
    

    public static void arquivoInterface(Scanner scanner, String caminhoArquivo, String caminhoArquivoBinario) {
        int choice = 0;
        while (choice != 3) {
            System.out.println("==================================");
            System.out.println("==                               ==");
            System.out.println("==        ████████               ==");
            System.out.println("==          ██                   ==");
            System.out.println("/=          ██                   ==");
            System.out.println("==          ██                   ==");
            System.out.println("==          ██                   ==");
            System.out.println("==        ████████               ==");
            System.out.println("==================================");

            System.out.println("1 ------- Criar Arquivo");
            System.out.println("2 ------- Explicação");
            System.out.println("3 ------- Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    Criacao.CriarArquivo(caminhoArquivo, caminhoArquivoBinario);
                    break;
                case 2:
                    System.out.println(
                            "📚 Este opcao recebe o caminho do arquivo csv e cria um arquivo binario com os");
                    System.out.println(
                            " pokemons, os nomes por padrao estao dados_modificacos.csv e PokemonBin.dat(eu acho lembro mais nao");
                    System.out.println(
                            " OBS: se vc n criar o arquivo o codigo nao volta mais aqui e vai dar tudo errado");
                    break;
                case 3:
                    System.out.println("🚪 Saindo do programa. Até logo!");
                    choice = 3;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    public static void createInterface(Scanner scanner, String caminhoArquivo, String caminhoArquivoBinario) {
        int choice = 0;
        while (choice != 4) {

            System.out.println("==================================");
            System.out.println("==                               ==");
            System.out.println("==     ██████╗                   ==");
            System.out.println("==    ██╔═══██╗                  ==");
            System.out.println("/=    ██║                        ==");
            System.out.println("==    ██║                        ==");
            System.out.println("==    ╚██████╔╝                  ==");
            System.out.println("==     ╚═════╝                   ==");
            System.out.println("==================================");

            System.out.println("1 ------- Adicionar Pokémon");
            System.out.println("2 ------- Ler Último Pokémon");
            System.out.println("3 ------- Explicação");
            System.out.println("4 ------- Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    PokemonCRUD.CREATE(scanner, caminhoArquivo, caminhoArquivoBinario);
                    ;
                    break;
                case 2:
                    Auxiliares.imprimirPokemon(Leitura.lerUltimoPokemon(caminhoArquivoBinario));
                    break;
                case 3:
                    System.out.println(
                            "📚 O Create para evitar que vc digite os 22 atributos do pokemon eu estou abrindo o CSV e pegando um pokemon aleatorio e");
                    System.out.println(
                            " adicionando no arquivo binario, apos rodar sugiro que vc chame o ler ultimo pokemon\\n");

                    break;
                case 4:
                    System.out.println("🚪 Saindo do programa. Até logo!");
                    choice = 4;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    public static void updateInterface(Scanner scanner, String caminhoArquivoBinario) {
        int choice = 0;
        while (choice != 3) {
            System.out.println("==================================");
            System.out.println("==                               ==");
            System.out.println("==   ██       ██                 ==");
            System.out.println("==   ██       ██                 ==");
            System.out.println("/=   ██       ██                 ==");
            System.out.println("==   ██       ██                 ==");
            System.out.println("==   ██████████                 ==");
            System.out.println("==    ████████                  ==");
            System.out.println("==================================");

            System.out.println("1 ------- Atualizar Pokémon");
            System.out.println("2 ------- Explicação");
            System.out.println("3 ------- Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    PokemonCRUD.UPDATE(scanner, caminhoArquivoBinario);
                    break;
                case 2:
                    System.out
                            .println(" O Update sempre retorna um pokemon, so n ira retornar quando ele for o ultimo");
                    System.out.println(
                            " o Update vai na posicao desejada porem se nao ha ninguem la ele entra em um loop while.");
                    System.out.println(" e continuar percorrer o arquivo ate achar um pokemon valido");
                    break;
                case 3:
                    System.out.println("🚪 Saindo do menu de atualização.");
                    choice = 3;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    public static void leituraInterface(Scanner scanner, String caminhoArquivoBinario) {
        int choice = 0;
        while (choice != 3) {
            System.out.println("==================================");
            System.out.println("==                               ==");
            System.out.println("==   ███████╗                    ==");
            System.out.println("==   ██╔═══██╗                   ==");
            System.out.println("/=   ███████╔╝                   ==");
            System.out.println("==   ██╔═══██╗                   ==");
            System.out.println("==   ██║   ██║                   ==");
            System.out.println("==   ██║   ██║                   ==");
            System.out.println("==   ╚═╝   ╚═╝                   ==");
            System.out.println("==================================");

            System.out.println("1 ------- Ler Pokémon");
            System.out.println("2 ------- Explicação");
            System.out.println("3 ------- Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    PokemonCRUD.READ(scanner, caminhoArquivoBinario);
                    break;
                case 2:
                    System.out.println(
                            "Essa opcao ira ler o pokemon com o id selecionado, lembrando que nao eh o id do pokemon");
                    System.out.println("na pokedex e sim o id do pokemon da posicao dele no arquivo binario");
                    System.out
                            .println(
                                    "e ele tbm usa o loop while para percorrer ate achar um pokemon valido se n exister");
                    System.out.println("pokemon na posicao desejada");
                    break;
                case 3:
                    System.out.println("🚪 Saindo do menu de leitura.");
                    choice = 3;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    public static void deleteInterface(Scanner scanner, String caminhoArquivoBinario) {
        int choice = 0;
        while (choice != 3) {
            System.out.println("==================================");
            System.out.println("==                               ==");
            System.out.println("==   ███████╗                    ==");
            System.out.println("==   ██╔═══██╗                   ==");
            System.out.println("/=   ██║   ██║                   ==");
            System.out.println("==   ██║   ██║                   ==");
            System.out.println("==   ██║   ██║                   ==");
            System.out.println("==   ███████╔╝                   ==");
            System.out.println("==   ╚══════╝                    ==");
            System.out.println("==================================");

            System.out.println("1 ------- Deletar Pokémon");
            System.out.println("2 ------- Explicação");
            System.out.println("3 ------- Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    PokemonCRUD.DELETE(scanner, caminhoArquivoBinario);
                    break;
                case 2:
                    System.out.println("📚 Esta opção permite deletar um Pokémon do arquivo binário.");
                    System.out.println("   o atributo de delete sera o id e novamente o loop while que percorrer ate" +
                            "achar um pokemon valido");
                    break;
                case 3:
                    System.out.println("🚪 Saindo do menu de deleção.");
                    choice = 3;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    public static void organizacaoInterface(Scanner scanner, String caminhoArquivoBinario) {
        int choice = 0;
        while (choice != 3) {
            System.out.println("==================================");
            System.out.println("==                               ==");
            System.out.println("==      ████████                 ==");
            System.out.println("==     ██      ██                ==");
            System.out.println("/=     ██      ██                ==");
            System.out.println("==     ██      ██                ==");
            System.out.println("==     ██      ██                ==");
            System.out.println("==      ████████                 ==");
            System.out.println("==================================");

            System.out.println("1 ------- Organizar Pokémon");
            System.out.println("2 ------- Explicação");
            System.out.println("3 ------- Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    Ordenacao.iniciarOrdenacao(scanner, caminhoArquivoBinario, caminhoArquivoBinario);
                    break;
                case 2:
                    System.out.println("📚 Esta opção permite organizar os Pokémons no arquivo binário.");
                    System.out.println(
                            "  A organizacao sera feita por id, mas se vc quizer mudar o atributo basta mudar a linha do quick sort");
                    System.out.println(
                            " no arquivo Ordenacao.java , a ordenacao sempre eh otimizada ou seja sempre se tenta pegar a maior RUN de pokemons possiveis");
                    System.out.println(
                            " na ordenacao eu crio varios temporarios mas o arquivo que junta os binarios EH apenas um");
                    break;
                case 3:
                    System.out.println("🚪 Saindo do menu de organização.");
                    choice = 3;
                    break;
                default:
                    System.out.println("❌ Opção inválida! Tente novamente.");
                    break;
            }
        }
    }
    public static void mainInterface(Scanner scanner, String caminhoArquivo, String caminhoArquivoBinario) {
        while (true) {
            System.out.println("==================================");
            System.out.println("==                               ==");
            System.out.println("==   ██████╗ ██████╗ ██    ██    ==");
            System.out.println("==   ██╔══██╗██╔══██╗██    ██    ==");
            System.out.println("/=   ██████╔╝██████╔╝██    ██    ==");
            System.out.println("==   ██╔═══╝ ██╔═══╝ ██    ██    ==");
            System.out.println("==   ██║     ██║     ██    ██    ==");
            System.out.println("==   ╚═╝     ╚═╝      ██████  ✚  ==");
            System.out.println("==           ORGANIZAÇÃO (O)      ==");
            System.out.println("==================================");
    
            System.out.println("1 ------- Criar Arquivo");
            System.out.println("2 ------- CREATE");
            System.out.println("3 ------- UPDATE");
            System.out.println("4 ------- READ");
            System.out.println("5 ------- DELETE");
            System.out.println("6 ------- ORGANIZAÇÃO");
            System.out.println("7 ------- Sair");
  
            System.out.print("Escolha uma opção: ");
    
            try {
                int opcao = Integer.parseInt(scanner.nextLine().trim());
    
                switch (opcao) {
                    case 1:
                        arquivoInterface(scanner, caminhoArquivo,caminhoArquivoBinario);
                        break;
                    case 2:
                        createInterface(scanner, caminhoArquivo, caminhoArquivoBinario);
                        break;
                    case 3:
                        updateInterface(scanner, caminhoArquivoBinario);
                        break;
                    case 4:
                        leituraInterface(scanner, caminhoArquivoBinario);
                        break;
                    case 5:
                        deleteInterface(scanner, caminhoArquivoBinario);
                        break;
                    case 6:
                        organizacaoInterface(scanner, caminhoArquivoBinario);
                        break;
                        case 7:
                        System.out.println("🚪 Saindo do programa. Até logo!");
                        return;
                    default:
                        System.out.println("❌ Opção inválida! Tente novamente.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida! Digite um número válido.");
            }
        }
    }
    
    /**
     * metodo que chama varias funcoes para mostrar o funcionamento da estrutura de
     * dados criada em arquivo e dos algoritimos neles pedidos
     * 
     * @param scanner                    leitura do teclado
     * @param caminhoArquivo             caminho do arquivo csv
     * @param caminhoArquivoBinario      caminho do arquivo binario antes da
     *                                   ordenacao
     * @param caminhoArquivoBinarioFinal caminho do arquivo binario final apos a
     *                                   ordenacao
     */
    public static void entregaFinal(Scanner scanner, String caminhoArquivo, String caminhoArquivoBinario,
            String caminhoArquivoBinarioFinal) {
        mainInterface(scanner, caminhoArquivo, caminhoArquivoBinario);

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String caminhoArquivo = "data/dados_modificados.csv";
        String caminhoArquivoBinario = "data/pokemon_bytes.bin";
        String caminhoArquivoBinarioFinal = "arquivofinal.bin";
        entregaFinal(scanner, caminhoArquivo, caminhoArquivoBinario, caminhoArquivoBinarioFinal);
        scanner.close();
    }

}
