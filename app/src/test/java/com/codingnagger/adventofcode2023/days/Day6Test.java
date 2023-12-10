package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day6Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day6.txt");
    private static final Day DAY = new Day6();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("288");
    }

    @Test
    void numberOfWinningWays_isValid() {
        var race = new Day6.Race(7, 9);

        assertThat(race.numberOfRecordWinningWays()).isEqualTo(4);
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("71503");
    }
}
