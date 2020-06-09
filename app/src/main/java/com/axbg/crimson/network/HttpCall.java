package com.axbg.crimson.network;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import static com.axbg.crimson.network.HttpMethods.DELETE_METHOD;
import static com.axbg.crimson.network.HttpMethods.GET_METHOD;
import static com.axbg.crimson.network.HttpMethods.POST_METHOD;
import static com.axbg.crimson.network.HttpMethods.PUT_METHOD;

public class HttpCall extends AsyncTask<String, String, String> {

    private HttpsURLConnection httpsURLConnection;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;

    //strings[0] - method
    //string[1] - URL
    //string[2] - json object
    @Override
    protected String doInBackground(String... strings) {
        StringBuilder builder = new StringBuilder();
        String method = strings[0];

        try {
            String line;
            URL url = new URL(strings[1]);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();

            switch (method) {
                case GET_METHOD:
                case DELETE_METHOD:
                    httpsURLConnection.setRequestMethod(method);
                    break;
                case PUT_METHOD:
                case POST_METHOD:
                    httpsURLConnection.setRequestMethod("POST");
                    httpsURLConnection.setRequestProperty("Content-type", "application/json");
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setDoOutput(true);
                    OutputStream out = httpsURLConnection.getOutputStream();
                    OutputStreamWriter outWriter = new OutputStreamWriter(out, StandardCharsets.US_ASCII);
                    outWriter.write(strings[2]);
                    outWriter.flush();
                    outWriter.close();
                    break;
            }

            httpsURLConnection.connect();
            inputStream = httpsURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return builder.toString();
    }
}