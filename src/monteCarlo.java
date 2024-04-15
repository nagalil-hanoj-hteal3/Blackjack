package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class monteCarlo {
    private static final int NUM_SIMULATIONS = 1000;

    static class EpisodeStep {
        int[] state;
        int action;
        double reward;

        public EpisodeStep(int[] state, int action, double reward) {
            this.state = state;
            this.action = action;
            this.reward = reward;
        }
    }

    public static void runSimulation() {
        // value for policy 
        double policyProb = 0.8;

        

        int playerWins = 0;
        // int dealerWins = 0;
        int draws = 0;

        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            blackjack Blackjack = new blackjack();
            Blackjack.dealInitialHands();

            while (!Blackjack.isPlayerBust()) {
                Random r = new Random();
                double randNum = r.nextDouble();
                randNum = Math.round(randNum * 10.0) / 10.0;

                if ((Blackjack.getPlayerScore() >= 17) && (randNum < policyProb)) {
                    // System.out.println("Player Score at " + Blackjack.getPlayerScore());
                    break;
                }
                Blackjack.playerHit();
            }

            // while (!Blackjack.isDealerBust() && Blackjack.getDealerScore() < 17) {
            //     // System.out.println("Dealer Score at " + Blackjack.getDealerScore());
            //     Blackjack.dealerHit();
            // }

            if (Blackjack.isPlayerWin()) {
                playerWins++;
                // System.out.println("Player wins");
            // } else if (Blackjack.isDealerWin()) {
            //     dealerWins++;
                // System.out.println("Dealer Wins");
            }
             else {
                draws++;
                // System.out.println("No one wins");
            }

            double playerWinPercentage = (double) playerWins / NUM_SIMULATIONS * 100;
            // double dealerWinPercentage = (double) dealerWins / NUM_SIMULATIONS * 100;
            double drawPercentage = (double) draws / NUM_SIMULATIONS * 100;

            playerWinPercentage = Math.round(playerWinPercentage * 10) / 10.0;
            // dealerWinPercentage = Math.round(dealerWinPercentage * 10) / 10.0;
            drawPercentage = Math.round(drawPercentage * 10) / 10.0;

            System.out.println("=====================================");
            System.out.println("    Monte-Carlo Algorithm Results:   \n");
            System.out.println("Game #" + (i + 1));
            System.out.println("Player wins: " + playerWins + " => " + playerWinPercentage + "%");
            // System.out.println("Dealer wins: " + dealerWins + " => " + dealerWinPercentage + "%");
            System.out.println("Draws: " + draws + " => " + drawPercentage + "%");
            
        }
        System.out.println("=====================================\n");
        
    }
}