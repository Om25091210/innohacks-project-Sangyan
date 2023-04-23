package com.alpha.innohacksproject.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alpha.innohacksproject.Model.NumberModel;
import com.alpha.innohacksproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.ViewHolder> {

    List<NumberModel> list;
    Context context;
    String district;
    TextView yes, no;
    boolean isadmin;
    private Timer timer;


    public NumberAdapter(Context context, List<NumberModel> list, String district) {
        this.context = context;
        this.list = list;
        this.district = district;
        isadmin = context.getSharedPreferences("isAdmin_or_not", MODE_PRIVATE)
                .getBoolean("authorizing_admin", false);
    }

    public void setTasks(List<NumberModel> todoList) {
        this.list = todoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_number_data, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Phone numbers");
        String ps_name = list.get(position).getName();
        String ps_number = list.get(position).getNumber();
        holder.name.setText(ps_name);

        holder.number.setText("+91 " + ps_number);


        holder.call_btn.setOnClickListener(v -> {
            String phone = "+91" + ps_number;
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            context.startActivity(intent);
        });

        holder.wp_btn.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=" + "+91" + ps_number;
            try {
                PackageManager pm = v.getContext().getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                v.getContext().startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        holder.share_btn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Download App");
                String message = "District - " + district + "\n" + "Police Station - " + ps_name + "\n" +
                        "Phone Number - " + "+91 " + ps_number + "\n\n⭐SANGYAN | INNOHACKS⭐";
                intent.putExtra(Intent.EXTRA_TEXT, message);
                context.startActivity(Intent.createChooser(intent, "Share using"));
            } catch (Exception e) {
                Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });

        holder.delete.setOnClickListener(v -> {

            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_delete);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            dialog.setCancelable(false);
            yes = dialog.findViewById(R.id.yes);
            no = dialog.findViewById(R.id.no);

            yes.setOnClickListener(v2 -> {
                reference.child(district).child(list.get(position).getName()).removeValue();
                dialog.dismiss();
            });

            no.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
        });
        if (isadmin)
            holder.delete.setVisibility(View.VISIBLE);
        else
            holder.delete.setVisibility(View.GONE);

        holder.sms_btn.setOnClickListener(v -> {
            Uri sms_uri = Uri.parse("smsto:+" + ps_number);
            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            sms_intent.putExtra("sms_body", "To : " + ps_name);
            context.startActivity(sms_intent);
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;
        ImageView call_btn, wp_btn, delete, share_btn, sms_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ps_name);
            number = itemView.findViewById(R.id.ps_number);
            call_btn = itemView.findViewById(R.id.call);
            wp_btn = itemView.findViewById(R.id.wp);
            delete = itemView.findViewById(R.id.delete);
            share_btn = itemView.findViewById(R.id.share);
            sms_btn = itemView.findViewById(R.id.sms);
        }
    }

    public void searchNotes(final String searchKeyword) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {

                if (searchKeyword.trim().isEmpty()) {
                    ArrayList<NumberModel> xyz = new ArrayList<>();
                    for (NumberModel mode : list) {
                        xyz.add(mode);
                    }
                    list = xyz;
                } else {
                    ArrayList<NumberModel> temp = new ArrayList<>();
                    for (NumberModel note : list) {
                        if (note.getName().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        } else if (note.getNumber().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    list = temp;
                }
                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 100);
    }
        public void cancelTimer() {
            if (timer != null) {
                timer.cancel();
            }
        }

}
