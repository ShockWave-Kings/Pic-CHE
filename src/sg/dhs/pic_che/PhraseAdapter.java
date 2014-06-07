package sg.dhs.pic_che;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhraseAdapter extends ArrayAdapter<String>{

	Context context;
	int[] images;
	String[] hokkienArray;
	String[] cantoneseArray;
	String[] chineseArray;
	String[] englishArray;
	
    PhraseAdapter(Context c, int[] imgs, String[] hokkien, String[] cantonese, String[] chinese, String[] english){
    	super(c, R.layout.list_row, R.id.hokkien, hokkien);
    	this.context = c;
    	this.images = imgs;
    	this.hokkienArray = hokkien;
    	this.cantoneseArray = cantonese;
    	this.chineseArray = chinese;
    	this.englishArray = english;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View row = inflater.inflate(R.layout.list_row, parent, false);
    	ImageView image = (ImageView) row.findViewById(R.id.listImage);
    	TextView hokkienText = (TextView) row.findViewById(R.id.hokkien);
    	TextView cantoneseText = (TextView) row.findViewById(R.id.cantonese);
    	TextView chineseText = (TextView) row.findViewById(R.id.chinese);
    	TextView englishText = (TextView) row.findViewById(R.id.english);
    	
    	image.setImageResource(images[position]);
    	hokkienText.setText(hokkienArray[position]);
    	cantoneseText.setText(cantoneseArray[position]);
    	chineseText.setText(chineseArray[position]);
    	englishText.setText(englishArray[position]);
    	
    	
    	return row;
    }
    
}
