import java.util.*;


public class Memory {



    private int [] instructions = new int [1024];
    private int [] datamemory = new int [2048];
    private int [] GPRS = new int[64];
    private boolean [] SREG = new boolean[8]; //0 0 0 C V N S Z
    private int PC = 0;

    public Memory(){



    }



    public int[] getInstructions() {
        return instructions;
    }

    public void setInstructions(int[] instructions) {
        this.instructions = instructions;
    }

    public int[] getDatamemory() {
        return datamemory;
    }

    public void setDatamemory(int[] datamemory) {
        this.datamemory = datamemory;
    }

    public int[] getGPRS() {
        return GPRS;
    }

    public void setGPRS(int[] GPRS) {
        this.GPRS = GPRS;
    }

    public boolean[] getSREG() {
        return SREG;
    }

    public void setSREG(boolean[] SREG) {
        this.SREG = SREG;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public static void main(String[] args) {

    }

}
