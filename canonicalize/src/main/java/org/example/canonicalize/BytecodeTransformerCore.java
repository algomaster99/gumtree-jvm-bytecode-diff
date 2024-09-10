package org.example.canonicalize;

import java.io.File;
import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassFileBuilder;
import java.lang.classfile.ClassModel;
import java.nio.file.Files;

public class BytecodeTransformerCore {
    private File javaClassfile;

    public BytecodeTransformerCore(File javaClassfile) {
        this.javaClassfile = javaClassfile;
    }

    public byte[] getTransformedBytes() throws IOException {
        ClassModel bytecodeModel = readClassFile(javaClassfile);
        ClassModel transformedModel = ConstantPoolTransform.transform(bytecodeModel);

        // implementation copied from java.lang.classfile.ClassTransform.ACCEPT_ALL
        // ClassFileBuilder::with transformer does not modify the class file
        return ClassFile.of().transform(transformedModel, ClassFileBuilder::with);
    }

    private static ClassModel readClassFile(File file) throws IOException {
        return ClassFile.of().parse(Files.readAllBytes(file.toPath()));
    }

    public static void writeClassfile(File file, byte[] bytes) throws IOException {
        Files.write(file.toPath(), bytes);
    }
}
