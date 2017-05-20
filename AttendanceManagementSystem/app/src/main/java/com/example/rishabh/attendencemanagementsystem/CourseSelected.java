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
import android.widget.TextView;
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
import java.util.HashMap;

public class CourseSelected extends AppCompatActivity {
    String courseId;
    TextView tv;
    ListView listView;
    String json_string,name,id;
    ArrayList al=new ArrayList();
    ArrayList studentMarked;
    dbHelper obj;
    boolean updatecheck=false;
    HashMap<String,String> totalStudent= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentMarked = new ArrayList();
        setContentView(R.layout.activity_course_selected);
        Intent i=getIntent();
        Bundle b= i.getExtras();
        courseId= b.getString("courseId");
        tv=(TextView) findViewById(R.id.textView);
        listView=(ListView) findViewById(R.id.student);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        tv.setText(courseId);
        new backgroundTask().execute(courseId);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (studentMarked.contains(al.get(position))) {
                    studentMarked.remove(al.get(position));
                    //Toast.makeText(getApplicationContext(), "removed", Toast.LENGTH_SHORT).show();


                } else {
                    studentMarked.add(al.get(position));
                   // Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_SHORT).show();
                }

            }
        });


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
            json_url="http://www.rishabhnegi.esy.es/studentData.php";
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String courseId=params[0];
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
                    name=jo.getString("name");
                    id=jo.getString("id");
                    totalStudent.put(name,id);
                    al.add(name);
                    count++;

                }
                obj = new dbHelper(getApplicationContext(), al);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            listView.setBackgroundColor(Color.parseColor("#F5DC49"));

            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_multiple_choice,al);


            listView.setAdapter(adapter);
        }

    }
    public void sync(View v)
    {
        obj.markAttendence(totalStudent,studentMarked,courseId);
        studentMarked.clear();
        for(int i=0;i<listView.getChildCount();i++)
             listView.setItemChecked(i,false);
        updatecheck=true;
    }

    public void serverSync(View v)throws Exception
    {
        if(updatecheck) {
            String jsonresp = obj.createJsonResopse();
            obj.clearDataBase();
            new backgroundTask2(this).execute(jsonresp);
            updatecheck=false;
            Toast.makeText(this,"Updated on SERVER!!!", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this,"Nothing to update!!!", Toast.LENGTH_SHORT).show();
        }

    }


}
