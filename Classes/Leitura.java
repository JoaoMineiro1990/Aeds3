package Classes;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Leitura {
    
   public static void verificarBuracos(String caminhoArquivoBinario) {
        List<String> buracos = new ArrayList<>();
    
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            raf.seek(0);
            int idInicial = raf.readInt();
            System.out.println("ID inicial do arquivo: " + idInicial);
    
            while (raf.getFilePointer() < raf.length()) {
                long posicaoAntes = raf.getFilePointer();
                
                // Se estamos a menos de 8 bytes do final do arquivo, paramos a leitura para evitar erro
                if (raf.length() - posicaoAntes < 8) {
                    System.out.println("✅ Arquivo terminou corretamente. Nenhuma entrada inválida no final.");
                    break;
                }
    
                int cova = raf.readInt();  // Lê a cova
                int tamanhoEntrada = raf.readInt();  // Lê o tamanho do Pokémon
    
                // 🔹 Proteção contra leitura inválida (caso tamanho seja inconsistente)
                if (tamanhoEntrada <= 0 || (posicaoAntes + tamanhoEntrada + 4) > raf.length()) {
                    System.out.println("❌ ERRO: Entrada inválida na posição " + posicaoAntes + ". Pulando...");
                    break;
                }
    
                if (cova == 0) {
                    // É um buraco, então registramos
                    buracos.add("Buraco detectado na posição " + posicaoAntes + " com tamanho " + tamanhoEntrada + " bytes.");
                }
    
                // Pula para a próxima entrada, garantindo leitura sequencial
                raf.seek(posicaoAntes + tamanhoEntrada + 4);
            }
    
        } catch (IOException e) {
            System.out.println("❌ ERRO ao abrir o arquivo.");
            e.printStackTrace();
        }
    
        // Exibe os buracos encontrados
        if (buracos.isEmpty()) {
            System.out.println("✅ Nenhum buraco encontrado no arquivo.");
        } else {
            System.out.println("\n📌 Lista de buracos encontrados:");
            for (String buraco : buracos) {
                System.out.println(buraco);
            }
        }
    } 
    
   public static void lerTodasEntradas(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            raf.seek(0);
            int id = raf.readInt();
            System.out.println("ID inicial do arquivo: " + id);
    
            int contadorPokemons = 0;
            int contadorBuracos = 0;
    
            while (raf.getFilePointer() < (raf.length() - 4)) {
                long posicaoInicio = raf.getFilePointer();
                int cova = raf.readInt();
                int tamanhoEntrada = raf.readInt();
                if (cova == 1) {
                    raf.seek(posicaoInicio);
                    lerPokemon(raf);
                    contadorPokemons++;
                } else if (cova == 0) {
                    contadorBuracos++;
                    System.out.println("⚠ Buraco encontrado na posição " + posicaoInicio + ", tamanho: " + tamanhoEntrada + " bytes.");
                    raf.seek(posicaoInicio + tamanhoEntrada + 4);
                } else {
                    System.out.println("❌ ERRO: Valor inesperado para cova: " + cova + " na posição " + posicaoInicio);
                    break;
                }
            }
            System.out.println("\n📊 Total de Pokémon lidos: " + contadorPokemons);
            System.out.println("🕳️ Total de buracos encontrados: " + contadorBuracos);
            System.out.println("Fim do arquivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public static Pokemon lerPokemon(RandomAccessFile raf) throws IOException {
        int cova = raf.readInt();
            if (cova == 0) {
            int tamanhoEntrada;
            tamanhoEntrada = raf.readInt();
            long posicaoAntes = raf.getFilePointer();
            System.out.println(" Entrada removida. Posição antes do pulo: " + posicaoAntes + " bytes. Pulando "
                    + tamanhoEntrada + " bytes...");
            raf.seek(posicaoAntes + tamanhoEntrada - 4);
            long posicaoDepois = raf.getFilePointer();
            System.out.println(" Nova posição após pular: " + posicaoDepois + " bytes.");
            return null;
        }
        Pokemon p = Criacao.CriarPokemonDoArquivo(raf);
        //imprimirPokemon(p);
        //System.out.println("Posição atual no arquivo: " + raf.getFilePointer() + " bytes.");
        return p;
    }
 
   public static Pokemon lerUltimoPokemon(String caminhoArquivoBinario) {
        Pokemon ultimoPokemon = new Pokemon(0, "", "", "", "", 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, "", 0, 0.0, 0.0, 0.0, 0.0,
                0.0);

        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            raf.seek(0);
            raf.readInt();
            while (raf.getFilePointer() < raf.length() - 4) {
                int cova = raf.readInt();
                int tamanhoEntrada;

                if (cova == 0) {
                    tamanhoEntrada = raf.readInt();
                    raf.seek(raf.getFilePointer() + tamanhoEntrada - 4);
                } else {
                    Pokemon p = Criacao.CriarPokemonDoArquivo(raf);
                    ultimoPokemon = p;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ultimoPokemon;
    }

   public static Pokemon lerPokemonPorNumero(String caminhoArquivoBinario, int numeroEscolhido) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            raf.seek(0);
            raf.readInt();
            int contador = 0;

            for (int i = 0; i < numeroEscolhido; i++) {
                while (raf.getFilePointer() < raf.length()) {
                    long posicaoAntes = raf.getFilePointer();
                    int cova = raf.readInt();
                    int tamanhoEntrada = raf.readInt();

                    if (cova == 1) {
                        contador++;
                        if (contador == numeroEscolhido) {
                            System.out.println("Pokémon encontrado na posição: " + (raf.getFilePointer()));
                            raf.seek(raf.getFilePointer() - 8);
                            Pokemon p = lerPokemon(raf);
                            System.out.println("Nome: "+ p.getName());
                            return p;

                        }
                    }
                    raf.seek(posicaoAntes + tamanhoEntrada + 4);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Nenhum Pokémon encontrado com esse número.");
        return null;
    }

   public static Pokemon encontrarEExcluirPokemon(String caminhoArquivoBinario, int numeroEscolhido) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "rw")) {
            raf.seek(0);
            raf.readInt(); // Pula o ID inicial do arquivo
            
            int contador = 0;
    
            while (raf.getFilePointer() < raf.length()) {
                long posicaoOriginal = raf.getFilePointer();
                int cova = raf.readInt(); // Lê a cova (0 = buraco, 1 = Pokémon válido)
                int tamanhoEntrada = raf.readInt(); // Lê o tamanho da entrada
                
                // Proteção contra erros de leitura no final do arquivo
                if (tamanhoEntrada <= 0 || (posicaoOriginal + tamanhoEntrada + 8) > raf.length()) {
                    System.out.println("❌ ERRO: Entrada inválida na posição " + posicaoOriginal + ". Pulando...");
                    break;
                }
    
                if (cova == 1) { 
                    contador++;
                    if (contador == numeroEscolhido) {
                        System.out.println("📍 Pokémon encontrado na posição: " + posicaoOriginal);
    
                        // Criando o Pokémon diretamente aqui antes de removê-lo
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
    
                        // Agora voltamos à posição inicial para marcar como buraco (cova = 0)
                        raf.seek(posicaoOriginal);
                        int valor = 0;
                        raf.writeInt(valor);
    
                        System.out.println("🚨 Pokémon removido! ID: " + p.getId() + " - " + p.getName());
                        return p;
                    }
                }
    
                // Pular para a próxima entrada
                raf.seek(posicaoOriginal + tamanhoEntrada + 4);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        System.out.println("❌ Nenhum Pokémon encontrado com esse número.");
        return null;
    }  

}
