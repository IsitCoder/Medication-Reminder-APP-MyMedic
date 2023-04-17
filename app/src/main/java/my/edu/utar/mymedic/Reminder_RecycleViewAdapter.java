package my.edu.utar.mymedic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.utar.mymedic.model.Reminder;


public class Reminder_RecycleViewAdapter extends RecyclerView.Adapter<Reminder_RecycleViewAdapter.MyViewHolder>{

    Context context;
    ArrayList<Reminder>reminders;


    public Reminder_RecycleViewAdapter(Context context, ArrayList<Reminder>reminders) {
        this.context = context;
        this.reminders = reminders;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reminder_recycle_view_row,parent,false);

        return new Reminder_RecycleViewAdapter.MyViewHolder(view);
    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvReminderName.setText(reminders.get(position).getMedicineName());
        holder.tvReminderTime.setText(reminders.get(position).getAlarmTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),EditReminder.class);
                intent.putExtra("id",reminders.get(holder.getAdapterPosition()).getId());
                v.getContext().startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvReminderName, tvReminderTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReminderName = itemView.findViewById(R.id.reminderName);
            tvReminderTime = itemView.findViewById(R.id.reminderTime);

        }
    }

}
