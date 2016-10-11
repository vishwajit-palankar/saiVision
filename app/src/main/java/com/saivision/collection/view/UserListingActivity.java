package com.saivision.collection.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.saivision.collection.R;
import com.saivision.collection.model.CustomerPOJO;

import java.util.ArrayList;

public class UserListingActivity extends AppCompatActivity {
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
//        rvCustomers.setItemAnimator(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

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
}
