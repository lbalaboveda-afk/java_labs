package task_xml;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Использование: java Main <input.xml> <output.xml>");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        if (!inputFile.exists()) {
            System.out.println("Входной файл не найден: " + inputFile.getAbsolutePath());
            System.exit(2);
        }

        Operations.readXMLFile(args[0], args[1]);
    }
}