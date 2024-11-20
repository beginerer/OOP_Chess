package xyz.niflheim.stockfish.ui.board;

import javax.swing.*;
import java.awt.*;

public class SurrenderPanel extends JPanel {
    private JButton surrenderButton;
    private GameFrame gameFrame;

    public SurrenderPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        surrenderButton = new JButton("Surrender");
        surrenderButton.addActionListener(e -> {
            boolean isWhiteSurrendering = gameFrame.getBoardPanel().isWhiteTurn(); // 현재 차례가 흰색인지 확인
            gameFrame.handleSurrender(isWhiteSurrendering);
        });
        add(surrenderButton, BorderLayout.CENTER);
    }
}
