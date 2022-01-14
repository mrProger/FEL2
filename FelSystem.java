package fel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FelSystem {
    // Функция для разбиения строк на две части по разделителю ->
    public String[] parseLineCode(String line) {
        return line.split("->");
    }

    // Функция для разбиения строк на две части, а именно: оператор и значение
    public HashMap<String, String> parseLine(String line) {
        HashMap<String, String> ret = new HashMap<>();

        // Если строка начинается с head, body или html, то тупо записываем ее в HashMap
        if (line.startsWith("head") || line.startsWith("body") || line.startsWith("html")) {
            ret.put("operator", line);
            ret.put("value", "null");
        }
        // Иначе ...
        else {
            // Если строка оканчивается на ')
            if (line.endsWith("')")) {
                // Подрубаем регулярку
                Pattern pattern = Pattern.compile("([\\w\\-]+)\\(\\'([\\w\\-\\\\\\/\\+\\*\\.\\,\\!\\|\\@\\#\\№\\$\\;\\$\\%\\^\\:\\&\\?\\_\\=\\s]+)\\'\\)");

                // Парсим строку регуляркой
                Matcher matcher = pattern.matcher(line);

                // Эта строка нужна тупо, чтобы не вылетало исключение
                matcher.matches();

                // Если совпадений больше 1
                if (matcher.groupCount() == 2) {
                    ret.put("operator", matcher.group(1));
                    ret.put("value", matcher.group(2));
                }
            }
            else {
                // Если ничего не подошло, то записываем тупо NULL
                ret.put("operator", "null");
                ret.put("value", "null");
            }
        }

        // Возвращаем HashMap
        return ret;
    }

    public String compile(HashMap<String, String> args) {
        String ret = "";

        if (!args.get("tag").equals("")) {
            if (!args.get("tag").equals("img") && !args.get("tag").equals("hr") && !args.get("tag").equals("js") && !args.get("tag").equals("css") && !args.get("tag").equals("meta")) {
                ret = String.format("<%s class='%s' id='%s' onclick='%s'>%s</%s>", args.get("tag"), args.get("class"), args.get("id"), args.get("onclick"), args.get("value"), args.get("tag"));
            }
            else {
                if (args.get("tag").equals("img")) {
                    ret = String.format("<%s class='%s' id='%s' src='%s' alt='%s'>", args.get("tag"), args.get("class"), args.get("id"), args.get("src"), args.get("alt"));
                }
                else if (args.get("tag").equals("hr")) {
                    ret = String.format("<%s class='%s' id='%s' width='%s' height='%s'>", args.get("tag"), args.get("class"), args.get("id"), args.get("width"), args.get("height"));
                }
                else if (args.get("tag").equals("js")) {
                    ret = String.format("<script src='%s'></script>", args.get("value"));
                }
                else if (args.get("tag").equals("css")) {
                    ret = String.format("<link rel='stylesheet' href='%s'>", args.get("value"));
                }
                else if (args.get("tag").equals("br")) {
                    ret = "<br>";
                }
                else if (args.get("tag").equals("meta")) {
                    System.out.println(String.format("tag - %s; http-equiv - %s; content - %s", args.get("tag"), args.get("http-equiv"), args.get("content")));
                    ret = String.format("<meta http-equiv='%s' content='%s'>", args.get("http-equiv"), args.get("content"));
                }
            }

            if (!args.get("mainTag").equals("")) {
                if (args.get("mainTag").equals("html")) {
                    Config.html.add(ret);
                }
                else if (args.get("mainTag").equals("head")) {
                    Config.head.add(ret);
                }
                else if (args.get("mainTag").equals("body")) {
                    Config.body.add(ret);
                }
            }
        }

        return ret;
    }

    public List<String> readFelFile(String path) {
        List<String> ret = new ArrayList<>();

        try {
            if (path.toLowerCase().endsWith(".")) {
                path += "fel";
            } else if (path.toLowerCase().endsWith(".f")) {
                path += "el";
            } else if (path.toLowerCase().endsWith(".fe")) {
                path += "l";
            } else {
                path = path.toLowerCase().endsWith(".fel") ? path : path + ".fel";
            }

            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();

            while (line != null) {
                ret.add(line);
                line = reader.readLine();
            }

            reader.close();
        }
        catch (Exception ex) {
            System.out.println(String.format("Error: %s", ex.getMessage()));
            ex.printStackTrace();
        }

        return ret;
    }

    public boolean isTag(String line) {
        boolean result = false;
        String[] operators = new String[] {"class", "id", "value", "onclick", "src", "alt", "width", "height", "http-equiv", "content"};

        for (int i = 0; i < operators.length; i++) {
            result = operators[i].equals(line) ? true : result;
        }

        return result;
    }

    public boolean compileAllCode(String lineCode) {
        boolean status = false;

        try {
            List<HashMap<String, String>> code = new ArrayList<>();
            HashMap<String, String> params = new HashMap<>();
            String[] subStr = parseLineCode(lineCode);
            String[] hashMapKeys = new String[] {"mainTag", "tag", "class", "id", "value", "onclick", "src", "alt", "width", "height", "http-equiv", "content"};

            for (int i = 0; i < hashMapKeys.length; i++) {
                params.put(hashMapKeys[i], "");
            }

            for (int i = 0; i < subStr.length; i++) {
                code.add(parseLine(subStr[i]));
            }

            for (int i = 0; i < code.size(); i++) {
                System.out.println(String.format("operator - %s; value - %s", code.get(i).get("operator"), code.get(i).get("value")));

                if (code.get(i).get("operator") != "null" && code.get(i).get("value") == "null") {
                    params.put("mainTag", code.get(i).get("operator"));
                }

                if (code.get(i).get("operator") != "null" && code.get(i).get("value") != "null") {
                    params.put("class", code.get(i).get("operator").equals("class") ? code.get(i).get("value") : "");
                    params.put("id", code.get(i).get("operator").equals("id") ? code.get(i).get("value") : "");
                    params.put("onclick", code.get(i).get("operator").equals("onclick") ? code.get(i).get("value") : "");
                    params.put("src", code.get(i).get("operator").equals("src") ? code.get(i).get("value") : "");
                    params.put("alt", code.get(i).get("operator").equals("alt") ? code.get(i).get("value") : "");
                    params.put("width", code.get(i).get("operator").equals("width") ? code.get(i).get("value") : "");
                    params.put("height", code.get(i).get("operator").equals("height") ? code.get(i).get("value") : "");
                    params.put("content", code.get(i).get("operator").equals("content") ? code.get(i).get("value") : "");

                    if (code.get(i).get("operator").equals("http-equiv")) {
                        params.put("http-equiv", code.get(i).get("value"));
                    }

                    if (!isTag(code.get(i).get("operator"))) {
                        params.put("tag", code.get(i).get("operator"));
                        params.put("value", code.get(i).get("value"));
                    }
                }
            }

            compile(params);

            status = true;
        }
        catch (Exception ex) {
            System.out.println(String.format("Error: %s", ex.getMessage()));
            ex.printStackTrace();
            status = false;
        }

        return status;
    }

    public String getFileName(String path) {
        Pattern pattern = Pattern.compile("([\\w]+.fel)");
        Matcher matcher = pattern.matcher(path);

        return (matcher.find() ? matcher.group().replace(".fel", "") : "null");
    }

    public boolean createHtmlPage(String pageName) {
        boolean status = false;
        String strToWrite = "";

        try {
            if (Config.html.size() == 0) {
                strToWrite = "<html>\n<head>\n";

                for (int i = 0; i < Config.head.size(); i++) {
                    strToWrite += String.format("%s\n", Config.head.get(i));
                }

                strToWrite += "</head>\n<body>\n";

                for (int i = 0; i < Config.body.size(); i++) {
                    strToWrite += String.format("%s\n", Config.body.get(i));
                }

                strToWrite += "</body>\n</html>";
            }
            else {
                strToWrite = "<html>\n";

                for (int i = 0; i < Config.html.size(); i++) {
                    strToWrite += String.format("%s\n", Config.html.get(i));
                }

                strToWrite += "<head>\n";

                for (int i = 0; i < Config.head.size(); i++) {
                    strToWrite += String.format("%s\n", Config.head.get(i));
                }

                strToWrite += "</head>\n<body>\n";

                for (int i = 0; i < Config.body.size(); i++) {
                    strToWrite += String.format("%s\n", Config.body.get(i));
                }

                strToWrite += "</body>\n</html>";
            }

            try(FileWriter writer = new FileWriter(pageName, false)) {
                writer.write(strToWrite);
                writer.flush();
            }
            catch (Exception ex) {
                System.out.println(String.format("Error: %s", ex.getMessage()));
                ex.printStackTrace();
            }

            Config.html.clear();
            Config.head.clear();
            Config.body.clear();

            status = true;
        }
        catch (Exception ex) {
            System.out.println(String.format("Error: %s", ex.getMessage()));
            ex.printStackTrace();
            status = false;
        }

        return status;
    }
}
