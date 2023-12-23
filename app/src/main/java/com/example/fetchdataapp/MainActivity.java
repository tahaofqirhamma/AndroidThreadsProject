package com.example.fetchdataapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView text;
    private Button btn;
    private ProgressBar progressBar;
    private LinearLayout container;

    private static final String API_URL = "https://jsonplaceholder.typicode.com/todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        btn = findViewById(R.id.btn);
        progressBar = findViewById(R.id.progressBar);
        container = findViewById(R.id.container);
    }

    public void fetchData(View view) {
        // Show progress indicator
        progressBar.setVisibility(View.VISIBLE);

        // Using AsyncTask
        new FetchDataTask().execute();
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return fetchDataFromApi();
        }

        @Override
        protected void onPostExecute(String result) {
            // Update UI on the main thread
            try {
                // Parse JSON data and format it as a list of cards
                JSONArray jsonArray = new JSONArray(result);
                container.removeAllViews(); // Clear previous cards

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String title = jsonObject.getString("title");
                    boolean completed = jsonObject.getBoolean("completed");

                    // Create a card dynamically
                    TextView card = new TextView(MainActivity.this);
                    card.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    card.setPadding(16, 16, 16, 16);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
                    params.setMargins(16, 16, 16, 16); // Adjust margins as needed
                    card.setLayoutParams(params);

                    // Set the background drawable based on completion status
                    if (completed) {

                        card.setBackgroundResource(R.drawable.card_background_incomplete);
                    } else {
                        card.setBackgroundResource(R.drawable.card_background_complete);
                    }

                    // Format the data as a card
                    String cardText = String.format("Title: %s\nCompleted: %s\n", title, completed);
                    card.setText(cardText);
                    container.addView(card);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                text.setText("Error parsing data");
            }

            // Hide progress indicator
            progressBar.setVisibility(View.GONE);
        }

        private String fetchDataFromApi() {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                urlConnection.disconnect();
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error fetching data";
            }
        }
    }
}