import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AirportDatabase {
    private final Map<AirportCode, String> codeToNameMap;

    public AirportDatabase(){
        this.codeToNameMap = new FileIO().readAirportDB("out.csv");
        System.out.println("Airport Database instance ready with " + this.codeToNameMap.size() + " airports");
    }

    public List<String> getEntries(String prefix){
        return codeToNameMap.keySet().stream().filter(airportCode -> airportCode.code.startsWith(prefix.toUpperCase())).map(airportCode -> codeToNameMap.get(airportCode)).collect(Collectors.toList());
    }

    public String getEntryReversed(String airportName){
        return codeToNameMap.keySet().stream().filter(airportCode -> codeToNameMap.get(airportCode).equals(airportName)).map(airportCode -> airportCode.code).collect(Collectors.toList()).get(0);
    }
}
