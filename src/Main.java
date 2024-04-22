package src;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        // code to call the outputs in the compiler to train
        monteCarlo.runSimulation();

        // Show JOptionPane before initializing GUI
        JOptionPane.showMessageDialog(null, "Welcome to Blackjack! Press OK to start the game.");

        // plan to use the GUI class for calling the monteCarlo.runSimulation() 
        // for displaying a live gameplay of the model playing blackjack

        // have another option for allowing the user to play the game
        
        // Initialize GUI after closing JOptionPane
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI gui = new GUI();
            }
        });
    }
}
