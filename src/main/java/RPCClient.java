import java.util.*;

public class RPCClient{
    private static Thread messageListenerThread2;
    private static RPCClientInterface rpcClient;

    private static boolean isLoggedIn = false;

    public static void main(String[] argv) throws Exception {
        rpcClient = new RPCClientInterface();
        String response = null;
        String[] temp_cmd;
        String cmd;

        Scanner in = new Scanner(System.in);
        System.out.print("Enter your command: ");
        cmd = in.next();

        temp_cmd = cmd.split("[<>]");
        temp_cmd = Arrays.stream(temp_cmd).filter(s -> (s != null && s.length()>0)).toArray(String[]::new);

        Runnable messageListener2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Message Listener 2 started...");
                try {
                    int i = 0;
                    while (isLoggedIn){
                        System.out.println(rpcClient.getMessage());
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        messageListenerThread2 = new Thread(messageListener2);

//        rpcClient.setName(cmd);

        try {
            while (temp_cmd[0].equalsIgnoreCase("nick") && !isLoggedIn){
                if (rpcClient.call(cmd).equalsIgnoreCase("OK")){
                    rpcClient.nickname=temp_cmd[1];
                    isLoggedIn=true;
                    System.out.println("Selamat, anda terdaftar sebagai " + rpcClient.nickname);
                }
                else{
                    System.out.println("Nickname yang dimasukkan sudah ada, input nickname baru!");
                }
                System.out.print("Enter your command: ");
                cmd = in.next();
                temp_cmd = cmd.split("[<>]");
                temp_cmd = Arrays.stream(temp_cmd).filter(s -> (s != null && s.length()>0)).toArray(String[]::new);
            }

            while (!isLoggedIn){
                StringBuilder nick_default=new StringBuilder();
                char ch_random='a';
                Random random_int=new Random();
                while (ch_random <= 'z'){
                    nick_default.append(ch_random);
                    ch_random+=random_int.nextInt(5);
                }
                rpcClient.nickname = nick_default.toString();
                String message = "nick<" + rpcClient.nickname + ">";
//                System.out.println("Autogenerate message nick: " + message);
                if (rpcClient.call(message).equalsIgnoreCase("OK")){
                    isLoggedIn=true;
                    System.out.println("Selamat, anda terdaftar sebagai "+ rpcClient.nickname);
                }
            }

            messageListenerThread2.start();

            while (!cmd.equalsIgnoreCase("exit")){
                response = rpcClient.call(cmd);
//                String dih = null;
//                dih.copyValueOf(response.toCharArray().toString).toString();
//                System.out.println("Nama client ini: " + rpcClient.getName());
//                System.out.println("Nickname anda: "+ rpcClient.nickname);
                if (!response.equalsIgnoreCase("")){
                    System.out.println(" [.] Got '" + response + "'");
                }
                synchronized (rpcClient) {
                    System.out.print("Enter your command: ");
                    cmd = in.next();
                }
            }
            isLoggedIn = false;
            rpcClient.call(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try{
                if (messageListenerThread2 != null){
                    messageListenerThread2.join();
                }
//                System.out.println("why1?");
                rpcClient.close();
//                System.out.println("why2?");
            }
            catch (Exception ignore) { }
        }
    }


}