package com.codingnagger.adventofcode2023.days;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day1 implements Day {

    public static final List<String> NUMERIC_DIGITS = IntStream.rangeClosed(1, 9).mapToObj(String::valueOf).toList();
    public static final Map<String, String> LETTER_DIGITS_MAP = Map.of(
            "one", "1",
            "two", "2",
            "three", "3",
            "four", "4",
            "five", "5",
            "six", "6",
            "seven", "7",
            "eight", "8",
            "nine", "9"
    );
    public static final Set<String> LETTER_DIGITS = LETTER_DIGITS_MAP.keySet();
    public static final List<String> DIGITS = Stream.concat(NUMERIC_DIGITS.stream(), LETTER_DIGITS.stream()).toList();

    @Override
    public String partOne(List<String> input) {
        return String.valueOf(input.stream().mapToInt(this::calibrationValue).sum());
    }

    private int calibrationValue(String line) {
        var numbers = line.chars()
                .mapToObj(i -> (char) i)
                .filter(Character::isDigit)
                .toList();

        return Integer.parseInt(String.valueOf(numbers.getFirst()) + numbers.getLast());
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(input.stream().mapToInt(this::calibrationValuePartTwo).sum());
    }

    private int calibrationValuePartTwo(String line) {
        var firstDigit = "";
        var firstDigitIndex = line.length();
        var lastDigit = "";
        var lastDigitIndex = -1;

        for (var digit : DIGITS) {
            var index = line.indexOf(digit);

            if (index == -1) {
                continue;
            }

            var lastIndex = line.lastIndexOf(digit);

            if (index < firstDigitIndex) {
                firstDigitIndex = index;
                firstDigit = NUMERIC_DIGITS.contains(digit) ? digit : LETTER_DIGITS_MAP.get(digit);
            }

            if (lastIndex > lastDigitIndex) {
                lastDigitIndex = lastIndex;
                lastDigit = NUMERIC_DIGITS.contains(digit) ? digit : LETTER_DIGITS_MAP.get(digit);
            }
        }

       return Integer.parseInt(firstDigit + lastDigit);
    }
}
