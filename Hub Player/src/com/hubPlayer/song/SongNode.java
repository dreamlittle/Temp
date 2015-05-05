package com.hubPlayer.song;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * SongNode ��ʾ�������� ����ʾ����·��
 * 
 * @date 2014-10-15
 */

public class SongNode extends DefaultMutableTreeNode {

	private File song;
	private String dataUrl;
	// ��Ǹ����Ƿ�Ϊ������Դ
	private boolean HTTPFlag;
	// ����ʱ��
	private int totalTime;
	// �ļ�����
	private int dataSize;

	// ��������������Դ
	private File lrcFile;
	// �����ĸ�����Ϣ
	private LrcInfos lrcInfo;

	// �������ϼ�·��
	private String parentPath;
	// ����չ���ĸ�����
	private String songName;

	public SongNode(File song) {
		super(song, false);
		this.song = song;

		parentPath = song.getParent();
		songName = song.getName();
		songName = songName.substring(0, songName.lastIndexOf("."));

		File f = new File(parentPath + "\\" + songName + ".lrc");
		lrcInfo = new LrcInfos();
		if (f.exists()) {
			lrcFile = f;
			lrcInfo.read(lrcFile);
		}
	}

	// ������Դ
	public SongNode(String songName, int totalTime, int dataSize,
			String lrcUrl, String dataUrl) {
		try {
			song = new File(dataUrl);
		} catch (NullPointerException e) {
			song = null;
		}
		this.dataUrl = dataUrl;
		this.songName = songName;
		this.dataSize = dataSize;

		HTTPFlag = true;

		// ��ŵĲ���ʱ�� ��������ʱ������������ʱ��-3
		this.totalTime = totalTime - 3;

		lrcInfo = new LrcInfos();

		// ���ڸ�������
		if (lrcUrl.length() > 0) {
			lrcInfo.read(lrcUrl);
			// ���ø�����õ���ȷ�Ĳ���ʱ��
			int time = lrcInfo.getTotalTime();
			if (this.totalTime < time) {
				this.totalTime = time;
			}
		}

	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setLrc(File lrcFile) {
		this.lrcFile = lrcFile;
		lrcInfo.read(lrcFile);
	}

	public File getLrc() {
		return lrcFile;
	}

	public LrcInfos getLrcInfo() {
		return lrcInfo;
	}

	public String getSongName() {
		return songName;
	}

	public File getSong() {
		return song;
	}

	public String getDataURL() {
		return dataUrl;
	}

	public boolean getHTTPFlag() {
		return HTTPFlag;
	}

	public int getDataSize() {
		return dataSize;
	}

	@Override
	public String toString() {
		if (songName.endsWith(".mp3"))
			return songName;
		return songName + ".mp3";
	}

	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;

		if (song == null)
			return false;

		SongNode objectNode = (SongNode) object;

		return song.equals(objectNode.getSong());
	}
}
