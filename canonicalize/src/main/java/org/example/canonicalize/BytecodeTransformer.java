package org.example.canonicalize;

import static org.example.canonicalize.BytecodeTransformerCore.writeClassfile;

import java.io.File;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
        name = "bytecode-transformer",
        mixinStandardHelpOptions = true,
        description = "Transforms Java bytecode")
public class BytecodeTransformer implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The Java class file to transform")
    private File javaClassfile;

    @CommandLine.Option(
            names = {"-o", "--output"},
            description = "The output file to write the transformed bytecode",
            required = true)
    private File outputFile;

    @Override
    public Integer call() throws Exception {
        BytecodeTransformerCore transformer = new BytecodeTransformerCore(javaClassfile);
        writeClassfile(outputFile, transformer.getTransformedBytes());
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new BytecodeTransformer()).execute(args);
        System.exit(exitCode);
    }
}
