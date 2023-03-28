package com.ecost.specter;

import android.os.AsyncTask;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;

public class DataTimeTask extends AsyncTask<String, Integer, Long> {

    @Override
    protected Long doInBackground(String... strings) {
        long l = 0L;
        try {
            TimeTCPClient client = new TimeTCPClient();
            try {
                client.setDefaultTimeout(60000);
                client.connect("time-e-g.nist.gov");
                l = client.getTime();
            } finally {
                client.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l-70L*365*24*60*60-17L*24*60*60;
    }

}