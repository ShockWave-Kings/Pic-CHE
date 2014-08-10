package sg.dhs.pic_che.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import sg.dhs.pic_che.R;

public class PhraseAdapter extends ArrayAdapter<String> {

    Context context;
    String[] hokkienArray;
    String[] cantoneseArray;
    String[] chineseArray;
    String[] englishArray;

    public PhraseAdapter(Context c, String[] hokkien, String[] cantonese, String[] chinese, String[] english){
        super(c, R.layout.list_row, R.id.hokkien, hokkien);
        this.context = c;
        this.hokkienArray = hokkien;
        this.cantoneseArray = cantonese;
        this.chineseArray = chinese;
        this.englishArray = english;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowHolder holder;

        if(row == null){ //1st time
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, parent, false);
            holder = new RowHolder(row);
            row.setTag(holder);
        }
        else{
            holder = (RowHolder) row.getTag();
        }

        String fileName = (position+1)+".png";
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images/"+fileName);
        Bitmap bmp = BitmapFactory.decodeFile(dir.getAbsolutePath());
        holder.img.setImageBitmap(bmp);
        holder.hokkien.setText(hokkienArray[position]);
        holder.cantonese.setText(cantoneseArray[position]);
        holder.chinese.setText(chineseArray[position]);
        holder.english.setText(englishArray[position]);

        return row;
    }

    class RowHolder {

        ImageView img;
        TextView hokkien;
        TextView cantonese;
        TextView chinese;
        TextView english;

        RowHolder(View v){

            img = (ImageView) v.findViewById(R.id.listImage);
            hokkien = (TextView) v.findViewById(R.id.hokkien);
            cantonese = (TextView) v.findViewById(R.id.cantonese);
            chinese = (TextView) v.findViewById(R.id.chinese);
            english = (TextView) v.findViewById(R.id.english);
        }
    }
}