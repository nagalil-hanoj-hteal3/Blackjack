package src;

import javax.swing.JOptionPane;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // code to call the outputs in the compiler to train
        monteCarloControl control = new monteCarloControl();
        int num = monteCarloPredict.getNumSimulations();
        Map<List<Integer>, double[]> QValues = monteCarloPredict.runSimulation();
        Map<List<Integer>, Double> N = monteCarloPredict.N;

        control.mc_control(num, QValues, N); // Pass Q-values to mc_control

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
