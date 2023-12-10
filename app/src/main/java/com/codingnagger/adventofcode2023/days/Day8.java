package com.codingnagger.adventofcode2023.days;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 implements Day {
    private static final Pattern NODE_PATTERN = Pattern.compile("([0-9A-Z]{3}) = \\(([0-9A-Z]{3}), ([0-9A-Z]{3})\\)");

    @Override
    public String partOne(List<String> input) {
        return String.valueOf(
                CamelPouchMap.parse(input).stepCountToDestination(
                        "AAA"::equals,
                        "ZZZ"::equals
                )
        );
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(
                CamelPouchMap.parse(input).stepCountToDestination(
                        start -> start.endsWith("A"),
                        end -> end.endsWith("Z")
                )
        );
    }

    record CamelPouchMap(Instruction[] instructions, Map<String, Node> network) {
        public static CamelPouchMap parse(List<String> input) {
            return new CamelPouchMap(
                    Arrays.stream(input.get(0).split(""))
                            .map(Instruction::valueOf)
                            .toArray(Instruction[]::new),
                    input.stream()
                            .skip(2)
                            .map(NODE_PATTERN::matcher)
                            .filter(Matcher::matches)
                            .map(matcher -> new Node(
                                    matcher.group(1),
                                    matcher.group(2),
                                    matcher.group(3)
                            ))
                            .collect(Collectors.toUnmodifiableMap(Node::current, node -> node))
            );
        }

        public long stepCountToDestination(Predicate<String> startPredicate, Predicate<String> destinationPredicate) {
            var currentInstructionIndex = 0;
            var currents = network.keySet().stream().filter(startPredicate).collect(Collectors.toUnmodifiableSet());
            var destinations = network.keySet().stream().filter(destinationPredicate).collect(Collectors.toUnmodifiableSet());
            var steps = 0L;

            var cycles = new HashMap<String, Long>();

            while (!currents.containsAll(destinations) && !cycles.keySet().containsAll(destinations)) {
                var instruction = instructions[currentInstructionIndex];
                currents = currents.stream()
                        .map(network::get)
                        .map(node -> switch (instruction) {
                            case L -> node.left();
                            case R -> node.right();
                        }).collect(Collectors.toUnmodifiableSet());
                currentInstructionIndex = (currentInstructionIndex + 1) % instructions.length;
                steps++;

                final var currentSteps = steps;

                currents.stream()
                        .filter(destinations::contains)
                        .filter(Predicate.not(cycles.keySet()::contains))
                        .forEach(destination -> cycles.put(destination, currentSteps));
            }
            return currents.containsAll(destinations) ? steps : greaterCommonMultiple(cycles.values());
        }

        private long greaterCommonMultiple(Collection<Long> values) {
            return values.stream()
                    .reduce(1L, (a, b) -> a * b / greatestCommonDivisor(a, b));
        }

        private long greatestCommonDivisor(Long a, Long b) {
            return b == 0 ? a : greatestCommonDivisor(b, a % b);
        }
    }

    enum Instruction {L, R}

    record Node(String current, String left, String right) {
    }
}
