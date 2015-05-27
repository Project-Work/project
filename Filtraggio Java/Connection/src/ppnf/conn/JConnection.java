package ppnf.conn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class JConnection {

	
	public static Connection connect(){
		try {
			String driver = "org.postgresql.Driver";
			Class.forName(driver);
			Properties properties = new Properties();
			
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("credentialsDb.properties");
		    properties.load(is);
		    String url = properties.getProperty("url");
		    Connection connection = DriverManager.getConnection (url, properties.getProperty("user"), properties.getProperty("password"));
			
		    return connection;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
}
