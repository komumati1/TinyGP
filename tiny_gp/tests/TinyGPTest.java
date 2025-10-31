import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;


public class TinyGPTest {
    public char[][] population;
    public final String filename = "population.txt";

    @Before
    public void setUp() throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            population = reader.lines()
                    .map(line -> {
                        try {
                            byte[] decoded = Base64.getDecoder().decode(line);
                            return new String(decoded, "UTF-8").toCharArray();
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(char[][]::new);
        } catch (IOException e) {
            System.err.println("Error loading population: " + e.getMessage());
        }
    }

    @Test
    public void simplifyTest() {
        // Save the original System.out
        PrintStream originalOut = System.out;
        long durationMs;
        long durationMs2;
        long startTime;
        long endTime;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));

            TinyGP gp = new TinyGP("xmxp2.dat", 2, population, false, false);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, population, true, false);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs2 = (endTime - startTime) / 1_000_000;

        } finally {
            System.setOut(originalOut);
        }

        System.out.println("Simplify: " + durationMs + " ms");  //21333
        System.out.println("Original: " + durationMs2 + " ms"); //12622
    }

    @Test
    public void iterativeTest() {
        // Save the original System.out
        PrintStream originalOut = System.out;
        long durationMs;
        long durationMs2;
        long startTime;
        long endTime;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));

            TinyGP gp = new TinyGP("xmxp2.dat", 2, population, true, true);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, population, true, false);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs2 = (endTime - startTime) / 1_000_000;

        } finally {
            System.setOut(originalOut);
        }

        System.out.println("Iterative: " + durationMs + " ms");  //6989
        System.out.println("Recurrent: " + durationMs2 + " ms"); //13443
    }

    @Test
    public void threadTest() {
        // Save the original System.out
        PrintStream originalOut = System.out;
        long durationMs;
        long durationMs2;
        long startTime;
        long endTime;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));

            TinyGP gp = new TinyGP("xmxp2.dat", 2, population, true, true);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, population, true, true);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve_concurrent();

            endTime = System.nanoTime();
            durationMs2 = (endTime - startTime) / 1_000_000;

        } finally {
            System.setOut(originalOut);
        }

        System.out.println("Single threaded: " + durationMs + " ms");  //7871
        System.out.println("Concurrent: " + durationMs2 + " ms"); //3268
    }

    @Test
    public void allTest() {
        // Save the original System.out
        PrintStream originalOut = System.out;
        long durationMs;
        long durationMs2;
        long startTime;
        long endTime;

        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));

            //warm up
            TinyGP gp = new TinyGP("xmxp2.dat", 2);
            TinyGP.GENERATIONS = 10;
            TinyGP.goal_fitness = 1e-15;
            gp.evolve();

            gp = new TinyGP("xmxp2.dat", 2, population, false, false);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, population, true, true);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve_concurrent();

            endTime = System.nanoTime();
            durationMs2 = (endTime - startTime) / 1_000_000;

        } finally {
            System.setOut(originalOut);
        }

        System.out.println("Old version: " + durationMs + " ms");  //21977
        System.out.println("New version: " + durationMs2 + " ms"); //3296
    }
}
