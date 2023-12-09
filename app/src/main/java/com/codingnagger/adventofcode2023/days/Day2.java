package com.codingnagger.adventofcode2023.days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Day2 implements Day {
    private static final int GAME_DEFINITION_PREFIX_LENGTH = "Game ".length();

    @Override
    public String partOne(List<String> input) {
        var bag = createBag(12, 13, 14);

        return String.valueOf(
                input.stream()
                        .map(Game::parse)
                        .filter(game -> game.isPossibleWithBag(bag))
                        .mapToInt(Game::id)
                        .sum()
        );
    }

    private static Bag createBag(int reds, int greens, int blues) {
        return new Bag(List.of(
                new GemDefinition("red", reds),
                new GemDefinition("green", greens),
                new GemDefinition("blue", blues)
        ));
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(
                input.stream()
                        .map(Game::parse)
                        .map(Game::smallestPossibleBag)
                        .mapToInt(Bag::power)
                        .sum()
        );
    }

    record Bag(List<GemDefinition> gems) {
        public boolean canFit(GemDefinition gem) {
            return gems.stream()
                    .filter(g -> g.matchesName(gem))
                    .anyMatch(g -> g.count >= gem.count);
        }

        public Bag increaseCapacityIfNeeded(List<GemDefinition> outsideGems) {
            var unfitGems = outsideGems.stream().filter(g -> !canFit(g)).toList();
            var modifiableGems = new ArrayList<>(gems);

            unfitGems.forEach(unfit -> modifiableGems.removeIf(unfit::matchesName));
            modifiableGems.addAll(unfitGems);

            return new Bag(Collections.unmodifiableList(modifiableGems));
        }

        public static Bag merge(Bag a, Bag b) {
            return a.increaseCapacityIfNeeded(b.gems);
        }

        public int power() {
            return gems.stream()
                    .mapToInt(GemDefinition::count)
                    .reduce(1, (a, b) -> a * b);
        }
    }

    record Game(int id, List<GameSet> sets) {
        static Game parse(String gameDefinition) {
            var idAndSets = Arrays.stream(gameDefinition.substring(GAME_DEFINITION_PREFIX_LENGTH).split(": ")).toList();
            var id = Integer.parseInt(idAndSets.get(0));
            var sets = Arrays.stream(idAndSets.get(1).split("; ")).map(GameSet::parse).toList();
            return new Game(id, sets);
        }

        public boolean isPossibleWithBag(Bag bag) {
            return sets.stream().allMatch(set -> set.isPossibleWithBag(bag));
        }

        public Bag smallestPossibleBag() {
            return sets.stream().reduce(
                    createBag(0, 0, 0),
                    (bag, set) -> bag.increaseCapacityIfNeeded(set.gems),
                    Bag::merge
            );
        }
    }

    record GameSet(List<GemDefinition> gems) {
        public static GameSet parse(String setDefinition) {
            return new GameSet(
                    Arrays.stream(setDefinition.split(", ")).map(GemDefinition::parse).toList()
            );
        }

        public boolean isPossibleWithBag(Bag bag) {
            return gems.stream().allMatch(bag::canFit);
        }
    }

    record GemDefinition(String name, int count) {
        public static GemDefinition parse(String description) {
            var splitDescription = description.split(" ");

            return new GemDefinition(
                    splitDescription[1],
                    Integer.parseInt(splitDescription[0])
            );
        }

        public boolean matchesName(GemDefinition gemDefinition) {
            return name.equals(gemDefinition.name);
        }
    }
}
