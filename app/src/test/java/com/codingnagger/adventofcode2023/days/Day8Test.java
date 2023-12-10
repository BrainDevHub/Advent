package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day8Test {
    private static final Day DAY = new Day8();

    @Test
    void partOne_noLoop() {
        String result = DAY.partOne(InputLoader.LoadTest("day8_no_loop.txt"));

        assertThat(result).isEqualTo("2");
    }

    @Test
    void partOne_with_loop() {
        String result = DAY.partOne(InputLoader.LoadTest("day8_with_loop.txt"));

        assertThat(result).isEqualTo("6");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(InputLoader.LoadTest("day8_partTwo.txt"));

        assertThat(result).isEqualTo("6");
    }
}
