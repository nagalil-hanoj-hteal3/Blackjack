package src;

import java.util.*;

public class monteCarloControl {
    private static final double initialEpsilon = 1.0;
    private static final double minEpsilon = 0.01;
    private static final double alpha = 0.001;
    private static final double gamma = 1.0;
    private static final double decay = 0.99;

    private Map<List<Integer>, double[]> Q = new HashMap<>();
    private Map<List<Integer>, Double> N;
    private List<EpisodeStep> episode = new ArrayList<>();

    public void mc_control(int num_episodes, Map<List<Integer>, double[]> QValues, Map<List<Integer>, Double> predictN) {
        //Random rand = new Random();
        int nA = 2; // Assuming two actions (hit or stand)
        
        // Initialize Q map with the provided QValues
        Q = new HashMap<>(QValues);
        N = new HashMap<>(predictN);

        

        // for (int i_episode = num_episodes; i_episode <= num_episodes + 1; i_episode++) {
        //     double epsilon = Math.max(initialEpsilon * Math.pow(decay, i_episode), minEpsilon);
        //     List<EpisodeStep> episode = play_game(rand, nA, epsilon);
        //     update_Q(episode, alpha, gamma);
        // }
    }

    public void addEpisodeStep(int playerScore, int dealerCard, int action, double reward) {
        EpisodeStep step = new EpisodeStep(playerScore, dealerCard, action, reward);
        episode.add(step);
    }
    
    public void clearEpisode() {
        episode = new ArrayList<>();
    }

    private void update_Q(List<EpisodeStep> episode) {
        /*for (int i = 0; i < episode.size(); i++) {
            EpisodeStep step = episode.get(i);
            List<Integer> stateActionPair = Arrays.asList(step.state, step.action);
            
            double G = 0;
            for (int j = i; j < episode.size(); j++) {
                double gammaPower = Math.pow(gamma, j - i);
                double reward = episode.get(j).reward;
                G += reward * gammaPower;
            }
            
            double[] qValues = Q.getOrDefault(stateActionPair, new double[1]);
            qValues[0] += alpha * (G - qValues[0]); // Update Q-value using alpha
            Q.put(stateActionPair, qValues);
        }*/
        for (int i = 0; i < episode.size(); i++) {
            EpisodeStep step = episode.get(i);
            List<Integer> stateActionPair = new ArrayList<>();
            stateActionPair.add(step.playerScore);
            stateActionPair.add(step.dealerCard);
            stateActionPair.add(step.action);

            int firstOccurrenceIdx = -1;
            for (int j = i; j >= 0; j--) {
                if (episode.get(j).playerScore == step.playerScore && episode.get(j).dealerCard == step.dealerCard && episode.get(j).action == step.action) {
                    firstOccurrenceIdx = j;
                    break;
                }
            }

            double G = 0;
            for (int j = firstOccurrenceIdx; j < episode.size(); j++) {
                double gammaPower = Math.pow(gamma, j - firstOccurrenceIdx);
                double reward = episode.get(j).reward;
                G += reward * gammaPower;
            }

            N.putIfAbsent(stateActionPair, 0.0);
            N.put(stateActionPair, N.get(stateActionPair) + 1);
            
            Q.putIfAbsent(stateActionPair, new double[]{0.0});
            double[] qValues = Q.get(stateActionPair);
            qValues[0] += (G - qValues[0]) / N.get(stateActionPair);
            //qValues[0] += alpha * (G - qValues[0]); // Update Q-value using alpha
            Q.put(stateActionPair, qValues);
        }
    }    

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
        double[] hitValue = Q.get(hitPair);
        double[] standValue = Q.get(standPair);
        //double[] standValues = Q.getOrDefault(Arrays.asList(state, 1), new double[1]);
        
        //System.out.println("Dealer Card: " + dealerCard);
        System.out.println("Hit Value: " + hitValue[0]);
        System.out.println("Stand Value: " + standValue[0]);
         //System.out.println("Stand Value: "+standValues[0]);
        if (hitValue[0] > standValue[0]) {
            return 0; // Hit
        } else {
            if(state <= 11) {
                System.out.println("Score <= 11, hitting regardless.");
                return 0; // Hit
            }
            return 1; // Stand
        }
    }

    // private double[] get_probs(List<Integer> state, double epsilon, int nA) {
    //     double[] probs = new double[nA];
    //     int bestAction = getBestAction(state.get(0));
        
    //     for (int i = 0; i < nA; i++) {
    //         if (i == bestAction) {
    //             probs[i] = 1.0 - epsilon + (epsilon / nA);
    //         } else {
    //             probs[i] = epsilon / nA;
    //         }
    //     }
        
    //     return probs;
    // }
/*
    private List<EpisodeStep> play_game(Random rand, int nA, double epsilon) {
        List<EpisodeStep> episode = new ArrayList<>();
        blackjack Blackjack = new blackjack();
        Blackjack.dealInitialHands();
        
        while (!Blackjack.isPlayerBust()) {
            int state = Blackjack.getPlayerScore();
            double[] probs = get_probs(Arrays.asList(state), epsilon, nA);
            int action = rand.nextDouble() < probs[0] ? 0 : 1; // Select action based on epsilon-greedy policy
            
            int reward = action == 1 ? 0 : determineReward(Blackjack); // Reward is 0 for standing, or actual reward for hitting
            EpisodeStep step = new EpisodeStep(state, action, reward);
            episode.add(step);
            
            if (action == 1) {
                break; // Player stands, end the episode
            }
            
            Blackjack.playerHit();
        }
        
        return episode;
    }*/

    public void determineReward(blackjack Blackjack) {
        int reward;

        if (Blackjack.isPlayerWin()) {
            reward = 1;
        } else if (Blackjack.isGameDraw()) {
            reward = 0;
        } else {
            reward = -1;
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