package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.*;
import java.util.prefs.Preferences;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class TicTacToe extends JFrame implements ActionListener {
    private JButton[][] buttons;
    private JLabel statusLabel;
    private JLabel infoLabel;
    private int boardSize;
    private boolean playerTurn = true;
    private int movesCount = 0;
    private int humanWins = 0;
    private int botWins = 0;
    private Clip backgroundMusic;
    private boolean musicPlaying = true;
    private boolean multiplayer = false;
    private Preferences prefs;
    private boolean showTimer;
    private boolean showBoardInfo;
    private boolean showWinCounter;
    private Timer matchTimer;
    private long startTime;

    public TicTacToe() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
        boardSize = prefs.getInt("boardSize", 3);
        multiplayer = prefs.getBoolean("multiplayer", false);
        humanWins = prefs.getInt("humanWins", 0);
        botWins = prefs.getInt("botWins", 0);
        showTimer = prefs.getBoolean("showTimer", true);
        showBoardInfo = prefs.getBoolean("showBoardInfo", true);
        showWinCounter = prefs.getBoolean("showWinCounter", true);

        setTitle("Tic Tac Toe");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        playMusic("C:/Users/zafri/Downloads/kumpulan-lagu-barat_slipknot-before-i-forget.wav");
        showWelcomePage();
    }

    private void showWelcomePage() {
        if (matchTimer != null) matchTimer.cancel();

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Tic Tac Toe", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton playButton = new JButton("Play");
        JButton settingsButton = new JButton("Settings");

        playButton.addActionListener(e -> startGame());
        settingsButton.addActionListener(e -> showSettingsPage());

        buttonPanel.add(playButton);
        buttonPanel.add(settingsButton);

        add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void startGame() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        buttons = new JButton[boardSize][boardSize];
        movesCount = 0;
        playerTurn = true;

        JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 40));
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel(multiplayer ? "Player 1's Turn" : "Your Turn", SwingConstants.CENTER);
        topPanel.add(statusLabel, BorderLayout.NORTH);

        infoLabel = new JLabel("", SwingConstants.CENTER);
        updateInfoLabel();
        topPanel.add(infoLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        JButton muteButton = new JButton(musicPlaying ? "Mute" : "Unmute");
        muteButton.addActionListener(e -> toggleMusic());
        add(muteButton, BorderLayout.SOUTH);

        if (showTimer) {
            startTime = System.currentTimeMillis();
            matchTimer = new Timer();
            matchTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    updateInfoLabel();
                }
            }, 0, 1000);
        }

        revalidate();
        repaint();
    }

    private void updateInfoLabel() {
        String info = "";
        if (showTimer) {
            long seconds = (System.currentTimeMillis() - startTime) / 1000;
            info += "⏱ " + seconds + "s ";
        }
        if (showBoardInfo) {
            info += "| 📦 Spots taken: " + movesCount + " ";
        }
        if (showWinCounter) {
            info += "| 👤 Wins: Human " + humanWins + ", Bot " + botWins;
        }
        infoLabel.setText(info);
    }

    private void showSettingsPage() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JCheckBox multiplayerCheck = new JCheckBox("Multiplayer", multiplayer);
        JComboBox<Integer> boardSizeBox = new JComboBox<>();
        for (int i = 3; i <= 6; i++) boardSizeBox.addItem(i);
        boardSizeBox.setSelectedItem(boardSize);

        JCheckBox timerCheck = new JCheckBox("Match Timer", showTimer);
        JCheckBox boardInfoCheck = new JCheckBox("Board Info", showBoardInfo);
        JCheckBox counterCheck = new JCheckBox("Player Counter", showWinCounter);

        panel.add(new JLabel("Game Mode:")); panel.add(multiplayerCheck);
        panel.add(new JLabel("Board Size:")); panel.add(boardSizeBox);
        panel.add(timerCheck); panel.add(new JLabel("Keep track of match time"));
        panel.add(boardInfoCheck); panel.add(new JLabel("Show number of moves"));
        panel.add(counterCheck); panel.add(new JLabel("Show win counters"));

        JButton resetButton = new JButton("Reset to Default");
        resetButton.addActionListener(e -> {
            multiplayerCheck.setSelected(false);
            boardSizeBox.setSelectedItem(3);
            timerCheck.setSelected(true);
            boardInfoCheck.setSelected(true);
            counterCheck.setSelected(true);
        });
        panel.add(resetButton);
        panel.add(new JLabel("Reset all fields above"));

        int result = JOptionPane.showConfirmDialog(this, panel, "Settings", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            multiplayer = multiplayerCheck.isSelected();
            boardSize = (Integer) boardSizeBox.getSelectedItem();
            showTimer = timerCheck.isSelected();
            showBoardInfo = boardInfoCheck.isSelected();
            showWinCounter = counterCheck.isSelected();

            prefs.putBoolean("multiplayer", multiplayer);
            prefs.putInt("boardSize", boardSize);
            prefs.putBoolean("showTimer", showTimer);
            prefs.putBoolean("showBoardInfo", showBoardInfo);
            prefs.putBoolean("showWinCounter", showWinCounter);
        }

        showWelcomePage();
    }

    private void playMusic(String filepath) {
        try {
            if (backgroundMusic != null && backgroundMusic.isRunning()) {
                return; // Already playing
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filepath));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("Music error: " + e.getMessage());
        }
    }

    private void toggleMusic() {
        if (backgroundMusic != null) {
            if (musicPlaying) backgroundMusic.stop();
            else backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            musicPlaying = !musicPlaying;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if (!source.getText().equals("")) return;

        source.setText(playerTurn ? "X" : "O");
        source.setEnabled(false);
        movesCount++;
        updateInfoLabel();

        if (checkWinner(playerTurn ? "X" : "O")) {
            String winner;
            if (multiplayer) {
                winner = playerTurn ? "Player 1" : "Player 2";
            } else {
                winner = playerTurn ? "Human" : "Bot";
                if (playerTurn) humanWins++; else botWins++;
                prefs.putInt("humanWins", humanWins);
                prefs.putInt("botWins", botWins);
            }
            DatabaseManager.saveGameResult(multiplayer ? "multiplayer" : "singleplayer", boardSize, winner);
            showResult(winner + " Wins!");
            return;
        }

        if (movesCount == boardSize * boardSize) {
            DatabaseManager.saveGameResult(multiplayer ? "multiplayer" : "singleplayer", boardSize, "Draw");
            showResult("Draw!");
            return;
        }

        playerTurn = !playerTurn;
        statusLabel.setText(multiplayer ? (playerTurn ? "Player 1's Turn" : "Player 2's Turn") : (playerTurn ? "Your Turn" : "Bot Turn"));

        if (!multiplayer && !playerTurn) {
            SwingUtilities.invokeLater(this::botMove);
        }
    }

    private void botMove() {
        Random rand = new Random();
        int i, j;
        do {
            i = rand.nextInt(boardSize);
            j = rand.nextInt(boardSize);
        } while (!buttons[i][j].getText().equals(""));

        buttons[i][j].setText("O");
        buttons[i][j].setEnabled(false);
        movesCount++;
        updateInfoLabel();

        if (checkWinner("O")) {
            botWins++;
            prefs.putInt("botWins", botWins);
            DatabaseManager.saveGameResult("singleplayer", boardSize, "Bot");
            showResult("Bot Wins!");
            return;
        }

        if (movesCount == boardSize * boardSize) {
            DatabaseManager.saveGameResult("singleplayer", boardSize, "Draw");
            showResult("Draw!");
            return;
        }

        playerTurn = true;
        statusLabel.setText("Your Turn");
    }

    private boolean checkWinner(String symbol) {
        for (int i = 0; i < boardSize; i++) {
            if (checkLine(symbol, i, 0, 0, 1) || checkLine(symbol, 0, i, 1, 0)) return true;
        }
        return checkLine(symbol, 0, 0, 1, 1) || checkLine(symbol, 0, boardSize - 1, 1, -1);
    }

    private boolean checkLine(String symbol, int startRow, int startCol, int rowInc, int colInc) {
        for (int i = 0; i < boardSize; i++) {
            if (!buttons[startRow + i * rowInc][startCol + i * colInc].getText().equals(symbol)) {
                return false;
            }
        }
        return true;
    }

    private void showResult(String message) {
        if (matchTimer != null) matchTimer.cancel();
        JOptionPane.showMessageDialog(this, message);
        showWelcomePage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe().setVisible(true));
    }
}