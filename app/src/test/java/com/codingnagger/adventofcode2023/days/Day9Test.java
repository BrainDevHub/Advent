package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day9Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day9.txt");
    private static final Day DAY = new Day9();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("114");
    }

    @ParameterizedTest
    @CsvSource({
            "0 3, 6",
            "0 3 6 9 12 15, 18",
            "1 3 6 10 15 21, 28",
            "10 13 16 21 30 45, 68"
    })
    void partOne_lineByLine(String line, String expected) {
        String result = DAY.partOne(List.of(line));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void partOne_secondLine() {
        String result = DAY.partOne(List.of("0 3 6 9 12 15"));

        assertThat(result).isEqualTo("18");
    }

    @Test
    void partOne_thirdLine() {
        String result = DAY.partOne(List.of("10 13 16 21 30 45"));

        assertThat(result).isEqualTo("18");
    }

    @ParameterizedTest
    @CsvSource({
            "2 2 2, 2",
            "0 2 4 6, -2",
            "3 3 5 9 15, 5",
            "10 13 16 21 30 45, 5"
    })
    void partTwo(String line, String expected) {
        String result = DAY.partTwo(List.of(line));

        assertThat(result).isEqualTo(expected);
    }
}
