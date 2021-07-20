package com.dacodes.bepensa.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.fragments.RegisterFragmentOne;
import com.dacodes.bepensa.fragments.RegisterFragmentThree;
import com.dacodes.bepensa.fragments.RegisterFragmentTwo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements
        RegisterFragmentOne.OnFragmentInteractionListener,
        RegisterFragmentTwo.OnFragmentInteractionListener,
    RegisterFragmentThree.OnFragmentInteractionListener {
    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    Context mContext;
    AppController appController;
    Toast mToast;
    private int current_fragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String email;
    private String phone;
    private String new_password;

    private String userName;
    private int divisionId;
    private String passwordNew;

    public int getCurrent_fragment() {
        return current_fragment;
    }

    public void setCurrent_fragment(int current_fragment) {
        this.current_fragment = current_fragment;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNew_password() {
        return new_password;
    }
    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    //    Ends facebook
    //RegisterFragmentOne registerFragmentOne;
    RegisterFragmentTwo registerFragmentTwo;
    RegisterFragmentThree registerFragmentThree;

    String username, password;
    int division;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        //Glide.with(mContext).load(R.drawable.login_background).apply(new RequestOptions().fitCenter()).into(binding.ivBanner);
        toolbar.setTitle("Registro de empleado");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        division = getIntent().getIntExtra("division", 0);

        setDivisionId(division);
        setUserName(username);
        setPasswordNew(password);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        registerFragmentTwo = new RegisterFragmentTwo();
        Bundle bundle = new Bundle();
        bundle.putSerializable("username", username);
        bundle.putSerializable("password", password);
        bundle.putSerializable("division", division);
        registerFragmentTwo.setArguments(bundle);
        transaction.add(R.id.content, registerFragmentTwo);
        transaction.commit();

    }


    public void showSmsFragment(String username, String email, String phone, String password, int division){
        this.email = email;
        this.phone = phone.substring(3);//Delete "+52" in the string
        this.new_password = password;
        current_fragment = 2;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        registerFragmentThree = new RegisterFragmentThree();
        Bundle bundle = new Bundle();
        bundle.putSerializable("username", username);
        bundle.putSerializable("email", email);
        bundle.putSerializable("phone", phone);
        bundle.putSerializable("password", password);
        bundle.putSerializable("division", division);
        registerFragmentThree.setArguments(bundle);
        transaction.add(R.id.content, registerFragmentThree);
        try {
            transaction.commit();
        }catch (Exception e){
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void onFragmentInteraction(String username, String division) {
        setCurrent_fragment(1);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        registerFragmentTwo = new RegisterFragmentTwo();
        Bundle bundle = new Bundle();
        bundle.putSerializable("username", username);
        bundle.putSerializable("division", division);
        registerFragmentTwo.setArguments(bundle);
        transaction.add(R.id.content, registerFragmentTwo);
        transaction.commit();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (current_fragment){
            case 1:
                super.onBackPressed();
                break;
            case 2:
                onFragmentInteraction(username, String.valueOf(division));
                break;
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }
}
