public class App {
		
	public static void main(String[] args) {
		RecordsStringQueue recordsStringQueue = new RecordsStringQueue();
		MQTTClient mqttClient = new MQTTClient(recordsStringQueue);
		mqttClient.connect();
		Thread mqttClientThread = new Thread(mqttClient);
		mqttClientThread.start();
    }
}
