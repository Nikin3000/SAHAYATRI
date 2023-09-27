package com.example.proto3.Services;

import android.os.AsyncTask;
import com.azure.messaging.servicebus.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class SendMessageTask {

    private static final Logger logger = LoggerFactory.getLogger(SendMessageTask.class);
    private final String connectionString;
    private final String queueName;

    public SendMessageTask() {
        super();
        this.connectionString = "Endpoint=sb://sahayatri-apple.servicebus.windows.net/;SharedAccessKeyName=local-development;SharedAccessKey=SjiZJDhSAMsGJEnGItris7Du3xrsoT8Qf+ASbGvs+wM=;EntityPath=default-apple";
        this.queueName = "default-apple";
    }

    public void sendCustomerRequest(String pickupLocation, String dropOffLocation, String time) {
        String messageContent = "Customer Request: Pickup: " + pickupLocation +
                ", Drop-off: " + dropOffLocation +
                ", Time: " + time;
        sendMessage(messageContent);
    }

    public void sendDriverAcceptance(String driverName, String estimatedArrivalTime) {
        String messageContent = "Driver Acceptance: Driver: " + driverName +
                ", ETA: " + estimatedArrivalTime;
        sendMessage(messageContent);
    }

    private void sendMessage(String content) {
        try {
            ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                    .connectionString(connectionString)
                    .sender()
                    .queueName(queueName)
                    .buildClient();

            System.out.println("Sending message...");
            sender.sendMessage(new ServiceBusMessage(content));
            System.out.println("Message sent successfully...");

            sender.close();
        } catch (Exception e) {
            System.out.println("Failed to send message: "+ e);
        }
    }

    public CompletableFuture<Void> receiveMessages() {
        return CompletableFuture.runAsync(() -> {
            try {
                ServiceBusReceiverClient receiver = new ServiceBusClientBuilder()
                        .connectionString(connectionString)
                        .receiver()
                        .queueName(queueName)
                        .buildClient();

                System.out.println("Receiving messages...");

                while (true) {
                    Iterable<ServiceBusReceivedMessage> messages = receiver.receiveMessages(1, Duration.ofSeconds(15));
                    for (ServiceBusReceivedMessage message : messages) {
                        System.out.println("Received message: " + message.getBody());
                        receiver.complete(message);
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to receive messages: " + e);
            }
        });
    }




//    @Override
//    protected Void doInBackground(Void... voids) {
//        try {
//            System.out.println("Sending message");
//            System.out.println(this.connectionString);
//            System.out.println(this.queueName);
//            ServiceBusSenderClient sender = new ServiceBusClientBuilder()
//                    .connectionString(connectionString)
//                    .sender()
//                    .queueName(queueName)
//                    .buildClient();
//            System.out.println("Client connected");
//
//            sender.sendMessage(new ServiceBusMessage("Hello, Azure Service Bus!"));
//            System.out.println("Message Sent");
//
//            sender.close();
//            logger.info("Message sent successfully.");
//            System.out.println("Client Closed");
//        } catch (NullPointerException e) {
//            System.out.println("NullPointerException failed");
//            System.out.println(e.getMessage());
//        } catch (ServiceBusException e) {
//            System.out.println("ServiceBusException failed");
//            System.out.println(e.getMessage());
//        } catch (IllegalStateException e) {
//            System.out.println("IllegalStateException failed");
//            System.out.println(e.getMessage());
//        } catch (Exception e) {
//            logger.error("Sending message failed: {}", e.getMessage());
//            System.out.println("Sending failed");
//            System.out.println(e.getMessage());
//        } finally {
//            return null;
//        }
//    }
}
