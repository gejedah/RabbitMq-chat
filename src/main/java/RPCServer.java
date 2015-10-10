import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RPCServer{
    private final static String Rpc_Queue_Name = "rpc_queue";
    private static RPCHandler rpcHandler;
    private static String message = null;

    public RPCServer(){
//        System.out.println("Invoke RPCServer");
    }


    public static void main(String[] argv) throws Exception {
        rpcHandler = new RPCHandler();
        Connection connection = null;
        Channel channel = null;

        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(Rpc_Queue_Name, false, false, false, null);

            channel.basicQos(1);

            QueueingConsumer consumer = new QueueingConsumer(channel);

            channel.basicConsume(Rpc_Queue_Name, false, consumer);

            System.out.println(" [x] Awaiting RPC requests");
            while (true){
                String response = null;
                String[] temp_cmd = null;

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                BasicProperties props = delivery.getProperties();

                BasicProperties replyProps = new BasicProperties.Builder().correlationId(props.getCorrelationId()).build();

                try{
                    message = new String(delivery.getBody(), "UTF-8");

                    System.out.println(" Message yg diterima: " + message);

                    temp_cmd = message.split("[<>]");
                    temp_cmd = Arrays.stream(temp_cmd).filter(s -> (s != null && s.length()>0)).toArray(String[]::new);

//                    for (int i = 0; i < temp_cmd.length; i++) {
//                        System.out.println("Indek ke-" + i + ": " + temp_cmd[i]);
//                        System.out.println(" ===== ");
//                    }

                    if (temp_cmd[0].equalsIgnoreCase("nick")){
                        if (rpcHandler.addUser(temp_cmd[1])){
                            response = "OK";
                        }
                        else{
                            response = "FALSE";
                        }
                    }
                    else if (temp_cmd[0].equalsIgnoreCase("leave")){
                        if (rpcHandler.leaveChannel(temp_cmd[1],temp_cmd[2])){
                            response = "Anda berhasil meninggalkan channel " + temp_cmd[1];
                        }
                        else{
                            response = "Anda tidak tergabung dengan channel tersebut atau kesalahan nama channel";
                        }
                    }
                    else if (temp_cmd[0].equalsIgnoreCase("join")){
                        if (rpcHandler.joinChannel(temp_cmd[1],temp_cmd[2])){
                            response = "Anda telah berhasil bergabung dengan channel " + temp_cmd[1] + " sebagai " + temp_cmd[2];
                        }
                        else{
                            response = "Anda telah tergabung dengan channel tersebut atau kesalahan nama channel";
                        }

                    }
                    else if (temp_cmd[0].equalsIgnoreCase("@")){
                        rpcHandler.sendChannel(temp_cmd[1],temp_cmd[3],temp_cmd[2]);
                    }
                    else if (temp_cmd[0].equalsIgnoreCase("exit")){
                        rpcHandler.exit(temp_cmd[1]);
                    }
                    else if (temp_cmd[0].equalsIgnoreCase("recv")){
                        response = rpcHandler.recvMessage(temp_cmd[1]);
                    }
                    else{
                        rpcHandler.sendAllChannel(temp_cmd[1],temp_cmd[0]);
                    }
                }
                catch (Exception e){
                    System.out.println(" [.] " + e.toString());
                    response = "";
                }
                finally {
//                    System.out.println("Arah Reply Pertama: " + props.getReplyTo());

//                    System.out.println("Properti corrdId pertama: " + replyProps.getCorrelationId());

                    if (response == null){
                        response= "";
                    }

                    channel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
//                System.out.println("Udah sampai akhir");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (connection != null){
                try{
                    connection.close();
                }
                catch (Exception ignore){
                }
            }
        }
    }

}