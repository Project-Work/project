import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConnessioneDb {

	public static ArrayList<Tweet> getTweets(Statement cmd) {
		String query = "SELECT trash_tweet.id, countries.country, text, data FROM trash_tweet, countries"
				+ " WHERE countries.id = trash_tweet.id_country";
		ResultSet tweets;

		try {
			tweets = cmd.executeQuery(query);

			ArrayList<Tweet> listTweets = new ArrayList<Tweet>();

			while (tweets.next()) {
				listTweets.add(new Tweet(tweets.getString("id"), tweets
						.getString("country"), tweets.getString("text")
						.toLowerCase(), tweets.getDate("data")));
			}
			tweets.close();
			return listTweets;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Language> getLanguages(Statement cmd) {
		String query = "SELECT id, language FROM languages";
		ResultSet languages;

		try {
			languages = cmd.executeQuery(query);

			ArrayList<Language> listLang = new ArrayList<Language>();

			while (languages.next()) {
				listLang.add(new Language(languages.getInt("id"), languages
						.getString("language")));
			}
			languages.close();
			return listLang;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

		// ESTRAZIONE DEI TWEET

		try {
			// Connessione al DB
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			String url = "jdbc:postgresql://192.168.0.23:5432/projectwork";
			Connection connessione = DriverManager.getConnection(url,
					"ecommerce", "password");
			Statement cmd = connessione.createStatement();

			ArrayList<Tweet> tweets = getTweets(cmd);

			// // data
			// // 0 1 2
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// String data = sdf.format(new Date());
			// String[] date = data.split("-");

			ArrayList<Language> languages = getLanguages(cmd);
			String text;
			int count = 0;

			for (Language language : languages) {

				for (Tweet tweet : tweets) {
					text = tweet.getText();

					if ((text.contains(" " + language.getLanguage() + " ")
							|| text.contains(" " + language.getLanguage() + "#") || text
								.contains("#" + language.getLanguage() + " "))) {

						count++;
					}
				}

				System.out.println("id: " + language.getId() + " lang: "
						+ language.getLanguage() + " count: " + count);

				// query = "SELECT counter, data FROM analisi WHERE id_lang = "
				// + idLang + " AND data = '" + data + "'";
				// // count += langList.getInt("counter");
				//
				// Statement cmd2 = connessione.createStatement();
				// ResultSet langList = cmd2.executeQuery(query);
				/*
				 * if (!langList.next()) { query =
				 * "INSERT INTO analisi (id_lang, counter, data) VALUES (" +
				 * idLang + ", " + count + ", '" + data + "')"; } else { query =
				 * "UPDATE analisi SET id_lang = " + idLang + ", counter = " +
				 * count + ", data = '" + data + "' WHERE id = " + idLang; }
				 */

				// inserimento del counter nel datab

				// query = "inserisci counter o update";
				// cmd.executeUpdate(query);
				count = 0;
			}
			cmd.close();
			connessione.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}