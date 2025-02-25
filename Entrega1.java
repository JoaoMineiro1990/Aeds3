import java.io.*;

public class Entrega1 {

      // Método para criar o arquivo e escrever um int no início
      public static File CriarArquivo(String path) {
        File pokemonsBites = new File(path);

        // Verificar se o arquivo existe; se não, cria
        if (!pokemonsBites.exists()) {
            try {
                boolean criado = pokemonsBites.createNewFile();
                if (criado) {
                    System.out.println("Arquivo criado com sucesso!");
                    
                    // Depois de criar, escrevemos um int no início do arquivo
                    try (RandomAccessFile raf = new RandomAccessFile(pokemonsBites, "rw")) {
                        int valor = 0; // O valor que você quer escrever no início
                        raf.writeInt(valor); // Escreve o int no início do arquivo
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("O arquivo já existe.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("O arquivo já existe.");
        }
        
        return pokemonsBites;
    }

    // Método para preencher o arquivo
    public static void Preencher(File BaseDeDados, String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            // Pular a primeira linha (geralmente cabeçalho)
            br.readLine(); 

            String linha;
            while ((linha = br.readLine()) != null) {
                // Processar cada linha, por exemplo, convertendo para bytes e escrevendo no arquivo
                // Aqui você pode fazer qualquer operação com a linha lida
                System.out.println("Processando linha: " + linha);

                // Exemplo: Convertendo a linha em bytes e escrevendo no arquivo binário
                try (FileOutputStream fos = new FileOutputStream(BaseDeDados, true)) {
                    byte[] dados = linha.getBytes(); // Convertendo a linha em bytes
                    fos.write(dados);
                    fos.write(System.lineSeparator().getBytes()); // Adicionando quebra de linha no arquivo binário
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        // Chama a função para criar o arquivo e recebe o objeto File
        File PokemonBites = CriarArquivo("data/PokemonBites.bin");
        Preencher(PokemonBites,"data/dados_modificados.csv");
    }
}
