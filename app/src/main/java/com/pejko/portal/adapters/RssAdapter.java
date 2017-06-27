package com.pejko.portal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.casperise.common.utils.FontUtils;
import com.einmalfel.earl.Item;
import com.pejko.portal.R;
import com.pejko.portal.entity.ModelRss;

import java.util.List;

public class RssAdapter extends ArrayAdapter<ModelRss>
{

    private final List<ModelRss> array;
    private LayoutInflater inflater;
    private Context context;

    public RssAdapter(Context context, List<ModelRss> itemList)
    {
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.array = itemList;
    }

    public View getView(final int pos, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rss_item, null);
            holder = new ViewHolder();

            holder.textTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.textDesc = (TextView) convertView.findViewById(R.id.txtDesc);

            holder.textTitle.setTypeface(FontUtils.getDefaultTypeface(context));
            holder.textDesc.setTypeface(FontUtils.getLightTypeface(context));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ModelRss item = array.get(pos);
        holder.textTitle.setText(item.getTitle());
        holder.textTitle.setSelected(true);
        holder.textTitle.requestFocus();
        holder.textDesc.setText(item.getDescription());
        //http://stackoverflow.com/questions/11311085/androids-textview-with-marquee-in-a-listview-wont-animate-for-the-fist-time-it
        holder.textDesc.setSelected(true);
        holder.textDesc.requestFocus();

        return convertView;
    }

    @Override
    public int getCount() {
        if (array.size() > 4){
            return 4;
        }else
         return array.size();
    }

    private class ViewHolder
    {

        public TextView textTitle;
        public TextView textDesc;

    }
}

// eof

