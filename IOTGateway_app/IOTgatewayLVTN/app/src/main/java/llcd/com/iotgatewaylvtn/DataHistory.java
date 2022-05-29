package llcd.com.iotgatewaylvtn;

public class DataHistory {
    private String Thoigian;
    private String Nhietdo;
    private String Doam;
    private String Anhsang;
    private String Trangthai;

    public DataHistory( String thoigian, String nhietdo, String doam, String anhsang, String trangthai) {
        Thoigian = thoigian;
        Nhietdo = nhietdo;
        Doam = doam;
        Anhsang = anhsang;
        Trangthai = trangthai;
    }

    public String getThoigian() {
        return Thoigian;
    }

    public String getNhietdo() {
        return Nhietdo;
    }

    public String getDoam() {
        return Doam;
    }

    public String getAnhsang() {
        return Anhsang;
    }

    public String getTrangthai() {
        return Trangthai;
    }

}
