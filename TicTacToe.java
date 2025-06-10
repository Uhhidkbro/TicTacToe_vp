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
    private FloatControl volumeControl;
    private float currentVolume = 0.0f;
    private JLabel timerValLabel;
    private JLabel movesValLabel;
    private JLabel humanValLabel;
    private JLabel botValLabel;

    // keep a reference to the big yellow status text
    private JLabel bigStatusLabel;
    
    /** makes small white rounded stat box */
    private JLabel makeStatBox(String txt) {
        JLabel box = new JLabel(txt, SwingConstants.CENTER);
        box.setOpaque(true);
        box.setBackground(Color.WHITE);
        box.setForeground(Color.BLACK);
        box.setPreferredSize(new Dimension(90, 55));  // 💥 wider & taller for 3-digit numbers
        box.setFont(new Font("Arial", Font.BOLD, 26)); // 🆙 bigger font for visibility
        box.setHorizontalAlignment(SwingConstants.CENTER);
        box.setVerticalAlignment(SwingConstants.CENTER);
        return box;
    }

    /** fake icon using emoji – swap for real icons if you have images */
    private JLabel makeIconLabel(String emoji) {
        JLabel label = new JLabel(emoji, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48)); // Use emoji-friendly font
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /** big rounded bottom buttons */
    private JButton makeWideButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(190, 55));
        b.setFont(new Font("Arial Black", Font.BOLD, 18));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        return b;
    }

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
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(58, 0, 110));
        playMusic("C:/Users/zafri/Downloads/background music - vp.wav");
        showWelcomePage();
    }

    private void showWelcomePage() {
        if (matchTimer != null) matchTimer.cancel();

        getContentPane().removeAll();
        getContentPane().setBackground(new Color(58, 0, 110)); // Purple
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("WELCOME TO TICTACTOE");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 36));
        titleLabel.setForeground(Color.YELLOW);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // PLAY button
        JButton playButton = new JButton("PLAY");
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setBackground(new Color(0, 200, 100));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setPreferredSize(new Dimension(180, 40));
        playButton.addActionListener(e -> startGame());
        gbc.gridy = 1;
        add(playButton, gbc);

        // SETTINGS button
        JButton settingsButton = new JButton("SETTINGS");
        settingsButton.setFont(new Font("Arial", Font.BOLD, 18));
        settingsButton.setBackground(Color.WHITE);
        settingsButton.setForeground(new Color(50, 0, 100));
        settingsButton.setFocusPainted(false);
        settingsButton.setPreferredSize(new Dimension(180, 40));
        settingsButton.addActionListener(e -> showSettingsPage());
        gbc.gridy = 2;
        add(settingsButton, gbc);

        revalidate();
        repaint();
    }

    private void startGame() {
    	multiplayer = prefs.getBoolean("multiplayer", false); // 🔥 pull saved mode
    	playerTurn = true;

        if (matchTimer != null) matchTimer.cancel();

        getContentPane().removeAll();
        getContentPane().setBackground(new Color(58, 0, 110));
        setLayout(new BorderLayout(20, 0));

        // ── LEFT SIDEBAR ────────────────────────────────────────────
        JPanel side = new JPanel();
        side.setBackground(new Color(58, 0, 110));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        timerValLabel = makeStatBox("60");
        movesValLabel = makeStatBox("0");
        humanValLabel = makeStatBox(String.valueOf(humanWins));
        botValLabel   = makeStatBox(String.valueOf(botWins));

        if (showTimer) {
            side.add(makeIconLabel("⏱"));
            side.add(timerValLabel);
            side.add(Box.createVerticalStrut(25));
        }
        if (showBoardInfo) {
            side.add(makeIconLabel("📦"));
            side.add(movesValLabel);
            side.add(Box.createVerticalStrut(25));
        }
        if (showWinCounter) {
            side.add(makeIconLabel("👤"));
            side.add(humanValLabel);
            side.add(Box.createVerticalStrut(25));
            side.add(makeIconLabel("🤖"));
            side.add(botValLabel);
        }

        add(side, BorderLayout.WEST);

        // ── BIG STATUS LABEL ON TOP ────────────────────────────────
        bigStatusLabel = new JLabel(multiplayer ? "TURN PLAYER 1" : "YOUR TURN!", SwingConstants.CENTER);
        bigStatusLabel.setFont(new Font("Arial Black", Font.BOLD, 36));
        bigStatusLabel.setForeground(new Color(255, 222, 70));
        add(bigStatusLabel, BorderLayout.NORTH);

        // ── GAME BOARD (CENTER) ────────────────────────────────────
        JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize, 6, 6));
        boardPanel.setBackground(new Color(0,0,0,0));   // transparent so purple shows
        buttons = new JButton[boardSize][boardSize];
        movesCount = 0;
        playerTurn = true;

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                JButton b = new JButton("");
                b.setFont(new Font("Arial Black", Font.BOLD, 48));
                b.setBackground(new Color(45, 0, 100));
                b.setForeground(Color.WHITE);
                b.setFocusable(false);
                b.addActionListener(this);
                buttons[r][c] = b;
                boardPanel.add(b);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // ── BOTTOM BUTTONS ─────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        bottom.setBackground(new Color(58, 0, 110));

        JButton homeBtn = makeWideButton("HOME", new Color(90,200,255), Color.WHITE);
        JButton againBtn= makeWideButton("PLAY AGAIN", new Color(0,200,100), Color.WHITE);

        homeBtn.addActionListener(e -> showWelcomePage());
        againBtn.addActionListener(e -> startGame());

        bottom.add(homeBtn);
        bottom.add(againBtn);
        add(bottom, BorderLayout.SOUTH);

        // ── TIMER UPDATE ───────────────────────────────────────────
        if (showTimer) {
            startTime = System.currentTimeMillis();
            matchTimer = new Timer();
            matchTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() { SwingUtilities.invokeLater(() -> updateInfoLabel()); }
            }, 0, 1000);
        }

        revalidate();
        repaint();
    }

    private void updateInfoLabel() {
        if (showTimer) {
            long secs = (System.currentTimeMillis() - startTime) / 1000;
            timerValLabel.setText(String.valueOf(secs));
        }
        movesValLabel.setText(String.valueOf(movesCount));
        humanValLabel.setText(String.valueOf(humanWins));
        botValLabel.setText(String.valueOf(botWins));
    }

 // NOTE: Only the showSettingsPage() method is replaced with the new styled layout

    private void showSettingsPage() {
        getContentPane().removeAll();
        getContentPane().setBackground(new Color(58, 0, 110));
        setLayout(new BorderLayout());

        JLabel title = new JLabel("SETTINGS", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 36));
        title.setForeground(Color.YELLOW);
        add(title, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        settingsPanel.setBackground(new Color(58, 0, 110));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Left Panel: General
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        generalPanel.setBackground(new Color(40, 0, 80));
        generalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        generalPanel.add(labelWithSpacing("Gamemode"));
        String[] modes = {"Singleplayer", "Multiplayer"};
        JComboBox<String> modeBox = new JComboBox<>(modes);
        modeBox.setSelectedIndex(multiplayer ? 1 : 0);
        generalPanel.add(modeBox);

        generalPanel.add(labelWithSpacing("Board"));
        JComboBox<String> boardBox = new JComboBox<>(new String[]{"3x3 (default)", "4x4", "5x5", "6x6"});
        switch (boardSize) {
        case 4 -> boardBox.setSelectedIndex(1);
        case 5 -> boardBox.setSelectedIndex(2);
        case 6 -> boardBox.setSelectedIndex(3);
        default -> boardBox.setSelectedIndex(0); // 3x3 default
        }
        generalPanel.add(boardBox);

        generalPanel.add(labelWithSpacing("Volume"));
        JPanel volumeRow = new JPanel(new FlowLayout());
        volumeRow.setBackground(generalPanel.getBackground());
        JButton minus = new JButton("-");
        JLabel volumeLabel = new JLabel(getVolumePercentage() + "%");
        volumeLabel.setForeground(Color.WHITE); // Set text color to white
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Optional: bold and readable
        JButton plus = new JButton("+");
        plus.addActionListener(e -> {
        	if (currentVolume < 0.0f) {
        	setVolume(Math.min(0.0f, currentVolume + 5.0f));
        	volumeLabel.setText(getVolumePercentage() + "%");
        	}
        	});
        volumeRow.add(minus);
        minus.addActionListener(e -> {
        	if (currentVolume > -80.0f) {
        	setVolume(Math.max(-80.0f, currentVolume - 5.0f));
        	volumeLabel.setText(getVolumePercentage() + "%");
        	}
        	});
        volumeRow.add(volumeLabel);
        volumeRow.add(plus);
        generalPanel.add(volumeRow);

        // Right Panel: Match Info
        JPanel matchPanel = new JPanel();
        matchPanel.setLayout(new BoxLayout(matchPanel, BoxLayout.Y_AXIS));
        matchPanel.setBackground(new Color(40, 0, 80));
        matchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JCheckBox timerCheck = new JCheckBox("Match Timer", showTimer);
        JCheckBox boardInfoCheck = new JCheckBox("Board Info", showBoardInfo);
        JCheckBox counterCheck = new JCheckBox("Player Counter", showWinCounter);

        for (JCheckBox cb : new JCheckBox[]{timerCheck, boardInfoCheck, counterCheck}) {
            cb.setForeground(Color.WHITE);
            cb.setOpaque(false);
            cb.setFont(new Font("Arial", Font.PLAIN, 14));
            matchPanel.add(Box.createVerticalStrut(10));
            matchPanel.add(cb);
        }

        settingsPanel.add(generalPanel);
        settingsPanel.add(matchPanel);
        add(settingsPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(58, 0, 110));
        JButton home = new JButton("HOME");
        JButton save = new JButton("SAVE");
        JButton reset = new JButton("RESET TO DEFAULT");

        home.setBackground(new Color(90, 200, 255));
        home.setForeground(Color.WHITE);
        save.setBackground(Color.WHITE);
        save.setForeground(new Color(40, 0, 80));
        reset.setBackground(new Color(255, 100, 50));
        reset.setForeground(Color.WHITE);

        bottomPanel.add(home);
        bottomPanel.add(save);
        bottomPanel.add(reset);
        add(bottomPanel, BorderLayout.SOUTH);

        home.addActionListener(e -> showWelcomePage());

        reset.addActionListener(e -> {
            modeBox.setSelectedIndex(0);
            boardBox.setSelectedIndex(0);
            timerCheck.setSelected(true);
            boardInfoCheck.setSelected(true);
            counterCheck.setSelected(true);
        });

        save.addActionListener(e -> {
        	multiplayer = modeBox.getSelectedIndex() == 1;
        	showTimer = timerCheck.isSelected();
        	showBoardInfo = boardInfoCheck.isSelected();
        	showWinCounter = counterCheck.isSelected();
        	boardSize = switch (boardBox.getSelectedIndex()) {
        	    case 1 -> 4;
        	    case 2 -> 5;
        	    case 3 -> 6;
        	    default -> 3;
        	};

        	prefs.putBoolean("multiplayer", multiplayer);
        	prefs.putInt("boardSize", boardSize);
        	prefs.putBoolean("showTimer", showTimer);
        	prefs.putBoolean("showBoardInfo", showBoardInfo);
        	prefs.putBoolean("showWinCounter", showWinCounter);

        	showWelcomePage();
        	});

        revalidate();
        repaint();
    }

    private JLabel labelWithSpacing(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        return label;
    }

    private void playMusic(String filepath) {
    	try {
    	if (backgroundMusic != null && backgroundMusic.isRunning()) {
    	return;
    	}

    	    AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filepath));
    	    backgroundMusic = AudioSystem.getClip();
    	    backgroundMusic.open(audioStream);

    	    volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
    	    setVolume(currentVolume); // Set default

    	    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    	} catch (Exception e) {
    	    System.out.println("Music error: " + e.getMessage());
    	}
    	}

    	private void setVolume(float volumeDB) {
    	if (volumeControl != null) {
    	volumeControl.setValue(volumeDB);
    	currentVolume = volumeDB;
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
        bigStatusLabel.setText(multiplayer ? (playerTurn ? "Player 1's Turn" : "Player 2's Turn") : (playerTurn ? "Your Turn" : "Bot Turn"));

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

        bigStatusLabel.setText(message.toUpperCase());
        for (JButton[] row : buttons)
            for (JButton b : row) b.setEnabled(false);
    }
    
    private int getVolumePercentage() {
    	return Math.max(0, Math.min(100, (int) ((currentVolume + 80.0f) / 80.0f * 100)));
    	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe().setVisible(true));
    }
}
