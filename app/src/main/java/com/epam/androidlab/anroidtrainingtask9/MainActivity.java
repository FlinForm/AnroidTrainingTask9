package com.epam.androidlab.anroidtrainingtask9;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private View saveButton;
    private EditText text;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (EditText) findViewById(R.id.editText);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        View openButton = findViewById(R.id.openButton);
        openButton.setOnClickListener(event -> getTextFile());

        saveButton = findViewById(R.id.saveButton);
        saveButton.setEnabled(false);
        saveButton.setOnClickListener(event -> writeTextToFile(file, text.getText().toString()));
    }

    private void getTextFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, 2);
    }

    private void readTextFile(File file) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String source = "";
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while (reader.ready())
                        source += reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return source;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    text.setText(s);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private void writeTextToFile(File file, String text) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(String... params) {
                if (params.length != 0) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(params[0]);
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }.execute(text);
    }

    private String getUrl(Intent data) {
        Uri uri = Uri.parse(data.toString());
        String[] str = uri.getPath().split(" ");
        return str[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            file = new File(getUrl(data));
            readTextFile(file);
            saveButton.setEnabled(true);
        }
    }
}
