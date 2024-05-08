package src;

import java.util.*;

public class monteCarloControl {
    private static final double alpha = 0.001;
    private static final double gamma = 1.0;
    int reward;

    private Map<List<Integer>, double[]> Q = new HashMap<>();
    private Map<List<Integer>, double[]> Q_random = new HashMap<>();
    private Map<List<Integer>, Double> N;
    private List<EpisodeStep> episode = new ArrayList<>();

    public void mc_control(int num_episodes, Map<List<Integer>, double[]> QValues, Map<List<Integer>, Double> predictN, Map<List<Integer>, double[]> Q_rand) {
        //Random rand = new Random();
        // int nA = 2; // Assuming two actions (hit or stand)
        // Initialize Q map with the provided QValues
        Q = new HashMap<>(QValues);
        Q_random = new HashMap<>(Q_rand);
        N = new HashMap<>(predictN);
    }

    public void addEpisodeStep(int playerScore, int dealerCard, int action, double reward) {
        EpisodeStep step = new EpisodeStep(playerScore, dealerCard, action, reward);
        episode.add(step);
    }
    
    public void clearEpisode() {
        episode = new ArrayList<>();
    }

    private void update_Q(List<EpisodeStep> episode) {
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

            N.putIfAbsent(stateActionPair, 0.0);
            N.put(stateActionPair, N.get(stateActionPair) + 1);
            
            //update Q value
            Q.putIfAbsent(stateActionPair, new double[]{0.0});
            double[] qValues = Q.get(stateActionPair);
            //qValues[0] += (G - qValues[0]) / N.get(stateActionPair);
            qValues[0] += alpha * (G - qValues[0]); // Update Q-value using alpha
            Q.put(stateActionPair, qValues);
        }
    }    

    //return the best action based on the current state
    public int getBestAction(int state, int dealerCard) {
        List<Integer> hitPair = new ArrayList<>();
        hitPair.add(state);
        hitPair.add(dealerCard);
        hitPair.add(0);

        List<Integer> standPair = new ArrayList<>();
        standPair.add(state);
        standPair.add(dealerCard);
        standPair.add(1);

        Q.putIfAbsent(hitPair, new double[]{0.0});
        Q.putIfAbsent(standPair, new double[]{0.0});
        Q_random.putIfAbsent(hitPair, new double[]{0.0});
        Q_random.putIfAbsent(standPair, new double[]{0.0});
        double[] hitValue = Q.get(hitPair);
        double[] standValue = Q.get(standPair);
        
        //System.out.println("Dealer Card: " + dealerCard);
        System.out.println("Hit Value: " + hitValue[0]);
        System.out.println("Stand Value: " + standValue[0]);

        if (hitValue[0] > standValue[0]) {
            return 0; // Hit
        } else if(state <= 11) {
            System.out.println("Score <= 11, hitting regardless.");
            return 0; // Hit
        } else {
            return 1; // Stand
        }
    }

    //used by runSimulation() to get the best action based on the current state
    private int getBestActionSimulation(int state, int dealerCard) {
        List<Integer> hitPair = new ArrayList<>();
        hitPair.add(state);
        hitPair.add(dealerCard);
        hitPair.add(0);

        List<Integer> standPair = new ArrayList<>();
        standPair.add(state);
        standPair.add(dealerCard);
        standPair.add(1);

        Q.putIfAbsent(hitPair, new double[]{0.0});
        Q.putIfAbsent(standPair, new double[]{0.0});

        double[] hitValue = Q.get(hitPair);
        double[] standValue = Q.get(standPair);

        if (hitValue[0] > standValue[0]) {
            return 0; // Hit
        } else if(state <= 11) {
            //Score <= 11, hitting regardless
            return 0; // Hit
        } else {
            return 1; // Stand
        }
    }

    //used to gather the results of using the optimal policy
    public void runSimulation() {
        Random rand = new Random();
        int num_simulations = 500000;
        int[] stats = new int[3];

        for (int i = 0; i < num_simulations; i++) {
            List<EpisodeStep> episode = new ArrayList<>();
            blackjack blackjack = new blackjack();
            blackjack.dealInitialHands();

            while (!blackjack.isPlayerBust() || blackjack.isDealerWin()) {
                int playerScore = blackjack.getPlayerScore();
                int dealerCard = blackjack.getDealerShownScore();

                double randNum = rand.nextDouble();
                randNum = Math.round(randNum * 10.0) / 10.0;

                int action = getBestActionSimulation(blackjack.getPlayerScore(), blackjack.getDealerShownScore());
                
                //stand (1) 80% chance when score 17 or more, else hit (0)
                //action = ((playerScore >= 17) && (randNum < policyProb))? 1 : 0;

                int reward;
                if (blackjack.isPlayerWin()) {
                    reward = 1;
                } else if (blackjack.isPlayerBust()) {
                    reward = -1;
                } else {
                    reward = 0;
                }

                EpisodeStep step = new EpisodeStep(playerScore, dealerCard, action, reward);
                episode.add(step);

                if (action == 1) {
                    break;
                }

                blackjack.playerHit();
            }

            //update statistics
            if (blackjack.isPlayerWin()) { stats[0]++; } 
            else if (blackjack.isGameDraw()){ stats[1]++; } 
            else if(blackjack.isDealerWin()) { stats[2]++; }
        }
        
        System.out.println("Optimal policy results\n");
        printFinalResults(stats[0], stats[1], stats[2], num_simulations);
    }

    private static void printFinalResults(int playerWins, int playerDraws, int playerLosses, int num_simulations) {
        double playerWinPercentage = (double) playerWins / num_simulations * 100;
        double drawPercentage = (double) playerDraws / num_simulations * 100;
        double playerLosePercentage = (double) playerLosses / num_simulations * 100;
    
        playerWinPercentage = Math.round(playerWinPercentage * 10) / 10.0;
        drawPercentage = Math.round(drawPercentage * 10) / 10.0;
        playerLosePercentage = Math.round(playerLosePercentage * 10) / 10.0;
    
        //System.out.println("\n=====================================\n");
        //System.out.println("    Monte-Carlo Algorithm Results:   \n");
        System.out.println("Player wins: " + playerWins + " => " + playerWinPercentage + "%");
        System.out.println("Player losses: " + playerLosses + " => " + playerLosePercentage + "%");
        System.out.println("Draws: " + playerDraws + " => " + drawPercentage + "%");
        System.out.println("\n=====================================\n");
    }

    
    //called by GUI at the end of a game to update Q based on its outcome
    public void determineReward(blackjack Blackjack) {
        // int reward;

        if (Blackjack.isPlayerWin()) {
            reward = 1;
        } else if (Blackjack.isPlayerBust()) {
            reward = -1;
        } else {
            reward = 0;
        }

        update_Q(episode);
    }

    private static class EpisodeStep {
        // int state;
        int playerScore;
        int dealerCard;
        int action;
        double reward;

        EpisodeStep(int playerScore, int dealerCard, int action, double reward) {
            // this.state = state;
            this.playerScore = playerScore;
            this.dealerCard = dealerCard;
            this.action = action;
            this.reward = reward;
        }
    }
}