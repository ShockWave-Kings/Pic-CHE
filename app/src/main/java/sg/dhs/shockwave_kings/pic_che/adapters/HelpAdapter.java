package sg.dhs.shockwave_kings.pic_che.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sg.dhs.shockwave_kings.pic_che.R;
import sg.dhs.shockwave_kings.pic_che.model.HelpLetter;

public class HelpAdapter extends BaseAdapter{
    private Activity context;
    private List<HelpLetter> letters;

    public HelpAdapter(Activity context, List<HelpLetter> letters) {
        this.context = context;
        this.letters = letters;
    }

    @Override
    public int getCount() {
        return letters.size();
    }

    @Override
    public Object getItem(int position) {
        return letters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HelpLetter letter = (HelpLetter) getItem(position);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_help, parent, false);
        }
        TextView letterText = (TextView) convertView.findViewById(R.id.helpLetterTextView);
        TextView exampleText = (TextView) convertView.findViewById(R.id.helpExampleTextView);
        TextView chineseText = (TextView) convertView.findViewById(R.id.helpChineseTextView);

        letterText.setText(letter.getLetter());
        exampleText.setText(letter.getExample());
        chineseText.setText(letter.getChinese());

        return convertView;
    }
}
