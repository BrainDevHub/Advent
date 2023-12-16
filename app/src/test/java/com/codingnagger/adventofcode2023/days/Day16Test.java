package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day16Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day16.txt");
    private static final Day DAY = new Day16();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("46");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("51");
    }
}
