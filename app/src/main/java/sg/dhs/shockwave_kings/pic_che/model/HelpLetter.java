package sg.dhs.shockwave_kings.pic_che.model;

/**
 * Created by Jerome Leow on 19/11/2014.
 */
public class HelpLetter {

    private String letter;
    private String example;
    private String chinese;

    public HelpLetter() {
    }

    public HelpLetter(String l, String e, String c){
        this.letter = l;
        this.example = e;
        this.chinese = c;
    }

    public String getLetter(){ return letter; }

    public void setLetter(String l) {
        this.letter = l;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String e) {
        this.example = e;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String c) {
        this.chinese = c;
    }
}
