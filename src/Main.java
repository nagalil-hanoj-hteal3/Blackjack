package src;

import javax.swing.JOptionPane;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // code to call the outputs in the compiler to train
        monteCarloControl control = new monteCarloControl();
        int num = monteCarloPredict.getNumSimulations();

        Map<List<Integer>, double[]> Q_basic = new HashMap<>();
        Map<List<Integer>, Double> N_basic;

        Map<List<Integer>, double[]> Q_rand = new HashMap<>();
        //Map<List<Integer>, Double> N_rand;

        //basic policy
        Q_basic = monteCarloPredict.runSimulation(Q_basic, 0);
        N_basic = monteCarloPredict.N;

        //random policy
        Q_rand = monteCarloPredict.runSimulation(Q_rand, 1);
        //N_rand = monteCarloPredict.N;

        control.mc_control(num, Q_basic, N_basic, Q_rand); // Pass Q-values to mc_control
        control.runSimulation();

        // Show JOptionPane before initializing GUI
        int option = JOptionPane.showConfirmDialog(null, "Welcome to Blackjack!", "Welcome", JOptionPane.OK_CANCEL_OPTION);

        // Initialize GUI after closing JOptionPane
        if (option == JOptionPane.OK_OPTION) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    @SuppressWarnings("unused")
                    GUI gui = new GUI(control);
                }
            });
        }

    }
}
