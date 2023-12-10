package com.codingnagger.adventofcode2023.days;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 implements Day {
    @Override
    public String partOne(List<String> input) {
        return String.valueOf(
                input.stream()
                        .map(ScratchCard::parse)
                        .mapToLong(ScratchCard::calculateScore)
                        .sum()
        );
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(
                new Game(input.stream().map(ScratchCard::parse).toList()).calculateScore()
        );
    }

    record Game(List<ScratchCard> scratchCards) {
        public long calculateScore() {
            var multipliers = new long[scratchCards.size()];
            Arrays.fill(multipliers, 1);

            var originalWinningCounts = scratchCards.stream().map(ScratchCard::winningCardsCount).toArray(Long[]::new);

            for (var i = 0; i < scratchCards.size(); i++) {
                for (var bonusCounts = 1; bonusCounts <= originalWinningCounts[i]; bonusCounts++) {
                    multipliers[i + bonusCounts] += multipliers[i];
                }
            }

            return scratchCards.stream()
                    .map(s -> s.id - 1)
                    .mapToLong(index -> multipliers[index])
                    .sum();
        }
    }

    record ScratchCard(int id, Numbers winning, Numbers mine) implements Comparable<ScratchCard> {
        private static final Pattern PARSING_PATTERN = Pattern.compile("Card\\s+([0-9]+): ([^|]+) \\| (.+)");

        static ScratchCard parse(String line) {

            Matcher matcher = PARSING_PATTERN.matcher(line);

            if (!matcher.find()) {
                throw new IllegalStateException("Invalid card input " + line);
            }

            return new ScratchCard(
                    Integer.parseInt(matcher.group(1)),
                    Numbers.parse(matcher.group(2)),
                    Numbers.parse(matcher.group(3))
            );
        }

        private long winningCardsCount() {
            return winning.values().stream().filter(mine.values::contains).count();
        }

        long calculateScore() {
            double winningCount = winningCardsCount();
            return winningCount > 0 ? (long) Math.pow(2, winningCount - 1) : 0;
        }

        @Override
        public int compareTo(ScratchCard o) {
            return id - o.id;
        }
    }

    record Numbers(List<Long> values) {
        static Numbers parse(String definition) {
            return new Numbers(
                    Arrays.stream(definition.split(" "))
                            .filter(s -> !s.isBlank())
                            .map(Long::parseLong)
                            .toList()
            );
        }
    }
}
