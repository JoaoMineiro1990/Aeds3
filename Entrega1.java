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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class Entrega1 {

    // ================================================================================================== //
    // ======================================== MÉTODOS Criacao ========================================= //
    // ================================================================================================== //

    public static void CriarArquivo() {
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
                Pokemon p = criarPokemon(Separado);
                p.setId(ultimoId);
                ultimoId++;
                escreverEntrada(dos, p);
            }
            AtualizarId(ultimoId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Pokemon criarPokemon(List<String> Separado) {
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

    // ================================================================================================== //
    // ======================================== MÉTODOS Leitura ========================================= //
    // ================================================================================================== //

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

    private static int lerPokemon(RandomAccessFile raf) throws IOException {
        int cova = raf.readInt();
        int tamanhoEntrada;
        if (cova == 0) {
            tamanhoEntrada = raf.readInt();
            long posicaoAntes = raf.getFilePointer();
            System.out.println(" Entrada removida. Posição antes do pulo: " + posicaoAntes + " bytes. Pulando " + tamanhoEntrada + " bytes...");
            raf.seek(posicaoAntes + tamanhoEntrada - 4);
            long posicaoDepois = raf.getFilePointer(); 
            System.out.println(" Nova posição após pular: " + posicaoDepois + " bytes.");
            
            return tamanhoEntrada;
        }

        tamanhoEntrada = raf.readInt();
        System.out.println(" Lendo Pokémon com tamanho: " + tamanhoEntrada + " bytes.");
        System.out.print(" Lido ID: " + raf.readInt());
        System.out.print(" Número Pokedex: " + raf.readInt());
        System.out.print(" Nome: " + raf.readUTF());
        System.out.print(" Tipo 1: " + raf.readUTF());
        System.out.print(" Tipo 2: " + raf.readUTF());
        int habilidades = raf.readInt();
        System.out.print(" Habilidades: " + habilidades);
        for (int i = 0; i < habilidades; i++) {
            System.out.print(" Habilidade " + (i + 1) + ": " + raf.readUTF());
        }
        System.out.print(" HP: " + raf.readInt());
        System.out.print(" ATT: " + raf.readInt());
        System.out.print(" DEF: " + raf.readInt());
        System.out.print(" SPA: " + raf.readInt());
        System.out.print(" SPD: " + raf.readInt());
        System.out.print(" SPE: " + raf.readInt());
        System.out.print(" BST: " + raf.readInt());
        System.out.print(" Mean: " + raf.readDouble());
        System.out.print(" StdDev: " + raf.readDouble());
        System.out.print(" Generation: " + converterEpochParaData(raf.readLong()));
        System.out.print(" Catch Rate: " + raf.readInt());
        System.out.print(" Legendary: " + raf.readDouble());
        System.out.print(" Mega Evolution: " + raf.readDouble());
        System.out.print(" Height: " + raf.readDouble());
        System.out.print(" Weight: " + raf.readDouble());
        System.out.print(" BMI: " + raf.readDouble());
        System.out.println(" Posição atual: " + raf.getFilePointer() + " bytes.");

        return tamanhoEntrada;
    }

    // ================================================================================================== //
    // ======================================== MÉTODOS Auxiliares ===================================== //
    // ================================================================================================== //

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

    public static List<String> SplitInteligente(String linha) {
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

    // ================================================================================================== //
    // ======================================== MÉTODOS Escrita ========================================= //
    // ================================================================================================== //

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
    int cova = random.nextBoolean() ? 1 : 0; 
    dos.writeInt(cova);
    }

    public static long EscreverHoraBytes(String data) {
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

    // ================================================================================================== //
    // ============================================ MAIN ================================================ //
    // ================================================================================================== //

    public static void main(String[] args) {
        CriarArquivo();
        lerTodasEntradas("data\\pokemon_bytes.bin");
    }
}
