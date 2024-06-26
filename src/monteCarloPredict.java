package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class monteCarloPredict {
    private static final int NUM_SIMULATIONS = 500000;

    private static final double initialEpsilon = 1.0;
    private static final double minEpsilon = 0.01;
    private static final double gamma = 0.9;
    private static final double decay = 0.9999;

    public static Map<List<Integer>, Double> N;

    static class EpisodeStep {
        int playerScore;
        int dealerCard;
        int action;
        double reward;

        public EpisodeStep(int playerScore, int dealerCard, int action, double reward) {
            this.playerScore = playerScore;
            this.dealerCard = dealerCard;
            this.action = action;
            this.reward = reward;
        }
    }

    //int policy (0 = basic policy, 1 = random policy)
    public static Map<List<Integer>, double[]> runSimulation(Map<List<Integer>, double[]> Q, int policy) {
        double epsilon = initialEpsilon;
        Map<List<Integer>, double[]> returnsSum = new HashMap<>();
        N = new HashMap<>();
        Q = new HashMap<>();
        int[] stats = new int[3];

        double policyProb = 0.8;
        Random rand = new Random();

        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            epsilon = Math.max(initialEpsilon * Math.pow(decay, i), minEpsilon);
            

            List<EpisodeStep> episode = new ArrayList<>();
            blackjack blackjack = new blackjack();
            blackjack.dealInitialHands();

            while (!blackjack.isPlayerBust() || blackjack.isDealerWin()) {
                int playerScore = blackjack.getPlayerScore();
                int dealerCard = blackjack.getDealerShownScore();

                double randNum = rand.nextDouble();
                randNum = Math.round(randNum * 10.0) / 10.0;

                int action;

                //policy 0: basic policy, policy 1: random policy
                if (policy == 0) {
                    if (rand.nextDouble() < epsilon) {
                        action = rand.nextInt(2); // Random action
                    } else {
                        //action = (playerScore >= 17) ? 1 : 0; // Basic policy: hit if player score is less than 17, stand otherwise
                        action = ((playerScore >= 17) && (randNum < policyProb))? 1 : 0; //stand (1) 80% chance when score 17 or more, else hit (0)
                    }
                } else {
                    action = rand.nextInt(2);
                }
                
                //stand (1) 80% chance when score 17 or more, else hit (0)
                //action = ((playerScore >= 17) && (randNum < policyProb))? 1 : 0;

                int reward = determineReward(blackjack, action);
                EpisodeStep step = new EpisodeStep(playerScore, dealerCard, action, reward);
                episode.add(step);

                if (action == 1) {
                    break;
                }

                blackjack.playerHit();
            }
            updateStatistics(blackjack, stats, i);
            Q = updateQ(episode, returnsSum, N, Q); 
        }

        switch(policy) {
            case 0:
                System.out.println("\n=====================================\n");
                System.out.println("Basic policy results\n");
                break;
            case 1:
                System.out.println("Random policy results\n");
                break;
        }
        printFinalResults(stats[0], stats[1], stats[2]);

        System.out.println("\n=====================================\n");

        return Q;
    }

    public static int getNumSimulations() {
        return NUM_SIMULATIONS;
    }

    private static void updateStatistics(blackjack Blackjack, int stats[], int i) {
        if (Blackjack.isPlayerWin()) { stats[0]++; } 
        else if (Blackjack.isGameDraw()){ stats[1]++; } 
        else if(Blackjack.isDealerWin()) { stats[2]++; }
    }

    private static void printFinalResults(int playerWins, int playerDraws, int playerLosses) {
        double playerWinPercentage = (double) playerWins / NUM_SIMULATIONS * 100;
        double drawPercentage = (double) playerDraws / NUM_SIMULATIONS * 100;
        double playerLosePercentage = (double) playerLosses / NUM_SIMULATIONS * 100;
    
        playerWinPercentage = Math.round(playerWinPercentage * 10) / 10.0;
        drawPercentage = Math.round(drawPercentage * 10) / 10.0;
        playerLosePercentage = Math.round(playerLosePercentage * 10) / 10.0;
    
        //System.out.println("\n=====================================\n");
        //System.out.println("    Monte-Carlo Algorithm Results:   \n");
        System.out.println("Player wins: " + playerWins + " => " + playerWinPercentage + "%");
        System.out.println("Player losses: " + playerLosses + " => " + playerLosePercentage + "%");
        System.out.println("Draws: " + playerDraws + " => " + drawPercentage + "%");
    }

    private static Map<List<Integer>, double[]> updateQ(List<EpisodeStep> episode, Map<List<Integer>, double[]> returnsSum, Map<List<Integer>, Double> N, Map<List<Integer>, double[]> Q) {
        //Loop through each step in the current episode (game)
        for (int i = 0; i < episode.size(); i++) {
            EpisodeStep step = episode.get(i);
            List<Integer> stateActionPair = new ArrayList<>();
            stateActionPair.add(step.playerScore);
            stateActionPair.add(step.dealerCard);
            stateActionPair.add(step.action);

            // find the first occurence of a state/action pair
            int firstOccurrenceIdx = -1;
            for (int j = i; j >= 0; j--) {
                if (episode.get(j).playerScore == step.playerScore && episode.get(j).dealerCard == step.dealerCard && episode.get(j).action == step.action) {
                    firstOccurrenceIdx = j;
                    break;
                }
            }
            
            //add discounted rewards
            double G = 0;
            for (int j = firstOccurrenceIdx; j < episode.size(); j++) {
                double gammaPower = Math.pow(gamma, j - firstOccurrenceIdx);
                double reward = episode.get(j).reward;
                G += reward * gammaPower;
            }

            returnsSum.putIfAbsent(stateActionPair, new double[]{0.0});
            returnsSum.get(stateActionPair)[0] += G;

            //amount of each state/action pair
            N.putIfAbsent(stateActionPair, 0.0);
            N.put(stateActionPair, N.get(stateActionPair) + 1);

            //update Q value with the average of the returns and amount of each state/action pair
            Q.putIfAbsent(stateActionPair, new double[]{0.0});
            double[] qValues = Q.get(stateActionPair);
            //qValues[0] += (G - qValues[0]) / N.get(stateActionPair);
            qValues[0] = returnsSum.get(stateActionPair)[0] / N.get(stateActionPair);
            Q.put(stateActionPair, qValues);
        }

        return Q;
    }

    private static int determineReward(blackjack blackjack, int action) {
        if (blackjack.isPlayerWin()) { return 1; } 
        //else if (blackjack.isGameDraw() || (blackjack.getPlayerScore() < 21 && blackjack.getDealerScore() < 21)) { return 0; }
        else if (blackjack.isPlayerBust() || (action == 1 && blackjack.isDealerWin())) { return -1; }
        else { return 0; }
    }
}