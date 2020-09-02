package mx.com.pendulum.olintareas.camera;

import android.app.Fragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.ui.adapter.GalleryTakePhotoAdapter;
import mx.com.pendulum.utilities.recycler.OnStartDragListener;
import mx.com.pendulum.utilities.recycler.SimpleItemTouchHelperCallback;

public class GalleryTakePhotoFragment extends Fragment implements OnStartDragListener, Interfaces.OnResponse {

    private GalleryTakePhotoAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private static GalleryTakePhotoFragment fragment;
    Interfaces.OnResponse response;
    int handlerCode;

    


    public static GalleryTakePhotoFragment newInstance() {
        if (fragment == null) {
            fragment = new GalleryTakePhotoFragment();
        }
        return fragment;
    }

    public void setOnResponse(Interfaces.OnResponse response, int handlerCode) {
        this.response = response;
        this.handlerCode = handlerCode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GalleryTakePhotoAdapter(getActivity(), this, new ArrayList<String>(), false);
        adapter.setOnResponseListener(this, 23);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView rv = view.findViewById(R.id.rvTakePhoto);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(getActivity(), adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rv);
    }


    public boolean itemExiste(String file){
        boolean exito= false;
        ArrayList<String> a = adapter.getItems();
        for(String b : a){
            if(file.equals(b)){
                exito = true;
                break;
            }
        }
        return exito;
    }

    public void itemWasAdded(String file) {
        RecyclerView rv = getView().findViewById(R.id.rvTakePhoto);
        adapter.add(file, rv);

    }

    public void itemsWereModified() {
        adapter.notifyDataSetChanged();
    }

    public ArrayList<String> getItems() {
        if (adapter == null) {
            return new ArrayList<>();
        }
        return adapter.getItems();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onResponse(int handlerCode, Object o) {
        if (handlerCode == 23) {
            if (response != null) {
                response.onResponse(handlerCode, o);
            }
        } else if (handlerCode == CaptureVideoActivity.VIDEO_PREVIEW) {
            if (response != null) {
                response.onResponse(handlerCode, o);
            }
        }
    }


}