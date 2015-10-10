import java.sql.Timestamp;
import java.util.ArrayList;

public class ctujuh{
    public String channelname;
    public Timestamp waktu_terakhir;

    public ctujuh(String nama_channel){
        channelname=nama_channel;
        waktu_terakhir=new Timestamp(System.currentTimeMillis());
    }
}