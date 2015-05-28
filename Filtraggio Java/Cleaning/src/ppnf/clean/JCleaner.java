package ppnf.clean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ppnf.conn.JConnection;

public class JCleaner {

	public static void main(String[] args) {

		Connection connection = JConnection.connect();

		try {

			connection.setAutoCommit(false);
			
			Statement cmdBlock = connection.createStatement();
			String queryBlock = "SELECT value FROM block_cleaner";
			ResultSet value = cmdBlock.executeQuery(queryBlock);
			value.next();
			int block = value.getInt("value");
			
			Statement cmdTrashTweet = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String queryTrashTweet = "SELECT id, text FROM trash_tweet WHERE id > " + block;
			ResultSet rsTrashTweet = cmdTrashTweet.executeQuery(queryTrashTweet);

			Statement cmdBlackList = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String queryBlackList = "SELECT word FROM blacklist";
			ResultSet rsBlackList = cmdBlackList.executeQuery(queryBlackList);

			Statement cmdWhiteList = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String queryWhiteList = "SELECT word FROM whitelist";
			ResultSet rsWhiteList = cmdWhiteList.executeQuery(queryWhiteList);

			String preparedInsClean = "INSERT INTO clean_tweet(id_trash) VALUES (?)";
			PreparedStatement cmdCleanTweet = connection.prepareStatement(preparedInsClean);

			boolean whiteList;
			boolean blackList;
			
			while (rsTrashTweet.next()) {

				whiteList = false;
				blackList = false;

				String text = rsTrashTweet.getString("text").toLowerCase();
				
				while (rsBlackList.next()) {
					String word = rsBlackList.getString("word");
					if (text.contains("#" + word + " ") ||text.contains(" " + word + " ")) {
						blackList = true;
					}					
				}
				rsBlackList.first();
				
				while (rsWhiteList.next()) {
					String word = rsWhiteList.getString("word");
					if (text.contains("#" + word + " ") ||text.contains(" " + word + " ")) {
						whiteList = true;
					}
					
				}
				rsWhiteList.first();
				
				if (whiteList && !blackList) {
					cmdCleanTweet.setInt(1, rsTrashTweet.getInt("id"));
					cmdCleanTweet.executeUpdate();		
				}					
			}
			rsTrashTweet.previous();
			int last = rsTrashTweet.getInt("id");
			String queryUpdBlock = "UPDATE block_cleaner SET value = " + last + " WHERE value = " + block;
			cmdBlock.executeUpdate(queryUpdBlock);
			
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
