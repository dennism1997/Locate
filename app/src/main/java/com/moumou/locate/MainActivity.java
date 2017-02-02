package com.moumou.locate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.moumou.locate.reminder.LocationReminder;
import com.moumou.locate.reminder.POIReminder;
import com.moumou.locate.reminder.Reminder;
import com.moumou.locate.reminder.WifiReminder;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Reminder> reminderList;
    private ListView listView;
    private ReminderListAdapter listAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    //FAB's
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private boolean isFabOpen = false;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Constants.PREF_KEY_FIRST_START, true);

        if (firstStart || ActivityCompat.checkSelfPermission(this,
                                                             android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                          PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                               PackageManager.PERMISSION_GRANTED) {

            Intent i = new Intent(this, IntroActivity.class);
            startActivityForResult(i, Constants.RC_INTRO);
        } else {
            Intent locationService = new Intent(this, LocationService.class);
            startService(locationService);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFromStorage();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Locate");
        setSupportActionBar(toolbar);

        reminderList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.reminder_listview);

        //View listEmptyView = getLayoutInflater().inflate(R.layout.reminder_list_empty, null);
        //        TextView emptyView = new TextView(this);
        //        emptyView.setText(R.string.empty_reminder_list_text);
        //        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(findViewById(R.id.empty_list_textview));

        //todo make resource
        listAdapter = new ReminderListAdapter(this, R.layout.reminder_list_item, reminderList);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeReminderLabelDialog((Reminder) listView.getItemAtPosition(position));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                reminderList.remove(position);
                listAdapter.notifyDataSetChanged();
                writeToStorage();

                return true;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(this);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(this);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(this);

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //todo open settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private synchronized void getFromStorage() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(Constants.REMINDER_FILE);
            ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            if (o instanceof List) {
                List list = (List) o;
                if (!list.isEmpty() && list.get(0) instanceof Reminder) {
                    Log.d("IO", "Read from storage:" + list.toString());
                    reminderList = (List<Reminder>) list;
                    listAdapter = new ReminderListAdapter(this,
                                                          R.layout.reminder_list_item,
                                                          reminderList);
                    listView.setAdapter(listAdapter);
                    Log.d("ADAPTER", listAdapter.getCount() + "");
                } else if (list.isEmpty()) {
                    listAdapter = new ReminderListAdapter(this,
                                                          R.layout.reminder_list_item,
                                                          new ArrayList<Reminder>());
                    listView.setAdapter(listAdapter);
                    Log.d("ADAPTER", listAdapter.getCount() + "");
                }
            }
            fis.close();
            ois.close();
        } catch (FileNotFoundException | EOFException e) {
            try {
                openFileOutput(Constants.REMINDER_FILE, MODE_PRIVATE);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException | IOException e) {
            Log.d("IO", "Couldn't read from storage");
            e.printStackTrace();
            reminderList = new ArrayList<>();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void writeToStorage() {
        try {
            FileOutputStream fos = openFileOutput(Constants.REMINDER_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(reminderList);
            fos.close();
            oos.close();
            Log.d("IO", "Wrote to storage:" + reminderList.toString());
            getFromStorage();
        } catch (IOException e) {
            Log.d("IO", "Couldn't write to storage");
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getFromStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.RC_LOCATION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    final int finalI = i;
                    alertDialogBuilder.setTitle(R.string.Permission)
                            .setMessage(R.string.permission_needed + permissions[i])
                            .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                                                      new String[]{permissions[finalI]},
                                                                      Constants.RC_LOCATION);
                                }
                            })
                            .show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStop() {
        super.onStop();
        closeFAB();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:
                //location reminder
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(this), Constants.RC_NEW_LOC);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                animateFAB();
                break;
            case R.id.fab2:
                //poi reminder
                Intent i = new Intent(this, AddPoiRemActivity.class);
                startActivityForResult(i, Constants.RC_NEW_PLACE);
                animateFAB();
                break;
            case R.id.fab3:
                Intent intent = new Intent(this, AddWifiRemActivity.class);
                startActivityForResult(intent, Constants.RC_NEW_WIFI);
                animateFAB();
                break;
        }
    }

    public void animateFAB() {

        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }

    private void closeFAB() {
        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String label = "label";
        switch (requestCode) {
            case Constants.RC_NEW_LOC: {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    LocationReminder lr = new LocationReminder(Constants.getActivitycounter(),
                                                               label,
                                                               place.getId(),
                                                               place.getName().toString(),
                                                               place.getAddress().toString(),
                                                               place.getLatLng().latitude,
                                                               place.getLatLng().longitude);
                    reminderList.add(lr);
                    changeReminderLabelDialog(lr);
                }
                break;
            }
            case Constants.RC_NEW_PLACE: {
                if (resultCode == Activity.RESULT_OK) {
                    int[] types = data.getIntArrayExtra(Constants.NEW_TYPES_ARRAY);

                    List<Integer> list = new ArrayList<>();
                    for (int type : types) {
                        list.add(type);
                    }
                    POIReminder pr = new POIReminder(Constants.getActivitycounter(), label, list);
                    reminderList.add(pr);
                    changeReminderLabelDialog(pr);
                }
                break;
            }
            case Constants.RC_NEW_WIFI: {
                if (resultCode == Activity.RESULT_OK) {

                    WifiReminder wr = new WifiReminder(Constants.getActivitycounter(),
                                                       label,
                                                       data.getStringExtra(Constants.NEW_WIFI_REM));
                    changeReminderLabelDialog(wr);
                } else if (resultCode == Constants.NO_WIFI) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Can't get list of wifi spots")
                            .setMessage(
                                    "We couldn't get access to your wifi configurations. Is wifi enabled?")
                            .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                break;
            }
            case Constants.RC_INTRO: {
                if (resultCode == RESULT_OK) {
                    //TODO uncomment
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putBoolean(Constants.PREF_KEY_FIRST_START, false)
                            .apply();
                    Intent locationService = new Intent(this, LocationService.class);
                    startService(locationService);
                } else {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putBoolean(Constants.PREF_KEY_FIRST_START, true)
                            .apply();
                    //User cancelled the intro so we'll finish this activity too.
                    finish();
                }
                break;
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    public void changeReminderLabelDialog(final Reminder r) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_dialog, null);
        alertDialogBuilder.setView(view);

        final EditText input = (EditText) view.findViewById(R.id.input_label_edittext);
        if (!r.getLabel().equals("label")) {
            input.setText(r.getLabel());
            input.setSelection(input.getText().length());
        }

        final Dialog d = alertDialogBuilder.setPositiveButton("OK",
                                                              new DialogInterface.OnClickListener() {
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      r.setLabel(input.getText()
                                                                                         .toString());
                                                                      writeToStorage();
                                                                  }
                                                              }).setCancelable(false).show();

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    r.setLabel(input.getText().toString());
                    writeToStorage();
                    d.dismiss();
                    return true;
                }
                return false;
            }
        });
    }
}
