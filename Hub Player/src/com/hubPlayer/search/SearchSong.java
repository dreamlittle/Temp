package com.hubPlayer.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hubPlayer.song.SongInfos;

/**
 * ����JSoup�����ٶ����� ���еõ������ĸ�����Ϣ(SongInfos)
 *
 * @date 2014-11-06
 */

public class SearchSong {

	// http://music.baidu.com/search/song?s=1&key=key&start=00&size=20
	// �����ǰٶ�����������ַ��ʽ, key�ǹؼ���,start���Կ��еĵڼ���������ʼ,sizeΪҳ����ʾ�ĸ�����Ŀ(���Ϊ20)

	// ������ַ����ҳ���뼯 �ٶ���������utf-8����
	private static final String baseUrl = "http://music.baidu.com";
	private String encode = "utf-8";

	// ��������
	private Map<String, List<SongInfos>> songLibraryMap;
	private int songNumber;

	// ��������չʾ��彻��
	private String key;
	private int start;
	private int page;

	// boolean flag;

	public SearchSong() {
		// songLibraryMap = new HashMap<String, List<SongInfos>>();

		// ��һҳ
		page = 1;
		// ��һ�׸����
		start = 0;

		
		songNumber =20;
	}

	/**
	 * ��������ַ����ȡHTML
	 */
	public boolean openConnection() {
		if (key == null)
			return false;

		// ƴ��������ַ
		String searchUrl = "";
		if ("�ٶ������¸��/�°�".equals(key)) {
			// �ٶ������¸��/�°��ַ
			searchUrl = "http://music.baidu.com/top/new/month/";

		} else {

			String keyEncode = "";
			// ��key�ؼ���ת��URL����
			try {
				keyEncode = URLEncoder.encode(key, encode);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			searchUrl = baseUrl + "/search/song?s=1&key=" + keyEncode
					+ "&start=" + start + "&size=20";
		}

		try {
			// ������,��ȡHTML�� ��
			// ������URLConnectionһ��,���汻ע���˵Ĵ���
			Document document = Jsoup.connect(searchUrl).get();
			//

			parseHtml(document);

			// // ������
			// URLConnection connection = new URL(searchUrl).openConnection();
			//
			//
			// // ��������
			// BufferedReader reader = new BufferedReader(new InputStreamReader(
			// connection.getInputStream(), encode));
			//
			// // ��ȡHTML
			// StringBuffer stringbuffer = new StringBuffer();
			// String line;
			// while ((line = reader.readLine()) != null) {
			// stringbuffer.append(line + "\n");
			// }
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "�������ӳ�ʱ", "",
					JOptionPane.PLAIN_MESSAGE);
			return false;
		}

	}

	/**
	 * ����HTML ��ȡ��Ϣ:���������֡�ר�������������ڵĵ�ַ
	 * 
	 * @param document
	 *            HTML�ĵ�
	 */
	private void parseHtml(Document document) {
		// ��ȡHTML�еĸ����б������

		// ��ȡ����������Ŀ
		songNumber = 20;
		Element e = document.select("span[class=number]").first();
		if (e != null) {
			String number = e.text();
			songNumber = Integer.parseInt(number);
		}

		// ÿ�������������
		Elements songDiv = null;
		// �¸�� �Ĺؼ��ֲ�һ��
		if ("�ٶ������¸��/�°�".equals(key))
			songDiv = document.select("div[class=song-item]");
		else
			songDiv = document.select("div[class=song-item clearfix");

		List<SongInfos> temporaryList = new Vector<SongInfos>();
		// ����ÿ��������
		for (Element aSongNode : songDiv) {
			// ѡ��class������song-title��ͷ��span��ǩ
			Element songTitle = aSongNode.select("span[class^=song-title")
					.first().select("a[href^=/song]").first();
			if (songTitle == null)
				continue;

			// ��ȡ�������ڵľ��Ե�ַ
			String songUrl = songTitle.attr("abs:href");

			// ��ȡ������
			String songName = songTitle.text();

			// �����б��������Ϣ
			temporaryList.add(getSongInfos(songName, songUrl));
		}

		if (songLibraryMap.get(key) == null)
			songLibraryMap.put(key, temporaryList);
		else
			songLibraryMap.get(key).addAll(temporaryList);
	}

	// �����ȡ������Ϣ
	private SongInfos getSongInfos(String songName, String songUrl) {

		SongInfos songInfos = new SongInfos(songName);

		try {
			// �򿪸�������,��ȡ��HTML����
			Document document = Jsoup.connect(songUrl).get();

			// ������Դ��ַ
			// ��ʽ http://music.baidu.com/song/7319923
			// String songID = songUrl.substring(28, songUrl.length());
			// String dataUrl =
			// "http://music.baidu.com/data/music/file?link=&song_id="
			// + songID;

			// ������
			String singer = "";
			Element SingerElement = document.select("span[class^=author_list]")
					.first();
			if (SingerElement != null) {
				singer = SingerElement.text();

				// �����������ʽ����"x1/x2"��ת��"x1��x2"
				if (singer.contains("/")) {
					String[] singers = singer.split("/");
					singer = "";
					for (String s : singers) {
						singer = singer + "��" + s;
					}
					singer = singer.substring(1);
				}
			}

			// ��ȡר����Ϣ ��ʽ-����ר����album
			String album = "";
			Element albumElement = document.select("li[class^=clearfix]")
					.first();
			if (albumElement != null)
				album = albumElement.text();
			// ȥ��"����ר����"
			if (album.length() >= 5)
				album = album.substring(5);

			searchDataURL(songInfos, singer, songName);

			// �ٶ����ָİ� �˽ڵ��Ѿ��Ҳ���
			// ��ȡ����������,������ҳ���ȡ���������ַ
			// String downloadUrl = "";
			// Element downloadElement = document.select("a[data_url]").first();
			// if (downloadElement != null)
			// downloadUrl = downloadElement.attr("data_url");

			// if (!flag) {
			// findDataUrl("����Ѹ", "���");
			// flag = true;
			// }

			// �ٶ����ָİ� �˽ڵ��Ѿ��Ҳ���
			// // ��ȡ�����ļ�����
			// int dataSize = 0;
			// // ����ʱ��
			// int totalTime = 0;
			//
			// Element dataSizeElement =
			// document.select("a[data_size]").first();
			// if (dataSizeElement != null) {
			// // ��ŵ�ʱ��
			// String size = dataSizeElement.attr("data_size");
			// dataSize = Integer.parseInt(size);
			// totalTime = dataSize * 8 / songInfos.getBitRate();
			// }

			// ��ȡ����ļ���ַ
			String lrcUrl = "";
			Element lrcUrlElement = document.select("a[data-lyricdata]")
					.first();
			if (lrcUrlElement != null) {
				lrcUrl = lrcUrlElement.attr("data-lyricdata");

				// ����ƥ��
				Pattern pattern = Pattern.compile("(/.*\\.lrc)");
				Matcher matcher = pattern.matcher(lrcUrl);
				if (matcher.find())
					lrcUrl = baseUrl + matcher.group();
				else
					lrcUrl = "";
			}

			// ���������Ϣ
			songInfos.setSinger(singer);
			songInfos.setAlbum(album);
			songInfos.setLrcUrl(lrcUrl);
			// songInfos.setSongDataUrl(dataUrl);

			// System.out.println("--------A song item--------");
			// System.out.println("song: " + songName + " singer: " + singer
			// + " album: " + songInfos.getAlbum() + " dataSize: "
			// + songInfos.getDataSize() + " bitRate: "
			// + songInfos.getBitRate());
			// System.out.println("songDataUrl: " + songInfos.getSongDataUrl());
			// System.out.println("lrcUrl: " + songInfos.getLrcUrl());
			// System.out.println("---------------------------");

		} catch (IOException e) {
			e.printStackTrace();

			// JOptionPane.showMessageDialog(null, "������ַ: " + songUrl +
			// "\n������: "
			// + songName + "  ��ȡ�����쳣", "", JOptionPane.PLAIN_MESSAGE);

		}

		return songInfos;
	}

	/**
	 * �����ܰٶ����ָİ��Ӱ��,���ﲻֱ��ȥ��ȡ������Դ��ַ���ļ����ֽ��� �ٶ��������˵�½��JS���������ַ
	 * �����ðٶ����ֺ�http://box.zhangmen.baidu.com/��xml�ļ���ӻ�ȡ
	 * 
	 * �˷����Ѿ�����ʹ�� ��xmlҪ���ó���Ȩ����
	 */

	private void searchDataURL(SongInfos songInfos, String singer, String song) {

		String songBoxUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title="
				+ song + "$$" + singer + "$$";

		Document document = null;
		try {
			document = Jsoup.connect(songBoxUrl).get();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		/**
		 * ��ȡdurl�ڵ��е�encode�ڵ���ַ�����decode�ڵ���ַ���ƴ�Ӹ�����Դ��ַ
		 **/
		String dataUrl = "";
		Elements durlNodes = document.select("durl");

		for (Element durlNode : durlNodes) {

			Element encode = durlNode.select("encode").first();
			Element decode = durlNode.select("decode").first();

			if (encode == null || decode == null)
				continue;
			String encodeText = encode.text();
			String decodeText = decode.text();
			encodeText = encodeText.substring(0,
					encodeText.lastIndexOf("/") + 1);
			dataUrl = encodeText + decodeText;

		}

		/**
		 * ��ȡ�����ļ����Ⱥͱ�����
		 **/
		int dataSize = 0;
		int totalTime = 0;

		Element p2p = document.select("p2p").first();
		if (p2p != null) {
			// �ļ����ֽ���
			String dataSizeText = p2p.select("size").first().text();
			// ������
			int bitRate = Integer.parseInt(p2p.select("bitrate").text()) * 1000;

			dataSize = Integer.parseInt(dataSizeText);
			totalTime = dataSize * 8 / bitRate;
			songInfos.setBitRate(bitRate);

		}

		songInfos.setSongDataUrl(dataUrl);
		songInfos.setDataSize(dataSize);
		songInfos.setTotalTime(totalTime);

	}

	/**
	 * ����ѽ����ĸ�����Ϣ��
	 */
	public void clear() {

		start = 0;
		key = "";
		page = 1;
		songNumber = 20;
	}

	/**
	 * ���������Ĺؼ��� ֮�������search������������
	 * 
	 * @param key
	 *            �ؼ���
	 * @return SearchSong this
	 */
	public SearchSong setKey(String key) {
		this.key = key;
		return this;
	}

	public String getKey() {
		return key;
	}

	/**
	 * ���õ�ǰҳ��
	 * 
	 * @param start
	 *            ���õ�ǰҳ���һ�׸����ڿ��е����,
	 * @return SearchSong this
	 */
	public SearchSong setPage(int page) {
		this.page = page;
		start = (page - 1) * 20;
		
		return this;
	}

	// ����ҳ��
	public int getPage() {
		return page;
	}

	public void setSongLibraryMap(Map<String, List<SongInfos>> songLibraryMap) {

		this.songLibraryMap = songLibraryMap;
	}

	public int getSongNumber() {
		return songNumber;
	}

}
