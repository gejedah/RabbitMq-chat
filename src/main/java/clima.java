import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class clima{
    public ArrayList<String> anggota_user;
    public ArrayList<cenam> messages;

    public clima(){
        anggota_user= new ArrayList<String>();
        messages=new ArrayList<cenam>();
    }

    public List<String> getAfterMessages(Timestamp waktu_join_channel, String channel){
        ArrayList<String> notif_messages=new ArrayList<String>();
        String pre_process_ret;
        int i=messages.size()-1;
        boolean batas=true;
        while ((i>=0) && batas){
            if (((cenam) messages.get(i)).waktu_buat.after(waktu_join_channel)){
//                System.out.println("Waktu buat"+ ((cenam) messages.get(i)).waktu_buat);
                pre_process_ret= "<" + channel + "><" + ((cenam) messages.get(i)).username + "><" + ((cenam) messages.get(i)).message + ">";
                notif_messages.add(pre_process_ret);
            }
            else{
                batas=false;
            }
            i--;
        }
        return notif_messages;
    }
}