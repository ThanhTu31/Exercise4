package com.example.homework2;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button analyzeButton;
    TextView sentimentTextView;
    ImageView sentimentIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        analyzeButton = findViewById(R.id.analyzeButton);
        sentimentTextView = findViewById(R.id.sentimentTextView);
        sentimentIcon = findViewById(R.id.sentimentIcon);

        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    new SentimentAnalysisTask().execute(text);
                }
            }
        });
    }

    private class SentimentAnalysisTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "https://api-inference.huggingface.co/models/wonrax/phobert-base-vietnamese-sentiment";
            String token = "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";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("inputs", params[0]);

                OutputStream os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes());
                os.flush();
                os.close();

                InputStream inputStream;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                bufferedReader.close();
                conn.disconnect();

                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                String sentiment = "";
                double maxScore = 0.0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    double score = item.getDouble("score");
                    if (score > maxScore) {
                        maxScore = score;
                        sentiment = item.getString("label");
                    }
                }
                return sentiment;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String sentiment) {
            super.onPostExecute(sentiment);
            updateUI(sentiment);
        }
    }

    private void updateUI(String sentiment) {
        int iconResource;
        int colorResource;

        switch (sentiment) {
            case "POS":
                iconResource = R.drawable.ic_launcher_background;
                colorResource = android.R.color.holo_green_dark;
                break;
            case "NEU":
                iconResource = R.drawable.ic_launcher_foreground;
                colorResource = android.R.color.darker_gray;
                break;
            case "NEG":
                iconResource = R.drawable.ic_launcher_foreground;
                colorResource = android.R.color.holo_red_dark;
                break;
            default:
                iconResource = R.drawable.ic_launcher_background;
                colorResource = android.R.color.black;
        }

        sentimentIcon.setImageResource(iconResource);
        sentimentIcon.setColorFilter(getResources().getColor(colorResource));
        sentimentTextView.setText("Sentiment: " + sentiment);
    }
}
