package com.example.catering_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catering_app.R;
import com.example.catering_app.models.Order;
import java.util.List;

public class NewOrdersAdapter extends RecyclerView.Adapter<NewOrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> ordersList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onOrderAction(String orderId, String action);
    }

    public NewOrdersAdapter(Context context, List<Order> ordersList, OnOrderActionListener listener) {
        this.context = context;
        this.ordersList = ordersList;
        this.listener = listener;
    }

    public void updateList(List<Order> newList) {
        this.ordersList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = ordersList.get(position);

        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvItemCount.setText(order.getItemCount() + " items");
        holder.tvTotalAmount.setText("₹" + order.getTotalAmount());
        holder.tvOrderTime.setText(order.getOrderTime());

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderAction(order.getOrderId(), "accept");
            }
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderAction(order.getOrderId(), "decline");
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList != null ? ordersList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvItemCount, tvTotalAmount, tvOrderTime;
        Button btnAccept, btnDecline;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }
}