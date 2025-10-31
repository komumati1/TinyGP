import org.junit.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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

        //warm up
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            TinyGP gp = new TinyGP("xmxp2.dat", 2);
            TinyGP.GENERATIONS = 10;
            TinyGP.goal_fitness = 1e-15;
            gp.evolve();
        } finally {
            System.setOut(originalOut);
        }
    }

    public char[][] getPopulation() {
        char[][] new_pop = new char[population.length][];
        for (int i = 0; i < population.length; i++) {
            new_pop[i] = Arrays.copyOf(population[i], population[i].length);
        }
        return new_pop;
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

            TinyGP gp = new TinyGP("xmxp2.dat", 2, getPopulation(), false, false);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, false);
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

            TinyGP gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, true);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, false);
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

            TinyGP gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, true);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, true);
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

            TinyGP gp = new TinyGP("xmxp2.dat", 2, getPopulation(), false, false);
            TinyGP.GENERATIONS = 2;
            TinyGP.goal_fitness = 1e-15;
            startTime = System.nanoTime();

            gp.evolve();

            endTime = System.nanoTime();
            durationMs = (endTime - startTime) / 1_000_000;

            gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, true);
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

    @Test
    public void timeTest() {
        PrintStream originalOut = System.out;
        long[] durationMsOriginal = new long[10];
        long[] durationMsSimpRec = new long[10];
        long[] durationMsSimpIter = new long[10];
        long[] durationMsThreaded = new long[10];
        long startTime;
        long endTime;

        TinyGP gp;

        try {
            //System.setOut(new PrintStream(OutputStream.nullOutputStream()));

//            for (int i = 0; i < 10; i++) {
//                gp =new TinyGP("xmxp2.dat", 2, getPopulation(), false, false);
//                TinyGP.GENERATIONS = 2;
//                TinyGP.goal_fitness = 1e-15;
//                startTime = System.nanoTime();
//
//                gp.evolve();
//
//                endTime = System.nanoTime();
//                durationMsOriginal[i] = (endTime - startTime) / 1_000_000;
//            }
//
//            originalOut.println("Old version: " + Arrays.toString(durationMsOriginal));
//
//            for (int i = 0; i < 10; i++) {
//                gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, false);
//                TinyGP.GENERATIONS = 2;
//                TinyGP.goal_fitness = 1e-15;
//                startTime = System.nanoTime();
//
//                gp.evolve();
//
//                endTime = System.nanoTime();
//                durationMsSimpRec[i] = (endTime - startTime) / 1_000_000;
//            }
//
//            originalOut.println("Simp Rec version: " + Arrays.toString(durationMsSimpRec));
//
//            for (int i = 0; i < 10; i++) {
//                gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, true);
//                TinyGP.GENERATIONS = 2;
//                TinyGP.goal_fitness = 1e-15;
//                startTime = System.nanoTime();
//
//                gp.evolve();
//
//                endTime = System.nanoTime();
//                durationMsSimpIter[i] = (endTime - startTime) / 1_000_000;
//            }
//
//            originalOut.println("Simp Iter version: " + Arrays.toString(durationMsSimpIter));

            for (int i = 0; i < 10; i++) {
                gp = new TinyGP("xmxp2.dat", 2, getPopulation(), true, true);
                TinyGP.GENERATIONS = 2;
                TinyGP.goal_fitness = 1e-15;
                startTime = System.nanoTime();

                gp.evolve_concurrent();

                endTime = System.nanoTime();
                durationMsThreaded[i] = (endTime - startTime) / 1_000_000;
            }

        } finally {
            System.setOut(originalOut);
        }

        System.out.println("Old version: " + Arrays.toString(durationMsOriginal));
        System.out.println("Simp Rec version: " + Arrays.toString(durationMsSimpRec));
        System.out.println("Simp Iter version: " + Arrays.toString(durationMsSimpIter));
        System.out.println("Threaded version: " + Arrays.toString(durationMsThreaded));
    }
}

/*
Old version: [36839, 30781, 35808, 24716, 28447, 26252, 25819, 27857, 32878, 31533]
Simp Rec version: [13436, 13158, 13584, 20782, 18087, 17934, 18318, 17847, 19787, 19081]
Simp Iter version: [10902, 14096, 13635, 14650, 15307, 12418, 12343, 12320, 12010, 11758]
Threaded version: [3864, 4050, 3945, 4167, 4314, 4360, 4176, 4186, 4492, 5594]
 */
