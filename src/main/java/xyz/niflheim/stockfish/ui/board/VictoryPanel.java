package xyz.niflheim.stockfish.ui.board;
import javax.swing.*;
import java.awt.*;

public class VictoryPanel extends JPanel {
    private JLabel victoryLabel;
    private JLabel timeLabel;
    private JLabel playerNameLabel;
    private Image backgroundImage;

    public VictoryPanel() {
        backgroundImage = new ImageIcon(getClass().getResource("/image/vb.png")).getImage();

        setupPanel();
        addVictoryLabel();
        addInfoPanel();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
    private void setupPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600)); // Set default size
        setMaximumSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800, 600));
    }

    private void addVictoryLabel() {
        victoryLabel = new JLabel("게임 결과", SwingConstants.CENTER);
        victoryLabel.setFont(new Font("맑은 고딕", Font.BOLD, 50));
        victoryLabel.setForeground(Color.GREEN);
        add(victoryLabel, BorderLayout.NORTH);
    }

    private void addInfoPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        timeLabel = new JLabel("Time: ", SwingConstants.CENTER);
        timeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 25));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        centerPanel.add(timeLabel, gbc);

        playerNameLabel = new JLabel("Winner: ", SwingConstants.CENTER);
        playerNameLabel.setForeground(Color.BLUE);
        playerNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 25));
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        centerPanel.add(playerNameLabel, gbc);

        JButton closeButton = new JButton("닫기");
        closeButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        closeButton.setPreferredSize(new Dimension(200, 60));
        closeButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 2;
        gbc.insets = new Insets(250, 0, 50, 0);
        centerPanel.add(closeButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void setTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        timeLabel.setText(String.format("게임 플레이 타임: %02d:%02d", minutes, seconds));
    }

    public void setWinnerName(String playerName) {
        playerNameLabel.setText("승리자 !  " + playerName);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Result Screend");
        frame.setResizable(false);
        VictoryPanel victoryPanel = new VictoryPanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(victoryPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VictoryPanel::createAndShowGUI);
    }
}
