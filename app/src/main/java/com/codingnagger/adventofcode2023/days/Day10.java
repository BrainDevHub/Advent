package com.codingnagger.adventofcode2023.days;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day10 implements Day {
    @Override
    public String partOne(List<String> input) {
        return AlienLandscape.parse(input).furthestDistance() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return AlienLandscape.parse(input).nestSurface() + "";
    }

    record AlienLandscape(MapPosition[][] positions) {
        private final static Long DEFAULT_DISTANCE = null;

        public static AlienLandscape parse(List<String> input) {
            return new AlienLandscape(
                    IntStream.range(0, input.size())
                            .mapToObj(rowIndex -> {
                                var row = input.get(rowIndex).toCharArray();
                                return IntStream.range(0, row.length)
                                        .mapToObj(colIndex -> {
                                            var col = row[colIndex];
                                            return switch (col) {
                                                case 'S' -> new AnimalStart(colIndex, rowIndex);
                                                case '|' -> new VerticalPipe(colIndex, rowIndex, DEFAULT_DISTANCE);
                                                case '-' -> new HorizontalPipe(colIndex, rowIndex, DEFAULT_DISTANCE);
                                                case 'L' -> new NorthEastBend(colIndex, rowIndex, DEFAULT_DISTANCE);
                                                case 'J' -> new NorthWestBend(colIndex, rowIndex, DEFAULT_DISTANCE);
                                                case '7' -> new SouthWestBend(colIndex, rowIndex, DEFAULT_DISTANCE);
                                                case 'F' -> new SouthEastBend(colIndex, rowIndex, DEFAULT_DISTANCE);
                                                case '.' -> new Ground(colIndex, rowIndex);

                                                default -> throw new RuntimeException("Unknown map character: " + col);
                                            };
                                        }).toArray(MapPosition[]::new);
                            })
                            .toArray(MapPosition[][]::new)
            );
        }

        long furthestDistance() {
            return pipesWithDistanceFromStartAnimal().stream()
                    .filter(MapPosition::hasDistance)
                    .mapToLong(MapPosition::distance)
                    .max()
                    .orElseThrow();
        }

        private Set<MapPosition> pipesWithDistanceFromStartAnimal() {
            var animalStart = findAnimalStart();

            var positionsToVisit = new PriorityQueue<>(Comparator.comparingLong(MapPosition::distance));
            positionsToVisit.add(animalStart);
            var newPositions = new HashSet<MapPosition>();

            while (!positionsToVisit.isEmpty()) {
                var current = positionsToVisit.poll();
                var neighbors = getVisitableNeighbors(current).stream()
                        .map(neighbor ->
                                newPositions.stream()
                                        .filter(p -> p.x() == neighbor.x() && p.y() == neighbor.y())
                                        .findFirst()
                                        .orElse(neighbor)
                        )
                        .toList();

                if (neighbors.isEmpty()) {
                    continue;
                }

                for (var neighbor : neighbors) {
                    var newDistance = current.distance() + 1;

                    if (neighbor.hasNoDistance() || neighbor.distance() > newDistance) {
                        newPositions.remove(neighbor);

                        neighbor = switch (neighbor) {
                            case VerticalPipe v -> new VerticalPipe(v.x(), v.y(), newDistance);
                            case HorizontalPipe h -> new HorizontalPipe(h.x(), h.y(), newDistance);
                            case NorthEastBend ne -> new NorthEastBend(ne.x(), ne.y(), newDistance);
                            case NorthWestBend nw -> new NorthWestBend(nw.x(), nw.y(), newDistance);
                            case SouthEastBend se -> new SouthEastBend(se.x(), se.y(), newDistance);
                            case SouthWestBend sw -> new SouthWestBend(sw.x(), sw.y(), newDistance);
                            default -> throw new IllegalStateException("Illegal map position: " + neighbor);
                        };

                        newPositions.add(neighbor);
                        positionsToVisit.add(neighbor);
                    }
                }
            }

            return newPositions;
        }

        private Optional<MapPosition> getWestNeighbor(MapPosition current) {
            if (current.x() == 0) {
                return Optional.empty();
            }

            return Optional.of(positions[current.y()][current.x() - 1]);
        }

        private Optional<MapPosition> getEastNeighbor(MapPosition current) {
            if (current.x() == positions[0].length - 1) {
                return Optional.empty();
            }

            return Optional.of(positions[current.y()][current.x() + 1]);
        }

        private Optional<MapPosition> getNorthNeighbor(MapPosition current) {
            if (current.y() == 0) {
                return Optional.empty();
            }

            return Optional.of(positions[current.y() - 1][current.x()]);
        }

        private Optional<MapPosition> getSouthNeighbor(MapPosition current) {
            if (current.y() == positions.length - 1) {
                return Optional.empty();
            }

            return Optional.of(positions[current.y() + 1][current.x()]);
        }

        private List<MapPosition> getVisitableNeighbors(MapPosition current) {
            var neighbors = new ArrayList<MapPosition>();

            if (current.isWestOpen()) {
                getWestNeighbor(current)
                        .filter(MapPosition::isEastOpen)
                        .ifPresent(neighbors::add);
            }

            if (current.isEastOpen()) {
                getEastNeighbor(current)
                        .filter(MapPosition::isWestOpen)
                        .ifPresent(neighbors::add);
            }

            if (current.isNorthOpen()) {
                getNorthNeighbor(current)
                        .filter(MapPosition::isSouthOpen)
                        .ifPresent(neighbors::add);
            }

            if (current.isSouthOpen()) {
                getSouthNeighbor(current)
                        .filter(MapPosition::isNorthOpen)
                        .ifPresent(neighbors::add);
            }

            return neighbors.stream()
                    .filter(Predicate.not(AnimalStart.class::isInstance))
                    .toList();
        }

        private AnimalStart findAnimalStart() {
            return Arrays.stream(positions)
                    .map(Arrays::stream)
                    .flatMap(stream -> stream.filter(AnimalStart.class::isInstance))
                    .map(AnimalStart.class::cast)
                    .findFirst()
                    .orElseThrow();
        }

        public Long nestSurface() {
            var allPositions = allPositions();
            var closedCircuit = Stream.concat(
                    allPositions.stream().filter(AnimalStart.class::isInstance),
                    pipesWithDistanceFromStartAnimal().stream()
            ).collect(Collectors.toSet());

            var ground = groundPositions(allPositions, closedCircuit);

            if (ground.size() != allPositions.stream().filter(Ground.class::isInstance).count()) {
                return new AlienLandscape(
                        Arrays.stream(positions)
                                .map(row -> Arrays.stream(row)
                                        .map(p -> setContainsPosition(ground, p) ? new Ground(p.x(), p.y()) : p)
                                        .toArray(MapPosition[]::new)
                                )
                                .toArray(MapPosition[][]::new)
                ).nestSurface();
            }

            if (ground.isEmpty()) {
                return 0L;
            }

            var groundPockets = findGroundPockets(ground, closedCircuit);

            var groundPocketsMergedByCommonGround = mergePockets(groundPockets, (p, pocket) ->
                    p.stream().anyMatch(pp -> setContainsPosition(pocket, pp)));
            var groundPocketsMergedByNeighbours = mergePockets(groundPocketsMergedByCommonGround, (p, pocket) ->
                    p.stream().anyMatch(g -> getNeighbors(g)
                            .stream()
                            .filter(Ground.class::isInstance)
                            .map(Ground.class::cast)
                            .anyMatch(pp -> setContainsPosition(pocket, pp))));

            var alreadyOutsideGroundPockets = groundPocketsMergedByNeighbours.stream()
                    .filter(p -> p.stream().anyMatch(g -> getNeighbors(g).size() < 4))
                    .collect(Collectors.toSet());

            var groundOutsideCircuit = alreadyOutsideGroundPockets.stream()
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            var escapedGround = groundPocketsMergedByNeighbours.stream()
                    .filter(Predicate.not(alreadyOutsideGroundPockets::contains))
                    .filter(p -> p.stream().anyMatch(g -> this.canEscapeCircuit(g, closedCircuit, groundOutsideCircuit)))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            var allEscapedStuff = Stream.concat(
                    groundOutsideCircuit.stream(),
                    escapedGround.stream()
            ).collect(Collectors.toSet());
            printMap(allPositions, allEscapedStuff);

            return (long) allPositions.size() - closedCircuit.size() - escapedGround.size() - groundOutsideCircuit.size();
        }

        private boolean canEscapeCircuit(MapPosition g, Set<MapPosition> closedCircuit, Set<MapPosition> outside) {
            var circuitNeighbors = getNeighbors(g).stream().filter(gg -> setContainsPosition(closedCircuit, gg)).toList();

            var south = circuitNeighbors.stream().filter(n -> n.y() > g.y()).findFirst();
            if (south.isPresent()) {
                var southNeighbor = south.get();
                var latestCircuitPipe = southNeighbor;

                if (!southNeighbor.isNorthOpen() && southNeighbor.isSouthOpen()) {
                    var southerner = getSouthNeighbor(southNeighbor);

                    while (southerner.isPresent() && southerner.get().isNorthOpen()) {
                        latestCircuitPipe = southerner.filter(s -> setContainsPosition(closedCircuit, s)).orElse(latestCircuitPipe);
                        southerner = getSouthNeighbor(southerner.get());
                    }

                    if (latestCircuitPipe.isWestOpen() == southNeighbor.isWestOpen() && maybeOutside(outside, southerner)) {
                        return true;
                    }
                }
            }

            var north = circuitNeighbors.stream().filter(n -> n.y() < g.y()).findFirst();
            if (north.isPresent()) {
                var northNeighbor = north.get();
                var latestCircuitPipe = northNeighbor;

                if (!northNeighbor.isSouthOpen() && northNeighbor.isNorthOpen()) {
                    var northerner = getNorthNeighbor(northNeighbor);

                    while (northerner.isPresent() && northerner.get().isSouthOpen()) {
                        latestCircuitPipe = northerner.filter(s -> setContainsPosition(closedCircuit, s)).orElse(latestCircuitPipe);
                        northerner = getNorthNeighbor(northerner.get());
                    }

                    if (latestCircuitPipe.isWestOpen() == northNeighbor.isWestOpen() && maybeOutside(outside, northerner)) {
                        return true;
                    }
                }
            }

            var west = circuitNeighbors.stream().filter(n -> n.x() < g.x()).findFirst();
            if (west.isPresent()) {
                var westNeighbor = west.get();
                var latestCircuitPipe = westNeighbor;

                if (!westNeighbor.isEastOpen() && westNeighbor.isWestOpen()) {
                    var wester = getWestNeighbor(westNeighbor);

                    while (wester.isPresent() && wester.get().isEastOpen()) {
                        latestCircuitPipe = wester.filter(s -> setContainsPosition(closedCircuit, s)).orElse(latestCircuitPipe);
                        wester = getWestNeighbor(wester.get());
                    }

                    if (latestCircuitPipe.isNorthOpen() == westNeighbor.isNorthOpen() && maybeOutside(outside, wester)) {
                        return true;
                    }
                }
            }

            var east = circuitNeighbors.stream().filter(n -> n.x() > g.x()).findFirst();
            if (east.isPresent()) {
                var eastNeighbor = east.get();
                var latestCircuitPipe = eastNeighbor;

                if (!eastNeighbor.isWestOpen() && eastNeighbor.isEastOpen()) {
                    var easter = getEastNeighbor(eastNeighbor);

                    while (easter.isPresent() && easter.get().isWestOpen()) {
                        latestCircuitPipe = easter.filter(s -> setContainsPosition(closedCircuit, s)).orElse(latestCircuitPipe);
                        easter = getEastNeighbor(easter.get());
                    }

                    if (latestCircuitPipe.isNorthOpen() == eastNeighbor.isNorthOpen() && maybeOutside(outside, easter)) {
                        return true;
                    }
                }
            }

            return false;
        }

        private static boolean maybeOutside(Set<MapPosition> outside, Optional<MapPosition> position) {
            if (position.isEmpty()) {
                return true;
            }

            return position
                    .filter(Ground.class::isInstance)
                    .map(Ground.class::cast)
                    .filter(p -> setContainsPosition(outside, p))
                    .isPresent();
        }

        private HashSet<Set<MapPosition>> findGroundPockets(Set<Ground> ground, Set<MapPosition> closedCircuit) {
            var visited = new HashSet<MapPosition>();
            var groundPockets = new HashSet<Set<MapPosition>>();
            var currentGroundPocket = new HashSet<MapPosition>();
            var queue = new ArrayDeque<>(ground);

            while (!queue.isEmpty()) {
                var current = queue.poll();

                if (visited.contains(current)) {
                    continue;
                }

//                System.out.println("\n---\nVisiting: " + current.x() + ", " + current.y());
                visited.add(current);
                currentGroundPocket.add(current);

                var neighbors = getNeighbors(current);
                var groundNeighbors = neighbors.stream()
                        .filter(Ground.class::isInstance)
                        .map(Ground.class::cast)
                        .toList();

                if (!groundNeighbors.isEmpty() && currentGroundPocket.containsAll(groundNeighbors)) {
                    currentGroundPocket.add(current);
                    continue;
                }

                var westVisited = traverseCircuit(current, this::getWestNeighbor);
                if (westVisited.stream().allMatch(Predicate.not(v -> setContainsPosition(closedCircuit, v)))) {
                    visited.addAll(westVisited);
                    currentGroundPocket.addAll(
                            westVisited.stream().filter(Ground.class::isInstance).map(Ground.class::cast).toList()
                    );
                }

                var eastVisited = traverseCircuit(current, this::getEastNeighbor);
                if (eastVisited.stream().allMatch(Predicate.not(v -> setContainsPosition(closedCircuit, v)))) {
                    visited.addAll(eastVisited);
                    currentGroundPocket.addAll(
                            eastVisited.stream().filter(Ground.class::isInstance).map(Ground.class::cast).toList()
                    );
                }

                var northVisited = traverseCircuit(current, this::getNorthNeighbor);
                if (northVisited.stream().allMatch(Predicate.not(v -> setContainsPosition(closedCircuit, v)))) {
                    visited.addAll(northVisited);
                    currentGroundPocket.addAll(
                            northVisited.stream().filter(Ground.class::isInstance).map(Ground.class::cast).toList()
                    );
                }

                var southVisited = traverseCircuit(current, this::getSouthNeighbor);
                if (southVisited.stream().allMatch(Predicate.not(v -> setContainsPosition(closedCircuit, v)))) {
                    visited.addAll(southVisited);
                    currentGroundPocket.addAll(
                            southVisited.stream().filter(Ground.class::isInstance).map(Ground.class::cast).toList()
                    );
                }

                if (!groundNeighbors.isEmpty() && currentGroundPocket.containsAll(groundNeighbors)) {
                    currentGroundPocket.add(current);
                }

                groundPockets.add(currentGroundPocket);
                currentGroundPocket = new HashSet<>();
            }
            return groundPockets;
        }

        private HashSet<Set<MapPosition>> mergePockets(HashSet<Set<MapPosition>> groundPockets, MergeDetector mergeDetector) {
            var pocketsToMerge = new ArrayDeque<>(groundPockets);
            var mergedPockets = new HashSet<Set<MapPosition>>();

            while (!pocketsToMerge.isEmpty()) {
                var pocket = pocketsToMerge.poll();

                var mergeablePocket = pocketsToMerge.stream()
                        .filter(p -> p.stream().anyMatch(pp -> setContainsPosition(pocket, pp)))
                        .findFirst()
                        .or(() -> pocketsToMerge.stream()
                                .filter(p -> mergeDetector.canMerge(pocket, p))
                                .findFirst()
                        );

                if (mergeablePocket.isEmpty()) {
                    mergedPockets.add(pocket);
                    continue;
                }

                var definitelyMergeablePocket = mergeablePocket.get();
                pocketsToMerge.remove(definitelyMergeablePocket);

                pocketsToMerge.add(
                        Stream.concat(
                                pocket.stream(),
                                definitelyMergeablePocket.stream()
                        ).collect(Collectors.toSet())
                );
            }
            return mergedPockets;
        }

        private void printMap(List<MapPosition> allPositions, Set<MapPosition> groundOutsideLoop) {
            var minX = allPositions.stream().mapToInt(MapPosition::x).min().orElseThrow();
            var maxX = allPositions.stream().mapToInt(MapPosition::x).max().orElseThrow();
            var minY = allPositions.stream().mapToInt(MapPosition::y).min().orElseThrow();
            var maxY = allPositions.stream().mapToInt(MapPosition::y).max().orElseThrow();

            System.out.println();

            for (var y = minY; y <= maxY; y++) {
                for (var x = minX; x <= maxX; x++) {
                    if (setContainsPosition(groundOutsideLoop, positions[y][x])) {
                        System.out.print("0");
                        continue;
                    }

                    switch (positions[y][x]) {
                        case AnimalStart _ -> System.out.print("S");
                        case VerticalPipe _ -> System.out.print("|");
                        case HorizontalPipe _ -> System.out.print("-");
                        case NorthEastBend _ -> System.out.print("L");
                        case NorthWestBend _ -> System.out.print("J");
                        case SouthEastBend _ -> System.out.print("F");
                        case SouthWestBend _ -> System.out.print("7");
//                        case Pipe _ -> System.out.print("#");
                        case Ground g -> System.out.print(".");
                        default -> throw new IllegalStateException("Unexpected value: " + positions[y][x]);
                    }
                }
                System.out.println();
            }
        }

        private static HashSet<MapPosition> traverseCircuit(MapPosition start, Function<MapPosition, Optional<MapPosition>> nextPosition) {
            var visited = new HashSet<MapPosition>();

            var current = nextPosition.apply(start);

            while (current.isPresent()) {
                visited.add(current.get());
                current = nextPosition.apply(current.get());
            }

            return visited;
        }

        private static boolean setContainsPosition(Set<? extends MapPosition> closedCircuit, MapPosition mapPosition) {
            return closedCircuit.stream().anyMatch(p -> p.x() == mapPosition.x() && p.y() == mapPosition.y());
        }

        private List<MapPosition> getNeighbors(MapPosition current) {
            var neighbors = new ArrayList<MapPosition>();

            getWestNeighbor(current).ifPresent(neighbors::add);
            getEastNeighbor(current).ifPresent(neighbors::add);
            getNorthNeighbor(current).ifPresent(neighbors::add);
            getSouthNeighbor(current).ifPresent(neighbors::add);

            return neighbors;
        }

        private static Set<Ground> groundPositions(List<MapPosition> positions, Set<MapPosition> closedCircuit) {
            return positions
                    .stream()
                    .filter(Predicate.not(p -> setContainsPosition(closedCircuit, p)))
                    .map(p -> p instanceof Ground ground ? ground : new Ground(p.x(), p.y()))
                    .collect(Collectors.toSet());
        }

        private List<MapPosition> allPositions() {
            return Arrays.stream(positions)
                    .flatMap(row -> Arrays.stream(row, 0, row.length))
                    .toList();
        }
    }

    interface MergeDetector {
        boolean canMerge(Set<MapPosition> pocket1, Set<MapPosition> pocket2);
    }

    interface MapPosition {
        int x();

        int y();

        default boolean isNorthOpen() {
            return false;
        }

        default boolean isWestOpen() {
            return false;
        }

        default boolean isEastOpen() {
            return false;
        }

        default boolean isSouthOpen() {
            return false;
        }

        default Long distance() {
            return null;
        }

        default Optional<Long> potentialDistance() {
            return Optional.ofNullable(distance());
        }

        default boolean hasDistance() {
            return potentialDistance().isPresent();
        }

        default boolean hasNoDistance() {
            return !hasDistance();
        }
    }

    interface Pipe extends MapPosition {
    }

    record AnimalStart(int x, int y) implements Pipe {
        @Override
        public boolean isEastOpen() {
            return true;
        }

        @Override
        public boolean isWestOpen() {
            return true;
        }

        @Override
        public boolean isNorthOpen() {
            return true;
        }

        @Override
        public boolean isSouthOpen() {
            return true;
        }

        @Override
        public Long distance() {
            return 0L;
        }
    }

    record VerticalPipe(int x, int y, Long distance) implements Pipe {
        @Override
        public boolean isNorthOpen() {
            return true;
        }

        @Override
        public boolean isSouthOpen() {
            return true;
        }
    }

    record HorizontalPipe(int x, int y, Long distance) implements Pipe {
        @Override
        public boolean isWestOpen() {
            return true;
        }

        @Override
        public boolean isEastOpen() {
            return true;
        }
    }

    record NorthEastBend(int x, int y, Long distance) implements Pipe {
        @Override
        public boolean isNorthOpen() {
            return true;
        }

        @Override
        public boolean isEastOpen() {
            return true;
        }
    }

    record NorthWestBend(int x, int y, Long distance) implements Pipe {
        @Override
        public boolean isNorthOpen() {
            return true;
        }

        @Override
        public boolean isWestOpen() {
            return true;
        }
    }

    record SouthEastBend(int x, int y, Long distance) implements Pipe {
        @Override
        public boolean isSouthOpen() {
            return true;
        }

        @Override
        public boolean isEastOpen() {
            return true;
        }
    }

    record SouthWestBend(int x, int y, Long distance) implements Pipe {
        @Override
        public boolean isSouthOpen() {
            return true;
        }

        @Override
        public boolean isWestOpen() {
            return true;
        }
    }

    record Ground(int x, int y) implements MapPosition {
    }
}
