package mx.com.pendulum.olintareas.ui.adapter.tareas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.db.dao.SepoColony;
import mx.com.pendulum.olintareas.db.dao.SepoMuni;
import mx.com.pendulum.utilities.views.CustomTextView;

public class SepoColonyAdapter extends ArrayAdapter<SepoColony> {

    private List<SepoColony> items;

    public SepoColonyAdapter(Context context, List<SepoColony> items) {
        super(context, 0, 0, items);
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SepoColony getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_spinner, parent, false);
        }
        CustomTextView textViewName = convertView.findViewById(R.id.tvDescription);
        SepoColony currentItem = getItem(position);
        if (currentItem != null) {
            textViewName.setText(currentItem.getName());
        }
        return convertView;
    }
}