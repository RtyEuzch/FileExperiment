/**
 * <p>
 * This class produces a CLI program that converts every letter in a text file to
 * either lower- or uppercase, while giving the option to move or not to move
 * the resulting file to a different directory. 
 *  
 * The user must enter as a command line argument the name of the text file they
 * want to convert to all lower- or uppercase, or the program will stop.
 * </p>
 * 
 * <p>
 * <pre>
 * javac FileProcessor.java
 * java FileProcessor demo.txt
 * </pre>
 * </p>
 * @author Charles Doan
 * ate: 5 August 2024
 */
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class FileProcessor {
    // Static Variables
    private static boolean moveUpdatedFile;
    private static Path sourcePath;
    private static Path targetPath;
    private static final Scanner input = new Scanner(System.in);

    /**
     * Runs the program and catches any inital user-side errors, such as not
     * entering a commend line argument.
     * @param args the command line arguments the user should enter. Only
     *      one command line argument is needed: the name of the text file
     *      to manipulate.
     */
    public static void main(String[] args) {/* */
        if (args.length != 1) {
            System.out.println(
                "Must provide name of a file as a command line argument.");
            System.exit(1);
        }
        
        if (moveUpdatedFile = promptForMove()) {
            sourcePath = getPath(
                "Enter the path for the current location of the file: ");
            targetPath = getPath(
                "Enter the path for the destination location of the file: ");
        }

        ArrayList<String> lines = new ArrayList<>();
        readFile(args[0], lines);

        if (isUpper())
            writeToFile(args[0], lines, str -> str.toUpperCase());
        else writeToFile(args[0], lines, str -> str.toLowerCase());

        System.out.println("Process completed.");

        input.close();
    }

    /**
     * Prompts the user for whether or not they want to move the edited file to a 
     * different directory.
     * @return true if the user wants to move the file to a different directory;
     *      false otherwise.
     */
    public static boolean promptForMove() {
        String answer;
        boolean decision = false;
        boolean done = false;
        while (!done) {
            System.out.println(
                "Would you like to put the new file in different directory? Y/N");
            answer = input.nextLine().toUpperCase();
            if (answer.equals("Y")) {
                decision = done = true;
            } else if (answer.equals("N")) {
                done = true;
            } else continue;
        }

        return decision;
    }
    
    /**
     * Prompts the user for if they want to make the text file's characters 
     * lower- or uppercase. 
     * @return true if the user wants the characters to be uppercase;
     *      false if they want it lowercase.
     */
    public static boolean isUpper() {
        String answer;
        boolean decision = false;
        boolean done = false;
        while (!done) {
            System.out.println("Upper- or lowercase? (Type \"Upper\" or \"lower\")");
            answer = input.nextLine().trim().toLowerCase();
            if (answer.equals("upper")) {
                decision = done = true;
            } else if (answer.equals("lower")) {
                done = true;
            } else continue;
        }

        return decision;
    }

    /**
     * Prompts the user to enter a path to a directory, which can be where
     * the file is located or where it should be moved.
     * @param message a message prompting the user either to enter the
     *      path to the directory where teh file is located or where the 
     *      file should be moved.
     * @return a Path to where the file is located or should be moved.
     */
    public static Path getPath(String message) {
        Path path = null;
        String pathString = null;
        boolean gotPath = false;
        while (!gotPath) {
            System.out.println(message);
            pathString = input.nextLine();
            if (!isValidPath(pathString.trim())) {
                System.out.println("Invalid path; try again.");
                continue;
            } 

            path = Paths.get(pathString);
            gotPath = true;
        }

        return path;
    }

    /**
     * @param path the path in question as a String
     * @return whether or not the path is valid
     */
    public static boolean isValidPath(String path) {
        Path testPath = null;
        try {
            testPath = Paths.get(path).toAbsolutePath();
        } catch (InvalidPathException | NullPointerException e) {
            return false;
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (!Files.exists(testPath) || !Files.isDirectory(testPath)) {
            return false;
        }

        return true;
    }

    /**
     * Reads in the contents of the file and add each line to an ArrayList,
     * where each line is a separate element
     * @param fileName the name of the file
     * @param lines the ArrayList each line in the file will be stored in
     */
    public static void readFile(String fileName, ArrayList<String> lines) {
        try (FileReader initReader = new FileReader(fileName);
             BufferedReader reader = new BufferedReader(initReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the file being used, making every letter either lowercase 
     * or uppercase.
     * @param fileName the name of the file
     * @param lines the ArrayList containing each line as an element
     * @param func the lambda expression that either makes each letter
     *      lower- or uppercase
     */
    public static void writeToFile(String fileName,
         ArrayList<String> lines, CaseFunction func) {
        try (FileWriter initWriter = new FileWriter(fileName);
             BufferedWriter writer = new BufferedWriter(initWriter)) {
            for (String line: lines) {
                writer.write(func.caseFunc(line) + "\n");
            }
            try {
                if (moveUpdatedFile) 
                    Files.move(sourcePath.resolve(fileName), targetPath.resolve(fileName));
            } catch (IOException e) {
                System.out.println("Error moving files: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * A functional interface intended to support a lambda expression that either
 * makes every letter in a String lower- or uppercase.
 */
@FunctionalInterface
interface CaseFunction {
    /**
     * A functional abstract method used to perform actions given a certain 
     * occurance, or "case"
     * @param str the String to be made lower- or uppercase
     * @return the lower- or uppercase version of str
     */
    String caseFunc(String str);
}