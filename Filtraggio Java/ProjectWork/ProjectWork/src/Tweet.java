import java.util.Date;

public class Tweet {

	private int id;
	private String id_str;
	private String country;
	private String text;
	private Date data;

	public Tweet(int id, String id_str, String country, String text, Date data) {
		this.id = id;
		this.id_str = id_str;
		this.country = country;
		this.text = text;
		this.data = data;
	}

	public int getId() {
		return id;
	}
	
	public String getId_str() {
		return id_str;
	}

	public String getCountry() {
		return country;
	}

	public String getText() {
		return text;
	}

	public Date getData() {
		return data;
	}
}

