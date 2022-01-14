package fel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    public static List<String> html = new ArrayList<>();
    public static List<String> head = new ArrayList<>();
    public static List<String> body = new ArrayList<>();

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static HashMap<String, String> messagesHashMap = new HashMap<>();

    public static void init() {
        messagesHashMap.put("file_not_found", ANSI_RED + "Error: File not found" + ANSI_RESET + "\n");
        messagesHashMap.put("null_input", ANSI_RED + "Error: Empty line" + ANSI_RESET + "\n");
        messagesHashMap.put("compile_success", ANSI_GREEN + "Compilation successful!" + ANSI_RESET + "\n");
    }
}
