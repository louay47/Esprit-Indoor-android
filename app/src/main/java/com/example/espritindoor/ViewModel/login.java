package com.example.espritindoor.ViewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.espritindoor.MainActivity;
import com.example.espritindoor.Model.user;
import com.example.espritindoor.R;
import com.example.espritindoor.technique.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class login extends AppCompatActivity {

    TextView T , T2 ;
    String EMAIL ,PASS ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        T = findViewById(R.id.Email);
        T2 = findViewById(R.id.password2);



    }

    public void login(View view) {
        EMAIL = T.getText().toString();
        PASS = T2.getText().toString();
        Log.d("***", "*******"+PASS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.23:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<user> call = apiInterface.GetUser(EMAIL,PASS);
        call.enqueue(new Callback<user>() {
            @Override
            public void onResponse(Call<user> call, Response<user> response) {

                if(response.body() != null)
                {  if(EMAIL.equals(response.body().getEmail()) && response.body().getEtat() ==  true)
                {
                    Intent A = new Intent(login.this , MainActivity.class);
                    startActivity(A);
                }
                }

            }

            @Override
            public void onFailure(Call<user> call, Throwable t) {

                Log.d("***", "************************"+t.getMessage());
            }
        });
    }

    public void registre(View view) {
        Intent intent = new Intent(login.this , registre.class);
        startActivity(intent);
    }
}

