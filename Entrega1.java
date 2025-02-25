import java.io.*;

public class Entrega1 {

    // Método para criar o arquivo e escrever um int no início
    public static File CriarArquivo(String path) {
        File pokemonsBites = new File(path);
        if (!pokemonsBites.exists()) {
            try {
                boolean criado = pokemonsBites.createNewFile();
                if (criado) {
                    System.out.println("Arquivo criado com sucesso!");
                    try (RandomAccessFile raf = new RandomAccessFile(pokemonsBites, "rw")) {
                        int valor = 0; 
                        raf.writeInt(valor); 
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
            br.readLine();

            String linha;

            while ((linha = br.readLine()) != null) {
                System.out.println("Processando linha: " + linha);
                try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(BaseDeDados, true))) {
                    dos.writeUTF(linha);
                    dos.writeByte('\n');
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
        Preencher(PokemonBites, "data/dados_teste.csv");
    }
}
