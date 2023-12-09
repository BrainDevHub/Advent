package com.codingnagger.adventofcode2023.days;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Day3 implements Day {
    @Override
    public String partOne(List<String> input) {
        return String.valueOf(
                EngineSchematic.parse(input)
                        .findPartNumbers()
                        .sum()
        );
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(
                EngineSchematic.parse(input)
                        .findGearRatios()
                        .sum()
        );
    }

    record EngineSchematic(char[][] map) {

        private static final char VOID = '.';
        private static final char GEAR = '*';

        public static EngineSchematic parse(List<String> input) {
            return new EngineSchematic(
                    input.stream()
                            .map(String::toCharArray)
                            .toArray(char[][]::new)
            );
        }

        public IntStream findPartNumbers() {
            return findNumberMatches(
                    digitLocation -> hasNeighborMatching(
                            digitLocation.y,
                            digitLocation.x,
                            neighbor -> charAt(neighbor) != VOID && !Character.isDigit(charAt(neighbor))));
        }

        public IntStream findNumberMatches(Predicate<Location> predicate) {
            var result = new ArrayList<String>();
            var currentNumber = new StringBuilder();
            var match = false;

            for (var row = 0; row < map.length; row++) {
                for (var col = 0; col < map[row].length; col++) {
                    char current = map[row][col];

                    if (Character.isDigit(current)) {
                        currentNumber.append(current);

                        if (!match) {
                            match = predicate.test(new Location(col, row));
                        }
                    } else {
                        if (match) {
                            result.add(currentNumber.toString());
                        }

                        currentNumber = new StringBuilder();
                        match = false;
                    }
                }
            }

            return result.stream().mapToInt(Integer::parseInt);
        }

        private char charAt(Location location) {
            return map[location.y][location.x];
        }

        private boolean hasNeighborMatching(int row, int col, Predicate<Location> match) {
            int minRow = Math.max(row - 1, 0);
            int maxRow = Math.min(row + 1, map.length - 1);
            int minCol = Math.max(col - 1, 0);
            int maxCol = Math.min(col + 1, map[row].length - 1);

            for (var r = minRow; r <= maxRow; r++) {
                for (var c = minCol; c <= maxCol; c++) {
                    if (match.test(new Location(c, r))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public IntStream findGearRatios() {
            var locations = new ArrayList<Location>();

            for (var row = 0; row < map.length; row++) {
                for (var col = 0; col < map[row].length; col++) {
                    if (map[row][col] == GEAR) {
                        locations.add(new Location(col, row));
                    }
                }
            }

            return locations.stream()
                    .map(
                            l -> findNumberMatches(
                                    digitLocation -> hasNeighborMatching(
                                            digitLocation.y,
                                            digitLocation.x,
                                            neighbor -> l.x == neighbor.x && l.y == neighbor.y
                                    )).toArray())
                    .filter(numbers -> numbers.length == 2)
                    .mapToInt(numbers -> numbers[0] * numbers[1]);
        }
    }

    record Location(int x, int y) {
    }
}
