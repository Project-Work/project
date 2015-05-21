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
		
		try {
			
			// estraggo l'id fino al quale l'analisi ha avuto effetto
			String queryBlock = "SELECT value FROM block";
			ResultSet value = cmd.executeQuery(queryBlock);
			value.next();
			int block = value.getInt("value");
			
			String query = "SELECT dirty_tweets.id, dirty_tweets.id_tweet, state.name, text, data FROM dirty_tweets, state"
				+ " WHERE state.id_state = dirty_tweets.id_state AND dirty_tweets.id > " + block;
			ResultSet tweets;
			tweets = cmd.executeQuery(query);

			ArrayList<Tweet> listTweets = new ArrayList<Tweet>();

			while (tweets.next()) {
				listTweets.add(new Tweet(tweets.getInt("id"),tweets.getString("id_tweet"), tweets
						.getString("name"), tweets.getString("text")
						.toLowerCase(), tweets.getDate("data")));
			}
			if (listTweets.size() != 0){
				// modifico la tabella block per salvare l'indice fin dove i dati sono stati esaminati
				int max = listTweets.get(listTweets.size() - 1).getId();
				String queryUpdBlock = "UPDATE block SET value = " + max + " WHERE value = " + block;
				cmd.executeUpdate(queryUpdBlock);
				System.out.println("ELEMENTO TABELLA BLOCK MODIFICATO");
			
			}
			tweets.close();
			return listTweets;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Language> getLanguages(Statement cmd) {
		String query = "SELECT id_languages, name FROM languages";
		ResultSet languages;

		try {
			languages = cmd.executeQuery(query);

			ArrayList<Language> listLang = new ArrayList<Language>();

			while (languages.next()) {
				listLang.add(new Language(languages.getInt("id_languages"), languages
						.getString("name")));
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
			String url = "jdbc:postgresql://192.168.1.113:5432/projectwork";
			Connection connessione = DriverManager.getConnection(url,
					"projectwork", "projectwork");
			
			SimpleDateFormat month  = new SimpleDateFormat("MM");
			SimpleDateFormat year  = new SimpleDateFormat("yyyy");
			
			Statement cmd = connessione.createStatement();
			ArrayList<Tweet> tweets = getTweets(cmd);

			ArrayList<Language> languages = getLanguages(cmd);
			String text;
			int count = 0;

			for (Language language : languages) {
				for (Tweet tweet : tweets) {
					text = tweet.getText();

					// controllo del linguaggio e conteggio
					if ((text.contains("" + language.getLanguage() + " ") || text.contains("" + language.getLanguage() + "!") ||
							text.contains(" " + language.getLanguage() + ",") || text.contains("" + language.getLanguage() + ":") ||
							text.contains("" + language.getLanguage() + "'") || text.contains("" + language.getLanguage() + "?") || 
							text.contains(" " + language.getLanguage() + ".")
							|| text.contains(" " + language.getLanguage() + "#") || text
								.contains("#" + language.getLanguage() + " "))) {
						count++;
					}
				}
				
				
//				System.out.println("id: " + language.getId() + " lang: "
//						+ language.getLanguage() + " count: " + count);

				// estrazione riga della tabella analysis con l'id del linguaggio, l'anno corrente e il mese corrente
				String query = "SELECT count, year, month FROM analysis WHERE id_language = " + language.getId() + " AND year = " + year.format(new Date()) + " AND month = " + month.format(new Date());
				ResultSet analysis = cmd.executeQuery(query);
				
				 if (!analysis.next()) { 
					 
					 //inserimento nella tabella analysis se non esiste l'elemento con l'id della lingua presente, nell'anno corrente e nel mese corrente
					String queryIns = "INSERT INTO analysis (id_language, count, year, month) VALUES (" + language.getId() +", " + count + ", " + year.format(new Date())+", "+ month.format(new Date()) + ")";
					cmd.executeUpdate(queryIns);
					System.out.println("ELEMENTI INSERITI NELLA TABELLA ANALYSIS");
					
				 }
				 else{
					 
					 // update tabella analysis se l'id è presente, anno in corso e mese in corso
					 count += analysis.getInt("count");
					 String queryUpd = "UPDATE analysis SET count = " + count + " WHERE id_language = " + language.getId()+ " AND year = " + year.format(new Date()) + " AND month = " + month.format(new Date()); 
					 cmd.executeUpdate(queryUpd);
					 System.out.println("ELEMENTI DELLA TABELLA ANALYSIS MODIFICATI");
				
				 }
				
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
