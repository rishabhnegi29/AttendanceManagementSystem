package com.example.rishabh.attendencemanagementsystem;

/**
 * Created by Rishabh on 15-02-2017.
 */
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class backgroundTask2 extends AsyncTask<String,Void,String>
{
    Context ctx;
    public backgroundTask2(Context ctx)
    {
        this.ctx=ctx;
    }

    @Override
    protected String doInBackground(String... params) {


        String jsonString=params[0],Json_string="";

        try {
            URL url = new URL("http://www.rishabhnegi.esy.es/jsonconvert2.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("jsonString", "UTF-8") + "=" + URLEncoder.encode(jsonString, "UTF-8") + "";
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder=new StringBuilder();
            while ((Json_string=bufferedReader.readLine())!=null )
            {
                stringBuilder.append(Json_string+"\n");
            }
            bufferedReader.close();
            inputStream.close();

            httpURLConnection.disconnect();
            return stringBuilder.toString().trim();

        }catch (Exception e){}
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
       //Toast.makeText(ctx, "in post " + result, Toast.LENGTH_SHORT).show();



    }

}