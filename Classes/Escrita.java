package Classes;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Escrita {
    
    public static void escreverEntrada(RandomAccessFile raf, Pokemon p, String caminhoArquivoBinario) throws IOException {
    long posicao = raf.getFilePointer(); 
    raf.writeInt(1); 
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    DataOutputStream tempStream = new DataOutputStream(byteArrayStream);
    tempStream.writeInt(p.getId());
    tempStream.writeInt(p.getNumberPokedex());
    tempStream.writeUTF(p.getName());
    tempStream.writeUTF(p.getType1());
    tempStream.writeUTF(p.getType2());
    String[] habilidades = p.getAbilities().split(", ");
    tempStream.writeInt(habilidades.length);
    for (String habilidade : habilidades) {
        tempStream.writeUTF(habilidade);
    }

    tempStream.writeInt(p.getHp());
    tempStream.writeInt(p.getAtt());
    tempStream.writeInt(p.getDef());
    tempStream.writeInt(p.getSpa());
    tempStream.writeInt(p.getSpd());
    tempStream.writeInt(p.getSpe());
    tempStream.writeInt(p.getBst());
    tempStream.writeDouble(p.getMean());
    tempStream.writeDouble(p.getStandardDeviation());
    tempStream.writeLong(EscreverHoraBytes(p.getGeneration()));
    tempStream.writeInt(p.getCatchRate());
    tempStream.writeDouble(p.getLegendary());
    tempStream.writeDouble(p.getMegaEvolution());
    tempStream.writeDouble(p.getHeight());
    tempStream.writeDouble(p.getWeight());
    tempStream.writeDouble(p.getBmi());

    // Fecha o stream temporário e pega o tamanho real da entrada
    tempStream.flush();
    byte[] entradaBytes = byteArrayStream.toByteArray();
    int tamanhoEntrada = entradaBytes.length;

    // Escreve o tamanho da entrada antes de escrever os dados
    raf.writeInt(tamanhoEntrada);
    raf.write(entradaBytes); // Escreve os bytes reais da entrada

    System.out.println("✅ Pokémon escrito na posição " + posicao + " (Nome: " + p.getName() + ")");
}

    public static void escreverPokemon(DataOutputStream dos, Pokemon p, String caminhoArquivo) throws IOException {
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
    
        dos.writeDouble(Auxiliares.truncarDouble(p.getMean(), 2));
        bytesEscritos += Double.BYTES;
    
        dos.writeDouble(Auxiliares.truncarDouble(p.getStandardDeviation(), 2));
        bytesEscritos += Double.BYTES;
    
        dos.writeLong(EscreverHoraBytes(p.getGeneration()));
        bytesEscritos += Long.BYTES;
    
        dos.writeInt(p.getCatchRate());
        bytesEscritos += Integer.BYTES;
    
        dos.writeDouble(Auxiliares.truncarDouble(p.getLegendary(), 2));
        bytesEscritos += Double.BYTES;
    
        dos.writeDouble(Auxiliares.truncarDouble(p.getMegaEvolution(), 2));
        bytesEscritos += Double.BYTES;
    
        dos.writeDouble(Auxiliares.truncarDouble(p.getHeight(), 2));
        bytesEscritos += Double.BYTES;
    
        dos.writeDouble(Auxiliares.truncarDouble(p.getWeight(), 2));
        bytesEscritos += Double.BYTES;
    
        dos.writeDouble(Auxiliares.truncarDouble(p.getBmi(), 2));
        bytesEscritos += Double.BYTES;
    
        // Agora, ele escreve no arquivo correto passado como parâmetro
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivo, "rw")) {
            raf.seek(posicaoInicio);
            raf.writeInt(bytesEscritos);
        }
    }
    
    public static void escreverEntrada(DataOutputStream dos, Pokemon p,String caminho) throws IOException {
        escreverCova(dos);
        escreverPokemon(dos, p,caminho);
    }

    public static void escreverCova(DataOutputStream dos) throws IOException {
        int cova = 1;
        dos.writeInt(cova);
    }

    public static long EscreverHoraBytes(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(data, formatter);
        return localDate.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
    }

    public static int escreverHabilidades(DataOutputStream dos, String habilidades) throws IOException {
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

}
