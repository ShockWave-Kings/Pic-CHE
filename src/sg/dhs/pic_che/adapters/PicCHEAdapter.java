package sg.dhs.pic_che.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Map;

import sg.dhs.pic_che.R;
import sg.dhs.pic_che.model.Category;
import sg.dhs.pic_che.model.Phrase;

public class PicCHEAdapter extends BaseExpandableListAdapter{

    Activity context;
    private Map<Category, List<Phrase>> picches;
    private List<Category> categories;

    public PicCHEAdapter(Activity context, List<Category> categories, Map<Category, List<Phrase>> picches){
        this.context = context;
        this.categories = categories;
        this.picches = picches;
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return picches.get(categories.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Category category = (Category) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, parent, false);
        }

        TextView hokkien = (TextView) convertView.findViewById(R.id.groupHokkien);
        TextView cantonese = (TextView) convertView.findViewById(R.id.groupCantonese);
        TextView chinese = (TextView) convertView.findViewById(R.id.groupChinese);
        TextView english = (TextView) convertView.findViewById(R.id.groupEnglish);

        hokkien.setText(category.getHokkien());
        cantonese.setText(category.getCantonese());
        chinese.setText(category.getChinese());
        english.setText(category.getEnglish());

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return picches.get(categories.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Phrase picche = (Phrase) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, parent, false);
        }
        ImageView imgView = (ImageView) convertView.findViewById(R.id.listImage);

        String fileName;

        if(picche.getCatId()==256) {
            fileName = "self_" + picche.getId() + ".png";
        }
        else {
            fileName = picche.getId() + ".png";
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PICCHE/PIC-CHE_Images/"+fileName);
        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());

        imgView.setImageBitmap(bm);

        TextView hokkien = (TextView) convertView.findViewById(R.id.hokkien);
        TextView cantonese = (TextView) convertView.findViewById(R.id.cantonese);
        TextView chinese = (TextView) convertView.findViewById(R.id.chinese);
        TextView english = (TextView) convertView.findViewById(R.id.english);

        hokkien.setText(picche.getHokkien());
        cantonese.setText(picche.getCantonese());
        chinese.setText(picche.getChinese());
        english.setText(picche.getEnglish());

        return convertView;
    }



    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
