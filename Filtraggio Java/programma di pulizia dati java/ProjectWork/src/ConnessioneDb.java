import java.sql.*;

public class ConnessioneDb {

	public static void main(String[] args) {

		// ESTRAZIONE DEI TWEET

		try {
			// Carichiamo un driver di tipo 1 (bridge jdbc-odbc)
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			// Creiamo la stringa di connessione
			String url = "jdbc:postgresql://192.168.0.23:5432/projectwork";
			// Otteniamo una connessione con username e password
			Connection connessione = DriverManager.getConnection(url,
					"ecommerce", "password");
			// Creiamo un oggetto Statement per poter interrogare il db
			Statement cmd = connessione.createStatement();
			// Eseguiamo una query e immagazziniamone i risultati
			// in un oggetto ResultSet
			String query = "SELECT trash_tweet.id, countries.country, date, text, retweet FROM trash_tweet, countries"
					+ " WHERE countries.id = trash_tweet.id_country";
			ResultSet risultato = cmd.executeQuery(query);

			// #################################################################################################################

			// STAMPA RISULTATO QUERY

			int countJavascript = 0;
			int countJava = 0;
			int countC = 0;
			int countCpp = 0;
			int countPy = 0;
			int countObjective_c = 0;
			int countC_sharp = 0;
			int countPhp = 0;
			int countVisual_basic = 0;
			int countVisual_basic_Net = 0;
			int countDelphiObject_pascal = 0;
			int countPerl = 0;
			int countTransact_Sql = 0;
			int countMatlab = 0;
			int countAbap = 0;
			int countF_sharp = 0;
			int countPL_Sql = 0;
			int countRuby = 0;
			int countPascal = 0;
			int countR = 0;

			String text;

			while (risultato.next()) {
				System.out
						.println("------------------------------------------------");
				System.out.println("lang: " + risultato.getString("id"));
				System.out
						.println("country: " + risultato.getString("country"));
				System.out.println("date: " + risultato.getString("date"));
				System.out.println("text: " + risultato.getString("text"));
				System.out
						.println("retweet: " + risultato.getString("retweet"));

				// ###################################################################################################################

				// FILTRI

				text = risultato.getString("text").toLowerCase();

				if ((text.contains("javascript"))) {

					countJavascript++;
				} else if (text.contains("java ") || text.contains("java#")) {

					countJava++;
				} else if (text.contains("python") || text.contains("python#")) {

					countPy++;
				} else if (text.contains("c") || text.contains("c#")) {

					countC++;
				} else if (text.contains("cpp") || text.contains("cpp#")) {

					countCpp++;
				} else if (text.contains("objective_c")
						|| text.contains("objective_c#")) {

					countObjective_c++;
				} else if (text.contains("c_sharp")
						|| text.contains("c_sharp#")) {

					countC_sharp++;
				} else if (text.contains("php") || text.contains("php#")) {

					countPhp++;
				} else if (text.contains("visual_basic")
						|| text.contains("visual_basic#")) {

					countVisual_basic++;
				} else if (text.contains("visual_basic_Net")
						|| text.contains("visual_basic_Net#")) {

					countVisual_basic_Net++;
				} else if (text.contains("delphiObject_pascal")
						|| text.contains("delphiObject_pascal#")) {

					countDelphiObject_pascal++;
				} else if (text.contains("perl") || text.contains("perl#")) {

					countPerl++;
				} else if (text.contains("transact_Sql")
						|| text.contains("transact_Sql#")) {

					countTransact_Sql++;
				} else if (text.contains("matlab") || text.contains("matlab#")) {

					countMatlab++;
				} else if (text.contains("abap") || text.contains("abap#")) {

					countAbap++;
				} else if (text.contains("f_sharp")
						|| text.contains("f_sharp#")) {

					countF_sharp++;
				} else if (text.contains("pL_Sql") || text.contains("pL_Sql#")) {

					countPL_Sql++;
				} else if (text.contains("ruby") || text.contains("ruby#")) {

					countRuby++;
				} else if (text.contains("pascal") || text.contains("pascal#")) {

					countPascal++;
				} else if (text.contains("r") || text.contains("r#")) {

					countPascal++;
				}
				// ########################################################################################################
			}

			System.out.println("########################################################################################################");
			System.out.println("countJavascript: " + countJavascript + " || countJava:  " + countJava + " || countC: "
					+ countC + " \n|| countCpp: " + countCpp + " || countPy:" + countPy + " || countObjective_c: "
					+ countObjective_c + " || countC_sharp: " + countC_sharp + " \n|| countPhp: " + countPhp
					+ " || countVisual_basic: " + countVisual_basic + " || countVisual_basic_Net: " + countVisual_basic_Net
					+ " || countDelphiObject_pascal: " + countDelphiObject_pascal + " \n|| countPerl: " + countPerl
					+ " || countTransact_Sql: " + countTransact_Sql + " || countMatlab: " + countMatlab + " || countAbap: "
					+ countAbap + " \n|| countF_sharp: " + countF_sharp + " || countPL_Sql: " + countPL_Sql
					+ " || countRuby: " + countRuby + " || countPascal: " + countPascal + " || countR: " + countR);

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