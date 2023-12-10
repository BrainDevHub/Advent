package com.codingnagger.adventofcode2023.days;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class Day6 implements Day {
    @Override
    public String partOne(List<String> input) {
        return String.valueOf(
            parseRaces(input).stream()
                .map(Race::numberOfRecordWinningWays)
                .reduce(
                    1L,
                    (a, b) -> a * b
                )
        );
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(
            parseBigRace(input).numberOfRecordWinningWays()
        );
    }

    private List<Race> parseRaces(List<String> input) {
        var times = input.get(0).split(":")[1].trim().split("\\s+");
        var recordDistances = input.get(1).split(":")[1].trim().split("\\s+");

        var races = new ArrayList<Race>(times.length);

        for (var i = 0; i < times.length; i++) {
            races.add(new Race(Long.parseLong(times[i]), Long.parseLong(recordDistances[i])));
        }

        return races;
    }

    private Race parseBigRace(List<String> input) {
        var time = String.join("", input.get(0).split(":")[1].trim().split("\\s+"));
        var recordDistance = String.join("", input.get(1).split(":")[1].trim().split("\\s+"));

        return new Race(
            Long.parseLong(time),
            Long.parseLong(recordDistance)
        );
    }


    record Race(long duration, long recordDistance) {
        public long numberOfRecordWinningWays() {
            return LongStream.range(1, duration)
                .filter(wait -> wait * (duration - wait) > recordDistance)
                .count();
        }
    }
}
