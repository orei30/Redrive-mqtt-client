import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DrivingRecord {
    private String uid;
    private long time;
    private String GPStime;
    private String latitude;
    private String longitude;
    private String Altitude;
    private String satellitesNum;
    private double speed;
    private double rpm;
    private double throttlePosition;
    private double engineLoad;
    private double maf;
    private double acceX;
    private double acceY;
    private double acceZ;
    private double gyroX;
    private double gyroY;
    private double gyroZ;

    public DrivingRecord(String[] record) {
        this.uid = record[1];
        Date now = new Date();
        this.time = now.getTime();
        this.GPStime = record[2];
        this.latitude = record[3];
        this.longitude = record[4];
        this.Altitude = record[5];
        this.satellitesNum = record[6];
        this.speed = Double.parseDouble(record[7]);
        this.rpm = Double.parseDouble(record[8]);
        this.throttlePosition = Double.parseDouble(record[9]);
        this.engineLoad = Double.parseDouble(record[10]);
        this.maf = Double.parseDouble(record[11]);
        this.acceX = Double.parseDouble(record[12]);
        this.acceY = Double.parseDouble(record[13]);
        this.acceZ = Double.parseDouble(record[14]);
        this.gyroX = Double.parseDouble(record[15]);
        this.gyroY = Double.parseDouble(record[16]);
        this.gyroZ = Double.parseDouble(record[17]);
    }

    public String getUid() {
        return uid;
    }

    public long getTime() {
        return time;
    }

    public Map<String, Object> getGPSData() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("GPStime", GPStime);
        dataMap.put("latitude", latitude);
        dataMap.put("longitude", longitude);
        dataMap.put("Altitude", Altitude);
        dataMap.put("satellitesNum", satellitesNum);
        return dataMap;
    }

    public Map<String, Object> getCanData() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("speed", speed);
        dataMap.put("rpm", rpm);
        dataMap.put("throttlePosition", throttlePosition);
        dataMap.put("engineLoad", engineLoad);
        dataMap.put("maf", maf);
        return dataMap;
    }

    public Map<String, Object> getGyroData() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("acceX", acceX);
        dataMap.put("acceY", acceY);
        dataMap.put("acceZ", acceZ);
        dataMap.put("gyroX", gyroX);
        dataMap.put("gyroY", gyroY);
        dataMap.put("gyroZ",gyroZ );
        return dataMap;
    }

    @Override
    public String toString() {
        return "uid='" + uid + '\'' +
                ", time=" + time +
                ", GPStime=" + GPStime +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", Altitude='" + Altitude + '\'' +
                ", satellitesNum='" + satellitesNum + '\'' +
                ", speed=" + speed +
                ", rpm=" + rpm +
                ", throttlePosition=" + throttlePosition +
                ", engineLoad=" + engineLoad +
                ", maf=" + maf +
                ", acceX=" + acceX +
                ", acceY=" + acceY +
                ", acceZ=" + acceZ +
                ", gyroX=" + gyroX +
                ", gyroY=" + gyroY +
                ", gyroZ=" + gyroZ +
                '}';
    }
}
