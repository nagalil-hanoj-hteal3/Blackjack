package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class blackjack {
    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private int playerScore;
    private int dealerScore;
    private boolean playerHasBlackjack;

    public blackjack() {
        initializeDeck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        playerScore = 0;
        dealerScore = 0;
        playerHasBlackjack = false;
    }

    private void initializeDeck() {
        deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    public void dealInitialHands() {
        playerHand.clear();
        dealerHand.clear();
        for (int i = 0; i < 2; i++) {
            playerHand.add(drawCard());
            dealerHand.add(drawCard());
        }
        playerScore = calculateScore(playerHand);
        dealerScore = calculateScore(dealerHand);

        // Check if player has a blackjack (ace + 10)
        if (playerScore == 21 && playerHand.size() == 2) {
            playerHasBlackjack = true;
        }
    }

    public boolean isPlayerHasBlackjack() { return playerHasBlackjack; }

    private Card drawCard() {
        Random rand = new Random();
        int index = rand.nextInt(deck.size());
        return deck.remove(index);
    }

    private int calculateScore(List<Card> hand) {
        int score = 0;
        int numAces = 0;
        for (Card card : hand) {
            if (card.getRank().getValue() == 1) {
                numAces++;
                score += 11;
            } else if (card.getRank().getValue() > 10) {
                score += 10;
            } else {
                score += card.getRank().getValue();
            }
        }
        while (score > 21 && numAces > 0) {
            score -= 10;
            numAces--;
        }
        return score;
    }

    public void playerHit() {
        playerHand.add(drawCard());
        playerScore = calculateScore(playerHand);
    }

    public void dealerHit() {
        dealerHand.add(drawCard());
        dealerScore = calculateScore(dealerHand);
    }

    public boolean isPlayerBust() { return playerScore > 21; }

    public boolean isDealerBust() { return dealerScore > 21; }

    public boolean isPlayerWin() { return !isPlayerBust() && (playerScore > dealerScore || isDealerBust()); }

    public boolean isDealerWin() { return !isDealerBust() && (dealerScore > playerScore || isPlayerBust()); }

    public boolean isGameDraw() { return playerScore == dealerScore; }

    public List<Card> getPlayerHand() { return playerHand; }

    public List<Card> getDealerHand() { return dealerHand; }

    public int getPlayerScore() { return playerScore; }

    public int getDealerScore() { return dealerScore; }

    public boolean isGameOver() {
        return (isPlayerBust() || isDealerBust() || isPlayerWin() || isDealerWin() || isGameDraw());
    }

}