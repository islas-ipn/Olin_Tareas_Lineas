package mx.com.pendulum.olintareas.ui.adapter.tareas;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import java.util.List;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.dto.tareasV2.AnswerDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NotasDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.NoteResponseDTO;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.utilities.views.CustomTextView;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {

    private List<NotasDTO> list;
    private Interfaces.OnResponse<Object> mResponse;
    private int handlerCode;
    private Activity activity;

    public NotasAdapter(Activity activity, List<NotasDTO> list, Interfaces.OnResponse<Object> response, int requestResponse) {
        this.list = list;
        this.mResponse = response;
        this.handlerCode = requestResponse;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notas, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NotasDTO notasDTO = list.get(position);
        holder.tvFechaSeguimiento.setText(SeguimientoTarea.convertirFecha(notasDTO.getFechaAlta()));
        holder.tvEstado.setText(notasDTO.getEstado());
        holder.tvUsuarioSeguimiento.setText(notasDTO.getResNombre());
        holder.tvDescripcion.setText(notasDTO.getComentario());
        holder.flFromPencel.setVisibility(notasDTO.isFromPencel() ? View.VISIBLE : View.GONE);
        if (notasDTO.isFromPencel()) {
            holder.flFromPencel.setVisibility(View.VISIBLE);
        } else {
            holder.flFromPencel.setVisibility(View.GONE);
        }
        if (notasDTO.isParcialSave()) {
            holder.flParcialSave.setVisibility(View.VISIBLE);
            holder.flFromPencel.setVisibility(View.GONE);
        } else {
            holder.flParcialSave.setVisibility(View.GONE);
        }
        if (holder.respContainer.getTag() == null) {
            if (notasDTO.isFromPencel()) {
                holder.tvID.setText("");
                if (notasDTO.getAnswerDTOList() == null || notasDTO.getAnswerDTOList().isEmpty()) {
                    holder.respContainer.setVisibility(View.GONE);
                } else {
                    holder.respContainer.removeAllViews();
                    for (AnswerDTO answerDTO : notasDTO.getAnswerDTOList()) {
                        View v = View.inflate(activity, R.layout.row_resp_task_note, null);
                        CustomTextView tvQuestion = v.findViewById(R.id.tvQuestion);
                        CustomTextView tvResponse = v.findViewById(R.id.tvResponse);
                        if (answerDTO.getResponse() == null) continue;
                        if (answerDTO.getResponse().isEmpty()) continue;
                        tvQuestion.setText(answerDTO.getQuestion());
                        tvResponse.setText(answerDTO.getResponse());
                        holder.respContainer.addView(v);
                    }
                    holder.respContainer.setVisibility(View.VISIBLE);
                }
            } else {
                String str = notasDTO.getId() + "";
                holder.tvID.setText(str);
                if (notasDTO.getNoteResponseDTO() == null || notasDTO.getNoteResponseDTO().isEmpty()) {
                    holder.respContainer.setVisibility(View.GONE);
                } else {
                    holder.respContainer.removeAllViews();
                    for (NoteResponseDTO noteResponseDTO : notasDTO.getNoteResponseDTO()) {
                        View v = View.inflate(activity, R.layout.row_resp_task_note, null);
                        CustomTextView tvQuestion = v.findViewById(R.id.tvQuestion);
                        CustomTextView tvResponse = v.findViewById(R.id.tvResponse);
                        tvQuestion.setText(noteResponseDTO.getQuestion());
                        tvResponse.setText(noteResponseDTO.getResponse());
                        holder.respContainer.addView(v);
                    }
                    holder.respContainer.setVisibility(View.VISIBLE);
                }
            }
            holder.respContainer.setTag(1);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CustomTextView tvFechaSeguimiento;
        CustomTextView tvEstado;
        CustomTextView tvUsuarioSeguimiento;
        LinearLayout respContainer;
        View flFromPencel;
        CustomTextView tvDescripcion;
        CustomTextView tvID;
        View flParcialSave;

        ViewHolder(View view) {
            super(view);
            tvFechaSeguimiento = view.findViewById(R.id.tvFechaSeguimiento);
            tvEstado = view.findViewById(R.id.tvEstado);
            tvUsuarioSeguimiento = view.findViewById(R.id.tvUsuarioSeguimiento);
            tvDescripcion = view.findViewById(R.id.tvComentario);
            tvID = view.findViewById(R.id.tvID);
            respContainer = view.findViewById(R.id.respContainer);
            flFromPencel = view.findViewById(R.id.flFromPencel);
            flParcialSave = view.findViewById(R.id.flParcialSave);
            view.findViewById(R.id.row).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mResponse.onResponse(handlerCode, list.get(getAdapterPosition()));
        }
    }
}