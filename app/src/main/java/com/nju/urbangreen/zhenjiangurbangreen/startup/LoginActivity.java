package com.nju.urbangreen.zhenjiangurbangreen.startup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.map.MapActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.PermissionsUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.SPUtils;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_username)
    public EditText etUserName;

    @BindView(R.id.edit_password)
    public EditText etPassword;

    @BindView(R.id.btn_login)
    public Button btnLogin;

    @BindView(R.id.cb_remember_pwd)
    public AppCompatCheckBox cbRememberPassword;
    private final String RememberKEY = "RememberPassword";
    private boolean remember_password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionsUtil.setPermissions(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("登录中...");
        setButton();

        if(SPUtils.getBool(RememberKEY, false)) {
            etUserName.setText(SPUtils.getString("username", "xk"));
            etPassword.setText(SPUtils.getString("password", "@"));
            onLogin();
        }
    }

    public void setButton(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogin();
            }
        });
        cbRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                remember_password = b;
            }
        });
    }

    private void onLogin() {
        progressDialog.show();
        final String username = etUserName.getText().toString(), password = etPassword.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg[] = new String[1];
                Map<String, Object> res = WebServiceUtils.login(username, password, errorMsg);
                progressDialog.dismiss();
                if(res != null) {
                    if(remember_password) {
                        SPUtils.put(RememberKEY, true);
                        SPUtils.put("username", username);
                        SPUtils.put("password", password);
                    }
                    Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(intent);
                } else {
                    Looper.prepare();
                    if(errorMsg[0] != null) {
                        Toast.makeText(LoginActivity.this, errorMsg[0], Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                }
            }
        }).start();
    }
}
