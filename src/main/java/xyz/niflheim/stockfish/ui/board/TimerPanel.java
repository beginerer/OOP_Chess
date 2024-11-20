package xyz.niflheim.stockfish.ui.board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TimerPanel extends JPanel {
    private Timer timer;
    private int seconds;
    private JLabel timeLabel;

    public TimerPanel() {
        setLayout(new BorderLayout());
        seconds = 0;

        // Time display label
        timeLabel = new JLabel(formatTime(seconds), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(timeLabel, BorderLayout.CENTER);

        // Timer action listener
        timer = new Timer(1000, (ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                seconds++;
                timeLabel.setText(formatTime(seconds));
            });
        });

        // Start the timer immediately
        timer.start();
    }

    private String formatTime(int seconds) {
        int hrs = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }

    public int getTotalSeconds() {
        return seconds;
    }
}
