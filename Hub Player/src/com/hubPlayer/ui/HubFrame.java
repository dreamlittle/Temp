package com.hubPlayer.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JTree;


import java.util.Map;
import java.util.List;

import com.hubPlayer.player.HigherPlayer;
import com.hubPlayer.search.SongLibraryMap;
import com.hubPlayer.song.SongInfos;
import com.hubPlayer.ui.tool.ButtonToolBar;
import com.hubPlayer.ui.tool.IconButton;

/**
 * �� KuGou����Ϊ���
 * 
 * @date 2014-10-1
 */

public class HubFrame extends JFrame {

	private final int InitialWidth = 975;
	private final int InitialHeight = 670;
	private final Point InitialPoint;

	private final int ChangedWidth = 365;

	private PlayPanel playPanel;
	private PlayListPanel playListPanel;
	private SearchPanel searchPanel;
	private ShowPanel showPanel;

	private ButtonToolBar toolBar;

	private SongLibraryMap<String, List<SongInfos>> songLibrary;

	private final static String savaPath = "E:/Hub/download";

	public HubFrame() {

		setTitle("Hub");
		setSize(InitialWidth, InitialHeight);

		Dimension dime = Toolkit.getDefaultToolkit().getScreenSize();
		InitialPoint = new Point((dime.width - InitialWidth) / 2,
				(dime.height - InitialHeight) / 2);
		setLocation(InitialPoint);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// �������ظ����ļ���
		File savefolder = new File(savaPath);

		// ���������ظ����Ĵ洢�ļ���
		if (!savefolder.exists())
			savefolder.mkdirs();

		readSongLibrary();

		buildPanel();

		setVisible(true);

		requestFocus();
	}

	private void buildPanel() {
		playPanel = new PlayPanel();

		// ToolBar:the left of the Frame
		toolBar = new ButtonToolBar(JToolBar.VERTICAL, 4);

		JButton[] toolBarButtons = new JButton[] {
				new IconButton("�����б�", "icon/note.png"),
				new IconButton("�����ղ�", "icon/clouds.png"),
				new IconButton("�ҵ�����", "icon/download.png"),
				new IconButton("����", "icon/app.png") };

		toolBar.addButtons(toolBarButtons);

		playListPanel = new PlayListPanel(toolBar.getButtons(), this);

		searchPanel = new SearchPanel();

		showPanel = new ShowPanel();

		// ���ݸ�����������
		JTree[] listTree = playListPanel.deliverTree();
		HigherPlayer player = playPanel.getHigherPlayer();

		// ��ͨ�������������б����
		playPanel.setTrees(listTree);
		player.setJTree(listTree[0]);
		playListPanel.deliverHigherPlayer(player);

		// ��ͨ�ֿ����������б����
		showPanel.setListTree(listTree);

		// ��ͨ���������չʾ���
		searchPanel.setShowPanel(showPanel);

		// ��ͨ��������뱾��
		searchPanel.setSongLibraryMap(songLibrary);

		// ��ͨ��������������
		playPanel.setLrcPanelTextArea(showPanel.getTextArea());

		// ��ͨ�ֿ�����벥�����
		showPanel.setPlayer(player);

		playPanel.setParentFrame(this);

		// Set the preferredSize of those panels
		playPanel.setPreferredSize(new Dimension(350, 115));
		playListPanel.setPreferredSize(new Dimension(305, 520));
		toolBar.setPreferredSize(new Dimension(47, 520));
		searchPanel.setPreferredSize(new Dimension(610, 115));
		showPanel.setPreferredSize(new Dimension(610, 520));

		buildLayout();

		setAction();
	}

	private void setAction() {

		// ��������¼� ��չ������
		this.addWindowStateListener(event -> {
			if (event.getNewState() == JFrame.MAXIMIZED_BOTH) {
				searchPanel.setVisible(true);
				showPanel.setVisible(true);
				setSize(InitialWidth, InitialHeight);
				setLocation(InitialPoint);
				setVisible(true);

			}
		});

		// �۵�����
		searchPanel.getCollapseButton().addActionListener(event -> {
			searchPanel.setVisible(false);
			showPanel.setVisible(false);
			setSize(ChangedWidth, InitialHeight);
			setVisible(true);
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				saveSongLibrary();

				System.exit(0);
			}
		});
	}

	private void buildLayout() {
		Box topBox = Box.createHorizontalBox();

		topBox.add(playPanel);
		topBox.add(searchPanel);

		Box bottomBox = Box.createHorizontalBox();
		bottomBox.add(toolBar);
		bottomBox.add(playListPanel);
		bottomBox.add(showPanel);

		Container mainPanel = getContentPane();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.add(topBox);
		mainPanel.add(bottomBox);
		mainPanel.add(Box.createVerticalStrut(15));
	}

	@SuppressWarnings({ "unchecked" })
	private void readSongLibrary() {

		File library = new File("E:/Hub/SongLibrary.dat");
		if (!library.exists())
			songLibrary = new SongLibraryMap<String, List<SongInfos>>();
		else {

			try {
				ObjectInputStream inputStream = new ObjectInputStream(
						new FileInputStream(library));
				songLibrary = (SongLibraryMap<String, List<SongInfos>>) inputStream
						.readObject();

				inputStream.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveSongLibrary() {

		if (songLibrary != null) {

			try {
				ObjectOutputStream outputStream = new ObjectOutputStream(
						new FileOutputStream(new File("E:/Hub/SongLibrary.dat")));
				outputStream.writeObject(songLibrary);
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
