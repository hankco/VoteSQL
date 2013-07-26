package me.javoris767.votesql.utils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import me.javoris767.votesql.VoteSQL;

public class VoteSQLUpdate {

	private VoteSQL plugin;
	private URL filesFeed;
	private String version;
	private String link;
	
	public VoteSQLUpdate(VoteSQL voteSQL, String url) {
		plugin = voteSQL;
		
		try {
			this.filesFeed = new URL(url);
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public boolean updateNeeded() {
		try {
			InputStream input = this.filesFeed.openConnection().getInputStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			
			Node latest = doc.getElementsByTagName("item").item(0);
			NodeList children = latest.getChildNodes();
			
			this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
			this.link = children.item(3).getTextContent();
			
			if (!plugin.getDescription().getVersion().equals(this.version)) {
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
			}
		return false;
		}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getLink() {
		return this.link;
	}
	}