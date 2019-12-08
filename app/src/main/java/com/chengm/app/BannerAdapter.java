package com.chengm.app;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chengm.cslayout.CSLayout;
import com.chengm.cslayout.ScaleViewGesture;
import com.chengm.cslayout.ShareAnimKt;

import java.util.List;

import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * author : ChenWJ
 * date : 2019/11/9 11:34
 * description : 适配器
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Banner> data;
    private Activity context;

    public BannerAdapter(Activity context, List<Banner> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner, parent, false);
        final CSLayout csLayout = view.findViewById(R.id.cslayout);
        final BannerViewHolder holder = new BannerViewHolder(view);

        new ScaleViewGesture(context)
                .setCustomScale(0.95f)
                .bindToView(csLayout, csLayout)
                .setOnClick(new Function1<View, Unit>() {
                    @Override
                    public Unit invoke(View view) {
                        Intent intent = new Intent(context, TargetActivity.class);
                        intent.putExtra("data", data.get(holder.getAdapterPosition()));

                        Pair<String, View> pair1 = new Pair<>("image", (View) holder.image);
                        Pair<String, View> pair2 = new Pair<>("title", (View) holder.textView);
                        intent = ShareAnimKt.createIntentDef(intent, csLayout, pair1, pair2);
                        context.startActivity(intent);
                        return null;
                    }
                });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BannerViewHolder holder, final int position) {
        holder.image.setImageResource(data.get(position).getResId());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView textView;

        public BannerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.text_title);
        }
    }
}
