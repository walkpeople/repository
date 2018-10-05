package demo_awt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

class client_window {
	private JFrame jf;
	private JPanel jp1;
	private JPanel panel;
	private JPanel northpanel;
	private JTextField ip;
	private JTextField port;
	private JTextField user;
	private JButton start;
	private JButton stop;
	private JMenuBar jmb;
	private JMenu record;
	private JMenuItem save;
	private JMenu format;
	private JMenuItem font;
	private JSplitPane centerpanel;
	private JTextArea area1;
	private JTextArea area2;
	private JPanel southpanel;
	private JButton send;
	private JTextField msg_tf;

	private Socket client;
	private boolean isStart =true;
	private PrintStream out;
	private Scanner scan;

	public void chatServer() {
		init();
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (isStart) {
					String ip_adress = ip.getText().trim();
					try {
						client = new Socket(ip_adress, Integer.valueOf(port.getText().trim()));
						out = new PrintStream(client.getOutputStream());
						out.println("用户  " + user.getText().trim() + " " + "上线");
						scan = new Scanner(client.getInputStream());
						new Thread(new readThread(user.getText().trim(), client, scan)).start();
						port.setEditable(false);
						ip.setEditable(false);
						user.setEditable(false);
						isStart = false;
					} catch (NumberFormatException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				area2.append("你已经退出"+"\n");
				try {
					out.println("bye");
					area1.setText("");
					client.close();
					out.close();
					scan.close();
					isStart=true;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String msg = msg_tf.getText().trim();
					out.println(user.getText().trim() + "说：" + msg);
					msg_tf.setText("");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public void init() {
		jf = new JFrame();
		jp1 = new JPanel();
		panel = new JPanel();

		northpanel = new JPanel();
		ip = new JTextField(10);
		port = new JTextField(6);
		user = new JTextField(10);
		start = new JButton("启动");
		stop = new JButton("停止");
		northpanel.setBorder(new TitledBorder("配置信息"));
		northpanel.add(new JLabel("主机地址ַ"));
		northpanel.add(ip);
		northpanel.add(new JLabel("端口号"));
		northpanel.add(port);
		northpanel.add(new JLabel("用户名"));
		northpanel.add(user);
		northpanel.add(start);
		northpanel.add(stop);
		jf.setBounds(300, 300, 640, 500);
		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		jf.setLayout(new BorderLayout());

		jmb = new JMenuBar();
		record = new JMenu("记录");
		save = new JMenuItem("保存");
		format = new JMenu("格式");
		font = new JMenuItem("字体");
		record.add(save);
		format.add(font);
		jmb.add(record);
		jmb.add(format);

		area1 = new JTextArea();
		area1.setEditable(true);
		JScrollPane leftpanel = new JScrollPane(area1);
		leftpanel.setBorder(new TitledBorder("在线人数"));

		area2 = new JTextArea();
		JScrollPane rightpanel = new JScrollPane(area2);
		area2.setEditable(true);
		rightpanel.setBorder(new TitledBorder("聊天信息"));

		centerpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerpanel.setLeftComponent(leftpanel);
		centerpanel.setRightComponent(rightpanel);
		centerpanel.setDividerLocation(100);

		southpanel = new JPanel();
		southpanel.setBorder(new TitledBorder("发消息"));
		msg_tf = new JTextField(45);
		send = new JButton("发送");
		southpanel.add(msg_tf);
		southpanel.add(send);

		jp1.setLayout(new BorderLayout());
		jp1.add(jmb, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout());
		panel.add(northpanel, BorderLayout.NORTH);
		panel.add(centerpanel, BorderLayout.CENTER);
		panel.add(southpanel, BorderLayout.SOUTH);
		jp1.add(panel, BorderLayout.CENTER);
		jf.add(jp1);
		jf.setVisible(true);
	}

	private class readThread implements Runnable {

		private String name;
		private Socket client;
		private Scanner scan;

		public readThread(String name, Socket client, Scanner scan) {
			super();
			this.name = name;
			this.client = client;
			this.scan = scan;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				boolean flag = true;
				while (flag) {
					if (scan.hasNext()) {
						String msg = scan.nextLine().trim();
						if (msg.contains("在")) {
							area1.setText(msg);
						} else {
							if ("bye".equals(msg)) {
								scan.close();
								client.close();
								area2.append("已断开连接" + "\n");
								flag = false;
							} else {
								area2.append(msg + "\n");
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}

public class Swing_two {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		client_window client = new client_window();
		client.chatServer();
	}

}
