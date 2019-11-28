package com.example.contactselector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button addContactButton, clearListButton;
    ListView listView;
    ArrayAdapter<listObject> adapter;
    ArrayList<listObject> contacts = new ArrayList<>();
    Context context;

    final static int KONTAKT = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (context == null) {
            context = getApplicationContext();
        }

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapteri(context, R.layout.list_item, contacts);
        listView.setAdapter(adapter);

        addContactButton = findViewById(R.id.buttonAdd);
        clearListButton = findViewById(R.id.buttonClear);

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryOpenContactPicker();
            }
        });

        clearListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(context, "List cleared.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isMarshmallowOrNewer() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private boolean hasPermissionReadContacts() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public void tryOpenContactPicker() {

        if (hasPermissionReadContacts()) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, KONTAKT);
        }
        else {
            Toast.makeText(context, "Please add permission to read contacts.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    KONTAKT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != KONTAKT) return;

        switch (resultCode) {
            case RESULT_CANCELED:
                Toast.makeText(context, "Contact select cancelled.", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_OK:
                Uri contactUri = data.getData();
                if (contactUri != null) {
                    String selectedId = contactUri.getLastPathSegment();
                    CursorTask cursorAsyncTask = new CursorTask();
                    cursorAsyncTask.execute(selectedId);
                }
        }
    }

    private class CursorTask extends AsyncTask<String , Void, String> {

        @Override
        protected String doInBackground(String ... id) {

            Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=?",
                    new String[]{id[0]},
                    null);

            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                cursor.close();
                return name;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String selectedName) {
            super.onPostExecute(selectedName);

            if(selectedName == null) {
                Toast.makeText(context, "Contact has no name.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if selected contact is already on the contacts list.
            for (listObject object: contacts) {
                if (object.name.equals(selectedName)) {
                    Toast.makeText(context, selectedName + " already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Add contact to the contacts list.
            listObject newObj = new listObject(selectedName, false);
            adapter.add(newObj);
            adapter.notifyDataSetChanged();
            Toast.makeText(context, selectedName + " added.", Toast.LENGTH_SHORT).show();
        }
    }
}
