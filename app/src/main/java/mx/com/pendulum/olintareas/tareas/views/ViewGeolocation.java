package mx.com.pendulum.olintareas.tareas.views;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.Questions;
import mx.com.pendulum.olintareas.ui.activities.tareas.GetLocationActivity;
import mx.com.pendulum.olintareas.ui.adapter.tareas.DynamicFormAdapter;
import mx.com.pendulum.olintareas.ui.parents.FragmentParent;

public class ViewGeolocation extends ParentViewMain {

    public ViewGeolocation(Context context, Fragment fragment, DynamicFormAdapter adapter) {
        super(context, adapter, fragment);
    }

    public View getView(int position, View convertView, Questions question) {
        DynamicViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.row_dynamic_form_geolocation, null);
        } else {
            holder = (DynamicViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new DynamicViewHolder();
            holder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
            holder.rlAnswer = convertView.findViewById(R.id.rlAnswer);
            holder.tvAnswer = convertView.findViewById(R.id.tvAnswer);
            holder.flError = convertView.findViewById(R.id.flError);
            holder.ivDelete = convertView.findViewById(R.id.ivDelete);
            convertView.setTag(holder);
        }
        super.configureDocsAndComments(question.getAnswerContainerDocsList(), question.getAnswerContainerCommList(), question.getQuestionContainerDocsList(), convertView, question, position);
        if (question.getObject() != null) {
            Log.i("", "");
            LatLng latLng = (LatLng) question.getObject();
            Location loc = new Location("");
            loc.setLatitude(latLng.latitude);
            loc.setLongitude(latLng.longitude);
            setLocation(holder, question, loc);
            question.setObject(null);
        }
        if (getPendingAnswer() != null && question.getAnswer() == null) {
            Log.i("", "");
            try {
                String locStr = getPendingAnswer().getValue();
                String[] array = locStr.split(" , ");
                double lat = Double.parseDouble(array[0]);
                double lon = Double.parseDouble(array[1]);
                Location loc = new Location("");
                loc.setLatitude(lat);
                loc.setLongitude(lon);
                setLocation(holder, question, loc);
            } catch (Exception ignored) {
            }
        }
        setClickAction(holder, question, position);
        super.configureQuestion(question.getQuestionContainerDocsList(), convertView, holder.tvQuestion, question, position);
        if (question.getOptions() != null) {
            super.addOptions(holder.tvAnswer, question);
        }
        setOnClickDelete(holder, question);
        super.setError(holder.flError, question.isError());
        setAddVisible(holder, super.setRespText(question.getAnswer(), holder.tvAnswer, null));
        return convertView;
    }

    private void setOnClickDelete(final DynamicViewHolder holder, final Questions question) {
        if (holder.ivDelete != null)
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tvAnswer.setText("");
                    holder.tvAnswer.setTag(null);
                    question.setAnswer(null);
                    setAddVisible(holder, false);
                }
            });
    }


    private void setClickAction(final DynamicViewHolder holder, final Questions question, final int position) {
        if (holder.rlAnswer != null)
            holder.rlAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

//                    new MyLocation(getContext(), 0, new MyLocation.OnLocationFound() {
//                        @Override
//                        public void onLocationFound(int request, Location location) {
//                            if (location != null) {
//
//                                setLocation(holder, question, location);
//                            }
//
//                        }
//                    });

//                    GeolocationDialog dialog = new GeolocationDialog(getContext());
//                    dialog.showDialog();

                    Intent in = new Intent(getContext(), GetLocationActivity.class);
                    in.putExtra(FragmentParent.IS_HOME_ENABLED, true);
                    in.putExtra(FragmentParent.TITLE_FRAGMENT, question.getQuestion());
                    in.putExtra("POSITION", position);


                    getFragment().startActivityForResult(in, DynamicFormAdapter.GEOLOCATION);

//                    new GeolocationDialog().show(((Activity)getContext()).getFragmentManager(),"TAG");
                }
            });
    }

    private void setLocation(DynamicViewHolder holder, Questions question, Location location) {
        String str = location.getLatitude() + " , " + location.getLongitude();
        holder.tvAnswer.setText(str);
        holder.tvAnswer.setTag(location);
        question.setError(false);
        setError(holder.flError, question.isError());
        question.setAnswer(location);
        setAddVisible(holder, true);
    }

    private void setAddVisible(DynamicViewHolder holder, boolean added) {
        if (holder.ivDelete != null)
            if (added) {
//                holder.tvAnswer.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);
            } else {
//                holder.tvAnswer.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.GONE);
            }
    }


//    public static class DynamicViewHolder {
//        RelativeLayout rlAnswer;
//        CustomTextView tvQuestion;
//        public CustomTextView tvAnswer;
//        public FrameLayout flError;
//        ImageView ivDelete;
//        public List<View> questionContainerDocsList;
//        public List<View> answerContainerDocsList;
//        public List<View> answerContainerCommList;
//    }
}
