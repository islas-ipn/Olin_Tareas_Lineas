package mx.com.pendulum.olintareas.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.complementos.ManualDTO;
import mx.com.pendulum.olintareas.dto.complementos.ManualTypeDTO;
import mx.com.pendulum.utilities.views.CustomTextView;

public class ManualExpandableAdapter extends BaseExpandableListAdapter {

    private static String TAG = ManualExpandableAdapter.class.getSimpleName();
    private Context context;
    private List<ManualTypeDTO> items;

    public ManualExpandableAdapter(Context context, List<ManualTypeDTO> listDataHeader) {
        this.context = context;
        this.items = listDataHeader;


    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ManualTypeDTO item = items.get(groupPosition);

        ViewParentHoldet holder;
        if (convertView == null) {


            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_parent_manual, null);

            holder = new ViewParentHoldet();
            holder.tvTitulo = convertView.findViewById(R.id.tvTitulo);

            convertView.setTag(holder);
        } else {
            holder = (ViewParentHoldet) convertView.getTag();
        }
        holder.tvTitulo.setText(item.getNombre());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ManualDTO item = items.get(groupPosition).getManualList().get(childPosition);

        ViewChildHolder holder;
        if (convertView == null) {


            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_child_manual, null);

            holder = new ViewChildHolder();
            holder.tvTitulo = convertView.findViewById(R.id.tvTitulo);

            convertView.setTag(holder);
        } else {
            holder = (ViewChildHolder) convertView.getTag();
        }
        holder.tvTitulo.setText(item.getNombre_manual());
        return convertView;
    }


    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(groupPosition).getManualList().size();
    }

    @Override
    public ManualTypeDTO getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public ManualDTO getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).getManualList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return items.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return items.get(groupPosition).getManualList().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class ViewParentHoldet {

        CustomTextView tvTitulo;

    }

    private static class ViewChildHolder {
        CustomTextView tvTitulo;
    }
}
