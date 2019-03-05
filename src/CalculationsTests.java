import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class CalculationsTests {

    //Scenario 2 tests
    private RunwayDesignator runwayDesignatorScenario2 = new RunwayDesignator("09R");
    private RunwayConfig initialRunwayScenario2 = new RunwayConfig(runwayDesignatorScenario2, 3660, 3660, 3660, 3353, 307);
    private Calculations calculationsScenario2 = new Calculations(initialRunwayScenario2);
    private Obstacle obstacleScenario2 = new Obstacle("testObstacle", 25);
    private RunwayConfig recalculatedRunwayScenario2 = calculationsScenario2.recalculateParams(obstacleScenario2, 2853, Calculations.Direction.TOWARDS);


    @Test
    public void scenario2TestNewTORA(){ assertThat(  2903, is(equalTo( recalculatedRunwayScenario2.getTORA()))  ); }
    @Test
    public void scenario2TestNewTODA(){ assertThat( 2903, is(equalTo(recalculatedRunwayScenario2.getTODA()))); }
    @Test
    public void scenario2TestNewASDA(){ assertThat( 2903, is(equalTo(recalculatedRunwayScenario2.getASDA()))); }
    @Test
    public void scenario2TestNewLDA(){ assertThat( 2393, is(equalTo(recalculatedRunwayScenario2.getLDA()))); }

}
