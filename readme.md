
# JAVA GUI 기반 체스 게임


### 기능
- Stock fish(체스엔진)과 라이브러리 연동 및 GUI 기반 인터렉션 인터페이스 제공
- 컴퓨터 VS 유저, 유저 VS 유저 모드 제공
- 컴퓨터 대전의 경우 난이도 설정 가능
- 무브 히스토리 패널, 보드 뒤집기 기능, 타이머 기능, 로그인 기능 등 다양한 체스 유틸리티 기능 제공
- 리플레이 기능을 통해 체스 기보를 실행할 수 있음

### 개선 사항
- 기물의 이동을 일반이동, 앙파상, 캐슬링으로 구분하여 위치가 변경된 부분만 업데이트 => 보드 전체를 업데이트 하지않아 성능 향상

### 추후 추가 개선 포인트
- 승진(Promotion)기능 미 구현
- 체크 메이트 시 종료 로직 미구현
- 기물이 이동할 때 이동할 칸의 배경색이 바뀐 후 기물이 나중에 움직이는 문제

### 사용한 라이브러리
[https://github.com/senyast4745/Stockfish-Java](https://github.com/senyast4745/Stockfish-Java) </br>
[https://github.com/bhlangonijr/chesslib](https://github.com/bhlangonijr/chesslib)

### 참고한 오픈 소스
[https://github.com/243698334/Chess](https://github.com/243698334/Chess)


### YouTube demo video
**[youtube demo video](https://youtu.be/Cco1NRFp1SQ)**

<img src="https://github.com/user-attachments/assets/4ef177ae-8b00-41aa-a4bc-16f7a1e378b8" width="600" height="520"/>
<img src="https://github.com/user-attachments/assets/0e608749-d607-4fa9-a0c1-ef855a0a8f7c" width="600" height="520"/>
<img src="https://github.com/user-attachments/assets/8d36e9e5-27c4-4765-90e9-1d7c772effb7" width="812" height="648"/>
