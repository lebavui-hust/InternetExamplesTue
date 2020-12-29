package com.example.internetexamples;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textContent = findViewById(R.id.text_content);

        findViewById(R.id.btn_quick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textContent.setText("QUICK WORK done.");
            }
        });

        findViewById(R.id.btn_slow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlowTask2().execute(15);
            }
        });

        // new GetTask().execute();
        // new DownloadTask().execute("https://lebavui.github.io/videos/ecard.mp4");


        String jsonString = "[{\"name\":\"John\", \"age\":20, \"gender\":\"male\"}, {\"name\":\"Peter\", \"age\":21, \"gender\":\"male\"}, {\"name\":\"July\", \"age\":19, \"gender\":\"female\"}]";
        try {
            JSONArray jArr = new JSONArray(jsonString);
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jObj = jArr.getJSONObject(i);
                String name = jObj.getString("name");
                int age = jObj.getInt("age");
                String gender = jObj.getString("gender");

                Log.v("TAG", name + " - " + age + " - " + gender);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class SlowTask1 extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute() {
            textContent.setText("SLOW WORK started.");
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int total = params[0];

            try {
                for (int i = 0; i < total; i++) {
                    Thread.sleep(1000);
                    publishProgress(i + 1, total);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            int total = values[1];
            textContent.setText("SLOW WORK progress ... " + (progress * 100.0) / total);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            textContent.setText("SLOW WORK done.");
        }
    }

    private class SlowTask2 extends AsyncTask<Integer, Integer, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Processing");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int total = params[0];

            try {
                for (int i = 0; i < total; i++) {
                    Thread.sleep(1000);
                    publishProgress(i + 1, total);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            int total = values[1];
            dialog.setMax(total);
            dialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
        }
    }

    private class GetTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL("http://httpbin.org/get?param1=value1&param2=value2");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                Log.v("TAG", "Response code: " + responseCode);

                InputStream is = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String data = "";
                String line;
                while ((line = reader.readLine()) != null)
                    data += line + "\n";

                reader.close();
                is.close();

                Log.v("TAG", data);

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Downloading");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                int total = con.getContentLength();

                InputStream is = con.getInputStream();
                OutputStream os = openFileOutput("test1.mp4", 0);

                byte[] buffer = new byte[1024];
                int len = 0;
                int downloaded = 0;

                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                    downloaded += len;
                    publishProgress(downloaded, total);
                }

                os.close();
                is.close();

                Log.v("TAG", "Done");

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            dialog.setMax(values[1]);
            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
        }
    }
}