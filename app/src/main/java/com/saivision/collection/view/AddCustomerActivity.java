package com.saivision.collection.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.saivision.collection.R;
import com.saivision.collection.SaiVisionApplication;
import com.saivision.collection.database.NewCustomer;
import com.saivision.collection.model.GroupPOJO;
import com.saivision.collection.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;

public class AddCustomerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = AddCustomerActivity.class.getSimpleName();
    ArrayList<GroupPOJO> groupsList;
    ArrayList<String> groups = new ArrayList<>();
    String selectedGroup;
    private EditText mETFirstName;
    private EditText mETLastName;
    private EditText mETPhoneNumber;
    private EditText mETEmail;
    private EditText mETRegistrationAmount;
    private EditText mETAppartmentName;
    private EditText mETLane1;
    private EditText mETLane2;
    private RadioGroup rgGender;
    private String gender;
    private EditText mETBoxNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        if (Utility.isConnectedToInternet(this))
            getGroups();
//        else {
//            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText(getString(R.string.no_connection))
//                    .setContentText(getString(R.string.check_internet_connection))
//                    .show();
//        }
        initViews();
    }

    private void getGroups() {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        dialog.setCancelable(false);
        String url = getString(R.string.service_get_group);

        Log.e(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                Log.v(TAG, "group response: " + response.toString());
                parseGroupData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e(TAG, "group Error: " + error.toString());
                new SweetAlertDialog(AddCustomerActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    private void parseGroupData(JSONObject response) {
        try {
            JSONArray groupArray = response.getJSONArray("group_list");
            groups.add("Select Group");
            groupsList = new ArrayList<>();
            for (int i = 0; i < groupArray.length(); i++) {
                groupsList.add(new Gson().fromJson(groupArray.get(i).toString(), GroupPOJO.class));
                groups.add(groupsList.get(i).getGroupName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            setGroupAdapter();
        }
    }

    private void setGroupAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_group);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);


        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);

        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(groupAdapter);
    }

    private void initViews() {


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_customer);

        mETBoxNo = (EditText) findViewById(R.id.et_box_no);
        mETFirstName = (EditText) findViewById(R.id.et_first_name);
        mETLastName = (EditText) findViewById(R.id.et_last_name);
        mETPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        mETEmail = (EditText) findViewById(R.id.et_email);
        mETRegistrationAmount = (EditText) findViewById(R.id.et_registration_amount);
        mETAppartmentName = (EditText) findViewById(R.id.et_apt_name);
        mETLane1 = (EditText) findViewById(R.id.et_address_lane1);
        mETLane2 = (EditText) findViewById(R.id.et_address_lane2);

        rgGender = (RadioGroup) findViewById(R.id.rg_gender);
        rgGender.setOnCheckedChangeListener(this);

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            selectedGroup = null;
        } else {
            selectedGroup = groupsList.get(position - 1).getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            if (validateFields()) {

                if (Utility.isConnectedToInternet(this))
                    addCustomer();
                else {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getString(R.string.no_connection))
                            .setContentText(getString(R.string.check_internet_connection))
                            .show();
                    StoreInDatabase();
                }
            }
        }
    }

    private void StoreInDatabase() {
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        NewCustomer newCustomer = realm.createObject(NewCustomer.class);
        newCustomer.setBoxNo(mETBoxNo.getText().toString().trim());
        newCustomer.setFirstName(mETFirstName.getText().toString().trim());
        newCustomer.setLastName(mETLastName.getText().toString().trim());
        newCustomer.setEmail(mETEmail.getText().toString().trim());
        newCustomer.setPhone(mETPhoneNumber.getText().toString().trim());
        newCustomer.setGender(gender);
        newCustomer.setApartmentName(mETAppartmentName.getText().toString().trim());
        newCustomer.setAddressLane1(mETLane1.getText().toString().trim());
        newCustomer.setAddressLane2(mETLane2.getText().toString().trim());
        newCustomer.setGroup(selectedGroup);
        newCustomer.setRegistrationAmount(mETRegistrationAmount.getText().toString().trim());
        newCustomer.setBoxNo(mETBoxNo.getText().toString().trim());
        realm.commitTransaction();
        realm.close();
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.offline))
                .setContentText(getString(R.string.added_to_the_database))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void addCustomer() {
        try {
            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);
            dialog.setCancelable(false);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fname", mETFirstName.getText().toString().trim());
            jsonObject.put("lname", mETLastName.getText().toString().trim());
            if (!mETEmail.getText().toString().trim().isEmpty()) {
                jsonObject.put("email", mETEmail.getText().toString().trim());
            }
            jsonObject.put("mobile_no", mETPhoneNumber.getText().toString().trim());
            jsonObject.put("gender", gender);
            jsonObject.put("apt_name", mETAppartmentName.getText().toString().trim());
            jsonObject.put("lane1", mETLane1.getText().toString().trim());
            jsonObject.put("lane2", mETLane2.getText().toString().trim());
            jsonObject.put("group_id", selectedGroup);
            jsonObject.put("registration_amount", mETRegistrationAmount.getText().toString().trim());
            jsonObject.put("box_no", mETBoxNo.getText().toString().trim());
            JSONArray requestParameter = new JSONArray();
            requestParameter.put(jsonObject);
            String url = String.format(getString(R.string.service_add_customer), requestParameter.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Log.v(TAG, "login response: " + response.toString());
                    parseData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Log.e(TAG, "Login Error: " + error.toString());
                    new SweetAlertDialog(AddCustomerActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    private void parseData(JSONObject response) {
        try {
            String status = response.getString("success");
            if (status.equalsIgnoreCase("true")) {
                new SweetAlertDialog(AddCustomerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(getString(R.string.well_done))
                        .setContentText("Customer Added Successfully.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        })
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {

        if (mETBoxNo.getText().toString().trim().isEmpty()) {
            mETBoxNo.setError(getString(R.string.empty_field));
            mETBoxNo.requestFocus();
            return false;
        }

        if (mETFirstName.getText().toString().trim().isEmpty()) {
            mETFirstName.setError(getString(R.string.empty_field));
            mETFirstName.requestFocus();
            return false;
        }

        if (mETLastName.getText().toString().trim().isEmpty()) {
            mETLastName.setError(getString(R.string.empty_field));
            mETLastName.requestFocus();
            return false;
        }

        if (mETPhoneNumber.getText().toString().trim().length() < 10) {
            mETPhoneNumber.setError(getString(R.string.invalid_mobile));
            mETPhoneNumber.requestFocus();
            return false;
        }

        if (!mETEmail.getText().toString().trim().isEmpty()) {

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mETEmail.getText().toString().trim()).matches()) {
                mETEmail.setError(getString(R.string.invalid_email));
                mETEmail.requestFocus();
                return false;
            }
        }

        if (gender == null) {
            new SweetAlertDialog(AddCustomerActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.oops))
                    .setContentText(getString(R.string.select_gender))
                    .show();
            return false;
        }

        if (mETRegistrationAmount.getText().toString().trim().isEmpty()) {
            mETRegistrationAmount.setError(getString(R.string.empty_field));
            mETRegistrationAmount.requestFocus();
            return false;
        }
        if (Utility.isConnectedToInternet(this) && selectedGroup == null) {
            new SweetAlertDialog(AddCustomerActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.oops))
                    .setContentText(getString(R.string.select_group))
                    .show();
            return false;
        }

        if (mETAppartmentName.getText().toString().trim().isEmpty()) {
            mETAppartmentName.setError(getString(R.string.empty_field));
            mETAppartmentName.requestFocus();
            return false;
        }

        if (mETLane1.getText().toString().trim().isEmpty()) {
            mETLane1.setError(getString(R.string.empty_field));
            mETLane1.requestFocus();
            return false;
        }

        if (mETLane2.getText().toString().trim().isEmpty()) {
            mETLane2.setError(getString(R.string.empty_field));
            mETLane2.requestFocus();
            return false;
        }


        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == rgGender) {
            switch (checkedId) {
                case R.id.rb_male:
                    gender = "Male";
                    break;
                case R.id.rb_female:
                    gender = "Female";
                    break;
            }
        }
    }
}
