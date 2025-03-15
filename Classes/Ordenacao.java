package Classes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Ordenacao {

    /**
     * Cria arquivos temporários para a distribuição dos Pokémon.
     * 
     * @param numArquivos numero total de arquivos temporarios Maximo 7
     */
    public static void criarArquivosTemporarios(int numArquivos) {
        for (int i = 1; i <= numArquivos; i++) {
            String nomeArquivo = "temp" + i + ".bin";
            try {
                File file = new File(nomeArquivo);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                System.out.println("Arquivo criado: " + nomeArquivo);
            } catch (IOException e) {
                System.out.println("Erro ao criar o arquivo: " + nomeArquivo);
                e.printStackTrace();
            }
        }
    }

    /**
     * Intercala os arquivos temporários dentro de apenas 1 arquivo final
     * para que esse posteriormente seja distribuido novamente ate ordenacao
     * completa.
     * Este codigo foi criado com ajuda de IA entao ele esta cheio de sysout
     * que eu achei pertinete deixar para que o entendimento seja mais facil.
     * 
     * @param arquivosTemporarios Lista de arquivos temporários
     * @param caminhoArquivoFinal Caminho do arquivo final
     */
    public static void intercalarArquivosTemporarios(List<String> arquivosTemporarios, String caminhoArquivoFinal) {
        try (DataOutputStream dosFinal = new DataOutputStream(new FileOutputStream(caminhoArquivoFinal))) {
            System.out.println("\n=== 🔄 Iniciando Intercalação com Replacement Selection ===");

            dosFinal.writeInt(0);
            List<RandomAccessFile> arquivos = new ArrayList<>();
            for (String arquivo : arquivosTemporarios) {
                arquivos.add(new RandomAccessFile(arquivo, "r"));
            }

            /*
             * Duas Heaps para intercalacao a primeira vai tentar fazer a maior Run possivel
             * e a segunda vai guardar os Pokemons que nao foram possiveis de serem
             * inseridos
             */
            PriorityQueue<PokemonEntry> minHeap = new PriorityQueue<>(
                    Comparator.comparing(entry -> entry.pokemon.getName()));
            PriorityQueue<PokemonEntry> nextRunHeap = new PriorityQueue<>(
                    Comparator.comparing(entry -> entry.pokemon.getName()));

            int heapSize = 7;
            for (int i = 0; i < arquivos.size(); i++) {
                RandomAccessFile raf = arquivos.get(i);
                if (raf.getFilePointer() < raf.length() && minHeap.size() < heapSize) {
                    Pokemon p = Leitura.lerPokemon(raf);
                    if (p != null) {
                        minHeap.add(new PokemonEntry(p, i));
                    }
                }
            }

            int contadorIteracao = 0;
            /* esse cara eh importante para quando mudarmos de heap */
            Pokemon ultimoSalvo = null;

            /*
             * A Heap percorre o arquvio fazendo adicoes e remocoes sempre no tamanho maximo
             * de 7
             */
            while (!minHeap.isEmpty() || !nextRunHeap.isEmpty()) {
                int pokemonRemovidos = 0;

                while (!minHeap.isEmpty()) {
                    PokemonEntry menorEntrada = minHeap.poll();
                    Pokemon menorPokemon = menorEntrada.pokemon;
                    int origemArquivo = menorEntrada.origemArquivo;
                    Escrita.escreverEntrada(dosFinal, menorPokemon, caminhoArquivoFinal);
                    pokemonRemovidos++;
                    ultimoSalvo = menorPokemon;
                    RandomAccessFile raf = arquivos.get(origemArquivo);
                    if (raf.getFilePointer() < raf.length()) {
                        Pokemon proximoPokemon = Leitura.lerPokemon(raf);
                        if (proximoPokemon != null) {
                            if (ultimoSalvo == null || proximoPokemon.getName().compareTo(ultimoSalvo.getName()) >= 0) {
                                minHeap.add(new PokemonEntry(proximoPokemon, origemArquivo));
                            } else {
                                nextRunHeap.add(new PokemonEntry(proximoPokemon, origemArquivo));
                            }
                        }
                    }
                }
                contadorIteracao++;
                System.out.println(
                        "🔄 Iteração " + contadorIteracao + ": " + pokemonRemovidos + " Pokémon removidos da heap.");

                if (!nextRunHeap.isEmpty()) {
                    System.out.println("🔄 Iniciando nova run com Pokémon remanescentes...");
                    minHeap.addAll(nextRunHeap);
                    nextRunHeap.clear();
                    ultimoSalvo = null;
                }
            }
            for (RandomAccessFile raf : arquivos) {
                raf.close();
            }

            System.out.println(
                    "\n Intercalação concluída com Replacement Selection! Dados salvos em: " + caminhoArquivoFinal);

        } catch (IOException e) {
            System.out.println(" ERRO ao criar o arquivo final.");
            e.printStackTrace();
        }
    }

    /**
     * Distribui os Pokémon do arquivo binário para os arquivos temporários.
     * O primeiro binario eh o arquivo criado pela funcao CriarArquivo
     * na segunda repeticao ele ja usa o arquivo final criado pela intercalacao
     * 
     * @param caminhoArquivoBinario Caminho do arquivo binário
     * @param numArquivosTemp       Número de arquivos temporários
     */
    private static void distribuirParaArquivosTemporarios(String caminhoArquivoBinario, int numArquivosTemp) {

        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            raf.seek(0);
            raf.readInt();
            /*
             * isso aqui eh so para conseguir circular pelos arquivos temporarios
             * tem que colocar o nome deles em array
             */
            List<DataOutputStream> arquivosTemporarios = new ArrayList<>();
            List<String> nomesArquivosTemp = new ArrayList<>();
            int[] contadores = new int[numArquivosTemp];
            int totalLidos = 0;

            /*
             * Abrindo os arquivos temporarios e adicionando eles em uma lista
             */
            for (int i = 1; i <= numArquivosTemp; i++) {
                String nomeArquivo = "temp" + i + ".bin";
                arquivosTemporarios.add(new DataOutputStream(new FileOutputStream(nomeArquivo)));
                nomesArquivosTemp.add(nomeArquivo);
            }

            int arquivoAtual = 0;
            int heapSize = 7;
            /*
             * Duas Heaps para distribuicao a primeira vai tentar fazer a maior Run possivel
             * e a segunda vai guardar os Pokemons que nao foram possiveis de serem
             * inseridos
             */
            PriorityQueue<Pokemon> minHeap = new PriorityQueue<>(Comparator.comparing(Pokemon::getName));
            PriorityQueue<Pokemon> nextRunHeap = new PriorityQueue<>(Comparator.comparing(Pokemon::getName));

            while (raf.getFilePointer() < raf.length() && minHeap.size() < heapSize) {
                Pokemon p = Leitura.lerPokemon(raf);
                if (p != null) {
                    minHeap.add(p);
                    totalLidos++;
                }
            }
            
            Pokemon ultimoSalvo = null;
            /*
             * Enquanto a heap nao estiver vazia ele vai adicionando os pokemons nos
             * arquivos temporarios de formar circular
             */
            while (!minHeap.isEmpty() || !nextRunHeap.isEmpty()) {
                List<Pokemon> bufferPokemons = new ArrayList<>();
                while (!minHeap.isEmpty()) {
                    Pokemon menorPokemon = minHeap.poll();
                    bufferPokemons.add(menorPokemon);
                    ultimoSalvo = menorPokemon;
                    if (raf.getFilePointer() < raf.length()) {
                        Pokemon proximoPokemon = Leitura.lerPokemon(raf);
                        if (proximoPokemon != null) {
                            totalLidos++;
                            if (ultimoSalvo == null || proximoPokemon.getName().compareTo(ultimoSalvo.getName()) >= 0) {
                                minHeap.add(proximoPokemon);
                            } else {
                                nextRunHeap.add(proximoPokemon);
                            }
                        }
                    }
                }

                if (!bufferPokemons.isEmpty()) {
                    System.out.println("📂 Salvando " + bufferPokemons.size() + " Pokémon no arquivo: "
                            + nomesArquivosTemp.get(arquivoAtual));
                    salvarPokemonsOrdenados(bufferPokemons, arquivosTemporarios.get(arquivoAtual),
                            nomesArquivosTemp.get(arquivoAtual));
                    contadores[arquivoAtual] += bufferPokemons.size();
                    arquivoAtual = (arquivoAtual + 1) % numArquivosTemp;
                }

                if (!nextRunHeap.isEmpty()) {
                    System.out.println("🔄 Iniciando nova run com Pokémon remanescentes...");
                    minHeap.addAll(nextRunHeap);
                    nextRunHeap.clear();
                    ultimoSalvo = null;
                }
            }

            for (DataOutputStream dos : arquivosTemporarios) {
                dos.close();
            }

            // Exibir o número total de Pokémon lidos e salvos em cada arquivo temporário
            System.out.println("\n Distribuição concluída com Replacement Selection!");
            System.out.println(" Total de Pokémon lidos: " + totalLidos);
            for (int i = 0; i < numArquivosTemp; i++) {
                System.out.println("📂 " + nomesArquivosTemp.get(i) + " contém " + contadores[i] + " Pokémon.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Ordena o arquivo final com Replacement Selection Utilizando apenas um arquivo
     * final para a ordenacao dos Pokemons.
     * novamente esse codigo foi criado com ajuda de ia entao os Sysout eu vou deixar porque fica bonito
     * @param numArquivosTemp numero de arquivos temporarios
     * @param caminhoArquivoBinario caminho do arquivo binario
     * @param caminhoArquivoBinarioFinal caminho do arquivo final
     */
    private static void ordenarArquivoFinal(int numArquivosTemp, String caminhoArquivoBinario,
            String caminhoArquivoBinarioFinal) {
        List<String> arquivosTemporarios = new ArrayList<>();
        for (int i = 1; i <= numArquivosTemp; i++) {
            arquivosTemporarios.add("temp" + i + ".bin");
        }

        criarArquivosTemporarios(numArquivosTemp);
        String caminhoArquivoAtual = caminhoArquivoBinario;
        int iteracao = 1;
        while (true) {
            System.out.println("\n🔄 Iteração " + iteracao + ": Distribuindo Pokémon...");
            distribuirParaArquivosTemporarios(caminhoArquivoAtual, numArquivosTemp);

            System.out.println("\n🔄 Iteração " + iteracao + ": Intercalando arquivos...");
            intercalarArquivosTemporarios(arquivosTemporarios, caminhoArquivoBinarioFinal);

            if (arquivosTemporarios.stream().skip(1).allMatch(arquivo -> new File(arquivo).length() == 0)) {
                System.out.println("✅ Ordenação concluída! Pokémon organizados corretamente.");
                break;
            }

            caminhoArquivoAtual = caminhoArquivoBinarioFinal;
            iteracao++;
        }
        System.out.println("\n Removendo arquivos temporários...");
        for (String arquivo : arquivosTemporarios) {
            File tempFile = new File(arquivo);
            if (tempFile.exists() && tempFile.delete()) {
                System.out.println("🗑️ Arquivo deletado: " + arquivo);
            } else {
                System.out.println("⚠️ Não foi possível deletar: " + arquivo);
            }
        }

        File pokemonBin = new File(caminhoArquivoBinario);
        if (pokemonBin.exists() && pokemonBin.delete()) {
            System.out.println(" Arquivo deletado: data/pokemon_bytes.bin");
        } else {
            System.out.println(" Não foi possível deletar: data/pokemon_bytes.bin");
        }
    }
    /**
     * Le os Pokemons do arquivo temporario ou arquivo final
     * @param caminhoArquivo caminho do arquivo binario
     */
    public static void lerPokemonsArquivoTemporario(String caminhoArquivo) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivo, "r")) {

            raf.seek(0);
            raf.readInt();
            long tamanhoArquivo = raf.length();

            while (raf.getFilePointer() < tamanhoArquivo) {
                if (raf.getFilePointer() >= tamanhoArquivo - 4) {
                    System.out.println("🏁 Fim do arquivo detectado! Nenhum Pokémon restante para ler.");
                    break;
                }
                raf.readInt();
                int tamanhoEntrada = raf.readInt();
                long posicaoAtual = raf.getFilePointer();
                long bytesRestantes = tamanhoArquivo - posicaoAtual;
                if (tamanhoEntrada > bytesRestantes) {
                    tamanhoEntrada = (int) bytesRestantes;
                }
                if (tamanhoEntrada <= 0 || (posicaoAtual + tamanhoEntrada) > tamanhoArquivo) {
                    return;
                }
                byte[] dados = new byte[tamanhoEntrada];
                try {
                    raf.readFully(dados);
                } catch (IOException e) {
                    break;
                }
                Pokemon p = reconstruirPokemonDeBytes(dados);
                System.out.println("📖 Pokémon lido: " + p.getName());
                raf.seek(raf.getFilePointer() - 4);
            }

            System.out.println("🏁 Fim do arquivo: " + caminhoArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Reconstrói um Pokémon a partir de um array de bytes.
     * @param dados Array de bytes que contem o pokemon estruturado
     * @return    Pokemon reconstruido
     */
    public static Pokemon reconstruirPokemonDeBytes(byte[] dados) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(dados);
                DataInputStream dis = new DataInputStream(bais)) {
            Pokemon p = new Pokemon(0, "", "", "", "", 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, "", 0, 0.0, 0.0, 0.0, 0.0, 0.0);
            p.setId(dis.readInt());
            p.setNumberPokedex(dis.readInt());
            p.setName(dis.readUTF());
            p.setType1(dis.readUTF());
            p.setType2(dis.readUTF());

            int habilidades = dis.readInt();
            StringBuilder habilidadesStr = new StringBuilder();
            for (int i = 0; i < habilidades; i++) {
                habilidadesStr.append(dis.readUTF());
                if (i < habilidades - 1)
                    habilidadesStr.append(", ");
            }
            p.setAbilities(habilidadesStr.toString());
            p.setHp(dis.readInt());
            p.setAtt(dis.readInt());
            p.setDef(dis.readInt());
            p.setSpa(dis.readInt());
            p.setSpd(dis.readInt());
            p.setSpe(dis.readInt());
            p.setBst(dis.readInt());
            p.setMean(dis.readDouble());
            p.setStandardDeviation(dis.readDouble());
            long epochTime = dis.readLong();
            p.setGeneration(Auxiliares.converterEpochParaData(epochTime));
            p.setCatchRate(dis.readInt());
            p.setLegendary(dis.readDouble());
            p.setMegaEvolution(dis.readDouble());
            p.setHeight(dis.readDouble());
            p.setWeight(dis.readDouble());
            p.setBmi(dis.readDouble());
            return p;
        } catch (IOException e) {
            System.out.println(" ERRO ao reconstruir Pokémon dos bytes.");
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Salva os Pokemons ordenados no arquivo temporario
     * @param pokemons Lista de Pokemons ordenados
     * @param dos     DataOutputStream para escrever os Pokemons
     * @param caminhoArquivoTemp Caminho do arquivo temporario que vai ser salvo
     */
    public static void salvarPokemonsOrdenados(List<Pokemon> pokemons, DataOutputStream dos,
            String caminhoArquivoTemp) {
        try {
            for (Pokemon p : pokemons) {
                Escrita.escreverEntrada(dos, p, caminhoArquivoTemp);
            }
        } catch (IOException e) {
            System.out.println(" ERRO ao salvar Pokémon no arquivo temporário.");
            e.printStackTrace();
        }
    }

    /**
     * Chamada para iniciar a ordenação dos Pokémon.
     * onde pergunta com quantos arquivo temporarios vai ser feita
     * @param scanner Scanner para entrada de dados
     * @param caminhoArquivoBinario Caminho do arquivo binário
     * @param caminhoArquivoBinarioFinal Caminho do arquivo binário final
     */
    public static void iniciarOrdenacao(Scanner scanner, String caminhoArquivoBinario,
            String caminhoArquivoBinarioFinal) {
        int numTemporarios;

        while (true) {
            System.out.print("🔢 Quantos arquivos temporários deseja usar? (2-7): ");
            numTemporarios = scanner.nextInt();
            if (numTemporarios >= 2 && numTemporarios <= 7) {
                break;
            }
            System.out.println("❌ Valor inválido! Escolha um número entre 2 e 7.");
        }

        ordenarArquivoFinal(numTemporarios, caminhoArquivoBinario, caminhoArquivoBinarioFinal);
    }
}
