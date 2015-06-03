package ppnf.analysis.european;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ppnf.conn.JConnection;
import ppnf.logging.Logging;;

public class JEuropeanAnalysis {
	
	
	public static void update(int language_id, int sum, Statement cmdUpdate, int year, int month){
		String queryUpdate = "UPDATE european_analysis SET counter = " + sum + " WHERE id_lang=" + language_id + " AND year=" + year +" AND month="+month;
		try {
			cmdUpdate.executeUpdate(queryUpdate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Logging.logging(e.getMessage());
		}
	}
	
	
	public static void insert(int language_id, int sum, Statement cmdInsert, int year, int month){
		
		String queryInsert = "INSERT INTO european_analysis(id_lang, counter, year, month) VALUES("+ language_id+", " + sum +" , " + year + ", " + month +") ";
		try {
			cmdInsert.executeUpdate(queryInsert);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Logging.logging(e.getMessage());
		}
	}	
	
	public static void analyze(ArrayList<Language> languages, Statement cmdCountriesAnalysis, Statement cmdEuropeanAnalysis, Statement cmdInsert, Statement cmdUpdate){
		try {
			
			SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
			int month = Integer.parseInt(sdfMonth.format(new Date()));
			SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
			int year = Integer.parseInt(sdfYear.format(new Date()));
		for (Language language : languages) {
			
			
				String queryCountriesAnalysis = "SELECT language_id, SUM(counter)  FROM countries_analysis WHERE language_id = " + language.getId()+" AND year= " + year +" AND month=" + month+" GROUP BY language_id";
				ResultSet rsCountriesAnalysis = cmdCountriesAnalysis.executeQuery(queryCountriesAnalysis);
				rsCountriesAnalysis.next();
				
				String queryEuropeanAnalysis = "SELECT * FROM european_analysis WHERE id_lang=" + language.getId() + " AND year= " + year +" AND month=" + month;
				ResultSet rsEuropeanAnalysis = cmdEuropeanAnalysis.executeQuery(queryEuropeanAnalysis);
				
				int sum = rsCountriesAnalysis.getInt("sum");
				if(rsEuropeanAnalysis.next()){
					update(language.getId(), sum, cmdUpdate, year, month);
				}
				else
					insert(language.getId(), sum, cmdInsert, year, month);
			}
		} catch (SQLException e) {
				// TODO Auto-generated catch block
				Logging.logging(e.getMessage());
			}
			
		
		
	}

	public static void main(String[] args) {
		
		Connection connection = JConnection.connect();
		
		try(Statement cmdLanguages = connection.createStatement();
				Statement cmdCountriesAnalysis = connection.createStatement();
				Statement cmdEuropeanAnalysis = connection.createStatement();
				Statement cmdInsert = connection.createStatement();
				Statement cmdUpdate = connection.createStatement();) {
			connection.setAutoCommit(false);
			String queryLanguages = "SELECT * FROM programming_languages";
			ResultSet rsLanguages = cmdLanguages.executeQuery(queryLanguages);
			
			ArrayList<Language> languages = new ArrayList<Language>();
			while(rsLanguages.next()){
				languages.add(new Language(rsLanguages.getInt("id"), rsLanguages.getString("language")));
			}
			
			analyze(languages, cmdCountriesAnalysis, cmdEuropeanAnalysis, cmdInsert, cmdUpdate);
			Logging.logging("Correct Insert");
			
			connection.commit();
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			Logging.logging(e.getMessage());
		}

	}

}
