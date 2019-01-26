package com.checkin.app.checkin.ManagerProfile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopManagerTableStaticsAdapter extends RecyclerView.Adapter<ShopManagerTableStaticsAdapter.ShopManagerTableStaticsHolder> {

    public ShopManagerTableStaticsAdapter(){
    }

    @NonNull
    @Override
    public ShopManagerTableStaticsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_shop_manger_table_statics_item, parent, false);
        return new ShopManagerTableStaticsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopManagerTableStaticsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ShopManagerTableStaticsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_shop_manager_table_statics_item)
        ImageView ivShopManagerTableStaticsItem;
        @BindView(R.id.tv_shop_manager_table_statics_item)
        TextView tvShopManagerTableStaticsItem;
        @BindView(R.id.tv_shop_manager_table_statics_item_description)
        TextView tvShopManagerTableStaticsItemDescription;

        ShopManagerTableStaticsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
