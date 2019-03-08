import java.util.HashMap;
import java.util.Map;

public class AirportConfig {
    private String name;
    private Map<String, RunwayPair> runways;

    public AirportConfig(String name){
        this.name = name;
        this.runways = new HashMap<>();
    }

    public void addRunwayPair(RunwayPair runway){
        this.runways.put(runway.getName(), runway);
    }

    public Map<String, RunwayPair> getRunways() {
        return runways;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Airport name : " + getName());
        sb.append("\n");
        for(String runwayPairName : this.runways.keySet()){
            sb.append(this.runways.get(runwayPairName).toString()).append("\n");
        }
        return sb.toString();
    }

    public static void main (String[] args){
        AirportConfig airportConfig = new AirportConfig("Heathrow");
        RunwayConfig runway09R = new RunwayConfig(new RunwayDesignator("09R"), 3660, 3660, 3660, 3353, 307);
        RunwayConfig runway27L = new RunwayConfig(new RunwayDesignator("27L"), 3660, 3660, 3660, 3660, 0);
        airportConfig.addRunwayPair(new RunwayPair(runway09R, runway27L));


        FileIO fileIO = new FileIO();
        fileIO.write(airportConfig, "heathrow.xml");
        AirportConfig ac2 = fileIO.read("heathrow2.xml");
        Map<String, AirportConfig> airportConfigs = fileIO.readRunwayDB("runways.csv");
        for (String name : airportConfigs.keySet()){
            System.out.println(name + " -> " + airportConfigs.get(name));
        }
    }
}
