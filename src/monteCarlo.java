package src;
// import java.util.HashMap;
// import java.util.Map;

public class monteCarlo {
    private static final int NUM_SIMULATIONS = 100000;

    public static void runSimulation() {
        int playerWins = 0;
        int dealerWins = 0;
        int draws = 0;

        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            blackjack Blackjack = new blackjack();
            Blackjack.dealInitialHands();

            while (!Blackjack.isPlayerBust()) {
                if (Blackjack.getPlayerScore() >= 17) {
                    break;
                }
                Blackjack.playerHit();
            }

            while (!Blackjack.isDealerBust() && Blackjack.getDealerScore() < 17) {
                Blackjack.dealerHit();
            }

            if (Blackjack.isPlayerWin()) {
                playerWins++;
            } else if (Blackjack.isDealerWin()) {
                dealerWins++;
            } else {
                draws++;
            }
        }

        System.out.println("Player wins: " + playerWins);
        System.out.println("Dealer wins: " + dealerWins);
        System.out.println("Draws: " + draws);
    }
}
