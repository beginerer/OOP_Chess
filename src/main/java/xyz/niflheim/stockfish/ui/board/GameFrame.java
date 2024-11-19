package xyz.niflheim.stockfish.ui.board;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.GameMode;
import xyz.niflheim.stockfish.engine.StockfishClient;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;
import xyz.niflheim.stockfish.util.Elo;
import xyz.niflheim.stockfish.util.GameDTO;
import xyz.niflheim.stockfish.util.Preference;
import xyz.niflheim.stockfish.ui.launch.LaunchFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    private JLayeredPane layeredPane;
    private BoardPanel boardPanel;
    private JPanel capturePanel;
    private MoveHistoryPanel moveHistoryPanel;
    private NamePanel whiteNamePanel;
    private NamePanel blackNamePanel;
    private JButton reverseBoardButton;
    private TimerPanel timerPanel;
    private final GameDTO gameDTO;
    private final StockfishClient stockfishClient;
    private final Board board;
    private SurrenderPanel surrenderPanel;
    private VictoryPanel victoryPanel;
    private JButton homeButton;

    public GameFrame(GameDTO gameDTO) {
        this.gameDTO = gameDTO;
        boardPanel = new BoardPanel(gameDTO);
        timerPanel = new TimerPanel();
        stockfishClient = gameDTO.getStockfishClient();
        moveHistoryPanel = boardPanel.getMoveHistoryPanel();
        capturePanel = boardPanel.initializeCapturedPiecesPanel();
        board = boardPanel.getBoard();
        timerPanel = timerPanel.getTimerPanel();

        whiteNamePanel = new NamePanel(gameDTO.getWhitePlayer(), gameDTO.getBlackPlayer(), true, gameDTO.isBoardReserved());
        blackNamePanel = new NamePanel(gameDTO.getWhitePlayer(), gameDTO.getBlackPlayer(), false, gameDTO.isBoardReserved());
        surrenderPanel = new SurrenderPanel(this); // GameFrame을 전달하여 항복 버튼에서 GameFrame 메서드를 호출할 수 있게 함
        victoryPanel = new VictoryPanel(); // VictoryPanel 초기화

        setBackground(Color.decode("#302E2B"));
        initFrame();
    }

    private void initFrame() {
        setTitle("OOP Chess Game");
        setSize(12 * BoardPanel.SQUARE_DIMENSION, 8 * BoardPanel.SQUARE_DIMENSION + 240); // 새 프레임 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setResizable(false);

        boardPanel.setBounds(0, 60, 8 * BoardPanel.SQUARE_DIMENSION, 8 * BoardPanel.SQUARE_DIMENSION);
        layeredPane = new JLayeredPane();
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        setContentPane(layeredPane);

        moveHistoryPanel.setBounds(8 * BoardPanel.SQUARE_DIMENSION, 0, 4 * BoardPanel.SQUARE_DIMENSION, 8 * BoardPanel.SQUARE_DIMENSION);
        layeredPane.add(moveHistoryPanel, JLayeredPane.DEFAULT_LAYER);

        blackNamePanel.setBounds(8 * BoardPanel.SQUARE_DIMENSION - 220, 0, 220, 60);
        layeredPane.add(blackNamePanel, JLayeredPane.DEFAULT_LAYER);

        whiteNamePanel.setBounds(0, 8 * BoardPanel.SQUARE_DIMENSION + 60, 220, 60);
        layeredPane.add(whiteNamePanel, JLayeredPane.DEFAULT_LAYER);

        capturePanel.setBounds(0, 8 * BoardPanel.SQUARE_DIMENSION + 120, 8 * BoardPanel.SQUARE_DIMENSION, 2 * BoardPanel.SQUARE_DIMENSION);
        layeredPane.add(capturePanel, JLayeredPane.DEFAULT_LAYER);

        timerPanel.setBounds(8*BoardPanel.SQUARE_DIMENSION,8*BoardPanel.SQUARE_DIMENSION,4*BoardPanel.SQUARE_DIMENSION,60);
        layeredPane.add(timerPanel,JLayeredPane.DEFAULT_LAYER);

        ImageIcon icon = new ImageIcon(getClass().getResource("/image/boardReserve.png"));
        Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        reverseBoardButton = new JButton(new ImageIcon(scaledImage));
        reverseBoardButton.addActionListener(e -> {
            boardPanel.reverseBoard();
            blackNamePanel.updateName(boardPanel.boardReversed);
            whiteNamePanel.updateName(boardPanel.boardReversed);
        });
        reverseBoardButton.setBounds(8 * BoardPanel.SQUARE_DIMENSION-50, 8 * BoardPanel.SQUARE_DIMENSION+65, 50, 50);
        layeredPane.add(reverseBoardButton);

        // SurrenderPanel 위치를 화면에 맞게 수정
        int xPos = 8 * BoardPanel.SQUARE_DIMENSION + 40; // 보드 오른쪽 끝
        int yPos = 8 * BoardPanel.SQUARE_DIMENSION + 80; // 캡처 패널 밑으로 적당히 여유 두기
        surrenderPanel.setBounds(xPos, yPos, 200, 100); // 적절한 위치와 크기로 설정
        layeredPane.add(surrenderPanel, JLayeredPane.DEFAULT_LAYER);

        homeButton = new JButton("Home");
        homeButton.setBounds(0, 0, 80, 50);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 초기화면으로 되돌아가기 위해 현재 프레임을 닫고 LaunchFrame을 생성
                dispose();
                SwingUtilities.invokeLater(LaunchFrame::new);
            }
        });
        layeredPane.add(homeButton);
    }

    public void showVictoryPanel() {
        // 기존의 게임 관련 패널을 숨기고 승리 화면을 표시합니다.
        layeredPane.removeAll(); // 모든 패널 제거
        victoryPanel.setBounds(0, 0, getWidth(), getHeight()); // VictoryPanel을 화면 전체에 표시
        layeredPane.add(victoryPanel, JLayeredPane.DEFAULT_LAYER); // VictoryPanel을 추가
        layeredPane.repaint(); // 화면 새로고침
        layeredPane.revalidate(); // 레이아웃 다시 계산
    }

    public static void main(String[] args) throws StockfishInitException {
        Preference preference = new Preference("UserName");
        preference.setElo(Elo.BEGINNER);
        preference.setGameMode(GameMode.HUMAN_VS_MACHINE);
        GameDTO gameDTO1 = new GameDTO(preference);
        GameFrame gameFrame = new GameFrame(gameDTO1);
        gameFrame.setVisible(true);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}
