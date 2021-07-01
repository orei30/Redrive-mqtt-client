import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;
import java.util.Date;

public class ProcessData implements Runnable {
    public final static String RECORD = "RECORD";
    public final static String START_DRIVE = "START_DRIVE";
    public final static String END_DRIVE = "END_DRIVE";
    private RecordsStringQueue recordsStringQueue;
    private MqttClient client;
    private AmazonDynamoDB DBClient;
    private DynamoDB dynamoDB;
    private Table drivesTable;

    public ProcessData(RecordsStringQueue recordsStringQueue, MqttClient client) {
        this.recordsStringQueue = recordsStringQueue;
        this.client = client;
        this.DBClient = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
        dynamoDB = new DynamoDB(DBClient);
        drivesTable = dynamoDB.getTable("drives");
    }

    @Override
    public synchronized void run() {
        while (true) {
            try {
                // Get the first element on the queue.
                String recordString = recordsStringQueue.take();
                // Split it by "@".
                String[] recordList = recordString.split("@");
                if (recordList[0].equals(RECORD)) { // If it's record add it to records table.
                    // Convert the String array to DrivingRecord.
                    DrivingRecord record = new DrivingRecord(recordList);
                    System.out.println("[" + LocalDateTime.now() + "]: " + record.getUid());
                    System.out.println(record);
                    // add the record to records table.
                    createItem(record);
                } else if (recordList[0].equals(START_DRIVE)) { // if it's a new drive create a new row on the drives table.
                    newDrive(recordList[1]); // create a row for a new drive.
                } else if (recordList[0].equals(END_DRIVE)) { // if it's the end of a drive update the row of the drive.
                    endDrive(recordList[1], recordList[2]); // update the drive row by the start time.
                } else {
                    System.out.println("Unexpected String");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void newDrive(String userName) {
        System.out.println("New Drive");
        // connect and get the drives table.
        Date now = new Date();
        long time = now.getTime();
        try {
            // create new row for that drive.
            System.out.println("Adding a new drive...");
            PutItemOutcome outcome = drivesTable
                    .putItem(new Item().withPrimaryKey("startTime", time, "userName", userName));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e) {
            System.err.println("Unable to add item: " + time + " " + userName);
            System.err.println(e.getMessage());
        }
        // create message with the user name and the start time.
        MqttMessage message = new MqttMessage((userName + "@" + time).getBytes());
        message.setQos(2);
        try {
            // publish the message to redrive/finish-drive.
            client.publish("redrive/finish-drive", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void endDrive(String startTime, String userName) {
        System.out.println("End Drive");
        // connect and get the drives table.
        Date now = new Date();
        long time = now.getTime();
        // set the row to update.
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("startTime", Long.parseLong(startTime), "userName", userName)
                .withUpdateExpression("set endTime = :e")
                .withValueMap(new ValueMap().withNumber(":e", time))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            System.out.println("Updating the item...");
            // update the row.
            UpdateItemOutcome outcome = drivesTable.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
        } catch (Exception e) {
            System.err.println("Unable to update item: " + startTime + " " + userName);
            System.err.println(e.getMessage());
        }
        try {
            //TODO: update file path to the python script we want to run on the end of a drive.
            ProcessBuilder pb = new ProcessBuilder("python","filepath", userName, startTime, "" + startTime);
            pb.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void createItem(DrivingRecord record) {
        System.out.println("create Item");
        // connect and get the user table.
        Table table = dynamoDB.getTable(record.getUid());
        try {
            System.out.println("Adding a new item...");
            // add new row for the driving record.
            PutItemOutcome outcome = table
                    .putItem(new Item()
                            .withPrimaryKey("time", record.getTime())
                            .withMap("GPSData", record.getGPSData())
                            .withMap("canData", record.getCanData())
                            .withMap("gyroData", record.getGyroData()));
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e) {
            System.err.println("Unable to add item: " + record.getUid() + " " + record.getTime());
            System.err.println(e.getMessage());
        }
    }
}
