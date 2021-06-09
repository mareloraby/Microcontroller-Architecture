import java.util.*;


public class Microcontroller {


    private short [] instructions;
    private byte [] datamemory;
    private byte [] Registers;
    private boolean [] SREG;

    private byte PC;

    private short fetch;
    private short decode;
    private short execute;
    private byte opcode;
    private byte r1;
    private byte r2;

    public Microcontroller(){
        instructions = new short [1024]; //16 bit
        Arrays.fill(instructions, (short) -1);

        datamemory = new byte [2048];
        Registers = new byte[64];
        SREG = new boolean[8]; //0 0 0 C V N S Z
        PC = 0;
        fetch =-1;
        decode=-1;
        execute=-1;

    }


    void parser(){


    }

    void fetch(){
        if ( instructions[PC]!= -1) {
            fetch = instructions[PC];
            PC++;
        }

    }

    void decode(){

        if (fetch!=-1) {
            decode = fetch;

            opcode = (byte) ((decode & 0b1111000000000000) >> 12);  // bits15:12
            System.out.println(opcode);

            r1 = (byte) ((decode & 0b0000111111000000) >> 6);  // bits15:12
            r2 = (byte) ((decode & 0b0000000000111111));  // bits11:0
        }
    }

    void execute(){

       if (decode!= -1) {
           execute = decode;
           //
           // status registers


           switch (opcode) {

               case 0: //add


                   break;
               case 1://sub

                   break;
               case 2://mul

                   break;
               case 3://movI

                   break;
               case 4://BEQZ

                   //empty decode and execute

                   break;
               case 5://ANDI

                   break;
               case 6://EOR

                   break;
               case 7://BR

                   break;
               case 8://SAL

                   break;
               case 9://SAR

                   break;
               case 10://LDR

                   break;
               case 11://STR

                   break;

           }
       }

    }


    public short[] getInstructions() { return instructions; }

    public void setInstructions(short[] instructions) {
        this.instructions = instructions;
    }

    public byte[] getDatamemory() {
        return datamemory;
    }

    public void setDatamemory(byte[] datamemory) {
        this.datamemory = datamemory;
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

        Microcontroller MC = new Microcontroller();
        int n = MC.instructions.length;
        int clkCycles = 3 + ((n-1)*1);


        for(int i=0; i<n; i++){

            MC.execute();
            MC.decode();
            MC.fetch();




        }





    }

}
