package ppnf.analysis.country;

public class Tweet {
	private String text;
	private String id_country;
	
	public Tweet(String text, String id_country){
		this.text = text;
		this.id_country = id_country;
	}

	public String getText() {
		return text;
	}

	public String getId_country() {
		return id_country;
	}

}
