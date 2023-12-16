package com.codingnagger.adventofcode2023.days;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class Day16 implements Day {
    @Override
    public String partOne(List<String> input) {
        return Cave.parse(input)
                .igniteBeam(new Beam(0, 0, Direction.RIGHT))
                .countEnergizedTiles() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return Cave.parse(input)
                .discoverOptimizedCountOfEnergizedTiles() + "";
    }

    static class Cave {
        private final Tile[][] floor;

        private Cave(Tile[][] floor) {
            this.floor = floor;
        }

        public Cave igniteBeam(Beam beam) {
            resetEnergizedTiles();

            var queue = new ArrayDeque<Beam>();
            queue.add(beam);

            var visitedPath = new HashSet<Beam>();

            while (!queue.isEmpty()) {
                var current = queue.poll();

                if (visitedPath.contains(current)) {
                    continue;
                }

                visitedPath.add(current);

                if (outsideCave(current)) {
                    continue;
                }

                var tile = floor[current.y][current.x];

                tile.directions(current.direction).forEach(newDirection -> queue.add(current.move(newDirection)));
            }

            return this;
        }

        private void resetEnergizedTiles() {
            Arrays.stream(floor)
                    .flatMap(Arrays::stream)
                    .forEach(Tile::resetTraversalCount);
        }

        @SuppressWarnings("unused")
        private void printEnergizedTiles() {
            Arrays.stream(floor)
                    .map(row -> Arrays.stream(row)
                            .map(tile -> tile.isEnergized() ? '#' : '.')
                            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                            .toString())
                    .forEach(System.out::println);
        }

        private boolean outsideCave(Beam beam) {
            return beam.x < 0 || beam.x >= floor[0].length || beam.y < 0 || beam.y >= floor.length;
        }

        public static Cave parse(List<String> input) {
            return new Cave(
                    input.stream()
                            .map(String::chars)
                            .map(chars -> chars.mapToObj(c -> switch (c) {
                                case '.' -> new EmptySpace();
                                case '/' -> new BottomLeftToTopRightMirror();
                                case '\\' -> new TopLeftToBottomRightMirror();
                                case '-' -> new HorizontalSplitter();
                                case '|' -> new VerticalSplitter();
                                default -> throw new IllegalArgumentException("Unknown tile type: " + c);
                            }).toArray(Tile[]::new))
                            .toArray(Tile[][]::new)
            );
        }

        public long countEnergizedTiles() {
            return Arrays.stream(floor)
                    .flatMap(Arrays::stream)
                    .filter(Tile::isEnergized)
                    .count();
        }

        public long discoverOptimizedCountOfEnergizedTiles() {
            return IntStream.range(0, floor.length)
                    .mapToObj(y -> IntStream.range(0, floor[y].length)
                            .filter(x -> x == 0 || x == floor[y].length - 1 || y == 0 || y == floor.length - 1)
                            .mapToObj(x -> List.of(
                                    new Beam(x, y, Direction.UP),
                                    new Beam(x, y, Direction.DOWN),
                                    new Beam(x, y, Direction.LEFT),
                                    new Beam(x, y, Direction.RIGHT)))
                    )
                    .flatMap(beams -> beams)
                    .flatMap(List::stream)
                    .map(this::igniteBeam)
                    .mapToLong(Cave::countEnergizedTiles)
                    .max()
                    .orElseThrow();
        }
    }

    record Beam(int x, int y, Direction direction) {
        public Beam move(Direction direction) {
            return switch (direction) {
                case UP -> new Beam(x, y - 1, direction);
                case DOWN -> new Beam(x, y + 1, direction);
                case LEFT -> new Beam(x - 1, y, direction);
                case RIGHT -> new Beam(x + 1, y, direction);
            };
        }
    }

    static abstract class Tile {
        private int beamTraversalCount = 0;

        public List<Direction> directions(Direction from) {
            beamTraversalCount++;
            return computeDirections(from);
        }

        abstract List<Direction> computeDirections(Direction from);


        public boolean isEnergized() {
            return beamTraversalCount > 0;
        }

        public void resetTraversalCount() {
            beamTraversalCount = 0;
        }
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    static class EmptySpace extends Tile {
        @Override
        List<Direction> computeDirections(Direction from) {
            return List.of(from);
        }
    }

    static class BottomLeftToTopRightMirror extends Tile {
        @Override
        List<Direction> computeDirections(Direction from) {
            return switch (from) {
                case DOWN -> List.of(Direction.LEFT);
                case UP -> List.of(Direction.RIGHT);
                case RIGHT -> List.of(Direction.UP);
                case LEFT -> List.of(Direction.DOWN);
            };
        }
    }

    static class TopLeftToBottomRightMirror extends Tile {
        @Override
        List<Direction> computeDirections(Direction from) {
            return switch (from) {
                case DOWN -> List.of(Direction.RIGHT);
                case UP -> List.of(Direction.LEFT);
                case RIGHT -> List.of(Direction.DOWN);
                case LEFT -> List.of(Direction.UP);
            };
        }
    }

    static class HorizontalSplitter extends Tile {
        @Override
        List<Direction> computeDirections(Direction from) {
            return switch (from) {
                case LEFT, RIGHT -> List.of(from);
                case UP, DOWN -> List.of(Direction.LEFT, Direction.RIGHT);
            };
        }
    }

    static class VerticalSplitter extends Tile {
        @Override
        List<Direction> computeDirections(Direction from) {
            return switch (from) {
                case LEFT, RIGHT -> List.of(Direction.UP, Direction.DOWN);
                case UP, DOWN -> List.of(from);
            };
        }
    }
}
