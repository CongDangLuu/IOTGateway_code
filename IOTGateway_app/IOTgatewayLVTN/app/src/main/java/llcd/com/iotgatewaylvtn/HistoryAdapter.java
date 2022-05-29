package llcd.com.iotgatewaylvtn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<DataHistory> datalist;

    public HistoryAdapter(Context context, int layout, List<DataHistory> datalist) {
        this.context = context;
        this.layout = layout;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        TextView txtThoigian, txtNhietdo, txtDoam, txtAnhsang, txtTrangthai;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder Holder;
        if(view == null){
            Holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            Holder.txtNhietdo   = (TextView) view.findViewById(R.id.Nhietdo1);
            Holder.txtDoam      = (TextView) view.findViewById(R.id.Doam1);
            Holder.txtAnhsang   = (TextView) view.findViewById(R.id.Anhsang1);
            Holder.txtThoigian  = (TextView) view.findViewById(R.id.Thoigian1);
            Holder.txtTrangthai = (TextView) view.findViewById(R.id.Trangthai1);

            view.setTag(Holder);
        }
        else {
            Holder = (ViewHolder) view.getTag();
        }

        DataHistory Data = datalist.get(i);

        Holder.txtNhietdo.setText(Data.getNhietdo());
        Holder.txtDoam.setText(Data.getDoam());
        Holder.txtAnhsang.setText(Data.getAnhsang());
        Holder.txtTrangthai.setText(Data.getTrangthai());
        Holder.txtThoigian.setText(Data.getThoigian());

        return view;
    }

}

