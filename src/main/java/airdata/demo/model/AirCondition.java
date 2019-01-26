package airdata.demo.model;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;

@Component
public class AirCondition {
    private ArrayList<String []> data;





    public ArrayList<String []> getData() {
        return data;
    }

    public void setData(ArrayList<String []> data) {
        this.data = data;
    }

    public ArrayList<String> readCSV(){
        ArrayList<String> arrayList1 = new ArrayList<>();
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(new File("AirQualityUCI.csv")));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String stemp;
            stemp = bufferedReader.readLine();
            while((stemp =bufferedReader.readLine())!=null){
                arrayList1.add(stemp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList1;
    }
    public ArrayList<String []> transfer(ArrayList<String> arrayList){
        ArrayList<String []> arrayLists = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            if (arrayList.get(i).equals(";;;;;;;;;;;;;;;;")){
                continue;
            }
            else {
                String tmp = arrayList.get(i);
//              System.out.println(tmp);
                arrayLists.add(tmp.split(";"));
            }

        }
//        for (int j=0;j<5;j++){
//            for (int k = 0;k<arrayLists.get(j).length;k++){
//                System.out.print(arrayLists.get(j)[k]);
//                System.out.print(" ");
//            }
//            System.out.println();
//        }
        return arrayLists;
    }
    public Boolean insertData(ArrayList<String []> arrayLists){
        InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
        Pong response = influxDB.ping();
        if (response.getVersion().equalsIgnoreCase("unknown")) {
            System.out.println("Error pinging server.");
            return Boolean.FALSE;
        }
        String dbname = "AirData";
        influxDB.deleteDatabase(dbname);
        influxDB.createDatabase(dbname);
        BatchPoints batchPoints = BatchPoints.database(dbname).build();
        for (int i=0;i<arrayLists.size();i++){
            Point point = Point.measurement("air")
                    .addField("Date",arrayLists.get(i)[0])
                    .addField("Time",arrayLists.get(i)[1])
                    .addField("CO(GT)",arrayLists.get(i)[2])
                    .addField("PT08.S1(CO)",arrayLists.get(i)[3])
                    .addField("NMHC(GT)",arrayLists.get(i)[4])
                    .addField("C6H6(GT)",arrayLists.get(i)[5])
                    .addField("PT08.S2(NMHC)",arrayLists.get(i)[6])
                    .addField("NOx(GT)",arrayLists.get(i)[7])
                    .addField("PT08.S3(NOx)",arrayLists.get(i)[8])
                    .addField("NO2(GT)",arrayLists.get(i)[9])
                    .addField("PT08.S5(O3)",arrayLists.get(i)[10])
                    .addField("PT08.S5(O3)",arrayLists.get(i)[11])
                    .addField("T",arrayLists.get(i)[12])
                    .addField("RH",arrayLists.get(i)[13])
                    .addField("AH",arrayLists.get(i)[14])
                    .build();
            batchPoints.point(point);
            influxDB.write(batchPoints);
        }
        return Boolean.TRUE;
    }
    public QueryResult query(String commend){
        QueryResult result;
        InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
        Pong response = influxDB.ping();
        if (response.getVersion().equalsIgnoreCase("unknown")) {
            System.out.println("Error pinging server.");
            return null;
        }
        Query query = new Query(commend, "AirData");
        result = influxDB.query(query);
        return result;
    }
}
