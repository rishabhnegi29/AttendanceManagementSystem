package com.example.rishabh.attendencemanagementsystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Welcome extends AppCompatActivity  {

    String json_string;
    ArrayList al=new ArrayList();
    ListView listView;
    String name,cid;
    String courseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        listView = (ListView) findViewById(R.id.batch);
        Intent i=getIntent();
        Bundle b= i.getExtras();
        courseId= b.getString("courseId");
        Toast.makeText(this,courseId, Toast.LENGTH_SHORT).show();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Welcome.this,"clicked", Toast.LENGTH_SHORT).show();


                Bundle b= new Bundle();
                b.putString("courseId",al.get(position).toString());

                Intent i=new Intent(Welcome.this,CourseSelected.class);
                i.putExtras(b);
                //Toast.makeText(this,"CHANGING...", Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });


        new backgroundTask().execute(courseId);
    }

    class backgroundTask extends AsyncTask<String,Void,String>
        {
            String json_url;
            String Json_string;
            String jdon_string;
            JSONObject jsonObject;
            JSONArray jsonArray;


            @Override
            protected void onPreExecute() {
                json_url="http://www.rishabhnegi.esy.es/ret.php";
            }

            @Override
            protected String doInBackground(String... params) {
                String courseId=params[0];
                try {
                    URL url=new URL(json_url);
                    HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);


                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("courseId","UTF-8")+"="+URLEncoder.encode(courseId,"UTF-8")+" ";
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

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String result) {
                json_string=result;
                //Toast.makeText(getApplicationContext(),"in post "+json_string, Toast.LENGTH_SHORT).show();
                setListView(json_string);


            }
            public  void setListView(String s)
            {
                jdon_string=s;
                try {
                    jsonObject=new JSONObject(jdon_string);
                    jsonArray=jsonObject.getJSONArray("server_response");
                    int count=0;

                    while (count<jsonArray.length())
                    {
                        JSONObject jo=jsonArray.getJSONObject(count);
                        name=jo.getString("courseName");
                        cid=jo.getString("courseId");
                        al.add(cid);
                        count++;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                listView.setBackgroundColor(Color.parseColor("#F5DC49"));
                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1,al);


                listView.setAdapter(adapter);
            }

       }

}


