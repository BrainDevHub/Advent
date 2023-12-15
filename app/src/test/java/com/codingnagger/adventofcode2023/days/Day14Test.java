package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day14Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day14.txt");
    private static final Day DAY = new Day14();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("136");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("64");
    }

    @Test
    void cycle() {
        var original = Day14.Platform.parse(INPUT);

        var firstCycle = original.cycle();

        assertThat(firstCycle.spaces()).isDeepEqualTo(Day14.Platform.parse(List.of(
                ".....#....",
                "....#...O#",
                "...OO##...",
                ".OO#......",
                ".....OOO#.",
                ".O#...O#.#",
                "....O#....",
                "......OOOO",
                "#...O###..",
                "#..OO#...."
        )).spaces());

        var secondCycle = firstCycle.cycle();

        assertThat(secondCycle.spaces()).isDeepEqualTo(Day14.Platform.parse(List.of(
                ".....#....",
                "....#...O#",
                ".....##...",
                "..O#......",
                ".....OOO#.",
                ".O#...O#.#",
                "....O#...O",
                ".......OOO",
                "#..OO###..",
                "#.OOO#...O"
        )).spaces());

        var thirdCycle = secondCycle.cycle();

        assertThat(thirdCycle.spaces()).isDeepEqualTo(Day14.Platform.parse(List.of(
                ".....#....",
                "....#...O#",
                ".....##...",
                "..O#......",
                ".....OOO#.",
                ".O#...O#.#",
                "....O#...O",
                ".......OOO",
                "#...O###.O",
                "#.OOO#...O"
        )).spaces());
    }
}
