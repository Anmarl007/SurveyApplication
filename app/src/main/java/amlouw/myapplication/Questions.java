package amlouw.myapplication;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Questions extends Activity {

    String responseStr,seperator;
    String[] questionData = new String[3];
    ArrayList<String> questionParams = new ArrayList<String>();
    int currentQuestion = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        GetQuestion n = new GetQuestion();
        n.execute();
        Button nextQ = (Button) findViewById(R.id.btnNextQ);
        nextQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestion++;
                GetQuestion n = new GetQuestion();
                n.execute();
            }
        });
    }

    public class GetQuestion extends AsyncTask
    {
        TextView newTextID = (TextView) findViewById(R.id.qID);
        TextView newTextType = (TextView) findViewById(R.id.qType);
        TextView newTextText = (TextView) findViewById(R.id.qText);
        EditText enterText = (EditText) findViewById(R.id.editText);
        Spinner questionSpin = (Spinner) findViewById(R.id.spinner);


        @Override
        protected Object doInBackground(Object[] params) {
            String url = "http://amlouw.1gh.in/getQuestions.php";
            HttpPost request = new HttpPost(url);

            BufferedReader bufferedReader = null;
            StringBuffer stringBuffer = new StringBuffer("");

            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                List<NameValuePair> qID = new ArrayList<NameValuePair>();
                qID.add(new BasicNameValuePair("qID", Integer.toString(currentQuestion)));
                request.setEntity(new UrlEncodedFormEntity(qID, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(request);
                responseStr = EntityUtils.toString(response.getEntity());


                //bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                //String line = "";
                //String LineSeperator = System.getProperty("line.seperator");
                //while((line = bufferedReader.readLine())!=null){
               //    stringBuffer.append(line+LineSeperator);
                //}
                //bufferedReader.close();

                //TextView newText = (TextView) findViewById(R.id.qText);

                //text = stringBuffer.toString();

                //TextView newText = (TextView) findViewById(R.id.qText);
                //newText.setText((CharSequence) stringBuffer);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            seperator = "*";
            if(responseStr.indexOf(seperator) == -1)
            {
                newTextText.setText(responseStr);
            }
            else
            {
                int posBegin = 0;
                int posStop = responseStr.indexOf(seperator);
                for (int i = 0; i < 3; i++) {
                    questionData[i] = responseStr.substring(posBegin, posStop);
                    posBegin = posStop + 1;
                    posStop = responseStr.indexOf(seperator, posBegin);
                }
                newTextID.setText(questionData[0]);
                newTextType.setText(questionData[1]);
                newTextText.setText(questionData[2]);

                if (questionData[1].equals("1")) {
                    enterText.setVisibility(View.VISIBLE);
                    questionSpin.setVisibility(View.INVISIBLE);
                } else if (questionData[1].equals("2")) {
                    enterText.setVisibility(View.INVISIBLE);
                    questionSpin.setVisibility(View.VISIBLE);
                    GetQuestionParams m = new GetQuestionParams();
                    m.execute();
                }
            }
        }
    }

    public class GetQuestionParams extends AsyncTask
    {
        Spinner questionSpin = (Spinner) findViewById(R.id.spinner);


        @Override
        protected Object doInBackground(Object[] params) {
            String url = "http://amlouw.1gh.in/getQuestionParams.php";
            HttpPost request = new HttpPost(url);

            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                List<NameValuePair> qID = new ArrayList<NameValuePair>();
                qID.add(new BasicNameValuePair("qID", questionData[1]));
                request.setEntity(new UrlEncodedFormEntity(qID, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(request);
                responseStr = EntityUtils.toString(response.getEntity());

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            seperator = "*";
            int counter = 0;
            for(int i =0;i<responseStr.length();i++)
            {
                if(responseStr.charAt(i) == '*')
                {
                    counter++;
                }
            }
            int posBegin = 0;
            int posStop = responseStr.indexOf(seperator);
            for(int i =0;i<counter;i++)
            {
                questionParams.add(responseStr.substring(posBegin,posStop));
                posBegin = posStop+1;
                posStop = responseStr.indexOf(seperator,posBegin);
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_item, questionParams);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            questionSpin.setAdapter(dataAdapter);


        }
    }

}
