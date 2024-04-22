package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class monteCarlo {
    private static final int NUM_SIMULATIONS = 500;

    private static final double initialEpsilon = 1.0;
    private static final double minEpsilon = 0.01;
    private static final double alpha = 0.001;
    private static final double gamma = 1.0;
    private static final double decay = 0.99;

    // Used to represent a single step in an episode
    static class EpisodeStep {
        int state;
        int action;
        double reward;

        public EpisodeStep(int state, int action, double reward) {
            this.state = state;
            this.action = action;
            this.reward = reward;
        }
    }

    // Used to run the Monte Carlo algorithm
    public static void runSimulation() {
        double epsilon = initialEpsilon;
        int[] stats = new int[3];

        // for randomizing their exploration/exploitation
        Random rand = new Random();

        Map<List<Integer>, double[]> returns_sum = new HashMap<>();
        Map<List<Integer>, Double> N = new HashMap<>();
        Map<List<Integer>, double[]> Q = new HashMap<>();

        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            // this is the exploration rate
            // epsilon = Math.max(initialEpsilon * decay, minEpsilon);
            epsilon = Math.max(initialEpsilon * Math.pow(decay, i), minEpsilon);

            List<EpisodeStep> episode = new ArrayList<>();
            blackjack Blackjack = new blackjack();
            Blackjack.dealInitialHands();

            // this while loop determines the exploration and exploitation
            while (!Blackjack.isPlayerBust()) {
                // randNum = Math.round(randNum * 10.0) / 10.0;

                int state = Blackjack.getPlayerScore();
                // int action = ((Blackjack.getPlayerScore() >= 17) && (randNum < policyProb))? 0 : 1;
                int action;

                // Epsilon-greedy action selection
                if (rand.nextDouble() < epsilon) {
                    // Exploration: Choose a random action
                    action = rand.nextInt(2); // Assuming there are 2 actions (hit or stand)
                } else {
                    // Exploitation: Choose the action with the highest estimated value
                    action = getBestAction(Q, state);
                }

                int reward = determineReward(Blackjack);

                EpisodeStep step = new EpisodeStep(state, action, reward);
                episode.add(step);

                if (action == 0) {
                    break;
                }

                Blackjack.playerHit();
            }

            // Update Q-values after all simulations
            update_Q(episode, Q, returns_sum, N, alpha, epsilon);

            updateStatistics(Blackjack, stats, i);
        }

        printFinalResults(stats[0], stats[1], stats[2]);

        System.out.println("\n=====================================\n");
    }

    private static void update_Q(List<EpisodeStep> episode, Map<List<Integer>, double[]> Q, Map<List<Integer>, double[]> returns_sum, Map<List<Integer>, Double> N, double alpha, double epsilon) {
        for (int i = 0; i < episode.size(); i++) {
            EpisodeStep step = episode.get(i);
            List<Integer> stateActionPair = new ArrayList<>();
            stateActionPair.add(step.state);
            stateActionPair.add(step.action);
    
            double G = 0;
            for (int j = i; j < episode.size(); j++) {
                G += Math.pow(gamma, j - i) * episode.get(j).reward;
            }
    
            // Update returns_sum and N
            if (!returns_sum.containsKey(stateActionPair)) {
                returns_sum.put(stateActionPair, new double[]{0.0});
            }
            returns_sum.get(stateActionPair)[0] += G;
    
            if (!N.containsKey(stateActionPair)) {
                N.put(stateActionPair, 0.0);
            }
            N.put(stateActionPair, N.get(stateActionPair) + 1);
    
            // Update Q-value with alpha
            if (!Q.containsKey(stateActionPair)) {
                Q.put(stateActionPair, new double[]{0.0});
            }
            double[] qValues = Q.get(stateActionPair);
            qValues[0] += alpha * (G - qValues[0]); // Update Q-value using alpha
            Q.put(stateActionPair, qValues);
        }
    }

    // Method to determine the best action based on Q-values
    private static int getBestAction(Map<List<Integer>, double[]> Q, int state) {
        // Assuming there are only two actions (hit or stand)
        if (Q.containsKey(Arrays.asList(state, 0)) && Q.containsKey(Arrays.asList(state, 1))) {
            if ((Q.get(Arrays.asList(state, 0))[0] > Q.get(Arrays.asList(state, 1))[0]) || state >= 17) {
                return 0; // Hit
            } else {
                return 1; // Stand
            }
        } 
        else {
            // If no Q-values are available, choose randomly
            return new Random().nextInt(2);
        }
    }

    // Method to determine the reward for the current state
    private static int determineReward(blackjack Blackjack) {
        if (Blackjack.isPlayerWin()) {
            return 1;
        } 
        // else if (Blackjack.isGameDraw()) {
        //     return 0;
        // }
        return -1; 
    }

    // Method to update win/loss statistics
    private static void updateStatistics(blackjack Blackjack, int stats[], int i) {
        if (Blackjack.isPlayerWin()) {
            stats[0]++;
        } else if (Blackjack.isGameDraw()){
            stats[1]++;
        } else if(Blackjack.isDealerWin()) {
            stats[2]++;
        }

        double playerWinPercentage = (double) stats[0] / NUM_SIMULATIONS * 100;
        double drawPercentage = (double) stats[1] / NUM_SIMULATIONS * 100;
        double playerLosePercentage = (double) stats[2] / NUM_SIMULATIONS * 100;

        playerWinPercentage = Math.round(playerWinPercentage * 10) / 10.0;
        drawPercentage = Math.round(drawPercentage * 10) / 10.0;
        playerLosePercentage = Math.round(playerLosePercentage * 10) / 10.0;

        System.out.println("\n=====================================\n");
        System.out.println("    Monte-Carlo Algorithm Results:   \n");
        System.out.println("Game #" + (i + 1));
        System.out.println("Player wins: " + stats[0] + " => " + playerWinPercentage + "%");
        System.out.println("Player losses: " + stats[1] + " => " + playerLosePercentage + "%");
        System.out.println("Draws: " + stats[2] + " => " + drawPercentage + "%");
    }

    private static void printFinalResults(int playerWins, int playerDraws, int playerLosses) {
        double playerWinPercentage = (double) playerWins / NUM_SIMULATIONS * 100;
        double drawPercentage = (double) playerDraws / NUM_SIMULATIONS * 100;
        double playerLosePercentage = (double) playerLosses / NUM_SIMULATIONS * 100;
    
        playerWinPercentage = Math.round(playerWinPercentage * 10) / 10.0;
        drawPercentage = Math.round(drawPercentage * 10) / 10.0;
        playerLosePercentage = Math.round(playerLosePercentage * 10) / 10.0;
    
        System.out.println("\n=====================================\n");
        System.out.println("    Monte-Carlo Algorithm Results:   \n");
        System.out.println("Player wins: " + playerWins + " => " + playerWinPercentage + "%");
        System.out.println("Player losses: " + playerLosses + " => " + playerLosePercentage + "%");
        System.out.println("Draws: " + playerDraws + " => " + drawPercentage + "%");
    }    
    
}

// package src;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Random;

// public class monteCarlo {
//     // private static final int NUM_SIMULATIONS = 10;

//     static class EpisodeStep {
//         int state;
//         int action;
//         double reward;

//         public EpisodeStep(int state, int action, double reward) {
//             this.state = state;
//             this.action = action;
//             this.reward = reward;
//         }
//     }

//     public static void runSimulation() {
//         double initialEpsilon = 1.0;
//         double minEpsilon = 0.01;
//         double alpha = 0.001;
//         double gamma = 1.0;
//         int num_episodes = 500000;

//         Random r = new Random();
//         blackjack Blackjack = new blackjack();

//         Map<List<Integer>, double[]> Q = new HashMap<>();

//         for (int i = 0; i < num_episodes; i++) {
//             double epsilon = Math.max(initialEpsilon * Math.pow(0.9999, i), minEpsilon);

//             Blackjack.dealInitialHands();
//             List<EpisodeStep> episode = play_game(Blackjack, Q, epsilon, 2, r);
//             update_Q(episode, Q, alpha, gamma);
//         }
//     }

//     private static void update_Q(List<EpisodeStep> episode, Map<List<Integer>, double[]> Q, double alpha, double gamma) {
//         for (int i = 0; i < episode.size(); i++) {
//             EpisodeStep step = episode.get(i);
//             List<Integer> stateActionPair = Arrays.asList(step.state, step.action);

//             double G = 0;
//             for (int j = i; j < episode.size(); j++) {
//                 G += Math.pow(gamma, j - i) * episode.get(j).reward;
//             }

//             if (!Q.containsKey(stateActionPair)) {
//                 Q.put(stateActionPair, new double[]{0.0, 0.0});
//             }
//             double[] qValues = Q.get(stateActionPair);
//             qValues[0] = qValues[0] + alpha * (G - qValues[0]);
//             Q.put(stateActionPair, qValues);
//         }
//     }

//     private static double[] get_probs(Map<List<Integer>, double[]> Q, int state, double epsilon, int nA) {
//         double[] policy_s = new double[nA];
//         Arrays.fill(policy_s, epsilon / nA);
//         if (Q.containsKey(Arrays.asList(state, 0))) {
//             double[] qValues = Q.get(Arrays.asList(state, 0));
//             int bestAction = (qValues[0] > qValues[1]) ? 0 : 1;
//             policy_s[bestAction] = 1 - epsilon + (epsilon / nA);
//         }
//         return policy_s;
//     }

//     private static List<EpisodeStep> play_game(blackjack Blackjack, Map<List<Integer>, double[]> Q, double epsilon, int nA, Random r) {
//         List<EpisodeStep> episode = new ArrayList<>();
//         // Deal initial hands for both player and dealer
//         Blackjack.dealInitialHands();
        
//         // Continue playing until the player busts or decides to stand
//         while (!Blackjack.isPlayerBust()) {
//             double[] probs = get_probs(Q, Blackjack.getPlayerScore(), epsilon, nA);
//             int action = (r.nextDouble() < probs[0]) ? 0 : 1;
//             int reward = (action == 0) ? (Blackjack.isPlayerWin() ? 1 : -1) : 0;
//             EpisodeStep step = new EpisodeStep(Blackjack.getPlayerScore(), action, reward);
//             episode.add(step);
//             if (action == 0) {
//                 break;
//             }
//             Blackjack.playerHit();
//         }
    
//         // Dealer's turn
//         while (!Blackjack.isGameOver() && Blackjack.getDealerScore() < 17) {
//             Blackjack.dealerHit();
//         }
    
//         return episode;
//     }
    
// }