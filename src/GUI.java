package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel playerHandLabel;
    private JLabel dealerHandLabel;
    private JLabel playerScoreLabel;
    private JLabel dealerScoreLabel;
    private JButton hitButton;
    private JButton standButton;

    private blackjack Blackjack;

    // constructor
    public GUI() {
        frame = new JFrame("Blackjack");
        panel = new JPanel();
        playerHandLabel = new JLabel("Player Hand: ");
        dealerHandLabel = new JLabel("Dealer Hand: ");
        playerScoreLabel = new JLabel("Player Score: ");
        dealerScoreLabel = new JLabel("Dealer Score: ");
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");

        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Blackjack.playerHit();
                updateGUI();
                printGameProgress();
                if (Blackjack.isPlayerBust()) {
                    endGame();
                    JOptionPane.showMessageDialog(frame, "Player Bust! Dealer Wins!");
                }
            }
        });

        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (!Blackjack.isDealerBust() && Blackjack.getDealerScore() < 17) {
                    Blackjack.dealerHit();
                }
                updateGUI();
                printGameProgress();
                if (Blackjack.isDealerBust() || Blackjack.isPlayerWin()) {
                    endGame();
                    JOptionPane.showMessageDialog(frame, "Player Wins!");
                } else if (Blackjack.isDealerWin()) {
                    endGame();
                    JOptionPane.showMessageDialog(frame, "Dealer Wins!");
                } else {
                    endGame();
                    JOptionPane.showMessageDialog(frame, "Draw!");
                }
            }
        });

        panel.setLayout(new GridLayout(2, 2));
        panel.add(playerHandLabel);
        panel.add(playerScoreLabel);
        panel.add(dealerHandLabel);
        panel.add(dealerScoreLabel);
        panel.add(hitButton);
        panel.add(standButton);

        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Blackjack = new blackjack();
        Blackjack.dealInitialHands();
        updateGUI();
        printGameProgress();

        ImageIcon icon = new ImageIcon("image/bj.jpg");
        // JLabel label = new JLabel(icon);
        frame.setIconImage(icon.getImage());
    }

    private void updateGUI() {
        playerHandLabel.setText("Player Hand: " + Blackjack.getPlayerHand());
        dealerHandLabel.setText("Dealer Hand: " + Blackjack.getDealerHand());
        playerScoreLabel.setText("Player Score: " + Blackjack.getPlayerScore());
        dealerScoreLabel.setText("Dealer Score: " + Blackjack.getDealerScore());
    }

    private void endGame() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
    }

    private void printGameProgress() {
        System.out.println("Player Hand: " + Blackjack.getPlayerHand());
        System.out.println("Player Score: " + Blackjack.getPlayerScore());
        System.out.println("Dealer Hand: " + Blackjack.getDealerHand());
        System.out.println("Dealer Score: " + Blackjack.getDealerScore());
        System.out.println();
    }

}
