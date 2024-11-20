package xyz.niflheim.stockfish.ui.board;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.game.GameMode;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;
import xyz.niflheim.stockfish.util.GameDTO;
import xyz.niflheim.stockfish.util.Preference;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MoveHistoryPanel extends JPanel {

    private MoveList moveHistory;
    private JLabel moveHistoryDisplay;
    private JScrollPane scrollPane;

    public MoveHistoryPanel(GameDTO gameDTO) {
        moveHistory = gameDTO.getMoveHistory();

        // JLabel 초기화
        moveHistoryDisplay = new JLabel();
        moveHistoryDisplay.setVerticalAlignment(SwingConstants.TOP); // 위쪽 정렬
        moveHistoryDisplay.setHorizontalAlignment(SwingConstants.LEFT); // 왼쪽 정렬
        moveHistoryDisplay.setOpaque(true);
        moveHistoryDisplay.setBackground(new Color(255, 255, 204)); // JLabel 배경색 설정 (확인용)

        // JScrollPane에 JLabel 추가
        scrollPane = new JScrollPane(moveHistoryDisplay);
        scrollPane.setPreferredSize(new Dimension(400, 400)); // 패널의 기본 크기 설정
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 테두리 제거
        scrollPane.getViewport().setBackground(new Color(255, 255, 204)); // JScrollPane 뷰포트 배경 설정

        // MoveHistoryPanel 설정
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        setBackground(new Color(255, 255, 204)); // 패널 배경색 설정 (테스트용)
    }

    // 기물 이동이 발생하면 외부에서 이 함수를 호출하여 JLabel을 갱신합니다.
    public void updateLabel() {
        String[] fanArray = moveHistory.toFanArray();
        int i = 0;

        // moveHistory에서 모든 기물 이동을 문자열로 가져오기
        StringBuilder movesText = new StringBuilder("<html>");
        for (String fan : fanArray) {
            movesText.append((i + 1)).append(". ").append(fan.charAt(0)).append(moveHistory.get(i)).append("<br>");
            i++;
        }
        movesText.append("</html>");

        // JLabel의 텍스트 갱신
        moveHistoryDisplay.setText(movesText.toString());

        // 스크롤을 자동으로 가장 아래로 이동
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

    // 테스트 코드
    public static void main(String[] args) throws StockfishInitException, InterruptedException {
        // 기본 세팅
        Preference preference = new Preference("UserName");
        preference.setGameMode(GameMode.HUMAN_VS_HUMAN);
        GameDTO gameDTO = new GameDTO(preference);
        BoardPanel boardPanel = new BoardPanel(gameDTO);
        Board board = boardPanel.getBoard();
        List<Move> moveList = new ArrayList<>(List.of(
                new Move(Square.D2, Square.D4), new Move(Square.G8, Square.F6),
                new Move(Square.C2, Square.C4), new Move(Square.E7, Square.E6),
                new Move(Square.G1, Square.G3)
        ));

        // 테스트를 위한 임시 프레임 생성
        JFrame frame = new JFrame("Test Frame");
        MoveHistoryPanel moveHistoryPanel = new MoveHistoryPanel(gameDTO); // moveHistory패널 생성
        frame.add(moveHistoryPanel);
        frame.pack(); // 프레임 크기를 패널에 맞게 자동 조정
        frame.setLocationRelativeTo(null); // 창을 화면 중앙에 배치
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // 기물 이동 테스트
        for (Move move : moveList) {
            Thread.sleep(1000);
            boolean isMoveValid = board.doMove(move, true);
            moveHistoryPanel.updateLabel();
            if (!isMoveValid) {
                throw new RuntimeException("기물 이동 오류");
            }
        }
    }
}
