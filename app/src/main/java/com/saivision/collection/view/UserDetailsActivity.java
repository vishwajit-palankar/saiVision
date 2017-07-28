package com.saivision.collection.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.saivision.collection.R;
import com.saivision.collection.SaiVisionApplication;
import com.saivision.collection.database.CustomerPayment;
import com.saivision.collection.model.CustomerPOJO;
import com.saivision.collection.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;

public class UserDetailsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();
    private TextInputEditText mETPaymentAmount;
    String mode = "Cash";
    private RadioGroup rgPaymentMode;
    private TextInputLayout chequeView;
    private TextInputEditText mETChequeNumber;
    private String mBoxNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        initViews();
    }

    private void initViews() {

        CustomerPOJO customerInfo = getIntent().getParcelableExtra(CustomerPOJO.class.getSimpleName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mETPaymentAmount = (TextInputEditText) findViewById(R.id.et_amount);
        TextView tvName = (TextView) findViewById(R.id.tv_name);
        TextView tvAddress = (TextView) findViewById(R.id.tv_address);
        TextView tvLastPaymentDate = (TextView) findViewById(R.id.tv_last_payment_date);
        TextView tvBoxNo = (TextView) findViewById(R.id.tv_box_no);
        TextInputLayout tilId = (TextInputLayout) findViewById(R.id.til_id);

        if (customerInfo != null) {
            mBoxNo = customerInfo.getBoxNo();
            getSupportActionBar().setTitle(String.format("%s %s", customerInfo.getFname(), customerInfo.getLname()));

            mETPaymentAmount.setText(customerInfo.getRemainingAmount());

            tvName.setText(String.format("%s %s", customerInfo.getFname(), customerInfo.getLname()));

//        tvAddress.setText(customerInfo.geta);

            tvLastPaymentDate.setText(customerInfo.getPaymentDate());

            tvBoxNo.setText(String.format("Box No. %s", customerInfo.getBoxNo()));
        } else {
            getSupportActionBar().setTitle("Offline Payment");

            tvName.setVisibility(View.GONE);
            tvLastPaymentDate.setVisibility(View.GONE);
            tvBoxNo.setVisibility(View.GONE);
            tvAddress.setVisibility(View.GONE);
            tilId.setVisibility(View.VISIBLE);
            tilId.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mBoxNo = s.toString();
                }
            });
        }

        mETChequeNumber = (TextInputEditText) findViewById(R.id.et_cheque);

        chequeView = (TextInputLayout) findViewById(R.id.til_cheque);

        Button btnPay = (Button) findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(this);
        rgPaymentMode = (RadioGroup) findViewById(R.id.rg_payment_mode);
        rgPaymentMode.setOnCheckedChangeListener(this);

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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == rgPaymentMode) {
            switch (checkedId) {
                case R.id.rb_cash:
                    chequeView.setVisibility(View.GONE);
                    mode = "Cash";
                    break;
                case R.id.rb_cheque:
                    chequeView.setVisibility(View.VISIBLE);
                    mode = "Cheque";
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (validateFields()) {
            if (Utility.isConnectedToInternet(this))
                doPayment();
            else {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.no_connection))
                        .setContentText(getString(R.string.check_internet_connection))
                        .show();

                StoreInDatabase();
            }
        }

    }

    private void StoreInDatabase() {
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        CustomerPayment customerPayment = realm.createObject(CustomerPayment.class);
        customerPayment.setId(mBoxNo);
        customerPayment.setAmount(mETPaymentAmount.getText().toString());
        customerPayment.setMode(mode);
        if (mode.equals("Cheque")) {
            customerPayment.setChequeNumber(mETChequeNumber.getText().toString());
        }
        realm.commitTransaction();
        realm.close();
        SweetAlertDialog dialog = new SweetAlertDialog(UserDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
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

    private boolean validateFields() {
        if (mETPaymentAmount.getText().toString().isEmpty()) {
            mETPaymentAmount.setError(getString(R.string.empty_field));
            return false;
        }
        if (mode.equals("Cheque")) {
            if (mETChequeNumber.getText().toString().isEmpty()) {
                mETChequeNumber.setError(getString(R.string.empty_field));
                mETChequeNumber.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void doPayment() {
        try {

            String amount = mETPaymentAmount.getText().toString();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", mBoxNo);
            jsonObject.put("amount", amount);
            jsonObject.put("mode", mode);

            if (mode.equals("Cheque")) {
                jsonObject.put("cheque_number", mETChequeNumber.getText().toString());
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
                    dialog.dismiss();
                    Log.v(TAG, "payment response: " + response.toString());
                    parsePaymentData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Log.e(TAG, "payment Error: " + error.toString());
                    new SweetAlertDialog(UserDetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    private void parsePaymentData(JSONObject response) {
        try {
            String status = response.getString("success");
            if (status.equalsIgnoreCase("True")) {
                new SweetAlertDialog(UserDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(getString(R.string.success))
                        .setContentText("Payment Received Successfully!!!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent(UserDetailsActivity.this, FilterActivity.class);
//Clear all activities and start new task
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            } else {
                new SweetAlertDialog(UserDetailsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.oops))
                        .setContentText(getString(R.string.something_went_wrong))
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
