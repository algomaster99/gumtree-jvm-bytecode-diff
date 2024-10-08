package org.example;

import static org.example.Constants.ROOT;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class Dataset {
    private static final Path DATASET = ROOT.resolve("dataset");
    private static final Logger logger = Constants.getLogger(Dataset.class, "dataset.log");
    private static final Path OUTPUT = ROOT.resolve("output");

    private static final List<String> RANDOM_NUMBERS;

    static {
        try {
            RANDOM_NUMBERS = Files.readAllLines(ROOT.resolve("random_numbers.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Path datasetEQ = DATASET.resolve("EQ.tsv");
        Reader reader = new FileReader(datasetEQ.toFile());

        FileWriter f0 = Constants.getFileForFurtherProcessing("paths.csv");

        Iterable<CSVRecord> records = CSVFormat.MONGODB_TSV.parse(reader);
        for (CSVRecord record : records) {
            long recordNumber = record.getRecordNumber();
            if (recordNumber == 1) {
                // title row
                continue;
            }
            if (!RANDOM_NUMBERS.contains(String.valueOf(recordNumber))) {
                continue;
            }
            Path pathToJar1 = DATASET.resolve(Paths.get(record.get(0)));
            Path pathToJar2 = DATASET.resolve(Paths.get(record.get(1)));

            String classfileInsideJar1 = record.get(2);
            String classfileInsideJar2 = record.get(3);

            assert classfileInsideJar1 != null && classfileInsideJar1.equals(classfileInsideJar2);

            JarFile jar1 = new JarFile(pathToJar1.toFile());
            JarFile jar2 = new JarFile(pathToJar2.toFile());

            byte[] classfileBytes1 = getClassFileBytes(jar1, classfileInsideJar1);
            byte[] classfileBytes2 = getClassFileBytes(jar2, classfileInsideJar2);
            String classfileName1 = getClassNameFromFullyQualifiedClassName(classfileInsideJar1);
            String classfileName2 = getClassNameFromFullyQualifiedClassName(classfileInsideJar2);
            Path outputClassfile1 = OUTPUT.resolve(String.valueOf(recordNumber))
                    .resolve("first")
                    .resolve(classfileName1);
            Path outputClassfile2 = OUTPUT.resolve(String.valueOf(recordNumber))
                    .resolve("second")
                    .resolve(classfileName2);

            Files.createDirectories(outputClassfile1.getParent());
            Files.write(outputClassfile1, classfileBytes1);

            Files.createDirectories(outputClassfile2.getParent());
            Files.write(outputClassfile2, classfileBytes2);

            String scope1 = record.get(24);
            String scope2 = record.get(25);

            assert scope1.equals(scope2) && scope1.equals("main");

            f0.write(ROOT.relativize(outputClassfile1) + "," + ROOT.relativize(outputClassfile2)
                    + System.lineSeparator());

            logger.info("Record Number: " + recordNumber + " " + ROOT.relativize(outputClassfile1) + " "
                    + ROOT.relativize(outputClassfile1));
        }
        f0.close();
    }

    private static byte[] getClassFileBytes(JarFile jar, String classfile) throws IOException {
        String sanitizedClassfile = classfile.substring(1);
        ZipEntry entry = jar.getEntry(sanitizedClassfile);
        return jar.getInputStream(entry).readAllBytes();
    }

    private static String getClassNameFromFullyQualifiedClassName(String fullyQualifiedClassName) {
        return fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf('/') + 1);
    }
}
