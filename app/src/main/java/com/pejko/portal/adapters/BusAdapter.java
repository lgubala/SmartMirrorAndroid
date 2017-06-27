package com.pejko.portal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.casperise.common.utils.FontUtils;
import com.pejko.portal.R;
import com.pejko.portal.entity.ModelBus;
import com.pejko.portal.entity.ModelRss;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusAdapter extends ArrayAdapter<ModelBus>
{

    private List<ModelBus> array;
    private final SimpleDateFormat formatter;
    private LayoutInflater inflater;
    private Context context;

    public BusAdapter(Context context)
    {
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public View getView(final int pos, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bus_item, null);
            holder = new ViewHolder();

            holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            holder.txtFinish = (TextView) convertView.findViewById(R.id.txtFinish);

            holder.txtTime.setTypeface(FontUtils.getBoldTypeface(context));
            holder.txtFinish.setTypeface(FontUtils.getLightTypeface(context));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ModelBus item = array.get(pos);
       // System.out.println("### BUS LIST: " + item.toString());
        holder.txtTime.setText(item.getTime());
        holder.txtFinish.setText(item.getCity() + ", " + item.getStation());

        return convertView;
    }

    @Override
    public int getCount() {
        if (array != null) {
            if (array.size() > 5) {
                return 5;
            } else
                return array.size();
        } else {
            return 0;
        }
    }

    public void setData(ArrayList<ModelBus> array) {
        this.array = array;
        notifyDataSetChanged();
    }

    private class ViewHolder
    {

        public TextView txtTime;
        public TextView txtFinish;

    }
}

// eof

