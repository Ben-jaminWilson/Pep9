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
        String filePath = "/Users/benwilson/Downloads/Project - Assembler/code/program2.pep";
        String documentContent = Files.readString(Paths.get(filePath));
        System.out.println(documentContent);

        Pattern mainPattern = Pattern.compile("([A-Z]+)\\s(0x[0-9A-Fa-f]+),\\s(i|d)"); // square brackets any character inside them, parentheses allow alteration, so i or d, need all 3
        Pattern simplifiedPattern = Pattern.compile("(ASLA|ASRA)"); // for ASLA and ASRA
        Pattern branchPattern = Pattern.compile("(BRNE)");
        Pattern stopPattern = Pattern.compile("(STOP)");
        Pattern endPattern = Pattern.compile("\\.END");

        Matcher mainMatcher = mainPattern.matcher(documentContent);
        Matcher simplifiedMatcher = simplifiedPattern.matcher(documentContent);
        Matcher branchMatcher = branchPattern.matcher(documentContent);
        Matcher stopMatcher = stopPattern.matcher(documentContent);
        Matcher endMatcher = endPattern.matcher(documentContent);
        StringBuilder machineCode = new StringBuilder();
        int position = 0;
        while (position < documentContent.length()){
            boolean mainMatch = mainMatcher.find(position);
            boolean simplifiedMatch = simplifiedMatcher.find(position);
            boolean branchMatch = branchMatcher.find(position);

            if (mainMatch && (!simplifiedMatch || mainMatcher.start() < simplifiedMatcher.start())) {
                String opcode = mainMatcher.group(1);
                String address = mainMatcher.group(2);
                String machineCodeOpcode = opcodeMap.get(opcode);
                if (machineCodeOpcode != null) {
                    machineCode.append(machineCodeOpcode).append(" ");

                    String cleanAddress = address.replaceFirst("(?i)0x", ""); // Remove the 0x prefix
                    String correctedAddress = String.format("%4s", cleanAddress).replace(' ', '0'); // Pad to 4 characters
                    String splitAddress1 = correctedAddress.substring(0, 2);
                    String splitAddress2 = correctedAddress.substring(2, 4);
                    machineCode.append(splitAddress1).append(" ").append(splitAddress2).append(" ");
                }
                // Update the position to continue after this match
                position = mainMatcher.end();
            } else if (simplifiedMatch) {
                String tempOpcode = simplifiedMatcher.group(1);
                String tempMachineCodeOpcode = opcodeMap.get(tempOpcode);
                if (tempMachineCodeOpcode != null) {
                    machineCode.append(tempMachineCodeOpcode).append(" ");
                }
                // Update the position to continue after this match
                position = simplifiedMatcher.end();
            } else if (branchMatch) {
                String tempOpcode = branchMatcher.group(1);
                String tempMachineCodeOpcode = opcodeMap.get(tempOpcode);
                if (tempMachineCodeOpcode != null) {
                    machineCode.append(tempMachineCodeOpcode).append(" 00 03 ");
                }
                // Update the position to continue after this match
                position = branchMatcher.end();
            } else {
                break; // No more matches
            }
        }
        if (stopMatcher.find()) {
            machineCode.append("00 ");
        }
        if (endMatcher.find()) {
            machineCode.append("zz");
        }

        System.out.println("machine code:"+ machineCode.toString().trim());
    }
}



