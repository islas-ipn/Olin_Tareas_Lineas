package mx.com.pendulum.olintareas.config.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Options;
import mx.com.pendulum.utilities.views.CustomTextView;

public class SpinnerAdapter extends BaseAdapter {

    private List<Options> items;
    private Context context;

    public SpinnerAdapter(Context context, List<Options> options) {
        this.items = options;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        view = View.inflate(context, R.layout.row_spinner, null);
        final TextView textView = view.findViewById(R.id.tvDescription);
        textView.setText(items.get(position).getOption());
        return view;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Options item = (Options) getItem(position);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.row_spinner, parent, false);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvDescription.setText(item.getOption());
        return convertView;
    }

    private static class ViewHolder {
        private CustomTextView tvDescription;
    }
}