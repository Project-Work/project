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

			Statement cmdTrashTweet = connection.createStatement();
			String queryTrashTweet = "SELECT id, text FROM trash_tweet";
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
			int tmp = 0; //remove IT !!!
			while (rsTrashTweet.next()) {

				whiteList = false;
				blackList = false;

				String text = rsTrashTweet.getString("text").toLowerCase();
				
				while (rsBlackList.next()) {
					String word = rsBlackList.getString("word");
					if (text.contains(word)) {
						blackList = true;
					}					
				}
				rsBlackList.first();
				
				while (rsWhiteList.next()) {
					String word = rsWhiteList.getString("word");
					if (text.contains(word)) {
						whiteList = true;
					}
					
				}
				rsWhiteList.first();
				
				if (whiteList && !blackList) {
					//cmdCleanTweet.setInt(1, rsTrashTweet.getInt("id"));
					//cmdCleanTweet.executeUpdate();
					System.out.println(text);
					tmp++;
					
				}					
			}
			System.out.println(tmp);
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
