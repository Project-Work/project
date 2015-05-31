package ppnf.logging;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import ppnf.conn.JConnection;

public class Logging {
		// TODO Auto-generated method stub
	
	public static void logging(String text){
		
		Connection connection = JConnection.connect();
		
		String date = new Date().toString();
		
		try {
			Statement cmdLog = connection.createStatement();
			String queryLog = "INSERT INTO logs_java(text, date) VALUES('" + text +"' , '" + date + "')";
			cmdLog.executeUpdate(queryLog);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
