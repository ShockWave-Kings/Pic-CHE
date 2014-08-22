package sg.dhs.pic_che.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import sg.dhs.pic_che.R;
import sg.dhs.pic_che.model.Category;

public class NavigationAdapter extends BaseAdapter{
    private Activity context;
    private List<Category> categories;

    public NavigationAdapter(Activity context, List<Category> categories){
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category category = (Category) getItem(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, parent, false);
        }

        ImageView imgView = (ImageView) convertView.findViewById(R.id.groupImage);

        String fileName;
        fileName = "cat_"+category.getId()+".png";

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images/"+fileName);
        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());

        imgView.setImageBitmap(bm);

        TextView english = (TextView) convertView.findViewById(R.id.groupEnglish);

        english.setText(category.getEnglish());

        return convertView;
    }
}
