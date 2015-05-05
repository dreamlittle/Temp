package com.hubPlayer.ui.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.hubPlayer.player.HigherPlayer;
import com.hubPlayer.song.SongInfos;
import com.hubPlayer.song.SongNode;

/**
 * HubLibraryOperation������㽻�� �ֿ����ݱ���еĲ������(OperationPanel),��Ҫ����3����ť:����,�ӵ��б�,����
 * 
 * @date 2014-11-07
 */

public class LibraryOperation {

	private JTree[] trees;

	// ������
	private HigherPlayer player;

	// ���������ʾ ֻΪ��һ����������ʾ
	private static boolean TipFlag = true;

	private final static String savePath = "E:/Hub/download";



	public void setListTree(JTree[] trees) {
		this.trees = trees;
	}

	public void setPlayer(HigherPlayer player) {
		this.player = player;
	}

	public class OperationPanel extends JPanel {

		private JButton play;
		private JButton toList;
		private JButton download;

		private String song;
		private String singer;
		private String dataURL;

		private SongNode songNode;

		public OperationPanel() {
			initComponent();
			setAction();
		}

		// ���ո�����Ϣ
		public OperationPanel(SongInfos songInfos) {

			this();

			song = songInfos.getSong();

			singer = songInfos.getSinger();

			dataURL = songInfos.getSongDataUrl();

			songNode = new SongNode(singer + "-" + song,
					songInfos.getTotalTime(), songInfos.getDataSize(),
					songInfos.getLrcUrl(), dataURL);
		}

		private void initComponent() {
			play = new IconButton("����", "icon/note2.png");
			toList = new IconButton("��ӵ��б�", "icon/add.png");
			download = new IconButton("����", "icon/download2.png");

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			Box box = Box.createHorizontalBox();
			box.add(play);
			box.add(toList);
			box.add(download);

			add(Box.createHorizontalStrut(40));
			add(box);
		}

		private void setAction() {

			// ����
			play.addActionListener(event -> {

				// ѡ��Ĭ�ϲ����б�
				trees[0].setSelectionRow(0);
				
				addTreeList(trees[0], 0);

				player.setSelectTreeNodeInCurrentList(songNode, dataURL);

				// ���Ű�ť����
				player.getPlayButton().doClick();
			});

			// �ӵ������б�
			toList.addActionListener(event -> {
				addTreeList(trees[0], 0);
			});

			// ����
			download.addActionListener(event -> {

				if (dataURL == null || dataURL.length() == 0) {
					JOptionPane.showMessageDialog(null, "û���ҵ���Դ��Ӧ����������", "",
							JOptionPane.PLAIN_MESSAGE);
					return;
				}

				new Thread(() -> {
					try {

						// ����Դ����
						HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
								dataURL).openConnection();

						// ����IO�� ��д����
						BufferedInputStream inputStream = new BufferedInputStream(
								httpURLConnection.getInputStream());

						String songName = savePath + "/" + singer + "-" + song;
						if (!songName.endsWith(".mp3"))
							songName += ".mp3";

						BufferedOutputStream outputStream = new BufferedOutputStream(
								new FileOutputStream(new File(songName)));

						// ���뵽������� ��"������"�ڵ�
						addTreeList(trees[2], 0);

						byte[] buff = new byte[1024];
						int onceRead = 0;
						while ((onceRead = inputStream.read(buff, 0,
								buff.length)) > 0) {
							outputStream.write(buff, 0, onceRead);
						}

						outputStream.flush();
						outputStream.close();
						inputStream.close();

						// �Ƴ�"������"�ĸ�����Ϣ
						removeSongNodeInTreeList(trees[2], 0);
						// ��������Ϣ����"������"
						addTreeList(trees[2], 1);

						// ���������ʾ
						if (TipFlag) {

							JOptionPane.showMessageDialog(null, "�������,�ļ�����  "
									+ savePath, "", JOptionPane.PLAIN_MESSAGE);
							TipFlag = false;
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

				}).start();

			});

		}

		public void addTreeList(JTree tree, int index) {

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree
					.getModel().getRoot();
			DefaultMutableTreeNode list = (DefaultMutableTreeNode) root
					.getChildAt(index);

			list.add(songNode);

			// �б�������
			String listName = (String) list.getUserObject();
			listName = listName.substring(0, listName.lastIndexOf("[")) + "["
					+ list.getChildCount() + "]";
			list.setUserObject(listName);

			// ������ﲻ�������Ļ� �᲻��ȷ��ʾ
			tree.updateUI();

		}

		public void removeSongNodeInTreeList(JTree tree, int index) {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree
					.getModel().getRoot();
			DefaultMutableTreeNode list = (DefaultMutableTreeNode) root
					.getChildAt(index);

			list.remove(songNode);

			// �б�������
			String listName = (String) list.getUserObject();
			listName = listName.substring(0, listName.lastIndexOf("[")) + "["
					+ list.getChildCount() + "]";
			list.setUserObject(listName);

			// ������ﲻ�������Ļ� �᲻��ȷ��ʾ
			tree.updateUI();

		}

		public void setSong(String song) {
			this.song = song;

		}

		public void setSinger(String singer) {
			this.singer = singer;
		}

		public void setDataURL(String dataURL) {
			this.dataURL = dataURL;
		}

	}

}
