package xyz.niflheim.stockfish.ui.preference;

import com.github.bhlangonijr.chesslib.game.GameMode;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;
import xyz.niflheim.stockfish.ui.board.GameFrame;
import xyz.niflheim.stockfish.util.Elo;
import xyz.niflheim.stockfish.util.GameDTO;
import xyz.niflheim.stockfish.util.Preference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessModeSelectionPanel extends JPanel {
    private JPanel dynamicPanel;
    private JButton playButton;
    private JTextField nicknameField;
    private JComboBox<String> difficultyBox;
    private Image backgroundImage;
    private Preference preference;

    public ChessModeSelectionPanel(Preference preference) {
        this.preference = preference;
        // 배경 이미지 로드
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/LaunchForm.png"));
        backgroundImage = imageIcon.getImage();


        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 제목 라벨 설정
        JLabel titleLabel = new JLabel("Select Game Mode", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setOpaque(false);
        add(titleLabel, BorderLayout.NORTH); // 제목 라벨을 항상 고정된 상단에 추가

        // 메인 패널 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // 수직 중앙 배치를 위한 BoxLayout
        mainPanel.setOpaque(false); // 배경 이미지 보이도록 설정
        add(mainPanel, BorderLayout.CENTER);

        // 라벨을 250px 정도 아래로 내리기 위한 spacerPanel
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(800, 250));  // 250px 만큼 공간 확보
        spacerPanel.setOpaque(false);
        mainPanel.add(spacerPanel);

        // 모드 선택 라벨들
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        labelPanel.setOpaque(false);

        JLabel oneToOneLabel = createModeLabel("1VS1 Mode", Color.BLUE);
        oneToOneLabel.setPreferredSize(new Dimension(250, 80)); // 크기 절반으로 설정
        oneToOneLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showOneToOnePanel();
            }
        });

        JLabel engineModeLabel = createModeLabel("PVP vs 1 Mode", Color.RED);
        engineModeLabel.setPreferredSize(new Dimension(250, 80)); // 크기 절반으로 설정
        engineModeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showEngineModePanel();
            }
        });

        labelPanel.add(oneToOneLabel);
        labelPanel.add(engineModeLabel);

        mainPanel.add(labelPanel); // 라벨을 중앙에 추가

        // 모드별 입력 필드가 나타날 동적 패널 설정
        dynamicPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        dynamicPanel.setOpaque(false);
        mainPanel.add(dynamicPanel);

        // 플레이 버튼 설정
        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 30));
        playButton.setEnabled(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    handlePlayAction();
                } catch (StockfishInitException ex) {

                }
            }
        });
        add(playButton, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private JLabel createModeLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(color);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setOpaque(true);
        label.setBackground(Color.LIGHT_GRAY);
        return label;
    }

    private void showOneToOnePanel() {
        dynamicPanel.removeAll();

        // 엔진 모드 정보 초기화
        difficultyBox = null;

        JLabel nicknameLabel = new JLabel("Enter Opponent's Nickname:");
        nicknameField = new JTextField(10);
        nicknameField.addCaretListener(e -> updatePlayButtonState());


        dynamicPanel.add(nicknameLabel);
        dynamicPanel.add(nicknameField);

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    private void showEngineModePanel() {
        dynamicPanel.removeAll();

        // 1대1 모드 정보 초기화
        nicknameField = null;

        JLabel difficultyLabel = new JLabel("Select Difficulty Level:");
        String[] difficulties = {"Beginner", "Normal", "Hard"};
        difficultyBox = new JComboBox<>(difficulties);
        difficultyBox.addActionListener(e -> updatePlayButtonState());

        dynamicPanel.add(difficultyLabel);
        dynamicPanel.add(difficultyBox);

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
        String selectedItem = (String) difficultyBox.getSelectedItem();
        if(selectedItem.equals(difficulties[0])) {
            preference.setElo(Elo.BEGINNER);
        }else if(selectedItem.equals(difficulties[1])) {
            preference.setElo(Elo.INTERMEDIATE);
        }else {
            preference.setElo(Elo.ADVANCED);
        }
    }

    private void updatePlayButtonState() {
        boolean isOneToOneSelected = nicknameField != null && !nicknameField.getText().trim().isEmpty();
        boolean isEngineModeSelected = difficultyBox != null && difficultyBox.getSelectedIndex() != -1;
        playButton.setEnabled(isOneToOneSelected || isEngineModeSelected);
    }

    private void handlePlayAction() throws StockfishInitException {
        if (nicknameField != null && !nicknameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Starting 1VS1 game with opponent: " + nicknameField.getText());
            preference.setGameMode(GameMode.HUMAN_VS_HUMAN);
            preference.setOpponent(nicknameField.getText());
        } else if (difficultyBox != null && difficultyBox.getSelectedIndex() != -1) {
            JOptionPane.showMessageDialog(this, "Starting game with engine at difficulty: " + difficultyBox.getSelectedItem());
            preference.setGameMode(GameMode.HUMAN_VS_MACHINE);
        }
        GameFrame gameFrame = new GameFrame(new GameDTO(preference));
        gameFrame.setVisible(true);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Chess Game Mode Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new ChessModeSelectionPanel(new Preference("username")));
        frame.setResizable(false); // 창 크기 변경 불가
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}
