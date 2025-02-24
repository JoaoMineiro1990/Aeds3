import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Entrega1 {
    public static void main(String[] args) {
        // Caminho do arquivo
        String caminhoArquivo = "data/dados_modificados.csv";
        
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            // Pular o cabeçalho (primeira linha)
            br.readLine();  // Lê a primeira linha (cabeçalho), mas não faz nada com ela
            
            // Ler e imprimir as linhas seguintes
            String linha;
            if((linha = br.readLine()) != null) {
                System.out.println(linha);  // Imprime a linha
            }
            System.out.println();
            if((linha = br.readLine()) != null) {
                System.out.println(linha);  // Imprime a linha
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
