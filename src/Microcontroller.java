import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public static ArrayList<ArrayList<String>> readFile(String nameOfFile) throws IOException {
        ArrayList<ArrayList<String>> FileLines = new ArrayList<ArrayList<String>>();
        BufferedReader bR = new BufferedReader(new FileReader(nameOfFile.concat(".txt")));
        String FileLine = bR.readLine();// to start the while loop and be updated inside;

        while (FileLine != null) {
            FileLines.add((toarraylist(FileLine.split(" "))));
            FileLine = bR.readLine();
        }
        bR.close();
        return FileLines;
    }

    public static ArrayList<String> toarraylist(String[] x) {// helper method array to arraylist

        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < x.length; i++) {
            result.add(x[i]);
        }
        return result;
    }

    public static void addToInstructionMemory(ArrayList<ArrayList<String>> Lines, short[] instruction) {
        for (int i = 0; i < Lines.size(); i++) {
            String intNumber = "";
            switch (Lines.get(i).get(0).toUpperCase()) { //opcode
                case "ADD":
                    intNumber = "0000";
                    break;
                case "SUB":
                    intNumber = "0001";
                    break;
                case "MUL":
                    intNumber = "0010";
                    break;
                case "MOVI":
                    intNumber = "0011";
                    break;
                case "BEQZ":
                    intNumber = "0100";
                    break;
                case "ANDI":
                    intNumber = "0101";
                    break;
                case "EOR":
                    intNumber = "0110";
                    break;
                case "BR":
                    intNumber = "0111";
                    break;
                case "SAL":
                    intNumber = "1000";
                    break;
                case "SAR":
                    intNumber = "1001";
                    break;
                case "LDR":
                    intNumber = "1011";
                    break;
                case "STR":
                    intNumber = "1100";
                    break;
            }

            if(!(Lines.get(i).get(1).charAt(0)>=48&&Lines.get(i).get(1).charAt(0)<=57)) {
                Integer registerOne = Integer.valueOf(Lines.get(i).get(1).substring(1));

                String extendedAdd = Integer.toBinaryString(registerOne);
                while (extendedAdd.length() < 6) {
                    extendedAdd = "0".concat(extendedAdd);
                }
                intNumber = intNumber.concat(extendedAdd);//first register
            }
            else{
                Integer registerOne = Integer.valueOf(Lines.get(i).get(1));

                String extendedAdd = Integer.toBinaryString(registerOne);
                while (extendedAdd.length() < 6) {
                    extendedAdd = "0".concat(extendedAdd);
                }
                intNumber = intNumber.concat(extendedAdd);//first register
            }
            if(!(Lines.get(i).get(1).charAt(0)>=48&&Lines.get(i).get(1).charAt(0)<=57)) {
                Integer registerTwo = Integer.valueOf(Lines.get(i).get(2).substring(1));
                String extendedAdd2 = Integer.toBinaryString(registerTwo);
                while (extendedAdd2.length() < 6) {
                    extendedAdd2 = "0".concat(extendedAdd2);
                }
                intNumber = intNumber.concat(extendedAdd2);//second register
                instruction[i] = Short.parseShort(intNumber, 2);
            }
            else{
                Integer registerTwo = Integer.valueOf(Lines.get(i).get(2));
                String extendedAdd2 = Integer.toBinaryString(registerTwo);
                while (extendedAdd2.length() < 6) {
                    extendedAdd2 = "0".concat(extendedAdd2);
                }
                intNumber = intNumber.concat(extendedAdd2);//second register
                instruction[i] = Short.parseShort(intNumber, 2);
            }
        }
    }

    public void parser() throws IOException {
        ArrayList<ArrayList<String>> Lines = readFile("test");
        addToInstructionMemory(Lines, instructions);
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

           switch (opcode) {
               case 0: //add
                   byte temp1 = Registers[r1];
                   byte temp2 = Registers[r2];
                   byte temp3 = (byte)(temp1 + temp2);
                   Registers[r1] =temp3;

                   if((temp1 + temp2)>Byte.MAX_VALUE) //C
                   {
                       SREG[3]=true;
                   }
                   else
                   {
                       SREG[3]=false;
                   }

                   byte mask = -128;
                   byte s1 = (byte) ((temp1&mask)>>7);
                   byte s2 = (byte) ((temp2&mask)>>7);
                   byte s3 = (byte) ((temp3&mask)>>7);
                   if(s1==s2 && s1!=s3)//V
                   {
                       SREG[4]=true;
                   }
                   else
                   {
                       SREG[4]=false;
                   }
                   if(temp3<0) //N
                   {
                       SREG[5]=true;
                   }
                   else
                   {
                       SREG[5]=false;
                   }
                   SREG[6] = SREG[5]^SREG[4]; //S
                   if(temp3 == 0) //Z
                   {
                       SREG[7]= true;
                   }
                   else
                   {
                       SREG[7]= false;
                   }

                   break;

               case 1://sub
                   r1 = (byte) (Registers[r1] - Registers[r2]);
                   if(r1 != 0){
                       SREG[0] = false;
                   }
                   SREG[1] = SREG[2] ^ SREG[3];

                   if ( r1 > Byte.MAX_VALUE)
                       SREG[4] = true;
                   else
                       SREG[4] = false;

                   break;

               case 2://mul
                   if ((Registers[r1] * Registers[r2]) > Byte.MAX_VALUE)
                       SREG[3] = true;
                   else
                       SREG[3] = false;
                   Registers[r1] = (byte) (Registers[r1] * Registers[r2]);
                   break;

               case 3://movI
                   Registers[r1] = Registers[r2];
                   break;

               case 4://BEQZ
                   if(r1 == 0) {
                       fetch = -1;
                       decode = -1;
                       String s = r2 + "0" + "0";
                       int i = Integer.parseInt(s);
                       r1 = (byte) i;
                       PC = (byte) (PC + 1 + r1);
                   }
                   break;

               case 5://ANDI
                   if ((Registers[r1] & r2) < 1) SREG[5] = true;
                   else SREG[5] = false;
                   Registers[r1] = (byte) (Registers[r1] & r2);
                   break;

               case 6://EOR
                   byte tem1 = Registers[r1];
                   byte tem2 = Registers[r2];
                   byte tem3 = (byte)(tem1 ^ tem2);
                   Registers[r1] =tem3;
                   if(tem3<0) //N
                   {
                       SREG[5]=true;
                   }
                   else {
                       SREG[5] = false;
                   }
                   if(tem3 == 0) //Z
                   {
                       SREG[7]= true;
                   }
                   else
                   {
                       SREG[7]= false;
                   }

                   break;

               case 7://BR
                   String rs1 = Registers[r1] + "";
                   String rs2 = Registers[r2] + "";
                   String rs3 = rs1 + rs2;
                   byte t1 = Byte.parseByte(rs3);
                   PC = t1;
                   fetch = -1;
                   decode = -1;
                   break;

               case 8://SAL
                   if ((Registers[r1] << r2) ==0) SREG[7] = true;
                   else SREG[7] = false;
                   Registers[r1] = (byte) (Registers[r1] << r2);
                   break;

               case 9://SAR
                   r1 = (byte) (r1 >> r2);
                   if(r1 != 0){
                       SREG[0] = false;
                   }
                   break;

               case 10://LDR
                   r1 = datamemory[r2];

                   break;
               case 11://STR
                   datamemory[r2] = r1;
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

    public byte[] getRegisters() {
        return Registers;
    }

    public void setRegisters(byte[] registers) {
        Registers = registers;
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







    public static void main(String[] args) throws IOException {

        Microcontroller MC = new Microcontroller();
        int n = MC.instructions.length;
        int clkCycles = 3 + ((n-1)*1);

        MC.parser();

        for(int i=0; i<n; i++){

            MC.execute();
            MC.decode();
            MC.fetch();

        }


    }

}
