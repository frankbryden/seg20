import java.util.HashMap;
import java.util.Map;

public class AirportConfig {
    private String name;
    private Map<RunwayDesignator, RunwayConfig> runwayConfigs;

    public AirportConfig(String name){
        this.name = name;
        this.runwayConfigs = new HashMap<>();
    }

    public void addRunway(RunwayConfig runwayConfig){
        this.runwayConfigs.put(runwayConfig.getRunwayDesignator(), runwayConfig);
    }

    public Map<RunwayDesignator, RunwayConfig> getRunwayConfigs() {
        return runwayConfigs;
    }

    public static void main (String[] args){
        AirportConfig airportConfig = new AirportConfig("Heathrow");
        RunwayConfig runway09R = new RunwayConfig(new RunwayDesignator("09R"), 3660, 3660, 3660, 3353);
        RunwayConfig runway27L = new RunwayConfig(new RunwayDesignator("27L"), 3660, 3660, 3660, 3660);
        airportConfig.addRunway(runway09R);
        airportConfig.addRunway(runway27L);

        FileIO fileIO = new FileIO();
        fileIO.write(airportConfig, "heathrow.xml");
    }
}
