package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {
    private JFrame frame;

    private JPanel panel;
    private JPanel buttonPanel;
    
    private JLabel playerHandLabel;
    private JLabel dealerHandLabel;
    private JLabel playerScoreLabel;
    private JLabel dealerScoreLabel;
    private JLabel resultLabel;

    private JButton hitButton;
    private JButton standButton;

    private blackjack Blackjack;

    private int choice = JOptionPane.NO_OPTION;

    private boolean playerHasStood = false;

    // constructor
    public GUI() {
        frame = new JFrame("Blackjack");
        frame.setLayout(new BorderLayout());

        // add contents of the GUI
        // ImageIcon backgroundImg = new ImageIcon("image/board/board.jpg");
        ImageIcon backgroundImg = new ImageIcon("image/board/csusm-theme.jpg");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImg.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        
        panel.setLayout(new BorderLayout());

        playerHandLabel = new JLabel("Player Hand: ");
        dealerHandLabel = new JLabel("Dealer Hand: ");
        playerScoreLabel = new JLabel("Player Score: ");
        dealerScoreLabel = new JLabel("Dealer Score: ");
        resultLabel = new JLabel("");

        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");

        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Blackjack.playerHit();
                updateGUI();
                printGameProgress();
                if (Blackjack.getPlayerScore() == 21) {
                    // Player wins with a blackjack
                    endGame();
                    updateResultLabel("Player wins with a Blackjack!");
                    choice = JOptionPane.showConfirmDialog(frame, "Player wins with a Blackjack! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        restartGame();
                    } else {
                        frame.dispose();
                    }
                } else if (Blackjack.isPlayerBust()) {
                    endGame();
                    updateResultLabel("Player Bust! Dealer Wins!");
                    // JOptionPane.showMessageDialog(frame, "Player Bust! Dealer Wins!");
                    choice = JOptionPane.showConfirmDialog(frame, "Player Bust! Dealer Wins! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
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
                playerHasStood = true;
                while (!Blackjack.isDealerBust() && Blackjack.getDealerScore() < 17) {
                    Blackjack.dealerHit();
                }
                updateGUI();
                printGameProgress();

                // Check for ace + 6 rule
                if (Blackjack.getDealerHand().stream().anyMatch(card -> card.getRank().getValue() == 1) && 
                    Blackjack.getDealerHand().stream().anyMatch(card -> card.getRank().getValue() == 6)) {
                    while (!Blackjack.isDealerBust() && 
                        Blackjack.getDealerScore() < 17 &&
                        (Blackjack.getDealerHand().stream().anyMatch(card -> card.getRank().getValue() == 1) && 
                            Blackjack.getDealerHand().stream().anyMatch(card -> card.getRank().getValue() == 6))) {
                        Blackjack.dealerHit();
                    }
                }

                if (Blackjack.isDealerBust() || Blackjack.isPlayerWin()) {
                    endGame();
                    updateResultLabel("Player Wins!");
                    choice = JOptionPane.showConfirmDialog(frame, "Player Wins! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                } else if (Blackjack.isDealerWin()) {
                    endGame();
                    updateResultLabel("Dealer Wins!");
                    choice = JOptionPane.showConfirmDialog(frame, "Dealer Wins! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                } else {
                    endGame();
                    updateResultLabel("It is a stalemate!");
                    choice = JOptionPane.showConfirmDialog(frame, "Draw! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
                }

                if (choice == JOptionPane.YES_OPTION) {
                    restartGame();
                } else {
                    frame.dispose();
                }
            }
        });

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JPanel labelsPanel = new JPanel(new GridLayout(3, 2));
        labelsPanel.add(playerHandLabel);
        labelsPanel.add(playerScoreLabel);
        labelsPanel.add(dealerHandLabel);
        labelsPanel.add(dealerScoreLabel);
        labelsPanel.add(resultLabel);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // buttonPanel.setOpaque(false);
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        // buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);

        frame.add(labelsPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        // frame.setContentPane(background);
        
        frame.add(panel);
        frame.setSize(900,800);
        // frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Blackjack = new blackjack();
        Blackjack.dealInitialHands();

        // Check if the player has a blackjack
        if (Blackjack.isPlayerHasBlackjack()) {
            endGame();
            updateResultLabel("Player wins with a Blackjack!");
            choice = JOptionPane.showConfirmDialog(frame, "Player wins with a Blackjack! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                frame.dispose();
            }
        } else {
            updateGUI();
            printGameProgress();
        }

        ImageIcon icon = new ImageIcon("image/bj.jpg");
        // JLabel label = new JLabel(icon);
        frame.setIconImage(icon.getImage());
    }
    
    public void updateGUI() {
        // Clear previous card images
        panel.removeAll();
    
        // Set layout manager to BorderLayout
        panel.setLayout(new BorderLayout());
    
        // Create a JPanel for the player's hand
        JPanel playerHandPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 0));
        playerHandPanel.setOpaque(false);
    
        // Add player's hand images to the playerHandPanel
        for (Card card : Blackjack.getPlayerHand()) {
            addCardImage(card, playerHandPanel);
        }
    
        playerHandPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
    
        // Add the playerHandPanel to the top of the main panel
        panel.add(playerHandPanel, BorderLayout.NORTH);
    
        // Create a JPanel for the dealer's hand
        JPanel dealerHandPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 0));
        dealerHandPanel.setOpaque(false);
    
        dealerHandPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 200, 0));
    
        // Add dealer's hand images to the dealerHandPanel
        for (int i = 0; i < Blackjack.getDealerHand().size(); i++) {
            Card card = Blackjack.getDealerHand().get(i);
            // Create a panel for each card
            JPanel cardPanel = new JPanel(new BorderLayout());
            cardPanel.setOpaque(false); // Make the panel transparent
    
            if (playerHasStood || Blackjack.isDealerBust() || Blackjack.isPlayerBust() || i != 0) {
                // Show actual card images for all cards when the game is over or for non-first cards
                ImageIcon cardImage = new ImageIcon("image/cards/" + card.getRank().getName() + "-" + card.getSuit().getName() + ".png");
                Image scaledCardImage = cardImage.getImage().getScaledInstance(135, 200, Image.SCALE_SMOOTH);
                ImageIcon scaledCardIcon = new ImageIcon(scaledCardImage);
                JLabel cardLabel = new JLabel(scaledCardIcon);
                cardPanel.add(cardLabel, BorderLayout.CENTER);
            } else {
                // Show back of the card for the first card when the game is ongoing
                ImageIcon backCardImage = new ImageIcon("image/backCardCSUSM.png");
        
                // Get the dimensions of the card image
                int width = 135;
                int height = 200;
        
                // Scale the back card image
                Image scaledBackCardImage = backCardImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon scaledBackCardIcon = new ImageIcon(scaledBackCardImage);
        
                // Create and add the back card label to the card panel
                JLabel backCardLabel = new JLabel(scaledBackCardIcon);
                cardPanel.add(backCardLabel, BorderLayout.CENTER);
            }
    
            // Add the card panel to the dealerHandPanel
            dealerHandPanel.add(cardPanel);
        }
    
        // If game is not over, show "*" as the dealer's score
        String dealerScoreText = (playerHasStood || Blackjack.isDealerBust() || Blackjack.isPlayerBust()) ? Integer.toString(Blackjack.getDealerScore()) : "*";
        dealerScoreLabel.setText("Dealer Score: " + dealerScoreText);
    
        // Add the dealerHandPanel to the bottom of the main panel
        panel.add(dealerHandPanel, BorderLayout.SOUTH);
    
        // Repaint the panel to update changes
        panel.revalidate();
        panel.repaint();
    
        // Update player score label
        playerScoreLabel.setText("Player Score: " + Blackjack.getPlayerScore());
    
        // Update player hand label
        playerHandLabel.setText("Player Hand: " + Blackjack.getPlayerHand());

        // Update dealer hand label
        String dealerHandText = "Dealer Hand: [";

        // Add '*' for the first card if the game is ongoing and it's not revealed yet
        if (!playerHasStood && !Blackjack.isDealerBust() && !Blackjack.isPlayerBust()) {
            dealerHandText += "*";
        } else {
            dealerHandText += Blackjack.getDealerHand().get(0); // Add actual first card if revealed
        }

        // Add actual card values for the rest of the cards
        for (int i = 1; i < Blackjack.getDealerHand().size(); i++) {
            dealerHandText += ", " + Blackjack.getDealerHand().get(i);
        }

        dealerHandLabel.setText(dealerHandText + "]");
 // Reset the choice variable
        choice = JOptionPane.NO_OPTION;
    }
    
    private void addCardImage(Card card, JPanel panel) {
        String imageName = card.getRank().getName() + "-" + card.getSuit().getName() + ".png";
        ImageIcon originalCardImage = new ImageIcon("image/cards/" + imageName);
        
        // Scale the original image to the desired size
        Image scaledImage = originalCardImage.getImage().getScaledInstance(135, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledCardImage = new ImageIcon(scaledImage);
        
        // Create a JLabel with the scaled card image
        JLabel cardLabel = new JLabel(scaledCardImage);
        
        // Add the JLabel to the specified panel
        panel.add(cardLabel);
    }     

    public void endGame() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
    }

    public void printGameProgress() {
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
        playerHasStood = false;
        updateResultLabel(null);
        updateGUI();
    }

    private void updateResultLabel(String result) {
        resultLabel.setText(result);
    }
}