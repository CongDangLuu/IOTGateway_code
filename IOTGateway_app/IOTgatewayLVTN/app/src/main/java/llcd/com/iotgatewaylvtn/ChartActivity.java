package llcd.com.iotgatewaylvtn;


import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class ChartActivity extends AppCompatActivity {
    Button NDChart, DAChart, ASChart;
    TextView minview, maxview, t_minview, t_maxview;

    LineChart chart;
    XAxis xAxis;
    YAxis leftAxis;

    String Timest, day, Pre_day, time,set_day="";

    String  t_min, t_max;
    float min=1000;
    float max =0;


    Float ValOfChart;
    String URL;
    String Lable = "Nhiệt độ";
    String typeData = "ND";

    private Spinner spnCategory;
    private List<String> list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        NDChart = findViewById(R.id.BtnNDChart);
        ASChart = findViewById(R.id.BtnASChart);
        DAChart = findViewById(R.id.BtnDAChart);
        minview = findViewById(R.id.minvalue);
        t_minview = findViewById(R.id.mintimeval);
        maxview = findViewById(R.id.maxvalue);
        t_maxview = findViewById(R.id.maxtimeval);


        chart = findViewById(R.id.NhietdoChart);
        xAxis = chart.getXAxis();
        leftAxis = chart.getAxisLeft();

        spnCategory = findViewById(R.id.spnCategory);

        URL = Urls.GetDatabase + BtnActivity.Pro +".php";

        combobox(URL);




        NDChart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Lable = "Nhiệt độ";
                typeData = "ND";
                GetChart(URL, Lable, typeData, set_day);
            }
        });

        DAChart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Lable = "Độ ẩm";
                typeData = "DA";
                GetChart(URL, Lable, typeData, set_day);
            }
        });

        ASChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lable = "Ánh sáng";
                typeData = "AS";
                GetChart(URL, Lable, typeData, set_day);
            }
        });

    }
//set data--------------------------------

    private void GetChart(String url, String lable, String type, String day_c) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // 2.truyền đường dẫn vào request
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String date = Sort_date(day_c);
                t_min= t_max="";
                min=1000;
                max =0;
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                setupchart(type);
                ArrayList<Entry> dataSet = new ArrayList<Entry>();
                for (int i = 0; i < 24; i++) {
                    float sum=0;
                    int a = 0;
                    int t =0;

                    for (int j = 0; j < response.length(); j++) {
                        try {
                            JSONObject person = response.getJSONObject(j);
                            Timest = person.getString("Time");
                            String splits[] = Timest.split(" ");
                            String tg[] = splits[1].split(":");
                            day = splits[0];
                            time = tg[0];
                            t = Integer.parseInt(time);
                            if((day.equals(date))&&(i==t)){
                                float value = Float.parseFloat(person.getString(type));
                                sum +=value;
                                a++;
                                if(value <= min){
                                    min = value;
                                    t_min = splits[1];
                                }
                                if(value >= max){
                                    max = value;
                                    t_max = splits[1];
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    float x = sum/a;
                    ValOfChart = (float)Math.round(x * 100)/100;
                    dataSet.add(new Entry(i, ValOfChart));

                }


                minview.setText(String.valueOf(min));
                t_minview.setText(t_min);
                maxview.setText(String.valueOf(max));
                t_maxview.setText(t_max);

                String str = lable + " - " + day_c;
                LineDataSet lineDataSet = new LineDataSet(dataSet, str);
                ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
                iLineDataSets.add(lineDataSet);

                LineData lineData = new LineData(iLineDataSets);
                chart.setData(lineData);
                chart.invalidate();

                chart.setNoDataText("Data not Available");

                //you can modify your line chart graph according to your requirement there are lots of method available in this library
                //now customize line chart
                int color;
                switch (type) {
                    case "ND":
                        color = Color.RED;
                        break;
                    case "DA":
                        color = Color.BLUE;
                        break;
                    case "AS":
                        color = Color.YELLOW;
                        break;
                    default:
                        color = Color.RED;
                        break;
                }
                lineDataSet.setColor(color);
                Legend legend = chart.getLegend();
                int[] colorClassArray = new int[] {color};
                String[] legendName = {str};
                LegendEntry[] legendEntries = new LegendEntry[1];
                LegendEntry entry = new LegendEntry();
                entry.formColor = colorClassArray[0];
                entry.label = String.valueOf(legendName[0]);
                legendEntries[0] = entry;
                legend.setCustom(legendEntries);
                lineDataSet.setDrawCircles(false);

                lineDataSet.setLineWidth(1.5f);
                lineDataSet.setValueTextColor(Color.BLACK);
                lineDataSet.setValueTextSize(5f);

                legend.setTextSize(15);
                legend.setForm(Legend.LegendForm.LINE);
                legend.setXEntrySpace(15);
                legend.setFormSize(17);
                legend.setFormToTextSpace(10);

                Description descrip = new Description();
                descrip.setText(" ");
                descrip.setTextColor(Color.BLACK);
                descrip.setTextSize(17);
                chart.setDescription(descrip);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        //4. add
        queue.add(req);
    }


    private void setupchart(String type){

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(23f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);

        if( type=="ND"){

            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(50f);
        }else if( type=="AS"){
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(500f);
        }else if( type == "DA"){
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(100f);
        }
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
                Pre_day=day="";

                for (int i = response.length() - 1; i >= 0; i--) {
                    try {
                        JSONObject person = response.getJSONObject(i);
                        Timest = person.getString("Time");
                        String Spl[] = Timest.split(" ");
                        day = Sort_date(Spl[0]);
                        if(!(day.equals(Pre_day))){
                            list.add(day);
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

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategory.setAdapter(adapter);
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                set_day = list.get(position);
                GetChart(URL, Lable, typeData, set_day);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                set_day= list.get(0);
                GetChart(URL, Lable, typeData, set_day);
            }
        });
    }

    private String Sort_date(String date){
        String tg[] = date.split("-");
        String  time_sort= tg[2]+"-"+tg[1]+"-"+tg[0];
        return time_sort;
    }

    private float min(float a, float b){
        float min=a;
        if(b<a){
            min = b;
        }
        return min;
    }

    private float max(float a, float b){
        float max =a;
        if(b >a){
            max = b;
        }
        return max;
    }

}


