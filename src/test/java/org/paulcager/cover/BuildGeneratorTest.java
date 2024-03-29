package org.paulcager.cover;

import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuildGeneratorTest {

    @BeforeEach
    void setup() {

    }

    private String run(Object o) {
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw, true);
        try {
            new BuildGenerator(o).generate(w);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        w.flush();
        assert !w.checkError();
        return sw.toString();
    }

    private void assertCode(String expected, Object o) {
        String actual = run(o);
        Assertions.assertEquals(
                expected.replaceAll("\\s", ""),
                actual.replaceAll("\\s", "")
        );
    }

    @Test
    void testNull() {
        assertCode("null", null);
    }

    @Test
    void testString() {
        assertCode("\"Hello\"", "Hello");
    }

    @Test
    void testQuoted() {
        assertCode("\"Hello, \\\"World\\\"\"", "Hello, \"World\"");
    }

    @Builder
    @Getter
    static class B1 {
        private final int a;
    }

    @Test
    void testBuilderPlusGetter() {
        assertCode("""
                        org.paulcager.cover.BuildGeneratorTest$B1.builder().a(42).build()
                        """,
                B1.builder().a(42).build()
        );
    }

    @Builder
    record B2(
            int a) {
    }

    @Test
    void testBuilderPlusRecord() {
        assertCode("""
                        org.paulcager.cover.BuildGeneratorTest$B2.builder().a(42).build()
                        """,
                B2.builder().a(42).build()
        );
    }

    @Test
    void testInstant() {
        assertCode(
                """
                        java.time.Instant.parse("2023-09-17T15:25:09Z")
                        """,
                Instant.ofEpochMilli(1694964309000L)
        );
    }

    @Test
    void testListOfPrimitive() {
        assertCode(
                """
                        List.of(1, true, 'C')
                        """,
                List.of(1, Boolean.TRUE, 'C')
        );
    }

    @Test
    void testMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("K1", "V1");
        map.put("K2", "V2");
        assertCode(
                """
                        Map.of("K1","V1","K2","V2")
                        """,
                map
        );
    }
}