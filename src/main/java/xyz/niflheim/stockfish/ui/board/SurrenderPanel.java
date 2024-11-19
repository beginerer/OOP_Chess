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
        setLayout(new BorderLayout()); // BorderLayout으로 설정하여 버튼이 전체 패널을 채우도록 함

        surrenderButton = new JButton("Surrender");
        surrenderButton.addActionListener(e -> gameFrame.showVictoryPanel());

        // 패널 크기에 맞게 버튼을 추가
        add(surrenderButton, BorderLayout.CENTER);
    }
}

