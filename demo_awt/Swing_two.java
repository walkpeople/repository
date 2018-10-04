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

class client_window{
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
	private boolean isStart=true;
	
	public void chatServer() {
		init();
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(isStart) {
					String ip_adress = ip.getText().trim();
					System.out.println(ip_adress);
					try {
						client = new Socket(ip_adress,Integer.valueOf(port.getText().trim()));
						new Thread(new readThread(user.getText().trim(), client)).start();
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
				
			}
		});
		
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					PrintStream out = new PrintStream(client.getOutputStream());
					String msg = msg_tf.getText().trim();
					out.println(user.getText().trim()+"˵��"+msg);
					msg_tf.setText("");
				} catch (IOException e1) {
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
		start = new JButton("����");
		stop = new JButton("ֹͣ");
		northpanel.setBorder(new TitledBorder("������Ϣ"));
		northpanel.add(new JLabel("������ַ"));
		northpanel.add(ip);
		northpanel.add(new JLabel("�˿ں�"));
		northpanel.add(port);
		northpanel.add(new JLabel("�û���"));
		northpanel.add(user);
		northpanel.add(start);
		northpanel.add(stop);
		jf.setBounds(300, 300, 640, 500);
		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		jf.setLayout(new BorderLayout());
		
		jmb = new JMenuBar();
		record= new JMenu("��¼");
		save = new JMenuItem("����");
		format= new JMenu("��ʽ");
		font = new JMenuItem("����");
		record.add(save);
		format.add(font);
		jmb.add(record);
		jmb.add(format);
		
		area1 = new JTextArea();
		area1.setEditable(true);
		JScrollPane leftpanel = new JScrollPane(area1);
		leftpanel.setBorder(new TitledBorder("��������"));
		
		area2 = new JTextArea();
		JScrollPane rightpanel = new JScrollPane(area2);
		area2.setEditable(true);
		rightpanel.setBorder(new TitledBorder("������Ϣ"));
		
		centerpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerpanel.setLeftComponent(leftpanel);
		centerpanel.setRightComponent(rightpanel);
		centerpanel.setDividerLocation(100);
		
		southpanel = new JPanel();
		southpanel.setBorder(new TitledBorder("����Ϣ"));
		msg_tf = new JTextField(45);
		send = new JButton("����");
		southpanel.add(msg_tf);
		southpanel.add(send);
		
		jp1.setLayout(new BorderLayout());
		jp1.add(jmb,BorderLayout.NORTH);
		panel.setLayout(new BorderLayout());
		panel.add(northpanel,BorderLayout.NORTH);
		panel.add(centerpanel,BorderLayout.CENTER);
		panel.add(southpanel,BorderLayout.SOUTH);
		jp1.add(panel,BorderLayout.CENTER);
		jf.add(jp1);
		jf.setVisible(true);
	}
	
	private class readThread implements Runnable{
		
		private String name;
		private Socket client;
		
		
		public readThread(String name, Socket client) {
			super();
			this.name = name;
			this.client = client;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				boolean flag = true;
				while(flag) {
					Scanner scan = new Scanner(client.getInputStream());
					if(scan.hasNext()) {
						String msg = scan.nextLine().trim();
						if("bye".equals(msg)) {
							scan.close();
							client.close();
							area2.append("�ѶϿ�����"+"\n");
							flag = false;
						}else {
							area2.append(msg+"\n");
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
