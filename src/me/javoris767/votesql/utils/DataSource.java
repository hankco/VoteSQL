package me.javoris767.votesql.utils;

public interface DataSource {
	
	public enum DataSourceType {
		MYSQL
	}
	void close();
	
	void reload();
}
