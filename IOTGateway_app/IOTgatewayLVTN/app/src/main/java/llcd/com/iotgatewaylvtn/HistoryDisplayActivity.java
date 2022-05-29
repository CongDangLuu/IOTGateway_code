package llcd.com.iotgatewaylvtn;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class HistoryDisplayActivity extends AppCompatActivity {

    ListView LVHistory;
    ArrayList<DataHistory> arrayData;
    HistoryAdapter adapter;

    private Spinner spnCategory, spnCategoryHour;
    private List<String> list, listHour;

    String Timest, day, Pre_day , hour, set_day, set_hour="";

    String URL;
    public static TextView DVName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        LVHistory = findViewById(R.id.LVData);


        DVName = findViewById(R.id.DVName);
        spnCategory = findViewById(R.id.spnCategoryHistory);
        spnCategoryHour = findViewById(R.id.spnCategoryHistoryHour);

        set_hour= DisplayActivity.hour;
        listHour = new ArrayList<>();
        for(int i =0; i<24;i++){
            String str =String.valueOf(i);
            if(i<10){
                str ="0"+ String.valueOf(i);
            }
            listHour.add(str);

        }



        URL = Urls.GetDatabase + BtnActivity.Pro +".php";
        HistoryDisplayActivity.DVName.setText(DisplayActivity.Name);

        combobox(URL);
    }

    private void Getdata(String url, String day_select, String hour_select){
        String date = Sort_date(day_select);
        arrayData = new ArrayList<>();
        adapter = new HistoryAdapter(this, R.layout.activity_history_view, arrayData);
        LVHistory.setAdapter(adapter);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int flat =0;
                        for (int i = response.length()-1; i>=0 ; i--){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Timest = object.getString("Time");
                                String splits[] = Timest.split(" ");
                                day = splits[0];
                                String tg[] = splits[1].split(":");
                                hour = tg[0];
                                if(day.equals(date) && hour.equals(hour_select)){
                                    flat =1;
                                    arrayData.add(new DataHistory(
                                            splits[1],
                                            object.getString("ND"),
                                            object.getString("DA"),
                                            object.getString("AS"),
                                            object.getString("TT")
                                    ));
                                }else if(flat==1){
                                    break;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                       //Toast.makeText(HistoryDisplayActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }


    private void combobox(String url) {
        list = new ArrayList<>();

        // 1.Khởi tạo request
        RequestQueue queue = Volley.newRequestQueue(this);
        // 2.truyền đường dẫn vào request
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // chuyen mang thanh chuoi
                for (int i = response.length() - 1; i >= 0; i--) {
                    try {
                        JSONObject person = response.getJSONObject(i);
                        Timest = person.getString("Time");
                        String Spl[] = Timest.split(" ");
                        day = Spl[0];
                        if(!(day.equals(Pre_day))){
                            list.add(Sort_date(day));
                            Pre_day=day;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                showCombobox(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(req);


    }

    private void showCombobox(List<String> list){
        //Spinner day
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.spinner_list,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategory.setAdapter(adapter);

        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                set_day = list.get(position);
                Getdata(URL, set_day, set_hour);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                set_day= list.get(0);
                Getdata(URL, set_day, set_hour);

            }
        });
        //Spinner hour
        ArrayAdapter<String> adapterHour = new ArrayAdapter(this, R.layout.spinner_list,listHour);
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategoryHour.setAdapter(adapterHour);
        spnCategoryHour.setSelection(Integer.parseInt(set_hour));
        spnCategoryHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                set_hour = listHour.get(position);
                Getdata(URL, set_day, set_hour );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    private String Sort_date(String date){
        String tg[] = date.split("-");
        String  time_sort= tg[2]+"-"+tg[1]+"-"+tg[0];
        return time_sort;
    }
}
