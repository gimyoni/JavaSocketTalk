package Client;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientTalk extends JFrame {
	private Socket socket;

	private String local = "127.0.0.1";
	private int port = 5000;
	private String name = "홍길동";

	private JPanel panel, topPanel, chatInfoPanel, inputPanel;

	private JTextField inputText;
	private JButton sendButton;

	private JLabel title_lbl;
	private JLabel chatInfo_lbl;
	private JLabel ip_lbl;
	private JLabel port_lbl;
	private JLabel name_lbl;
	private JTextField name_tf;
	private JTextField ip_tf;
	private JTextField port_tf;

	private JButton connectionButton;

	private JScrollPane sp;
	private JTextArea textArea;

	public ClientTalk() {
		super("채팅 참여자");
		
		try {
			local = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(new Color(178,199,217));
		panel.setBounds(0,100, 500,650);
		

		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(169,189,206));
		topPanel.setBounds(0, 0, 500, 100);
		
		title_lbl = new JLabel("채팅 참여자");
		title_lbl.setFont(new Font("나눔바른고딕", Font.BOLD, 25));
		title_lbl.setBounds(50, 35, 200, 30);
		topPanel.add(title_lbl);

		add(topPanel);
		
		chatInfoPanel = new JPanel();
		chatInfoPanel.setLayout(null);
		chatInfoPanel.setBackground(Color.white);
		chatInfoPanel.setBounds(10,110, 465, 230);
		panel.add(chatInfoPanel);
		
		chatInfo_lbl=new JLabel("접속할 채팅방 정보");
		chatInfo_lbl.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		chatInfo_lbl.setBounds(140, 10, 400, 30);
		chatInfoPanel.add(chatInfo_lbl);

		
		ip_lbl = new JLabel("IP");
		ip_lbl.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		ip_lbl.setBounds(100, 55, 100, 30);
		chatInfoPanel.add(ip_lbl);
		ip_tf = new JTextField(local);
		ip_tf.setBounds(215, 55, 200, 30);
		ip_tf.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		chatInfoPanel.add(ip_tf);

		port_lbl = new JLabel("PORT");
		port_lbl.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		port_lbl.setBounds(100, 90, 100, 30);
		chatInfoPanel.add(port_lbl);
		port_tf = new JTextField(String.valueOf(port));
		port_tf.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		port_tf.setBounds(215, 90, 200, 30);
		chatInfoPanel.add(port_tf);

		name_lbl = new JLabel("NAME");
		name_lbl.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		name_lbl.setBounds(100, 125, 100, 30);
		chatInfoPanel.add(name_lbl);
		name_tf = new JTextField(name);
		name_tf.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		name_tf.setBounds(215, 125, 200, 30);
		chatInfoPanel.add(name_tf);

		connectionButton = new JButton("참여");
		connectionButton.setBounds(0, 180, 465, 50);
		connectionButton.setBackground(new Color(85,95,103));
		connectionButton.setForeground(Color.WHITE);
		connectionButton.setFont(new Font("나눔바른고딕", Font.PLAIN, 22));
		connectionButton.requestFocus();
		connectionButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (connectionButton.getText().equals("접속")) {
					if (ip_tf.getText().trim().equals("") || port_tf.getText().trim().equals("") || name_tf.getText().trim().equals("")) {
						JOptionPane.showMessageDialog(null, "정보를 입력하세요.", "", JOptionPane.WARNING_MESSAGE);
					} else {
						setTitle("손님_" + name_tf.getText());
						local = ip_tf.getText();
						port = Integer.parseInt(port_tf.getText());
						name = name_tf.getText();
						startClient(local, port);
						connectionButton.setText("종료");
						inputText.setEditable(true);
						sendButton.setEnabled(true);
					}
				} else {
					stopClient();
					connectionButton.setText("접속");
					inputText.setEditable(false);
					sendButton.setEnabled(false);
				}
			}
		});
		chatInfoPanel.add(connectionButton);

		textArea = new JTextArea();
		textArea.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		sp = new JScrollPane(textArea);
		sp.setBounds(10, 350, 465, 380);
		panel.add(sp);

		inputText = new JTextField();
		inputText.setBounds(10, 740, 350, 60);
		inputText.setFont(new Font("나눔바른고딕", Font.PLAIN, 25));
		inputText.setEditable(false);
		inputText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendButton.doClick();
				}
			}
		});
		panel.add(inputText);

		sendButton = new JButton("전송");
		sendButton.setBounds(370, 740, 100, 60);
		sendButton.setBackground(new Color(255, 236, 66));
		sendButton.setForeground(new Color(191, 144, 0));
		sendButton.setFont(new Font("나눔바른고딕", Font.PLAIN, 21));
		sendButton.setEnabled(false);
		sendButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!inputText.getText().trim().equals("")) {
					send(name + " : " + inputText.getText());
					inputText.requestFocus();
				}
			}
		});
		panel.add(sendButton);

		
		add(panel);

		setSize(500, 850);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stopClient();
				System.exit(0);
			}
		});
	}

	// 클라이언트 프로그램 동작
	public void startClient(String IP, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);

					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[512];
					int length = in.read(buffer);
					if (length == -1) {
						throw new IOException();
					}
					String message = new String(buffer, 0, length, "UTF-8");
					textArea.setText("");
					
					send("-- " + name + "님이 참가했습니다. --");
					
					receive();
				} catch (Exception e) {
					if (!socket.isClosed()) {
						stopClient();
						System.out.println("[서버 접속 실패]");
						System.exit(0);
					}
				}
			}
		};
		thread.start();
	}

	// 클라이언트 프로그램 종료
	public void stopClient() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 메시지 수신
	public void receive() {
		while (true) {
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[512];
				int length = in.read(buffer);
				if (length == -1) {
					throw new IOException();
				}
				String message = new String(buffer, 0, length, "UTF-8");
				textArea.append(message + "\n");
				textArea.setCaretPosition(textArea.getDocument().getLength());
				inputText.setText("");
			} catch (Exception e) {
				stopClient();
				break;
			}
		}
	}

	// 메시지 전송
	public void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					stopClient();
				}
			}
		};
		thread.start();
	}

	public static void main(String[] args) {
		new ClientTalk();
	}
}
