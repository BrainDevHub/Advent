package com.codingnagger.adventofcode2023.days;

import com.codingnagger.adventofcode2023.utils.InputLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

import static com.codingnagger.adventofcode2023.days.Day7.Card.*;
import static com.codingnagger.adventofcode2023.days.Day7.HandType.*;
import static org.assertj.core.api.Assertions.assertThat;

class Day7Test {
    private static final List<String> INPUT = InputLoader.LoadTest("day7.txt");
    private static final Day DAY = new Day7();

    public static Stream<Arguments> provideHandsToConvertWithExpectedResults() {
        return Stream.of(
            Arguments.of(
                new Day7.Hand(List.of(CA, CA, C2, C2, C2), 89),
                FULL_HOUSE,
                CA
            ),
            Arguments.of(
                new Day7.Hand(List.of(CT, CT, CT, CT, CT), 3213),
                FIVE_OF_KIND,
                CT
            ),
            Arguments.of(
                new Day7.Hand(List.of(CQ, CA, C2, C2, C2), 3213),
                THREE_OF_KIND,
                CQ
            ),
            Arguments.of(
                new Day7.JokerHand(List.of(CA, CA, C9, CJ, CJ), 89),
                FOUR_OF_KIND,
                CA
            ),
            Arguments.of(
                new Day7.JokerHand(List.of(CJ, CJ, CT, CT, CT), 3213),
                FIVE_OF_KIND,
                CJ
            ),
            Arguments.of(
                new Day7.JokerHand(List.of(CQ, CA, CJ, CJ, C2), 3213),
                THREE_OF_KIND,
                CQ
            ),
            Arguments.of(
                new Day7.JokerHand(List.of(C7, C5, C3, C4, CJ), 3213),
                ONE_PAIR,
                C7
            ),
            Arguments.of(
                new Day7.JokerHand(List.of(CT, C5, C5, CJ, C5), 3213),
                FOUR_OF_KIND,
                CT
            ),
            Arguments.of(
                new Day7.JokerHand(List.of(CK, CT, CJ, CJ, CT), 3213),
                FOUR_OF_KIND,
                CK
            ),
            Arguments.of(
                new Day7.JokerHand(List.of(CQ, CQ, CQ, CJ, CA), 3213),
                FOUR_OF_KIND,
                CQ
            )
        );
    }

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("6440");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("5905");
    }

    @Test
    void compareCards() {
        var cards = List.of(CT, C_NONE, C2, CA, CK, C8, C6);

        assertThat(new TreeSet<>(cards))
            .containsExactly(
                CA, CK, CT, C8, C6, C2, C_NONE
            );
    }

    @Test
    void detectFiveOfKind() {
        var hand = new Day7.Hand(List.of(CT, CT, CT, CT, CT), 23);

        assertThat(hand.isFiveOfKind()).isTrue();
    }

    @Test
    void detectFourOfKind() {
        var hand = new Day7.Hand(List.of(CA, CT, CT, CT, CT), 23);

        assertThat(hand.isFourOfKind()).isTrue();
    }

    @Test
    void detectFullHouse() {
        var hand = new Day7.Hand(List.of(CA, CA, C2, C2, C2), 23);

        assertThat(hand.isFullHouse()).isTrue();
    }

    @Test
    void detectThreeOfKind() {
        var fullHouseHand = new Day7.Hand(List.of(CQ, CA, C2, C2, C2), 23);

        assertThat(fullHouseHand.isThreeOfKind()).isTrue();
    }

    @Test
    void detectThreeOfKind_ignoresFullHouse() {
        var fullHouseHand = new Day7.Hand(List.of(CA, CA, C2, C2, C2), 23);

        assertThat(fullHouseHand.isThreeOfKind()).isFalse();
    }

    @Test
    void detectTwoPair() {
        var hand = new Day7.Hand(List.of(CA, CJ, C8, C8, CJ), 23);

        assertThat(hand.isTwoPair()).isTrue();
    }

    @Test
    void detectOnePair() {
        var hand = new Day7.Hand(List.of(CA, C2, C8, CT, C2), 23);

        assertThat(hand.isOnePair()).isTrue();
    }

    @Test
    void detectOnePair_ignoresTwoPair() {
        var hand = new Day7.Hand(List.of(CA, CJ, C8, C8, CJ), 23);

        assertThat(hand.isOnePair()).isFalse();
    }

    @Test
    void detectHighCard() {
        var hand = new Day7.Hand(List.of(CA, C2, C8, CT, CQ), 23);

        assertThat(hand.isHighCard()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideHandsToConvertWithExpectedResults")
    void convertHandToPlayedHand(Day7.PlayableHand hand,
                                 Day7.HandType expectedHandType,
                                 Day7.Card expectedHighCard) {

        var playedHand = Day7.PlayedHand.convert(hand);

        assertThat(playedHand.type()).isEqualByComparingTo(expectedHandType);
        assertThat(playedHand.highestCard()).isEqualByComparingTo(expectedHighCard);
        assertThat(playedHand.bid()).isEqualByComparingTo(hand.bid());
    }
}
