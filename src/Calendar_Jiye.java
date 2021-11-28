import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import com.ibm.icu.util.ChineseCalendar;					//외부 jar파일 - 음력

class CalendarData {										//달력 값 구하기
	static final int CAL_WIDTH = 7;							//열
	static final int CAL_HEIGHT = 6;						//행
	int calYear;											//연도
	int calMonth;											//달
	int calDate[][] = new int[CAL_HEIGHT][CAL_WIDTH];		//달력 날짜 배열
	int calDayOfMon;										//날짜
	int calStart;											//1일 시작 위치
	final int calLastDateOfMonth[] = {31,28,31,30,31,30,31,31,30,31,30,31};		//마지막 날짜
	int calLastDate;										//마지막 날짜
	//solarterm : 절기 및 기념일, 제헌절
	int solarterm_Month[] = {1,1,1,1,2,2,2,2,3,3,4,4,5,5,6,6,7,7,7,7,7,8,8,9,9,10,10,11,11,11,12,12};
	int solarterm_Day[] = {6,16,20,27,4,7,15,19,6,21,5,20,6,21,6,22,7,11,17,21,23,8,23,8,23,8,24,8,22,29,7,22};
	String solarterm[] = {"소한","<html>효민<br>생일</html>","대한","<html>웅이<br>생일♥</html>","입춘",
						"<html>혜원<br>생일</html>","내 생일♥","우수","경칩","춘분","청명","곡우","입하","소만",
						"망종","하지","소서","<html>동생<br>생일</html>","제헌절","<html>민정<br>언니<br>생일</html>","대서","입추",
						"처서","백로","추분","한로","상강","입동","소설","<html>혜원<br>언니<br>생일</html>","대설","동지"};
	//solarHoliday : 양력 공휴일
	int solarHoliday_Month[] = {1,3,5,6,8,10,10,12};		//신정,삼일절,어린이날,현충일,광복절,개천절,한글날,크리스마스
	int solarHoliday_Date[] = {1,1,5,6,15,3,9,25};
	String solarHoliday[] = {"신정","삼일절","어린이날","현충일","광복절","개천절","한글날","크리스마스"};
	//lunarHoliday : 음력 공휴일, 부모님 생신		solar_ : 음력 -> 양력 변환
	int solarYear;
	int solarMonth[] = new int[5];				//음력 달을 양력으로 바꾼 것
	int solarDay[] = new int[5];				//음력 날짜를 양력으로 바꾼 것
	int lunarHoliday_Month[] = {1,3,4,4,8};		//설날,부처님오신날,추석 - 음력 & 부모님 생신
	int lunarHoliday_Date[] = {1,11,8,30,15};
	String lunarHoliday[] = {"설날","<html>엄마<br>생일♥</html>","부처님오신날","<html>아빠<br>생일♥</html>","추석"};
	
	Calendar today = Calendar.getInstance();		//객체 생성
	//Calendar today = new Calendar();				추상 클래스이기 때문에 일반적인 객체 생성 방법 불가.
	Calendar cal = Calendar.getInstance();
	ChineseCalendar cc = new ChineseCalendar();		//외부 라이브러리
	
	public CalendarData() {									//생성자 - 기본 설정은 오늘 날짜
		setToday();
	}
	public void setToday() {								//오늘 날짜로 설정하기
		calYear = today.get(Calendar.YEAR);					//현재 연도
		calMonth = today.get(Calendar.MONTH);				//현재 달 - 1, Calendar.Month는 0~11 값을 반환
		calDayOfMon = today.get(Calendar.DAY_OF_MONTH);		//현재 날짜
		makeCalData(today);
	}
	public void makeCalData(Calendar cal) {				//1일의 위치와 마지막 날짜를 구함 - 달력 배열 만듦
		calStart = (cal.get(Calendar.DAY_OF_WEEK)+7 - (cal.get(Calendar.DAY_OF_MONTH))%7)%7;
		//첫 시작 요일 = (현재날짜 요일값(1~7,일요일은 1을 반환)+7- (현재 날짜 % 7)) % 7
		//현재날짜의 요일값이 (현재 날짜 % 7)보다 작은 경우를 대비해서 +7을 해줌.
		//마지막 %7은 총 값이 7이 나온 경우 0을 반환하기 위해 넣음.(일요일)
		
		if(calMonth == 1)
			calLastDate = calLastDateOfMonth[calMonth] + leapCheak(calYear);
			//윤년이면 2월은 29일임.
		else
			calLastDate = calLastDateOfMonth[calMonth];
		
		for(int i = 0 ; i < CAL_HEIGHT ; i++) {			//달력 배열 초기화, 초기화 안하면 현재 달을 제외한 나머지 달 잘못 출력됨.
			for(int j = 0 ; j < CAL_WIDTH ; j++) {
				calDate[i][j] = 0;
			}
		}
		for(int i = 0, num = 1, k = 0 ; i < CAL_HEIGHT ; i++) {
			if(i == 0)
				k = calStart;			//첫행은 1일이 포함되어있으므로 1일 위치부터 배열에 날짜 넣기.
			else
				k = 0;					//나머지행은 1열부터 배열에 날짜 넣음.
			for(int j = k ; j < CAL_WIDTH ; j++) {
				if(num <= calLastDate)
					calDate[i][j] = num++;			//날짜 넣기.
			}
		}
		
	}
	private int leapCheak(int year) {			//윤년인지 확인
		if(year%400 == 0 || year%4 == 0 && year%100 != 0)
			//400으로 나누어지는 경우이거나 4로 나누어지면서 100으로 나누어지지 않는 경우 - 윤년임.
			return 1;
		else
			return 0;
	}
	public void moveMonth(int mon) {		//현재달로부터 n달 전후를 받아 달력 배열을 만드는 함수
		calMonth += mon;			//mon만큼 움직임
		if(calMonth > 11)			//12월을 넘어가는 경우
			while(calMonth>11) {
				calYear++;
				calMonth -= 12;
			}
		else if(calMonth < 0)		//1월을 안넘는 경우
			while(calMonth < 0) {
				calYear--;
				calMonth += 12;
			}
		cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);			//Calendar의 하위 클래스
		makeCalData(cal);			//mon만큼 움직이고 난 그 달의 날짜 배열 만들기
	}
	public void toSolar(int i) {		//음력을 양력으로 변환해주는 함수
		//ChineseCalendar.EXTENDED_YEAR는 Calendar.YEAR 값과 2637 만큼의 차이를 가짐
		cc.set(ChineseCalendar.EXTENDED_YEAR, calYear+2637);
		cc.set(ChineseCalendar.MONTH, lunarHoliday_Month[i]-1);
		cc.set(ChineseCalendar.DAY_OF_MONTH, lunarHoliday_Date[i]);
		
		cal.setTimeInMillis(cc.getTimeInMillis());	//getTimeInMillis()는 현재 날짜와 시간을 밀리초로 바꿔서 반환해 줌.
		
		solarYear = cal.get(Calendar.YEAR);
		solarMonth[i] = cal.get(Calendar.MONTH)+1;
		solarDay[i] = cal.get(Calendar.DAY_OF_MONTH);
	}
}

public class Calendar_Jiye extends CalendarData {
	JFrame introFrame;			//시작 화면
	JFrame mainFrame;			//메인 화면 - 캘린더
	
	JPanel selectPanel;
		JComboBox selectedYear;		//연도 선택
		JLabel year;
		JComboBox selectedMonth;	//달 선택
		JLabel month;
		JButton selectBut;			//이동 버튼
		ListenForSelectBut ForSelectBut = new ListenForSelectBut();
	
	JPanel calSetPanel;
		JButton todayBut;			//투데이 버튼
		JLabel todayLab;			//현재 년/월/일
		JButton lYear;				//<<버튼 이전 년도로 가기
		JButton lMonth;				//<버튼 이전 달로 가기
		JLabel cur;					//현재 달/년
		JButton rMonth;				//>버튼 다음 달로 가기
		JButton rYear;				//>>버튼 다음 년도 가기
		ListenForCalSetButtons ForCalSetButtons = new ListenForCalSetButtons();
	
	JPanel calPanel;
		JButton weekName[];								//요일
		JButton dateButs[][] = new JButton[6][7];		//날짜
		ListenForDatesButs ForDateButs = new ListenForDatesButs();
	
	JPanel infoPanel;
		JLabel infoClock;				//시간
	
	JPanel selBPanel;
		JButton dDay;					//디데이
		JButton memo;					//메모 및 스케줄
		JButton money;					//가계부
		ListenForFunctionButs ForFunctionButs = new ListenForFunctionButs();
	
	JPanel memoPanel;
		JPanel memoBackPanel;
		JLabel selectedDate;			//선택된 날짜
		JPanel memoSubPanel;
		JPanel dDaySel;
		JPanel dDayTxt;
		JPanel all;
		JRadioButton dDayBut[];			//디데이 라디오버튼
		JLabel dDayName;				//디데이 이름
		JTextField dDayNameTxt;			//디데이 이름 입력
		JRadioButton moneyBut[];		//가계부 라디오버튼(지출 or 수입)
		JButton saveBut;				//Save 버튼
		JButton delBut;					//Delete 버튼
		JButton clearBut;				//Clear 버튼
			
	JPanel frameBottomPanel;
		JLabel bottomInfo = new JLabel("Welcome to Jiye's Scheduler!!");		//현재 상태(확인 메시지나 오류 메시지 출력)
	
	final String title = "★JIYE'S SCHEDULER★";
	final String WEEK_NAME[] = {"SUN","MON","TUE","WED","THR","FRI","SAT"};
	
	public Calendar_Jiye() {
		introFrame = new JFrame("안녕하세요!!!!");
		introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		introFrame.setSize(870,605);			//사이즈
		introFrame.setLocationRelativeTo(null);	//윈도우 창을 가운데에 뜨도록 하기
		
		mainFrame = new JFrame(title);			//이름
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(850,605);				//사이즈
		mainFrame.setLocationRelativeTo(null);	//윈도우 창을 가운데에 뜨도록 하기
		//아이콘 설정
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.getImage("calendar.png");
		mainFrame.setIconImage(img);
		introFrame.setIconImage(img);
		
		//LookAndFeel 라이브러리 적용
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainFrame);
		}
		catch(Exception e) {
			bottomInfo.setText("ERROR:LookAndFeel setting failed");
		}
		
		//<시작 화면 설정>
		ImageIcon backGround = new ImageIcon("bear.jpg");
		JPanel backIntro = new JPanel() {
			public void paintComponent(Graphics g) {				//배경 이미지 크기 설정 및 배경 넣기	
				Dimension d = getSize();	//윈도우 사이즈를 받아옴
				g.drawImage(backGround.getImage(), 0, 0, d.width, d.height, null);
				setOpaque(false);			//background의 알파도를 0으로 만들어서 배경을 투명하게 만듦
				super.paintComponent(g);
			}
		};
		backIntro.setLayout(new BorderLayout());
		
		JLabel introLabel = new JLabel("<SCHEDULER>", SwingConstants.CENTER);
		introLabel.setForeground(Color.DARK_GRAY);
		introLabel.setBorder(BorderFactory.createEmptyBorder(90, 0, 0, 0));		//여백 주기(위,왼쪽,아래,오른쪽)
		introLabel.setFont(new Font("",Font.ITALIC,80));						//new Font(폰트 이름, 폰트 스타일, 폰트 크기)
		backIntro.add(introLabel,BorderLayout.NORTH);
		
		JLabel myLabel = new JLabel("IT정보공학과 201819168 안지예", SwingConstants.CENTER);
		myLabel.setForeground(Color.GRAY);
		myLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 90, 0));
		myLabel.setFont(new Font("",Font.PLAIN,18));
		backIntro.add(myLabel,BorderLayout.CENTER);
		
		JButton start = new JButton("♥♥♥♥♥");
		start.setCursor(new Cursor(Cursor.HAND_CURSOR));
		start.setBorderPainted(false);							//버튼 겉선 제거
		start.setOpaque(false);
		start.setFont(new Font("",Font.CENTER_BASELINE, 20));
		start.setForeground(new Color(200, 50, 50));			//글씨 색상 - new Color(r,g,b)
		start.setPreferredSize(new Dimension(125, 70));			//컴포넌트 기본 크기 설정
		start.addActionListener(new ActionListener() {			//시작 화면을 없애고, 메인 화면 띄우기
			public void actionPerformed(ActionEvent e) {
				introFrame.setVisible(false);
				mainFrame.setVisible(true);
			}
		});
		
		JPanel startPanel = new JPanel();
		startPanel.setOpaque(false);
		startPanel.add(start);
		startPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 5));
		backIntro.add(startPanel,BorderLayout.SOUTH);
		
		introFrame.add(backIntro);
		introFrame.setVisible(true);
		
		
		//<메인 화면 설정>
		//콤보박스를 이용해서 원하는 연도와 달로 가기
		selectPanel = new JPanel();
		
		selectedYear = new JComboBox();
		for(int i = 1901 ; i <= 3000 ; i++) {		//1901년부터 2040년까지 선택 가능
            selectedYear.addItem(i);
        }
		selectedYear.setSelectedItem(calYear);		//처음 보이는 연도를 현재 연도로 설정
		year = new JLabel("년 ");
		
		selectedMonth = new JComboBox();
		for(int i = 1 ; i <= 12 ; i++) {			//1월부터 12월 선택
			selectedMonth.addItem(i);
		}
		selectedMonth.setSelectedItem(calMonth+1);	//처음 보이는 달을 현재 달로 설정
		month = new JLabel("월 ");
		
		selectBut = new JButton("이동");
		selectBut.setCursor(new Cursor(Cursor.HAND_CURSOR));	//커서 가져다 대면 마우스 커서가 손 모양으로 바뀌게 함
		selectBut.setForeground(Color.WHITE);					//글씨 색상
		selectBut.setBackground(new Color(115, 120, 120));		//배경 색상 - new Color(r,g,b)
		selectBut.setBorderPainted(false);						//버튼 겉선 없애기
		selectBut.setToolTipText("선택된 날짜로 이동합니다.");			//커서 가져다 대면 도움말이 뜸
		selectBut.addActionListener(ForSelectBut);
		
		selectPanel.add(selectedYear);
		selectPanel.add(year);
		selectPanel.add(selectedMonth);
		selectPanel.add(month);
		selectPanel.add(selectBut);
		
		
		//오늘 날짜로 돌아가는 버튼과 오늘 날짜, '<' '<<' '>' '>>' 버튼, 현재 달/현재 연도
		calSetPanel = new JPanel();
		
		todayBut = new JButton("Today");						//오늘 날짜로 돌아가는 버튼
		todayBut.setToolTipText("오늘 날짜로 돌아갑니다.");			//도움말
		todayBut.addActionListener(ForCalSetButtons);
		
		String str = "오늘은 "+today.get(Calendar.YEAR)+"년 "+(int)(today.get(Calendar.MONTH)+1)
								+"월 "+today.get(Calendar.DAY_OF_MONTH)+"일 입니다.";			//오늘 날짜 정보
		todayLab = new JLabel(str);
		
		lYear = new JButton("<<");							//이전 년도로 가는 버튼
		lYear.setFont(new Font("", Font.PLAIN, 15));
		lYear.setContentAreaFilled(false);					//버튼 모양 제거
		lYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lYear.setToolTipText("이전 년도");
		lYear.addActionListener(ForCalSetButtons);
		
		lMonth = new JButton("<");							//이전 달로 가는 버튼
		lMonth.setFont(new Font("", Font.PLAIN, 15));
		lMonth.setContentAreaFilled(false);					//버튼 모양 제거
		lMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lMonth.setToolTipText("이전 달");
		lMonth.addActionListener(ForCalSetButtons);
		
		cur = new JLabel("<html><table width=100><th><font size=5>"+((calMonth+1)<10 ? "0":"")
								+(calMonth+1)+" / "+calYear+"</th></table></html>");
		//calMonth가 한 글자이면(10월보다 작을 때) 앞에 "0" 추가 - ex)1월 -> 01월
		//<th> - 제목 셀(크고 두껍게 설정)
		
		rMonth = new JButton(">");							//다음 달로 가는 버튼
		rMonth.setFont(new Font("", Font.PLAIN, 15));
		rMonth.setContentAreaFilled(false);					//버튼 모양 제거
		rMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rMonth.setToolTipText("다음 달");
		rMonth.addActionListener(ForCalSetButtons);
		
		rYear = new JButton(">>");							//다음 년도로 가는 버튼
		rYear.setFont(new Font("", Font.PLAIN, 15));
		rYear.setContentAreaFilled(false);					//버튼 모양 제거
		rYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rYear.setToolTipText("다음 년도");
		rYear.addActionListener(ForCalSetButtons);
		
		calSetPanel.setLayout(new GridBagLayout());
		//GridLayout과 비슷하지만 컴포넌트의 위치와 크기를 직접 설정 가능
		GridBagConstraints calSet = new GridBagConstraints();		//배치 방법 설정
		
		calSet.gridx = 1;		calSet.gridy = 1;					//초기의 x축(왼쪽으로부터 수평), y축 값(위로부터 수직)
		//컴포넌트의 좌측 상단 모서리의 시작위치를 지정
		
		calSet.gridwidth = 2;	calSet.gridheight = 1;				//초기의 넓이 값, 초기의 높이 값
		//컴포넌트가 화면에 출력되는 행과 열의 셀 수를 지정
		
		calSet.weightx = 1;		calSet.weighty = 1;					//셀과 셀 사이의 간격(x:수평, y:수직)
		//컴포넌트의 크기가 변경될 때 변경되는 크기를 제어
		
		calSet.insets = new Insets(5,5,0,0);						//프레임 영역안에서의 컴포넌트 외곽의 빈 공간(여백) 값
		calSet.anchor = GridBagConstraints.WEST;					//프레임 영역안에서 컴퍼넌트의 배치하는 장소 값
		calSet.fill = GridBagConstraints.NONE;						//가득 채우지 않고, 기본 크기로 나눔(기본)
		//컴포넌트가 요청한 사이즈보다 컴포넌트의 표시 영역이 클 경우 사이즈 변경에 사용
		//주어진 공간이 있는경우 공간을 컴포넌트로 어떻게 채울것인지 지정해 준다.
		calSetPanel.add(todayBut,calSet);
		
		calSet.gridwidth = 10;
		calSet.gridx = 2;		calSet.gridy = 1;
		calSetPanel.add(todayLab,calSet);
		
		calSet.anchor = GridBagConstraints.CENTER;
		calSet.gridwidth = 1;
		calSet.gridx = 1;		calSet.gridy = 2;
		calSetPanel.add(lYear,calSet);
		
		calSet.gridwidth = 1;
		calSet.gridx = 2;		calSet.gridy = 2;
		calSetPanel.add(lMonth,calSet);
		
		calSet.gridwidth = 2;
		calSet.gridx = 3;		calSet.gridy = 2;
		calSetPanel.add(cur,calSet);
		
		calSet.gridwidth = 1;
		calSet.gridx = 5;		calSet.gridy = 2;
		calSetPanel.add(rMonth,calSet);
		
		calSet.gridwidth = 1;
		calSet.gridx = 6;		calSet.gridy = 2;
		calSetPanel.add(rYear,calSet);
		
		
		//캘린더 출력
		calPanel = new JPanel();
		
		weekName = new JButton[7];							//요일 버튼
		for(int i = 0 ; i < CAL_WIDTH ; i++) {
			weekName[i] = new JButton(WEEK_NAME[i]);
			weekName[i].setBorderPainted(false);			//버튼 테두리 없애기
			weekName[i].setContentAreaFilled(false);		//버튼 모양 제거
			weekName[i].setForeground(Color.WHITE);			//텍스트 색상
			if(i==0)
				weekName[i].setBackground(new Color(200,110,110));		//일요일 - 빨간색, color(r,g,b)
			else if(i == 6)
				weekName[i].setBackground(new Color(95,110,150));		//토요일 - 파란색, color(r,g,b)
			else
				weekName[i].setBackground(new Color(170,170,170));		//평일 - 회색, color(r,g,b)
			weekName[i].setOpaque(true);
			//setOpaque(false)는 background의 알파도를 0으로 만들어서 배경을 투명하게 만드는 함수
			weekName[i].setFocusPainted(false);				//버튼 누르면 버튼에 테두리가 생기는 것을 제거해줌
			calPanel.add(weekName[i]);
		}
		
		for(int i = 0 ; i < CAL_HEIGHT ; i++) {				//달력 날짜 버튼
			for(int j = 0 ; j < CAL_WIDTH ; j++) {
				dateButs[i][j] = new JButton();
				dateButs[i][j].setBorderPainted(false);
				dateButs[i][j].setContentAreaFilled(false);
				dateButs[i][j].setBackground(Color.WHITE);
				dateButs[i][j].setOpaque(true);
				dateButs[i][j].addActionListener(ForDateButs);
				calPanel.add(dateButs[i][j]);
			}
		}
		
		calPanel.setLayout(new GridLayout(0,7,2,2));
		//모든 여백이 2인 7열짜리 GridLayout, 필요한 만큼의 행
		calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		//테두리와 내용물의 간격(위 왼쪽 아래 오른쪽)
		showCal();				//달력 보여주는 함수
		
		
		//현재 시간 및 선택된 날짜
		infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());
		
		infoClock = new JLabel("", SwingConstants.RIGHT);			//현재 시간, 오른쪽 정렬
		infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		infoPanel.add(infoClock, BorderLayout.NORTH);
		infoPanel.add(selectPanel, BorderLayout.CENTER);			//위에서 했던 콤보박스 panel
		
		selectedDate = new JLabel(today.get(Calendar.YEAR)+"/"+(today.get(Calendar.MONTH)+1)
									+"/"+today.get(Calendar.DAY_OF_MONTH)+" 날짜를 선택해주세요!", SwingConstants.LEFT);
		//선택된 날짜, 왼쪽 정렬
		selectedDate.setFont(new Font("",Font.BOLD,12));
		selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		
		
		//디데이, 메모 및 스케줄, 가계부 버튼
		selBPanel = new JPanel();
		
		dDay = new JButton("디데이");
		dDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
		dDay.setForeground(Color.WHITE);
		dDay.setBackground(new Color(255, 190, 190));
		dDay.setBorderPainted(false);
		dDay.setToolTipText("디데이 입력으로 넘어갑니다.");
		dDay.addActionListener(ForFunctionButs);
		
		memo = new JButton("메모 및 스케줄");
		memo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		memo.setForeground(Color.WHITE);
		memo.setBackground(new Color(255, 211, 190));
		memo.setBorderPainted(false);
		memo.setToolTipText("스케줄 입력으로 넘어갑니다.");
		memo.addActionListener(ForFunctionButs);
		
		money = new JButton("가계부");
		money.setCursor(new Cursor(Cursor.HAND_CURSOR));
		money.setForeground(Color.WHITE);
		money.setBackground(new Color(255, 220, 180));
		money.setBorderPainted(false);
		money.setToolTipText("가계부 입력으로 넘어갑니다.");
		money.addActionListener(ForFunctionButs);
		
		selBPanel.setLayout(new FlowLayout());
		selBPanel.add(dDay);
		selBPanel.add(memo);
		selBPanel.add(money);

		infoPanel.add(selBPanel, BorderLayout.SOUTH);
		
		
		//입력할 수 있는 panel, 초기 화면(selBPanel에서 버튼 누르기 전)
		memoPanel = new JPanel();
		memoPanel.setBorder(BorderFactory.createTitledBorder("!!!!위에 버튼을 선택하고 편하게 작성해주세요>O<!!!"));
		//테두리 만들기, 제목 설정
		memoPanel.setLayout(new BorderLayout());
		
		JLabel beginMemoLabel1 = new JLabel("WELCOME", SwingConstants.CENTER);
		beginMemoLabel1.setFont(new Font("",Font.BOLD,35));
		beginMemoLabel1.setBorder(BorderFactory.createEmptyBorder(90, 0, 0, 0));
		
		JLabel beginMemoLabel2 = new JLabel("TO", SwingConstants.CENTER);
		beginMemoLabel2.setFont(new Font("",Font.BOLD,35));
		
		JLabel beginMemoLabel3 = new JLabel("MY SCHEDULER!!!", SwingConstants.CENTER);
		beginMemoLabel3.setFont(new Font("",Font.BOLD,35));
		beginMemoLabel3.setBorder(BorderFactory.createEmptyBorder(0, 0, 90, 0));
		
		memoPanel.add(beginMemoLabel1, BorderLayout.NORTH);
		memoPanel.add(beginMemoLabel2, BorderLayout.CENTER);
		memoPanel.add(beginMemoLabel3, BorderLayout.SOUTH);
		
		
		//메인 프레임 - panel 배치
		JPanel frameSubPanelWest = new JPanel();
		Dimension calSetPanelSize = calSetPanel.getPreferredSize();		//적정 사이즈 반환
		calSetPanelSize.height = 90;									//높이 설정 - 90
		calSetPanel.setPreferredSize(calSetPanelSize);					//calSetPanel 사이즈 설정
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(calSetPanel,BorderLayout.NORTH);
		frameSubPanelWest.add(calPanel,BorderLayout.CENTER);
		
		JPanel frameSubPanelEast = new JPanel();
		frameSubPanelEast.setLayout(new BorderLayout());
		frameSubPanelEast.add(infoPanel,BorderLayout.NORTH);
		frameSubPanelEast.add(memoPanel,BorderLayout.CENTER);
				
		Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
		frameSubPanelWestSize.width = 440;								//너비 설정 - 440
		frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);		//frameSubPanelWest 사이즈 설정
		
		frameBottomPanel = new JPanel();
		frameBottomPanel.add(bottomInfo);
				
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(frameSubPanelWest, BorderLayout.WEST);
		mainFrame.add(frameSubPanelEast, BorderLayout.CENTER);
		mainFrame.add(frameBottomPanel, BorderLayout.SOUTH);	
		
		focusToday();			//오늘 날짜 표시
		
		//Thread 작동(시계, bottomMsg 일정시간후 삭제)
		ThreadControl threadCnl = new ThreadControl();
		threadCnl.start();
	}
	private void focusToday(){				//오늘 날짜에 버튼을 누른 것처럼 표시
		dateButs[today.get(Calendar.WEEK_OF_MONTH)-1][today.get(Calendar.DAY_OF_WEEK)-1].requestFocusInWindow();
		//오늘 날짜에 포커스
	}
	private void showCal() {					//달력 출력하는 함수 - 공휴일, 기념일, 절기 등등 포함
		String str = "대체 휴일";
		
		for(int i = 0 ; i < CAL_HEIGHT ; i++) {
			for(int j = 0 ; j < CAL_WIDTH ; j++) {
				String fontColor = "black";
				if(j==0) fontColor = "red";
				else if(j==6) fontColor = "blue";
				
				//디데이 파일이나 메모 파일이 있는 경우 해당 날짜 두껍게 표시하기 위해 파일 선언
				File f1 = new File("dDayData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDate[i][j]<10?"0":"")+calDate[i][j]+".txt");
				File f2 = new File("MemoData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDate[i][j]<10?"0":"")+calDate[i][j]+".txt");
				
				//양력 공휴일(신정,삼일절,어린이날,현충일,광복절,개천절,한글날,크리스마스)
				for(int m = 0 ; m < solarHoliday.length ; m++) {
					if((calMonth+1) == solarHoliday_Month[m] && calDate[i][j] == solarHoliday_Date[m]) {
						dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
								+"<br></b><font color=red><font size=1>"+solarHoliday[m]+"<br></font></html>");
						//절기 및 기념일 - 공휴일과 겹치면 거기에 추가해줘야 하므로 if문 안에 넣음
						for(int k = 0 ; k < solarterm.length ; k++) {
							if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
								dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
										+"<br></b><font color=red><font size=1>"+solarHoliday[m]
												+"<br><font color=black>"+solarterm[k]+"</font></html>");
							}
						}
						break;
					}
					//어린이날 대체공휴일 - 어린이날이 일요일인 경우, 대체휴일은 2014년부터 도입
					else if(j == 1 && (calMonth+1) == solarHoliday_Month[2] && calDate[i][0] == solarHoliday_Date[2] && calYear > 2013) {
						dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
								+"<br></b><font color=red><font size=1>"+str+"</font></br></html>");
						//절기 및 기념일
						for(int k = 0 ; k < solarterm.length ; k++) {
							if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
								dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
										+"<br></b><font color=red><font size=1>"+str
												+"<br><font color=black>"+solarterm[k]+"</font></html>");
							}
						}
						break;
					}
					//어린이날 대체공휴일 - 어린이날이 토요일인 경우
					//어린이날은 5일이기 때문에 어린이날이 토요일이면 어린이날은 무조건 첫째주 토요일이고, 대체휴일은 무조건 둘째주 월요일
					else if(i == 1 && j == 1 && (calMonth+1) == solarHoliday_Month[2] && calDate[0][6] == solarHoliday_Date[2] && calYear > 2013) {
						dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
								+"<br></b><font color=red><font size=1>"+str+"</font></br></html>");
						//절기 및 기념일
						for(int k = 0 ; k < solarterm.length ; k++) {
							if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
								dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
										+"<br></b><font color=red><font size=1>"+str
												+"<br><font color=black>"+solarterm[k]+"</font></html>");
							}
						}
						break;
					}
					
					//음력 공휴일(설날,부처님오신날,추석) 및 부모님 생신
					for(int n = 0 ; n < lunarHoliday.length ; n++) {
						toSolar(n);			//음력을 양력으로 변환해주는 함수
						if(calYear == solarYear && (calMonth+1) == solarMonth[n] && calDate[i][j] == solarDay[n]) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+lunarHoliday[n]+"</font></html>");
							//음력 생일은 공휴일이 아니므로 검정색 글씨로.
							if(calDate[i][j] == solarDay[1] || calDate[i][j] == solarDay[3])
								dateButs[i][j].setText("<html><b><font color=black><font size=4>"+calDate[i][j]
										+"<br></b><font color=black><font size=1>"+lunarHoliday[n]+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+lunarHoliday[n]
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//연휴, 대체 휴일(2014년부터 도입)
						//일요일이 설날이거나 추석인 경우 - 그 전 행 6번째열(토요일)이 연휴
						else if((j == 1) && (((calMonth+1) == solarMonth[0] && calDate[i][0] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i][0] == solarDay[4]))) {
							//(calMonth+1)이 양력으로 변환한 설날 달과 같으면 "설날" 출력
							String s1 = ((calMonth+1) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>연휴</html>";
							//토요일 연휴
							dateButs[i-1][6].setText("<html><b><font color=red><font size=4>"+calDate[i-1][6]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//월요일 연휴
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+s1
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//일요일이 설날 또는 추석인 경우 화요일 대체 휴일
						else if((j == 2) && (((calMonth+1) == solarMonth[0] && calDate[i][0] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i][0] == solarDay[4])) && calYear > 2013) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+str+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+str
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}						
							break;
						}
						//토요일이 설날이거나 추석인 경우 - 현재 일요일로 그 전 행 금요일도 연휴가 되어야함
						else if((i != 0 && j == 0) && (((calMonth+1) == solarMonth[0] && calDate[i-1][6] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i-1][6] == solarDay[4]))) {
							String s1 = ((calMonth+1) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>연휴</html>";
							//그 전 행 금요일
							dateButs[i-1][5].setText("<html><b><font color=red><font size=4>"+calDate[i-1][5]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//일요일
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red<font size=1>"+s1+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+s1
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//토요일이 설날이거나 추석인 경우 월요일 대체 휴일
						else if((i != 0 && j == 1) && (((calMonth+1) == solarMonth[0] && calDate[i-1][6] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i-1][6] == solarDay[4])) && calYear > 2013) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+str+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+str
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//평일이 설날이거나 추석인 경우
						else if((j != 0 && j != 1) && (((calMonth+1) == solarMonth[0] && calDate[i][j-1] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i][j-1] == solarDay[4]))) {
							String s1 = ((calMonth+1) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>연휴</html>";
							dateButs[i][j-2].setText("<html><b><font color=red><font size=4>"+calDate[i][j-2]
									+"<br></b><font color=red><font size=1>"+s1+"</font></b></html>");
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+s1
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//월요일이 설날 또는 추석인 경우 수요일 대체 휴일
						else if((j == 3) && (((calMonth+1) == solarMonth[0] && calDate[i][1] == solarDay[0])
										|| ((calMonth+1) == solarMonth[4] && calDate[i][1] == solarDay[4])) && calYear > 2013) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+str+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+str
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//디데이 파일이 있는 경우 해당 날짜 두껍게
						else if(f1.exists()){
							dateButs[i][j].setText("<html><b><font color="+fontColor+">"+calDate[i][j]+"</font></b></html>");
							break;
						}
						//메모 파일이 있는 경우 해당 날짜 두껍게
						else if(f2.exists()){
							dateButs[i][j].setText("<html><b><font color="+fontColor+">"+calDate[i][j]+"</font></b></html>");
							break;
						}
						//아무 날도 아닌 경우
						else {
							dateButs[i][j].setText("<html><font color="+fontColor+">"+calDate[i][j]+"</font></html>");
							//절기 및 기념일
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k] ) {
									dateButs[i][j].setText("<html><b><font color="+fontColor+"><font size=4>"+calDate[i][j]
											+"</b>"+"<br><font color=black><font size=1>"+solarterm[k]+"</font></html>");
								}
							}
						}
					}
				}
				//설날이나 추석이 1일인 경우 그 전 달 마지막 날이 연휴여야함.
				int count = 0, cnt = 0;
				for(int n = 4 ; n < CAL_HEIGHT ; n++) {		//마지막 행 구하기 위해서.
					if(calDate[n][0] == 0) {				//5째주가 없는데 for문으로 n이 5가 되어 cnt도 5가 되는 걸 방지
						cnt = 4;
						break;
					}
					for(int m = 0 ; m < CAL_WIDTH ; m++) {	//마지막 열 구하기 위해서.
						if(calDate[n][m] != 0) {			//calDate가 0이 되기 전이 마지막 열
							count = m;
						}
						else {								//calDate가 0이 되면 그 행이 마지막 행
							cnt = n;
							break;
						}
					}
				}
				if(((calMonth+2) == solarMonth[0] && solarDay[0] == 1) || ((calMonth+2) == solarMonth[4] && solarDay[4] == 1)) {
					//다음 달(calMonth+2)이 양력으로 변환된 설날의 달과 같으면 "설날" 출력
					String s1 = ((calMonth+2) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>연휴</html>";
					dateButs[cnt][count].setText("<html><b><font color=red><font size=4>"+calDate[cnt][count]
							+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
					//절기 및 기념일
					for(int k = 0 ; k < solarterm.length ; k++) {
						if((calMonth+1) == solarterm_Month[k] && calDate[cnt][count] == solarterm_Day[k]) {
							dateButs[cnt][count].setText("<html><b><font color=red><font size=4>"+calDate[cnt][count]
									+"<br></b><font color=red><font size=1>"+s1
											+"<br><font color=black>"+solarterm[k]+"</font></html>");
						}
					}
				}
				
				//설날이나 추석이 그 달의 마지막 날인 경우 다음 달 1일이 연휴여야함.
				if((calMonth == solarMonth[0] && solarDay[0] == calLastDateOfMonth[calMonth-1]) 
									|| (calMonth == solarMonth[4] && solarDay[4] == calLastDateOfMonth[calMonth-1])) {
					//이전 달(calMonth)이 양력으로 변환된 설날의 달과 같으면 "설날" 출력
					String s1 = (calMonth == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>연휴</html>";
					dateButs[0][calStart].setText("<html><b><font color=red><font size=4>"+calDate[0][calStart]
							+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
					//절기 및 기념일
					for(int k = 0 ; k < solarterm.length ; k++) {
						if((calMonth+1) == solarterm_Month[k] && calDate[cnt][count] == solarterm_Day[k]) {
							dateButs[0][calStart].setText("<html><b><font color=red><font size=4>"+calDate[0][calStart]
									+"<br></b><font color=red><font size=1>"+s1
											+"<br><font color=black>"+solarterm[k]+"</font></html>");
						}
					}
				}
				
				JLabel todayMark = new JLabel("<html><font color=green>*</html>");
				dateButs[i][j].removeAll();		//안하면 다른 달의 똑같은 위치의 버튼에서도 todayMark 출력됨
				if(calMonth == today.get(Calendar.MONTH)&&calYear == today.get(Calendar.YEAR)
							&&calDate[i][j] == today.get(Calendar.DAY_OF_MONTH)) {
					dateButs[i][j].add(todayMark);		//오늘 날짜 앞에 *붙이기
				}
				if(calDate[i][j] == 0)
					dateButs[i][j].setVisible(false);				//배열 값에 0 들어가있으면 달력 창에 안나타나게 하기(0일 출력x)
				else
					dateButs[i][j].setVisible(true);
			}
		}
	}
	//콤보 박스 이동 버튼 이벤트
	private class ListenForSelectBut implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == selectBut) {
				calYear = selectedYear.getSelectedIndex() + 1901;		//index는 0부터 시작하므로 +1901 해줘야함.
				calMonth = selectedMonth.getSelectedIndex();			//calMonth도 0부터 시작하므로 +1 안해줘도 됨.
				cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
				makeCalData(cal);			//달력 배열 만들기
				cur.setText("<html><table width=100><th><font size=5>"+((calMonth+1)<10 ? "0":"")
						+(calMonth+1)+" / "+calYear+"</th></table></html>");
				//calMonth가 한 글자이면(10월보다 작을 때) 앞에 "0" 추가
				showCal();					//달력 출력
			}
		}
	}
	//Today << < > >> 버튼 이벤트
	private class ListenForCalSetButtons implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == todayBut) {					//today버튼 눌렀을 때
				setToday();									//현재 날짜
				ForDateButs.actionPerformed(e);				//today버튼 눌렀을 때 selectedDate도 변경되어야하기 때문에.
				focusToday();								//현재 날짜에 포커스
			}
			else if(e.getSource() == lYear)					//'<<'버튼 눌렀을 때
				moveMonth(-12);
			else if(e.getSource() == lMonth)				//'<'버튼 눌렀을 때
				moveMonth(-1);
			else if(e.getSource() == rMonth)				//'>'버튼 눌렀을 때
				moveMonth(1);
			else if(e.getSource() == rYear)					//'>>'버튼 눌렀을 때
				moveMonth(12);
			
			cur.setText("<html><table width=100><th><font size=5>"+((calMonth+1)<10 ? "0":"")
								+(calMonth+1)+" / "+calYear+"</th></table></html>");
			//calMonth가 한 글자이면(10월보다 작을 때) 앞에 "0" 추가
			showCal();
		}
	}
	//달력 버튼, today 버튼 이벤트
	private class ListenForDatesButs implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int k = 0, l = 0;
			for(int i = 0 ; i< CAL_HEIGHT ; i++) {
				for(int j = 0 ; j < CAL_WIDTH ; j++) {
					if(e.getSource() == dateButs[i][j]) {		//달력 버튼 눌렀을 때
						k = i;
						l = j;
					}
				}
			}
			if(e.getSource() != todayBut)				//today버튼을 눌러서 실행되면 k=0, l=0으로 되므로 방지
				calDayOfMon = calDate[k][l];
			cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
			selectedDate.setText(calYear+"/"+(calMonth+1)+"/"+calDayOfMon);
		}
	}
	//디데이, 메모 및 스케줄, 가계부 버튼 이벤트
	private class ListenForFunctionButs implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == dDay) {               //디데이 버튼 눌렀을 때
		            memoPanel.removeAll();			  //초기화. 안하면 계속 위에 더해져서 만들어짐.	
		            memoPanel.setBackground(new Color(255,250,215));
		            memoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));      //테두리 양각으로.
		            
		            memoBackPanel = new JPanel();
		            memoBackPanel.setOpaque(false);
		            
		            dDaySel = new JPanel();
		            dDaySel.setOpaque(false);         //투명하게 만들어줌.
		            
		            all = new JPanel();
		            all.setOpaque(false);
		            all.setLayout(new BorderLayout());
		            all.add(selectedDate, BorderLayout.NORTH);
		            
		            dDayBut = new JRadioButton[5];					//종류 - 라디오 버튼
		            ButtonGroup dDayGroup = new ButtonGroup();		//중복 체크 안되게 그룹으로 설정
		            String dDayType[] = {"생일","기념일","과제","시험","기타"};
		            for(int i = 0 ; i < dDayType.length ; i++) {
		               dDayBut[i] = new JRadioButton();
		               dDayBut[i].setText(dDayType[i]);
		               dDayBut[i].setBackground(new Color(255,250,215));
		               dDayBut[i].setBorderPainted(false);
		               dDayGroup.add(dDayBut[i]);
		               dDaySel.add(dDayBut[i]);
		            }
		            all.add(dDaySel, BorderLayout.CENTER);
		            
		            dDayName = new JLabel("이름 : ");
		            dDayNameTxt = new JTextField(15);
		            
		            dDayTxt = new JPanel();
		            dDayTxt.setOpaque(false);
		            
		            dDayTxt.add(dDayName);
		            dDayTxt.add(dDayNameTxt);

		            final DefaultListModel<String> dDayList = new DefaultListModel<>();
		            //컴포넌트에 데이터를 추가 혹은 삭제하기 위해서 모델이 필요
		            
		            JScrollPane scrollList = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		            //세로 스크롤만. 가로 스크롤은 x
		            
		            final JList<String> ls = new JList<>(dDayList);			//디데이를 넣기 위한 JList 선언
		            ls.setPreferredSize(new Dimension(300,180));			//크기 설정
		            scrollList.setPreferredSize(new Dimension(300,160));
		            
		            JPanel list = new JPanel();
		            list.setOpaque(false);
		            
		            scrollList.setViewportView(ls);							//JList에 스크롤 추가
		            list.add(scrollList);
		            
		            JLabel help1 = new JLabel("1.디데이를 입력할 날짜를 선택하세요(입력할 때마다).");
		            JLabel help1_2 = new JLabel("(오늘 날짜에 입력할 때도 오늘 날짜를 눌러주세요.)");
		            JLabel help2 = new JLabel("2.디데이 삭제 : 삭제할 날짜를 선택 -> 해당 디데이를 선택 -> 삭제");
		            JLabel help3 = new JLabel("3.하루에 2개 이상의 디데이가 저장되어있는데 하나를 삭제한다면");
		            JLabel help3_2= new JLabel("  그 날의 디데이 파일이 모두 날라가므로 유의해주세요.");
		            JLabel help4 = new JLabel("4.디데이,일정,가계부 모두 엔터로 입력 가능합니다(save기능).");
		            list.add(help1);
		            list.add(help1_2);
		            list.add(help2);
		            list.add(help3);
		            list.add(help3_2);
		            list.add(help4);
		            
		            //껐다가 켰을 때 파일이 있으면 파일 내용 추가
		            try {
		    			File f = new File("dDayData");
		    			if(f.exists()) {
		    				String BufferStr = null;
		    				File[] FolderList = f.listFiles();
		    				if(FolderList.length == 0)
		    					f.delete();
		    				else {
						    	for(int j = 0 ; j < FolderList.length ; j++) {
						    		BufferedReader in = new BufferedReader(new FileReader(FolderList[j]));
						    		while((BufferStr = in.readLine()) != null) {
				    					dDayList.addElement(BufferStr);
				    				}
				    				in.close();
						    	}
					    	}
		    			}
		    		}
		    		catch(IOException ex) {
		    		}
		            
		            dDayNameTxt.addActionListener(new ActionListener() {			//엔터로 입력 가능
		            	public void actionPerformed(ActionEvent e) {
		            		String dDayStr = new String();
			                int dDayVal = ((int)((cal.getTimeInMillis() - today.getTimeInMillis())/1000/60/60/24));
			                //getTimeInMillis()는 현재 날짜와 시간을 밀리초로 바꿔서 반환해 줌.
			                
			                if(dDayVal == 0 && (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR))
			                      && (cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))
			                      && (cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)))
			                	dDayStr = "TODAY";
			                else if(dDayVal >= 0)
			                    dDayStr = "D-" +(dDayVal+1);
			                else if(dDayVal < 0)
			                    dDayStr = "D+" +(dDayVal)*(-1);
			                
			                int index;
			                for(index = 0 ; index <dDayType.length ; index++) {
			                    if(dDayBut[index].isSelected())		//선택된 라디오 버튼의 index 반환
			                        break;
			                }
			                
			                String name = dDayNameTxt.getText();
		            		if(name.length() <= 0)
		            			bottomInfo.setText("<html><font color = red>이름을 작성해주세요.</font></html>");
		            		else {
		            			 try {
			                    	 File f= new File("dDayData");
			                    	 if(!f.isDirectory()) f.mkdir();		//해당 폴더가 없으면 폴더 만듦
			                    	 
			                    	 String str = "<html><b><font color = green>["+dDayBut[index].getText()+"]&nbsp;</font>"
			                                    +selectedDate.getText() +"&nbsp;</b>"
			                                    +name+" : <b>"+dDayStr+"</html>";
			                    	 dDayList.addElement(str);			//JList에 추가
			                    	 
				                	 BufferedWriter bw = new BufferedWriter(new FileWriter("dDayData/"+calYear+((calMonth+1)<10?"0":"")
		              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt",true));
				                	 //FileWriter에서 true를 해주지 않으면 파일을 덮어쓰기 때문에 기존 파일이 날라간다.
				                	 PrintWriter pw = new PrintWriter(bw, true);
				                	 //생성된 파일의 뒤에 이어서 쓴다.
				                	 String saveStr = "<html><b><font color = green>["+dDayBut[index].getText()+"]&nbsp;</font>"
			                                    +selectedDate.getText() +"&nbsp;</b>"
			                                    +name+" : <b>"+dDayStr+"</html>";
				                	 //디데이 종류와 선택된 날짜, 이름, 디데이 입력
				                	 pw.write(saveStr+"\n");	//파일에 추가
				                	 pw.close();
				                  }
				                  catch(IOException ex) {
				                  }
			                      bottomInfo.setText("디데이를 저장하였습니다.");
			                      dDayNameTxt.setText("");		//초기화
			                      showCal();
		            		}
		            	}
		            });
		            
		            saveBut = new JButton("SAVE");
		            saveBut.setBackground(new Color(255,220,196));
		            saveBut.setBorderPainted(false);
		            saveBut.setToolTipText("디데이를 저장합니다.");
		            saveBut.addActionListener(new ActionListener() {			//save버튼 눌렀을 때, 엔터로 입력했을 때와 동일
		               public void actionPerformed(ActionEvent e) {
		                  String dDayStr = new String();
		                  int dDayVal = ((int)((cal.getTimeInMillis() - today.getTimeInMillis())/1000/60/60/24));
		                  
		                  if(dDayVal == 0 && (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR))
		                        && (cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))
		                        && (cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)))
		                     dDayStr = "TODAY";
		                  else if(dDayVal >= 0)
		                     dDayStr = "D-" +(dDayVal+1);
		                  else if(dDayVal < 0)
		                     dDayStr = "D+" +(dDayVal)*(-1);
		                  
		                  int index;
		                  for(index = 0 ; index <dDayType.length ; index++) {
		                     if(dDayBut[index].isSelected())
		                        break;
		                  }
		                  
		                  String name = dDayNameTxt.getText();
		                  
		                  if(index == dDayType.length)			//라디오 버튼 선택 안했을 때.
		                     bottomInfo.setText("<html><font color = red>종류를 선택해주세요.</font></html>");
		                  else if(name.length() <= 0)
		                     bottomInfo.setText("<html><font color = red>이름을 작성해주세요.</font></html>");
		                  else {
		                     try {
		                    	 File f= new File("dDayData");
		                    	 if(!f.isDirectory()) f.mkdir();
		                    	 
		                    	 String str = "<html><b><font color = green>["+dDayBut[index].getText()+"]&nbsp;</font>"
		                                    +selectedDate.getText() +"&nbsp;</b>"
		                                    +name+" : <b>"+dDayStr+"</html>";
		                    	 dDayList.addElement(str);
			                	 BufferedWriter bw = new BufferedWriter(new FileWriter("dDayData/"+calYear+((calMonth+1)<10?"0":"")
	              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt",true));
			                	 //FileWriter에서 true를 해주지 않으면 파일을 덮어쓰기 때문에 기존 파일이 날라간다.
			                	 PrintWriter pw = new PrintWriter(bw, true);
			                	 //생성된 파일의 뒤에 이어서 쓴다.
			                	 String saveStr = "<html><b><font color = green>["+dDayBut[index].getText()+"]&nbsp;</font>"
		                                    +selectedDate.getText() +"&nbsp;</b>"
		                                    +name+" : <b>"+dDayStr+"</html>\n";
			                	 pw.write(saveStr);		//파일에 추가
			                	 pw.close();
			                  }
			                  catch(IOException ex) {
			                  }
		                     bottomInfo.setText("디데이를 저장하였습니다.");
		                     dDayNameTxt.setText("");		//초기화
		                     showCal();
		                  }
		               }
		            });
		            
		            delBut = new JButton("DELETE");
		            delBut.setBackground(new Color(255,220,196));
		            delBut.setBorderPainted(false);
		            delBut.setToolTipText("선택한 디데이를 삭제합니다.");
		            delBut.addActionListener(new ActionListener() {			//delete버튼 눌렀을 때
		               public void actionPerformed(ActionEvent e) {
		            	  dDayNameTxt.setText("");		//초기화
		            	  
		                  int index = ls.getSelectedIndex();
		                  if(index < 0)					//JList 선택 안되었을 때
		                	  bottomInfo.setText("<html><font color = red>선택한 디데이가 없습니다.</font></html>");
		                  else {
		                	  File f1 =new File("dDayData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)
		                			  				+(calDayOfMon<10?"0":"")+calDayOfMon+".txt");
		                	  if(f1.exists()) {
		                		  f1.delete();
		                		  showCal();
		                	  }
		                	  dDayList.remove(index);	//해당 디데이 지우기
		                	  bottomInfo.setText("선택한 디데이를 삭제하였습니다.");
		                  }
		               }
		            });
		            
		            clearBut = new JButton("CLEAR");
		            clearBut.setBackground(new Color(255,220,196));
		            clearBut.setBorderPainted(false);
		            clearBut.setToolTipText("모든 디데이를 삭제합니다.");
		            clearBut.addActionListener(new ActionListener() {		//clear버튼 눌렀을 때
		               public void actionPerformed(ActionEvent e) {
		            	   dDayNameTxt.setText("");		//초기화
		            	   
		            	   if(dDayList.getSize() == 0)		//JList 비워져있는 경우
		            		   bottomInfo.setText("<html><font color = red>디데이 목록이 이미 비워져 있습니다.</font></html>");
		            	   else {
		            		   File f1 = new File("dDayData");
						       if(f1.exists()) {
						    	   File[] deleteFolderList = f1.listFiles();
						    	   for(int j = 0 ; j < deleteFolderList.length ; j++) {
						    		   deleteFolderList[j].delete();		//하위 파일 삭제
						    	   }
						    	   showCal();
						    	   bottomInfo.setText("모든 디데이를 삭제하였습니다.");
						       }
		            		   dDayList.removeAllElements();
		            	   }
		               }
		            });
		            saveBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
		            delBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
		            clearBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
		            
		            memoSubPanel = new JPanel();
		            memoSubPanel.add(saveBut);
		            memoSubPanel.add(delBut);
		            memoSubPanel.add(clearBut);
		            
		            memoBackPanel.setLayout(new BorderLayout());
		            memoBackPanel.add(all,BorderLayout.NORTH);
		            memoBackPanel.add(dDayTxt,BorderLayout.CENTER);
		            Dimension memoBackPanelSize = memoBackPanel.getPreferredSize();
		            memoBackPanelSize.height = 90;
		            memoBackPanel.setPreferredSize(memoBackPanelSize);
		            
		            memoPanel.add(memoBackPanel, BorderLayout.NORTH);
		            memoPanel.add(list,BorderLayout.CENTER);
		            memoPanel.add(memoSubPanel, BorderLayout.SOUTH);
		        }
			
			else if(e.getSource() == memo) {				//메모 및 스케줄
				memoPanel.removeAll();
				memoPanel.setBackground(new Color(255,250,215));
				memoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));		//테두리 양각으로.
				
				memoBackPanel = new JPanel();
				memoBackPanel.setOpaque(false);
				
				all = new JPanel();
				all.setOpaque(false);
				all.setLayout(new BorderLayout());
				all.add(selectedDate, BorderLayout.NORTH);
				
				JLabel info = new JLabel("<html><b><font size = 4><font color=gray>★Todo List★&nbsp;</b></font></html>", SwingConstants.CENTER);
				info.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
				all.add(info, BorderLayout.CENTER);
				
				JPanel todoList = new JPanel();
				todoList.setOpaque(false);
				JLabel input = new JLabel("<할 일>", SwingConstants.CENTER);
				JTextField tf = new JTextField(32);
				tf.setHorizontalAlignment(JTextField.CENTER);			//텍스트 필드 텍스트 가운데 정렬
				
				todoList.add(input);
				todoList.add(tf);
				todoList.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
				
				final DefaultListModel<String> memoList = new DefaultListModel<>();
				JScrollPane scrollList = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	            final JList<String> ls = new JList<>(memoList);
	            ls.setPreferredSize(new Dimension(300,200));
	            scrollList.setPreferredSize(new Dimension(300,180));
	            
	            JPanel list = new JPanel();
	            list.setOpaque(false);
	            
	            scrollList.setViewportView(ls);
	            list.add(scrollList);
	            
	            JLabel help1 = new JLabel("1.일정 및 메모를 입력할 날짜를 선택하세요.");
	            JLabel help1_2 = new JLabel("(오늘 날짜에 입력할 때도 오늘 날짜를 눌러주세요.)");
	            JLabel help2 = new JLabel("2.일정 삭제 : 삭제할 날짜를 선택 -> 해당 일정 선택 -> 삭제");
	            JLabel help3 = new JLabel("3.하루에 2개 이상의 일정이 저장되어있는데 하나를 삭제한다면");
	            JLabel help3_2= new JLabel("  그 날의 일정 파일이 모두 날라가므로 유의해주세요.");
	           
	            list.add(help1);
	            list.add(help1_2);
	            list.add(help2);
	            list.add(help3);
	            list.add(help3_2);
	            
	            //껐다가 다시 실행했을 때 파일이 있으면 표시하기 위해.
	            try {
	            	File f = new File("MemoData");
	            	if(f.exists()) {
	            		String Buffer = null;
	            		File[] FolderList = f.listFiles();
	            		if(FolderList.length == 0)
	            			f.delete();
	            		else {
	            			for(int i = 0 ; i < FolderList.length ; i++) {
	            				BufferedReader in = new BufferedReader(new FileReader(FolderList[i]));
	            				while((Buffer = in.readLine()) != null) {
	            					memoList.addElement(Buffer);
	            				}
	            				in.close();
	            			}
	            		}
	            	}
	            }
	            catch(IOException ex) {
	            }
	            
	            tf.addActionListener(new ActionListener() {					//엔터 누르면 추가
	            	public void actionPerformed(ActionEvent e) {
	            		if(tf.getText().length() <= 0)						//아무것도 입력이 안되어 있는 경우
            				bottomInfo.setText("<html><font color = red>일정이나 메모를 먼저 작성해주세요.</font></html>");
	            		else {
		            		try {
		            			File f = new File("MemoData");
		            			if(!f.isDirectory()) f.mkdir();				//폴더 없으면 폴더 만들기
		            			
		            			BufferedWriter bw = new BufferedWriter(new FileWriter("MemoData/"+calYear+((calMonth+1)<10?"0":"")
	              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt",true));
			                	//FileWriter에서 true를 해주지 않으면 파일을 덮어쓰기 때문에 기존 파일이 날라간다.
			                	PrintWriter pw = new PrintWriter(bw, true);
			                	//생성된 파일의 뒤에 이어서 쓴다.
			                	
			                	memoList.addElement(selectedDate.getText()+" : "+tf.getText());	//선택된 날짜와 입력된 메모 추가
			                	pw.write(selectedDate.getText()+" : "+tf.getText()+"\n");		//파일에 추가
			                	pw.close();
		            		}
		            		catch(IOException ex) {	
		            		}
		            		tf.setText("");			//초기화
		            		showCal();
		            	}
	            	}
	            });
	            
				memoSubPanel = new JPanel();
				saveBut = new JButton("SAVE");
				saveBut.setBackground(new Color(255,220,196));
				saveBut.setBorderPainted(false);
				saveBut.setToolTipText("일정 및 메모를 저장합니다.");
				saveBut.addActionListener(new ActionListener() {		//save버튼 눌렀을 때. 엔터로 입력할 때와 동일
	            	public void actionPerformed(ActionEvent e) {
	            		if(tf.getText().length() <= 0)
            				bottomInfo.setText("<html><font color = red>일정이나 메모를 먼저 작성해주세요.</font></html>");
	            		else {
		            		try {
		            			File f = new File("MemoData");
		            			if(!f.isDirectory()) f.mkdir();
		            			
		            			BufferedWriter bw = new BufferedWriter(new FileWriter("MemoData/"+calYear+((calMonth+1)<10?"0":"")
	              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt",true));
			                	//FileWriter에서 true를 해주지 않으면 파일을 덮어쓰기 때문에 기존 파일이 날라간다.
			                	PrintWriter pw = new PrintWriter(bw, true);
			                	pw.write(selectedDate.getText()+" : "+tf.getText());		//파일에 추가
			                	memoList.addElement(selectedDate.getText()+" : "+tf.getText());
			                	pw.close();
		            		}
		            		catch(IOException ex) {	
		            		}
		            		bottomInfo.setText("일정을 저장하였습니다.");
		            		tf.setText("");		//초기화
		            		showCal();
	            		}
	            	}
	            });
				
				delBut = new JButton("DELETE");
				delBut.setBackground(new Color(255,220,196));
				delBut.setBorderPainted(false);
				delBut.setToolTipText("선택한 일정 및 메모를 삭제합니다.");
				delBut.addActionListener(new ActionListener() {				//delete 버튼 눌렀을 때
					public void actionPerformed(ActionEvent e) {
						int index = ls.getSelectedIndex();
						if(index < 0)		//JList에서 선택이 안되어있을 때
							bottomInfo.setText("<html><font color = red>선택된 일정 및 메모가 없습니다.</font></html>");
						
						else {
							File f = new File("MemoData/"+calYear+((calMonth+1)<10?"0":"")
	              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt");
							if(f.exists()) {
								f.delete();				//파일 삭제
								showCal();
							}
							memoList.remove(index);		//JList에서 선택된 일정 삭제
							bottomInfo.setText("선택한 일정 및 메모를 삭제하였습니다.");
						}
					}
				});
				
				clearBut = new JButton("CLEAR");
				clearBut.setBackground(new Color(255,220,196));
				clearBut.setBorderPainted(false);
				clearBut.setToolTipText("모든 일정 및 메모를 삭제합니다.");
				clearBut.addActionListener(new ActionListener() {			//clear 버튼 눌렀을 때
					public void actionPerformed(ActionEvent e) {
						if(memoList.getSize() == 0)			//JList에 이미 아무것도 없을 때
							bottomInfo.setText("<html><font color = red>일정 목록이 이미 비워져 있습니다.</font></html>");
						
						else {
							File f = new File("MemoData");
							if(f.exists()) {
								File[] deleteFolderList = f.listFiles();	//폴더 안에 있는 파일들
								for(int i = 0 ; i < deleteFolderList.length ; i++)
									deleteFolderList[i].delete();			//모든 파일 삭제
								showCal();
								bottomInfo.setText("모든 디데이를 삭제하였습니다.");
							}
							memoList.removeAllElements();					//JList 다 지우기
						}
					}
				});
				
				saveBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
	            delBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
	            clearBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
	            
				memoSubPanel.add(saveBut);
				memoSubPanel.add(delBut);
				memoSubPanel.add(clearBut);
				
				memoBackPanel.setLayout(new BorderLayout());
				memoBackPanel.add(all,BorderLayout.NORTH);
				memoBackPanel.add(todoList, BorderLayout.CENTER);
				Dimension memoBackPanelSize = memoBackPanel.getPreferredSize();
				memoBackPanelSize.height = 100;
				memoBackPanel.setPreferredSize(memoBackPanelSize);
				
				memoPanel.add(memoBackPanel, BorderLayout.NORTH);
				memoPanel.add(list, BorderLayout.CENTER);
				memoPanel.add(memoSubPanel, BorderLayout.SOUTH);
			}
			else if(e.getSource() == money) {				// 가계부
				memoPanel.removeAll();
				memoPanel.setBackground(new Color(255,250,215));
				memoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));		//테두리 양각으로.
				
				memoBackPanel = new JPanel();
				memoBackPanel.setOpaque(false);
				memoBackPanel.setLayout(new BorderLayout());
				
				all = new JPanel();
				all.setOpaque(false);
				all.setLayout(new BorderLayout());
				all.add(selectedDate, BorderLayout.NORTH);
				
				JPanel moneySel = new JPanel();
				moneySel.setOpaque(false);
				
				moneyBut = new JRadioButton[2];					//지출, 수입 선택하는 라디오 버튼
				ButtonGroup moneyGroup = new ButtonGroup();		//중복 선택 안되게 그룹 설정
				String moneyType[] = {"지출","수입"};
				for(int i = 0 ; i < moneyType.length ; i++) {
					moneyBut[i] = new JRadioButton(moneyType[i]);
					moneyBut[i].setBackground(new Color(255,250,215));
					moneyBut[i].setBorderPainted(false);
					moneyGroup.add(moneyBut[i]);
					moneySel.add(moneyBut[i]);
				}
				all.add(moneySel, BorderLayout.CENTER);
				
				JPanel mainPanel = new JPanel();
				mainPanel.setOpaque(false);
				
				JLabel beginSum = new JLabel("현재 금액 : ");
				JTextField beginSumTxt = new JTextField(5);
				beginSumTxt.setHorizontalAlignment(JTextField.CENTER);
				
				JLabel use = new JLabel("이용 내역 : ");
				JTextField useTxt = new JTextField(5);
				useTxt.setHorizontalAlignment(JTextField.CENTER);
				
				JLabel price = new JLabel("       금액 : ");
				JTextField priceTxt = new JTextField(5);
				priceTxt.setHorizontalAlignment(JTextField.CENTER);
				
				Box beginBox = Box.createHorizontalBox();			//수평 방향으로 컴포넌트들 묶기
				Box useBox = Box.createHorizontalBox();
				Box priceBox = Box.createHorizontalBox();
				
				beginBox.add(beginSum);
				beginBox.add(beginSumTxt);
				useBox.add(use);
				useBox.add(useTxt);
				priceBox.add(price);
				priceBox.add(priceTxt);
				
				beginBox.setBorder(BorderFactory.createEmptyBorder(15, 40, 0, 40));
				useBox.setBorder(BorderFactory.createEmptyBorder(15, 40, 0, 40));
				priceBox.setBorder(BorderFactory.createEmptyBorder(15, 40, 0, 40));
				
				mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));		//수직 정렬
				mainPanel.add(beginBox);
				mainPanel.add(useBox);
				mainPanel.add(priceBox);
				
				JLabel resultSum = new JLabel("남은 금액 : ", SwingConstants.CENTER);
				JLabel resultSumTxt = new JLabel();
				
				JLabel resultOutput = new JLabel("총 지출 : ", SwingConstants.CENTER);
				JLabel resultOutputTxt = new JLabel("0");
				
				JLabel resultInput = new JLabel("총 수입 : ", SwingConstants.CENTER);
				JLabel resultInputTxt = new JLabel("0");
				
				Box resultBox = Box.createHorizontalBox();				//수평 방향으로 컴포넌트들 묶기
				Box resultOutputBox = Box.createHorizontalBox();
				Box resultInputBox = Box.createHorizontalBox();
				
				resultBox.add(resultSum);
				resultBox.add(resultSumTxt);
				resultBox.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));	
				resultOutputBox.add(resultOutput);
				resultOutputBox.add(resultOutputTxt);
				resultInputBox.add(resultInput);
				resultInputBox.add(resultInputTxt);
				
				mainPanel.add(resultBox);
				mainPanel.add(resultOutputBox);
				mainPanel.add(resultInputBox);
				
				memoBackPanel.add(mainPanel,BorderLayout.NORTH);
				Dimension mainPanelSize = mainPanel.getPreferredSize();
				mainPanelSize.height = 180;
				mainPanel.setPreferredSize(mainPanelSize);
				
				JPanel helpPanel = new JPanel();
				helpPanel.setOpaque(false);
				JLabel help1 = new JLabel("1.처음 사용하신다면 현재 금액(시작 금액)을 입력해주셔야합니다.");
	            JLabel help1_2 = new JLabel("(한 번 입력하고 가계부를 사용하면 현재 금액이 저절로 계산됨.)");
	            JLabel help2 = new JLabel("2.날짜를 선택한 뒤 종류를 고르고 입력해주세요.");
	            JLabel help3 = new JLabel("3.DELETE는 바로 전 금액으로 돌아가는 기능입니다(연속 2번은 안됨).");
	            JLabel help4= new JLabel("4.CLEAR는 현재 금액을 RESET하는 기능입니다.");
	            helpPanel.add(help1);
	            helpPanel.add(help1_2);
	            helpPanel.add(help2);
	            helpPanel.add(help3);
	            helpPanel.add(help4);
	            helpPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
	            memoBackPanel.add(helpPanel, BorderLayout.CENTER);
				
	            JLabel temp = new JLabel();				//임시적으로 값 보관, delete하면 그 전 값으로 돌아가기 위해서 만들어 놓음.
	            JLabel temp1 = new JLabel("0");
	            JLabel temp2 = new JLabel("0");
	            
	            //다시 실행했을 때 파일이 있는 경우 표시하기 위해
	            try {
	            	File f = new File("moneyData/money.txt");
	            	if(f.exists()) {
	            		String BufferedStr = null;
	            		int i = 0;
	            		
	            		BufferedReader in = new BufferedReader(new FileReader(f));
	            		
	            		while((BufferedStr = in.readLine()) != null) {
	            			if(i == 0) {			//첫 번째 줄은 현재 금액
	            				beginSumTxt.setText(BufferedStr);
	            				resultSumTxt.setText(BufferedStr + "원");
	            			}
	            			if(i == 1) {			//두 번째 줄은 총 지출
	            				resultOutputTxt.setText(BufferedStr);
	            			}
	            			if(i == 2) {			//세 번째 줄은 총 수입
	            				resultInputTxt.setText(BufferedStr);
	            			}
	            			i++;
	            		}
	            		in.close();
	            	}
	            }
	            catch(IOException ex) {
	            }
	            
	            priceTxt.addActionListener(new ActionListener() {					//엔터로 입력 가능
	            	public void actionPerformed(ActionEvent e) {
	            		int index;
	            		for(index = 0 ; index < moneyType.length ; index++) {
	            			if(moneyBut[index].isSelected())		//선택된 라디오 버튼의 index 반환
	            				break;
	            		}
	            		
	            		if(index == moneyType.length)		//라디오 버튼 선택 안했을 때
	            			bottomInfo.setText("<html><font color=red>지출, 수입을 골라주세요.</font></html>");
	            		else if(beginSumTxt.getText().length() <= 0||useTxt.getText().length() <= 0||priceTxt.getText().length() <= 0)
	            			//텍스트 필드에 아무 값도 입력되어있지 않은 경우
	            			bottomInfo.setText("<html><font color=red>빈칸을 입력해주세요.</font></html>");
	            		else if(isNumeric(beginSumTxt.getText()) == false || isNumeric(priceTxt.getText()) == false) {
	            			//현재 금액이나 금액이 숫자가 아닌경우
	            			bottomInfo.setText("<html><font color=red>숫자만 입력해주세요.</font></html>");
	            			if(isNumeric(beginSumTxt.getText()) == false)
	            				beginSumTxt.setText("");		//초기화
	            			if(isNumeric(priceTxt.getText()) == false)
	            				priceTxt.setText("");		//초기화
	            		}
	            		else {
	            			try {
	            				File f = new File("moneyData");
	            				if(!f.isDirectory()) f.mkdir();			//폴더가 없으면 폴더 생성
	            				
	            				BufferedWriter bw = new BufferedWriter(new FileWriter("moneyData/money.txt"));

	            				if(index == 0) {		//지출인 경우
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//지출이나 수입이 입력되기 전 값 임시 저장
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin - price;
	            					bw.write(Integer.toString(begin)+"\n");				//현재 금액 파일에 저장
	            					
	            					temp1.setText(resultOutputTxt.getText());			//총 지출 값 임시 저장
	            					temp2.setText(resultInputTxt.getText());			//총 수입 값 임시 저장
	            					
	            					int resultPrice = Integer.parseInt(resultOutputTxt.getText()) + price;
	            					resultOutputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(Integer.toString(resultPrice)+"\n");		//총 지출 값 파일에 저장
	            					bw.write(resultInputTxt.getText()+"\n");			//총 수입 값 파일에 저장
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "원");
	            					bottomInfo.setText("["+priceTxt.getText()+"원 지출] 저장하였습니다.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//초기화
	            					priceTxt.setText("");		//초기화
	            				}
	            				else if(index == 1) {
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//지출이나 수입이 입력되기 전 값 임시 저장
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin + price;
	            					bw.write(Integer.toString(begin)+"\n");				//현재 금액 파일에 저장
	            					
	            					temp1.setText(resultOutputTxt.getText());			//총 지출 값 임시 저장
	            					temp2.setText(resultInputTxt.getText());			//총 수입 값 임시 저장
	            					
	            					int resultPrice = Integer.parseInt(resultInputTxt.getText()) + price;
	            					resultInputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(resultOutputTxt.getText()+"\n");			//총 지출 값 파일에 저장
	            					bw.write(Integer.toString(resultPrice)+"\n");		//총 수입 값 파일에 저장
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "원");
	            					bottomInfo.setText("["+priceTxt.getText()+"원 수입] 저장하였습니다.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//초기화
	            					priceTxt.setText("");		//초기화
	            				}
	            				bw.close();
	            			}
	            			catch(IOException ex) {
	            			}
	            		}
	            	}
	            });
	            
				memoSubPanel = new JPanel();
				saveBut = new JButton("SAVE");
				saveBut.setBackground(new Color(255,220,196));
				saveBut.setBorderPainted(false);
				saveBut.setToolTipText("가계부를 저장합니다.");
				saveBut.addActionListener(new ActionListener() {			//save버튼을 눌렀을 때. 엔터로 입력했을 때와 동일
	            	public void actionPerformed(ActionEvent e) {
	            		int index;
	            		for(index = 0 ; index < moneyType.length ; index++) {
	            			if(moneyBut[index].isSelected())
	            				break;
	            		}
	            		
	            		if(index == moneyType.length)
	            			bottomInfo.setText("<html><font color=red>지출, 수입을 골라주세요.</font></html>");
	            		else if(beginSumTxt.getText().length() <= 0||useTxt.getText().length() <= 0||priceTxt.getText().length() <= 0)
	            			bottomInfo.setText("<html><font color=red>빈칸을 입력해주세요.</font></html>");
	            		else if(isNumeric(beginSumTxt.getText()) == false || isNumeric(priceTxt.getText()) == false) {
	            			bottomInfo.setText("<html><font color=red>숫자만 입력해주세요.</font></html>");
	            			if(isNumeric(beginSumTxt.getText()) == false)
	            				beginSumTxt.setText("");		//초기화
	            			if(isNumeric(priceTxt.getText()) == false)
	            				priceTxt.setText("");			//초기화
	            		}
	            		
	            		else {
	            			try {
	            				File f = new File("moneyData");
	            				if(!f.isDirectory()) f.mkdir();		//폴더가 없는 경우 폴더 생성
	            				
	            				BufferedWriter bw = new BufferedWriter(new FileWriter("moneyData/money.txt"));

	            				if(index == 0) {		//지출인 경우
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//지출이나 수입이 입력되기 전 값 임시 저장
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin - price;
	            					bw.write(Integer.toString(begin)+"\n");			//현재 금액 파일에 저장
	            					
	            					temp1.setText(resultOutputTxt.getText());		//총 지출 값 임시 저장
	            					temp2.setText(resultInputTxt.getText());		//총 수입 값 임시 저장
	            					
	            					int resultPrice = Integer.parseInt(resultOutputTxt.getText()) + price;
	            					resultOutputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(Integer.toString(resultPrice)+"\n");	//총 지출 값 파일에 저장
	            					bw.write(resultInputTxt.getText()+"\n");		//총 수입 값 파일에 저장
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "원");
	            					bottomInfo.setText("["+priceTxt.getText()+"원 지출] 저장하였습니다.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//초기화
	            					priceTxt.setText("");		//초기화
	            				}
	            				else if(index == 1) {
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//지출이나 수입이 입력되기 전 값 임시 저장
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin + price;
	            					bw.write(Integer.toString(begin)+"\n");				//현재 금액 파일에 저장
	            					
	            					temp1.setText(resultOutputTxt.getText());			//총 지출 값 임시 저장
	            					temp2.setText(resultInputTxt.getText());			//총 수입 값 임시 저장
	            					
	            					int resultPrice = Integer.parseInt(resultInputTxt.getText()) + price;
	            					resultInputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(resultOutputTxt.getText()+"\n");			//총 지출 값 파일에 저장
	            					bw.write(Integer.toString(resultPrice)+"\n");		//총 수입 값 파일에 저장
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "원");
	            					bottomInfo.setText("["+priceTxt.getText()+"원 수입] 저장하였습니다.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//초기화
	            					priceTxt.setText("");		//초기화
	            				}
	            				bw.close();
	            			}
	            			catch(IOException ex) {
	            			}
	            		}
	            	}
				});
				
				delBut = new JButton("DELETE");
				delBut.setBackground(new Color(255,220,196));
				delBut.setBorderPainted(false);
				delBut.setToolTipText("지출이나 수입을 입력하기 전으로 돌아갑니다.");
				delBut.addActionListener(new ActionListener() {				//delete 버튼 눌렀을 때
					public void actionPerformed(ActionEvent e) {
						if(temp.getText().length() <= 0)					//이전 값이 없을 때
							bottomInfo.setText("<html><font color=red>입력된 지출이나 수입이 없습니다.</font></html>");
						else {
							try {
								BufferedWriter bw = new BufferedWriter(new FileWriter("moneyData/money.txt"));
								
								//입력하기 전 값으로 돌아가기.
								beginSumTxt.setText(temp.getText());
								resultSumTxt.setText(temp.getText() + "원");
								resultOutputTxt.setText(temp1.getText());
								resultInputTxt.setText(temp2.getText());
								
								bw.write(temp.getText()+"\r");				// \n으로 하니까 메모장에 그대로 저장돼서 \r로 바꿈
								bw.write(temp1.getText()+"\r");
								bw.write(temp2.getText()+"\r");
								
								bottomInfo.setText("입력했던 지출이나 수입을 지웠습니다.");
								temp.setText("");		//초기화
								bw.close();
							}
							catch(IOException ex) {
	            			}
						}
					}
				});
				
				clearBut = new JButton("CLEAR");
				clearBut.setBackground(new Color(255,220,196));
				clearBut.setBorderPainted(false);
				clearBut.setToolTipText("현재 금액을 Reset합니다.");
				clearBut.addActionListener(new ActionListener() {		//clear버튼을 눌렀을 때
					public void actionPerformed(ActionEvent e) {
						if(beginSumTxt.getText().length() <= 0)			//현재 금액이 설정되어 있지 않은 경우
							bottomInfo.setText("<html><font color=red>이미 현재 금액이 설정되어 있지 않습니다.</font></html>");
						else {
							File f = new File("moneyData/money.txt");
							if(f.exists()) {
								f.delete();			//파일 지우기
								File f1 = new File("moneyData");
								f1.delete();		//폴더 지우기
							}
							beginSumTxt.setText("");		//초기화
							resultSumTxt.setText("");		//초기화
							resultOutputTxt.setText("0");	//초기화
							resultInputTxt.setText("0");	//초기화
							bottomInfo.setText("현재 금액을 초기화했습니다.");
						}
					}	
				});
				saveBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
	            delBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
	            clearBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
	            
				memoSubPanel.add(saveBut);
				memoSubPanel.add(delBut);
				memoSubPanel.add(clearBut);
								
				memoPanel.add(all, BorderLayout.NORTH);
				memoPanel.add(memoBackPanel, BorderLayout.CENTER);
				memoPanel.add(memoSubPanel,BorderLayout.SOUTH);
			}
		}
	}
	public static boolean isNumeric(String s) {				//문자열이 숫자인지 확인하는 함수
		try {
			Double.parseDouble(s);			//실수 안에 정수도 포함되어있으므로.
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
	//시간, bottomInfo 지속 시간
	private class ThreadControl extends Thread{
		public void run(){
			boolean msgCntFlag = false;
			int num = 0;
			String curStr = new String();
			
			while(true){
				try{
					//현재 시간 설정
					today = Calendar.getInstance();
					String amPm = (today.get(Calendar.AM_PM) == 0 ? "AM":"PM");
					
					String hour;
							if(today.get(Calendar.HOUR) == 0) hour = "12"; 
							else if(today.get(Calendar.HOUR) == 12) hour = " 0";
							else hour = (today.get(Calendar.HOUR)<10?"0":"")+today.get(Calendar.HOUR);
							//한 자리수면 앞에 "0"추가
							
					String min = (today.get(Calendar.MINUTE)<10?"0":"")+today.get(Calendar.MINUTE);
					String sec = (today.get(Calendar.SECOND)<10?"0":"")+today.get(Calendar.SECOND);
					
					infoClock.setText(amPm+" "+hour+":"+min+":"+sec);
					
					//bottomInfo 시간이 지나면 자동으로 사라지게 설정
					sleep(1000);		//1초동안 일시 정지
					String infoStr = bottomInfo.getText();
					
					//bottomInfo가 새로 들어왔을 때.
					if(infoStr != " " && (msgCntFlag == false || curStr != infoStr)){
						num = 5;
						msgCntFlag = true;
						curStr = infoStr;
					}
					//bottomInfo 지속되다가 사라짐
					else if(infoStr != " " && msgCntFlag == true){
						if(num > 0) num--;
						else {
							msgCntFlag = false;
							bottomInfo.setText(" ");
						}
					}		
				}
				catch(InterruptedException e){
					System.out.println("Thread:Error");
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Calendar_Jiye();
	}
}