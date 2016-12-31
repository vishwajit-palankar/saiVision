package com.saivision.collection.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.saivision.collection.R;
import com.saivision.collection.model.CustomerPOJO;
import com.saivision.collection.utils.DividerItemDecoration;

import java.util.ArrayList;

public class UserListingActivity extends AppCompatActivity implements CustomersAdapter.CustomerClickedListener {
    private ArrayList<CustomerPOJO> customersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_listing);

        initViews();
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.customer_listing);

        customersList = getIntent().getParcelableArrayListExtra(CustomerPOJO.class.getSimpleName());
        RecyclerView rvCustomers = (RecyclerView) findViewById(R.id.rv_user_lists);
        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        rvCustomers.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        CustomersAdapter adapter = new CustomersAdapter(this, customersList);
        adapter.setClickedListener(this);
        rvCustomers.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCustomerClicked(int position) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra(CustomerPOJO.class.getSimpleName(), customersList.get(position));
        startActivity(intent);
    }
}
