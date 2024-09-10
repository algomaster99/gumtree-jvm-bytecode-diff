package org.example.canonicalize;

import static org.example.canonicalize.BytecodeTransformerCore.writeClassfile;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class ConstantPoolTransformerTest {

    private static final Path TEST_RESOURCE = Path.of("src/test/resources");

    @Test
    void test() throws IOException {
        BytecodeTransformerCore transformer =
                new BytecodeTransformerCore(TEST_RESOURCE.resolve("First.class").toFile());
        writeClassfile(TEST_RESOURCE.resolve("First-transformed.class").toFile(), transformer.getTransformedBytes());
    }
}
