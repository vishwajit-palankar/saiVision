package com.saivision.collection.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.saivision.collection.model.CustomerPOJO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserDetailsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();
    private CustomerPOJO customerInfo;
    private TextInputEditText mETPaymentAmount;
    String mode = "Cash";
    private RadioGroup rgPaymentMode;
    private TextInputLayout chequeView;
    private TextInputEditText mETChequeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        initViews();
    }

    private void initViews() {

        customerInfo = getIntent().getParcelableExtra(CustomerPOJO.class.getSimpleName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(String.format("%s %s", customerInfo.getFname(), customerInfo.getLname()));

        mETPaymentAmount = (TextInputEditText) findViewById(R.id.et_amount);
        mETPaymentAmount.setText(customerInfo.getRemainingAmount());

        mETChequeNumber = (TextInputEditText) findViewById(R.id.et_cheque);

        chequeView = (TextInputLayout) findViewById(R.id.til_cheque);

        Button btnPay = (Button) findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(this);

        TextView tvName = (TextView) findViewById(R.id.tv_name);
        tvName.setText(String.format("%s %s", customerInfo.getFname(), customerInfo.getLname()));

        TextView tvAddress = (TextView) findViewById(R.id.tv_address);
//        tvAddress.setText(customerInfo.geta);

        TextView tvLastPaymentDate = (TextView) findViewById(R.id.tv_last_payment_date);
        tvLastPaymentDate.setText(customerInfo.getPaymentDate());

        TextView tvBoxNo = (TextView) findViewById(R.id.tv_box_no);
        tvBoxNo.setText(customerInfo.getBoxNo());

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
        if (validateFields())
            doPayment();

    }

    private boolean validateFields() {
        if (mETPaymentAmount.getText().toString().isEmpty()) {
            mETPaymentAmount.setError(getString(R.string.empty_field));
            return false;
        }
        if (mode.equals("Cheque")) {
            if (mETChequeNumber.getText().toString().isEmpty()) {
                mETChequeNumber.setError(getString(R.string.empty_field));
                return false;
            }
        }
        return true;
    }

    private void doPayment() {
        try {

            String amount = mETPaymentAmount.getText().toString();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", customerInfo.getBoxNo());
            jsonObject.put("amount", mETPaymentAmount.getText().toString());
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

    }
}
