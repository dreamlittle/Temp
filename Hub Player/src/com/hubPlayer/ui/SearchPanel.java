package com.hubPlayer.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.hubPlayer.search.SearchSong;
import com.hubPlayer.song.SongInfos;
import com.hubPlayer.ui.tool.ButtonToolBar;
import com.hubPlayer.ui.tool.IconButton;
import com.hubPlayer.ui.tool.LibraryPanel;
import com.hubPlayer.ui.tool.LibraryTableModel;

/**
 * �����������
 * 
 * @date 2014-10-26
 */

public class SearchPanel extends JPanel {

	// �����������
	private JTextField textField;
	private JButton searchButton;
	// ��¼ǰһ��������ı�
	private String beforeKey;
	// �����ɹؼ��ֲ�����������ҳ
	private SearchSong searchSong;
	private Thread searchThread;

	private JButton userButton;

	// ��Ҫ�����л�չʾ���ҳ��
	private ButtonToolBar hubToolBar;

	private JButton[] toolBarButtons;

	// ��չʾ������
	private ShowPanel showPanel;
	private CardLayout cardLayout;

	private LibraryPanel libraryPanel;
	private LibraryTableModel libraryTableModel;

	// �������ҳ��
	private int maxPage;

	// ���ֿ����ݼ�
	private Map<String, List<SongInfos>> songLibraryMap;

	public SearchPanel() {

		setLayout(new BorderLayout());
		setOpaque(false);

		init();
		setAction();
		createLayout();
	}

	private void init() {
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(200, 30));

		searchButton = new IconButton("����", "icon/search.png");
		userButton = new IconButton("�û�", "icon/user.png");

		searchButton.setPreferredSize(new Dimension(50, 30));
		userButton.setPreferredSize(new Dimension(50, 30));

		hubToolBar = new ButtonToolBar(JToolBar.HORIZONTAL, 6);
		hubToolBar.setPreferredSize(new Dimension(300, 65));

		toolBarButtons = new JButton[6];

		toolBarButtons[0] = new IconButton("�۵�", "icon/collapse.png");
		toolBarButtons[1] = new IconButton("�ֿ�");
		toolBarButtons[1].setText("�ֿ�");
		toolBarButtons[2] = new IconButton("MV");
		toolBarButtons[2].setText("MV");
		toolBarButtons[3] = new IconButton("���");
		toolBarButtons[3].setText("���");
		toolBarButtons[4] = new IconButton("��̨");
		toolBarButtons[4].setText("��̨");
		toolBarButtons[5] = new IconButton("ֱ��");
		toolBarButtons[5].setText("ֱ��");

		hubToolBar.addButtons(toolBarButtons);

		searchSong = new SearchSong();
		maxPage = 100;
	}

	private void createLayout() {

		Box Box1 = Box.createHorizontalBox();
		Box1.add(Box.createHorizontalStrut(10));
		Box1.add(userButton);
		Box1.add(Box.createHorizontalStrut(20));
		Box1.add(textField);
		Box1.add(Box.createHorizontalStrut(5));
		Box1.add(searchButton);
		Box1.add(Box.createHorizontalStrut(10));

		Box Box2 = Box.createVerticalBox();
		Box2.add(Box.createVerticalStrut(7));
		Box2.add(Box1);
		Box2.add(Box.createVerticalStrut(5));

		add(Box2, BorderLayout.NORTH);
		add(hubToolBar, BorderLayout.CENTER);
	}

	private void setAction() {

		for (int i = 1; i < toolBarButtons.length; i++) {
			int k = i;
			toolBarButtons[i].addActionListener(event -> {
				cardLayout.show(showPanel, String.valueOf(k));
			});

		}

		searchButton.addActionListener(event -> {

			String key = textField.getText();

			if (!prejudgeForSearchButton(key))
				return;

			searchThread = new Thread(() -> {
				searchForSearchButton(key);
			});

			searchThread.start();

		});

		// ��ʼ��ʾ�ٶ������¸�
		// textField.setText("�ٶ������¸��/�°�");
		// searchButton.doClick();
	}

	private void setMoreSearchAction(JButton moreSearch) {

		moreSearch.addActionListener(event -> {

			String key = textField.getText();

			if (!prejudgeForMoreSearch(key))
				return;

			if (!key.equals(beforeKey)) {
				textField.setText(beforeKey);
				key = beforeKey;
			}

			// �жϸ�����ӳ���Ƿ�����˹ؼ���,û�������searchButton������
				if (songLibraryMap.containsKey(key)) {

					int searchPage = searchSong.getPage() + 1;

					if (searchPage > maxPage) {
						JOptionPane.showMessageDialog(null, "�Ѿ�û�и�������", "",
								JOptionPane.PLAIN_MESSAGE);
						return;
					}

					String searchKey = key;
					searchThread = new Thread(() -> {

						searchForMoreSearch(searchKey, searchPage);

					});
					searchThread.start();
				}

				else {
					searchButton.doClick();
				}

			});
	}

	// ���������ҳ
	private int countPage(int songNumber) {
		int page = songNumber / 20;
		if (songNumber % 20 != 0)
			page++;
		return page;
	}

	// Ԥ�������ؼ���
	private boolean prejudgeForSearchButton(String key) {
		if (searchThread != null && searchThread.isAlive()) {
			JOptionPane.showMessageDialog(null, "��������������,�����ĵȴ�", "",
					JOptionPane.PLAIN_MESSAGE);
			return false;
		}

		if (key == null || key.length() == 0)
			return false;

		// �����ؼ���û���Ҳ���Ҫ���з�ҳ����
		if (key.equals(beforeKey))
			return false;

		return true;

	}

	// Ԥ�������ؼ���
	private boolean prejudgeForMoreSearch(String key) {
		// ����������
		if (searchThread != null && searchThread.isAlive()) {
			JOptionPane.showMessageDialog(null, "��������������,�����ĵȴ�", "",
					JOptionPane.PLAIN_MESSAGE);
			return false;
		}

		if (beforeKey == null || beforeKey.length() == 0)
			return false;

		return true;
	}

	// ��������
	private void searchForSearchButton(String key) {
		// <-----------��������------------>
		if (!songLibraryMap.containsKey(key)) {
			// ���ǰ�ν�������Ϣ
			searchSong.clear();

			// ���ùؼ��� ���д˴ν���
			if (!searchSong.setKey(key).openConnection()) {
				// ����ʧ��
				beforeKey = "";
				return;
			}

			if (songLibraryMap.containsKey(key)) {

				// ��ȡ��������ҳ��
				maxPage = countPage(searchSong.getSongNumber());
			}
		}

		// <-----------���ݼ�����------------>
		// ���ѡ�����ݺ�ȡ��ѡ�У���Ϊ��ȥ��һ��Bug:�ڵ�ǰҳ�������Ԫ��ʱ��������ҳʱ���Ǹ��������ĵ�Ԫ�����ݲ�����
		libraryPanel.getDataTable().selectAll();

		// ����������
		libraryTableModel.initTableData();

		List<SongInfos> songList = songLibraryMap.get(key);
		int addSongNum = songList.size();

		// �����ֿ�������
		songList.subList(0, addSongNum).forEach(
				each -> libraryTableModel.updateData(each));

		libraryPanel.getDataTable().clearSelection();

		// �����˴������Ĺؼ���
		beforeKey = key;

		int page = countPage(addSongNum);
		searchSong.setPage(page);

	}

	// ��������
	private void searchForMoreSearch(String key, int page) {
		searchSong.setPage(page);

		List<SongInfos> songList = songLibraryMap.get(key);

		int songNum = songList.size();
		// ���ùؼ��� ���д˴ν���
		if (!searchSong.setKey(key).openConnection()) {
			// ����ʧ��
			return;
		}

		maxPage = countPage(searchSong.getSongNumber());

		songList.subList(songNum, songList.size()).forEach(
				each -> libraryTableModel.updateData(each));

		// libraryPanel.getTableScrollBar()
		// .setValue(
		// libraryPanel.getTableScrollBar().getMaximum()+1);

	}

	public void setShowPanel(ShowPanel showPanel) {
		this.showPanel = showPanel;
		this.cardLayout = (CardLayout) showPanel.getLayout();

		// ��ͨ�ֿ����������������Ϣ
		libraryPanel = showPanel.getLibraryPanel();
		libraryTableModel = libraryPanel.getLibraryTableModel();

		setMoreSearchAction(libraryPanel.getMoreSearch());

	}

	// HubFrame�������ĸ����⼯��
	public void setSongLibraryMap(Map<String, List<SongInfos>> songLibraryMap) {

		this.songLibraryMap = songLibraryMap;
		searchSong.setSongLibraryMap(songLibraryMap);
	}

	// �۵���尴ť
	public JButton getCollapseButton() {
		return toolBarButtons[0];
	}

}
