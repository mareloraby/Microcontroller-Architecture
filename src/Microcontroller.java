import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Microcontroller {


    private short[] instructions;
    private byte[] datamemory;
    private byte[] Registers;
    private boolean[] SREG;

    private short PC;

    private short fetch;
    private short decode;
    private short execute;
    private byte opcode;
    private byte r1;
    private byte r2;
    private int nofLines;

    private boolean waitBEQZFlag;
    private int clkCycles;
    private int n;
    private int i;


    public Microcontroller() {
        waitBEQZFlag = false;
        instructions = new short[1024]; //16 bit
        Arrays.fill(instructions, (short) -1);

        datamemory = new byte[2048];

        Registers = new byte[64];


        SREG = new boolean[8]; // 0 0 0 C V N S Z
        PC = 0;
        fetch = -1;
        decode = -1;
        execute = -1;
        nofLines = 0;
        clkCycles = 0;
        n = 0;
        i=0;

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
                    intNumber = "1010";
                    break;
                case "STR":
                    intNumber = "1011";
                    break;
            }

            if (!(Lines.get(i).get(1).charAt(0) >= 48 && Lines.get(i).get(1).charAt(0) <= 57)) {
                Integer registerOne = Integer.valueOf(Lines.get(i).get(1).substring(1));

                String extendedAdd = Integer.toBinaryString(registerOne);
                while (extendedAdd.length() < 6) {
                    extendedAdd = "0".concat(extendedAdd);
                }
                intNumber = intNumber.concat(extendedAdd);//first register
            } else {
                Integer registerOne = Integer.valueOf(Lines.get(i).get(1));

                String extendedAdd = Integer.toBinaryString(registerOne);
                while (extendedAdd.length() < 6) {
                    extendedAdd = "0".concat(extendedAdd);
                }
                intNumber = intNumber.concat(extendedAdd);//first register
            }
            if (!(Lines.get(i).get(2).charAt(0) >= 48 && Lines.get(i).get(2).charAt(0) <= 57)) {
                if (Lines.get(i).get(2).charAt(0) == 45) {//negative sign
                    byte registerTwo = Byte.valueOf(Lines.get(i).get(2));
                    String negativeNumberRegisterTwo = Integer.toBinaryString(0b00111111 & registerTwo);
                    intNumber = intNumber.concat(negativeNumberRegisterTwo);//second register
                    instruction[i] = Short.parseShort(intNumber, 2);
                } else {
                    Integer registerTwo = Integer.valueOf(Lines.get(i).get(2).substring(1));
                    String extendedAdd2 = Integer.toBinaryString(registerTwo);
                    while (extendedAdd2.length() < 6) {
                        extendedAdd2 = "0".concat(extendedAdd2);
                    }
                    intNumber = intNumber.concat(extendedAdd2);//second register
                    instruction[i] = Short.parseShort(intNumber, 2);
                }
            } else {
                Integer registerTwo = Integer.valueOf(Lines.get(i).get(2));
                String extendedAdd2 = Integer.toBinaryString(registerTwo);
                while (extendedAdd2.length() < 6) {
                    extendedAdd2 = "0".concat(extendedAdd2);
                }
                intNumber = intNumber.concat(extendedAdd2);//second register

                instruction[i] = //Short.parseShort(intNumber, 2);
                ((Integer) (Integer.parseInt(intNumber, 2))).shortValue();


            }
        }
    }


    public static String invertDigits(String binaryInt) {
        String result = binaryInt;
        result = result.replace("0", "j"); //temp replace 0s
        result = result.replace("1", "0"); //replace 1s with 0s
        result = result.replace("j", "1"); //put the 1s back in
        return result;
    }

    public void parser() throws IOException {
        ArrayList<ArrayList<String>> Lines = readFile("test");
        nofLines = Lines.size();
        addToInstructionMemory(Lines, instructions);
    }

    public boolean getCarry(byte x, int y){
        System.out.println("Byte val " + x);
        System.out.println("Integer Val "+y);

        if (x==y){
            return false;
        }
        else if(x<0 && y<0 && x>y){
            return true;
        }
        else if(x>=0 && y>=0 && x<y) {

            return y>Byte.MAX_VALUE;
        }
        else if(x<0 && y>=0){

            return false;

        }
        else if(x>=0 && y<0) {
            return true;
        }
        else return false;


    }

    void fetch() {
        System.out.println("Instruction number: " + PC);
        if (instructions[PC] != -1) {
            fetch = instructions[PC];
            System.out.println("Fetched the Instruction code: " + instructions[PC] + " from address: " + PC);
            PC++;//is it possible that or should we increment even if this instruction is empty?

            System.out.println();
        } else {
            fetch = -1;
        }
        //PC++; could/should it be here?
    }

    void decode() {

        if (fetch != -1) {

            decode = fetch;

            opcode = (byte) ((decode & 0b1111000000000000) >> 12);  // bits15:12

            byte decimalValue = 0;


                if ((decode & 0b0000000000100000) == 32) {
                    String invertedInt = invertDigits(Integer.toBinaryString(decode & 0b0000000000111111));
                    decimalValue = (byte) Integer.parseInt(invertedInt, 2);
                    decimalValue = Byte.parseByte(String.valueOf((decimalValue + 1) * -1));
                } else {
                    decimalValue = (byte) (decode & 0b0000000000111111);
                }



            r1 = (byte) ((decode & 0b0000111111000000) >> 6);  // bits15:12
            r2 = decimalValue;

            System.out.println("Rt IS " + decimalValue);

            System.out.println("Decoded the instruction code: " + decode + " which was in address: " + (PC - 1));
            System.out.println();


        } else {
            decode = -1;
        }
    }

    void execute() {


        if (decode != -1) {
            execute = decode;

            System.out.println("Executed Instruction code: " + execute + " which was in position: " + ((fetch == -1) ? (PC - 1) : PC - 2));
            System.out.println(" with OPCode: " + opcode + " R1: " + r1 + " and R2: " + r2);


            Boolean print = true;
            byte oldR1 = Registers[r1];


            switch (opcode) {
                case 0: //add
                    byte temp1 = Registers[r1];
                    byte temp2 = Registers[r2];

                    byte temp3 = (byte) (temp1 + temp2);

                    Registers[r1] = temp3;

                    //C

                    int y = temp1+temp2;
                    byte x = (byte) (temp1 + temp2);


                    SREG[3] = getCarry(x,y);

                    byte mask = -128;
                    byte s1 = (byte) ((temp1 & mask) >> 7);
                    byte s2 = (byte) ((temp2 & mask) >> 7);
                    byte s3 = (byte) ((temp3 & mask) >> 7);
                    //V
                    SREG[4] = s1 == s2 && s1 != s3;
                    //N
                    SREG[5] = temp3 < 0;
                    SREG[6] = SREG[5] ^ SREG[4]; //S
                    //Z
                    SREG[7] = temp3 == 0;

                    break;

                case 1://sub
                    boolean flag = Registers[r1] > 0 && Registers[r2] < 0;

                    temp1 = Registers[r1];
                    temp2 = Registers[r2];
                    temp3 = (byte) (temp1 - temp2);

                    Registers[r1] = (byte) (Registers[r1] - Registers[r2]);


                   // System.out.println(temp3 + " here");
                    //carry
                    y = temp1-temp2;
                    x = (byte) (temp1 - temp2);
                    SREG[3] = getCarry(x,y);


                   // SREG[3] = temp3 > Byte.MAX_VALUE;


                    mask = -128;
                    s1 = (byte) ((temp1 & mask) >> 7);
                    s2 = (byte) ((temp2 & mask) >> 7);
                    s3 = (byte) ((temp3 & mask) >> 7);
                    //V
                    SREG[4] = (s1 == s2) && (s1 != s3); //overflow

                    SREG[5] = temp3 < 0; //negative

                    SREG[6] = SREG[5] ^ SREG[4]; // signed

                    SREG[7] = Registers[r1] == 0; // zero


                    break;

                case 2://mul

                    int z = (Registers[r1] * Registers[r2]);
                    Registers[r1] = (byte) z;
                    //carry

                    SREG[3] = getCarry((byte) z,z);

                    //SREG[3] = z > Byte.MAX_VALUE;
                    SREG[5] = Registers[r1]< 0; //negative
                    SREG[7] = Registers[r1] == 0;



                    break;

                case 3://movI
                    Registers[r1] = r2;
                    break;

                case 4://BEQZ
                    if (Registers[r1] == 0) {
                        System.out.println("BEQZ is executed");
                        fetch = -1;
                        decode = -1;
                        Short extendedBranchValue = Short.valueOf(r2);
                        PC = Short.valueOf((short) (PC + 1 - 2 + extendedBranchValue));
                        //-2 is the two instructions
                        // that went into
                        // fetching and decoding and caused the PC to increment by 2,
                        // so now we are removing them again and just following the normal equation again
                        System.out.println("PC is now: " + PC);
                        waitBEQZFlag = true;

                        clkCycles = 3*(n - PC) + 1;
                        i = 1;

                    }
                    print = false;
                    break;

                case 5://ANDI


                    Registers[r1] = (byte) (Registers[r1] & (byte) r2);

                    SREG[5] = (Registers[r1] ) < 0;
                    SREG[7] = (Registers[r1] ) == 0;

                    break;

                case 6://EOR
                    byte tem1 = Registers[r1];
                    byte tem2 = Registers[r2];
                    byte tem3 = (byte) (tem1 ^ tem2);
                    Registers[r1] = tem3;
                    //N
                    SREG[5] = tem3 < 0;
                    //Z
                    SREG[7] = tem3 == 0;

                    break;

                case 7://BR


                    String rs1 = Integer.toBinaryString(Registers[r1]);
                    int number = Registers[r1];
                    while (rs1.length() < 8) {//sign extend incase positive
                        rs1 = "0".concat(rs1);
                    }
                    if (rs1.length() > 8) {//incase if negative then it would be 32 bits because set into int
                        rs1 = rs1.substring(32 - 8);
                    }
                    String rs2 = Integer.toBinaryString(Registers[r2]);
                    int number2 = Registers[r2];
                    if (rs2.length() > 8) {//incase if negative then it would be 32 bits because set into int
                        rs2 = rs2.substring(32 - 8);
                    }
                    while (rs2.length() < 8) {//sign extend incase positive
                        rs2 = "0".concat(rs2);
                    }
                    String rs3 = rs1 + rs2;
                    PC = ((Integer) (Integer.parseInt(rs3, 2))).shortValue(); //fix issue here that it won't accept this value, says its out of range
                    fetch = -1;
                    decode = -1;
                    print = false;
                    waitBEQZFlag = true;
                    System.out.println("PC is now: " + PC);

                    clkCycles = 3*(n - PC) + 1;
                    i = 1;


                    break;

                case 8://SAL

                    Registers[r1] = (byte) (Registers[r1] << r2);
                    SREG[7] = ((Registers[r1])) == 0;
                    SREG[5] = ((Registers[r1])) < 0;
                    break;

                case 9://SAR
                    Registers[r1] = (byte) (Registers[r1] >> r2);
                    if (Registers[r1] != 0) {
                        SREG[7] = false;
                    }

                    SREG[5] = Registers[r1] < 0;

                    break;

                case 10://LDR
                    Registers[r1] = datamemory[r2];

                    break;
                case 11://STR
                    datamemory[r2] = Registers[r1];
                    print = false;
                    System.out.println("Address in memory number " + r2 + " was changed to: " + datamemory[r2]);
                    break;

            }

            if (print) {

                System.out.println(" Register number " + r1 + " was changed to: " + Registers[r1] + " from: " + oldR1);
            }
            System.out.println();


        }

    }

    public static void main(String[] args) throws IOException {

        Microcontroller MC = new Microcontroller();
        MC.parser();

        MC.n = MC.nofLines;
        MC.clkCycles = 3 + ((MC.n - 1) * 1);


        for (MC.i = 1; MC.i < MC.clkCycles + 1; MC.i++) {
            System.out.println();

            System.out.println("In Clock Cycle number: " + MC.i);
            System.out.println();
            MC.execute();
            if (!MC.waitBEQZFlag) {

                MC.decode();
                MC.fetch();
            }
            MC.waitBEQZFlag = false;
            System.out.println("-----------------------------------");


        }
        System.out.println(" ");

        System.out.println("This was the last clock cycle");
        System.out.println(" ");
        System.out.println("//////////////////////////////////////////////////////////////////////");
        System.out.println(" ");

        System.out.println("The content of all registers are as follows:- ");

        System.out.println(" ");

        System.out.println("PC Register contains the value: " + MC.PC);
        System.out.println(" ");

        System.out.println("The Status Register (SREG) contains: ");

        System.out.println("Z: " + MC.SREG[7] + " S: " + MC.SREG[6] + " N: " + MC.SREG[5] + " V: " + MC.SREG[4] + " C: " + MC.SREG[3]);

        System.out.println(" ");

        System.out.println("The 64 General purpose registers (GPRS) contains: ");
        boolean emptyfRegisters = true;
        for (int i = 0; i < MC.Registers.length; i++) {
            if ((MC.Registers[i] != 0)) {
                emptyfRegisters = false;
                System.out.println(" Register #" + i + " contains: " + MC.Registers[i]);
            }
        }
        if (emptyfRegisters) {
            System.out.println("Nothing, the Registers are all of value 0");
        } else
            System.out.println("Any Other unmentioned address is of value 0");

        System.out.println(" ");
        System.out.println("-----------------------------------");
        System.out.println(" ");

        System.out.println("The 2048 Sized Data Memory contains: ");
        boolean emptyf = true;

        for (int i = 0; i < MC.datamemory.length; i++) {
            if (MC.datamemory[i] != 0) {
                emptyf = false;
                System.out.println(" Data Memory address #" + i + " contains: " + ((MC.datamemory[i] == -1) ? "Zero" : MC.datamemory[i]));
            }
        }
        if (emptyf) {
            System.out.println("Nothing, the Data Memory is all of value 0");
        } else {
            System.out.println("Any Other unmentioned address is of value 0");
        }
        System.out.println(" ");
        System.out.println("-----------------------------------");
        System.out.println(" ");
        System.out.println("The 1024 Sized Instruction Memory contains: ");
        boolean emptyfInstructions = true;
        for (int i = 0; i < MC.instructions.length; i++) {
            if (MC.instructions[i] != -1) {
                emptyfInstructions = false;
                System.out.println(" Instruction Memory address #" + i + " contains: " + ((MC.instructions[i] == -1) ? "NULL" : MC.instructions[i]));
            }
        }
        if (emptyfInstructions) {
            System.out.println("Nothing, the Data Memory is Empty");
        } else
            System.out.println("Any Other unmentioned address is empty");

        System.out.println(" ");
        System.out.println("-----------------------------------");
        System.out.println(" ");

    }


}
