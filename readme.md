
<img src="https://github.com/user-attachments/assets/99273a87-ccdb-4a02-8ef2-53b7f0e2a221" width="468" height="489"/>

<개선사항></br>
1.현재 boardPanel의 loadingBoard메서드는 domove함수가 실행될때마다 보드 전체를 업데이트함 => 약간의 버퍼링 발생</br>
=> 움직이는 칸들만 업데이트하는 로직 구현 필요. // from과 to의 위치를 업데이트하는 것뿐만아니라 앙파상이나 캐슬링이 발생한 경우도 고려해야함

2.엔진 무브가 발생할때 이동할 칸의 배경색이 바뀐 후 기물이 움직이는 문제.

사용한 라이브러리</br>
https://github.com/senyast4745/Stockfish-Java</br>
https://github.com/bhlangonijr/chesslib </br>
-stockfish 체스엔진은 .exe 확장자로 원래는 커맨드로 엔진가 소통해야함. 자바 스톡피쉬 라이브러리리 덕분에 자바 클래스로 stockfish 체스엔진과 상호작용 할 수있음.</br>
-chesslib 라이브러리는 체스 게임 진행에 필요한 대부분의 기능 지원.

![image](https://github.com/user-attachments/assets/6657da2c-5efa-405d-bdaa-fc3ded86f366)

참고한 오픈소스
https://github.com/243698334/Chess
