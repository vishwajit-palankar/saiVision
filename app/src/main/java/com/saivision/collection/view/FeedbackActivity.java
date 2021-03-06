package com.saivision.collection.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.saivision.collection.R;
import com.saivision.collection.SaiVisionApplication;
import com.saivision.collection.model.GroupPOJO;
import com.saivision.collection.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FeedbackActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = FeedbackActivity.class.getSimpleName();
    ArrayList<GroupPOJO> serviceProviderdersList = new ArrayList<>();
    ArrayList<String> serviceProviders = new ArrayList<>();
    private String selectedProvider;
    private TextInputEditText mETFeedback;
    private Spinner spinner;
    private TextInputEditText mETBoxNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        if (Utility.isConnectedToInternet(this))
            getServiceUser();
        else {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.no_connection))
                    .setContentText(getString(R.string.check_internet_connection))
                    .show();
        }
    }

    private void getServiceUser() {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        dialog.setCancelable(false);
        String url = getString(R.string.service_get_service_users);

        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                Log.v(TAG, "group response: " + response.toString());
                parseServiceUserData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e(TAG, "group Error: " + error.toString());
                new SweetAlertDialog(FeedbackActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.oops))
                        .setContentText(getString(R.string.something_went_wrong))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        })
                        .show();
            }
        });

        SaiVisionApplication.getInstance().getRequestQueue().add(request);
    }

    private void parseServiceUserData(JSONObject response) {
        try {
            JSONArray usersArray = response.getJSONArray("user_list");
            serviceProviders.add("Pick a service Provider");
            for (int i = 0; i < usersArray.length(); i++) {
                GroupPOJO provider = new GroupPOJO();
                provider.setGroupName(usersArray.getJSONObject(i).getString("fname") + " " + usersArray.getJSONObject(i).getString("lname"));
                provider.setId(usersArray.getJSONObject(i).getString("id"));
                serviceProviderdersList.add(provider);
                serviceProviders.add(usersArray.getJSONObject(i).getString("fname") + " " + usersArray.getJSONObject(i).getString("lname"));
                setAdapter();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        spinner = (Spinner) findViewById(R.id.spinner_service_man);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);


        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceProviders);

        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(groupAdapter);

        mETFeedback = (TextInputEditText) findViewById(R.id.et_feedback);
        mETBoxNo = (TextInputEditText) findViewById(R.id.et_box_no);
        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            selectedProvider = null;
        } else {
            selectedProvider = serviceProviderdersList.get(position - 1).getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            if (validateFields()) {
                submitFeedback();
            }
        }
    }

    private void submitFeedback() {
        try {
            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Submitting, Please wait...", true);
            dialog.setCancelable(false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", selectedProvider);
            jsonObject.put("box_no", mETBoxNo.getText().toString().trim());
            jsonObject.put("feedback", mETFeedback.getText().toString().trim());
            JSONArray requestParameter = new JSONArray();
            requestParameter.put(jsonObject);
            String url = String.format(getString(R.string.service_feedback), requestParameter.toString());

            Log.d(TAG, url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Log.v(TAG, "feedback response: " + response.toString());
                    parseFeedbackData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Log.e(TAG, "feedback Error: " + error.toString());
                    new SweetAlertDialog(FeedbackActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    private void parseFeedbackData(JSONObject response) {
        try {
            String status = response.getString("success");
            if (status.equalsIgnoreCase("true")) {
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(getString(R.string.well_done))
                        .setContentText("Feedback Submitted Successfully")
                        .show();

                spinner.setSelection(0);
                mETFeedback.setText(null);

            } else {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.oops))
                        .setContentText(getString(R.string.something_went_wrong))
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (selectedProvider == null) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.oops))
                    .setContentText(getString(R.string.select_group))
                    .show();
            return false;
        }

        if (TextUtils.isEmpty(mETBoxNo.getText().toString().trim())) {
            mETBoxNo.setError(getString(R.string.empty_field));
            mETBoxNo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(mETFeedback.getText().toString().trim())) {
            mETFeedback.setError(getString(R.string.empty_field));
            mETFeedback.requestFocus();
            return false;
        }

        return true;
    }
}
