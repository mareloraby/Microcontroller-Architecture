import java.util.*;


public class Memory {



    private short [] instructions = new short [1024];
    private byte [] datamemory = new byte [2048];
    private byte [] GPRS = new byte[64];
    private boolean [] SREG = new boolean[8]; //0 0 0 C V N S Z
    private byte PC = 0;

    public Memory(){



    }



    public short[] getInstructions() {
        return instructions;
    }

    public void setInstructions(short[] instructions) {
        this.instructions = instructions;
    }

    public byte[] getDatamemory() {
        return datamemory;
    }

    public void setDatamemory(byte[] datamemory) {
        this.datamemory = datamemory;
    }

    public byte[] getGPRS() {
        return GPRS;
    }

    public void setGPRS(byte[] GPRS) {
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

    public void setPC(byte PC) {
        this.PC = PC;
    }

    public static void main(String[] args) {

    }

}
