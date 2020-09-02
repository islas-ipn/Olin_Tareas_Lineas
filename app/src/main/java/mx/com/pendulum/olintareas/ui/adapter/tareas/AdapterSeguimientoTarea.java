package mx.com.pendulum.olintareas.ui.adapter.tareas;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.List;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.dto.tareasV2.ResponseTask;
import mx.com.pendulum.olintareas.dto.tareasV2.SeguimientoTarea;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.utilities.views.CustomTextView;

public class AdapterSeguimientoTarea extends RecyclerView.Adapter<AdapterSeguimientoTarea.Holder> {
    private List<SeguimientoTarea> list;
    private Interfaces.OnResponse<Object> mResponse;
    private int handlerCode;
    private Context context;

    public AdapterSeguimientoTarea(Context context, Interfaces.OnResponse<Object> mResponse, int handlerCode, List<SeguimientoTarea> list) {
        this.list = list;
        this.mResponse = mResponse;
        this.handlerCode = handlerCode;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.row_task_new_list_activity, parent, false);
        return new Holder(contactView);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        SeguimientoTarea dto = list.get(position);
        long date;
        try {
            date = Long.valueOf(dto.getFechaCompromiso());
        } catch (NumberFormatException e) {
            date = Calendar.getInstance().getTimeInMillis() / 1000;
        }
        long date2;
        try {
            date2 = Long.valueOf(dto.getFechaOriginal());
        } catch (NumberFormatException e) {
            date2 = Calendar.getInstance().getTimeInMillis() / 1000;
        }
        Resources res = ContextApplication.getAppContext().getResources();
        holder.txttskresponsble.setText(dto.getResNombre());
        holder.txttskestado.setText(String.valueOf(dto.getDescEstado()));
        holder.txttskde.setText(dto.getDe());
        holder.txttskfecha_inicio.setText(SeguimientoTarea.convertirFecha(date2));
        holder.llCredJui.setVisibility(View.VISIBLE);
        if (dto.getCredito() != null && !dto.getCredito().isEmpty()) {
            holder.tvCredJui.setText("Credito: ");
            holder.txttskcredito.setText(dto.getCredito());
        } else if (dto.getJuicio() != null && !dto.getJuicio().isEmpty()) {
            holder.tvCredJui.setText("Juicio: ");
            holder.txttskcredito.setText(dto.getJuicio());
        } else {
            holder.llCredJui.setVisibility(View.GONE);
        }
        holder.txttsksolicita.setText(dto.getSolNombre());
        holder.txttskactividad.setText(dto.getTipotareaDesc());
        holder.txttskasunto.setText(dto.getAsunto());
        if (dto.getSubClasifica() != null) {
            if (dto.getSubClasifica().equalsIgnoreCase(""))
                holder.llSubAsunto.setVisibility(View.GONE);
            else {
                holder.llSubAsunto.setVisibility(View.VISIBLE);
                holder.txttsksubasunto.setText(dto.getSubClasifica());
            }
        } else {
            holder.llSubAsunto.setVisibility(View.GONE);
        }
        holder.txttskfecha_comp.setText(SeguimientoTarea.convertirFecha(date));
        if (dto.getDescripcion() != null) {
            holder.txttskdescr.setText(Html.fromHtml(dto.getDescripcion()));
        } else {
            holder.txttskdescr.setText("");
        }
        holder.txt_tsk_acreditado.setText(dto.getDeudorNombre() == null ? "" : dto.getDeudorNombre());
        if (dto.isSeguimiento()) {
            holder.txt_id.setText(String.valueOf(dto.getId()));
        } else {
            holder.txt_id.setText("");
        }
        if (dto.getResponseTasks() != null && dto.getResponseTasks().size() > 0) {
            StringBuilder text = new StringBuilder();
            holder.txt_respdinamicas.setVisibility(View.VISIBLE);
            List<ResponseTask> r = dto.getResponseTasks();
            for (ResponseTask d : r) {
                if (d.getResponse() == null) continue;
                text.append(String.format(res.getString(R.string.preguntas), d.getQuestion(), "", d.getResponse())).append("<br>");
            }
            holder.txt_respdinamicas.setText(Html.fromHtml(text.toString()));
            if (dto.getIdSubtipo() != null)
                switch (dto.getIdSubtipo()) {
                    /*case 34:    //Limpieza básica
                    case 47:    //Colocación de lona de venta
                    case 53:    //Completitud de datos
                    case 284:    //Rondín
                    case 366:*/    //Inmueble invadido
                    case 35:    //Limpieza mayor
                    case 367:    //Destapiado
                    case 305:    //Tapiado
                    case 306:    //Destapiado-tapiado
                        holder.txt_respdinamicas.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
        } else
            holder.txt_respdinamicas.setVisibility(View.GONE);
        if (dto.isUpdated())
            holder.flUpdated.setVisibility(View.VISIBLE);
        else
            holder.flUpdated.setVisibility(View.GONE);
        if (dto.isNotaParcialSave()) {
            holder.flNoteParcialSave.setVisibility(View.VISIBLE);
            holder.flUpdated.setVisibility(View.GONE);
        } else
            holder.flNoteParcialSave.setVisibility(View.GONE);
        GradientDrawable drawable = (GradientDrawable) holder.txttskestado.getBackground();
        switch (dto.getEstatus()) {
            case "cerrada":
                drawable.setColor(ContextCompat.getColor(context, R.color.tsk_cerrada));
                holder.txttskestado.setTextColor(ContextCompat.getColor(context, R.color.White));
                break;
            case "incorrecto":
                drawable.setColor(ContextCompat.getColor(context, R.color.tsk_incorrecto));
                holder.txttskestado.setTextColor(ContextCompat.getColor(context, R.color.White));
                break;
            case "correcto":
                drawable.setColor(ContextCompat.getColor(context, R.color.tsk_correcto));
                holder.txttskestado.setTextColor(ContextCompat.getColor(context, R.color.White));
                break;
            case "tiempo":
                drawable.setColor(ContextCompat.getColor(context, R.color.tsk_tiempo));
                holder.txttskestado.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
                break;
            case "vencida":
                drawable.setColor(ContextCompat.getColor(context, R.color.tsk_vencida));
                holder.txttskestado.setTextColor(ContextCompat.getColor(context, R.color.White));
                break;
            case "olin":
                drawable.setColor(ContextCompat.getColor(context, android.R.color.transparent));
                holder.txttskestado.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CustomTextView txttskresponsble;
        CustomTextView txttsksolicita;
        CustomTextView txttskestado;
        CustomTextView txttskactividad;
        CustomTextView txttskde;
        CustomTextView txttskasunto;
        CustomTextView txttsksubasunto;
        CustomTextView txttskcredito;
        CustomTextView tvCredJui;
        LinearLayout llCredJui;
        CustomTextView txttskfecha_inicio;
        CustomTextView txttskfecha_comp;
        CustomTextView txttskdescr;
        CustomTextView txt_respdinamicas;
        CustomTextView txt_tsk_acreditado;
        CustomTextView txt_id;
        View flUpdated;
        View flEstatus;
        View flNoteParcialSave;
        View row;
        View llSubAsunto;

        Holder(View itemView) {
            super(itemView);
            txttskresponsble = itemView.findViewById(R.id.txt_tsk_responsble);
            txttsksolicita = itemView.findViewById(R.id.txt_tsk_solicita);
            txttskestado = itemView.findViewById(R.id.txt_tsk_estado);
            txttskactividad = itemView.findViewById(R.id.txt_tsk_actividad);
            txttskde = itemView.findViewById(R.id.txt_tsk_de);
            txttskasunto = itemView.findViewById(R.id.txt_tsk_asunto);
            llSubAsunto = itemView.findViewById(R.id.llSubAsunto);
            txttsksubasunto = itemView.findViewById(R.id.txt_tsk_sub_asunto);
            txttskcredito = itemView.findViewById(R.id.txt_tsk_credito);
            tvCredJui = itemView.findViewById(R.id.tvCredJui);
            llCredJui = itemView.findViewById(R.id.llCredJui);
            txttskfecha_inicio = itemView.findViewById(R.id.txt_tsk_fecha_inicio);
            txttskfecha_comp = itemView.findViewById(R.id.txt_tsk_fecha_comp);
            txttskdescr = itemView.findViewById(R.id.txt_tsk_descr);
            txt_respdinamicas = itemView.findViewById(R.id.txt_respdinamicas);
            txt_id = itemView.findViewById(R.id.txt_id);
            flUpdated = itemView.findViewById(R.id.flUpdated);
            flEstatus = itemView.findViewById(R.id.flEstatus);
            flNoteParcialSave = itemView.findViewById(R.id.flNoteParcialSave);
            txt_tsk_acreditado = itemView.findViewById(R.id.txt_tsk_acreditado);
            row = itemView.findViewById(R.id.row);
            row.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mResponse.onResponse(handlerCode, list.get(getAdapterPosition()));
        }
    }
}