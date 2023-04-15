package my.edu.utar.mymedic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.utar.mymedic.model.medicineDto;

public class Medicine_RecycleViewAdapter extends RecyclerView.Adapter<Medicine_RecycleViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<medicineDto>medicineDtos;


    public Medicine_RecycleViewAdapter(Context context, ArrayList<medicineDto> medicineDtos)
    {
        this.context=context;
        this.medicineDtos=medicineDtos;
    }

    @NonNull
    @Override
    public Medicine_RecycleViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.medicine_recycle_view_row,parent,false);

        return new Medicine_RecycleViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Medicine_RecycleViewAdapter.MyViewHolder holder, int position) {
        holder.tvMedicine.setText(medicineDtos.get(position).getMedicineName());
        holder.tvDose.setText("Dose: "+String.format("%.2f", medicineDtos.get(position).getDose()));
        holder.tvVolume.setText("Remaining: "+String.format("%.2f",medicineDtos.get(position).getRemainVolume()));
    }

    @Override
    public int getItemCount() {
        return medicineDtos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedicine,tvVolume,tvDose;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMedicine=itemView.findViewById(R.id.MedicName);
            tvVolume=itemView.findViewById(R.id.Volume);
            tvDose= itemView.findViewById(R.id.RemainingVolume);


        }
    }

}
