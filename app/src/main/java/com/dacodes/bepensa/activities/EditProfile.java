package com.dacodes.bepensa.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dacodes.bepensa.R;
import com.dacodes.bepensa.fragments.FragmentEditContact;
import com.dacodes.bepensa.fragments.FragmetEditProfile;

import butterknife.BindView;
import butterknife.ButterKnife;
public class EditProfile extends AppCompatActivity {
    private static final String LOG_TAG = EditProfile.class.getSimpleName();

    @BindView(R.id.toolbar)Toolbar toolbar;
    Context mContext;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        toolbar.setTitle("EDITAR PERFIL");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorOrange));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content,new FragmetEditProfile()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public void fragmentEditContact(Bundle bundle){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        FragmentEditContact fragmentEditContact = new FragmentEditContact();
        fragmentEditContact.setArguments(bundle);
        fragmentTransaction.replace(R.id.content,fragmentEditContact).commit();
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
