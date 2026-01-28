package edu.brandeis.cosi103a.ip1;

import java.util.*;

public class ATG {
    enum CardType { AUTOMATION, CRYPTO }

    static class Card {
        String name;
        int cost;
        int value;
        CardType type;

        Card(String name, int cost, int value, CardType type) {
            this.name = name;
            this.cost = cost;
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {
            return name + " (cost:" + cost + ", value:" + value + ")";
        }
    }

    static class Supply {
        Map<String, Integer> counts = new HashMap<>();

        Supply() {
            counts.put("Method", 14);
            counts.put("Module", 8);
            counts.put("Framework", 8);
            counts.put("Bitcoin", 60);
            counts.put("Ethereum", 40);
            counts.put("Dogecoin", 30);
        }

        boolean canBuy(String cardName) {
            return counts.getOrDefault(cardName, 0) > 0;
        }

        void buy(String cardName) {
            if (canBuy(cardName)) {
                counts.put(cardName, counts.get(cardName) - 1);
            }
        }

        Card getCard(String cardName) {
            if (cardName.equals("Method")) return new Card("Method", 2, 1, CardType.AUTOMATION);
            if (cardName.equals("Module")) return new Card("Module", 5, 3, CardType.AUTOMATION);
            if (cardName.equals("Framework")) return new Card("Framework", 8, 6, CardType.AUTOMATION);
            if (cardName.equals("Bitcoin")) return new Card("Bitcoin", 0, 1, CardType.CRYPTO);
            if (cardName.equals("Ethereum")) return new Card("Ethereum", 3, 2, CardType.CRYPTO);
            if (cardName.equals("Dogecoin")) return new Card("Dogecoin", 6, 3, CardType.CRYPTO);
            return null;
        }

        boolean isEmpty() {
            return counts.get("Framework") == 0;
        }
    }

    static class Player {
        List<Card> deck = new ArrayList<>();
        List<Card> hand = new ArrayList<>();
        List<Card> discard = new ArrayList<>();
        List<Card> played = new ArrayList<>();
        Random random = new Random();

        void initDeck(Supply supply) {
            for (int i = 0; i < 7; i++) {
                supply.buy("Bitcoin");
                deck.add(supply.getCard("Bitcoin"));
            }
            for (int i = 0; i < 3; i++) {
                supply.buy("Method");
                deck.add(supply.getCard("Method"));
            }
            Collections.shuffle(deck, random);
            drawHand();
        }

        void drawHand() {
            while (hand.size() < 5 && !deck.isEmpty()) {
                hand.add(deck.remove(0));
            }
            if (hand.size() < 5 && !discard.isEmpty()) {
                deck.addAll(discard);
                discard.clear();
                Collections.shuffle(deck, random);
                while (hand.size() < 5 && !deck.isEmpty()) {
                    hand.add(deck.remove(0));
                }
            }
        }

        int getCoins() {
            int coins = 0;
            for (Card c : played) {
                if (c.type == CardType.CRYPTO) coins += c.value;
            }
            return coins;
        }

        void playCoins() {
            List<Card> toPlay = new ArrayList<>();
            for (Card c : hand) {
                if (c.type == CardType.CRYPTO) {
                    toPlay.add(c);
                }
            }
            played.addAll(toPlay);
            hand.removeAll(toPlay);
        }

        boolean canBuy(Card card, int coins) {
            return coins >= card.cost;
        }

        void buy(Card card) {
            discard.add(card);
        }

        void cleanup() {
            discard.addAll(hand);
            discard.addAll(played);
            hand.clear();
            played.clear();
            drawHand();
        }

        int getAPs() {
            int aps = 0;
            for (Card c : deck) {
                if (c.type == CardType.AUTOMATION) aps += c.value;
            }
            for (Card c : hand) {
                if (c.type == CardType.AUTOMATION) aps += c.value;
            }
            for (Card c : discard) {
                if (c.type == CardType.AUTOMATION) aps += c.value;
            }
            return aps;
        }

        // Simple AI: buy the most expensive card they can afford
        String chooseCardToBuy(Supply supply, int coins) {
            List<String> cardNames = Arrays.asList("Framework", "Dogecoin", "Module", "Ethereum", "Method", "Bitcoin");
            for (String name : cardNames) {
                Card card = supply.getCard(name);
                if (supply.canBuy(name) && coins >= card.cost) {
                    return name;
                }
            }
            return null;
        }
    }

    static class Game {
        Player p1 = new Player();
        Player p2 = new Player();
        Supply supply = new Supply();
        Random random = new Random();

        void setup() {
            p1.initDeck(supply);
            p2.initDeck(supply);
        }

        void play() {
            Player current = random.nextBoolean() ? p1 : p2;
            int playerNum = (current == p1) ? 1 : 2;
            while (!supply.isEmpty()) {
                takeTurn(current, playerNum);
                current = (current == p1) ? p2 : p1;
                playerNum = (playerNum == 1) ? 2 : 1;
            }
        }

        void takeTurn(Player player, int playerNum) {
            System.out.println("Player " + playerNum + " taking turn...");
            // Buy phase
            player.playCoins();
            int coins = player.getCoins();
            String cardName = player.chooseCardToBuy(supply, coins);
            if (cardName != null) {
                Card card = supply.getCard(cardName);
                supply.buy(cardName);
                player.buy(card);
                System.out.println("Bought: " + cardName);
            } else {
                System.out.println("No card bought.");
            }
            // Cleanup
            player.cleanup();
        }

        void declareWinner() {
            int ap1 = p1.getAPs();
            int ap2 = p2.getAPs();
            System.out.println("Player 1 APs: " + ap1);
            System.out.println("Player 2 APs: " + ap2);
            if (ap1 > ap2) {
                System.out.println("Player 1 wins!");
            } else if (ap2 > ap1) {
                System.out.println("Player 2 wins!");
            } else {
                System.out.println("It's a tie!");
            }
        }
    }

    public static int rollDie(Random random) {
        return random.nextInt(6) + 1;
    }

    public static int handleReRolls(Scanner scanner, Random random, int currentValue) {
        while (true) {
            System.out.println("Current value: " + currentValue + ". Re-roll? (y/n)");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                currentValue = rollDie(random);
            } else if (input.equals("n")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter y or n.");
            }
        }
        return currentValue;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setup();
        game.play();
        game.declareWinner();
    }
}
