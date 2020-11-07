package com.example.espritindoor.ViewModel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.espritindoor.Model.user;
import com.example.espritindoor.R;
import com.example.espritindoor.technique.ApiInterface;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class registre extends AppCompatActivity {

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword , textInputPassword2;

    private EditText name , email , password , password2 ;
    private Button btn ;
    String A,B,C,D;
    String P ,P2 ;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);

        btn = findViewById(R.id.btn);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputPassword2 = findViewById(R.id.text_input_password2);
        name = findViewById(R.id.name);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);



        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > textInputUsername.getCounterMaxLength())
                    textInputUsername.setError("Max character length is " + textInputUsername.getCounterMaxLength());
                else
                    textInputUsername.setError(null);

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!validateEmail(textInputEmail.getEditText().getText().toString()))
                    textInputEmail.setError("not a valid Email");
                else
                    textInputEmail.setError(null);

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                P = textInputPassword.getEditText().getText().toString();
                P2 = textInputPassword2.getEditText().getText().toString();
                if (!P.equals(P2))
                {textInputPassword.setError("the two mails are not identical" );}
                else
                {textInputPassword.setError(null);}



            }
        });
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                P = textInputPassword.getEditText().getText().toString();
                P2 = textInputPassword2.getEditText().getText().toString();
                if (!P.equals(P2))
                {textInputPassword.setError("the two mails are not identical" );}
                else
                {textInputPassword.setError(null);}


            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                     A =name.getText().toString();
                     B = email.getText().toString();
                     C = password.getText().toString();


                SetRetrofit(A,B,C);
            Toast.makeText(registre.this,"An email has been sent to "+B+" you need to verify your account" , Toast.LENGTH_LONG).show();
                Log.d("******", "********"+A+"**********"+B);

            }
        });


    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void SetRetrofit (String A ,String B ,String C)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.247.1:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        HashMap<Object, Object> map = new HashMap<>();
        map.put("username", A);
        map.put("email", B);
        map.put("password",C);

        Call<user> call = apiInterface.SetUser(map);

        call.enqueue(new Callback<user>() {
            @Override
            public void onResponse(Call<user> call, Response<user> response) {

            }

            @Override
            public void onFailure(Call<user> call, Throwable t) {

            }
        });


    }


}
