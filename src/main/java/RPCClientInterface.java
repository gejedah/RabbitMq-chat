import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RPCClientInterface {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;
    private QueueingConsumer consumer;
    private String username = null;
    private static int id = 0;
    private static String name;
    public static String nickname;

    public RPCClientInterface() throws Exception{
//        id+=1;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        System.out.println("Invoke RPCClient");
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String call(String message) throws Exception{
        synchronized (RPCClientInterface.this){
            String response = null;
            String[] temp_cmd;

            replyQueueName = channel.queueDeclare().getQueue();

//            System.out.println("Nama Reply Queue:" + replyQueueName);

            consumer = new QueueingConsumer(channel);

            channel.basicConsume(replyQueueName, true, consumer);


            String corrId = UUID.randomUUID().toString();

//            System.out.println("CorrId method call: " + corrId);

            temp_cmd = message.split("[<>]");
            temp_cmd = Arrays.stream(temp_cmd).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);

            if (!temp_cmd[0].equalsIgnoreCase("nick")){
//                System.out.println("Sebelum concat: " + message);
                message = message.concat("<" + nickname + ">");
//                System.out.println("Sesudah concat: " + message);
            }

            BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();

            channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

            while (true){
//                System.out.println("Sebelum consumer 1 next Delivery");
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//                System.out.println("Setelah consumer 1 next Delivery");
                if (delivery.getProperties().getCorrelationId().equals(corrId)){
//                    System.out.println("Yang sesuai corrId");
                    response = new String(delivery.getBody(),"UTF-8");
//                    System.out.println("Response method call: " + response);
                    break;
                }
                else{
//                    System.out.println("Yang tidak sesuai corrId");
                    response = new String(delivery.getBody(),"UTF-8");
//                    System.out.println("Response method call: " + response);
                    break;
                }
            }
            return  response;
        }
    }

    public String getMessage() throws Exception{
        synchronized (RPCClientInterface.this){
            String response = null;
            String message = null;

            replyQueueName = channel.queueDeclare().getQueue();

//            System.out.println("Nama Reply Queue:" + replyQueueName);

            consumer = new QueueingConsumer(channel);

            channel.basicConsume(replyQueueName, true, consumer);

            String corrId2 = UUID.randomUUID().toString();

//            System.out.println("CorrId method getMessage: " + corrId2);

            BasicProperties props2 = new BasicProperties.Builder().correlationId(corrId2).replyTo(replyQueueName).build();

            message = "recv<" + nickname + ">";

            channel.basicPublish("", requestQueueName, props2, message.getBytes("UTF-8"));

            while (true){
//                System.out.println("Sebelum consumer 2 next Delivery");
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
//                System.out.println("Setelah consumer 2 next Delivery");
                if (delivery.getProperties().getCorrelationId().equals(corrId2)){
//                    System.out.println("Yang sesuai corrId");
                    response = new String(delivery.getBody(),"UTF-8");
                    break;
                }
                else{
                    System.out.println("Yang tidak sesuai corrId");
                    response = new String(delivery.getBody(),"UTF-8");
//                    break;
                }
            }
            return  response;
        }
    }

    public void close() throws Exception{
        channel.close();
        connection.close();
    }
}
