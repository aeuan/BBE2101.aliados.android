package com.dacodes.bepensa.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.AppController;
import com.dacodes.bepensa.R;
import com.dacodes.bepensa.entities.RecoverPassword;
import com.dacodes.bepensa.fragments.FragmentResetPasswordOne;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditPassword extends AppCompatActivity {

    String LOG_TAG =EditPassword.class.getSimpleName();
    AppController appController;
    Context mContext;

    private RecoverPassword recoverPassword=null;

    @BindView(R.id.toolbar)Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        ButterKnife.bind(this);
        mContext=getApplicationContext();
        FragmentResetPasswordOne fragmentResetPasswordOne = new FragmentResetPasswordOne();
        openFragment(fragmentResetPasswordOne);
        toolbar.setTitle("RECUPERAR CONTRASEÑA");
        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recoverPassword= new RecoverPassword();
    }

    public void openFragment(final Fragment fragment)   {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        fragmentManager.popBackStack();
        transaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }
        return true;
    }

    public RecoverPassword getRecoverPassword() {
        return recoverPassword;
    }

    public void setRecoverPassword(RecoverPassword recoverPassword) {
        this.recoverPassword = recoverPassword;
    }
}
