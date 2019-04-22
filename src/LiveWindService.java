import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiveWindService extends Service<Map<String, Double>> {
    private double latitude, longitude;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    protected Task<Map<String, Double>> createTask() {
        return new Task<Map<String, Double>>() {
            @Override
            protected Map<String, Double> call() throws Exception {
                System.out.println("we're just gonna get some data here");
                String apiKey = "473ade203bfbbf2d4346749e61a37a95";
                String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;
                try {
                    URL url = new URL(urlString);
                    URLConnection urlConnection = url.openConnection();
                    InputStreamReader is = new InputStreamReader(urlConnection.getInputStream());
                    StringBuilder data = new StringBuilder();
                    while (is.ready()){
                        data.append((char) is.read());
                    }
                    System.out.println(data.toString());
                    Pattern p = Pattern.compile("\"wind\":\\{\"speed\":([0-9]+\\.[0-9]*),\"deg\":([0-9]+).*?\\}");
                    System.out.println(p.toString());
                    Matcher m = p.matcher(data.toString());
                    m.find();
                    System.out.println("Speed extracted from response : " + m.group(1));
                    System.out.println("Angle extracted from response : " + m.group(2));

                    double speed = Double.valueOf(m.group(1));
                    int angleDeg = Integer.valueOf(m.group(2));
                    //Convert angle to radians
                    double angleRad = angleDeg * Math.PI/180;
                    //Add PI/2 as the 0 in the meteorological is north, whereas it is east in the trigonometry world
                    angleRad += Math.PI/2;

                    Map<String, Double> result = new HashMap<>();
                    result.put("speed", speed);
                    result.put("direction", angleRad);
                    return result;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

}
