package com.saivision.collection.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.saivision.collection.R;
import com.saivision.collection.SaiVisionApplication;
import com.saivision.collection.model.CustomerPOJO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FilterActivity.class.getSimpleName();
    private EditText mETId;
    private EditText mETFirstName;
    private EditText mETLastname;
    private EditText mETAddress;
    private Button mBTNSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initViews();
    }

    private void initViews() {
        mETId = (EditText) findViewById(R.id.et_id);
        mETFirstName = (EditText) findViewById(R.id.et_first_name);
        mETLastname = (EditText) findViewById(R.id.et_last_name);
        mETAddress = (EditText) findViewById(R.id.et_address);
        mBTNSearch = (Button) findViewById(R.id.btn_search);

        mBTNSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (validateViews()) {
            callFilterApi();
        } else {

            new SweetAlertDialog(FilterActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.warning))
                    .setContentText(getString(R.string.all_fields_empty))
                    .show();
        }
    }

    private void callFilterApi() {
        try {

            String id = mETId.getText().toString().trim();
            String firstName = mETFirstName.getText().toString().trim();
            String lastName = mETLastname.getText().toString().trim();
            String address = mETAddress.getText().toString().trim();

            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);
            dialog.setCancelable(false);

            JSONObject jsonObject = new JSONObject();
            if (!id.isEmpty())
                jsonObject.put("id", id);
            if (!firstName.isEmpty())
                jsonObject.put("fname", firstName);
            if (!lastName.isEmpty())
                jsonObject.put("lname", lastName);
            if (!address.isEmpty())
                jsonObject.put("tvAddress", address);

            JSONArray requestParameter = new JSONArray();
            requestParameter.put(jsonObject);
            String url = String.format(getString(R.string.service_filter), requestParameter.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Log.v(TAG, "Filter response: " + response.toString());
                    parseFilterData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Log.e(TAG, "Filter Error: " + error.toString());
                    new SweetAlertDialog(FilterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getString(R.string.oops))
                            .setContentText(getString(R.string.something_went_wrong))
                            .show();
                }
            });

            SaiVisionApplication.getInstance().getRequestQueue().add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseFilterData(JSONObject response) {
        try {
            String status = response.getString("status");

            if (status.equalsIgnoreCase("True")) {
                JSONArray data = response.getJSONArray("data");
                ArrayList<CustomerPOJO> customersList = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    CustomerPOJO customer = new Gson().fromJson(data.getJSONObject(i).toString(), CustomerPOJO.class);
                    customersList.add(customer);
                }
                Intent intent = new Intent(this, UserListingActivity.class);
                intent.putParcelableArrayListExtra(CustomerPOJO.class.getSimpleName(), customersList);
                startActivity(intent);

            } else {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.oops))
                        .setContentText(getString(R.string.no_users_found))
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validateViews() {
        return !mETId.getText().toString().trim().isEmpty() || !mETFirstName.getText().toString().trim().isEmpty() || !mETLastname.getText().toString().trim().isEmpty() || !mETAddress.getText().toString().trim().isEmpty();

    }
}
