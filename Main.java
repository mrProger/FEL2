package fel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;

public class Main {
    static FelSystem fSystem = new FelSystem();
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> code = new ArrayList<>();
        String fileName = "";
        boolean result = false;

        Config.init();

        while (true) {
            code.clear();

            System.out.print("Input path to FEL file: ");
            String input = in.nextLine();

            if (input.strip().equals("")) {
                System.out.println(Config.messagesHashMap.get("null_input"));
            }
            else {
                if (input.equals("exit") || input.equals("quit")) {
                    return;
                }
                else {
                    File f = new File(input);
                    if (f.exists() && !f.isDirectory() && input.toLowerCase().endsWith(".fel")) {
                        code = fSystem.readFelFile(input);

                        for (int i = 0; i < code.size(); i++) {
                            fSystem.compileAllCode(code.get(i));
                        }

                        fileName = fSystem.getFileName(input);
                        result = fSystem.createHtmlPage(input.replace(fileName + ".fel", "") + fileName + ".html");

                        if (result) {
                            System.out.println(Config.messagesHashMap.get("compile_success"));
                        }
                    } else {
                        System.out.println(Config.messagesHashMap.get("file_not_found"));
                    }
                }
            }
        }
    }
}