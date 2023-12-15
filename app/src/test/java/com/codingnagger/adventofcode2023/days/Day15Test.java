package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day15Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day15.txt");
    private static final Day DAY = new Day15();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("1320");
    }

    @ParameterizedTest
    @CsvSource({
            "rn=1, 30",
            "cm-, 253",
            "HASH, 52"
    })
    void generateHash() {
        long result = Day15.generateHash("HASH");

        assertThat(result).isEqualTo(52);
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("145");
    }
}
