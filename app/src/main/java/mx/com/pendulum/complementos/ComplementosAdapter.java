package mx.com.pendulum.complementos;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.complementos.Item;
import mx.com.pendulum.utilities.Tools;
import mx.com.pendulum.utilities.views.CustomTextView;

public class ComplementosAdapter extends BaseAdapter {

    private List<Item> list;
    private Context context;

    public ComplementosAdapter(List<Item> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = (Item) getItem(position);
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.row_complementos, parent, false);


            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.ivImage = convertView.findViewById(R.id.ivImage);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvName.setText(item.getName());


//        String image;
        if (item.getPackageName().startsWith("http")) {


            // resourceId = R.drawable.ic_play_arrow;

            if (item.getPackageName().endsWith("avi")) {
                holder.ivImage.setImageResource(R.drawable.ic_movie);
            } else {
                holder.ivImage.setImageResource(R.drawable.ic_http);
            }


        } else {
            ImageLoader.getInstance().displayImage(item.getUrlImage(), holder.ivImage, Tools.getImageLoaderOptions(R.mipmap.tmp_icon));

        }


        return convertView;
    }

    private static class ViewHolder {

        private CustomTextView tvName;
        private ImageView ivImage;

    }
}

