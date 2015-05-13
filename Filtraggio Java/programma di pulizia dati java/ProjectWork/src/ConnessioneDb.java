import java.sql.*;

public class ConnessioneDb {

	public static void main(String[] args) {
		
		try {
			// Carichiamo un driver di tipo 1 (bridge jdbc-odbc)
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			// Creiamo la stringa di connessione
			String url = "jdbc:postgresql://192.168.0.21:5432/projectwork";
			// Otteniamo una connessione con username e password
			Connection connessione = DriverManager.getConnection(url, "ecommerce",
					"password");
			// Creiamo un oggetto Statement per poter interrogare il db
			Statement cmd = connessione.createStatement();
			// Eseguiamo una query e immagazziniamone i risultati
			// in un oggetto ResultSet
			String query = "SELECT countries.country, date, text, programming_languages.lang, retweet FROM trash_tweet, countries, programming_languages"
                    + " WHERE countries.id = trash_tweet.id_country AND programming_languages.id = trash_tweet.id_lang";
            ResultSet risultato = cmd.executeQuery(query);
            // Stampiamone i risultati riga per riga

            while (risultato.next()) {
                System.out.println("------------------------------------------------");
                System.out.println("country: " + risultato.getString("country"));
                System.out.println("date: " + risultato.getString("date"));
                System.out.println("text: " + risultato.getString("text"));
                System.out.println("lang: " + risultato.getString("lang"));
                System.out.println("retweet: " + risultato.getString("retweet"));   
                
                int retweet = Integer.parseInt (risultato.getString("retweet"));
                int pesoRetweet = 0;
                
                if(retweet > 0 && retweet <= 10) {
                	
                	pesoRetweet = 1;
                }
                
                else if(retweet > 11 && retweet <= 15) {
                	
                	pesoRetweet = 2;
                }
                
                else if(retweet > 15 && retweet <= 20) {
                	
                	pesoRetweet = 4;
                }
                
                else if(retweet > 20 && retweet <= 30) {
                	
                	pesoRetweet = 7;
                }
                
                else if(retweet > 30 && retweet <= 50) {
                	
                	pesoRetweet = 11;
                }
                
                else if(retweet > 50 && retweet <= 70) {
                	
                	pesoRetweet = 15;
                }
                
                else if(retweet > 70 && retweet <= 100) {
                	
                	pesoRetweet = 20;
                }
                
                else if(retweet > 100 && retweet <= 150) {
                	
                	pesoRetweet = 27;
                }
                
                else if(retweet > 150 && retweet <= 200) {
                	
                	pesoRetweet = 40;
                }
                
                else if(retweet > 200 && retweet <= 300) {
                	
                	pesoRetweet = 58;
                }
                
                System.out.println(pesoRetweet);
            }
            
            
            
          
            risultato.close();
			cmd.close();
			connessione.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
