package sg.dhs.shockwave_kings.pic_che.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sg.dhs.shockwave_kings.pic_che.R;
import sg.dhs.shockwave_kings.pic_che.model.Phrase;


public class PhraseAdapter extends BaseAdapter {
    private Activity context;
    private List<Phrase> phrases;
    private List<Phrase> phrases_back;

    public PhraseAdapter(Activity context, List<Phrase> phrases){
        this.context = context;
        this.phrases = phrases;
        phrases_back = new ArrayList<Phrase>();
        phrases_back.addAll(phrases);
    }

    @Override
    public int getCount() {
        return phrases.size();
    }

    @Override
    public Object getItem(int position) {
        return phrases.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Phrase phrase = (Phrase) getItem(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_row, parent, false);
        }
        ImageView imgView = (ImageView) convertView.findViewById(R.id.listImage);

        String fileName;

        if(phrase.getCatId()==256) {
            fileName = "self_" + phrase.getId() + ".png";
        }
        else {
            fileName = phrase.getId() + ".png";
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images/"+fileName);
        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());

        imgView.setImageBitmap(bm);

        TextView hokkien = (TextView) convertView.findViewById(R.id.hokkien);
        TextView cantonese = (TextView) convertView.findViewById(R.id.cantonese);
        TextView chinese = (TextView) convertView.findViewById(R.id.chinese);
        TextView english = (TextView) convertView.findViewById(R.id.english);

        hokkien.setText("Hok: "+phrase.getHokkien());
        cantonese.setText("Can: "+phrase.getCantonese());
        chinese.setText("中文:"+phrase.getChinese());
        english.setText("Eng: "+phrase.getEnglish());

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        phrases.clear();
        if (charText.length() == 0) {
            phrases.addAll(phrases_back);
        } else {
            for (Phrase phrase : phrases_back) {
                String combined = phrase.getChinese() + phrase.getCantonese() + phrase.getEnglish() + phrase.getHokkien();
                if (combined.toLowerCase(Locale.getDefault()).contains(charText)) {
                    phrases.add(phrase);
                }

            }

        }
        notifyDataSetChanged();
    }

}
