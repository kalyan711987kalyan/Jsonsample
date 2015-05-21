package com.example.kalyanchakravarthy.jsonsample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button serverbtn=(Button) findViewById(R.id.GetServerData);

        serverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String serverURL="http://androidexample.com/media/webservice/JsonReturn.php";

                new LongOperation(). execute(serverURL);
            }
        });


    }

    private class LongOperation extends AsyncTask<String, Void, Void> {

        //private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;

        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);

        String data ="";

        TextView uiUpdate = (TextView) findViewById(R.id.output);
        TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);
        int sizeData = 0;
        EditText serverText = (EditText) findViewById(R.id.serverText);

        protected void onPreExecute() {

            Dialog.setMessage(".......in progress");
            Dialog.show();

            try {

                data +="&" + URLEncoder.encode("data", "UTF-8") + "="+serverText.getText();

            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }


        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;


            try {

                // Defined URL  where to send data
                URL url = new URL(urls[0]);

                // Send POST data request
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + " ");
                }

                // Append Server Response To Content String
                Content = sb.toString();

            }
            catch (Exception ex){
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                uiUpdate.setText("Output : "+Error);

            } else {

// Show Response Json On Screen (activity)
                uiUpdate.setText( Content );


                /****************** Start Parse Response JSON Data *************/

                String OutputData = "";

                JSONObject jsonResponse;

                try {

                    jsonResponse = new JSONObject(Content);

                    JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");

                    int lengthJsonArr = jsonMainNode.length();

                    for (int i=0; i<lengthJsonArr; i++) {

                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        String name = jsonChildNode.optString("name").toString();
                        String number = jsonChildNode.optString("number").toString();
                        String date_added = jsonChildNode.optString("date_added").toString();


                        OutputData +=" Name           : " + name + "\n"
                                + "Number      : " + number + "\n"
                                + "Time                : " + date_added + "\n"
                                + "-------------------------------------------------- ";

                    }

                        jsonParsed.setText( OutputData );

                    }


                catch (JSONException e){

                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
