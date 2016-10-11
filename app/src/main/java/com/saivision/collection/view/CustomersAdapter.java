package com.saivision.collection.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saivision.collection.R;
import com.saivision.collection.model.CustomerPOJO;

import java.util.ArrayList;

/**
 * Created by Admin on 11/10/2016.
 */

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CustomerPOJO> customersList;
    private CustomerClickedListener listener;

    public CustomersAdapter(Context context, ArrayList<CustomerPOJO> customersList) {
        this.context = context;
        this.customersList = customersList;
    }

    public void setClickedListener(CustomerClickedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomerPOJO customer = customersList.get(position);
        holder.tvName.setText(customer.getFname() + " " + customer.getLname());
//        holder.tvAddress.setText(customer.get);
        holder.tvRemaining.setText(String.format("Rs. %s", customer.getPhoneNumber()));
        holder.tvLastPaymentDate.setText(customer.getPaymentDate());
        holder.tvMobile.setText(customer.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return customersList != null ? customersList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvAddress;
        TextView tvMobile;
        TextView tvRemaining;
        TextView tvLastPaymentDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            tvMobile = (TextView) itemView.findViewById(R.id.tv_number);
            tvRemaining = (TextView) itemView.findViewById(R.id.tv_remaining);
            tvLastPaymentDate = (TextView) itemView.findViewById(R.id.tv_last_payment_date);
        }
    }

    public interface CustomerClickedListener {
        void onCustomerClicked(int position);
    }
}
