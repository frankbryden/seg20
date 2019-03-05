import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class CalculationsTests {

    //Scenario 1 tests -modify input
    private RunwayDesignator runwayDesignatorScenario1 = new RunwayDesignator("09R");
    private RunwayConfig initialRunwayScenario1 = new RunwayConfig(runwayDesignatorScenario1, 3660, 3660, 3660, 3353, 307);
    private Calculations calculationsScenario1 = new Calculations(initialRunwayScenario1);
    private Obstacle obstacleScenario1 = new Obstacle("testObstacle", 25);
    //private RunwayConfig recalculatedRunwayScenario1 = calculationsScenario1.recalculateParams(obstacleScenario1, 2853, Calculations.Direction.TOWARDS);

    //Scenario 2 tests
    private RunwayDesignator runwayDesignatorScenario2 = new RunwayDesignator("09R");
    private RunwayConfig initialRunwayScenario2 = new RunwayConfig(runwayDesignatorScenario2, 3660, 3660, 3660, 3353, 307);
    private Calculations calculationsScenario2 = new Calculations(initialRunwayScenario2);
    private Obstacle obstacleScenario2 = new Obstacle("testObstacle", 25);
    //private RunwayConfig recalculatedRunwayScenario2 = calculationsScenario2.recalculateParams(obstacleScenario2, 2853, Calculations.Direction.TOWARDS);

    private RunwayDesignator runwayDesignatorScenario22 = new RunwayDesignator("27L");
    private RunwayConfig initialRunwayScenario22 = new RunwayConfig(runwayDesignatorScenario22, 3660, 3660, 3660, 3660, 0);
    private Calculations calculationsScenario22 = new Calculations(initialRunwayScenario22);
    private Obstacle obstacleScenario22 = new Obstacle("testObstacle", 25);
    private RunwayConfig recalculatedRunwayScenario22 = calculationsScenario22.recalculateParams(obstacleScenario22, 2853, Calculations.Direction.AWAY);

    //Scenario 3 tests -modify input
    private RunwayDesignator runwayDesignatorScenario3 = new RunwayDesignator("09R");
    private RunwayConfig initialRunwayScenario3 = new RunwayConfig(runwayDesignatorScenario3, 3660, 3660, 3660, 3353, 307);
    private Calculations calculationsScenario3 = new Calculations(initialRunwayScenario3);
    private Obstacle obstacleScenario3 = new Obstacle("testObstacle", 25);
    //private RunwayConfig recalculatedRunwayScenario3 = calculationsScenario3.recalculateParams(obstacleScenario3, 2853, Calculations.Direction.TOWARDS);

    //Scenario 4 tests -modify input
    private RunwayDesignator runwayDesignatorScenario4 = new RunwayDesignator("09R");
    private RunwayConfig initialRunwayScenario4 = new RunwayConfig(runwayDesignatorScenario4, 3660, 3660, 3660, 3353, 307);
    private Calculations calculationsScenario4 = new Calculations(initialRunwayScenario4);
    private Obstacle obstacleScenario4 = new Obstacle("testObstacle", 25);
    //private RunwayConfig recalculatedRunwayScenario4 = calculationsScenario4.recalculateParams(obstacleScenario4, 2853, Calculations.Direction.TOWARDS);


    /*@Test
    public void scenario2TestNewTORA(){ assertThat(  1850, is(equalTo( recalculatedRunwayScenario2.getTORA()))  ); }
    @Test
    public void scenario2TestNewTODA(){ assertThat( 1850, is(equalTo(recalculatedRunwayScenario2.getTODA()))); }
    @Test
    public void scenario2TestNewASDA(){ assertThat( 1850, is(equalTo(recalculatedRunwayScenario2.getASDA()))); }
    @Test
    public void scenario2TestNewLDA(){ assertThat( 2553, is(equalTo(recalculatedRunwayScenario2.getLDA()))); }*/

    @Test
    public void scenario22TestNewTORA(){ assertThat(  2860, is(equalTo( recalculatedRunwayScenario22.getTORA()))  ); }
    @Test
    public void scenario22TestNewTODA(){ assertThat( 2860, is(equalTo(recalculatedRunwayScenario22.getTODA()))); }
    @Test
    public void scenario22TestNewASDA(){ assertThat( 2860, is(equalTo(recalculatedRunwayScenario22.getASDA()))); }
    @Test
    public void scenario22TestNewLDA(){ assertThat( 1850,  is(equalTo(recalculatedRunwayScenario22.getLDA()))); }


}
