import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConnessioneDb {

	public static void main(String[] args) {

		// ESTRAZIONE DEI TWEET

		try {
			// Carichiamo un driver di tipo 1 (bridge jdbc-odbc)
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			// Creiamo la stringa di connessione
			String url = "jdbc:postgresql://192.168.101.108:5432/projectwork";
			// Otteniamo una connessione con username e password
			Connection connessione = DriverManager.getConnection(url,
					"ecommerce", "password");
			// Creiamo un oggetto Statement per poter interrogare il db
			Statement cmd = connessione.createStatement();
			// Eseguiamo una query e immagazziniamone i risultati
			// in un oggetto ResultSet
			String query = "SELECT trash_tweet.id, countries.country, text, data FROM trash_tweet, countries"
					+ " WHERE countries.id = trash_tweet.id_country";
			ResultSet tweets = cmd.executeQuery(query);

			ArrayList<Tweet> listTweets = new ArrayList<Tweet>();

			while (tweets.next()) {
				listTweets.add(new Tweet(tweets.getString("id"), tweets
						.getString("country"), tweets.getString("text")
						.toLowerCase(), tweets.getDate("data")));
			}
			/*for (Tweet tweet : listTweets) {
				System.out.print(tweet.getId());
				System.out.print(tweet.getCountry());
				System.out.print(tweet.getText());
				System.out.println(tweet.getData());
			}*/
			// data
			// 0 1 2
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String data = sdf.format(new Date());
			String[] date = data.split("-");

			query = "SELECT id, language FROM languages";
			ResultSet languages = cmd.executeQuery(query);

			String text;
			int count = 0;

			while (languages.next()) {

				// !!!Salvare i linguaggi come Chiave:Valore perchè senno non
				// funzia!!!

				int idLang = languages.getInt("id");
				String nameLang = languages.getString("language"); // recupera
																	// un
																	// singolo
																	// linguaggio
																	// dalla
																	// tabella
																	// linguaggi
				
				System.out.println("" + idLang + " " + nameLang);

				for (Tweet tweet : listTweets) {
					text = tweet.getText();

					if ((text.contains(" " + nameLang + " ")
							|| text.contains(" " + nameLang + "#") || text
								.contains("#" + nameLang + " "))) {

						count++;
					}
				}

				query = "SELECT counter, data FROM analisi WHERE id_lang = "
						+ idLang + " AND data = '" + data + "'";
				// count += langList.getInt("counter");

				Statement cmd2 = connessione.createStatement();
				ResultSet langList = cmd2.executeQuery(query);
				/*
				if (!langList.next()) {
					query = "INSERT INTO analisi (id_lang, counter, data) VALUES ("
							+ idLang + ", " + count + ", '" + data + "')";
				} else {
					query = "UPDATE analisi SET id_lang = " + idLang
							+ ", counter = " + count + ", data = '" + data
							+ "' WHERE id = " + idLang;
				}*/

				// inserimento del counter nel datab

				// query = "inserisci counter o update";
				// cmd.executeUpdate(query);
			}
			tweets.close();
			cmd.close();
			connessione.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}