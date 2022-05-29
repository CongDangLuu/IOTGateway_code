package llcd.com.iotgatewaylvtn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class BtnActivity extends AppCompatActivity {
    public static Button btnWif1, btnWif2, btnWif3, btnWif4, btnWif5;
    public static Button btnSub1, btnSub2, btnSub3, btnSub4, btnSub5;
    public static Button btnBlu1, btnBlu2;
    public static TextView NoDV;
    public static String Json_URL,Database_URL ="";
    public static String Pro, Protxt, MCU ="";
    private int a =1;
    int stt[]={0,0,0,0,0,0,0,0,0,0,0,0};//Wifi 1, Wifi 2, Blu 1, Blu2, Sub.
    int pre_stt[]={0,0,0,0,0};
    public int No;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NoDV = findViewById(R.id.NoDevice);

        btnWif1 = findViewById(R.id.btnWif1);
        btnWif2 = findViewById(R.id.btnWif2);
        btnWif3 = findViewById(R.id.btnWif3);
        btnWif4 = findViewById(R.id.btnWif4);
        btnWif5 = findViewById(R.id.btnWif5);

        btnSub1 = findViewById(R.id.btnSub1);
        btnSub2 = findViewById(R.id.btnSub2);
        btnSub3 = findViewById(R.id.btnSub3);
        btnSub4 = findViewById(R.id.btnSub4);
        btnSub5 = findViewById(R.id.btnSub5);

        btnBlu1 = findViewById(R.id.btnBlu1);
        btnBlu2 = findViewById(R.id.btnBlu2);
        Json_URL = Database_URL ="";

//sttdv
        new CountDownTimer(1000000000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new docJson().execute(Urls.Stt_path);

                    }
                });
            }
            @Override
            public void onFinish() {
            }
        }.start();



//button

        btnWif1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Protxt = "WIFI 1";
                Pro = "WIFI1";
                MCU = "ESP";
                SwScream();

            }
        });
        btnWif2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "WIFI 2";
                Pro = "WIFI2";
                MCU = "ESP";
                SwScream();
            }
        });
        btnWif3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "WIFI 3";
                Pro = "WIFI3";
                MCU = "ESP";
                SwScream();
            }
        });
        btnWif4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "WIFI 4";
                Pro = "WIFI4";
                MCU = "ESP";
                SwScream();
            }
        });
        btnWif5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "WIFI 5";
                Pro = "WIFI5";
                MCU = "ESP";
                SwScream();
            }
        });

        btnBlu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "BLUETOOTH 1";
                Pro = "BLU1";
                MCU = "STM";
                SwScream();
            }
        });
        btnBlu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "BLUETOOTH 2";
                Pro = "BLU2";
                MCU = "STM";
                SwScream();
            }
        });

        btnSub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Protxt = "Sub-Ghz 1";
                Pro = "SUB1";
                MCU = "STM";
                SwScream();

            }
        });
        btnSub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "Sub-Ghz 2";
                Pro = "SUB2";
                MCU = "STM";
                SwScream();
            }
        });
        btnSub3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "Sub-Ghz 3";
                Pro = "SUB3";
                MCU = "STM";
                SwScream();
            }
        });
        btnSub4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "Sub-Ghz 4";
                Pro = "SUB4";
                MCU = "STM";
                SwScream();
            }
        });
        btnSub5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Protxt = "Sub-Ghz 5";
                Pro = "SUB5";
                MCU = "STM";
                SwScream();
            }
        });

    }

    private void SwScream(){
        if(DisplayActivity.flattimer == 1){
            DisplayActivity.timerDisplay.cancel();
            DisplayActivity.flattimer =0;
       }
        Intent intent = new Intent(BtnActivity.this, DisplayActivity.class);
        startActivity(intent);
    }


    class docJson extends AsyncTask<String, Integer, String>    {
        @Override
        protected String doInBackground(String... params) {
            return readJson(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                String a;
                JSONObject root = new JSONObject(s);

                stt[0] = valStt(root.getString("WIFI1"));
                stt[1] = valStt(root.getString("WIFI2"));
                stt[2] = valStt(root.getString("WIFI3"));
                stt[3] = valStt(root.getString("WIFI4"));
                stt[4] = valStt(root.getString("WIFI5"));

                stt[5] = valStt(root.getString("BLU1"));
                stt[6] = valStt(root.getString("BLU2"));

                stt[7] = valStt(root.getString("SUB1"));
                stt[8] = valStt(root.getString("SUB2"));
                stt[9] = valStt(root.getString("SUB3"));
                stt[10] =valStt(root.getString("SUB4"));
                stt[11] =valStt(root.getString("SUB5"));

                No=0;
                for(int i =0;i<12;i++) {
                    if(stt[i]==1){
                        No+=1;
                    }
                }
                BtnActivity.NoDV.setText(String.valueOf(No));

//--------------enable btn----------------------------------
                enableBTN( stt);


//--------------enable btn----------------------------------



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

    private static int valStt(String str){
        int a;
        if(str.equals("")){
            a=0;
        }else{
            a = Integer.parseInt(str);
        }
        return a;
    }

    private static void enableBTN(int stt[]){

        if(stt[0]==0) {
            btnWif1.setEnabled(false);
            btnWif1.setBackgroundColor(Color.GRAY);
        }
        else{
            btnWif1.setEnabled(true);
            btnWif1.setBackgroundColor(Color.BLUE);
        }
        if(stt[1]==0) {
            btnWif2.setEnabled(false);
            btnWif2.setBackgroundColor(Color.GRAY);
        }
        else{
            btnWif2.setEnabled(true);
            btnWif2.setBackgroundColor(Color.BLUE);
        }
        if(stt[2]==0) {
            btnWif3.setEnabled(false);
            btnWif3.setBackgroundColor(Color.GRAY);
        }
        else{
            btnWif3.setEnabled(true);
            btnWif3.setBackgroundColor(Color.BLUE);
        }
        if(stt[3]==0) {
            btnWif4.setEnabled(false);
            btnWif4.setBackgroundColor(Color.GRAY);
        }
        else{
            btnWif4.setEnabled(true);
            btnWif4.setBackgroundColor(Color.BLUE);
        }
        if(stt[4]==0) {
            btnWif5.setEnabled(false);
            btnWif5.setBackgroundColor(Color.GRAY);
        }
        else{
            btnWif5.setEnabled(true);
            btnWif5.setBackgroundColor(Color.BLUE);
        }


        if(stt[5]==0) {
            btnBlu1.setEnabled(false);
            btnBlu1.setBackgroundColor(Color.GRAY);
        }
        else{
            btnBlu1.setEnabled(true);
            btnBlu1.setBackgroundColor(Color.BLUE);
        }
        if(stt[6]==0) {
            btnBlu2.setEnabled(false);
            btnBlu2.setBackgroundColor(Color.GRAY);
        }
        else{
            btnBlu2.setEnabled(true);
            btnBlu2.setBackgroundColor(Color.BLUE);
        }

        if(stt[7]==0) {
            btnSub1.setEnabled(false);
            btnSub1.setBackgroundColor(Color.GRAY);
        }
        else{
            btnSub1.setEnabled(true);
            btnSub1.setBackgroundColor(Color.BLUE);
        }
        if(stt[8]==0) {
            btnSub2.setEnabled(false);
            btnSub2.setBackgroundColor(Color.GRAY);
        }
        else{
            btnSub2.setEnabled(true);
            btnSub2.setBackgroundColor(Color.BLUE);
        }
        if(stt[9]==0) {
            btnSub3.setEnabled(false);
            btnSub3.setBackgroundColor(Color.GRAY);
        }
        else{
            btnSub3.setEnabled(true);
            btnSub3.setBackgroundColor(Color.BLUE);
        }
        if(stt[10]==0) {
            btnSub4.setEnabled(false);
            btnSub4.setBackgroundColor(Color.GRAY);
        }
        else{
            btnSub4.setEnabled(true);
            btnSub4.setBackgroundColor(Color.BLUE);
        }
        if(stt[11]==0) {
            btnSub5.setEnabled(false);
            btnSub5.setBackgroundColor(Color.GRAY);
        }
        else{
            btnSub5.setEnabled(true);
            btnSub5.setBackgroundColor(Color.BLUE);
        }
    }
}
