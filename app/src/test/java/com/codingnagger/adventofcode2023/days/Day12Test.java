package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Day12Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day12.txt");
    private static final Day DAY = new Day12();

    public static Stream<Arguments> arrangementCountsProvider() {
        return Stream.of(
                Arguments.of("???.### 1,1,3", 1),
                Arguments.of(".??..??...?##. 1,1,3", 4),
                Arguments.of("?#?#?#?#?#?#?#? 1,3,1,6", 1),
                Arguments.of("????.#...#... 4,1,1", 1),
                Arguments.of("????.######..#####. 1,6,5", 4),
                Arguments.of("?###???????? 3,2,1", 10)
        );
    }

    public static Stream<Arguments> partTwoProvider() {
        return Stream.of(
                Arguments.of(List.of("???.### 1,1,3"), "1"),
                Arguments.of(List.of(".??..??...?##. 1,1,3"), "16384"),
                Arguments.of(List.of("?#?#?#?#?#?#?#? 1,3,1,6"), "1"),
                Arguments.of(List.of("????.#...#... 4,1,1"), "16"),
                Arguments.of(List.of("????.######..#####. 1,6,5"), "2500"),
                Arguments.of(List.of("?###???????? 3,2,1"), "506250"),
                Arguments.of(INPUT, "525152")
        );
    }

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("21");
    }

    @Test
    void parseRow() {
        var row = Day12.SpringRow.parse("?#?#?#?#?#?#?#? 1,3,1,6");

        assertThat(row.definition()).isEqualTo("?#?#?#?#?#?#?#?");
        assertThat(row.damagedSpringCounts()).containsExactly(1, 3, 1, 6);
        assertThat(row.unknownSpringCount()).isEqualTo(8);
    }

    @Test
    void generateDamagedSpringArrangements() {
        var row = Day12.SpringRow.parse("?#?#?#?#?#?#?#? 1,3,1,6");

        assertThat(row.generateDamagedSpringArrangements()).containsExactly(".#.###.#.######");
    }

    @Test
    void validateArrangement() {
        var row = Day12.SpringRow.parse("?#?#?#?#?#?#?#? 1,3,1,6");

        assertThat(row.validateArrangement(".#.###.#.#####.")).isFalse();
        assertThat(row.validateArrangement(".#.###.#.######")).isTrue();
    }

    @Test
    void validateArrangement_trickierRow() {
        var row = Day12.SpringRow.parse("?###???????? 3,2,1");

        assertThat(row.validateArrangement(".###..##.#.#")).isFalse();
        assertThat(row.validateArrangement(".###..##.##.")).isFalse();
        assertThat(row.validateArrangement(".###..##....")).isFalse();
        assertThat(row.validateArrangement(".###...##..#")).isTrue();
    }

    @ParameterizedTest
    @MethodSource("arrangementCountsProvider")
    void arrangementCounts(String row, int expectedCount) {
        var springRow = Day12.SpringRow.parse(row);
        assertThat(springRow.countPotentialDamagedSpringArrangements()).isEqualTo(expectedCount);
    }

    @ParameterizedTest
    @MethodSource("partTwoProvider")
    void partTwo(List<String> input, String expectedCount) {
        String result = DAY.partTwo(input);

        assertThat(result).isEqualTo(expectedCount);
    }

    @Test
    void unfold() {
        var row = Day12.SpringRow.parse("???.### 1,1,3");
        var expectedResult = Day12.SpringRow.parse("???.###????.###????.###????.###????.### 1,1,3,1,1,3,1,1,3,1,1,3,1,1,3");

        var result = row.unfold();

        assertThat(result.definition()).isEqualTo(expectedResult.definition());
        assertThat(result.damagedSpringCounts()).containsExactly(expectedResult.damagedSpringCounts());
    }
}
