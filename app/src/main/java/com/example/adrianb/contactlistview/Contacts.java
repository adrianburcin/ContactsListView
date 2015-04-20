package com.example.adrianb.contactlistview;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by adrianb on 20/04/15.
 */
public class Contacts {
    private Context context;

    public Contacts(Context context) {
        this.context = context;
    }

    public ArrayList<ContactItem> getContactList() {
        ArrayList<ContactItem> allContacts = new ArrayList<ContactItem>();

        try {
            ContentResolver cr = context.getContentResolver(); // Activity/Application
            // android.content.Context
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));

                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[] { id },
                                null);
                        while (pCur.moveToNext()) {
                            String contactNumber = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String contactName = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            allContacts.add(new ContactItem(contactName,
                                    contactNumber));
                            break;
                        }
                        pCur.close();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allContacts;
    }

    public HashMap<String, ContactItem> getContactHashMap() {

        HashMap<String, ContactItem> allContacts = new HashMap<String, ContactItem> ();

        try {
            ContentResolver cr = context.getContentResolver(); // Activity/Application
            // android.content.Context
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));

                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[] { id },
                                null);
                        while (pCur.moveToNext()) {
                            String contactNumber = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String contactName = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            allContacts.put(contactName, new ContactItem(contactName,
                                    contactNumber));
                            break;
                        }
                        pCur.close();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allContacts;
    }

    public class ContactItem {
        private String name;
        private String phoneNumber;

        public ContactItem(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

    }

}

