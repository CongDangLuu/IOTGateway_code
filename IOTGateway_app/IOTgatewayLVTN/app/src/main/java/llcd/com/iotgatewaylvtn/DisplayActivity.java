package llcd.com.iotgatewaylvtn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;



public class DisplayActivity extends AppCompatActivity {
    TextView NDtxt, DAtxt, AStxt, Prttxt, DVname, TTtxt;
    String Pro, Stt, MCU = "";

    String UrlDB = "";
    String url_control = Urls.Control_path;
    public static String Name = "Device Name";
    public static String hour = "-1";

    Button HistoryBtn, BtnOn, BtnOff, ChartBtn;
    public static CountDownTimer timerDisplay;
    public static int flattimer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        HistoryBtn = findViewById(R.id.History_Btn);
        ChartBtn = findViewById(R.id.Chart_Btn);

        NDtxt = findViewById(R.id.NDView1);
        DAtxt = findViewById(R.id.DAView1);
        AStxt = findViewById(R.id.ASView1);
        Prttxt = findViewById(R.id.PrtView);
        DVname = findViewById(R.id.DVName);
        TTtxt = findViewById(R.id.TTView1);

        BtnOn = findViewById(R.id.BtnOn);
        BtnOff = findViewById(R.id.BtnOff);

        Pro = BtnActivity.Pro;
        MCU = BtnActivity.MCU;
        Prttxt.setText(BtnActivity.Protxt);
        UrlDB = Urls.GETDataJson + Pro +".json";

       timerDisplay = new CountDownTimer(1000000000, 1000) {
           @Override
            public void onTick(long millisUntilFinished) {
               flattimer =1;
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       new DisplayActivity.dataJson().execute(UrlDB);
                   }
               });
            }

            @Override
            public void onFinish() {
                flattimer =0;
            }
        }.start();

        HistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, HistoryDisplayActivity.class);
                startActivity(intent);
            }
        });
        ChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayActivity.this, ChartActivity.class);
                startActivity(intent);
            }
        });

        BtnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stt = "1";
                BtnOn.setBackgroundColor(Color.GREEN);
                BtnOff.setBackgroundColor(Color.GRAY);
                ESPcontrol(Pro, Stt, MCU);
            }
        });
        BtnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stt = "0";
                BtnOn.setBackgroundColor(Color.GRAY);
                BtnOff.setBackgroundColor(Color.RED);
                ESPcontrol(Pro, Stt, MCU);
            }
        });

    }

    private void ESPcontrol(String Pro, String Stt, String MCU) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_control, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Device", Pro);
                params.put("Stt", Stt);
                params.put("MCU", MCU);

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DisplayActivity.this);
        queue.add(stringRequest);
    }

    class dataJson extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return readJson(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                String a;
                JSONObject person = new JSONObject(s);
                Name =person.getString("Name");
                DVname.setText(Name);
                NDtxt.setText(person.getString("Temperature") + "  *C");
                DAtxt.setText(person.getString("Humidity")+  "  %");
                AStxt.setText(person.getString("Light") + "  lx");
                TTtxt.setText(person.getString("Status") );

                String timest = person.getString("Time");
                String slp[] = timest.split(" ");
                String tg[] = slp[1].split(":");
                hour = tg[0];
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static String readJson(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            // create a url object
            URL url = new URL(theUrl);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}




