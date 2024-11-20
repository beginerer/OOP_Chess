package xyz.niflheim.stockfish.ui.board;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.game.GameMode;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import xyz.niflheim.stockfish.engine.StockfishClient;
import xyz.niflheim.stockfish.engine.enums.Query;
import xyz.niflheim.stockfish.engine.enums.QueryType;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;
import xyz.niflheim.stockfish.ui.PieceDragAndDropListener;
import xyz.niflheim.stockfish.ui.SquarePanel;
import xyz.niflheim.stockfish.util.Elo;
import xyz.niflheim.stockfish.util.GameDTO;
import xyz.niflheim.stockfish.util.Preference;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BoardPanel extends JPanel implements BoardEventListener {

    public static final int SQUARE_DIMENSION = 75;
    private final MoveHistoryPanel moveHistoryPanel;
    private final StockfishClient stockfishClient;
    private final Board board;
    private final MoveList moveHistory;
    public boolean boardReversed;
    private boolean isUserTurn; // 사용자 턴인지 확인하는 변수
    private boolean isPVP;
    private boolean isReplayMode;
    private Square lastMove;
    private JPanel boardPanel;
    private JPanel[][] squarePanels;
    private JLayeredPane boardLayeredPane;

    private JPanel capturedPiecesPanel;
    private JTextArea BLACKcapturedPiecesTextArea;
    private JTextArea WHITEcapturedPiecesTextArea;


    private JPanel promotionPanel;  // 프로모션 패널
    private Square promotionSquare;
    private boolean isWhitePromotion;
    Square[] values;
    int index =0;


    public BoardPanel(GameDTO gameDTO) {
        super(new BorderLayout());
        stockfishClient = gameDTO.getStockfishClient();
        moveHistoryPanel = new MoveHistoryPanel(gameDTO);
        board = gameDTO.getBoard();
        moveHistory = gameDTO.getMoveHistory();
        isUserTurn = gameDTO.getGameMode() == GameMode.MACHINE_VS_HUMAN;
        isPVP = gameDTO.getGameMode() == GameMode.MACHINE_VS_HUMAN || gameDTO.getGameMode() == GameMode.HUMAN_VS_MACHINE;
        lastMove =Square.NONE;
        isReplayMode = gameDTO.isReplayMode();
        //board 리스너 추가
        board.addEventListener(BoardEventType.ON_MOVE,this);
        board.addEventListener(BoardEventType.ON_LOAD,this);
        board.addEventListener(BoardEventType.ON_UNDO_MOVE,this);

        this.boardReversed = gameDTO.isBoardReserved();
        values = Square.values();

        engineMove(isPVP,isUserTurn);
        initializeBoardLayeredPanel();
        initializeSquares();
        initializePieces();
    }

    private void engineMove(boolean isPVP, boolean isUserTurn) {
        if(isPVP && !isUserTurn) {
            requestEngineMove();
        }
    }

    // 죽은 말 표시 코드 - 영역 설정
    JPanel initializeCapturedPiecesPanel() {
        capturedPiecesPanel = new JPanel();
        capturedPiecesPanel.setLayout(new BorderLayout());
        capturedPiecesPanel.setPreferredSize(new Dimension(8 * SQUARE_DIMENSION, 100)); // 크기 확인
        capturedPiecesPanel.setBackground(Color.LIGHT_GRAY);

        // 경계선 스타일 설정
        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

        // 상단 패널 (1사분면, 2사분면)
        JPanel topPanel = new JPanel(new GridLayout(1, 2)); // 1행 2열의 GridLayout 설정
        topPanel.setPreferredSize(new Dimension(8 * SQUARE_DIMENSION, 20)); // 높이 비율 1
        topPanel.setBorder(border);

        JLabel blackLabel = new JLabel("BLACK", JLabel.CENTER);
        JLabel whiteLabel = new JLabel("WHITE", JLabel.CENTER);

        topPanel.add(blackLabel);
        topPanel.add(whiteLabel);

        // 하단 패널 (3사분면, 4사분면)
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2)); // 1행 2열의 GridLayout 설정
        bottomPanel.setPreferredSize(new Dimension(8 * SQUARE_DIMENSION, 80)); // 높이 비율 4
        bottomPanel.setBorder(border);

        BLACKcapturedPiecesTextArea = new JTextArea();
        BLACKcapturedPiecesTextArea.setEditable(false);
        BLACKcapturedPiecesTextArea.setLineWrap(true);

        WHITEcapturedPiecesTextArea = new JTextArea();
        WHITEcapturedPiecesTextArea.setEditable(false);
        WHITEcapturedPiecesTextArea.setLineWrap(true);

        // 각 텍스트 영역에 경계선 추가
        BLACKcapturedPiecesTextArea.setBorder(border);
        WHITEcapturedPiecesTextArea.setBorder(border);

        bottomPanel.add(BLACKcapturedPiecesTextArea);
        bottomPanel.add(WHITEcapturedPiecesTextArea);

        // 상단과 하단 패널을 capturedPiecesPanel에 추가
        capturedPiecesPanel.add(topPanel, BorderLayout.NORTH);
        capturedPiecesPanel.add(bottomPanel, BorderLayout.CENTER);

        return capturedPiecesPanel;
    }

    //죽은 말 표시 코드
    private void updateCapturedPiecesPanel(Piece capturedPiece) {

        if(capturedPiece.getPieceSide()==Side.BLACK){
            // 텍스트 영역에 캡처된 피스를 추가
            BLACKcapturedPiecesTextArea.append(capturedPiece.getFanSymbol() + " ");
            BLACKcapturedPiecesTextArea.setFont(BLACKcapturedPiecesTextArea.getFont().deriveFont(50f));
            BLACKcapturedPiecesTextArea.revalidate();
        }
        else{
            WHITEcapturedPiecesTextArea.append(capturedPiece.getFanSymbol() + " ");
            WHITEcapturedPiecesTextArea.setFont(WHITEcapturedPiecesTextArea.getFont().deriveFont(50f));
            WHITEcapturedPiecesTextArea.revalidate();
        }

    }


    public void processUserMove(Move move) {

        // 죽은 말 표시 코드
        Piece capturedPiece = board.getPiece(move.getTo()); // 이동 위치의 피스를 가져옴
        // 피스를 캡처한 경우 텍스트 영역에 추가
        if (capturedPiece != Piece.NONE) {
            updateCapturedPiecesPanel(capturedPiece);
        }


        Move userMove = move;
        removeLastMoveHighLight(lastMove);
        makeLastMoveHighlight(move.getTo());
        lastMove = move.getTo();
        board.doMove(userMove, true);
        if(isPVP && isUserTurn) {
                isUserTurn = false; // 사용자 턴 종료
                requestEngineMove(); // 엔진의 수 요청
        }
    }
    private void requestEngineMove() {
        String fen = board.getFen();
        Query query = new Query(QueryType.Best_Move, fen, 10, 10, 30000);
        stockfishClient.submit(query, this::processEngineMove);

    }

    private void processEngineMove(String bestMove) {

        System.out.println("Engine move: " + bestMove);
        String from = bestMove.substring(0, 2).toUpperCase();
        String to = bestMove.substring(2, 4).toUpperCase();
        Move move = new Move(Square.valueOf(from), Square.valueOf(to));
        // 죽은 말 표시 코드
        Piece capturedPiece = board.getPiece(move.getTo()); // 이동 위치의 피스를 가져옴
        // 피스를 캡처한 경우 텍스트 영역에 추가
        if (capturedPiece != Piece.NONE) {
            updateCapturedPiecesPanel(capturedPiece);
        }
        makeLastMoveHighlight(move.getTo());
        try {
            Thread.sleep(1000);
            boolean isMoveValid = board.doMove(move, true);
            Thread.sleep(500);
            if (isMoveValid) {
                isUserTurn = true; // 엔진 턴 종료, 사용자 턴으로 전환
                removeLastMoveHighLight(lastMove);
                makeLastMoveHighlight(move.getTo());
                lastMove = move.getTo();
            } else {
                throw new RuntimeException("Invalid move from Stockfish.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void initializeSquares() {
        squarePanels = new JPanel[8][8];
        if(boardReversed) {
            for(int row =0; row <8; row++) {
                for(int col = 7; col >=0; col--) {
                    initializeSingleSquaresPanel(row,col);
                }
            }
        }else { // 정방향
            for (int row = 7; row >= 0; row--) {
                for (int col = 0; col < 8; col++) {
                    initializeSingleSquaresPanel(row,col);
                }
            }
        }
    }
    public void reverseBoard() {
        boardReversed = !boardReversed;
        boardPanel.removeAll(); // 기존 보드 패널의 모든 컴포넌트 제거
        initializeSquares();
        initializePieces();
        boardPanel.revalidate(); // 패널 갱신
        boardPanel.repaint(); // 패널 다시 그리기
    }
    private void initializeSingleSquaresPanel(int row,int col) {
        JLayeredPane squareLayeredPane = new JLayeredPane();
        squareLayeredPane.setPreferredSize(new Dimension(SQUARE_DIMENSION,SQUARE_DIMENSION));

        //바닥 패널 설정
        JPanel jPanel = new JPanel(new GridLayout(1, 1));
        squarePanels[row][col] = new SquarePanel(new GridLayout(1,1));
        squarePanels[row][col].setPreferredSize(new Dimension(SQUARE_DIMENSION, SQUARE_DIMENSION));
        squarePanels[row][col].setBackground(row%2==col%2 ? Color.decode("#7D945D") : Color.decode("#EEEED5")); // 초록색 ,베이지색
        squarePanels[row][col].setName(Square.squareAt(index).toString());
        boardPanel.add(squarePanels[row][col]);
    }
    private void initializeBoardLayeredPanel() {
        boardPanel = new JPanel(new GridLayout(8,8));
        boardPanel.setBounds(0,0,8*SQUARE_DIMENSION,8*SQUARE_DIMENSION);

        boardLayeredPane = new JLayeredPane();
        boardLayeredPane.setPreferredSize(new Dimension(8*SQUARE_DIMENSION,8*SQUARE_DIMENSION));
        boardLayeredPane.add(boardPanel,JLayeredPane.DEFAULT_LAYER);

        PieceDragAndDropListener pieceDragAndDropListener = new PieceDragAndDropListener(this);
        boardLayeredPane.addMouseListener(pieceDragAndDropListener);
        boardLayeredPane.addMouseMotionListener(pieceDragAndDropListener);
        boardLayeredPane.setVisible(true);

        this.add(boardLayeredPane,BorderLayout.CENTER);
    }
    private void initializePieces() {

        for (Square square : Square.values()) {

            if(board.getPiece(square)!=Piece.NONE) {
                char file = square.getFile().getNotation().charAt(0);
                int rank = Integer.parseInt(square.getRank().getNotation());

                JPanel squarePanel = getSquarePanel(file,rank);
                squarePanel.add(getPieceImageLabel(board.getPiece(square).toString()));
                squarePanel.repaint();
            }
        }
    }

    public void showPromotionPanel(Square promotionSquare, boolean isWhite) {
        this.promotionSquare = promotionSquare;
        this.isWhitePromotion = isWhite;

        promotionPanel = new JPanel();
        promotionPanel.setLayout(new GridLayout(1, 4));
        promotionPanel.setBounds(200, 200, 300, 100); // 원하는 위치와 크기로 설정

        String[] promotionOptions = {"Queen", "Rook", "Bishop", "Knight"};
        for (String option : promotionOptions) {
            JButton button = new JButton(option);
            button.addActionListener(e -> promotePawn(option));
            promotionPanel.add(button);
        }

        boardLayeredPane.add(promotionPanel, JLayeredPane.POPUP_LAYER); // 가장 위에 패널 추가
        promotionPanel.setVisible(true);
    }

    private void promotePawn(String pieceType) {
        Piece promotedPiece;
        switch (pieceType) {
            case "Rook":
                promotedPiece = isWhitePromotion ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
                break;
            case "Bishop":
                promotedPiece = isWhitePromotion ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
                break;
            case "Knight":
                promotedPiece = isWhitePromotion ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
                break;
            default:
                promotedPiece = isWhitePromotion ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
        }

        // 프로모션 적용
        board.doMove(new Move(promotionSquare, promotionSquare, promotedPiece), true);
        boardLayeredPane.remove(promotionPanel);  // 프로모션 패널 제거
        boardLayeredPane.repaint();  // 리프레시
    }

    public JPanel getSquarePanel(char file,int rank) {
        if(file <'A' || file > 'H' || rank <1 || rank >8) {
            return null;
        }else {
            return squarePanels[rank-1][file-'A'];
        }
    }
    public void executeMove(Move move) {
        moveHistory.add(move);
        moveHistoryPanel.updateLabel();
        loadingBoard(board);
        //loadingBoard(board,move.getFrom(),move.getTo());
        System.out.println(board.toString());
        System.out.println(board.getFen());
    }
    public void replayMove(Move move) {
        moveHistoryPanel.updateLabel();
        loadingBoard(board);
        System.out.println(board.toString());
        System.out.println(board.getFen());
    }
    public void executeUndo(MoveBackup undoMove) {
        // 무르기 기능 구현 x
    }
    private void addComponentToPanel(JPanel panel,Component component) {
        panel.removeAll();
        panel.add(component);
        panel.revalidate();
        panel.repaint();
    }
    private void makeLastMoveHighlight(Square square) {
        char file = getFileFromSquare(square);
        int rank = getRankFromSquare(square);
        SquarePanel squarePanel = (SquarePanel)getSquarePanel(file, rank);
        squarePanel.lastMoveHighlight = true;
        squarePanel.repaint();
    }
    private void removeLastMoveHighLight(Square square) {
        if(square!=Square.NONE) {
            char file = getFileFromSquare(square);
            int rank = getRankFromSquare(square);
            SquarePanel squarePanel = (SquarePanel) getSquarePanel(file,rank);
            squarePanel.lastMoveHighlight = false;
            squarePanel.repaint();
        }

    }
    public void loadingBoard(Board board) {
        for(Square square :Square.values()) {
            if(square!=Square.NONE) {
                Piece boardPiece = board.getPiece(square);
                char file = getFileFromSquare(square);
                int rank = getRankFromSquare(square);
                updateSinglePanel(boardPiece,file,rank);
            }
        }
    }
    private void updateSinglePanel(Piece boardPiece,char file,int rank) {
        SquarePanel squarePanel = (SquarePanel)getSquarePanel(file, rank);
        if(boardPiece!=Piece.NONE) {
            addComponentToPanel(squarePanel,getPieceImageLabel(boardPiece.toString()));
        }else {
            squarePanel.removeAll();
            squarePanel.repaint();
        }
    }
    private char getFileFromSquare(Square square) {
        return square.getFile().getNotation().charAt(0);
    }
    private int getRankFromSquare(Square square) {
        return  Integer.parseInt(square.getRank().getNotation());
    }
    // 임시 메서드 앙파상,케슬링 처리 + 코드최적화 필요
    public void loadingBoard(Board board,Square from,Square to) {
        char fromFile = from.getFile().getNotation().charAt(0);
        int fromRank = Integer.parseInt(from.getRank().getNotation());
        Piece fromPiece = board.getPiece(from);
        SquarePanel squarePanel = (SquarePanel)getSquarePanel(fromFile, fromRank);
        if(fromPiece!=Piece.NONE)
            addComponentToPanel(squarePanel,getPieceImageLabel(fromPiece.toString()));
        else {
            squarePanel.removeAll();
            squarePanel.repaint();
        }

        char toFile = to.getFile().getNotation().charAt(0);
        int toRank = Integer.parseInt(to.getRank().getNotation());
        Piece toPiece = board.getPiece(to);
        SquarePanel squarePanel1 =(SquarePanel) getSquarePanel(toFile,toRank);
        if(toPiece!=Piece.NONE)
            addComponentToPanel(squarePanel1,getPieceImageLabel(toPiece.toString()));
        else {
            squarePanel.removeAll();
            squarePanel.repaint();
        }
    }

    private JLabel getPieceImageLabel(String piece) {
        String imagePath = "/pieces/";
        imagePath += piece.toLowerCase() + ".png";
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = icon.getImage().getScaledInstance(SQUARE_DIMENSION, SQUARE_DIMENSION, Image.SCALE_SMOOTH);
        JLabel pieceImageLable = new JLabel(new ImageIcon(scaledImage));
        return pieceImageLable;
    }

    public boolean isBoardReversed() {
        return boardReversed;
    }

    public Board getBoard() {
        return board;
    }

    public MoveHistoryPanel getMoveHistoryPanel() {
        return moveHistoryPanel;
    }

    public boolean isWhiteTurn() {
        return board.getSideToMove() == Side.WHITE;
    }

    @Override
    public void onEvent(BoardEvent event) {
        if(event.getType()== BoardEventType.ON_MOVE) {
            Move move = (Move) event;
            executeMove(move);
        }else if(event.getType()==BoardEventType.ON_UNDO_MOVE) {
            MoveBackup undoMove = (MoveBackup) event;
            executeUndo(undoMove);
        }else if(event.getType()==BoardEventType.ON_LOAD) {
            Board board = (Board) event;
            loadingBoard(board);
        }
    }
    public static void main(String[] args) throws StockfishInitException {
        // Swing UI를 만들기 위한 메인 프레임 생성
        JFrame frame = new JFrame("Chess Game");
        Preference preference = new Preference(GameMode.MACHINE_VS_HUMAN,"kyonggi");
        preference.setElo(Elo.BEGINNER);
        GameDTO gameDTO = new GameDTO(preference);

        // 종료 버튼 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 체스 보드 패널 생성 (정방향 설정)
        BoardPanel boardPanel = new BoardPanel(gameDTO);

        // 패널을 프레임에 추가
        frame.add(boardPanel);

        // 창 크기 및 위치 설정
        frame.setSize(8 * BoardPanel.SQUARE_DIMENSION, 8 * BoardPanel.SQUARE_DIMENSION);
        frame.setLocationRelativeTo(null); // 창을 화면 중앙에 배치
        frame.pack();

        // 프레임을 표시
        frame.setVisible(true);
    }
}
