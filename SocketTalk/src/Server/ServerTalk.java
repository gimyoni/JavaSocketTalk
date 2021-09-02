package Server;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerTalk extends JFrame {
	public static ExecutorService threadPool;
	public static Vector<Client> clients = new Vector<Client>();

	private ServerSocket serverSocket;

	private String local;
	private int port = 5000;
	private String name = "����";

	private JPanel panel, topPanel, chatInfoPanel, inputPanel;
	private JLabel title_lbl;
	private JLabel chatInfo_lbl;
	private JLabel ip_lbl;
	private JLabel port_lbl;
	private JLabel name_lbl;
	private JTextField ip_tf;
	private JTextField port_tf;
	private JTextField name_tf;
	private JScrollPane sp;
	public static JTextArea textArea;
	private JButton connectionButton;
	private JTextField inputText;
	private JButton sendButton;

	public ServerTalk() {
		super("ä�� ����");
		
		try {
			local = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
//        System.out.println("IP : " + local);

		panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(new Color(178,199,217));
		panel.setBounds(0,100, 500,650);
		
		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(169,189,206));
		topPanel.setBounds(0, 0, 500, 100);
		
		title_lbl = new JLabel("ä�� ����");
		title_lbl.setFont(new Font("�����ٸ����", Font.BOLD, 25));
		title_lbl.setBounds(50, 35, 200, 30);
		topPanel.add(title_lbl);

		add(topPanel);

		chatInfoPanel = new JPanel();
		chatInfoPanel.setLayout(null);
		chatInfoPanel.setBackground(Color.white);
		chatInfoPanel.setBounds(10,110, 465, 230);
		panel.add(chatInfoPanel);
		
		chatInfo_lbl=new JLabel("������ ä�ù� ����");
		chatInfo_lbl.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		chatInfo_lbl.setBounds(140, 10, 400, 30);
		chatInfoPanel.add(chatInfo_lbl);

		ip_lbl = new JLabel("IP");
		ip_lbl.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		ip_lbl.setBounds(100, 55, 100, 30);
		chatInfoPanel.add(ip_lbl);
		ip_tf = new JTextField(local);
		ip_tf.setBounds(215, 55, 200, 30);
		ip_tf.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		chatInfoPanel.add(ip_tf);

		port_lbl = new JLabel("PORT");
		port_lbl.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		port_lbl.setBounds(100, 90, 100, 30);
		chatInfoPanel.add(port_lbl);
		port_tf = new JTextField(String.valueOf(port));
		port_tf.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		port_tf.setBounds(215, 90, 200, 30);
		chatInfoPanel.add(port_tf);

		name_lbl = new JLabel("NAME");
		name_lbl.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		name_lbl.setBounds(100, 125, 100, 30);
		chatInfoPanel.add(name_lbl);
		name_tf = new JTextField(name);
		name_tf.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		name_tf.setBounds(215, 125, 200, 30);
		chatInfoPanel.add(name_tf);


		textArea = new JTextArea();
		textArea.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		sp = new JScrollPane(textArea);
		sp.setBounds(10, 350, 465, 380);
		panel.add(sp);

		inputText = new JTextField();
		inputText.setBounds(10, 740, 350, 60);
		inputText.setFont(new Font("�����ٸ����", Font.PLAIN, 25));
		inputText.setEditable(false);
		inputText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendButton.doClick();
				}
			}
		});
		panel.add(inputText);
		
		sendButton = new JButton("����");
		sendButton.setBounds(370, 740, 100, 60);
		sendButton.setBackground(new Color(255, 236, 66));
		sendButton.setForeground(new Color(191, 144, 0));
		sendButton.setFont(new Font("�����ٸ����", Font.PLAIN, 21));
		sendButton.setEnabled(false);
		sendButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(!inputText.getText().trim().equals("")) {
					send(name + " : " + inputText.getText());
					inputText.requestFocus();
				}
			}
		});
		panel.add(sendButton);

		connectionButton = new JButton("����");
		connectionButton.setBounds(0, 180, 465, 50);
		connectionButton.setBackground(new Color(85,95,103));
		connectionButton.setForeground(Color.WHITE);
		connectionButton.setFont(new Font("�����ٸ����", Font.PLAIN, 22));
		connectionButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { // ���콺�� ��������
				if (connectionButton.getText().equals("����")) {
					if (ip_tf.getText().trim().equals("") || port_tf.getText().trim().equals("") || name_tf.getText().trim().equals("")) {
						JOptionPane.showMessageDialog(null, "������ �Է��ϼ���.", "", JOptionPane.WARNING_MESSAGE);
					} else {
						local = ip_tf.getText();
						port = Integer.parseInt(port_tf.getText());
						name = name_tf.getText();
						startServer(local, port);
						String message = "-- �����ڸ� ��ٸ��� ���Դϴ�. --\n";
						textArea.append(message);
						textArea.setCaretPosition(textArea.getDocument().getLength());
						connectionButton.setText("����");
						inputText.setEditable(true);
						sendButton.setEnabled(true);
					}
				} else {
					stopServer();
					String message = "-- ������  �����մϴ�. --\n";
					textArea.append(message);
					textArea.setCaretPosition(textArea.getDocument().getLength());
					connectionButton.setText("����");
					inputText.setEditable(false);
					sendButton.setEnabled(false);
				}
			}
		});
		chatInfoPanel.add(connectionButton);

		getContentPane().add(panel);
		setSize(500, 850);
		setVisible(true);
		setResizable(false);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void startServer(String IP, int port) { // ���� ����
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, port));
		} catch (Exception e) {
			e.printStackTrace();
			if (!serverSocket.isClosed()) {
				stopServer();
			}
			return;
		}

		// Ŭ���̾�Ʈ�� ������ ������ ��ٸ�
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						clients.add(new Client(socket));//
						PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
						writer.println("");
						writer.flush();
					} catch (Exception e) {
						if (!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}
		};

		threadPool = Executors.newCachedThreadPool();
		threadPool.submit(thread);
	}

	public void stopServer() { // ���� ����
		try {
			// �۵����� ��� ���� �ݱ�
			Iterator<Client> iterator = clients.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.socket.close();
				iterator.remove();
			}
			// ���� ���� ��ü �ݱ�
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			// ������Ǯ ����
			if (threadPool != null && !threadPool.isShutdown()) {
				threadPool.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String message) {
		textArea.append(message + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
		inputText.setText("");
		for(Client client : ServerTalk.clients) {
			client.send(message);
		}
	}
	
	public static void main(String[] args) {
		new ServerTalk();
	}
}
