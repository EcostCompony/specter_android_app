package com.ecost.specter;

import android.os.AsyncTask;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;

public class DataTimeTask extends AsyncTask<String, Integer, Long> {

    @Override
    protected Long doInBackground(String... strings) {
        try {
            TimeTCPClient client = new TimeTCPClient();
            try {
                client.setDefaultTimeout(60000);
                client.connect("utcnist2.colorado.edu");
                return client.getTime();
            } finally {
                client.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}