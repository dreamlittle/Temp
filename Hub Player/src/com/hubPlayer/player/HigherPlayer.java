package com.hubPlayer.player;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.hubPlayer.song.SongNode;
import com.hubPlayer.ui.tool.TimeProgressBar;

/**
 * �߲㲥���� ��Ҫ�������ⲿ��Ϣ����
 * 
 * @date 2014-10-18
 */

public class HigherPlayer extends BasicPlayer {

	private JTree tree;

	private SongNode loadSong;
	private SongNode playingSong;

	private String loadSongName;// ��loadSong��Ӧ
	private String playingSongName;// ��playedSong��Ӧ

	// ��ǰ���Ÿ������ڵ�Ŀ¼
	private TreePath currentListPath;

	private JButton play;

	private JLabel songNameLabel;
	private JLabel audioTotalTimeLabel;

	// ����ģʽ
	public int mode;
	public boolean IsPlayNextSong;

	// ��ǰ��Ƶ����ʱ��
	public int audioTotalTime;

	public HigherPlayer() {
	}

	// ������Դ
	public void load(TreeNode node) {
		this.loadSong = (SongNode) node;
		DefaultMutableTreeNode mutablenode = (DefaultMutableTreeNode) node;
		File songFile = (File) mutablenode.getUserObject();
		loadSongName = songFile.getName();
		try {
			audio = songFile.toURI().toURL();
			this.HTTPFlag = false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	// ������Դ
	public void load(SongNode song, String dataURL) {
		try {

			if (dataURL==null || dataURL.length() == 0) {
				JOptionPane.showMessageDialog(null, "û���ҵ�������Դ���ӵ�ַ", "",
						JOptionPane.PLAIN_MESSAGE);
				loadSongName = null;
				return;
			}

			loadSongName = song.toString();
			loadSong = song;
			audio = new URL(dataURL);
			this.HTTPFlag = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void open() {
		playingSongName = loadSongName;
		playingSong = loadSong;

		IsComplete = false;

		// ������Դ����ʱ��
		if (playingSong.getHTTPFlag())
			audioTotalTime = playingSong.getTotalTime();
		// ������Դ����ʱ��
		else
			audioTotalTime = getAudioTrackLength(audio);

		audioTotalTimeLabel.setText(getAudioTotalTime(audioTotalTime));

		// ���ü�ʱ��
		timerProgressBar.cleanTimer();

		// �����µļ�ʱ��
		timerProgressBar.setAudioTotalTime(audioTotalTime);
		timerProgressBar.setCurrentPlayedSongLrcInfo(playingSong.getLrcInfo());

		timerProgressBar.startTimer();

		// ��ΪҪ�벥����彻�� �����ü�������״̬���߳��ڸ߲㲥������ʼ��
		playThread = new Thread(() -> {
			// ���Ž���ǰ, �߳��ڴ����� ���ܽ���ĸ��� �ⲻ����
				super.play();

				if (IsEnd) {
					IsEnd = false;
					return;
				}

				// ���Ž��� play��ť��ʾ"����"״̬
				play.doClick();

				// ����ģʽ�������Ž��еز���
				playSwitch();
			});
		playThread.start();

	}

	private void playSwitch() {

		IsComplete = true;
		// ��ʼ������״̬
		switch (mode) {
		// ��������
		case 0:
			return;
			// ����ѭ��
		case 1:
			break;

		// ˳�򲥷� ���������Ű�ťʱ,��ΪҪ�����¸��� ���Խ���
		// if(!player.getAfterSong().equals(player.getCurrentSong())) {}
		// ʹ�õ�ǰ�̱߳���ֹ,���µĲ��Ų�������ֹ
		case 2:
			IsPlayNextSong = true;
			next();
			break;
		// �б��� ���ͬ˳�򲥷�
		case 3:
			cycle();
			break;
		// ������� ���ͬ˳�򲥷�
		case 4:
			random();
			break;
		}

		play.doClick();
	}

	// ���Ž��еز���
	public void next() {
		DefaultMutableTreeNode list = (DefaultMutableTreeNode) playingSong
				.getParent();
		SongNode songNode = null;

		if (!IsPlayNextSong) {
			songNode = (SongNode) list.getChildBefore(playingSong);

		} else {
			songNode = (SongNode) list.getChildAfter(playingSong);
		}
		if (songNode == null) {
			IsPause = false;

			return;
		}
		// �ڵ�ǰ���ڵĸ����б�·���м�������ŵĸ��� �γɴ����Ÿ�����·��
		TreePath songPath = currentListPath.pathByAddingChild(songNode);
		tree.setSelectionPath(songPath);

		if (songNode.getHTTPFlag())
			load(songNode, songNode.getDataURL());
		else
			load(songNode);
	}

	// �б�ѭ������
	private void cycle() {
		DefaultMutableTreeNode list = (DefaultMutableTreeNode) playingSong
				.getParent();
		SongNode songNode = null;

		songNode = (SongNode) list.getChildAfter(playingSong);

		if (songNode == null) {
			songNode = (SongNode) list.getFirstChild();
		}

		// �ڵ�ǰ���ڵĸ����б�·���м�������ŵĸ��� �γɴ����Ÿ�����·��
		TreePath songPath = currentListPath.pathByAddingChild(songNode);
		tree.setSelectionPath(songPath);

		if (songNode.getHTTPFlag())
			load(songNode, songNode.getDataURL());
		else
			load(songNode);

	}

	// �������
	private void random() {
		DefaultMutableTreeNode list = (DefaultMutableTreeNode) playingSong
				.getParent();
		int songnum = list.getChildCount();

		// �������
		int songindex = (int) Math.round(Math.random() * songnum) - 1;
		if (songindex < 0)
			songindex = 0;

		SongNode songNode = (SongNode) list.getChildAt(songindex);
		// �ڵ�ǰ���ڵĸ����б�·���м�������ŵĸ��� �γɴ����Ÿ�����·��
		TreePath songPath = currentListPath.pathByAddingChild(songNode);
		tree.setSelectionPath(songPath);

		if (songNode.getHTTPFlag())
			load(songNode, songNode.getDataURL());
		else
			load(songNode);

	}

	// �ṩ������Դ�����б����ŵĽӿ�
	public void setSelectTreeNodeInCurrentList(SongNode songNode, String dataURL) {

		TreePath songPath = currentListPath.pathByAddingChild(songNode);
		tree.setSelectionPath(songPath);

		load(songNode, dataURL);
	}

	public void end() {
		super.end();
		timerProgressBar.cleanTimer();
	}

	public TreeNode getloadSong() {
		return loadSong;
	}

	public TreeNode getPlayingSong() {
		return playingSong;
	}

	public String getPlayingSongName() {
		return playingSongName;
	}

	public void setPlayingSongName(String song) {
		playingSongName = song;
	}

	public String getLoadSongName() {
		return loadSongName;
	}

	public JButton getPlayButton() {
		return play;
	}

	public void setPlayButton(JButton button) {
		this.play = button;

	}

	public void setCurrentListPath(TreePath currentListPath) {
		this.currentListPath = currentListPath;
	}

	public void setJTree(JTree tree) {
		this.tree = tree;
	}

	public JTree getJTree() {
		return this.tree;
	}

	public JLabel getSongNameLabel() {
		return songNameLabel;
	}

	public void setSongNameLabel(JLabel songNameLabel) {
		this.songNameLabel = songNameLabel;
	}

	public void setVoiceValue(float voiceValue) {
		super.getFloatControl().setValue(voiceValue);
	}

	public float getVoiceValue() {
		return getFloatControl().getValue();
	}

	public void setAudioTotalTimeLabel(JLabel label) {
		audioTotalTimeLabel = label;
	}

	public void setCurrentTimeCountLabel(JLabel label) {
	}

	public void setTimerProgressBar(TimeProgressBar timerProgressBar) {
		this.timerProgressBar = timerProgressBar;
	}
}
