package com.baowu.zwyuser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.baowu.zwyuser.databinding.ActivityLoginBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.baowu.zwyuser.MyApplication.NEW_ALARM;
import static com.baowu.zwyuser.MyApplication.USER_ID;
import static com.baowu.zwyuser.MyApplication.USER_SP;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.login.setOnClickListener(v -> {
            if (false) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                login();
            }
        });
    }

    private void login() {
        String url = "http://101.132.43.11:8001/auth/login";
        OkHttpClient client = new OkHttpClient();
        JSONObject req = new JSONObject();
        try {
            req.put("username", binding.username.getText().toString());
            req.put("password", binding.password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(req.toString(), MediaType.parse("application/json;charset=UTF-8")))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    try {
                        JSONObject object = new JSONObject(res);
                        if (object.has("companyId")) {
                            int id = object.getInt("userId");
                            boolean hasNewAlarm = object.getBoolean("new_alarm");
                            SharedPreferences sp = getApplication().getSharedPreferences(USER_SP, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt(USER_ID, id);
                            editor.putBoolean(NEW_ALARM, hasNewAlarm);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, "请登录管理员应用", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else { LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
