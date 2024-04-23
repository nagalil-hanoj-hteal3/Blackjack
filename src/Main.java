package src;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        // code to call the outputs in the compiler to train
        monteCarloControl control = new monteCarloControl();
        monteCarloPredict.runSimulation();

        control.mc_control(10);

        // Show JOptionPane before initializing GUI
        int option = JOptionPane.showConfirmDialog(null, "Welcome to Blackjack!", "Welcome", JOptionPane.OK_CANCEL_OPTION);

        // plan to use the GUI class for calling the monteCarlo.runSimulation() 
        // for displaying a live gameplay of the model playing blackjack

        // have another option for allowing the user to play the game
        
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
