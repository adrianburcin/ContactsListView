package com.example.adrianb.contactlistview;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

import com.example.adrianb.contactlistview.Contacts.ContactItem;


public class MainActivity extends ActionBarActivity {
    public static HashMap<String, ContactItem> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.contacts = new Contacts(this).getContactHashMap();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new ContactListFragment(contacts))
                .commit();
    }
}
