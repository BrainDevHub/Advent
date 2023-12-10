package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day5Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day5.txt");
    private static final Day DAY = new Day5();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("35");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("46");
    }


    @Test
    void partTwo_exampleSeed() {
        var almanac = Day5.Almanac.parse(INPUT, new Day5.NumberRanges(
            List.of(new Day5.NumberRange(82, 1)))
        );

        assertThat(almanac.lowestLocation()).isEqualTo(46);
    }

    @Test
    void partTwo_exampleSeed_sourceToLowestNumberSeed() {
        var almanac = Day5.Almanac.parse(INPUT, new Day5.NumberRanges(
            List.of(new Day5.NumberRange(79, 14)))
        );

        assertThat(almanac.lowestLocation()).isEqualTo(46);
    }

    @Test
    void partTwo_checkMiddleSplit_works() {
        var numberRange = new Day5.NumberRange(1, 6);
        var converterRange = new Day5.ConverterRange(99, 3, 2);

        assertThat(numberRange.partiallyContainsSource(converterRange)).isTrue();
        assertThat(converterRange.split(numberRange))
            .hasSize(3)
            .containsExactly(
                new Day5.NumberRange(1, 2),
                new Day5.NumberRange(3, 2),
                new Day5.NumberRange(5, 2)
            );
    }

    @Test
    void partTwo_checkSplitEnd_works() {
        var numberRange = new Day5.NumberRange(74, 4);
        var converterRange = new Day5.ConverterRange(45, 77, 23);

        assertThat(numberRange.partiallyContainsSource(converterRange)).isTrue();
        assertThat(converterRange.split(numberRange))
            .hasSize(2)
            .containsExactly(
                new Day5.NumberRange(74, 3),
                new Day5.NumberRange(77, 1)
            );
    }

    @Test
    void partTwo_checkSplitStart_works() {
        var numberRange = new Day5.NumberRange(74, 4);
        var converterRange = new Day5.ConverterRange(68, 64, 13);

        assertThat(numberRange.partiallyContainsSource(converterRange)).isTrue();
        assertThat(converterRange.split(numberRange))
            .hasSize(2)
            .containsExactly(
                new Day5.NumberRange(74, 3),
                new Day5.NumberRange(77, 1)
            );
    }
}
