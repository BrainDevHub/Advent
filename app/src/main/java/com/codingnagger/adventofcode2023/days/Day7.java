package com.codingnagger.adventofcode2023.days;

import java.util.*;

public class Day7 implements Day {
    @Override
    public String partOne(List<String> input) {
        return String.valueOf(
            new SetOfHands(
                new TreeSet<>(input.stream()
                    .map(Hand::parse)
                    .map(PlayedHand::convert)
                    .toList())
            ).totalWinnings()
        );
    }

    @Override
    public String partTwo(List<String> input) {
        return String.valueOf(
            new SetOfHands(
                new TreeSet<>(input.stream()
                    .map(JokerHand::parse)
                    .map(PlayedHand::convert)
                    .toList())
            ).totalWinnings()
        );
    }

    record SetOfHands(Set<PlayedHand> hands) {
        public long totalWinnings() {
            var rank = hands.size();
            var result = 0;

            for (var playedHand : hands) {
                result += playedHand.bid * rank--;
            }

            return result;
        }
    }

    record PlayedHand(HandType type, List<Card> cards, List<Card> cardsToCompare,
                      long bid) implements Comparable<PlayedHand> {

        public static PlayedHand convert(PlayableHand hand) {
            return new PlayedHand(
                findType(hand),
                hand.cards(),
                hand.cardsToCompare(),
                hand.bid()
            );
        }

        private static HandType findType(PlayableHand hand) {
            if (hand.isFiveOfKind()) return HandType.FIVE_OF_KIND;
            if (hand.isFourOfKind()) return HandType.FOUR_OF_KIND;
            if (hand.isFullHouse()) return HandType.FULL_HOUSE;
            if (hand.isThreeOfKind()) return HandType.THREE_OF_KIND;
            if (hand.isTwoPair()) return HandType.TWO_PAIR;
            if (hand.isOnePair()) return HandType.ONE_PAIR;

            return HandType.HIGH_CARD;
        }

        @Override
        public int compareTo(PlayedHand o) {
            if (type.compareTo(o.type) != 0) {
                return type.compareTo(o.type);
            }

            for (var i = 0; i < 5; i++) {
                if (cardsToCompare.get(i).compareTo(o.cardsToCompare.get(i)) != 0) {
                    return cardsToCompare.get(i).compareTo(o.cardsToCompare.get(i));
                }
            }

            return 0;
        }

        public Card highestCard() {
            return cards.getFirst();
        }
    }

    interface PlayableHand {
        long bid();

        List<Card> cards();

        Map<Card, Integer> getGroupedCards();

        default boolean isFiveOfKind() {
            return PatternDetector.isFiveOfKind(getGroupedCards());
        }

        default boolean isFourOfKind() {
            return PatternDetector.isFourOfKind(getGroupedCards());
        }

        default boolean isFullHouse() {
            return PatternDetector.isFullHouse(getGroupedCards());
        }

        default boolean isThreeOfKind() {
            return PatternDetector.isThreeOfKind(getGroupedCards());
        }

        default boolean isTwoPair() {
            return PatternDetector.isTwoPair(getGroupedCards());
        }

        default boolean isOnePair() {
            return PatternDetector.isOnePair(getGroupedCards());
        }

        default boolean isHighCard() {
            return PatternDetector.isHighCard(getGroupedCards());
        }

        default List<Card> cardsToCompare() {
            return cards();
        }
    }

    record JokerHand(List<Card> cards, long bid) implements PlayableHand {


        @Override
        public Map<Card, Integer> getGroupedCards() {
            var groupedCards = PatternDetector.groupedCards(this);

            if (!groupedCards.containsKey(Card.CJ)) {
                return groupedCards;
            }

            if (PatternDetector.isFiveOfKind(groupedCards)) {
                return groupedCards;
            }

            var strongestCard = groupedCards.entrySet()
                .stream()
                .filter(entry -> entry.getKey() != Card.CJ)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();

            var updatedGroup = new HashMap<Card, Integer>();

            for (var card : groupedCards.keySet()) {
                if (card == Card.CJ) continue;

                updatedGroup.put(
                    card,
                    groupedCards.get(card) +
                        (card == strongestCard ? groupedCards.get(Card.CJ) : 0)
                );
            }
            return updatedGroup;
        }

        public static JokerHand parse(String line) {
            var definition = line.split(" ");
            var cardsDefinition = Arrays.stream(definition[0].split(""))
                .map(card -> Card.valueOf("C" + card))
                .toList();
            var bid = Long.parseLong(definition[1]);
            return new JokerHand(cardsDefinition, bid);
        }

        @Override
        public List<Card> cardsToCompare() {
            return cards().stream()
                .map(card -> card == Card.CJ ? Card.C_NONE : card)
                .toList();
        }
    }

    record Hand(List<Card> cards, long bid) implements PlayableHand {
        @Override
        public boolean isFiveOfKind() {
            return PatternDetector.isFiveOfKind(getGroupedCards());
        }

        @Override
        public boolean isFourOfKind() {
            return PatternDetector.isFourOfKind(getGroupedCards());
        }

        @Override
        public boolean isFullHouse() {
            return PatternDetector.isFullHouse(getGroupedCards());
        }

        @Override
        public boolean isThreeOfKind() {
            return PatternDetector.isThreeOfKind(getGroupedCards());
        }

        @Override
        public boolean isTwoPair() {
            return PatternDetector.isTwoPair(getGroupedCards());
        }

        @Override
        public boolean isOnePair() {
            return PatternDetector.isOnePair(getGroupedCards());
        }

        @Override
        public boolean isHighCard() {
            return PatternDetector.isHighCard(getGroupedCards());
        }

        @Override
        public Map<Card, Integer> getGroupedCards() {
            return PatternDetector.groupedCards(this);
        }

        public static Hand parse(String line) {
            var definition = line.split(" ");
            var cardsDefinition = Arrays.stream(definition[0].split(""))
                .map(card -> Card.valueOf("C" + card))
                .toList();
            var bid = Long.parseLong(definition[1]);
            return new Hand(cardsDefinition, bid);
        }
    }

    static class PatternDetector {
        private PatternDetector() {
        }

        public static boolean isFiveOfKind(Map<Card, Integer> groupedCards) {
            return groupedCards.values().stream().anyMatch(count -> count == 5);
        }

        public static boolean isFourOfKind(Map<Card, Integer> groupedCards) {
            return groupedCards.values().stream().anyMatch(count -> count == 4);
        }

        public static boolean isFullHouse(Map<Card, Integer> groupedCards) {
            return groupedCards.values().stream().anyMatch(count -> count == 3) &&
                groupedCards.values().stream().anyMatch(count -> count == 2);
        }

        public static boolean isThreeOfKind(Map<Card, Integer> groupedCards) {
            return groupedCards.values().stream().anyMatch(count -> count == 3) &&
                groupedCards.values().stream().anyMatch(count -> count == 1);
        }

        public static boolean isTwoPair(Map<Card, Integer> groupedCards) {
            return groupedCards.size() == 3 &&
                groupedCards.values().stream().anyMatch(count -> count == 2);
        }

        public static boolean isOnePair(Map<Card, Integer> groupedCards) {
            return groupedCards.size() == 4 &&
                groupedCards.values().stream().anyMatch(count -> count == 2);
        }

        public static boolean isHighCard(Map<Card, Integer> groupedCards) {
            return groupedCards.size() == 5;
        }

        private static Map<Card, Integer> groupedCards(PlayableHand hand) {
            var group = new HashMap<Card, Integer>();

            for (var card : hand.cards()) {
                if (group.containsKey(card)) {
                    group.put(card, group.get(card) + 1);
                } else {
                    group.put(card, 1);
                }
            }
            return group;
        }
    }

    enum HandType implements Comparable<HandType> {
        FIVE_OF_KIND,
        FOUR_OF_KIND,
        FULL_HOUSE,
        THREE_OF_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }

    enum Card implements Comparable<Card> {
        CA,
        CK,
        CQ,
        CJ,
        CT,
        C9,
        C8,
        C7,
        C6,
        C5,
        C4,
        C3,
        C2,
        C_NONE
    }
}
