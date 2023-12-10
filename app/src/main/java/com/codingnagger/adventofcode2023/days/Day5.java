package com.codingnagger.adventofcode2023.days;

import java.util.*;
import java.util.regex.Pattern;

public class Day5 implements Day {
    private static final Pattern SEED_PAIR_PATTERN = Pattern.compile("(\\d+) (\\d+)");

    @Override
    public String partOne(List<String> input) {
        var almanac = Almanac.parse(input, parseSeeds(input));
        return String.valueOf(
            almanac.lowestLocation()
        );
    }

    @Override
    public String partTwo(List<String> input) {
        var almanac = Almanac.parse(input, parseSeedRanges(input));
        return String.valueOf(
            almanac.lowestLocation()
        );
    }

    private static ConvertibleNumbers<NumberRanges> parseSeedRanges(List<String> input) {
        var seedDefinition = extractSeedsDefinition(input);
        var seedsRanges = new ArrayList<NumberRange>();
        var seedMatcher = SEED_PAIR_PATTERN.matcher(seedDefinition);

        while (seedMatcher.find()) {
            seedsRanges.add(
                new NumberRange(
                    Long.parseLong(seedMatcher.group(1)),
                    Long.parseLong(seedMatcher.group(2))
                )
            );
        }

        return new NumberRanges(seedsRanges);
    }

    private static String extractSeedsDefinition(List<String> input) {
        return input.get(0).split(": ")[1];
    }

    private static Numbers parseSeeds(List<String> input) {
        return new Numbers(
            Arrays.stream(extractSeedsDefinition(input).split(" "))
                .map(Long::parseLong)
                .toList()
        );
    }

    record Almanac(ConvertibleNumbers seeds, List<Converter> converters) {
        private static final Pattern CONVERTER_PATTERN = Pattern.compile("(\\S+) map:");
        private static final Pattern CONVERTER_RANGE_PATTERN = Pattern.compile("(\\d+) (\\d+) (\\d+)");

        public static Almanac parse(List<String> input, ConvertibleNumbers seeds) {

            var converters = new ArrayList<Converter>();

            var currentConverterName = "";
            var currentConverterRanges = new ArrayList<ConverterRange>();

            for (var line : input) {
                var converterMatcher = CONVERTER_PATTERN.matcher(line);
                if (converterMatcher.matches()) {
                    if (!currentConverterName.isBlank()) {
                        converters.add(new Converter(currentConverterName, currentConverterRanges));
                    }

                    currentConverterName = converterMatcher.group(1);
                    currentConverterRanges = new ArrayList<>();
                    continue;
                }

                var rangeMatcher = CONVERTER_RANGE_PATTERN.matcher(line);
                if (rangeMatcher.matches()) {
                    currentConverterRanges.add(new ConverterRange(
                        Long.parseLong(rangeMatcher.group(1)),
                        Long.parseLong(rangeMatcher.group(2)),
                        Long.parseLong(rangeMatcher.group(3))
                    ));
                }
            }

            converters.add(new Converter(currentConverterName, currentConverterRanges));

            return new Almanac(seeds, converters);
        }

        long lowestLocation() {
            var newNumbers = seeds;

            for (var converter : converters) {
                newNumbers = newNumbers.convert(converter);
            }

            return newNumbers.lowestValue();
        }
    }

    interface ConvertibleNumbers<T extends ConvertibleNumbers> {
        T convert(Converter converter);

        long lowestValue();
    }

    record Numbers(List<Long> values) implements ConvertibleNumbers<Numbers> {
        @Override
        public Numbers convert(Converter converter) {
            return new Numbers(
                values.stream()
                    .map(value ->
                        converter.converterRangeFor(value)
                            .map(converterRange -> converterRange.transform(value))
                            .orElse(value)
                    )
                    .toList()
            );
        }

        @Override
        public long lowestValue() {
            return values.stream().min(Long::compareTo).orElseThrow();
        }
    }

    record NumberRanges(List<NumberRange> ranges) implements ConvertibleNumbers<NumberRanges> {
        @Override
        public NumberRanges convert(Converter converter) {
            return new NumberRanges(
                ranges.stream()
                    .map(range -> converter.canSplit(range) ? converter.split(range) : List.of(range))
                    .flatMap(List::stream)
                    .map(range ->
                        converter.converterRangeFor(range.start)
                            .filter(converterRange -> converterRange.contains(range.start + range.length - 1))
                            .map(converterRange -> new NumberRange(
                                converterRange.transform(range.start),
                                range.length
                            ))
                            .orElse(range)
                    )
                    .toList()
            );
        }

        @Override
        public long lowestValue() {
            return ranges.stream().map(NumberRange::start).min(Long::compareTo).orElseThrow();
        }
    }

    record NumberRange(long start, long length) {
        public boolean partiallyContainsSource(ConverterRange converterRange) {
            return contains(converterRange.sourceStart) || contains(converterRange.sourceEnd());
        }

        public boolean contains(long value) {
            return value >= start && value < start + length;
        }

        public boolean strictlyContainsSource(ConverterRange converterRange) {
            return strictlyContains(converterRange.sourceStart) && strictlyContains(converterRange.sourceEnd());
        }

        public boolean strictlyContains(long value) {
            return value > start && value < start + length - 1;
        }

        public long end() {
            return start + length - 1;
        }
    }

    record Converter(String name, List<ConverterRange> converterRanges) {
        public Optional<ConverterRange> converterRangeFor(long value) {
            return converterRanges.stream()
                .filter(converterRange -> converterRange.contains(value))
                .findFirst();
        }

        public List<NumberRange> split(NumberRange range) {
            var stack = new Stack<NumberRange>();
            stack.push(range);

            var result = new ArrayList<NumberRange>();

            while (!stack.empty()) {
                var current = stack.pop();

                if (canSplit(current)) {
                    converterRanges.stream()
                        .filter(converterRange -> converterRange.canSplit(current))
                        .map(converterRange -> converterRange.split(current))
                        .findFirst()
                        .orElseThrow()
                        .forEach(stack::push);
                } else {
                    result.add(current);
                }
            }

            return result;
        }

        public boolean canSplit(NumberRange numberRange) {
            return converterRanges.stream().anyMatch(converterRange -> converterRange.canSplit(numberRange));
        }
    }

    record ConverterRange(long destinationStart, long sourceStart, long length) {
        public boolean contains(long value) {
            return value >= sourceStart && value <= sourceEnd();
        }

        public long transform(long value) {
            return value - sourceStart + destinationStart;
        }

        public long sourceEnd() {
            return sourceStart + length - 1;
        }

        public List<NumberRange> split(NumberRange numberRange) {
            if (numberRange.strictlyContainsSource(this)) {
                return List.of(
                    new NumberRange(numberRange.start, sourceStart - numberRange.start),
                    new NumberRange(sourceStart, length),
                    new NumberRange(sourceStart + length, numberRange.length - sourceStart - length + 1)
                );
            } else if (numberRange.strictlyContains(sourceStart)) {
                return List.of(
                    new NumberRange(numberRange.start, sourceStart - numberRange.start),
                    new NumberRange(sourceStart, numberRange.start + numberRange.length - sourceStart)
                );
            } else if (numberRange.strictlyContains(sourceEnd())) {
                return List.of(
                    new NumberRange(numberRange.start, sourceEnd() - numberRange.start + 1),
                    new NumberRange(sourceEnd() + 1, numberRange.end() - sourceEnd())
                );
            }

            throw new IllegalStateException("Illegal state for converter " + this + " and number range " + numberRange);
        }

        public boolean canSplit(NumberRange numberRange) {
            return numberRange.strictlyContainsSource(this) ||
                numberRange.strictlyContains(sourceStart) ||
                numberRange.strictlyContains(sourceEnd());
        }
    }
}