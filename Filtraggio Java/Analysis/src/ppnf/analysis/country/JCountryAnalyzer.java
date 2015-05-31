package ppnf.analysis.country;

import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ppnf.conn.JConnection;
import ppnf.logging.Logging;

public class JCountryAnalyzer {

	public static void update(Statement cmdUpdate, String id_country,
			int id_language, int counter, int year, int month){
		String queryUpdate = "UPDATE countries_analysis SET counter = " + counter + " WHERE country_id='" +id_country + "' AND language_id=" + id_language + " AND year=" + year + " AND month=" + month;
		try {
			cmdUpdate.executeUpdate(queryUpdate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Logging.logging(e.getMessage());
		}
	}
	
	
	public static void insert(Statement cmdInsert, String id_country,
			int id_language, int counter, int year, int month) {
		String queryInsert = "INSERT INTO countries_analysis(country_id, language_id, counter, year, month) VALUES ('" + id_country +"', "+ id_language + ", " + counter+", " + year+", "+ month+ " )";
		try {	
				cmdInsert.executeUpdate(queryInsert);
		} catch (SQLException e) {
			Logging.logging(e.getMessage());
		}
	}

	
	
	public static void analyze(ArrayList<Country> countries,
			ArrayList<Language> languages, ResultSet rsCleanTweet,
			Statement cmdInsert, Statement cmdUpdate, Statement cmdAnalysis, int block, Statement cmdBlock) {

		int counter = 0;
		Tweet tweet;
		SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
		int month = Integer.parseInt(sdfMonth.format(new Date()));
		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
		int year = Integer.parseInt(sdfYear.format(new Date()));

		try {
			for (Country country : countries) {
				for (Language language : languages) {
					while (rsCleanTweet.next()) {
						tweet = new Tweet(rsCleanTweet.getString("text")
								.toLowerCase(),
								rsCleanTweet.getString("id_country"));
						if (language.getLanguage().equals("java")
								&& !tweet.getText().contains("javascript")
								&& tweet.getText().contains("java")
								&& tweet.getId_country().equals(country.getId())) {
							counter++;
						}
						if (!language.getLanguage().equals("java")
								&& tweet.getText().contains(
										language.getLanguage())
								&& tweet.getId_country().equals(country.getId())) {
							counter++;
						}
					}
					 
					String queryAnalysis = "SELECT * FROM countries_analysis WHERE country_id = '"+ country.getId() + "' AND language_id = " + language.getId() + " AND year = " + year + " AND month = " + month;
					ResultSet rsAnalysis =  cmdAnalysis.executeQuery(queryAnalysis);
					
					if(rsAnalysis.next()){
						if (counter != 0){
						counter += rsAnalysis.getInt("counter");
						update(cmdUpdate, country.getId(), language.getId(), counter, year, month);
						}
					}
					else
						insert(cmdInsert,country.getId(),language.getId(), counter, year, month);
					
					rsCleanTweet.previous();
					int last = rsCleanTweet.getInt("id");
					String qryUpdBlock = "UPDATE block_analysis SET value = " + last + " WHERE value = " + block;
					cmdBlock.executeUpdate(qryUpdBlock);

					rsCleanTweet.first();
					rsCleanTweet.previous();

					counter = 0;
				}
			}
		} catch (SQLException e) {
			Logging.logging(e.getMessage());
		}
	}

	public static void main(String[] args) {

		Connection connection = JConnection.connect();
		
		
		try (Statement cmdBlock = connection.createStatement();
				Statement cmdCountry = connection.createStatement();
				Statement cmdLanguage = connection.createStatement();
				Statement cmdCleanTweet = connection.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY); Statement cmdInsert = connection.createStatement();
				Statement cmdUpdate = connection.createStatement(); Statement cmdAnalysis = connection.createStatement();) {
			
			connection.setAutoCommit(false);
			
			//block
			String qryBlock = "SELECT value FROM block_analysis";
			ResultSet value = cmdBlock.executeQuery(qryBlock);
			value.next();
			int block = value.getInt("value");

			//countries
			String qryCountry = "SELECT id, country FROM countries";
			ResultSet rsCountry = cmdCountry.executeQuery(qryCountry);

			ArrayList<Country> countries = new ArrayList<Country>();
			while (rsCountry.next()) {
				countries.add(new Country(rsCountry.getString("id"), rsCountry
						.getString("country")));
			}

			//language
			String qryLanguage = "SELECT id, language FROM programming_languages";
			ResultSet rsLanguage = cmdLanguage.executeQuery(qryLanguage);

			ArrayList<Language> languages = new ArrayList<Language>();
			while (rsLanguage.next()) {
				languages.add(new Language(rsLanguage.getInt("id"), rsLanguage
						.getString("language")));
			}

			//clean_tweets
			String qryCleanTweet = "SELECT trash_tweet.text, trash_tweet.id_country, clean_tweet.id FROM trash_tweet, clean_tweet WHERE trash_tweet.id = clean_tweet.id_trash AND clean_tweet.id > "
					+ block;
			ResultSet rsCleanTweet = cmdCleanTweet.executeQuery(qryCleanTweet);

			if (rsCleanTweet.next()) {
				analyze(countries, languages, rsCleanTweet,cmdInsert,cmdUpdate, cmdAnalysis,  block, cmdBlock);
				Logging.logging("Correct Insert");
			}
			else
				Logging.logging("No data to be entered");
			
			connection.commit();

		} catch (SQLException e) {
			Logging.logging(e.getMessage());
		}

	}
}
