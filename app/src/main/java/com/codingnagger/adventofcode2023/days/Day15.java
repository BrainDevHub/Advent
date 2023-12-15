package com.codingnagger.adventofcode2023.days;

import java.util.*;
import java.util.stream.IntStream;

public class Day15 implements Day {
    @Override
    public String partOne(List<String> input) {
        return Arrays.stream(input.get(0).split(","))
                .mapToLong(Day15::generateHash)
                .sum() + "";
    }

    public static long generateHash(String code) {
        var currentValue = 0L;

        for (var charVal : code.toCharArray()) {
            currentValue = currentValue + charVal;
            currentValue *= 17;
            currentValue %= 256;
        }

        return currentValue;
    }

    @Override
    public String partTwo(List<String> input) {
        return LensAssortment.parse(input.get(0).split(",")).totalFocusingPower() + "";
    }

    static class LensAssortment {
        private final Map<String, List<Lens>> lenses;

        LensAssortment(Map<String, List<Lens>> lenses) {
            this.lenses = lenses;
        }

        public static LensAssortment parse(String[] initialisationSequence) {
            var lenses = new HashMap<String, List<Lens>>();

            for (var command : initialisationSequence) {
                if (command.endsWith("-")) {
                    var lensLabel = command.substring(0, command.length() - 1);
                    var hash = generateHash(lensLabel) + "";

                    if (!lenses.containsKey(hash)) {
                        continue;
                    }

                    lenses.put(
                            hash,
                            new ArrayList<>(lenses.get(hash).stream().filter(lens -> !lens.label().equals(lensLabel)).toList())
                    );

                    if (lenses.get(hash).isEmpty()) {
                        lenses.remove(hash);
                    }
                } else {
                    var setCommand = command.split("=");
                    var lensLabel = setCommand[0];
                    var hash = generateHash(lensLabel) + "";
                    var focalStrenght = Long.parseLong(setCommand[1]);

                    var list = lenses.getOrDefault(hash, new ArrayList<>());

                    int indexOfExisting = list.stream().filter(lens -> lens.label().equals(lensLabel))
                            .findFirst()
                            .map(list::indexOf)
                            .orElse(-1);

                    if (indexOfExisting == -1) {
                        list.add(new Lens(lensLabel, focalStrenght));
                    } else {
                        list.set(indexOfExisting, new Lens(lensLabel, focalStrenght));
                    }

                    lenses.put(hash, list);
                }
            }

            return new LensAssortment(lenses);
        }

        public long totalFocusingPower() {
            return IntStream.range(0, 256)
                    .mapToObj(String::valueOf)
                    .filter(lenses::containsKey)
                    .mapToLong(hash -> {
                        var box = Long.parseLong(hash) + 1;
                        var lenses = this.lenses.get(hash);
                        return IntStream.range(0, lenses.size())
                                .mapToLong(
                                        i -> box * (i + 1) * lenses.get(i).focalLength()
                                )
                                .sum();
                    })
                    .sum();
        }
    }

    record Lens(String label, long focalLength) {
    }
}
