import java.util.ArrayList;
import java.util.List;

public class Escalonador{

    public static String calcularFifo(List<Processo> lista) {
        
        List<Integer> respostas = new ArrayList<>();
        List<Integer> esperas = new ArrayList<>();
        List<Integer> turnarounds = new ArrayList<>();
        
        int tempo = 0;
        for (Processo p : lista) {
            
            int[] metricas = metricas(p, tempo);
            
            respostas.add(metricas[0]);
            esperas.add(metricas[1]);
            turnarounds.add(metricas[2]);

            tempo = metricas[3];
        }
        return buildString(calcularMedia(esperas), calcularMedia(respostas), calcularMedia(turnarounds));
    }

    public static String calcularSJF(List<Processo> lista) {

        List<Integer> respostas = new ArrayList<>();
        List<Integer> esperas = new ArrayList<>();
        List<Integer> turnarounds = new ArrayList<>();
        
        int tempo = 0;
        int finalizados = 0;

        List<Processo> executando = new ArrayList<>(lista);
        //grafico de gantt
        while(finalizados < lista.size()){

            Processo aux = null;
            for (Processo p : executando) 
                if(p.getChegada() <= tempo) 
                    if(aux == null || p.getCPUtime() < aux.getCPUtime())
                        aux = p;
            
            //periodos ociosas
            if(aux == null){
                tempo++;
                continue;
            }
            executando.remove(aux);

            int[] metricas = metricas(aux, tempo);

            respostas.add(metricas[0]);
            esperas.add(metricas[1]);
            turnarounds.add(metricas[2]);

            tempo = metricas[3];
            finalizados++;
        }
        return buildString(calcularMedia(respostas), calcularMedia(esperas), calcularMedia(turnarounds));
    }

    public static String calcularSRT(List<Processo> lista) {

        List<Integer> respostas = new ArrayList<>();
        List<Integer> esperas = new ArrayList<>();
        List<Integer> turnarounds = new ArrayList<>();

        List<Processo> queued = new ArrayList<>();
        List<Processo> finalizados = new ArrayList<>();

        int tempo = 0;
        while(!lista.isEmpty() || !queued.isEmpty()){

            //grafico de gantt
            for (int i = 0; i < lista.size(); i++) 
                if(lista.get(i).getChegada() <= tempo){
                    queued.add(lista.get(i));
                    lista.remove(i);
                    i--;
                }

            if(!queued.isEmpty()){
                ordenarPreemptivo(queued);
                Processo aux = queued.get(0);

                //muda se for a primeira execução
                if (aux.getStart() == -1) { aux.setStart(tempo); }
                
                aux.setAwaiting(aux.getAwaiting() -1);
                tempo++;

                if(aux.getAwaiting() == 0) {
                    aux.setEnd(tempo);
                    finalizados.add(aux);
                    queued.remove(aux);
                }
            } else {
                tempo ++;
            }
        }

        for (Processo p : finalizados) {
            int turnaround = p.getEnd() - p.getChegada();
            int espera = turnaround - p.getCPUtime();
            int resposta = p.getStart() - p.getChegada();

            respostas.add(resposta);
            esperas.add(espera);
            turnarounds.add(turnaround);
        }
        return buildString(calcularMedia(esperas), calcularMedia(respostas), calcularMedia(turnarounds));
    }

    public static String calcularRR(List<Processo> lista) {
        List<Processo> fila = new ArrayList<>();
        List<Integer> respostas = new ArrayList<>();
        List<Integer> esperas = new ArrayList<>();
        List<Integer> turnarounds = new ArrayList<>();
    
        int tempo = 0;
        List<Processo> processos = new ArrayList<>(lista);
    
        while (!processos.isEmpty() || !fila.isEmpty()) {
            //bota os processos na fila e corrige o index
            for (int i = 0; i < processos.size(); i++) {
                if (processos.get(i).getChegada() <= tempo) {
                    fila.add(processos.remove(i));
                    i--;
                }
            }
    
            if (!fila.isEmpty()) {
                Processo atual = fila.remove(0);
    
                if (atual.getStart() == -1) {
                    atual.setStart(tempo);
                }
    
                int tempoExecucao = Math.min(atual.getAwaiting(), atual.getQuantum());
                atual.setAwaiting(atual.getAwaiting() - tempoExecucao);
                tempo += tempoExecucao;
    
                if (atual.getAwaiting() > 0) {
                    fila.add(atual);
                } else {
                    atual.setEnd(tempo);
                    int turnaround = atual.getEnd() - atual.getChegada();
                    int espera = turnaround - atual.getCPUtime();
                    int resposta = atual.getStart() - atual.getChegada();
    
                    turnarounds.add(turnaround);
                    esperas.add(espera);
                    respostas.add(resposta);
                }
            } else {
                tempo++;
            }
        }
        return buildString(calcularMedia(esperas), calcularMedia(respostas), calcularMedia(turnarounds));
    }


    public static List<Processo> ordenar(List<Processo> lista) {
        int cont = 0;
        for (Processo p : lista) {
            for (int i = cont + 1; i < lista.size(); i++) {
                if(p.getChegada() > lista.get(i).getChegada()){
                    Processo temp = lista.get(i);
                    lista.add(lista.indexOf(p), lista.get(i));
                    lista.add(i, temp);
                }
            }
            cont++;
        }
        return lista;
    }

    public static List<Processo> ordenarPreemptivo(List<Processo> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            for (int j = 0; j < lista.size() - i - 1; j++) {
                if (lista.get(j).getAwaiting() > lista.get(j + 1).getAwaiting()) {
                    // Troca os elementos
                    Processo temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        return lista;
    }


    private static int[] metricas(Processo processo, int instante){
        int[] metricas = new int[4];

        int start = (instante > processo.getChegada()) ? instante : processo.getChegada();
        int end = start + processo.getCPUtime();
        
        int turnaround = end - processo.getChegada();
        int espera = turnaround - processo.getCPUtime();
        int resposta = start - processo.getChegada();

        metricas[0] = resposta;
        metricas[1] = espera;
        metricas[2] = turnaround;
        metricas[3] = end;

        return metricas;
    }

    private static String buildString (double espera, double resposta, double turnaround) {
        String tempos =  String.format("%.3f", resposta) + " ";
        tempos += String.format("%.3f", espera) + " ";
        tempos += String.format("%.3f", turnaround);

        return tempos;
    }

    private static double calcularMedia(List<Integer> nums){
        int soma = 0;
        int cont = 0;
        for (Integer num : nums) {
            soma += num;
            cont++;
        }
        return (double)soma/cont;
    }
}   
