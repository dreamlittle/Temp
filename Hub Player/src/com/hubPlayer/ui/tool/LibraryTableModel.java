package com.hubPlayer.ui.tool;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.hubPlayer.song.SongInfos;

/**
 * һ��������Ϣ���ģ��,����Ĭ�ϱ��ģʽDefaultTableModel,ʵ�ֵ�Ԫ����Ⱦ��TableCellRenderer
 * ���ͷΪ����(String),����(String),ר��(String),����(JPanel) ������Ⱦ����ʾ�������
 * ���в���������3����ť:����,�����б�,����
 * 
 * @date 2014-11-7
 */

public class LibraryTableModel extends DefaultTableModel {

	// ������
	private static final String[] title = new String[] { "����", "����", "ר��", "����" };
	// ��������Ӧ����������
	private static final Class[] types = new Class[] { String.class,
			String.class, String.class, Object.class };

	// ��������Ӧ�Ŀɱ༭״̬
	private static final boolean[] canEdit = new boolean[] { false, false,
			false, true };

	// ����ʼ����
	private static final Object[][] initData = new Object[][] {
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null } };

	// �������
	private LibraryOperation libraryOperation;

	public LibraryTableModel() {
		
		super(initData, title);

	}

	public void initTableData() {
		// ����������
		for (int i = getRowCount() - 1; i >= 0; i--) {
			removeRow(i);
			
		}

	}

	// ���ÿ�ж�Ӧ��������
	public Class getColumnClass(int columnIndex) {
		return types[columnIndex];
	}

	// ��ⵥԪ���Ӧ�Ŀɱ༭״̬
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return canEdit[columnIndex];
	}

	public void updateData(SongInfos songInfos) {

		String song = songInfos.getSong();
		String singer = songInfos.getSinger();
		String album = songInfos.getAlbum();

		// �������,���ܸ�����Ϣ
		JPanel panel = libraryOperation.new OperationPanel(songInfos);

		Object[] data = new Object[] { song, singer, album, panel };

		addRow(data);

	}

	public void setLibraryOperation(LibraryOperation libraryOperation) {
		this.libraryOperation = libraryOperation;
	}

}
