package com.codingnagger.adventofcode2023.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputConverter {
    public static List<Integer> convertToIntegers(List<String> input) {
        return input.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public static List<Integer> convertToIntegers(String input, String separator) {
        return Arrays.stream(input.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }
}
