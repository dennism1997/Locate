package com.moumou.locate;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AddPoiRemActivity extends FragmentActivity {

    private ListView listView;
    private PoiTypeAdapter poiTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poi_rem);

        listView = (ListView) findViewById(R.id.poi_type_listview);
        poiTypeAdapter = new PoiTypeAdapter(this,
                                            R.layout.poi_types_list_item,
                                            Constants.POI_TYPES_ARRAY);
        listView.setAdapter(poiTypeAdapter);
        Button cancelButton = (Button) findViewById(R.id.loc_cancel);
        Button okButton = (Button) findViewById(R.id.loc_ok);
        final EditText editText = (EditText) findViewById(R.id.loc_label);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent result = new Intent();
                result.putExtra(Constants.NEW_TYPES_ARRAY,
                                Constants.convertIntegers(poiTypeAdapter.getCheckedItems()));
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                poiTypeAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
