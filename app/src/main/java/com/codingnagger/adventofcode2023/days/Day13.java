package com.codingnagger.adventofcode2023.days;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Day13 implements Day {
    @Override
    public String partOne(List<String> input) {
        return Notebook.parse(input).notesScore() + "";
    }

    @Override
    public String partTwo(List<String> input) {
        return Notebook.parse(input).fixSmudgeToRevealNewHorizontalMirror().notesScore() + "";
    }

    record Notebook(List<PatternNotePage> pages) {
        public static Notebook parse(List<String> input) {
            var pages = new ArrayList<PatternNotePage>();
            var currentPageLines = new ArrayList<String>();
            for (var line : input) {
                if (!line.isBlank()) {
                    currentPageLines.add(line);
                    continue;
                }

                pages.add(new PatternNotePage(
                        currentPageLines.stream().map(String::toCharArray).toArray(char[][]::new)
                ));
                currentPageLines = new ArrayList<>();
            }

            pages.add(new PatternNotePage(
                    currentPageLines.stream().map(String::toCharArray).toArray(char[][]::new)
            ));

            return new Notebook(pages);
        }

        public long notesScore() {
            var verticallyMirroredNotesCount = pages.stream()
                    .mapToInt(PatternNotePage::columnCountBeforeMirror)
                    .sum();
            var horizontallyMirroredNotes = pages.stream()
                    .map(PatternNotePage::rowsCountAboveMirror)
                    .flatMap(Optional::stream)
                    .toList();

            return verticallyMirroredNotesCount + horizontallyMirroredNotes.stream()
                    .mapToLong(i -> i)
                    .sum() * 100;
        }

        public Notebook fixSmudgeToRevealNewHorizontalMirror() {
            return this;
        }
    }

    record PatternNotePage(char[][] page) {
        public int columnCountBeforeMirror() {
            var verticalAxis = findVerticalMirrorAxisInRow(0);

            if (verticalAxis.isEmpty()) {
                return 0;
            }

            return verticalAxis.stream()
                    .filter(axis -> IntStream.range(1, page.length)
                            .allMatch(row -> isVerticallyMirroredRow(row, axis)))
                    .mapToInt(axis -> axis + 1)
                    .sum();
        }

        private List<Integer> findVerticalMirrorAxisInRow(int row) {
            var result = new ArrayList<Integer>();

            for (var x = 0; x <= page[row].length; x++) {
                if (isVerticallyMirroredRow(row, x)) {
                    result.add(x);
                }
            }

            return result;
        }

        private boolean isVerticallyMirroredRow(int row, int x) {
            var left = x;
            var right = x + 1;

            if (right == page[row].length) {
                return false;
            }

            while (left >= 0 && right < page[row].length
                    && page[row][left] == page[row][right]) {
                left--;
                right++;
            }

            return left == -1 || right == page[row].length;
        }

        public Optional<Long> rowsCountAboveMirror() {
            return findHorizontalMirrorAxis()
                    .map(i -> i + 1L)
                    .filter(i -> i != 0 && i != page.length);
        }

        private Optional<Integer> findHorizontalMirrorAxis() {
            for (var y = 0; y <= page.length; y++) {
                if (isHorizontallyMirroredRow(y)) {
                    return Optional.of(y);
                }
            }

            return Optional.empty();
        }

        private boolean isHorizontallyMirroredRow(int y) {
            var top = y;
            var bottom = y + 1;

            while (top >= 0 && bottom < page.length) {
                final var lambdaTop = top;
                final var lambdaBottom = bottom;

                if (!IntStream.range(0, page[top].length)
                        .allMatch(x -> page[lambdaTop][x] == page[lambdaBottom][x])) {
                    return false;
                }

                top--;
                bottom++;
            }


            return top == -1 || bottom == page.length;
        }
    }
}
