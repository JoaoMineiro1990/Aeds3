package Classes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class Criacao {

    public static void CriarArquivo(String caminhoArquivo, String caminhoArquivoBinario) {

        int ultimoId = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo));
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(caminhoArquivoBinario))) {
            br.readLine();
            dos.writeInt(0);
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = Auxiliares.posicoesVazias(linha);
                List<String> Separado = Auxiliares.SplitInteligente(linha);
                Pokemon p = criarPokemonDoSplit(Separado);
                p.setId(ultimoId);
                ultimoId++;
                Escrita.escreverEntrada(dos, p, caminhoArquivoBinario);
            }
            Auxiliares.AtualizarId(ultimoId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Pokemon criarPokemonDoSplit(List<String> Separado) {
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
                Auxiliares.truncarDouble(Double.parseDouble(Separado.get(12)), 2),
                Auxiliares.truncarDouble(Double.parseDouble(Separado.get(13)), 2),
                Separado.get(14),
                Integer.parseInt(Separado.get(15)),
                Auxiliares.truncarDouble(Double.parseDouble(Separado.get(16)), 2),
                Auxiliares.truncarDouble(Double.parseDouble(Separado.get(17)), 2),
                Auxiliares.truncarDouble(Double.parseDouble(Separado.get(18)), 2),
                Auxiliares.truncarDouble(Double.parseDouble(Separado.get(19)), 2),
                Auxiliares.truncarDouble(Double.parseDouble(Separado.get(20)), 2));
    }

    public static Pokemon CriarPokemonDoArquivo(RandomAccessFile raf) throws IOException {
        raf.readInt();
        Pokemon p = new Pokemon(0, "", "", "", "", 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, "", 0, 0.0, 0.0, 0.0, 0.0, 0.0);
        p.setId(raf.readInt());
        p.setNumberPokedex(raf.readInt());
        p.setName(raf.readUTF());
        p.setType1(raf.readUTF());
        p.setType2(raf.readUTF());

        int habilidades = raf.readInt();
        StringBuilder habilidadesStr = new StringBuilder();
        for (int i = 0; i < habilidades; i++) {
            habilidadesStr.append(raf.readUTF());
            if (i < habilidades - 1)
                habilidadesStr.append(", ");
        }
        p.setAbilities(habilidadesStr.toString());
        p.setHp(raf.readInt());
        p.setAtt(raf.readInt());
        p.setDef(raf.readInt());
        p.setSpa(raf.readInt());
        p.setSpd(raf.readInt());
        p.setSpe(raf.readInt());
        p.setBst(raf.readInt());
        p.setMean(raf.readDouble());
        p.setStandardDeviation(raf.readDouble());
        p.setGeneration(Auxiliares.converterEpochParaData(raf.readLong()));
        p.setCatchRate(raf.readInt());
        p.setLegendary(raf.readDouble());
        p.setMegaEvolution(raf.readDouble());
        p.setHeight(raf.readDouble());
        p.setWeight(raf.readDouble());
        p.setBmi(raf.readDouble());

        return p;
    }

}
