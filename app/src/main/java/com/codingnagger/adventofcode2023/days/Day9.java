package com.codingnagger.adventofcode2023.days;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day9 implements Day {
    @Override
    public String partOne(List<String> input) {
        return input.stream()
                .map(line -> Arrays.asList(line.split(" ")))
                .map(line -> line.stream().map(Long::parseLong).toList())
                .map(ValueHistory::new)
                .mapToLong(ValueHistory::extrapolateNextValue)
                .sum() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return input.stream()
                .map(line -> Arrays.asList(line.split(" ")))
                .map(line -> line.stream().map(Long::parseLong).toList())
                .map(ValueHistory::new)
                .mapToLong(ValueHistory::extrapolatePreviousValue)
                .sum() + "";
    }

    record ValueHistory(List<Long> history) {
        public Long extrapolateNextValue() {
            var size = history.size();
            var nextSequence = getNextSequence(size);

            if (allSequenceEquals(nextSequence)) {
                return history.getLast() + nextSequence.getFirst();
            }

            return new ValueHistory(nextSequence).extrapolateNextValue() + history.getLast();
        }

        public Long extrapolatePreviousValue() {
            var size = history.size();
            var nextSequence = getNextSequence(size);

            if (allSequenceEquals(nextSequence)) {
                return history.getFirst() - nextSequence.getFirst();
            }

            return history.getFirst() - new ValueHistory(nextSequence).extrapolatePreviousValue();
        }

        private boolean allSequenceEquals(List<Long> nextSequence) {
            return nextSequence.stream().allMatch(nextSequence.getFirst()::equals);
        }

        private List<Long> getNextSequence(int size) {
            return IntStream.range(1, size)
                    .mapToObj(i -> history.get(i) - history.get(i - 1))
                    .toList();
        }
    }
}
