package ppnf.analysis.country;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ppnf.conn.JConnection;

public class JCountryAnalyzer {

	public static void insert(PreparedStatement cmdInsert, Country country,
			Language language, int counter) {
		try {
			SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
			int month = Integer.parseInt(sdfMonth.format(new Date()));
			SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
			int year = Integer.parseInt(sdfYear.format(new Date()));
			cmdInsert.setString(1, country.getId());
			cmdInsert.setInt(2, language.getId());
			cmdInsert.setInt(3, counter);
			cmdInsert.setInt(4, month);
			cmdInsert.setInt(5, year);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void analyze(ArrayList<Country> countries,
			ArrayList<Language> languages, ResultSet rsCleanTweet,
			PreparedStatement cmdInsert, int block, Statement cmdBlock) {

		int counter = 0;
		Tweet tweet;

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
								&& tweet.getId_country().equals(country)) {
							counter++;
						}
						if (!language.getLanguage().equals("java")
								&& tweet.getText().contains(
										language.getLanguage())
								&& tweet.getId_country().equals(country)) {
							counter++;
						}
					}

					insert(cmdInsert,country,language, counter);
					
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
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Connection connection = JConnection.connect();
		
		
		try (Statement cmdBlock = connection.createStatement();
				Statement cmdCountry = connection.createStatement();
				Statement cmdLanguage = connection.createStatement();
				Statement cmdCleanTweet = connection.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);) {
			connection.setAutoCommit(false);

			String qryBlock = "SELECT value FROM block_analysis";
			ResultSet value = cmdBlock.executeQuery(qryBlock);
			value.next();
			int block = value.getInt("value");

			String qryCountry = "SELECT id, country FROM countries";
			ResultSet rsCountry = cmdCountry.executeQuery(qryCountry);

			ArrayList<Country> countries = new ArrayList<Country>();
			while (rsCountry.next()) {
				countries.add(new Country(rsCountry.getString("id"), rsCountry
						.getString("country")));
			}

			String qryLanguage = "SELECT id, language FROM programming_languages";
			ResultSet rsLanguage = cmdLanguage.executeQuery(qryLanguage);

			ArrayList<Language> languages = new ArrayList<Language>();
			while (rsLanguage.next()) {
				languages.add(new Language(rsLanguage.getInt("id"), rsLanguage
						.getString("language")));
			}

			String qryCleanTweet = "SELECT trash_tweet.text, trash_tweet.id_country FROM trash_tweet, clean_tweet WHERE trash_tweet.id = clean_tweet.id_trash AND clean_tweet.id > "
					+ block;
			ResultSet rsCleanTweet = cmdCleanTweet.executeQuery(qryCleanTweet);

			String preparedInsClean = "INSERT INTO countries_analysis(country_id, language_id, counter, year, month) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement cmdInsert = connection
					.prepareStatement(preparedInsClean);

			analyze(countries, languages, rsCleanTweet, cmdInsert, block, cmdBlock);
			
			connection.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
