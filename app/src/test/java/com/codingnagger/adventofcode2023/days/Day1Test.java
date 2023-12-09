package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day1Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day1.txt");
    private static final Day DAY = new Day1();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("142");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(
                List.of(
                        "two1nine",
                        "eightwothree",
                        "abcone2threexyz",
                        "xtwone3four",
                        "4nineeightseven2",
                        "zoneight234",
                        "7pqrstsixteen"
                )
        );

        assertThat(result).isEqualTo("281");
    }

    @Test
    void partTwo_eightyThree() {
        String result = DAY.partTwo(
                List.of(
                        "eightwothree"
                )
        );

        assertThat(result).isEqualTo("83");
    }
}
