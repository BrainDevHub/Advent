package com.codingnagger.adventofcode2023.days;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 implements Day {
    @Override
    public String partOne(List<String> input) {
        return input.stream()
                .map(SpringRow::parse)
                .mapToInt(SpringRow::countPotentialDamagedSpringArrangements)
                .sum() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return input.stream()
                .map(SpringRow::parse)
                .map(SpringRow::unfold)
                .mapToInt(SpringRow::countPotentialDamagedSpringArrangements)
                .sum() + "";
    }

    record SpringRow(String definition, int[] damagedSpringCounts) {
        public static final int REPEAT_COUNT = 5;
        private static final Pattern DAMAGED_SPRING_PATTERN = Pattern.compile("(#+)");

        public static SpringRow parse(String rowDefinition) {
            var parts = rowDefinition.split(" ");
            return new SpringRow(
                    parts[0],
                    Arrays.stream(parts[1].split(",")).mapToInt(Integer::parseInt).toArray()
            );
        }

        public int unknownSpringCount() {
            return definition.chars().map(c -> c == '?' ? 1 : 0).sum();
        }

        public int countPotentialDamagedSpringArrangements() {
            return generateDamagedSpringArrangements().size();
        }

        public Set<String> generateDamagedSpringArrangements() {
            var arrangements = generateArrangements(definition, damagedSpringCounts);

            return new HashSet<>(arrangements)
                    .stream()
                    .filter(this::validateArrangement)
                    .collect(Collectors.toSet());
        }

        private Set<String> generateArrangements(String arrangement, int[] damagedSpringCounts) {
            var springIndex = IntStream.range(0, damagedSpringCounts.length)
                    .filter(i -> damagedSpringCounts[i] > 0)
                    .findFirst()
                    .orElse(-1);

            if (springIndex == -1 || arrangement.indexOf('?') == -1) {
                return Set.of(arrangement);
            }

            if (foundEnoughDamagedSprings(arrangement)) {
                return Set.of(arrangement);
            }

            var springCountsDamagedBranch = Arrays.copyOf(damagedSpringCounts, damagedSpringCounts.length);
            springCountsDamagedBranch[springIndex]--;

            var next = new HashSet<String>();

            next.addAll(generateArrangements(arrangement.replaceFirst("\\?", "#"), springCountsDamagedBranch));
            next.addAll(generateArrangements(arrangement.replaceFirst("\\?", "."), damagedSpringCounts));

            return next;
        }

        private boolean foundEnoughDamagedSprings(String arrangement) {
            return Arrays.stream(damagedSpringCounts).sum() == arrangement.chars().filter(c -> c == '#').count();
        }

        public boolean validateArrangement(String arrangement) {
            var matcher = DAMAGED_SPRING_PATTERN.matcher(arrangement);
            var cursor = 0;

            var didFind = matcher.find();

            while (didFind && cursor < damagedSpringCounts.length) {
                if (damagedSpringCounts[cursor++] != matcher.group(1).length()) {
                    return false;
                }

                didFind = matcher.find();
            }

            return !didFind && cursor == damagedSpringCounts.length;
        }

        public SpringRow unfold() {
            var tmpDefinition = (definition + "?").repeat(REPEAT_COUNT);
            var newDefinition = tmpDefinition.substring(0, tmpDefinition.length() - 1);

            return new SpringRow(
                    newDefinition,
                    IntStream.range(0, REPEAT_COUNT)
                            .flatMap(_ -> Arrays.stream(damagedSpringCounts))
                            .toArray()
            );
        }
    }
}
