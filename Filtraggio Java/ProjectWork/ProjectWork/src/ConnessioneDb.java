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
			
			String query = "SELECT trash_tweet.id, trash_tweet.id_tweet, countries.country, text, date FROM trash_tweet, countries"
				+ " WHERE countries.id = trash_tweet.id_country AND trash_tweet.id > " + block;
			ResultSet tweets;
			tweets = cmd.executeQuery(query);

			ArrayList<Tweet> listTweets = new ArrayList<Tweet>();

			while (tweets.next()) {
				listTweets.add(new Tweet(tweets.getInt("id"),tweets.getString("id_tweet"), tweets
						.getString("country"), tweets.getString("text")
						.toLowerCase(), tweets.getDate("date")));
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
		String query = "SELECT id, language FROM programming_languages";
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
			String url = "jdbc:postgresql://52.17.223.138:5432/projectwork";
			Connection connessione = DriverManager.getConnection(url,
					"projectwork", "password");
			
			SimpleDateFormat month  = new SimpleDateFormat("MM");
			SimpleDateFormat year  = new SimpleDateFormat("yyyy");
			String date = year.format(new Date()) + "-" + month.format(new Date());
			
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
							text.contains("" + language.getLanguage() + ",") || text.contains("" + language.getLanguage() + ":") ||
							text.contains("" + language.getLanguage() + "'") || text.contains("" + language.getLanguage() + "?") || 
							text.contains("" + language.getLanguage() + ".") || 
							text.contains("" + language.getLanguage() + "#") || text
								.contains("#" + language.getLanguage() + " "))) {
						count++;
					}
				}
				
				
//				System.out.println("id: " + language.getId() + " lang: "
//						+ language.getLanguage() + " count: " + count);

				// estrazione riga della tabella analysis con l'id del linguaggio, l'anno corrente e il mese corrente
				String query = "SELECT counter, date FROM european_analysis WHERE id_lang = " + language.getId() + " AND date = '" + date + "'";
				ResultSet analysis = cmd.executeQuery(query);
				
				 if (!analysis.next()) { 
					 
					 //inserimento nella tabella analysis se non esiste l'elemento con l'id della lingua presente, nell'anno corrente e nel mese corrente
					String queryIns = "INSERT INTO analysis (id_lang, counter, date) VALUES (" + language.getId() +", " + count + ", '" + date + "')";
					cmd.executeUpdate(queryIns);
					System.out.println("ELEMENTI INSERITI NELLA TABELLA ANALYSIS");
					
				 }
				 else{
					 
					 // update tabella analysis se l'id è presente, anno in corso e mese in corso
					 count += analysis.getInt("counter");
					 String queryUpd = "UPDATE analysis SET counter = " + count + " WHERE id_lang = " + language.getId()+ " AND date = '" + date + "'"; 
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
