import java.io.*;

public class Dataset {
    // Método para baixar o dataset
    public void baixarDataset(String dataset) {
        try {
            // Caminho para o Python e o script Python
            String pythonExecutable = "python"; // Ou o caminho completo do seu Python
            String scriptPath = "Conexao.py";  // Caminho para o script Python

            // Define a variável de ambiente KAGGLE_CONFIG_DIR para o diretório onde está o kaggle.json
            String kaggleConfigDir = "C:\\Users\\1454474\\.kaggle";  // Altere para o seu diretório
            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath, dataset);
            processBuilder.environment().put("KAGGLE_CONFIG_DIR", kaggleConfigDir);  // Define a variável de ambiente

            // Inicia o processo
            Process process = processBuilder.start();

            // Captura a saída do script Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // Exibe a saída do Python (mensagens de sucesso)
            }

            // Espera o processo terminar
            int exitCode = process.waitFor();
            System.out.println("Processo Python finalizado com código de saída: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Dataset baixar = new Dataset();
        String alo = "maca11/all-pokemon-dataset";
        baixar.baixarDataset(alo);
    }
}
