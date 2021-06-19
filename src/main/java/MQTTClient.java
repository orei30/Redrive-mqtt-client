import org.eclipse.paho.client.mqttv3.*;

public class MQTTClient implements Runnable {
	private MqttClient client;
	private RecordsStringQueue recordsStringQueue;

	public MQTTClient(RecordsStringQueue recordsStringQueue) {
		this.recordsStringQueue = recordsStringQueue;
	}

	public void connect() {
		// client, user and device details
		final String serverUrl = "tcp://broker.hivemq.com:1883"; /* ssl://mqtt.cumulocity.com:8883 for a secure connection */
		final String clientId = "admin";
		final String username = "admin";
		final String password = "password";

		final MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(username);
		options.setPassword(password.toCharArray());
		try {
			// Connect to HiveMQ MQTT broker
			client = new MqttClient(serverUrl, clientId, null);
			client.connect(options);
			System.out.println("Client is connected");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			System.out.println("Start listening");
			// Create a thread for processing data.
			ProcessData processData = new ProcessData(recordsStringQueue, client);
			Thread processDataThread = new Thread(processData);
			processDataThread.start();
			// Subscribe to the topic that receives the data
			client.subscribe("redrive/records", (topic, message) -> {
				// Convert the data from the byte array to String.
				final String payload = new String(message.getPayload());
				System.out.println("Data arrived: \n" + payload);
				// Add the data to a thread-safe queue.
				recordsStringQueue.add(payload);
			});
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

}
