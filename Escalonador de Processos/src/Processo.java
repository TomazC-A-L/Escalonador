public class Processo {
    private int quantum;
    private int chegada;
    private int CPUtime;
    private int awaiting;
    private int start =  -1;
    private int end = 0;
    private int wait = 0;

    public Processo(int quantum, int chegada, int CPUtime) {
        this.quantum = quantum;
        this.chegada = chegada;
        this.CPUtime = CPUtime;
        this.awaiting = CPUtime;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getWait() {
        return wait;
    }
    public void setWait(int wait) {
        this.wait = wait;
    }

    public int getChegada() {
        return chegada;
    }

    public int getCPUtime() {
        return CPUtime;
    }

    public int getAwaiting() {
        return awaiting;
    }

    public int getEnd() {
        return end;
    }
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setAwaiting(int awaiting) {
        this.awaiting = awaiting;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
