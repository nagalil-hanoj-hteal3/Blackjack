package src;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        monteCarlo.runSimulation();

        // Show JOptionPane before initializing GUI
        JOptionPane.showMessageDialog(null, "Welcome to Blackjack! Press OK to start the game.");

        // Initialize GUI after closing JOptionPane
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI gui = new GUI();
            }
        });
    }
}
