import java.sql.Timestamp;
import java.util.*;

public class RPCHandler
{
    public HashMap dftr_channel;
    public HashMap<String, ArrayList<ctujuh>> dftr_user;
    private static int id=0;

    public RPCHandler(){
        dftr_channel=new HashMap();
        dftr_user=new HashMap<String, ArrayList<ctujuh>>();
        id+=1;
//        System.out.println("Id sekarang: "+ id);
    }

    
    public boolean addUser(String nickname){
        if (dftr_user.containsKey(nickname)){
            return false;
        }
        else{
            dftr_user.put(nickname, new ArrayList<ctujuh>());

            return true;
        }
    }

    public boolean joinChannel(String channelname, String nickname){
        if (dftr_channel.containsKey(channelname)){
            if (((clima) dftr_channel.get(channelname)).anggota_user.contains(nickname)){
                return false;
            }
            else{
                ((clima) dftr_channel.get(channelname)).anggota_user.add(nickname);

                dftr_user.get(nickname).add(new ctujuh(channelname));
                return true;
            }
        }
        else{
            dftr_channel.put(channelname, new clima());
            ((clima) dftr_channel.get(channelname)).anggota_user.add(nickname);

            if (dftr_user.containsKey(nickname)){
                dftr_user.get(nickname).add(new ctujuh(channelname));
            }
            else{
                System.out.println("Tidak ada hash key dengan nickname: '" + nickname + "'");
            }
            return true;
        }
    }

    public boolean leaveChannel(String channelname, String nickname){
        if (dftr_channel.containsKey(channelname)){
            if (((clima) dftr_channel.get(channelname)).anggota_user.contains(nickname)){
                ((clima) dftr_channel.get(channelname)).anggota_user.remove(nickname);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    public void exit(String nickname){
        ArrayList<ctujuh> temp_list_channel= (ArrayList<ctujuh>) dftr_user.get(nickname);

//        System.out.println("Nickname sebelum exit: " + nickname);

        for (int i = 0; i < temp_list_channel.size(); i++) {
            leaveChannel(((ctujuh) temp_list_channel.get(i)).channelname, nickname);
        }
        dftr_user.remove(nickname);
    }

    public void sendChannel(String channelname, String nickname, String message){
        if (dftr_channel.containsKey(channelname)){
            if (((clima) dftr_channel.get(channelname)).anggota_user.contains(nickname)){
                ((clima) dftr_channel.get(channelname)).messages.add(new cenam(nickname, message));
            }
        }
    }

    public void sendAllChannel(String nickname, String message){
        ArrayList<ctujuh> temp_list_channel= (ArrayList<ctujuh>) dftr_user.get(nickname);
        for (int i = 0; i < temp_list_channel.size(); i++) {
            sendChannel(((ctujuh) temp_list_channel.get(i)).channelname, nickname, message);
        }
    }

    public String recvMessage(String nickname){
        ArrayList<ctujuh> temp_list_channel= (ArrayList<ctujuh>) dftr_user.get(nickname);
        List<String> response=new ArrayList<String>();
        Timestamp waktu_terakhir;
        String ret_messages = "";

        for (int i = 0; i < temp_list_channel.size(); i++) {
            waktu_terakhir= temp_list_channel.get(i).waktu_terakhir;
            response.addAll(((clima) dftr_channel.get(temp_list_channel.get(i).channelname)).getAfterMessages(waktu_terakhir, temp_list_channel.get(i).channelname));
            ((ArrayList<ctujuh>) dftr_user.get(nickname)).get(i).waktu_terakhir=new Timestamp(System.currentTimeMillis());
        }

        for (int i = response.size() -1; i >= 0; i--) {
            if (i == response.size() - 1){
                ret_messages = ret_messages.concat(response.get(i).toString());
            }
            else{
                ret_messages = ret_messages.concat("\n" + response.get(i).toString());
            }
        }
        System.out.println("Return Messages: " + ret_messages);
        return ret_messages;
    }

}