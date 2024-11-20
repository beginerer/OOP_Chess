package xyz.niflheim.stockfish.util;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.GameMode;
import com.github.bhlangonijr.chesslib.move.MoveList;
import xyz.niflheim.stockfish.engine.StockfishClient;
import xyz.niflheim.stockfish.engine.enums.Option;
import xyz.niflheim.stockfish.engine.enums.Variant;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;

public class GameDTO {
    private Board board;//chessLib 라이브러리
    private MoveList moveHistory;
    private StockfishClient stockfishClient; //스탁피쉬 엔진

    private final GameMode gameMode;
    private boolean boardReserved;
    private String blackPlayer;
    private String whitePlayer;
    private boolean IsReplayMode = false;

    public GameDTO(Preference preference) throws StockfishInitException {
        gameMode = preference.getGameMode();
        createStockFishClient(preference);
        initializeBoardSetting(preference);
    }

    private void createStockFishClient(Preference preference) throws StockfishInitException {
        if(gameMode==GameMode.HUMAN_VS_MACHINE || gameMode==GameMode.MACHINE_VS_HUMAN) { // 엔진이랑 플레이 할 경우
            Elo elo = preference.getElo();
            stockfishClient = new StockfishClient.Builder().setVariant(Variant.BMI2).setInstances(1) // 초심자,중수,고수 분류에 따른 엔진 생성
                    .setOption(Option.Skill_Level,elo.getRating()).build();
        }
    }
    private void initializeBoardSetting(Preference preference) {
        board = new Board();
        moveHistory = new MoveList(board.getFen());
        if(gameMode==GameMode.HUMAN_VS_MACHINE) {
            blackPlayer = preference.getUserName(); // 사용자 아이디
            whitePlayer = "StockFishEngine";
            boardReserved = true;
        }else if(gameMode==GameMode.MACHINE_VS_HUMAN) {
            blackPlayer = "StockFishEngine";
            whitePlayer = preference.getUserName(); // 사용자 아이디
        }else {
            blackPlayer = preference.getOpponent();
            whitePlayer = preference.getUserName();
        }
    }

    public Board getBoard() {
        return board;
    }

    public StockfishClient getStockfishClient() {
        return stockfishClient;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public boolean isBoardReserved() {
        return boardReserved;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public MoveList getMoveHistory() {
        return moveHistory;
    }

    public boolean isReplayMode() {
        return IsReplayMode;
    }

    public void setReplayMode(boolean replayMode) {
        IsReplayMode = replayMode;
    }
}
