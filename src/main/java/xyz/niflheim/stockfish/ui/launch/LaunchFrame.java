package xyz.niflheim.stockfish.ui.launch;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameMode;
import com.github.bhlangonijr.chesslib.game.Player;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;
import xyz.niflheim.stockfish.ui.board.BoardPanel;
import xyz.niflheim.stockfish.ui.board.GameFrame;
import xyz.niflheim.stockfish.util.GameDTO;
import xyz.niflheim.stockfish.util.Preference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

public class LaunchFrame extends JFrame {

    private LoginPanel loginPanel;
    private JLayeredPane layeredPane;

    public LaunchFrame() {
        // JFrame 설정
        setTitle("Kyonggi_Chess_game");
        setSize(800, 600); // 고정된 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setResizable(false);

        // JLayeredPane 설정
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));
        setContentPane(layeredPane); // layeredPane을 프레임의 content pane으로 설정

        // JPanel 설정
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // 이미지 로드 (이미지 경로를 정확히 설정)
                ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/image/LaunchForm.png"));
                if (backgroundImage.getImage() != null) {
                    // 이미지 크기에 맞게 패널 크기 설정 및 그리기
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), null);
                } else {
                    throw new RuntimeException("이미지를 로드할 수 없습니다.");
                }
            }
        };

        backgroundPanel.setLayout(null); // 사용자 정의 레이아웃 설정
        backgroundPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);
        backgroundPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (loginPanel != null && loginPanel.isVisible()) {
                    loginPanel.setVisible(false);
                }
            }
        });

        // 버튼 생성 및 스타일 적용
        JButton loginButton = new JButton("Log In");
        JButton signUpButton = new JButton("Replays");

        customizeButton(loginButton);
        customizeButton(signUpButton);

        // 두 버튼의 크기와 간격 설정
        int buttonWidth = 150;
        int buttonHeight = 50;
        int gapBetweenButtons = 30;

        // 화면 중앙 기준으로 두 버튼 위치 계산
        int frameWidth = getWidth();
        int frameCenterX = frameWidth / 2;

        int totalButtonWidth = buttonWidth * 2 + gapBetweenButtons;
        int startX = frameCenterX - totalButtonWidth / 2;

        loginButton.setBounds(startX, 400, buttonWidth, buttonHeight); // 중앙 왼쪽 버튼 배치
        signUpButton.setBounds(startX + buttonWidth + gapBetweenButtons, 400, buttonWidth, buttonHeight); // 중앙 오른쪽 버튼 배치

        // 버튼 리스너
        loginButton.addActionListener(e -> showLoginPanel());
        signUpButton.addActionListener(e->replayMoveHistory());

        // 버튼 추가
        backgroundPanel.add(loginButton);
        backgroundPanel.add(signUpButton);

        setVisible(true); // 프레임을 가시화
    }

    private void replayMoveHistory()  {
        GameDTO gameDTO = null;
        try {
            PgnIterator games = new PgnIterator("Anatoly Karpov_vs_Zoltan Ribli_____.__.__.pgn");
            Iterator<Game> iterator = games.iterator();
            Game next = iterator.next();
            Player whitePlayer = next.getWhitePlayer();
            Player blackPlayer = next.getBlackPlayer();
            MoveList halfMoves = next.getHalfMoves();
            Iterator<Move> movelist = halfMoves.iterator();
            Preference preference = new Preference(GameMode.HUMAN_VS_HUMAN, whitePlayer.getName());
            preference.setOpponent(blackPlayer.getName());
            gameDTO = new GameDTO(preference);
            gameDTO.setReplayMode(true);
            GameFrame gameFrame = new GameFrame(gameDTO);
            gameFrame.setVisible(true);
            BoardPanel boardPanel = gameFrame.getBoardPanel();
            Timer timer = new Timer(1000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (movelist.hasNext()) {
                        boardPanel.processUserMove(movelist.next());
                    } else {
                        ((Timer) e.getSource()).stop();
                        JOptionPane.showMessageDialog(gameFrame, "Winner is " +whitePlayer.getName()+"!");
                    }
                }
            });

            timer.start();
        } catch (StockfishInitException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void showLoginPanel() {
        if (loginPanel == null) {
            loginPanel = new LoginPanel(this);
            loginPanel.setSize(522, 515); // 패널 크기 설정
            loginPanel.setOpaque(false); // 패널을 투명하게 설정

            // LaunchFrame 중앙에 LoginPanel 배치
            int frameWidth = getWidth();
            int frameHeight = getHeight();
            int panelWidth = loginPanel.getWidth();
            int panelHeight = loginPanel.getHeight();

            int x = (frameWidth - panelWidth) / 2;
            int y = (frameHeight - panelHeight) / 2;

            loginPanel.setBounds(x, y, panelWidth, panelHeight); // 중앙에 위치하도록 설정
            layeredPane.add(loginPanel, JLayeredPane.PALETTE_LAYER); // 기존 배경 위에 덧대기
        }
        loginPanel.setVisible(true);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    // 버튼 스타일 설정 메소드
    private void customizeButton(JButton button) {
        button.setFocusPainted(false); // 버튼 클릭 시 테두리 제거
        button.setBackground(new Color(255, 255, 255, 180)); // 반투명 흰색 배경
        button.setForeground(new Color(0, 102, 204)); // 파란색 텍스트
        button.setFont(new Font("Arial", Font.BOLD, 18)); // 굵은 폰트 설정
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2)); // 파란색 테두리 설정
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LaunchFrame::new); // 로그인 폼 실행
    }
}

