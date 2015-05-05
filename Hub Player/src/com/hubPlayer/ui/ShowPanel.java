package com.hubPlayer.ui;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;

import com.hubPlayer.player.HigherPlayer;
import com.hubPlayer.ui.tool.LibraryPanel;

/**
 * չʾ���
 * 
 * @date 2014-10-26
 *
 */

public class ShowPanel extends JPanel {

	private LibraryPanel libraryPanel;

	// ���������
	private JPanel lrcPanel;
	private JTextArea textArea;

	// �������
	private JScrollPane MVPanel;
	private JScrollPane radioPanel;
	private JScrollPane livePanel;

	public ShowPanel() {
		setLayout(new CardLayout());

		init();
		createLayout();
	}

	private void init() {
		libraryPanel = new LibraryPanel();

		// �����崦��
		lrcPanel = new JPanel(new GridLayout());
		textArea = new JTextArea();

		// �ı������ò��ɱ༭��͸�������ࡢ�Զ�����
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setMargin(new Insets(0, 175, 0, 0));
		textArea.setOpaque(false);

		textArea.setFont(new Font("PLAN", Font.PLAIN, 14));
		lrcPanel.add(textArea);

		// �������
		MVPanel = new JScrollPane(new JLabel("MV"));
		radioPanel = new JScrollPane(new JLabel("��̨"));
		livePanel = new JScrollPane(new JLabel("ֱ��"));
	}

	private void createLayout() {
		add(libraryPanel, "1");
		add(MVPanel, "2");
		add(lrcPanel, "3");
		add(radioPanel, "4");
		add(livePanel, "5");
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public LibraryPanel getLibraryPanel() {
		return libraryPanel;
	}

	public void setListTree(JTree[] trees) {
		libraryPanel.setListTree(trees);
	}

	public void setPlayer(HigherPlayer player) {
		libraryPanel.setPlayer(player);
	}
}
