import com.sun.tools.javac.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final HashMap<String,String> opcodeMap = new HashMap<>();

    static {
        opcodeMap.put("LDBA","D0");
        opcodeMap.put("STBA","F1");
        opcodeMap.put("STWA","E1");
        opcodeMap.put("LDWA","C0");
        opcodeMap.put("ASLA","0A");
        opcodeMap.put("ASRA","0C");
        opcodeMap.put("ADDA","60");
        opcodeMap.put("CPBA","B0");
        opcodeMap.put("BRNE","1A");
    }
    public static void main(String[] args) throws IOException {
        String filePath = "/Users/benwilson/Downloads/Project - Assembler/code/program3.pep";
        String documentContent = Files.readString(Paths.get(filePath));
        System.out.println(documentContent);

        Pattern mainPattern = Pattern.compile("([A-Z]+)\\s(0x[0-9A-Fa-f]+),\\s(i|d)"); // square brackets any character inside them, parentheses allow alteration, so i or d
        Pattern stopPattern = Pattern.compile("(STOP)");
        Pattern endPattern = Pattern.compile("\\.END");
        Matcher matcher = mainPattern.matcher(documentContent);
        StringBuilder machineCode = new StringBuilder();

        while (matcher.find()){
            String opcode = matcher.group(1);
            String address = matcher.group(2); /// numbers
            String mode = matcher.group(3);
            String machineCodeOpcode = opcodeMap.get(opcode);
            if (machineCodeOpcode != null) {
                machineCode.append(machineCodeOpcode).append(" "); // adds updated opcode first

                String cleanAddress = address.replaceFirst("(?i)0x", ""); // checks 0x and removes (?i) makes it case insensitive
                String correctedAddress = String.format("%4s", cleanAddress).replace(' ', '0'); //adds 0s to the left till its size 4
                String SplitAddress1 = correctedAddress.substring(0,2);
                String SplitAddress2 = correctedAddress.substring(2,4);
                machineCode.append(SplitAddress1).append(" ").append(SplitAddress2).append(" "); // adds corrected size to finished machine code


            }
        }
        Matcher stopMatcher = stopPattern.matcher(documentContent);
        if (stopMatcher.find()) {
            machineCode.append("00 ");
        }
        Matcher endMatcher = endPattern.matcher(documentContent);
        if (endMatcher.find()) {
            machineCode.append("zz");
        }

        System.out.println("machine code:"+ machineCode.toString().trim());
    }
}



