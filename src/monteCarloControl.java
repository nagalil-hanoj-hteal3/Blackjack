package src;

import java.util.*;

public class monteCarloControl {
    private static final double initialEpsilon = 1.0;
    private static final double minEpsilon = 0.01;
    private static final double alpha = 0.001;
    private static final double gamma = 1.0;
    private static final double decay = 0.99;

    private Map<List<Integer>, double[]> Q = new HashMap<>();

    public void mc_control(int num_episodes, Map<List<Integer>, double[]> QValues) {
        Random rand = new Random();
        int nA = 2; // Assuming two actions (hit or stand)
        
        // Initialize Q map with the provided QValues
        Q = new HashMap<>(QValues);

        for (int i_episode = 1; i_episode <= num_episodes; i_episode++) {
            double epsilon = Math.max(initialEpsilon * Math.pow(decay, i_episode), minEpsilon);
            List<EpisodeStep> episode = play_game(rand, nA, epsilon);
            update_Q(episode, alpha, gamma);
        }
    }

    private void update_Q(List<EpisodeStep> episode, double alpha, double gamma) {
        for (int i = 0; i < episode.size(); i++) {
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
        }
    }    

    public int getBestAction(int state) {
        double[] actionValues = Q.getOrDefault(Arrays.asList(state, 0), new double[1]);
        double[] standValues = Q.getOrDefault(Arrays.asList(state, 1), new double[1]);
        
        // System.out.println("Action value: "+actionValues[0]);
        // System.out.println("Stand Value: "+standValues[0]);
        if (actionValues[0] <= standValues[0]) {
            return 0; // Hit
        } else {
            return 1; // Stand
        }
    }

    private double[] get_probs(List<Integer> state, double epsilon, int nA) {
        double[] probs = new double[nA];
        int bestAction = getBestAction(state.get(0));
        
        for (int i = 0; i < nA; i++) {
            if (i == bestAction) {
                probs[i] = 1.0 - epsilon + (epsilon / nA);
            } else {
                probs[i] = epsilon / nA;
            }
        }
        
        return probs;
    }

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
    }

    public int determineReward(blackjack Blackjack) {
        if (Blackjack.isPlayerWin()) {
            return 1;
        } else if (Blackjack.isGameDraw()) {
            return 0;
        } else {
            return -1;
        }
    }

    private static class EpisodeStep {
        int state;
        int action;
        double reward;

        EpisodeStep(int state, int action, double reward) {
            this.state = state;
            this.action = action;
            this.reward = reward;
        }
    }
}