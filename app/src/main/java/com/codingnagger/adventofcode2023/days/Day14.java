package com.codingnagger.adventofcode2023.days;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class Day14 implements Day {
    @Override
    public String partOne(List<String> input) {
        return Platform.parse(input).tiltNorth().calculateLoad() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return Platform.parse(input).calculateLoadAfterOneBillionCycles() + "";
    }

    record Platform(char[][] spaces) {
        private final static int ONE_BILLION = 1_000_000_000;

        public static Platform parse(List<String> input) {
            return new Platform(
                    input.stream()
                            .map(String::toCharArray)
                            .toArray(char[][]::new)
            );
        }

        public long calculateLoad() {
            return IntStream.range(0, spaces.length)
                    .mapToLong(row ->
                            IntStream.range(0, spaces[row].length)
                                    .filter(col -> spaces[row][col] == 'O')
                                    .count() * (spaces.length - row)
                    )
                    .sum();
        }

        public Platform tiltNorth() {
            var newSpaces = Arrays.stream(spaces)
                    .map(space -> new String(space).replaceAll("O", ".").toCharArray())
                    .toArray(char[][]::new);

            for (var row = 0; row < spaces.length; row++) {
                for (var col = 0; col < spaces[row].length; col++) {
                    if (spaces[row][col] != 'O') {
                        continue;
                    }

                    var furthestNorth = row;

                    while (furthestNorth > 0 && newSpaces[furthestNorth - 1][col] == '.') {
                        furthestNorth--;
                    }

                    newSpaces[furthestNorth][col] = spaces[row][col];
                }
            }

            return new Platform(newSpaces);
        }

        public Platform tiltSouth() {
            var newSpaces = Arrays.stream(spaces)
                    .map(space -> new String(space).replaceAll("O", ".").toCharArray())
                    .toArray(char[][]::new);

            for (var row = spaces.length - 1; row >= 0; row--) {
                for (var col = 0; col < spaces[row].length; col++) {
                    if (spaces[row][col] != 'O') {
                        continue;
                    }

                    var furthestSouth = row;

                    while (furthestSouth < spaces.length - 1 && newSpaces[furthestSouth + 1][col] == '.') {
                        furthestSouth++;
                    }

                    newSpaces[furthestSouth][col] = spaces[row][col];
                }
            }

            return new Platform(newSpaces);
        }

        public Platform tiltEast() {
            var newSpaces = Arrays.stream(spaces)
                    .map(space -> new String(space).replaceAll("O", ".").toCharArray())
                    .toArray(char[][]::new);

            for (var row = 0; row < spaces.length; row++) {
                for (var col = spaces[row].length - 1; col >= 0; col--) {
                    if (spaces[row][col] != 'O') {
                        continue;
                    }

                    var furthestEast = col;

                    while (furthestEast < spaces[row].length - 1 && newSpaces[row][furthestEast + 1] == '.') {
                        furthestEast++;
                    }

                    newSpaces[row][furthestEast] = spaces[row][col];
                }
            }

            return new Platform(newSpaces);
        }

        public Platform tiltWest() {
            var newSpaces = Arrays.stream(spaces)
                    .map(space -> new String(space).replaceAll("O", ".").toCharArray())
                    .toArray(char[][]::new);

            for (var row = 0; row < spaces.length; row++) {
                for (var col = 0; col < spaces[row].length; col++) {
                    if (spaces[row][col] != 'O') {
                        continue;
                    }

                    var furthestWest = col;

                    while (furthestWest > 0 && newSpaces[row][furthestWest - 1] == '.') {
                        furthestWest--;
                    }

                    newSpaces[row][furthestWest] = spaces[row][col];
                }
            }

            return new Platform(newSpaces);
        }

        public Platform cycle() {
            return tiltNorth().tiltWest().tiltSouth().tiltEast();
        }

        public long calculateLoadAfterOneBillionCycles() {
            var cycleDetectionCursor = this;

            var visited = new HashMap<String, Boolean>();
            int firstRepeatCount = 0;

            for (var i = 0; i < ONE_BILLION; i++) {
                var cacheKey = Arrays.deepToString(cycleDetectionCursor.spaces);
                if (visited.containsKey(cacheKey)) {
                    firstRepeatCount = i + 1;
                    break;
                }
                visited.put(cacheKey, true);
                cycleDetectionCursor = cycleDetectionCursor.cycle();
            }

            var cycleSize = 1;
            var postRepeatCycleDetectionCursor = cycleDetectionCursor.cycle();

            while (!Arrays.deepToString(postRepeatCycleDetectionCursor.spaces).equals(Arrays.deepToString(cycleDetectionCursor.spaces))) {
                cycleSize++;
                postRepeatCycleDetectionCursor = postRepeatCycleDetectionCursor.cycle();
            }

            var remainingCycles = (ONE_BILLION - firstRepeatCount) % cycleSize;

            var result = this;

            for (var i = 0; i < firstRepeatCount + remainingCycles; i++) {
                result = result.cycle();
            }

            return result.calculateLoad();
        }
    }
}
