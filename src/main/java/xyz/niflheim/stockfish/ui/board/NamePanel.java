package xyz.niflheim.stockfish.ui.board;

import javax.swing.*;
import java.awt.*;

public class NamePanel extends JPanel {

    String whitePlayer;
    String blackPlayer;
    boolean isWhite;
    JLabel nickNameLabel;
    public NamePanel(String whitePlayer,String blackPlayer,boolean isWhite,boolean boardReversed) {
        // 패널 레이아웃 설정
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40)); // 배경색 설정 (검은색 느낌)

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.isWhite = isWhite;

        // 이미지 추가
        ImageIcon userIcon = new ImageIcon(getClass().getResource("/image/user.png")); // 이미지 파일 경로 입력
        Image scaledImage = userIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // 이미지 크기 조정
        JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.repaint();

        String nickName;
        if(isWhite) {
            nickName = boardReversed ? blackPlayer : whitePlayer;
        }else {
            nickName = boardReversed ? whitePlayer : blackPlayer;
        }

        // 닉네임 추가
        nickNameLabel = new JLabel(nickName);
        nickNameLabel.setForeground(Color.WHITE); // 글자 색상 (흰색)
        nickNameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // 폰트 설정
        nickNameLabel.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트 정렬 (왼쪽)

        // 이미지와 닉네임을 수평으로 배치할 JPanel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS)); // 수평 박스 레이아웃
        contentPanel.setBackground(new Color(40, 40, 40)); // 배경색 설정
        contentPanel.add(iconLabel); // 아이콘 추가
        contentPanel.add(Box.createHorizontalStrut(10)); // 간격 추가
        contentPanel.add(nickNameLabel); // 닉네임 추가

        // 전체 패널에 contentPanel 추가
        add(contentPanel, BorderLayout.WEST); // 왼쪽 정렬
        setPreferredSize(new Dimension(200, 60)); // 패널 크기 설정
    }
    public void updateName(boolean boardReversed) {
        String nickName;
        if(isWhite) {
            nickName = boardReversed ? blackPlayer : whitePlayer;
        }else {
            nickName = boardReversed ? whitePlayer : blackPlayer;
        }
        nickNameLabel.setText(nickName);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Frame");
        NamePanel userNamePanel = new NamePanel("white","black",true,false);

        // 프레임 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.add(userNamePanel);
        frame.setSize(300, 150); // 프레임 크기 설정
        frame.setLocationRelativeTo(null); // 화면 중앙에 배치
        frame.setVisible(true); // 프레임 표시
    }

}
