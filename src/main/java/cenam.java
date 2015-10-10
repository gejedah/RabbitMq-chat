import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class cenam{
    public String username;
    public String message;
    public Timestamp waktu_buat;

    public cenam(){

    }

    public cenam(String akun, String teks){
        username=akun;
        message=teks;
        waktu_buat=new Timestamp(System.currentTimeMillis());
    }

}