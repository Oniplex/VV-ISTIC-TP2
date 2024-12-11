package fr.istic.vv;

import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar CyclomaticComplexity.jar <source-code-path> [output-report-path]");
            System.exit(1);
        }

        String sourcePath = args[0];
        String outputPath = "code/Exercise5/cc_report.csv";

        if (args.length >= 2) {
            outputPath = args[1];
        }

        File sourceDir = new File(sourcePath);
        if (!sourceDir.exists() || !sourceDir.isDirectory() || !sourceDir.canRead()) {
            System.err.println("Error: Provide a path to an existing readable directory.");
            System.exit(2);
        }

        try {
            SourceRoot sourceRoot = new SourceRoot(Paths.get(sourcePath));
            ComplexityCalculator calculator = new ComplexityCalculator();

            sourceRoot.parse("", (localPath, absolutePath, result) -> {
                result.ifSuccessful(unit -> unit.accept(calculator, null));
                return SourceRoot.Callback.Result.DONT_SAVE;
            });

            calculator.writeReport(outputPath);
            System.out.println("Report generated at: " + outputPath);

            calculator.printHistogram();

        } catch (IOException e) {
            System.err.println("IO Error during parsing: " + e.getMessage());
            System.exit(3);
        }
    }
}