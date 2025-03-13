import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class Entrega1 {

    // ==================================================================================================
    // ======================================== MÉTODOS Criacao
    // ==================================================================================================

    /**
     * Cria um arquivo binário a partir de um arquivo CSV.
     */
    private static void CriarArquivo() {
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
                Pokemon p = criarPokemonDoSplit(Separado);
                p.setId(ultimoId);
                ultimoId++;
                escreverEntrada(dos, p,"data/pokemon_bytes.bin");
            }
            AtualizarId(ultimoId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Pokemon criarPokemonDoSplit(List<String> Separado) {
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

    private static Pokemon CriarPokemonDoArquivo(RandomAccessFile raf) throws IOException {
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
        p.setGeneration(converterEpochParaData(raf.readLong()));
        p.setCatchRate(raf.readInt());
        p.setLegendary(raf.readDouble());
        p.setMegaEvolution(raf.readDouble());
        p.setHeight(raf.readDouble());
        p.setWeight(raf.readDouble());
        p.setBmi(raf.readDouble());

        return p;
    }

    // ================================================================================================
    // //
    // ======================================== MÉTODOS Leitura
    // ======================================= //
    // ================================================================================================
    // //
    private static void verificarBuracos() {
        List<String> buracos = new ArrayList<>();
    
        try (RandomAccessFile raf = new RandomAccessFile("data/pokemon_bytes.bin", "r")) {
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
    
    private static void lerTodasEntradas(String caminhoArquivoBinario) {
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
                System.out.println(tamanhoEntrada);
                if (cova == 1) {
                    raf.seek(posicaoInicio);
                    lerPokemon(raf);
                    contadorPokemons++;
                } else if (cova == 0) {
                    // Detecta buraco e pula corretamente
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

    private static Pokemon lerPokemon(RandomAccessFile raf) throws IOException {
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
        Pokemon p = CriarPokemonDoArquivo(raf);
        // System.out.println("Posição atual no arquivo: " + raf.getFilePointer() + " bytes.");
        return p;
    }
 
    private static Pokemon lerUltimoPokemon(String caminhoArquivoBinario) {
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
                    Pokemon p = CriarPokemonDoArquivo(raf);
                    ultimoPokemon = p;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ultimoPokemon;
    }

    private static Pokemon lerPokemonPorNumero(String caminhoArquivoBinario, int numeroEscolhido) {
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

                            return lerPokemon(raf);

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

    private static Pokemon encontrarEExcluirPokemon(String caminhoArquivoBinario, int numeroEscolhido) {
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
                        p.setGeneration(converterEpochParaData(raf.readLong()));
                        p.setCatchRate(raf.readInt());
                        p.setLegendary(raf.readDouble());
                        p.setMegaEvolution(raf.readDouble());
                        p.setHeight(raf.readDouble());
                        p.setWeight(raf.readDouble());
                        p.setBmi(raf.readDouble());
    
                        // Agora voltamos à posição inicial para marcar como buraco (cova = 0)
                        raf.seek(posicaoOriginal);
                        raf.writeInt(0);
    
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
    

    // ==================================================================================================
    // //
    // ======================================== MÉTODOS Auxiliares
    // ===================================== //
    // ==================================================================================================
    // //

    private static void atualizarPokemon(Scanner scanner) {
        String caminho = "data/pokemon_bytes.bin";
        System.out.print("Digite o ID do Pokémon que deseja atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Pokemon p = encontrarEExcluirPokemon(caminho, id);

        if (p == null) {
            System.out.println("Erro: Pokémon não encontrado.");
            return;
        }

        System.out.println("\nPokémon encontrado:");
        System.out.println("1 - Nome: " + p.getName());
        System.out.println("2 - Tipo 1: " + p.getType1());
        System.out.println("3 - Tipo 2: " + p.getType2());
        System.out.println("4 - Habilidades: " + p.getAbilities());
        System.out.print("\nEscolha um atributo para atualizar (1-4): ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        switch (escolha) {
            case 1:
                System.out.print("Digite o novo nome: ");
                p.setName(scanner.nextLine());
                break;
            case 2:
                System.out.print("Digite o novo Tipo 1: ");
                p.setType1(scanner.nextLine());
                break;
            case 3:
                System.out.print("Digite o novo Tipo 2: ");
                p.setType2(scanner.nextLine());
                break;
            case 4:
                atualizarHabilidades(p, scanner);
                break;
            default:
                System.out.println("Opção inválida.");
                return;
        }
        
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/pokemon_bytes.bin", true))) {
            int idPokemon = PegarIdUltimo(caminho);
            p.setId(idPokemon);
            AtualizarId(idPokemon);
            imprimirPokemon(p);
            escreverEntrada(dos, p,"data/pokemon_bytes.bin");
            System.out.println("Atualização concluída!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  private static void atualizarHabilidades(Pokemon p, Scanner scanner) {
    System.out.println("\nHabilidades atuais: " + p.getAbilities());
    System.out.println("1 - Adicionar habilidade");
    System.out.println("2 - Remover habilidade");
    System.out.print("Escolha uma opção: ");
    int escolha = scanner.nextInt();
    scanner.nextLine(); 
    List<String> habilidades = new ArrayList<>();
    if (!p.getAbilities().isEmpty()) {
        habilidades = new ArrayList<>(List.of(p.getAbilities().replace("'", "").split(", ")));
    }

    if (escolha == 1) {
        System.out.print("Digite a nova habilidade: ");
        String novaHabilidade = scanner.nextLine().trim();
        habilidades.add(novaHabilidade);
    } else if (escolha == 2) {
        if (habilidades.isEmpty()) {
            System.out.println("Este Pokémon não tem habilidades para remover.");
            return;
        }

        System.out.println("Selecione a habilidade para remover:");
        for (int i = 0; i < habilidades.size(); i++) {
            System.out.println((i + 1) + " - '" + habilidades.get(i) + "'"); 
        }
        System.out.print("Digite o número da habilidade: ");
        int remover = scanner.nextInt();
        scanner.nextLine(); 

        if (remover >= 1 && remover <= habilidades.size()) {
            habilidades.remove(remover - 1);
        } else {
            System.out.println("Opção inválida.");
            return;
        }
    } else {
        System.out.println("Opção inválida.");
        return;
    }

    p.setAbilities(habilidades.stream()
            .map(h -> "'" + h + "'") 
            .collect(Collectors.joining(", ")));

    System.out.println("Habilidades atualizadas: " + p.getAbilities());
}

    private static void imprimirPokemon(Pokemon p) {
        System.out.print("ID: " + p.getId() + " ");
        System.out.print("Number Pokedex: " + p.getNumberPokedex() + " ");
        System.out.print("Name: " + p.getName() + " ");
        System.out.print("Type1: " + p.getType1() + " ");
        System.out.print("Type2: " + p.getType2() + " ");
        System.out.print("Abilities: " + p.getAbilities() + " ");
        System.out.print("HP: " + p.getHp() + " ");
        System.out.print("ATT: " + p.getAtt() + " ");
        System.out.print("DEF: " + p.getDef() + " ");
        System.out.print("SPA: " + p.getSpa() + " ");
        System.out.print("SPD: " + p.getSpd() + " ");
        System.out.print("SPE: " + p.getSpe() + " ");
        System.out.print("BST: " + p.getBst() + " ");
        System.out.print("Mean: " + p.getMean() + " ");
        System.out.print("StdDev: " + p.getStandardDeviation() + " ");
        System.out.print("Generation: " + p.getGeneration() + " ");
        System.out.print("Catch Rate: " + p.getCatchRate() + " ");
        System.out.print("Legendary: " + p.getLegendary() + " ");
        System.out.print("Mega Evolution: " + p.getMegaEvolution() + " ");
        System.out.print("Height: " + p.getHeight() + " ");
        System.out.print("Weight: " + p.getWeight() + " ");
        System.out.println("BMI: " + p.getBmi() + " ");
    }

    private static int PegarIdUltimo(String caminhoArquivoBinario) {
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
            return raf.readInt() + 1;
        } catch (IOException e) {
            System.out.println("Erro ao ler o último ID. Retornando 1 como fallback.");
            return 1;
        }
    }

    private static void setIdArquivo(Pokemon p, int id) {
        p.setId(id);
    }

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

    private static List<String> SplitInteligente(String linha) {
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

    // =================================================================================================
    // //
    // ======================================== MÉTODOS Escrita
    // ======================================== //
    // =================================================================================================
    // //
    private static void escreverEntrada(RandomAccessFile raf, Pokemon p, String caminhoArquivoBinario) throws IOException {
    long posicao = raf.getFilePointer(); // Posição antes de escrever

    raf.writeInt(1); // Cova = 1 (entrada válida)

    // Criação de um buffer temporário para calcular tamanho da entrada
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

    System.out.println("✅ Pokémon escrito na posição " + posicao + " (tamanho: " + tamanhoEntrada + " bytes)");
}

    private static void escreverPokemon(DataOutputStream dos, Pokemon p, String caminhoArquivo) throws IOException {
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
    
        // Agora, ele escreve no arquivo correto passado como parâmetro
        try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivo, "rw")) {
            raf.seek(posicaoInicio);
            raf.writeInt(bytesEscritos);
        }
    }
    
    private static void escreverEntrada(DataOutputStream dos, Pokemon p,String caminho) throws IOException {
        escreverCova(dos);
        escreverPokemon(dos, p,caminho);
    }

    private static void escreverCova(DataOutputStream dos) throws IOException {
        Random random = new Random();
        
        // 90% dos Pokémon terão cova = 1 (vivos), 10% terão cova = 0 (removidos)
        int cova = (random.nextInt(100) < 90) ? 1 : 0;
        
        dos.writeInt(cova);
    }

    private static long EscreverHoraBytes(String data) {
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

    // ==================================================================================================
    // //
    // ======================================== MÉTODOS CRUD
    // ============================================ //
    // ==================================================================================================
    // //
    public static void CREATE() {
        String caminhoArquivoCSV = "data/dados_modificados.csv";
        String caminhoArquivoBinario = "data/pokemon_bytes.bin";
    
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivoCSV));
             RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "rw")) {
    
            br.readLine(); // Ignora o cabeçalho
            String linhaAleatoria = null;
            String linhaAtual;
            Random random = new Random();
            int linhaIndex = 0;
    
            while ((linhaAtual = br.readLine()) != null) {
                linhaIndex++;
                if (random.nextInt(linhaIndex) == 0) {
                    linhaAleatoria = linhaAtual;
                }
            }
    
            if (linhaAleatoria == null) {
                System.out.println("❌ O arquivo CSV está vazio.");
                return;
            }
    
            linhaAleatoria = posicoesVazias(linhaAleatoria);
            List<String> Separado = SplitInteligente(linhaAleatoria);
            Pokemon p = criarPokemonDoSplit(Separado);
    
            // Pega o último ID de forma segura
            int ultimoId = PegarIdUltimo(caminhoArquivoBinario);
            setIdArquivo(p, ultimoId);
    
            // 📌 Garante que o novo Pokémon seja escrito corretamente no final do arquivo
            raf.seek(raf.length());
            escreverEntrada(raf, p, caminhoArquivoBinario);
            AtualizarId(ultimoId);
    
            System.out.println("✅ Novo Pokémon adicionado com sucesso!");
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void READ(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\nEscolha uma opção:");
            System.out.println("1 - Ler outro Pokémon");
            System.out.println("2 - Sair");
            System.out.print("Opção: ");

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    System.out.print("Digite um número entre 1 e 1000: ");
                    int numeroEscolhido = scanner.nextInt();

                    if (numeroEscolhido < 1) {
                        System.out.println("Número inválido. Deve ser maior que 0.");
                        break;
                    }

                    Pokemon p = lerPokemonPorNumero("data/pokemon_bytes.bin", numeroEscolhido);
                    break;

                case 2:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 2);
    }

    public static void UPDATE(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\n=== Menu de Atualização ===");
            System.out.println("1 - Atualizar um Pokémon");
            System.out.println("2 - Verificar buracos");
            System.out.println("3 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    System.out.println("\nOpção: Atualizar um Pokémon.");
                    atualizarPokemon(scanner);
                    break;
                case 2:
                    System.out.println("\nOpção: Verificar buracos.");
                    verificarBuracos();
                    break;
                case 3:
                    System.out.println("Saindo do menu de atualização...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 3);

    }

    // ================================================================================================== //
    // ======================================== MÉTODO Organizacao ============================================ //
    // ================================================================================================== //

    private static void criarArquivosTemporarios(int numArquivos) {
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
        }}
        
    private static void intercalarArquivosTemporarios(List<String> arquivosTemporarios, String caminhoArquivoFinal) {
            try (DataOutputStream dosFinal = new DataOutputStream(new FileOutputStream(caminhoArquivoFinal))) {
                System.out.println("\n=== 🔄 Iniciando Intercalação com Replacement Selection ===");
        
                dosFinal.writeInt(0);
                List<RandomAccessFile> arquivos = new ArrayList<>();
                for (String arquivo : arquivosTemporarios) {
                    arquivos.add(new RandomAccessFile(arquivo, "r"));
                }
        
                PriorityQueue<PokemonEntry> minHeap = new PriorityQueue<>(Comparator.comparing(entry -> entry.pokemon.getName()));
                PriorityQueue<PokemonEntry> nextRunHeap = new PriorityQueue<>(Comparator.comparing(entry -> entry.pokemon.getName()));
        
                // 🔹 Adicionamos até 7 Pokémon no heap principal
                int heapSize = 7;
                for (int i = 0; i < arquivos.size(); i++) {
                    RandomAccessFile raf = arquivos.get(i);
                    if (raf.getFilePointer() < raf.length() && minHeap.size() < heapSize) {
                        Pokemon p = lerPokemon(raf);
                        if (p != null) {
                            minHeap.add(new PokemonEntry(p, i));
                        }
                    }
                }
        
                int contadorIteracao = 0;
                Pokemon ultimoSalvo = null;
        
                while (!minHeap.isEmpty() || !nextRunHeap.isEmpty()) {
                    int pokemonRemovidos = 0;
        
                    // 🔹 Enquanto houver Pokémon no heap principal
                    while (!minHeap.isEmpty()) {
                        PokemonEntry menorEntrada = minHeap.poll();
                        Pokemon menorPokemon = menorEntrada.pokemon;
                        int origemArquivo = menorEntrada.origemArquivo;
        
                        // 🔹 Escrevemos o menor Pokémon no arquivo final
                        escreverEntrada(dosFinal, menorPokemon, caminhoArquivoFinal);
                        pokemonRemovidos++;
        
                        // 🔹 O Pokémon removido se torna o último salvo
                        ultimoSalvo = menorPokemon;
        
                        // 🔹 Lemos o próximo Pokémon do mesmo arquivo
                        RandomAccessFile raf = arquivos.get(origemArquivo);
                        if (raf.getFilePointer() < raf.length()) {
                            Pokemon proximoPokemon = lerPokemon(raf);
                            if (proximoPokemon != null) {
                                if (ultimoSalvo == null || proximoPokemon.getName().compareTo(ultimoSalvo.getName()) >= 0) {
                                    // 🔹 Se o novo Pokémon for maior ou igual, continua na run atual
                                    minHeap.add(new PokemonEntry(proximoPokemon, origemArquivo));
                                } else {
                                    // 🔹 Se for menor, entra na heap para a próxima run
                                    nextRunHeap.add(new PokemonEntry(proximoPokemon, origemArquivo));
                                }
                            }
                        }
                    }
        
                    contadorIteracao++;
                    System.out.println("🔄 Iteração " + contadorIteracao + ": " + pokemonRemovidos + " Pokémon removidos da heap.");
        
                    // 🔄 Se a heap principal estiver vazia, iniciamos uma nova run com os Pokémon do nextRunHeap
                    if (!nextRunHeap.isEmpty()) {
                        System.out.println("🔄 Iniciando nova run com Pokémon remanescentes...");
                        minHeap.addAll(nextRunHeap);
                        nextRunHeap.clear();
                        ultimoSalvo = null; // 🔄 Resetamos para permitir novas comparações
                    }
                }
        
                // 🔹 Fechamos os arquivos temporários
                for (RandomAccessFile raf : arquivos) {
                    raf.close();
                }
        
                System.out.println("\n✅ Intercalação concluída com Replacement Selection! Dados salvos em: " + caminhoArquivoFinal);
        
            } catch (IOException e) {
                System.out.println("❌ ERRO ao criar o arquivo final.");
                e.printStackTrace();
            }
        }

    private static void distribuirParaArquivosTemporarios(String caminhoArquivoBinario, int numArquivosTemp) {
    try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivoBinario, "r")) {
        raf.seek(0);
        raf.readInt();

        List<DataOutputStream> arquivosTemporarios = new ArrayList<>();
        List<String> nomesArquivosTemp = new ArrayList<>();
        int[] contadores = new int[numArquivosTemp]; // Contadores para cada arquivo
        int totalLidos = 0; // Contador total de Pokémon lidos

        for (int i = 1; i <= numArquivosTemp; i++) {
            String nomeArquivo = "temp" + i + ".bin";
            arquivosTemporarios.add(new DataOutputStream(new FileOutputStream(nomeArquivo)));
            nomesArquivosTemp.add(nomeArquivo);
        }

        int arquivoAtual = 0;
        int heapSize = 7;  // Número máximo de Pokémon no heap
        PriorityQueue<Pokemon> minHeap = new PriorityQueue<>(Comparator.comparing(Pokemon::getName));
        PriorityQueue<Pokemon> nextRunHeap = new PriorityQueue<>(Comparator.comparing(Pokemon::getName));

        // 🔹 Preenchemos o heap inicial com até 7 Pokémon
        while (raf.getFilePointer() < raf.length() && minHeap.size() < heapSize) {
            Pokemon p = lerPokemon(raf);
            if (p != null) {
                minHeap.add(p);
                totalLidos++;
            }
        }

        Pokemon ultimoSalvo = null;

        while (!minHeap.isEmpty() || !nextRunHeap.isEmpty()) {
            List<Pokemon> bufferPokemons = new ArrayList<>();

            // 🔹 Processamos todos os Pokémon do heap principal antes de trocar de run
            while (!minHeap.isEmpty()) {
                Pokemon menorPokemon = minHeap.poll();
                bufferPokemons.add(menorPokemon);
                ultimoSalvo = menorPokemon;

                // 🔹 Pegamos o próximo Pokémon do arquivo e decidimos se continua na mesma run
                if (raf.getFilePointer() < raf.length()) {
                    Pokemon proximoPokemon = lerPokemon(raf);
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

            // 🔹 Salvamos os Pokémon processados no arquivo temporário atual
            if (!bufferPokemons.isEmpty()) {
                System.out.println("📂 Salvando " + bufferPokemons.size() + " Pokémon no arquivo: " + nomesArquivosTemp.get(arquivoAtual));
                salvarPokemonsOrdenados(bufferPokemons, arquivosTemporarios.get(arquivoAtual), nomesArquivosTemp.get(arquivoAtual));
                contadores[arquivoAtual] += bufferPokemons.size();
                arquivoAtual = (arquivoAtual + 1) % numArquivosTemp;
            }

            // 🔄 Se a heap principal estiver vazia, iniciamos uma nova run com os Pokémon do nextRunHeap
            if (!nextRunHeap.isEmpty()) {
                System.out.println("🔄 Iniciando nova run com Pokémon remanescentes...");
                minHeap.addAll(nextRunHeap);
                nextRunHeap.clear();
                ultimoSalvo = null;  // 🔄 Resetamos para permitir novas comparações
            }
        }

        for (DataOutputStream dos : arquivosTemporarios) {
            dos.close();
        }

        // Exibir o número total de Pokémon lidos e salvos em cada arquivo temporário
        System.out.println("\n✅ Distribuição concluída com Replacement Selection!");
        System.out.println("📊 Total de Pokémon lidos: " + totalLidos);
        for (int i = 0; i < numArquivosTemp; i++) {
            System.out.println("📂 " + nomesArquivosTemp.get(i) + " contém " + contadores[i] + " Pokémon.");
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private static void ordenarArquivoFinal() {
            List<String> arquivosTemporarios = Arrays.asList("temp1.bin", "temp2.bin", "temp3.bin", "temp4.bin");
        
            criarArquivosTemporarios(4);  // Criamos os arquivos vazios antes da primeira distribuição
            String caminhoArquivoAtual = "data/pokemon_bytes.bin"; // Começamos a partir do arquivo original
            int iteracao = 1;
            while (true) {
 
                System.out.println("\n🔄 Iteração " + iteracao + ": Distribuindo Pokémon...");
                distribuirParaArquivosTemporarios(caminhoArquivoAtual, 4);
                System.out.println("\n🔄 Iteração " + iteracao + ": Intercalando arquivos...");
                intercalarArquivosTemporarios(arquivosTemporarios, "arquivofinal.bin");
        
                // 📌 Verifica se os arquivos temporários além do temp1.bin estão vazios
                if (arquivosTemporarios.stream().skip(1).allMatch(arquivo -> new File(arquivo).length() == 0)) {
                    System.out.println("✅ Ordenação concluída! Pokémon organizados corretamente.");
                    break;
                }
        
                // Atualizamos o caminho do arquivo para a próxima rodada
                caminhoArquivoAtual = "arquivofinal.bin";
                iteracao++;
            }
        }

    private static void lerPokemonsArquivoTemporario(String caminhoArquivo) {
            try (RandomAccessFile raf = new RandomAccessFile(caminhoArquivo, "r")) {
                
                raf.seek(0);
                raf.readInt();
                long tamanhoArquivo = raf.length(); // 📏 Tamanho total do arquivo
        
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
        
    private static Pokemon reconstruirPokemonDeBytes(byte[] dados) {
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
                    if (i < habilidades - 1) habilidadesStr.append(", ");
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
                p.setGeneration(converterEpochParaData(epochTime));
        
                p.setCatchRate(dis.readInt());
                p.setLegendary(dis.readDouble());
                p.setMegaEvolution(dis.readDouble());
                p.setHeight(dis.readDouble());
                p.setWeight(dis.readDouble());
                p.setBmi(dis.readDouble());
        
                return p;
            } catch (IOException e) {
                System.out.println("❌ ERRO ao reconstruir Pokémon dos bytes.");
                e.printStackTrace();
                return null;
            }
        }
        
    private static void salvarPokemonsOrdenados(List<Pokemon> pokemons, DataOutputStream dos, String caminhoArquivoTemp) {
               try {
                for (Pokemon p : pokemons) {
                    escreverEntrada(dos, p, caminhoArquivoTemp); 
                }
            } catch (IOException e) {
                System.out.println("❌ ERRO ao salvar Pokémon no arquivo temporário.");
                e.printStackTrace();
            }
        }
    
    // ==================================================================================================
    // //
    // ============================================ MAIN
    // ================================================ //
    // ==================================================================================================
    // //
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CriarArquivo();
        Pokemon ultimo ;
        // System.out.println("Último Pokémon: " + ultimo.getName());
        // lerTodasEntradas("data/pokemon_bytes.bin");
        System.out.println("🚀 Iniciando o programa...");
        ultimo = lerUltimoPokemon("data\\pokemon_bytes.bin");
        System.out.println(ultimo.getName());
        CREATE();
        ultimo = lerUltimoPokemon("data\\pokemon_bytes.bin");
        System.out.println(ultimo.getName());
        // lerTodasEntradas("data/pokemon_bytes.bin");
        // ultimo = lerUltimoPokemon("data\\pokemon_bytes.bin");
        // System.out.println("Último Pokémon: " + ultimo.getName());
        // READ(scanner);
        verificarBuracos();
        // UPDATE(scanner);
        // lerTodasEntradas("data/pokemon_bytes.bin");
        // ordenarArquivoFinal();
        // lerPokemonsArquivoTemporario("arquivofinal.bin");
        scanner.close();

    }

}
