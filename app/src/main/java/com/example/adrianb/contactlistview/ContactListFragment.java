package com.example.adrianb.contactlistview;

import android.support.v4.app.Fragment;
import android.app.Service;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.example.adrianb.contactlistview.AlphabetListAdapter.Row;
import com.example.adrianb.contactlistview.AlphabetListAdapter.Cell;
import com.example.adrianb.contactlistview.AlphabetListAdapter.Header;
import com.example.adrianb.contactlistview.Contacts.ContactItem;

/**
 * Created by adrianb on 20/04/15.
 */
public class ContactListFragment extends ListFragment {
    private static String TAG = "ContactListFragment";

    private AlphabetListAdapter adapter = new AlphabetListAdapter();
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();

    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;

    private LinearLayout sideIndex;
    private View contactListView;
    private EditText inputSearch;
    private TextView animatedText;
    private Animation out;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactListView = inflater.inflate(R.layout.list_alphabet, container, false);

        inputSearch = (EditText) contactListView.findViewById(R.id.inputSearch);
        inputSearch.setHint(Html.fromHtml("<font size=\"10\">" + "search_contacts_hint" + "</font>"));
        inputSearch.setHintTextColor(Color.parseColor("#88888a"));

        sideIndex = (LinearLayout) contactListView.findViewById(R.id.sideIndex);

        RelativeLayout listAlphabetLayout = (RelativeLayout) contactListView.findViewById(R.id.list_alphabet_layout);

        animatedText = (TextView) contactListView.findViewById(R.id.animated_letter);
        out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);
        out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                animatedText.setVisibility(View.GONE);
                Log.d("onAnimationEnd", "setVisibility(View.GONE);");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        displayContactList(MainActivity.contacts);

        return contactListView;
    }

    private void displayContactList(HashMap<String, ContactItem> contactsHasMap) {
        // TODO Auto-generated method stub
        List<String> contacts = new ArrayList<String>(contactsHasMap.keySet());
        Collections.sort(contacts);

        List<Row> rows = new ArrayList<Row>();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");

        for (String contact : contacts) {
            String firstLetter = contact.substring(0, 1);

            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }

            // If we've changed to a new letter, add the previous letter to the alphabet scroller
            if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);

                start = end + 1;
            }

            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                rows.add(new Header(firstLetter));
                sections.put(firstLetter, start);
            }

            // Add the country to the list
            rows.add(new Cell(contact));
            previousLetter = firstLetter;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        adapter.setRows(rows);
        setListAdapter(adapter);

        updateList();

    }

    private void handleAnimatedLetter(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d("handleAnimatedLetter", "ACTION_MOVE");
            if(animatedText.getVisibility() == View.GONE) {
                animatedText.setVisibility(View.VISIBLE);
                Log.d("handleAnimatedLetter", "setVisibility(View.VISIBLE);");
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("handleAnimatedLetter", "ACTION_UP");
            if(animatedText.getVisibility() == View.VISIBLE) {
                animatedText.startAnimation(out);
                Log.d("handleAnimatedLetter", "setAnimation(out);");
            }
        }

    }

    public void updateList() {
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            tmpTV = new TextView(getActivity());
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(12);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();
        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.d("bla", "event: " + event.getAction());
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();

                handleAnimatedLetter(event);
                if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void displayListItem() {
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size() && itemPosition >= 0) {
            Object[] indexItem = alphabet.get(itemPosition);
            //set current alphabet letter under the user's finger to animated text which will be a overlay
            String currentAlphabetLetter = String.valueOf(indexItem[0]);
            animatedText.setText(currentAlphabetLetter);

            int subitemPosition = sections.get(indexItem[0]);
            getListView().setSelection(subitemPosition);
        }
    }

    public void hideListIndex() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sideIndex.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void showListIndex() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sideIndex.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cell item = (Cell) v.getTag();
        if(item instanceof Row) {
            //Your contact/item handling code here
            Toast.makeText(getActivity(), item.text + " " + MainActivity.contacts.get(item.text).getPhoneNumber(), Toast.LENGTH_SHORT).show();
            HashMap<String, String> chosenContact = new HashMap<String, String>();
            chosenContact.put(item.text, MainActivity.contacts.get(item.text).getPhoneNumber());
        }

        super.onListItemClick(l, v, position, id);
    }

}