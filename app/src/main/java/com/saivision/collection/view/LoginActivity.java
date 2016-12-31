package com.saivision.collection.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.saivision.collection.R;
import com.saivision.collection.SaiVisionApplication;
import com.saivision.collection.utils.PrefsManager;
import com.saivision.collection.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText etUserName;
    private EditText etPassword;
    private TextInputLayout tilUserName;
    private TextInputLayout tilPassword;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews() {
        etUserName = (EditText) findViewById(R.id.et_username);
        tilUserName = (TextInputLayout) findViewById(R.id.til_username);

        etPassword = (EditText) findViewById(R.id.et_password);
        tilPassword = (TextInputLayout) findViewById(R.id.til_password);

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);


        String userName = PrefsManager.getStringPref(getString(R.string.userName));

        if (userName != null && !userName.isEmpty()) {
            startActivity(new Intent(this, FilterActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (isValid()) {
                    if (Utility.isConnectedToInternet(this))
                        login();
                    else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getString(R.string.no_connection))
                                .setContentText(getString(R.string.check_internet_connection))
                                .show();
                    }
                }
                break;
        }
    }

    private void login() {
        try {

            userName = etUserName.getText().toString().trim();

            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);
            dialog.setCancelable(false);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("password", etPassword.getText().toString().trim());
            JSONArray requestParameter = new JSONArray();
            requestParameter.put(jsonObject);
            String url = String.format(getString(R.string.service_login), requestParameter.toString());

            Log.d(TAG, url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    Log.v(TAG, "login response: " + response.toString());
                    parseLoginData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Log.e(TAG, "Login Error: " + error.toString());
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
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

    private void parseLoginData(JSONObject response) {
        try {
            String status = response.getString("success");
            if (status.equalsIgnoreCase("true")) {
                PrefsManager.setPref(getString(R.string.userName), userName);
                startActivity(new Intent(this, FilterActivity.class));
                finish();
            } else {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.oops))
                        .setContentText(getString(R.string.invalid_credentials))
                        .show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid() {
        if (etUserName.getText().toString().trim().isEmpty()) {
            tilUserName.setError(getString(R.string.empty_field));
            return false;
        }
        if (etPassword.getText().toString().trim().isEmpty()) {
            tilPassword.setError(getString(R.string.empty_field));
            return false;
        }
        return true;
    }
}
