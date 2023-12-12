package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day11Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day11.txt");
    private static final Day DAY = new Day11();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("374");
    }

    @Test
    void parseGalaxies() {
        var map = Day11.GalaxyMap.parse(2, INPUT);

        assertThat(map.galaxies())
                .hasSize(9)
                .containsExactly(
                        new Day11.Galaxy(3, 0),
                        new Day11.Galaxy(7, 1),
                        new Day11.Galaxy(0, 2),
                        new Day11.Galaxy(6, 4),
                        new Day11.Galaxy(1, 5),
                        new Day11.Galaxy(9, 6),
                        new Day11.Galaxy(7, 8),
                        new Day11.Galaxy(0, 9),
                        new Day11.Galaxy(4, 9)
                );
    }

    @Test
    void expandGalaxy() {
        var map = Day11.GalaxyMap.parse(2, INPUT);
        var alreadyExpandedMap = Day11.GalaxyMap.parse(2, InputLoader.LoadTest("day11_expanded.txt"));

        var expandedMap = map.expand();

        assertThat(expandedMap).isEqualTo(alreadyExpandedMap);
    }

    @Test
    void expandGalaxy_partTwo() {
        var mapPartOne = Day11.GalaxyMap.parse(2, INPUT).expand();
        var mapPartTwo = Day11.GalaxyMap.parse(1000, INPUT).expand();

        assertThat(mapPartTwo)
                .isNotEqualTo(mapPartOne);
    }

    @Test
    void findGalaxyPairs() {
        var map = Day11.GalaxyMap.parse(2, INPUT).expand();

        assertThat(map.galaxyPairs()).hasSize(36);
    }

    @ParameterizedTest
    @CsvSource({
            "10, 1030",
            "100, 8410",
    })
    void partTwo(long spaceMultiplier, long expected) {
        var result = Day11.GalaxyMap.parse(spaceMultiplier, INPUT).expand().sumShortestDistances();

        assertThat(result).isEqualTo(expected);
    }
}
