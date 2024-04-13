package src;

public class monteCarlo {
    private static final int NUM_SIMULATIONS = 1000;

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

        double playerWinPercentage = (double) playerWins / NUM_SIMULATIONS * 100;
        double dealerWinPercentage = (double) dealerWins / NUM_SIMULATIONS * 100;
        double drawPercentage = (double) draws / NUM_SIMULATIONS * 100;

        playerWinPercentage = Math.round(playerWinPercentage * 10) / 10.0;
        dealerWinPercentage = Math.round(dealerWinPercentage * 10) / 10.0;
        drawPercentage = Math.round(drawPercentage * 10) / 10.0;

        System.out.println("=====================================");
        System.out.println("    Monte-Carlo Algorithm Results:   \n");
        System.out.println("Player wins: " + playerWins + " => " + playerWinPercentage + "%");
        System.out.println("Dealer wins: " + dealerWins + " => " + dealerWinPercentage + "%");
        System.out.println("Draws: " + draws + " => " + drawPercentage + "%");
        System.out.println("=====================================\n");
    }
}
