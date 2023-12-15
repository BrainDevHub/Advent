package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day13Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day13.txt");
    private static final Day DAY = new Day13();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("405");
    }

    @Test
    void partOne_extraExample() {
        String result = DAY.partOne(InputLoader.LoadTest("day13_extra.txt"));

        assertThat(result).isEqualTo("1117");
    }

    @Test
    void parseNotebook() {
        var notebook = Day13.Notebook.parse(INPUT);

        assertThat(notebook.pages().size()).isEqualTo(2);
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("400");
    }
}
