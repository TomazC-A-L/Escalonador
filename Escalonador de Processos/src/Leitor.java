import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Leitor{

    private static void escreverArquivo(String resultado[], String path) throws IOException {
        BufferedWriter bw =  new BufferedWriter(new FileWriter(path));
        for (int i = 0; i < resultado.length; i++)
            bw.write(resultado[i] + "\n");
        bw.close();
    }
    public static void lerArquivos() throws IOException{

        for (int i = 1; i <= 3 ; i++) {
            
            //cria o caminho do arquivo (alterar diretorio aqui)
            String fileNum = String.format("%02d",i);
            String caminhoArquivo = "testes\\TESTE-" + fileNum + ".txt";
            //lista pra armazenar os processos e enviar para o escalonador
            ArrayList<Processo> processos = new ArrayList<>();
            ArrayList<Processo> processos2  =new ArrayList<>();
            //le o arquivo atual
            try {
                List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo));

                // pega o quantum
                int quantum = Integer.parseInt(linhas.get(0));
                System.out.println("Arquivo: " + caminhoArquivo);


                // le o resto
                for (int j = 1; j < linhas.size(); j++) {
                    String[] procesData = linhas.get(j).split(" ");

                    //blindar contra numeros muito grandes
                    try {
                        long c = Long.parseLong(procesData[0]);
                        long d = Long.parseLong(procesData[1]);
    
                        if(c < 0 || c > 1000 || d < 1 || d > 1000) 
                            System.out.println("Linha " + (j + 1) + " contém números fora dos limites: " + c + " " + d);
                        else 
                            System.out.println(c + " " + d);
                        
                        processos.add(new Processo(quantum, (int)c , (int)d));  
                        processos2.add(new Processo(quantum, (int)c , (int)d));  
                    } catch (NumberFormatException e){
                        System.out.println("Linha " + (j + 1) + " contém números inválidos: " + procesData[0] + " " + procesData[1]);
                    }

                }
                System.out.println();
                String resultado [] = new String[4];
                resultado[0] = Escalonador.calcularFifo(Escalonador.ordenar(new ArrayList<>(processos)));
                resultado[1] = Escalonador.calcularSJF(Escalonador.ordenar(new ArrayList<>(processos)));
                resultado[2] = Escalonador.calcularSRT(Escalonador.ordenar(new ArrayList<>(processos)));
                resultado[3] = Escalonador.calcularRR(Escalonador.ordenar(new ArrayList<>(processos2)));

                String newPath = "testes\\TESTE-" + fileNum + "RESULTADO.txt";
                escreverArquivo(resultado, newPath);

            } catch (IOException e) {
                System.out.println("Erro ao ler o arquivo: " + caminhoArquivo);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Erro ao converter números no arquivo: " + caminhoArquivo);
                e.printStackTrace();
            }
        }
    }
}
