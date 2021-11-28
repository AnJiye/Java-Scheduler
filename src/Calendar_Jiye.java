import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import com.ibm.icu.util.ChineseCalendar;					//�ܺ� jar���� - ����

class CalendarData {										//�޷� �� ���ϱ�
	static final int CAL_WIDTH = 7;							//��
	static final int CAL_HEIGHT = 6;						//��
	int calYear;											//����
	int calMonth;											//��
	int calDate[][] = new int[CAL_HEIGHT][CAL_WIDTH];		//�޷� ��¥ �迭
	int calDayOfMon;										//��¥
	int calStart;											//1�� ���� ��ġ
	final int calLastDateOfMonth[] = {31,28,31,30,31,30,31,31,30,31,30,31};		//������ ��¥
	int calLastDate;										//������ ��¥
	//solarterm : ���� �� �����, ������
	int solarterm_Month[] = {1,1,1,1,2,2,2,2,3,3,4,4,5,5,6,6,7,7,7,7,7,8,8,9,9,10,10,11,11,11,12,12};
	int solarterm_Day[] = {6,16,20,27,4,7,15,19,6,21,5,20,6,21,6,22,7,11,17,21,23,8,23,8,23,8,24,8,22,29,7,22};
	String solarterm[] = {"����","<html>ȿ��<br>����</html>","����","<html>����<br>���Ϣ�</html>","����",
						"<html>����<br>����</html>","�� ���Ϣ�","���","��Ĩ","���","û��","���","����","�Ҹ�",
						"����","����","�Ҽ�","<html>����<br>����</html>","������","<html>����<br>���<br>����</html>","�뼭","����",
						"ó��","���","�ߺ�","�ѷ�","��","�Ե�","�Ҽ�","<html>����<br>���<br>����</html>","�뼳","����"};
	//solarHoliday : ��� ������
	int solarHoliday_Month[] = {1,3,5,6,8,10,10,12};		//����,������,��̳�,������,������,��õ��,�ѱ۳�,ũ��������
	int solarHoliday_Date[] = {1,1,5,6,15,3,9,25};
	String solarHoliday[] = {"����","������","��̳�","������","������","��õ��","�ѱ۳�","ũ��������"};
	//lunarHoliday : ���� ������, �θ�� ����		solar_ : ���� -> ��� ��ȯ
	int solarYear;
	int solarMonth[] = new int[5];				//���� ���� ������� �ٲ� ��
	int solarDay[] = new int[5];				//���� ��¥�� ������� �ٲ� ��
	int lunarHoliday_Month[] = {1,3,4,4,8};		//����,��ó�Կ��ų�,�߼� - ���� & �θ�� ����
	int lunarHoliday_Date[] = {1,11,8,30,15};
	String lunarHoliday[] = {"����","<html>����<br>���Ϣ�</html>","��ó�Կ��ų�","<html>�ƺ�<br>���Ϣ�</html>","�߼�"};
	
	Calendar today = Calendar.getInstance();		//��ü ����
	//Calendar today = new Calendar();				�߻� Ŭ�����̱� ������ �Ϲ����� ��ü ���� ��� �Ұ�.
	Calendar cal = Calendar.getInstance();
	ChineseCalendar cc = new ChineseCalendar();		//�ܺ� ���̺귯��
	
	public CalendarData() {									//������ - �⺻ ������ ���� ��¥
		setToday();
	}
	public void setToday() {								//���� ��¥�� �����ϱ�
		calYear = today.get(Calendar.YEAR);					//���� ����
		calMonth = today.get(Calendar.MONTH);				//���� �� - 1, Calendar.Month�� 0~11 ���� ��ȯ
		calDayOfMon = today.get(Calendar.DAY_OF_MONTH);		//���� ��¥
		makeCalData(today);
	}
	public void makeCalData(Calendar cal) {				//1���� ��ġ�� ������ ��¥�� ���� - �޷� �迭 ����
		calStart = (cal.get(Calendar.DAY_OF_WEEK)+7 - (cal.get(Calendar.DAY_OF_MONTH))%7)%7;
		//ù ���� ���� = (���糯¥ ���ϰ�(1~7,�Ͽ����� 1�� ��ȯ)+7- (���� ��¥ % 7)) % 7
		//���糯¥�� ���ϰ��� (���� ��¥ % 7)���� ���� ��츦 ����ؼ� +7�� ����.
		//������ %7�� �� ���� 7�� ���� ��� 0�� ��ȯ�ϱ� ���� ����.(�Ͽ���)
		
		if(calMonth == 1)
			calLastDate = calLastDateOfMonth[calMonth] + leapCheak(calYear);
			//�����̸� 2���� 29����.
		else
			calLastDate = calLastDateOfMonth[calMonth];
		
		for(int i = 0 ; i < CAL_HEIGHT ; i++) {			//�޷� �迭 �ʱ�ȭ, �ʱ�ȭ ���ϸ� ���� ���� ������ ������ �� �߸� ��µ�.
			for(int j = 0 ; j < CAL_WIDTH ; j++) {
				calDate[i][j] = 0;
			}
		}
		for(int i = 0, num = 1, k = 0 ; i < CAL_HEIGHT ; i++) {
			if(i == 0)
				k = calStart;			//ù���� 1���� ���ԵǾ������Ƿ� 1�� ��ġ���� �迭�� ��¥ �ֱ�.
			else
				k = 0;					//���������� 1������ �迭�� ��¥ ����.
			for(int j = k ; j < CAL_WIDTH ; j++) {
				if(num <= calLastDate)
					calDate[i][j] = num++;			//��¥ �ֱ�.
			}
		}
		
	}
	private int leapCheak(int year) {			//�������� Ȯ��
		if(year%400 == 0 || year%4 == 0 && year%100 != 0)
			//400���� ���������� ����̰ų� 4�� ���������鼭 100���� ���������� �ʴ� ��� - ������.
			return 1;
		else
			return 0;
	}
	public void moveMonth(int mon) {		//����޷κ��� n�� ���ĸ� �޾� �޷� �迭�� ����� �Լ�
		calMonth += mon;			//mon��ŭ ������
		if(calMonth > 11)			//12���� �Ѿ�� ���
			while(calMonth>11) {
				calYear++;
				calMonth -= 12;
			}
		else if(calMonth < 0)		//1���� �ȳѴ� ���
			while(calMonth < 0) {
				calYear--;
				calMonth += 12;
			}
		cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);			//Calendar�� ���� Ŭ����
		makeCalData(cal);			//mon��ŭ �����̰� �� �� ���� ��¥ �迭 �����
	}
	public void toSolar(int i) {		//������ ������� ��ȯ���ִ� �Լ�
		//ChineseCalendar.EXTENDED_YEAR�� Calendar.YEAR ���� 2637 ��ŭ�� ���̸� ����
		cc.set(ChineseCalendar.EXTENDED_YEAR, calYear+2637);
		cc.set(ChineseCalendar.MONTH, lunarHoliday_Month[i]-1);
		cc.set(ChineseCalendar.DAY_OF_MONTH, lunarHoliday_Date[i]);
		
		cal.setTimeInMillis(cc.getTimeInMillis());	//getTimeInMillis()�� ���� ��¥�� �ð��� �и��ʷ� �ٲ㼭 ��ȯ�� ��.
		
		solarYear = cal.get(Calendar.YEAR);
		solarMonth[i] = cal.get(Calendar.MONTH)+1;
		solarDay[i] = cal.get(Calendar.DAY_OF_MONTH);
	}
}

public class Calendar_Jiye extends CalendarData {
	JFrame introFrame;			//���� ȭ��
	JFrame mainFrame;			//���� ȭ�� - Ķ����
	
	JPanel selectPanel;
		JComboBox selectedYear;		//���� ����
		JLabel year;
		JComboBox selectedMonth;	//�� ����
		JLabel month;
		JButton selectBut;			//�̵� ��ư
		ListenForSelectBut ForSelectBut = new ListenForSelectBut();
	
	JPanel calSetPanel;
		JButton todayBut;			//������ ��ư
		JLabel todayLab;			//���� ��/��/��
		JButton lYear;				//<<��ư ���� �⵵�� ����
		JButton lMonth;				//<��ư ���� �޷� ����
		JLabel cur;					//���� ��/��
		JButton rMonth;				//>��ư ���� �޷� ����
		JButton rYear;				//>>��ư ���� �⵵ ����
		ListenForCalSetButtons ForCalSetButtons = new ListenForCalSetButtons();
	
	JPanel calPanel;
		JButton weekName[];								//����
		JButton dateButs[][] = new JButton[6][7];		//��¥
		ListenForDatesButs ForDateButs = new ListenForDatesButs();
	
	JPanel infoPanel;
		JLabel infoClock;				//�ð�
	
	JPanel selBPanel;
		JButton dDay;					//����
		JButton memo;					//�޸� �� ������
		JButton money;					//�����
		ListenForFunctionButs ForFunctionButs = new ListenForFunctionButs();
	
	JPanel memoPanel;
		JPanel memoBackPanel;
		JLabel selectedDate;			//���õ� ��¥
		JPanel memoSubPanel;
		JPanel dDaySel;
		JPanel dDayTxt;
		JPanel all;
		JRadioButton dDayBut[];			//���� ������ư
		JLabel dDayName;				//���� �̸�
		JTextField dDayNameTxt;			//���� �̸� �Է�
		JRadioButton moneyBut[];		//����� ������ư(���� or ����)
		JButton saveBut;				//Save ��ư
		JButton delBut;					//Delete ��ư
		JButton clearBut;				//Clear ��ư
			
	JPanel frameBottomPanel;
		JLabel bottomInfo = new JLabel("Welcome to Jiye's Scheduler!!");		//���� ����(Ȯ�� �޽����� ���� �޽��� ���)
	
	final String title = "��JIYE'S SCHEDULER��";
	final String WEEK_NAME[] = {"SUN","MON","TUE","WED","THR","FRI","SAT"};
	
	public Calendar_Jiye() {
		introFrame = new JFrame("�ȳ��ϼ���!!!!");
		introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		introFrame.setSize(870,605);			//������
		introFrame.setLocationRelativeTo(null);	//������ â�� ����� �ߵ��� �ϱ�
		
		mainFrame = new JFrame(title);			//�̸�
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(850,605);				//������
		mainFrame.setLocationRelativeTo(null);	//������ â�� ����� �ߵ��� �ϱ�
		//������ ����
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.getImage("calendar.png");
		mainFrame.setIconImage(img);
		introFrame.setIconImage(img);
		
		//LookAndFeel ���̺귯�� ����
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainFrame);
		}
		catch(Exception e) {
			bottomInfo.setText("ERROR:LookAndFeel setting failed");
		}
		
		//<���� ȭ�� ����>
		ImageIcon backGround = new ImageIcon("bear.jpg");
		JPanel backIntro = new JPanel() {
			public void paintComponent(Graphics g) {				//��� �̹��� ũ�� ���� �� ��� �ֱ�	
				Dimension d = getSize();	//������ ����� �޾ƿ�
				g.drawImage(backGround.getImage(), 0, 0, d.width, d.height, null);
				setOpaque(false);			//background�� ���ĵ��� 0���� ���� ����� �����ϰ� ����
				super.paintComponent(g);
			}
		};
		backIntro.setLayout(new BorderLayout());
		
		JLabel introLabel = new JLabel("<SCHEDULER>", SwingConstants.CENTER);
		introLabel.setForeground(Color.DARK_GRAY);
		introLabel.setBorder(BorderFactory.createEmptyBorder(90, 0, 0, 0));		//���� �ֱ�(��,����,�Ʒ�,������)
		introLabel.setFont(new Font("",Font.ITALIC,80));						//new Font(��Ʈ �̸�, ��Ʈ ��Ÿ��, ��Ʈ ũ��)
		backIntro.add(introLabel,BorderLayout.NORTH);
		
		JLabel myLabel = new JLabel("IT�������а� 201819168 ������", SwingConstants.CENTER);
		myLabel.setForeground(Color.GRAY);
		myLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 90, 0));
		myLabel.setFont(new Font("",Font.PLAIN,18));
		backIntro.add(myLabel,BorderLayout.CENTER);
		
		JButton start = new JButton("����������");
		start.setCursor(new Cursor(Cursor.HAND_CURSOR));
		start.setBorderPainted(false);							//��ư �Ѽ� ����
		start.setOpaque(false);
		start.setFont(new Font("",Font.CENTER_BASELINE, 20));
		start.setForeground(new Color(200, 50, 50));			//�۾� ���� - new Color(r,g,b)
		start.setPreferredSize(new Dimension(125, 70));			//������Ʈ �⺻ ũ�� ����
		start.addActionListener(new ActionListener() {			//���� ȭ���� ���ְ�, ���� ȭ�� ����
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
		
		
		//<���� ȭ�� ����>
		//�޺��ڽ��� �̿��ؼ� ���ϴ� ������ �޷� ����
		selectPanel = new JPanel();
		
		selectedYear = new JComboBox();
		for(int i = 1901 ; i <= 3000 ; i++) {		//1901����� 2040����� ���� ����
            selectedYear.addItem(i);
        }
		selectedYear.setSelectedItem(calYear);		//ó�� ���̴� ������ ���� ������ ����
		year = new JLabel("�� ");
		
		selectedMonth = new JComboBox();
		for(int i = 1 ; i <= 12 ; i++) {			//1������ 12�� ����
			selectedMonth.addItem(i);
		}
		selectedMonth.setSelectedItem(calMonth+1);	//ó�� ���̴� ���� ���� �޷� ����
		month = new JLabel("�� ");
		
		selectBut = new JButton("�̵�");
		selectBut.setCursor(new Cursor(Cursor.HAND_CURSOR));	//Ŀ�� ������ ��� ���콺 Ŀ���� �� ������� �ٲ�� ��
		selectBut.setForeground(Color.WHITE);					//�۾� ����
		selectBut.setBackground(new Color(115, 120, 120));		//��� ���� - new Color(r,g,b)
		selectBut.setBorderPainted(false);						//��ư �Ѽ� ���ֱ�
		selectBut.setToolTipText("���õ� ��¥�� �̵��մϴ�.");			//Ŀ�� ������ ��� ������ ��
		selectBut.addActionListener(ForSelectBut);
		
		selectPanel.add(selectedYear);
		selectPanel.add(year);
		selectPanel.add(selectedMonth);
		selectPanel.add(month);
		selectPanel.add(selectBut);
		
		
		//���� ��¥�� ���ư��� ��ư�� ���� ��¥, '<' '<<' '>' '>>' ��ư, ���� ��/���� ����
		calSetPanel = new JPanel();
		
		todayBut = new JButton("Today");						//���� ��¥�� ���ư��� ��ư
		todayBut.setToolTipText("���� ��¥�� ���ư��ϴ�.");			//����
		todayBut.addActionListener(ForCalSetButtons);
		
		String str = "������ "+today.get(Calendar.YEAR)+"�� "+(int)(today.get(Calendar.MONTH)+1)
								+"�� "+today.get(Calendar.DAY_OF_MONTH)+"�� �Դϴ�.";			//���� ��¥ ����
		todayLab = new JLabel(str);
		
		lYear = new JButton("<<");							//���� �⵵�� ���� ��ư
		lYear.setFont(new Font("", Font.PLAIN, 15));
		lYear.setContentAreaFilled(false);					//��ư ��� ����
		lYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lYear.setToolTipText("���� �⵵");
		lYear.addActionListener(ForCalSetButtons);
		
		lMonth = new JButton("<");							//���� �޷� ���� ��ư
		lMonth.setFont(new Font("", Font.PLAIN, 15));
		lMonth.setContentAreaFilled(false);					//��ư ��� ����
		lMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lMonth.setToolTipText("���� ��");
		lMonth.addActionListener(ForCalSetButtons);
		
		cur = new JLabel("<html><table width=100><th><font size=5>"+((calMonth+1)<10 ? "0":"")
								+(calMonth+1)+" / "+calYear+"</th></table></html>");
		//calMonth�� �� �����̸�(10������ ���� ��) �տ� "0" �߰� - ex)1�� -> 01��
		//<th> - ���� ��(ũ�� �β��� ����)
		
		rMonth = new JButton(">");							//���� �޷� ���� ��ư
		rMonth.setFont(new Font("", Font.PLAIN, 15));
		rMonth.setContentAreaFilled(false);					//��ư ��� ����
		rMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rMonth.setToolTipText("���� ��");
		rMonth.addActionListener(ForCalSetButtons);
		
		rYear = new JButton(">>");							//���� �⵵�� ���� ��ư
		rYear.setFont(new Font("", Font.PLAIN, 15));
		rYear.setContentAreaFilled(false);					//��ư ��� ����
		rYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rYear.setToolTipText("���� �⵵");
		rYear.addActionListener(ForCalSetButtons);
		
		calSetPanel.setLayout(new GridBagLayout());
		//GridLayout�� ��������� ������Ʈ�� ��ġ�� ũ�⸦ ���� ���� ����
		GridBagConstraints calSet = new GridBagConstraints();		//��ġ ��� ����
		
		calSet.gridx = 1;		calSet.gridy = 1;					//�ʱ��� x��(�������κ��� ����), y�� ��(���κ��� ����)
		//������Ʈ�� ���� ��� �𼭸��� ������ġ�� ����
		
		calSet.gridwidth = 2;	calSet.gridheight = 1;				//�ʱ��� ���� ��, �ʱ��� ���� ��
		//������Ʈ�� ȭ�鿡 ��µǴ� ��� ���� �� ���� ����
		
		calSet.weightx = 1;		calSet.weighty = 1;					//���� �� ������ ����(x:����, y:����)
		//������Ʈ�� ũ�Ⱑ ����� �� ����Ǵ� ũ�⸦ ����
		
		calSet.insets = new Insets(5,5,0,0);						//������ �����ȿ����� ������Ʈ �ܰ��� �� ����(����) ��
		calSet.anchor = GridBagConstraints.WEST;					//������ �����ȿ��� ���۳�Ʈ�� ��ġ�ϴ� ��� ��
		calSet.fill = GridBagConstraints.NONE;						//���� ä���� �ʰ�, �⺻ ũ��� ����(�⺻)
		//������Ʈ�� ��û�� ������� ������Ʈ�� ǥ�� ������ Ŭ ��� ������ ���濡 ���
		//�־��� ������ �ִ°�� ������ ������Ʈ�� ��� ä������� ������ �ش�.
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
		
		
		//Ķ���� ���
		calPanel = new JPanel();
		
		weekName = new JButton[7];							//���� ��ư
		for(int i = 0 ; i < CAL_WIDTH ; i++) {
			weekName[i] = new JButton(WEEK_NAME[i]);
			weekName[i].setBorderPainted(false);			//��ư �׵θ� ���ֱ�
			weekName[i].setContentAreaFilled(false);		//��ư ��� ����
			weekName[i].setForeground(Color.WHITE);			//�ؽ�Ʈ ����
			if(i==0)
				weekName[i].setBackground(new Color(200,110,110));		//�Ͽ��� - ������, color(r,g,b)
			else if(i == 6)
				weekName[i].setBackground(new Color(95,110,150));		//����� - �Ķ���, color(r,g,b)
			else
				weekName[i].setBackground(new Color(170,170,170));		//���� - ȸ��, color(r,g,b)
			weekName[i].setOpaque(true);
			//setOpaque(false)�� background�� ���ĵ��� 0���� ���� ����� �����ϰ� ����� �Լ�
			weekName[i].setFocusPainted(false);				//��ư ������ ��ư�� �׵θ��� ����� ���� ��������
			calPanel.add(weekName[i]);
		}
		
		for(int i = 0 ; i < CAL_HEIGHT ; i++) {				//�޷� ��¥ ��ư
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
		//��� ������ 2�� 7��¥�� GridLayout, �ʿ��� ��ŭ�� ��
		calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		//�׵θ��� ���빰�� ����(�� ���� �Ʒ� ������)
		showCal();				//�޷� �����ִ� �Լ�
		
		
		//���� �ð� �� ���õ� ��¥
		infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());
		
		infoClock = new JLabel("", SwingConstants.RIGHT);			//���� �ð�, ������ ����
		infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		infoPanel.add(infoClock, BorderLayout.NORTH);
		infoPanel.add(selectPanel, BorderLayout.CENTER);			//������ �ߴ� �޺��ڽ� panel
		
		selectedDate = new JLabel(today.get(Calendar.YEAR)+"/"+(today.get(Calendar.MONTH)+1)
									+"/"+today.get(Calendar.DAY_OF_MONTH)+" ��¥�� �������ּ���!", SwingConstants.LEFT);
		//���õ� ��¥, ���� ����
		selectedDate.setFont(new Font("",Font.BOLD,12));
		selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		
		
		//����, �޸� �� ������, ����� ��ư
		selBPanel = new JPanel();
		
		dDay = new JButton("����");
		dDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
		dDay.setForeground(Color.WHITE);
		dDay.setBackground(new Color(255, 190, 190));
		dDay.setBorderPainted(false);
		dDay.setToolTipText("���� �Է����� �Ѿ�ϴ�.");
		dDay.addActionListener(ForFunctionButs);
		
		memo = new JButton("�޸� �� ������");
		memo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		memo.setForeground(Color.WHITE);
		memo.setBackground(new Color(255, 211, 190));
		memo.setBorderPainted(false);
		memo.setToolTipText("������ �Է����� �Ѿ�ϴ�.");
		memo.addActionListener(ForFunctionButs);
		
		money = new JButton("�����");
		money.setCursor(new Cursor(Cursor.HAND_CURSOR));
		money.setForeground(Color.WHITE);
		money.setBackground(new Color(255, 220, 180));
		money.setBorderPainted(false);
		money.setToolTipText("����� �Է����� �Ѿ�ϴ�.");
		money.addActionListener(ForFunctionButs);
		
		selBPanel.setLayout(new FlowLayout());
		selBPanel.add(dDay);
		selBPanel.add(memo);
		selBPanel.add(money);

		infoPanel.add(selBPanel, BorderLayout.SOUTH);
		
		
		//�Է��� �� �ִ� panel, �ʱ� ȭ��(selBPanel���� ��ư ������ ��)
		memoPanel = new JPanel();
		memoPanel.setBorder(BorderFactory.createTitledBorder("!!!!���� ��ư�� �����ϰ� ���ϰ� �ۼ����ּ���>O<!!!"));
		//�׵θ� �����, ���� ����
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
		
		
		//���� ������ - panel ��ġ
		JPanel frameSubPanelWest = new JPanel();
		Dimension calSetPanelSize = calSetPanel.getPreferredSize();		//���� ������ ��ȯ
		calSetPanelSize.height = 90;									//���� ���� - 90
		calSetPanel.setPreferredSize(calSetPanelSize);					//calSetPanel ������ ����
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(calSetPanel,BorderLayout.NORTH);
		frameSubPanelWest.add(calPanel,BorderLayout.CENTER);
		
		JPanel frameSubPanelEast = new JPanel();
		frameSubPanelEast.setLayout(new BorderLayout());
		frameSubPanelEast.add(infoPanel,BorderLayout.NORTH);
		frameSubPanelEast.add(memoPanel,BorderLayout.CENTER);
				
		Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
		frameSubPanelWestSize.width = 440;								//�ʺ� ���� - 440
		frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);		//frameSubPanelWest ������ ����
		
		frameBottomPanel = new JPanel();
		frameBottomPanel.add(bottomInfo);
				
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(frameSubPanelWest, BorderLayout.WEST);
		mainFrame.add(frameSubPanelEast, BorderLayout.CENTER);
		mainFrame.add(frameBottomPanel, BorderLayout.SOUTH);	
		
		focusToday();			//���� ��¥ ǥ��
		
		//Thread �۵�(�ð�, bottomMsg �����ð��� ����)
		ThreadControl threadCnl = new ThreadControl();
		threadCnl.start();
	}
	private void focusToday(){				//���� ��¥�� ��ư�� ���� ��ó�� ǥ��
		dateButs[today.get(Calendar.WEEK_OF_MONTH)-1][today.get(Calendar.DAY_OF_WEEK)-1].requestFocusInWindow();
		//���� ��¥�� ��Ŀ��
	}
	private void showCal() {					//�޷� ����ϴ� �Լ� - ������, �����, ���� ��� ����
		String str = "��ü ����";
		
		for(int i = 0 ; i < CAL_HEIGHT ; i++) {
			for(int j = 0 ; j < CAL_WIDTH ; j++) {
				String fontColor = "black";
				if(j==0) fontColor = "red";
				else if(j==6) fontColor = "blue";
				
				//���� �����̳� �޸� ������ �ִ� ��� �ش� ��¥ �β��� ǥ���ϱ� ���� ���� ����
				File f1 = new File("dDayData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDate[i][j]<10?"0":"")+calDate[i][j]+".txt");
				File f2 = new File("MemoData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)+(calDate[i][j]<10?"0":"")+calDate[i][j]+".txt");
				
				//��� ������(����,������,��̳�,������,������,��õ��,�ѱ۳�,ũ��������)
				for(int m = 0 ; m < solarHoliday.length ; m++) {
					if((calMonth+1) == solarHoliday_Month[m] && calDate[i][j] == solarHoliday_Date[m]) {
						dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
								+"<br></b><font color=red><font size=1>"+solarHoliday[m]+"<br></font></html>");
						//���� �� ����� - �����ϰ� ��ġ�� �ű⿡ �߰������ �ϹǷ� if�� �ȿ� ����
						for(int k = 0 ; k < solarterm.length ; k++) {
							if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
								dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
										+"<br></b><font color=red><font size=1>"+solarHoliday[m]
												+"<br><font color=black>"+solarterm[k]+"</font></html>");
							}
						}
						break;
					}
					//��̳� ��ü������ - ��̳��� �Ͽ����� ���, ��ü������ 2014����� ����
					else if(j == 1 && (calMonth+1) == solarHoliday_Month[2] && calDate[i][0] == solarHoliday_Date[2] && calYear > 2013) {
						dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
								+"<br></b><font color=red><font size=1>"+str+"</font></br></html>");
						//���� �� �����
						for(int k = 0 ; k < solarterm.length ; k++) {
							if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
								dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
										+"<br></b><font color=red><font size=1>"+str
												+"<br><font color=black>"+solarterm[k]+"</font></html>");
							}
						}
						break;
					}
					//��̳� ��ü������ - ��̳��� ������� ���
					//��̳��� 5���̱� ������ ��̳��� ������̸� ��̳��� ������ ù°�� ������̰�, ��ü������ ������ ��°�� ������
					else if(i == 1 && j == 1 && (calMonth+1) == solarHoliday_Month[2] && calDate[0][6] == solarHoliday_Date[2] && calYear > 2013) {
						dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
								+"<br></b><font color=red><font size=1>"+str+"</font></br></html>");
						//���� �� �����
						for(int k = 0 ; k < solarterm.length ; k++) {
							if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
								dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
										+"<br></b><font color=red><font size=1>"+str
												+"<br><font color=black>"+solarterm[k]+"</font></html>");
							}
						}
						break;
					}
					
					//���� ������(����,��ó�Կ��ų�,�߼�) �� �θ�� ����
					for(int n = 0 ; n < lunarHoliday.length ; n++) {
						toSolar(n);			//������ ������� ��ȯ���ִ� �Լ�
						if(calYear == solarYear && (calMonth+1) == solarMonth[n] && calDate[i][j] == solarDay[n]) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+lunarHoliday[n]+"</font></html>");
							//���� ������ �������� �ƴϹǷ� ������ �۾���.
							if(calDate[i][j] == solarDay[1] || calDate[i][j] == solarDay[3])
								dateButs[i][j].setText("<html><b><font color=black><font size=4>"+calDate[i][j]
										+"<br></b><font color=black><font size=1>"+lunarHoliday[n]+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+lunarHoliday[n]
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//����, ��ü ����(2014����� ����)
						//�Ͽ����� �����̰ų� �߼��� ��� - �� �� �� 6��°��(�����)�� ����
						else if((j == 1) && (((calMonth+1) == solarMonth[0] && calDate[i][0] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i][0] == solarDay[4]))) {
							//(calMonth+1)�� ������� ��ȯ�� ���� �ް� ������ "����" ���
							String s1 = ((calMonth+1) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>����</html>";
							//����� ����
							dateButs[i-1][6].setText("<html><b><font color=red><font size=4>"+calDate[i-1][6]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//������ ����
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+s1
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//�Ͽ����� ���� �Ǵ� �߼��� ��� ȭ���� ��ü ����
						else if((j == 2) && (((calMonth+1) == solarMonth[0] && calDate[i][0] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i][0] == solarDay[4])) && calYear > 2013) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+str+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+str
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}						
							break;
						}
						//������� �����̰ų� �߼��� ��� - ���� �Ͽ��Ϸ� �� �� �� �ݿ��ϵ� ���ް� �Ǿ����
						else if((i != 0 && j == 0) && (((calMonth+1) == solarMonth[0] && calDate[i-1][6] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i-1][6] == solarDay[4]))) {
							String s1 = ((calMonth+1) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>����</html>";
							//�� �� �� �ݿ���
							dateButs[i-1][5].setText("<html><b><font color=red><font size=4>"+calDate[i-1][5]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//�Ͽ���
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red<font size=1>"+s1+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+s1
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//������� �����̰ų� �߼��� ��� ������ ��ü ����
						else if((i != 0 && j == 1) && (((calMonth+1) == solarMonth[0] && calDate[i-1][6] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i-1][6] == solarDay[4])) && calYear > 2013) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+str+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+str
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//������ �����̰ų� �߼��� ���
						else if((j != 0 && j != 1) && (((calMonth+1) == solarMonth[0] && calDate[i][j-1] == solarDay[0])
								|| ((calMonth+1) == solarMonth[4] && calDate[i][j-1] == solarDay[4]))) {
							String s1 = ((calMonth+1) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>����</html>";
							dateButs[i][j-2].setText("<html><b><font color=red><font size=4>"+calDate[i][j-2]
									+"<br></b><font color=red><font size=1>"+s1+"</font></b></html>");
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+s1
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//�������� ���� �Ǵ� �߼��� ��� ������ ��ü ����
						else if((j == 3) && (((calMonth+1) == solarMonth[0] && calDate[i][1] == solarDay[0])
										|| ((calMonth+1) == solarMonth[4] && calDate[i][1] == solarDay[4])) && calYear > 2013) {
							dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
									+"<br></b><font color=red><font size=1>"+str+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k]) {
									dateButs[i][j].setText("<html><b><font color=red><font size=4>"+calDate[i][j]
											+"<br></b><font color=red><font size=1>"+str
													+"<br><font color=black>"+solarterm[k]+"</font></html>");
								}
							}
							break;
						}
						//���� ������ �ִ� ��� �ش� ��¥ �β���
						else if(f1.exists()){
							dateButs[i][j].setText("<html><b><font color="+fontColor+">"+calDate[i][j]+"</font></b></html>");
							break;
						}
						//�޸� ������ �ִ� ��� �ش� ��¥ �β���
						else if(f2.exists()){
							dateButs[i][j].setText("<html><b><font color="+fontColor+">"+calDate[i][j]+"</font></b></html>");
							break;
						}
						//�ƹ� ���� �ƴ� ���
						else {
							dateButs[i][j].setText("<html><font color="+fontColor+">"+calDate[i][j]+"</font></html>");
							//���� �� �����
							for(int k = 0 ; k < solarterm.length ; k++) {
								if((calMonth+1) == solarterm_Month[k] && calDate[i][j] == solarterm_Day[k] ) {
									dateButs[i][j].setText("<html><b><font color="+fontColor+"><font size=4>"+calDate[i][j]
											+"</b>"+"<br><font color=black><font size=1>"+solarterm[k]+"</font></html>");
								}
							}
						}
					}
				}
				//�����̳� �߼��� 1���� ��� �� �� �� ������ ���� ���޿�����.
				int count = 0, cnt = 0;
				for(int n = 4 ; n < CAL_HEIGHT ; n++) {		//������ �� ���ϱ� ���ؼ�.
					if(calDate[n][0] == 0) {				//5°�ְ� ���µ� for������ n�� 5�� �Ǿ� cnt�� 5�� �Ǵ� �� ����
						cnt = 4;
						break;
					}
					for(int m = 0 ; m < CAL_WIDTH ; m++) {	//������ �� ���ϱ� ���ؼ�.
						if(calDate[n][m] != 0) {			//calDate�� 0�� �Ǳ� ���� ������ ��
							count = m;
						}
						else {								//calDate�� 0�� �Ǹ� �� ���� ������ ��
							cnt = n;
							break;
						}
					}
				}
				if(((calMonth+2) == solarMonth[0] && solarDay[0] == 1) || ((calMonth+2) == solarMonth[4] && solarDay[4] == 1)) {
					//���� ��(calMonth+2)�� ������� ��ȯ�� ������ �ް� ������ "����" ���
					String s1 = ((calMonth+2) == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>����</html>";
					dateButs[cnt][count].setText("<html><b><font color=red><font size=4>"+calDate[cnt][count]
							+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
					//���� �� �����
					for(int k = 0 ; k < solarterm.length ; k++) {
						if((calMonth+1) == solarterm_Month[k] && calDate[cnt][count] == solarterm_Day[k]) {
							dateButs[cnt][count].setText("<html><b><font color=red><font size=4>"+calDate[cnt][count]
									+"<br></b><font color=red><font size=1>"+s1
											+"<br><font color=black>"+solarterm[k]+"</font></html>");
						}
					}
				}
				
				//�����̳� �߼��� �� ���� ������ ���� ��� ���� �� 1���� ���޿�����.
				if((calMonth == solarMonth[0] && solarDay[0] == calLastDateOfMonth[calMonth-1]) 
									|| (calMonth == solarMonth[4] && solarDay[4] == calLastDateOfMonth[calMonth-1])) {
					//���� ��(calMonth)�� ������� ��ȯ�� ������ �ް� ������ "����" ���
					String s1 = (calMonth == solarMonth[0] ? lunarHoliday[0] : lunarHoliday[4]) + "<html><br>����</html>";
					dateButs[0][calStart].setText("<html><b><font color=red><font size=4>"+calDate[0][calStart]
							+"<br></b><font color=red><font size=1>"+s1+"</font></html>");
					//���� �� �����
					for(int k = 0 ; k < solarterm.length ; k++) {
						if((calMonth+1) == solarterm_Month[k] && calDate[cnt][count] == solarterm_Day[k]) {
							dateButs[0][calStart].setText("<html><b><font color=red><font size=4>"+calDate[0][calStart]
									+"<br></b><font color=red><font size=1>"+s1
											+"<br><font color=black>"+solarterm[k]+"</font></html>");
						}
					}
				}
				
				JLabel todayMark = new JLabel("<html><font color=green>*</html>");
				dateButs[i][j].removeAll();		//���ϸ� �ٸ� ���� �Ȱ��� ��ġ�� ��ư������ todayMark ��µ�
				if(calMonth == today.get(Calendar.MONTH)&&calYear == today.get(Calendar.YEAR)
							&&calDate[i][j] == today.get(Calendar.DAY_OF_MONTH)) {
					dateButs[i][j].add(todayMark);		//���� ��¥ �տ� *���̱�
				}
				if(calDate[i][j] == 0)
					dateButs[i][j].setVisible(false);				//�迭 ���� 0 �������� �޷� â�� �ȳ�Ÿ���� �ϱ�(0�� ���x)
				else
					dateButs[i][j].setVisible(true);
			}
		}
	}
	//�޺� �ڽ� �̵� ��ư �̺�Ʈ
	private class ListenForSelectBut implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == selectBut) {
				calYear = selectedYear.getSelectedIndex() + 1901;		//index�� 0���� �����ϹǷ� +1901 �������.
				calMonth = selectedMonth.getSelectedIndex();			//calMonth�� 0���� �����ϹǷ� +1 �����൵ ��.
				cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
				makeCalData(cal);			//�޷� �迭 �����
				cur.setText("<html><table width=100><th><font size=5>"+((calMonth+1)<10 ? "0":"")
						+(calMonth+1)+" / "+calYear+"</th></table></html>");
				//calMonth�� �� �����̸�(10������ ���� ��) �տ� "0" �߰�
				showCal();					//�޷� ���
			}
		}
	}
	//Today << < > >> ��ư �̺�Ʈ
	private class ListenForCalSetButtons implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == todayBut) {					//today��ư ������ ��
				setToday();									//���� ��¥
				ForDateButs.actionPerformed(e);				//today��ư ������ �� selectedDate�� ����Ǿ���ϱ� ������.
				focusToday();								//���� ��¥�� ��Ŀ��
			}
			else if(e.getSource() == lYear)					//'<<'��ư ������ ��
				moveMonth(-12);
			else if(e.getSource() == lMonth)				//'<'��ư ������ ��
				moveMonth(-1);
			else if(e.getSource() == rMonth)				//'>'��ư ������ ��
				moveMonth(1);
			else if(e.getSource() == rYear)					//'>>'��ư ������ ��
				moveMonth(12);
			
			cur.setText("<html><table width=100><th><font size=5>"+((calMonth+1)<10 ? "0":"")
								+(calMonth+1)+" / "+calYear+"</th></table></html>");
			//calMonth�� �� �����̸�(10������ ���� ��) �տ� "0" �߰�
			showCal();
		}
	}
	//�޷� ��ư, today ��ư �̺�Ʈ
	private class ListenForDatesButs implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int k = 0, l = 0;
			for(int i = 0 ; i< CAL_HEIGHT ; i++) {
				for(int j = 0 ; j < CAL_WIDTH ; j++) {
					if(e.getSource() == dateButs[i][j]) {		//�޷� ��ư ������ ��
						k = i;
						l = j;
					}
				}
			}
			if(e.getSource() != todayBut)				//today��ư�� ������ ����Ǹ� k=0, l=0���� �ǹǷ� ����
				calDayOfMon = calDate[k][l];
			cal = new GregorianCalendar(calYear,calMonth,calDayOfMon);
			selectedDate.setText(calYear+"/"+(calMonth+1)+"/"+calDayOfMon);
		}
	}
	//����, �޸� �� ������, ����� ��ư �̺�Ʈ
	private class ListenForFunctionButs implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == dDay) {               //���� ��ư ������ ��
		            memoPanel.removeAll();			  //�ʱ�ȭ. ���ϸ� ��� ���� �������� �������.	
		            memoPanel.setBackground(new Color(255,250,215));
		            memoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));      //�׵θ� �簢����.
		            
		            memoBackPanel = new JPanel();
		            memoBackPanel.setOpaque(false);
		            
		            dDaySel = new JPanel();
		            dDaySel.setOpaque(false);         //�����ϰ� �������.
		            
		            all = new JPanel();
		            all.setOpaque(false);
		            all.setLayout(new BorderLayout());
		            all.add(selectedDate, BorderLayout.NORTH);
		            
		            dDayBut = new JRadioButton[5];					//���� - ���� ��ư
		            ButtonGroup dDayGroup = new ButtonGroup();		//�ߺ� üũ �ȵǰ� �׷����� ����
		            String dDayType[] = {"����","�����","����","����","��Ÿ"};
		            for(int i = 0 ; i < dDayType.length ; i++) {
		               dDayBut[i] = new JRadioButton();
		               dDayBut[i].setText(dDayType[i]);
		               dDayBut[i].setBackground(new Color(255,250,215));
		               dDayBut[i].setBorderPainted(false);
		               dDayGroup.add(dDayBut[i]);
		               dDaySel.add(dDayBut[i]);
		            }
		            all.add(dDaySel, BorderLayout.CENTER);
		            
		            dDayName = new JLabel("�̸� : ");
		            dDayNameTxt = new JTextField(15);
		            
		            dDayTxt = new JPanel();
		            dDayTxt.setOpaque(false);
		            
		            dDayTxt.add(dDayName);
		            dDayTxt.add(dDayNameTxt);

		            final DefaultListModel<String> dDayList = new DefaultListModel<>();
		            //������Ʈ�� �����͸� �߰� Ȥ�� �����ϱ� ���ؼ� ���� �ʿ�
		            
		            JScrollPane scrollList = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		            //���� ��ũ�Ѹ�. ���� ��ũ���� x
		            
		            final JList<String> ls = new JList<>(dDayList);			//���̸� �ֱ� ���� JList ����
		            ls.setPreferredSize(new Dimension(300,180));			//ũ�� ����
		            scrollList.setPreferredSize(new Dimension(300,160));
		            
		            JPanel list = new JPanel();
		            list.setOpaque(false);
		            
		            scrollList.setViewportView(ls);							//JList�� ��ũ�� �߰�
		            list.add(scrollList);
		            
		            JLabel help1 = new JLabel("1.���̸� �Է��� ��¥�� �����ϼ���(�Է��� ������).");
		            JLabel help1_2 = new JLabel("(���� ��¥�� �Է��� ���� ���� ��¥�� �����ּ���.)");
		            JLabel help2 = new JLabel("2.���� ���� : ������ ��¥�� ���� -> �ش� ���̸� ���� -> ����");
		            JLabel help3 = new JLabel("3.�Ϸ翡 2�� �̻��� ���̰� ����Ǿ��ִµ� �ϳ��� �����Ѵٸ�");
		            JLabel help3_2= new JLabel("  �� ���� ���� ������ ��� ���󰡹Ƿ� �������ּ���.");
		            JLabel help4 = new JLabel("4.����,����,����� ��� ���ͷ� �Է� �����մϴ�(save���).");
		            list.add(help1);
		            list.add(help1_2);
		            list.add(help2);
		            list.add(help3);
		            list.add(help3_2);
		            list.add(help4);
		            
		            //���ٰ� ���� �� ������ ������ ���� ���� �߰�
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
		            
		            dDayNameTxt.addActionListener(new ActionListener() {			//���ͷ� �Է� ����
		            	public void actionPerformed(ActionEvent e) {
		            		String dDayStr = new String();
			                int dDayVal = ((int)((cal.getTimeInMillis() - today.getTimeInMillis())/1000/60/60/24));
			                //getTimeInMillis()�� ���� ��¥�� �ð��� �и��ʷ� �ٲ㼭 ��ȯ�� ��.
			                
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
			                    if(dDayBut[index].isSelected())		//���õ� ���� ��ư�� index ��ȯ
			                        break;
			                }
			                
			                String name = dDayNameTxt.getText();
		            		if(name.length() <= 0)
		            			bottomInfo.setText("<html><font color = red>�̸��� �ۼ����ּ���.</font></html>");
		            		else {
		            			 try {
			                    	 File f= new File("dDayData");
			                    	 if(!f.isDirectory()) f.mkdir();		//�ش� ������ ������ ���� ����
			                    	 
			                    	 String str = "<html><b><font color = green>["+dDayBut[index].getText()+"]&nbsp;</font>"
			                                    +selectedDate.getText() +"&nbsp;</b>"
			                                    +name+" : <b>"+dDayStr+"</html>";
			                    	 dDayList.addElement(str);			//JList�� �߰�
			                    	 
				                	 BufferedWriter bw = new BufferedWriter(new FileWriter("dDayData/"+calYear+((calMonth+1)<10?"0":"")
		              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt",true));
				                	 //FileWriter���� true�� ������ ������ ������ ����� ������ ���� ������ ���󰣴�.
				                	 PrintWriter pw = new PrintWriter(bw, true);
				                	 //������ ������ �ڿ� �̾ ����.
				                	 String saveStr = "<html><b><font color = green>["+dDayBut[index].getText()+"]&nbsp;</font>"
			                                    +selectedDate.getText() +"&nbsp;</b>"
			                                    +name+" : <b>"+dDayStr+"</html>";
				                	 //���� ������ ���õ� ��¥, �̸�, ���� �Է�
				                	 pw.write(saveStr+"\n");	//���Ͽ� �߰�
				                	 pw.close();
				                  }
				                  catch(IOException ex) {
				                  }
			                      bottomInfo.setText("���̸� �����Ͽ����ϴ�.");
			                      dDayNameTxt.setText("");		//�ʱ�ȭ
			                      showCal();
		            		}
		            	}
		            });
		            
		            saveBut = new JButton("SAVE");
		            saveBut.setBackground(new Color(255,220,196));
		            saveBut.setBorderPainted(false);
		            saveBut.setToolTipText("���̸� �����մϴ�.");
		            saveBut.addActionListener(new ActionListener() {			//save��ư ������ ��, ���ͷ� �Է����� ���� ����
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
		                  
		                  if(index == dDayType.length)			//���� ��ư ���� ������ ��.
		                     bottomInfo.setText("<html><font color = red>������ �������ּ���.</font></html>");
		                  else if(name.length() <= 0)
		                     bottomInfo.setText("<html><font color = red>�̸��� �ۼ����ּ���.</font></html>");
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
			                	 //FileWriter���� true�� ������ ������ ������ ����� ������ ���� ������ ���󰣴�.
			                	 PrintWriter pw = new PrintWriter(bw, true);
			                	 //������ ������ �ڿ� �̾ ����.
			                	 String saveStr = "<html><b><font color = green>["+dDayBut[index].getText()+"]&nbsp;</font>"
		                                    +selectedDate.getText() +"&nbsp;</b>"
		                                    +name+" : <b>"+dDayStr+"</html>\n";
			                	 pw.write(saveStr);		//���Ͽ� �߰�
			                	 pw.close();
			                  }
			                  catch(IOException ex) {
			                  }
		                     bottomInfo.setText("���̸� �����Ͽ����ϴ�.");
		                     dDayNameTxt.setText("");		//�ʱ�ȭ
		                     showCal();
		                  }
		               }
		            });
		            
		            delBut = new JButton("DELETE");
		            delBut.setBackground(new Color(255,220,196));
		            delBut.setBorderPainted(false);
		            delBut.setToolTipText("������ ���̸� �����մϴ�.");
		            delBut.addActionListener(new ActionListener() {			//delete��ư ������ ��
		               public void actionPerformed(ActionEvent e) {
		            	  dDayNameTxt.setText("");		//�ʱ�ȭ
		            	  
		                  int index = ls.getSelectedIndex();
		                  if(index < 0)					//JList ���� �ȵǾ��� ��
		                	  bottomInfo.setText("<html><font color = red>������ ���̰� �����ϴ�.</font></html>");
		                  else {
		                	  File f1 =new File("dDayData/"+calYear+((calMonth+1)<10?"0":"")+(calMonth+1)
		                			  				+(calDayOfMon<10?"0":"")+calDayOfMon+".txt");
		                	  if(f1.exists()) {
		                		  f1.delete();
		                		  showCal();
		                	  }
		                	  dDayList.remove(index);	//�ش� ���� �����
		                	  bottomInfo.setText("������ ���̸� �����Ͽ����ϴ�.");
		                  }
		               }
		            });
		            
		            clearBut = new JButton("CLEAR");
		            clearBut.setBackground(new Color(255,220,196));
		            clearBut.setBorderPainted(false);
		            clearBut.setToolTipText("��� ���̸� �����մϴ�.");
		            clearBut.addActionListener(new ActionListener() {		//clear��ư ������ ��
		               public void actionPerformed(ActionEvent e) {
		            	   dDayNameTxt.setText("");		//�ʱ�ȭ
		            	   
		            	   if(dDayList.getSize() == 0)		//JList ������ִ� ���
		            		   bottomInfo.setText("<html><font color = red>���� ����� �̹� ����� �ֽ��ϴ�.</font></html>");
		            	   else {
		            		   File f1 = new File("dDayData");
						       if(f1.exists()) {
						    	   File[] deleteFolderList = f1.listFiles();
						    	   for(int j = 0 ; j < deleteFolderList.length ; j++) {
						    		   deleteFolderList[j].delete();		//���� ���� ����
						    	   }
						    	   showCal();
						    	   bottomInfo.setText("��� ���̸� �����Ͽ����ϴ�.");
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
			
			else if(e.getSource() == memo) {				//�޸� �� ������
				memoPanel.removeAll();
				memoPanel.setBackground(new Color(255,250,215));
				memoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));		//�׵θ� �簢����.
				
				memoBackPanel = new JPanel();
				memoBackPanel.setOpaque(false);
				
				all = new JPanel();
				all.setOpaque(false);
				all.setLayout(new BorderLayout());
				all.add(selectedDate, BorderLayout.NORTH);
				
				JLabel info = new JLabel("<html><b><font size = 4><font color=gray>��Todo List��&nbsp;</b></font></html>", SwingConstants.CENTER);
				info.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
				all.add(info, BorderLayout.CENTER);
				
				JPanel todoList = new JPanel();
				todoList.setOpaque(false);
				JLabel input = new JLabel("<�� ��>", SwingConstants.CENTER);
				JTextField tf = new JTextField(32);
				tf.setHorizontalAlignment(JTextField.CENTER);			//�ؽ�Ʈ �ʵ� �ؽ�Ʈ ��� ����
				
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
	            
	            JLabel help1 = new JLabel("1.���� �� �޸� �Է��� ��¥�� �����ϼ���.");
	            JLabel help1_2 = new JLabel("(���� ��¥�� �Է��� ���� ���� ��¥�� �����ּ���.)");
	            JLabel help2 = new JLabel("2.���� ���� : ������ ��¥�� ���� -> �ش� ���� ���� -> ����");
	            JLabel help3 = new JLabel("3.�Ϸ翡 2�� �̻��� ������ ����Ǿ��ִµ� �ϳ��� �����Ѵٸ�");
	            JLabel help3_2= new JLabel("  �� ���� ���� ������ ��� ���󰡹Ƿ� �������ּ���.");
	           
	            list.add(help1);
	            list.add(help1_2);
	            list.add(help2);
	            list.add(help3);
	            list.add(help3_2);
	            
	            //���ٰ� �ٽ� �������� �� ������ ������ ǥ���ϱ� ����.
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
	            
	            tf.addActionListener(new ActionListener() {					//���� ������ �߰�
	            	public void actionPerformed(ActionEvent e) {
	            		if(tf.getText().length() <= 0)						//�ƹ��͵� �Է��� �ȵǾ� �ִ� ���
            				bottomInfo.setText("<html><font color = red>�����̳� �޸� ���� �ۼ����ּ���.</font></html>");
	            		else {
		            		try {
		            			File f = new File("MemoData");
		            			if(!f.isDirectory()) f.mkdir();				//���� ������ ���� �����
		            			
		            			BufferedWriter bw = new BufferedWriter(new FileWriter("MemoData/"+calYear+((calMonth+1)<10?"0":"")
	              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt",true));
			                	//FileWriter���� true�� ������ ������ ������ ����� ������ ���� ������ ���󰣴�.
			                	PrintWriter pw = new PrintWriter(bw, true);
			                	//������ ������ �ڿ� �̾ ����.
			                	
			                	memoList.addElement(selectedDate.getText()+" : "+tf.getText());	//���õ� ��¥�� �Էµ� �޸� �߰�
			                	pw.write(selectedDate.getText()+" : "+tf.getText()+"\n");		//���Ͽ� �߰�
			                	pw.close();
		            		}
		            		catch(IOException ex) {	
		            		}
		            		tf.setText("");			//�ʱ�ȭ
		            		showCal();
		            	}
	            	}
	            });
	            
				memoSubPanel = new JPanel();
				saveBut = new JButton("SAVE");
				saveBut.setBackground(new Color(255,220,196));
				saveBut.setBorderPainted(false);
				saveBut.setToolTipText("���� �� �޸� �����մϴ�.");
				saveBut.addActionListener(new ActionListener() {		//save��ư ������ ��. ���ͷ� �Է��� ���� ����
	            	public void actionPerformed(ActionEvent e) {
	            		if(tf.getText().length() <= 0)
            				bottomInfo.setText("<html><font color = red>�����̳� �޸� ���� �ۼ����ּ���.</font></html>");
	            		else {
		            		try {
		            			File f = new File("MemoData");
		            			if(!f.isDirectory()) f.mkdir();
		            			
		            			BufferedWriter bw = new BufferedWriter(new FileWriter("MemoData/"+calYear+((calMonth+1)<10?"0":"")
	              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt",true));
			                	//FileWriter���� true�� ������ ������ ������ ����� ������ ���� ������ ���󰣴�.
			                	PrintWriter pw = new PrintWriter(bw, true);
			                	pw.write(selectedDate.getText()+" : "+tf.getText());		//���Ͽ� �߰�
			                	memoList.addElement(selectedDate.getText()+" : "+tf.getText());
			                	pw.close();
		            		}
		            		catch(IOException ex) {	
		            		}
		            		bottomInfo.setText("������ �����Ͽ����ϴ�.");
		            		tf.setText("");		//�ʱ�ȭ
		            		showCal();
	            		}
	            	}
	            });
				
				delBut = new JButton("DELETE");
				delBut.setBackground(new Color(255,220,196));
				delBut.setBorderPainted(false);
				delBut.setToolTipText("������ ���� �� �޸� �����մϴ�.");
				delBut.addActionListener(new ActionListener() {				//delete ��ư ������ ��
					public void actionPerformed(ActionEvent e) {
						int index = ls.getSelectedIndex();
						if(index < 0)		//JList���� ������ �ȵǾ����� ��
							bottomInfo.setText("<html><font color = red>���õ� ���� �� �޸� �����ϴ�.</font></html>");
						
						else {
							File f = new File("MemoData/"+calYear+((calMonth+1)<10?"0":"")
	              		  				+(calMonth+1)+(calDayOfMon<10?"0":"")+calDayOfMon+".txt");
							if(f.exists()) {
								f.delete();				//���� ����
								showCal();
							}
							memoList.remove(index);		//JList���� ���õ� ���� ����
							bottomInfo.setText("������ ���� �� �޸� �����Ͽ����ϴ�.");
						}
					}
				});
				
				clearBut = new JButton("CLEAR");
				clearBut.setBackground(new Color(255,220,196));
				clearBut.setBorderPainted(false);
				clearBut.setToolTipText("��� ���� �� �޸� �����մϴ�.");
				clearBut.addActionListener(new ActionListener() {			//clear ��ư ������ ��
					public void actionPerformed(ActionEvent e) {
						if(memoList.getSize() == 0)			//JList�� �̹� �ƹ��͵� ���� ��
							bottomInfo.setText("<html><font color = red>���� ����� �̹� ����� �ֽ��ϴ�.</font></html>");
						
						else {
							File f = new File("MemoData");
							if(f.exists()) {
								File[] deleteFolderList = f.listFiles();	//���� �ȿ� �ִ� ���ϵ�
								for(int i = 0 ; i < deleteFolderList.length ; i++)
									deleteFolderList[i].delete();			//��� ���� ����
								showCal();
								bottomInfo.setText("��� ���̸� �����Ͽ����ϴ�.");
							}
							memoList.removeAllElements();					//JList �� �����
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
			else if(e.getSource() == money) {				// �����
				memoPanel.removeAll();
				memoPanel.setBackground(new Color(255,250,215));
				memoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));		//�׵θ� �簢����.
				
				memoBackPanel = new JPanel();
				memoBackPanel.setOpaque(false);
				memoBackPanel.setLayout(new BorderLayout());
				
				all = new JPanel();
				all.setOpaque(false);
				all.setLayout(new BorderLayout());
				all.add(selectedDate, BorderLayout.NORTH);
				
				JPanel moneySel = new JPanel();
				moneySel.setOpaque(false);
				
				moneyBut = new JRadioButton[2];					//����, ���� �����ϴ� ���� ��ư
				ButtonGroup moneyGroup = new ButtonGroup();		//�ߺ� ���� �ȵǰ� �׷� ����
				String moneyType[] = {"����","����"};
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
				
				JLabel beginSum = new JLabel("���� �ݾ� : ");
				JTextField beginSumTxt = new JTextField(5);
				beginSumTxt.setHorizontalAlignment(JTextField.CENTER);
				
				JLabel use = new JLabel("�̿� ���� : ");
				JTextField useTxt = new JTextField(5);
				useTxt.setHorizontalAlignment(JTextField.CENTER);
				
				JLabel price = new JLabel("       �ݾ� : ");
				JTextField priceTxt = new JTextField(5);
				priceTxt.setHorizontalAlignment(JTextField.CENTER);
				
				Box beginBox = Box.createHorizontalBox();			//���� �������� ������Ʈ�� ����
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
				
				mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));		//���� ����
				mainPanel.add(beginBox);
				mainPanel.add(useBox);
				mainPanel.add(priceBox);
				
				JLabel resultSum = new JLabel("���� �ݾ� : ", SwingConstants.CENTER);
				JLabel resultSumTxt = new JLabel();
				
				JLabel resultOutput = new JLabel("�� ���� : ", SwingConstants.CENTER);
				JLabel resultOutputTxt = new JLabel("0");
				
				JLabel resultInput = new JLabel("�� ���� : ", SwingConstants.CENTER);
				JLabel resultInputTxt = new JLabel("0");
				
				Box resultBox = Box.createHorizontalBox();				//���� �������� ������Ʈ�� ����
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
				JLabel help1 = new JLabel("1.ó�� ����ϽŴٸ� ���� �ݾ�(���� �ݾ�)�� �Է����ּž��մϴ�.");
	            JLabel help1_2 = new JLabel("(�� �� �Է��ϰ� ����θ� ����ϸ� ���� �ݾ��� ������ ����.)");
	            JLabel help2 = new JLabel("2.��¥�� ������ �� ������ ���� �Է����ּ���.");
	            JLabel help3 = new JLabel("3.DELETE�� �ٷ� �� �ݾ����� ���ư��� ����Դϴ�(���� 2���� �ȵ�).");
	            JLabel help4= new JLabel("4.CLEAR�� ���� �ݾ��� RESET�ϴ� ����Դϴ�.");
	            helpPanel.add(help1);
	            helpPanel.add(help1_2);
	            helpPanel.add(help2);
	            helpPanel.add(help3);
	            helpPanel.add(help4);
	            helpPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
	            memoBackPanel.add(helpPanel, BorderLayout.CENTER);
				
	            JLabel temp = new JLabel();				//�ӽ������� �� ����, delete�ϸ� �� �� ������ ���ư��� ���ؼ� ����� ����.
	            JLabel temp1 = new JLabel("0");
	            JLabel temp2 = new JLabel("0");
	            
	            //�ٽ� �������� �� ������ �ִ� ��� ǥ���ϱ� ����
	            try {
	            	File f = new File("moneyData/money.txt");
	            	if(f.exists()) {
	            		String BufferedStr = null;
	            		int i = 0;
	            		
	            		BufferedReader in = new BufferedReader(new FileReader(f));
	            		
	            		while((BufferedStr = in.readLine()) != null) {
	            			if(i == 0) {			//ù ��° ���� ���� �ݾ�
	            				beginSumTxt.setText(BufferedStr);
	            				resultSumTxt.setText(BufferedStr + "��");
	            			}
	            			if(i == 1) {			//�� ��° ���� �� ����
	            				resultOutputTxt.setText(BufferedStr);
	            			}
	            			if(i == 2) {			//�� ��° ���� �� ����
	            				resultInputTxt.setText(BufferedStr);
	            			}
	            			i++;
	            		}
	            		in.close();
	            	}
	            }
	            catch(IOException ex) {
	            }
	            
	            priceTxt.addActionListener(new ActionListener() {					//���ͷ� �Է� ����
	            	public void actionPerformed(ActionEvent e) {
	            		int index;
	            		for(index = 0 ; index < moneyType.length ; index++) {
	            			if(moneyBut[index].isSelected())		//���õ� ���� ��ư�� index ��ȯ
	            				break;
	            		}
	            		
	            		if(index == moneyType.length)		//���� ��ư ���� ������ ��
	            			bottomInfo.setText("<html><font color=red>����, ������ ����ּ���.</font></html>");
	            		else if(beginSumTxt.getText().length() <= 0||useTxt.getText().length() <= 0||priceTxt.getText().length() <= 0)
	            			//�ؽ�Ʈ �ʵ忡 �ƹ� ���� �ԷµǾ����� ���� ���
	            			bottomInfo.setText("<html><font color=red>��ĭ�� �Է����ּ���.</font></html>");
	            		else if(isNumeric(beginSumTxt.getText()) == false || isNumeric(priceTxt.getText()) == false) {
	            			//���� �ݾ��̳� �ݾ��� ���ڰ� �ƴѰ��
	            			bottomInfo.setText("<html><font color=red>���ڸ� �Է����ּ���.</font></html>");
	            			if(isNumeric(beginSumTxt.getText()) == false)
	            				beginSumTxt.setText("");		//�ʱ�ȭ
	            			if(isNumeric(priceTxt.getText()) == false)
	            				priceTxt.setText("");		//�ʱ�ȭ
	            		}
	            		else {
	            			try {
	            				File f = new File("moneyData");
	            				if(!f.isDirectory()) f.mkdir();			//������ ������ ���� ����
	            				
	            				BufferedWriter bw = new BufferedWriter(new FileWriter("moneyData/money.txt"));

	            				if(index == 0) {		//������ ���
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//�����̳� ������ �ԷµǱ� �� �� �ӽ� ����
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin - price;
	            					bw.write(Integer.toString(begin)+"\n");				//���� �ݾ� ���Ͽ� ����
	            					
	            					temp1.setText(resultOutputTxt.getText());			//�� ���� �� �ӽ� ����
	            					temp2.setText(resultInputTxt.getText());			//�� ���� �� �ӽ� ����
	            					
	            					int resultPrice = Integer.parseInt(resultOutputTxt.getText()) + price;
	            					resultOutputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(Integer.toString(resultPrice)+"\n");		//�� ���� �� ���Ͽ� ����
	            					bw.write(resultInputTxt.getText()+"\n");			//�� ���� �� ���Ͽ� ����
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "��");
	            					bottomInfo.setText("["+priceTxt.getText()+"�� ����] �����Ͽ����ϴ�.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//�ʱ�ȭ
	            					priceTxt.setText("");		//�ʱ�ȭ
	            				}
	            				else if(index == 1) {
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//�����̳� ������ �ԷµǱ� �� �� �ӽ� ����
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin + price;
	            					bw.write(Integer.toString(begin)+"\n");				//���� �ݾ� ���Ͽ� ����
	            					
	            					temp1.setText(resultOutputTxt.getText());			//�� ���� �� �ӽ� ����
	            					temp2.setText(resultInputTxt.getText());			//�� ���� �� �ӽ� ����
	            					
	            					int resultPrice = Integer.parseInt(resultInputTxt.getText()) + price;
	            					resultInputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(resultOutputTxt.getText()+"\n");			//�� ���� �� ���Ͽ� ����
	            					bw.write(Integer.toString(resultPrice)+"\n");		//�� ���� �� ���Ͽ� ����
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "��");
	            					bottomInfo.setText("["+priceTxt.getText()+"�� ����] �����Ͽ����ϴ�.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//�ʱ�ȭ
	            					priceTxt.setText("");		//�ʱ�ȭ
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
				saveBut.setToolTipText("����θ� �����մϴ�.");
				saveBut.addActionListener(new ActionListener() {			//save��ư�� ������ ��. ���ͷ� �Է����� ���� ����
	            	public void actionPerformed(ActionEvent e) {
	            		int index;
	            		for(index = 0 ; index < moneyType.length ; index++) {
	            			if(moneyBut[index].isSelected())
	            				break;
	            		}
	            		
	            		if(index == moneyType.length)
	            			bottomInfo.setText("<html><font color=red>����, ������ ����ּ���.</font></html>");
	            		else if(beginSumTxt.getText().length() <= 0||useTxt.getText().length() <= 0||priceTxt.getText().length() <= 0)
	            			bottomInfo.setText("<html><font color=red>��ĭ�� �Է����ּ���.</font></html>");
	            		else if(isNumeric(beginSumTxt.getText()) == false || isNumeric(priceTxt.getText()) == false) {
	            			bottomInfo.setText("<html><font color=red>���ڸ� �Է����ּ���.</font></html>");
	            			if(isNumeric(beginSumTxt.getText()) == false)
	            				beginSumTxt.setText("");		//�ʱ�ȭ
	            			if(isNumeric(priceTxt.getText()) == false)
	            				priceTxt.setText("");			//�ʱ�ȭ
	            		}
	            		
	            		else {
	            			try {
	            				File f = new File("moneyData");
	            				if(!f.isDirectory()) f.mkdir();		//������ ���� ��� ���� ����
	            				
	            				BufferedWriter bw = new BufferedWriter(new FileWriter("moneyData/money.txt"));

	            				if(index == 0) {		//������ ���
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//�����̳� ������ �ԷµǱ� �� �� �ӽ� ����
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin - price;
	            					bw.write(Integer.toString(begin)+"\n");			//���� �ݾ� ���Ͽ� ����
	            					
	            					temp1.setText(resultOutputTxt.getText());		//�� ���� �� �ӽ� ����
	            					temp2.setText(resultInputTxt.getText());		//�� ���� �� �ӽ� ����
	            					
	            					int resultPrice = Integer.parseInt(resultOutputTxt.getText()) + price;
	            					resultOutputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(Integer.toString(resultPrice)+"\n");	//�� ���� �� ���Ͽ� ����
	            					bw.write(resultInputTxt.getText()+"\n");		//�� ���� �� ���Ͽ� ����
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "��");
	            					bottomInfo.setText("["+priceTxt.getText()+"�� ����] �����Ͽ����ϴ�.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//�ʱ�ȭ
	            					priceTxt.setText("");		//�ʱ�ȭ
	            				}
	            				else if(index == 1) {
	            					int begin = Integer.parseInt(beginSumTxt.getText());
	            					temp.setText(Integer.toString(begin));	 			//�����̳� ������ �ԷµǱ� �� �� �ӽ� ����
	            					int price = Integer.parseInt(priceTxt.getText());
	            					begin = begin + price;
	            					bw.write(Integer.toString(begin)+"\n");				//���� �ݾ� ���Ͽ� ����
	            					
	            					temp1.setText(resultOutputTxt.getText());			//�� ���� �� �ӽ� ����
	            					temp2.setText(resultInputTxt.getText());			//�� ���� �� �ӽ� ����
	            					
	            					int resultPrice = Integer.parseInt(resultInputTxt.getText()) + price;
	            					resultInputTxt.setText(Integer.toString(resultPrice));
	            					bw.write(resultOutputTxt.getText()+"\n");			//�� ���� �� ���Ͽ� ����
	            					bw.write(Integer.toString(resultPrice)+"\n");		//�� ���� �� ���Ͽ� ����
	            					
	            					resultSumTxt.setText(Integer.toString(begin) + "��");
	            					bottomInfo.setText("["+priceTxt.getText()+"�� ����] �����Ͽ����ϴ�.");
	            					beginSumTxt.setText(Integer.toString(begin));
	            					useTxt.setText("");			//�ʱ�ȭ
	            					priceTxt.setText("");		//�ʱ�ȭ
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
				delBut.setToolTipText("�����̳� ������ �Է��ϱ� ������ ���ư��ϴ�.");
				delBut.addActionListener(new ActionListener() {				//delete ��ư ������ ��
					public void actionPerformed(ActionEvent e) {
						if(temp.getText().length() <= 0)					//���� ���� ���� ��
							bottomInfo.setText("<html><font color=red>�Էµ� �����̳� ������ �����ϴ�.</font></html>");
						else {
							try {
								BufferedWriter bw = new BufferedWriter(new FileWriter("moneyData/money.txt"));
								
								//�Է��ϱ� �� ������ ���ư���.
								beginSumTxt.setText(temp.getText());
								resultSumTxt.setText(temp.getText() + "��");
								resultOutputTxt.setText(temp1.getText());
								resultInputTxt.setText(temp2.getText());
								
								bw.write(temp.getText()+"\r");				// \n���� �ϴϱ� �޸��忡 �״�� ����ż� \r�� �ٲ�
								bw.write(temp1.getText()+"\r");
								bw.write(temp2.getText()+"\r");
								
								bottomInfo.setText("�Է��ߴ� �����̳� ������ �������ϴ�.");
								temp.setText("");		//�ʱ�ȭ
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
				clearBut.setToolTipText("���� �ݾ��� Reset�մϴ�.");
				clearBut.addActionListener(new ActionListener() {		//clear��ư�� ������ ��
					public void actionPerformed(ActionEvent e) {
						if(beginSumTxt.getText().length() <= 0)			//���� �ݾ��� �����Ǿ� ���� ���� ���
							bottomInfo.setText("<html><font color=red>�̹� ���� �ݾ��� �����Ǿ� ���� �ʽ��ϴ�.</font></html>");
						else {
							File f = new File("moneyData/money.txt");
							if(f.exists()) {
								f.delete();			//���� �����
								File f1 = new File("moneyData");
								f1.delete();		//���� �����
							}
							beginSumTxt.setText("");		//�ʱ�ȭ
							resultSumTxt.setText("");		//�ʱ�ȭ
							resultOutputTxt.setText("0");	//�ʱ�ȭ
							resultInputTxt.setText("0");	//�ʱ�ȭ
							bottomInfo.setText("���� �ݾ��� �ʱ�ȭ�߽��ϴ�.");
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
	public static boolean isNumeric(String s) {				//���ڿ��� �������� Ȯ���ϴ� �Լ�
		try {
			Double.parseDouble(s);			//�Ǽ� �ȿ� ������ ���ԵǾ������Ƿ�.
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
	//�ð�, bottomInfo ���� �ð�
	private class ThreadControl extends Thread{
		public void run(){
			boolean msgCntFlag = false;
			int num = 0;
			String curStr = new String();
			
			while(true){
				try{
					//���� �ð� ����
					today = Calendar.getInstance();
					String amPm = (today.get(Calendar.AM_PM) == 0 ? "AM":"PM");
					
					String hour;
							if(today.get(Calendar.HOUR) == 0) hour = "12"; 
							else if(today.get(Calendar.HOUR) == 12) hour = " 0";
							else hour = (today.get(Calendar.HOUR)<10?"0":"")+today.get(Calendar.HOUR);
							//�� �ڸ����� �տ� "0"�߰�
							
					String min = (today.get(Calendar.MINUTE)<10?"0":"")+today.get(Calendar.MINUTE);
					String sec = (today.get(Calendar.SECOND)<10?"0":"")+today.get(Calendar.SECOND);
					
					infoClock.setText(amPm+" "+hour+":"+min+":"+sec);
					
					//bottomInfo �ð��� ������ �ڵ����� ������� ����
					sleep(1000);		//1�ʵ��� �Ͻ� ����
					String infoStr = bottomInfo.getText();
					
					//bottomInfo�� ���� ������ ��.
					if(infoStr != " " && (msgCntFlag == false || curStr != infoStr)){
						num = 5;
						msgCntFlag = true;
						curStr = infoStr;
					}
					//bottomInfo ���ӵǴٰ� �����
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