package ppnf.clean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ppnf.conn.JConnection;

public class JCleaner {

	
	public static void main(String[] args){
		
		Connection connection = JConnection.connect();
		
		try {
			
			connection.setAutoCommit(false);
			
			Statement cmdTrashTweet = connection.createStatement();
			String queryTrashTweet = "SELECT id, text FROM trash_tweet";
			ResultSet rsTrashTweet = cmdTrashTweet.executeQuery(queryTrashTweet);
			
			Statement cmdBlackList = connection.createStatement();
			String queryBlackList = "SELECT word FROM blacklist";
			ResultSet rsBlackList = cmdBlackList.executeQuery(queryBlackList);
			
			Statement cmdWhiteList = connection.createStatement();
			String queryWhiteList = "SELECT word FROM whitelist";
			ResultSet rsWhiteList = cmdWhiteList.executeQuery(queryWhiteList);
			
			PreparedStatement cmdCleanTweet = connection.prepareStatement("INSERT INTO clean_tweet(id_trash) VALUES (?)");
			
			boolean whiteList = false;
			boolean blackList = false;
			
			while(rsTrashTweet.next()){
				String text = rsTrashTweet.getString("text");
				while(rsBlackList.next()){
					String word = rsBlackList.getString("word");
					if(text.contains(word)){
						blackList=true;
					}
				}
				while(rsWhiteList.next()){
					String word = rsWhiteList.getString("word");
					if(text.contains(word)){
						whiteList=true;
					}
				}
				if(whiteList && !blackList){
					cmdCleanTweet.setInt(0, rsTrashTweet.getInt("id"));
				}
			}
			
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
