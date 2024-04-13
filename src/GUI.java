package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JLabel playerHandLabel;
    private JLabel dealerHandLabel;
    private JLabel playerScoreLabel;
    private JLabel dealerScoreLabel;
    private JButton hitButton;
    private JButton standButton;

    private JLabel background;

    private blackjack Blackjack;

    // constructor
    public GUI() {
        frame = new JFrame("Blackjack");
        panel = new JPanel();

        // add contents of the GUI
        ImageIcon backgroundImg = new ImageIcon("image/board/board.jpg");
        background = new JLabel(backgroundImg);
        background.setLayout(new BorderLayout());

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
                    // JOptionPane.showMessageDialog(frame, "Player Bust! Dealer Wins!");
                    int choice = JOptionPane.showConfirmDialog(frame, "Player Bust! Dealer Wins! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        restartGame();
                    } else {
                        frame.dispose();
                    }
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

                int choice;

                if (Blackjack.isDealerBust() || Blackjack.isPlayerWin()) {
                    endGame();
                    choice = JOptionPane.showConfirmDialog(frame, "Player Wins! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                } else if (Blackjack.isDealerWin()) {
                    endGame();
                    choice = JOptionPane.showConfirmDialog(frame, "Dealer Wins! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                } else {
                    endGame();
                    choice = JOptionPane.showConfirmDialog(frame, "Draw! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                }

                if (choice == JOptionPane.YES_OPTION) {
                    restartGame();
                } else {
                    frame.dispose();
                }
            }
        });

        panel.setLayout(new GridLayout(3, 2));
        panel.add(playerHandLabel);
        panel.add(playerScoreLabel);
        panel.add(dealerHandLabel);
        panel.add(dealerScoreLabel);
        panel.add(hitButton);
        panel.add(standButton);

        background.add(panel, BorderLayout.CENTER);
        frame.setContentPane(background);
        
        // frame.add(panel);
        frame.setSize(600,800);
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

    private void restartGame() {
        Blackjack = new blackjack();
        Blackjack.dealInitialHands();
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
        updateGUI();
    }

}
