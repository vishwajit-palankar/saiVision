package com.saivision.collection.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.saivision.collection.R;
import com.saivision.collection.SaiVisionApplication;
import com.saivision.collection.database.CustomerPayment;
import com.saivision.collection.database.NewCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmResults;

public class SyncDataActivity extends AppCompatActivity {

    private static final String TAG = SyncDataActivity.class.getSimpleName();
    private Realm realm;
    private int totalSize = 0;
    private int totalCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);


        Realm.init(this);
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        uploadPayments();
        uploadNewCustomers();
    }

    private void uploadPayments() {
        RealmResults<CustomerPayment> paymentList = realm.where(CustomerPayment.class).findAll();
        if (paymentList != null && !paymentList.isEmpty()) {
            totalSize += paymentList.size();
            for (CustomerPayment payment : paymentList)
                doPayment(payment);
        }
    }

    private void doPayment(final CustomerPayment payment) {
        try {


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", payment.getId());
            jsonObject.put("amount", payment.getAmount());
            jsonObject.put("mode", payment.getMode());

            if (payment.getMode().equals("Cheque")) {
                jsonObject.put("cheque_number", payment.getChequeNumber());
            }

            JSONArray requestParameter = new JSONArray();

            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);
            dialog.setCancelable(false);
            requestParameter.put(jsonObject);
            String url = String.format(getString(R.string.service_payment), requestParameter.toString());

            Log.e(TAG, url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    totalCompleted++;
                    dialog.dismiss();
                    Log.v(TAG, "payment response: " + response.toString());

                    String status = null;
                    try {
                        status = response.getString("success");
                        if (status.equalsIgnoreCase("True")) {
                            payment.deleteFromRealm();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checkAndCloseConnection();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Log.e(TAG, "payment Error: " + error.toString());
                    new SweetAlertDialog(SyncDataActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    private void checkAndCloseConnection() {

        if (totalCompleted == totalSize) {
            realm.commitTransaction();
            realm.close();


            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText(getString(R.string.well_done))
                    .setContentText("Data Synced Successfully.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void uploadNewCustomers() {
        RealmResults<NewCustomer> customersList = realm.where(NewCustomer.class).findAll();
        if (customersList != null && !customersList.isEmpty()) {
            totalSize += customersList.size();
            for (NewCustomer newCustomer : customersList)
                addCustomer(newCustomer);
        }
        if (totalSize == 0) {

            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Empty")
                    .setContentText("Database is Empty")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void addCustomer(final NewCustomer newCustomer) {
        try {
            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);
            dialog.setCancelable(false);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fname", newCustomer.getFirstName());
            jsonObject.put("lname", newCustomer.getLastName());
            if (!newCustomer.getEmail().isEmpty()) {
                jsonObject.put("email", newCustomer.getEmail());
            }
            jsonObject.put("mobile_no", newCustomer.getPhone());
            jsonObject.put("gender", newCustomer.getGender());
            jsonObject.put("apt_name", newCustomer.getApartmentName());
            jsonObject.put("lane1", newCustomer.getAddressLane1());
            jsonObject.put("lane2", newCustomer.getAddressLane2());
            jsonObject.put("group_id", newCustomer.getGroup());
            jsonObject.put("registration_amount", newCustomer.getRegistrationAmount());
            jsonObject.put("box_no", newCustomer.getBoxNo());
            JSONArray requestParameter = new JSONArray();
            requestParameter.put(jsonObject);
            String url = String.format(getString(R.string.service_add_customer), requestParameter.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    totalCompleted++;
                    dialog.dismiss();
                    Log.v(TAG, "login response: " + response.toString());

                    String status = null;
                    try {
                        status = response.getString("success");
                        if (status.equalsIgnoreCase("true")) {
                            newCustomer.deleteFromRealm();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checkAndCloseConnection();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Log.e(TAG, "Login Error: " + error.toString());
                    new SweetAlertDialog(SyncDataActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    @Override
    public void onBackPressed() {
    }
}
