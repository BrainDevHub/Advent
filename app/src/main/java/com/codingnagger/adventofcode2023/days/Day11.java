package com.codingnagger.adventofcode2023.days;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day11 implements Day {
    @Override
    public String partOne(List<String> input) {
        return String.valueOf(
                GalaxyMap.parse(2, input).expand().sumShortestDistances()
        );
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(
                GalaxyMap.parse(1000000, input).expand().sumShortestDistances()
        );
    }

    record GalaxyMap(long spaceMultiplier, List<Galaxy> galaxies) {
        public GalaxyMap expand() {
            var minGalaxyX = galaxies.stream().mapToLong(Galaxy::x).min().orElseThrow();
            var maxGalaxyX = galaxies.stream().mapToLong(Galaxy::x).max().orElseThrow();
            var minGalaxyY = galaxies.stream().mapToLong(Galaxy::y).min().orElseThrow();
            var maxGalaxyY = galaxies.stream().mapToLong(Galaxy::y).max().orElseThrow();

            var rowsToExpand = LongStream.rangeClosed(minGalaxyY, maxGalaxyY)
                    .filter(y -> galaxies.stream().noneMatch(galaxy -> galaxy.y() == y))
                    .boxed()
                    .toList();

            var colsToExpand = LongStream.rangeClosed(minGalaxyX, maxGalaxyX)
                    .filter(x -> galaxies.stream().noneMatch(galaxy -> galaxy.x() == x))
                    .boxed()
                    .toList();

            return new GalaxyMap(spaceMultiplier, galaxies.stream()
                    .map(galaxy ->
                            new Galaxy(
                                    galaxy.x(),
                                    galaxy.y() + (spaceMultiplier - 1) * rowsToExpand.stream().filter(y -> y < galaxy.y()).count()
                            )
                    )
                    .map(galaxy ->
                            new Galaxy(
                                    galaxy.x() + (spaceMultiplier - 1) * colsToExpand.stream().filter(x -> x < galaxy.x()).count(),
                                    galaxy.y()
                            )
                    ).toList()
            );
        }

        public static GalaxyMap parse(long spaceMultiplier, List<String> input) {
            return new GalaxyMap(
                    spaceMultiplier,
                    IntStream.range(0, input.size())
                            .mapToObj(rowIndex -> {
                                var row = input.get(rowIndex).toCharArray();
                                return IntStream.range(0, row.length)
                                        .mapToObj(colIndex -> {
                                            var col = row[colIndex];
                                            return switch (col) {
                                                case '#' -> new Galaxy(colIndex, rowIndex);
                                                default -> null;
                                            };
                                        })
                                        .filter(Objects::nonNull);
                            })
                            .flatMap(galaxyStream -> galaxyStream)
                            .toList()
            );
        }

        public List<GalaxyPair> galaxyPairs() {
            return IntStream.range(0, galaxies.size())
                    .mapToObj(i -> IntStream.range(i + 1, galaxies.size())
                            .mapToObj(j -> new GalaxyPair(galaxies.get(i), galaxies.get(j)))
                            .toList()
                    )
                    .flatMap(List::stream)
                    .filter(Predicate.not(galaxyPair -> galaxyPair.left().equals(galaxyPair.right())))
                    .toList();
        }

        public long sumShortestDistances() {
            return galaxyPairs().stream()
                    .mapToLong(galaxyPair -> galaxyPair.left().distanceTo(galaxyPair.right()))
                    .sum();
        }
    }

    record Galaxy(long x, long y) {
        public long distanceTo(Galaxy other) {
            return Math.abs(x - other.x()) + Math.abs(y - other.y());
        }
    }

    record GalaxyPair(Galaxy left, Galaxy right) {
    }
}
