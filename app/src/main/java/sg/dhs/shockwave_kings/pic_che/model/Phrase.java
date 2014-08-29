package sg.dhs.shockwave_kings.pic_che.model;

public class Phrase {
	private long id;
    private int catId;
	private String hokkien;
	private String cantonese;
	private String chinese;
	private String english;
	
	public Phrase() {
	}
	
	public Phrase(String hokkien, String cantonese, String chinese, String english){
		this.hokkien = hokkien;
		this.cantonese = cantonese;
		this.chinese = chinese;
		this.english = english;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
    public int getCatId() { return catId; }
    public void setCatId(int id) { this.catId = id; }
	public String getHokkien() {
		return hokkien;
	}
	public void setHokkien(String hokkien) {
		this.hokkien = hokkien;
	}
	public String getCantonese() {
		return cantonese;
	}
	public void setCantonese(String cantonese) {
		this.cantonese = cantonese;
	}
	public String getChinese() {
		return chinese;
	}
	public void setChinese(String chinese) {
		this.chinese = chinese;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	
}
