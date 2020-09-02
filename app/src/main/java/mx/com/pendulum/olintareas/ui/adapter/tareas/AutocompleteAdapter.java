package mx.com.pendulum.olintareas.ui.adapter.tareas;


import android.content.Context;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Autocomplete;
import mx.com.pendulum.utilities.views.CustomTextView;

public class AutocompleteAdapter extends ArrayAdapter<Autocomplete> {

    private Context context;
    private List<Autocomplete> items, tempItems, suggestions;

    public AutocompleteAdapter(Context context, List<Autocomplete> items) {
        super(context, 0, 0, items);
        this.context = context;
        this.items = items;
        if (this.items != null)
            tempItems = new ArrayList<>(items); // this makes the difference.
        else
            tempItems = new ArrayList<>(); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_dynamic_form_row_autocomplete, parent, false);
        }
        Autocomplete autocomplete = items.get(position);
        if (autocomplete != null) {
            CustomTextView lblName = (CustomTextView) view.findViewById(R.id.text1);
            if (lblName != null)
                lblName.setText(autocomplete.getLabel());
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    private Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Autocomplete autocomplete = (Autocomplete) resultValue;
            String str = autocomplete.getLabel();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                if (constraint.length() < 3) {
                    return new FilterResults();
                }

                suggestions.clear();
                for (Autocomplete autocomplete : tempItems) {
                    try {
                        if (autocomplete.getLabel().toLowerCase().contains(constraint.toString().toLowerCase()) || autocomplete.getValue().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(autocomplete);
                        }
                    } catch (Exception ignored) {
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Autocomplete> filterList = (ArrayList<Autocomplete>) results.values;
            if (results.count > 0) {
                clear();
                try {
                    for (Autocomplete autocomplete : filterList) {
                        add(autocomplete);
                        notifyDataSetChanged();
                    }
                } catch (Exception ignored) {

                }
            }
        }
    };

}

