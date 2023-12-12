package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class Day10Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day3.txt");
    private static final Day DAY = new Day10();

    public static Stream<Arguments> partTwo() {
        return Stream.of(
                Arguments.of("Short circuit with no ground",
                        List.of(
                                "S7",
                                "LJ"
                        ), "0"),
                Arguments.of("Short circuit with some ground around",
                        List.of(
                                "....",
                                ".S7.",
                                ".LJ.",
                                "...."
                        ), "0"),
                Arguments.of("Simplest circuit from part one with only ground around", InputLoader.LoadTest("day10_simplest.txt"), "1"),
                Arguments.of("Simple circuit from part one with random pipes and no ground", InputLoader.LoadTest("day10_simple.txt"), "1"),
                Arguments.of("Complex circuit from part one with random pipes", InputLoader.LoadTest("day10_complex.txt"), "1"),
                Arguments.of("First part two example", InputLoader.LoadTest("day10_partTwo.txt"), "4"),
                Arguments.of("Squeezing part two example", InputLoader.LoadTest("day10_partTwo_squeeze.txt"), "4"),
                Arguments.of("Complex part two example", InputLoader.LoadTest("day10_partTwo_complex.txt"), "8"),
                Arguments.of("Made up part two example", InputLoader.LoadTest("day10_partTwo_madeup.txt"), "4"),
                Arguments.of("Most complex up part two example", InputLoader.LoadTest("day10_partTwo_mostComplex.txt"), "10")
        );
    }

    @Test
    void partOne() {
        var simpleResult = DAY.partOne(InputLoader.LoadTest("day10_simplest.txt"));

        assertThat(simpleResult).isEqualTo("4");
    }

    @Test
    void partOne_simpleAndSimplestResults_shouldMatch() {
        var simplestResult = DAY.partOne(InputLoader.LoadTest("day10_simplest.txt"));
        var simpleResult = DAY.partOne(InputLoader.LoadTest("day10_simple.txt"));

        assertThat(simplestResult).isEqualTo(simpleResult);
    }

    @Test
    void partOne_complex() {
        var simpleResult = DAY.partOne(InputLoader.LoadTest("day10_complex.txt"));

        assertThat(simpleResult).isEqualTo("8");
    }

    @ParameterizedTest
    @MethodSource("partTwo")
    void partTwo(String ignored, List<String> input, String expected) {
        var result = DAY.partTwo(input);

        assertThat(result).isEqualTo(expected);
    }
}
