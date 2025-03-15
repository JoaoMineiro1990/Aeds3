import java.util.Scanner;
import Classes.Criacao;
import Classes.Leitura;
import Classes.Ordenacao;
import Classes.Pokemon;
import Classes.PokemonCRUD;

public class Entrega1 {

  
    /**
     * metodo que chama varias funcoes para mostrar o funcionamento da estrutura de dados criada em arquivo e dos algoritimos neles pedidos
     * @param scanner leitura do teclado
     * @param caminhoArquivo caminho do arquivo csv
     * @param caminhoArquivoBinario caminho do arquivo binario antes da ordenacao
     * @param caminhoArquivoBinarioFinal caminho do arquivo binario final apos a ordenacao
     */
    public static void entregaFinal(Scanner scanner, String caminhoArquivo, String caminhoArquivoBinario, String caminhoArquivoBinarioFinal) { 
        System.out.println("🚀 Iniciando o programa...");
        
        // Criando arquivo inicial
        System.out.println("📁 Criando o arquivo...");
    
        Criacao.CriarArquivo(caminhoArquivo, caminhoArquivoBinario);
        
        // Lendo todas as entradas iniciais
        System.out.println("📖 Lendo todas as entradas iniciais...");
        Leitura.lerTodasEntradas(caminhoArquivoBinario);
        
        // Deleta uma entrada
        PokemonCRUD.DELETE(scanner, caminhoArquivoBinario);

        // Lendo o último Pokémon antes das alterações
        System.out.println("🔍 Lendo o último Pokémon...");
        Pokemon ultimo =  Leitura.lerUltimoPokemon(caminhoArquivoBinario);
        System.out.println("Último Pokémon antes das alterações: " + ultimo.getName());
    
        // Criando novo Pokémon
        System.out.println("📝 Criando um novo Pokémon...");
        PokemonCRUD.CREATE(scanner, caminhoArquivo, caminhoArquivoBinario);    
        // Lendo todas as entradas novamente
        Leitura.lerTodasEntradas("data/pokemon_bytes.bin");
    
        // Lendo os Pokémon do arquivo conforme entrada do usuário
        System.out.println("📖 Lendo Pokémon do arquivo com interação...");
        PokemonCRUD.READ(scanner,caminhoArquivoBinario);
    
        // Atualizando Pokémon
        System.out.println("✏️ Atualizando um Pokémon...");
        PokemonCRUD.UPDATE(scanner,caminhoArquivoBinario);
    
        // Lendo todas as entradas após atualização
        Leitura.lerTodasEntradas(caminhoArquivoBinario);
    
        // Ordenando o arquivo final
        System.out.println("📂 Ordenando arquivo final...");
        Ordenacao.iniciarOrdenacao(scanner, caminhoArquivoBinario, caminhoArquivoBinarioFinal);
    
        // Lendo os Pokémon do arquivo final ordenado
        System.out.println("📖 Lendo Pokémon do arquivo final ordenado...");
        Ordenacao.lerPokemonsArquivoTemporario(caminhoArquivoBinarioFinal);
    
        System.out.println("✅ Processo concluído!");
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
