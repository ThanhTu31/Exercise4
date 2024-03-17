package com.example.homework2;
import android.os.AsyncTask;
import android.support.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button submitButton;
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        submitButton = findViewById(R.id.submit_button);
        iconImageView = findViewById(R.id.icon_image);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editText.getText().toString();
                if (!inputText.isEmpty()) {
                    new SentimentAnalysisTask().execute(inputText);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class SentimentAnalysisTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String inputText = params[0];
            return performSentimentAnalysis(inputText);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                double score = jsonObject.getJSONObject("documents").getJSONArray("score").getDouble(0);
                // Update icon based on sentiment score
                if (score >= 0.5) {
                    iconImageView.setImageResource(R.drawable.positive_icon);
                } else {
                    iconImageView.setImageResource(R.drawable.negative_icon);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error analyzing sentiment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String performSentimentAnalysis(String text) {
        String apiKey = "YOUR_API_KEY";
        String endpoint = "https://eastasia.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);
            connection.setDoOutput(true);

            String requestBody = "{\"documents\": [{\"language\": \"en\", \"id\": \"1\", \"text\": \"" + text + "\"}]}";

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            connection.disconnect();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
