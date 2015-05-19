import java.util.Date;

public class Tweet {

	private String id;
	private String country;
	private String text;
	private Date data;

	public Tweet(String id, String country, String text, Date data) {
		this.id = id;
		this.country = country;
		this.text = text;
		this.data = data;
	}

	public String getId() {
		return id;
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
