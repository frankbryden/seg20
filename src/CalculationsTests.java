import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class CalculationsTests {

    //Scenario 1 tests
    private RunwayDesignator runwayDesignatorScenario1 = new RunwayDesignator("09L");
    private RunwayConfig initialRunwayScenario1 = new RunwayConfig(runwayDesignatorScenario1, 3902, 3902, 3902, 3595, 306);
    private Calculations calculationsScenario1 = new Calculations(initialRunwayScenario1);
    private Obstacle obstacleScenario1 = new Obstacle("testObstacle", 12);
    private RunwayConfig recalculatedRunwayScenario1 = calculationsScenario1.recalculateParams(obstacleScenario1, -50, Calculations.Direction.AWAY);

    private RunwayDesignator runwayDesignatorScenario12 = new RunwayDesignator("27R");
    private RunwayConfig initialRunwayScenario12 = new RunwayConfig(runwayDesignatorScenario12, 3884, 3962, 3884, 3884, 0);
    private Calculations calculationsScenario12 = new Calculations(initialRunwayScenario12);
    private Obstacle obstacleScenario12 = new Obstacle("testObstacle", 12);
    private RunwayConfig recalculatedRunwayScenario12 = calculationsScenario12.recalculateParams(obstacleScenario12, 3646, Calculations.Direction.TOWARDS);

    //Scenario 2 tests
    private RunwayDesignator runwayDesignatorScenario2 = new RunwayDesignator("09R");
    private RunwayConfig initialRunwayScenario2 = new RunwayConfig(runwayDesignatorScenario2, 3660, 3660, 3660, 3353, 307);
    private Calculations calculationsScenario2 = new Calculations(initialRunwayScenario2);
    private Obstacle obstacleScenario2 = new Obstacle("testObstacle", 25);
    private RunwayConfig recalculatedRunwayScenario2 = calculationsScenario2.recalculateParams(obstacleScenario2, 2853, Calculations.Direction.TOWARDS);

    private RunwayDesignator runwayDesignatorScenario22 = new RunwayDesignator("27L");
    private RunwayConfig initialRunwayScenario22 = new RunwayConfig(runwayDesignatorScenario22, 3660, 3660, 3660, 3660, 0);
    private Calculations calculationsScenario22 = new Calculations(initialRunwayScenario22);
    private Obstacle obstacleScenario22 = new Obstacle("testObstacle", 25);
    private RunwayConfig recalculatedRunwayScenario22 = calculationsScenario22.recalculateParams(obstacleScenario22, 500, Calculations.Direction.AWAY);

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


    @Test
    public void scenario1TestNewTORA(){ assertThat(  recalculatedRunwayScenario1.getTORA(), is(equalTo(3346 ))); }
    @Test
    public void scenario1TestNewTODA(){ assertThat(recalculatedRunwayScenario1.getTODA(), is(equalTo(3346))); }
    @Test
    public void scenario1TestNewASDA(){ assertThat( recalculatedRunwayScenario1.getASDA(), is(equalTo(3346))); }
    @Test
    public void scenario1TestNewLDA(){ assertThat( recalculatedRunwayScenario1.getLDA(), is(equalTo(2985))); }

    @Test
    public void scenario12TestNewTORA(){ assertThat(  recalculatedRunwayScenario12.getTORA(), is(equalTo(2986 ))); }
    @Test
    public void scenario12TestNewTODA(){ assertThat(recalculatedRunwayScenario12.getTODA(), is(equalTo(2986))); }
    @Test
    public void scenario12TestNewASDA(){ assertThat( recalculatedRunwayScenario12.getASDA(), is(equalTo(2986))); }
    @Test
    public void scenario12TestNewLDA(){ assertThat( recalculatedRunwayScenario12.getLDA(), is(equalTo(3346))); }



    @Test
    public void scenario2TestNewTORA(){ assertThat( recalculatedRunwayScenario2.getTORA()  , is(equalTo( 1850))); }
    @Test
    public void scenario2TestNewTODA(){ assertThat(recalculatedRunwayScenario2.getTODA() , is(equalTo(1850))); }
    @Test
    public void scenario2TestNewASDA(){ assertThat( recalculatedRunwayScenario2.getASDA(), is(equalTo(1850))); }
    @Test
    public void scenario2TestNewLDA(){ assertThat( recalculatedRunwayScenario2.getLDA(), is(equalTo(2553))); }

    @Test
    public void scenario22TestNewTORA(){ assertThat(  recalculatedRunwayScenario22.getTORA(), is(equalTo( 2860))); }
    @Test
    public void scenario22TestNewTODA(){ assertThat( recalculatedRunwayScenario22.getTODA(), is(equalTo(2860))); }
    @Test
    public void scenario22TestNewASDA(){ assertThat( recalculatedRunwayScenario22.getASDA(), is(equalTo(2860))); }
    @Test
    public void scenario22TestNewLDA(){ assertThat( recalculatedRunwayScenario22.getLDA(),  is(equalTo(1850))); }


}
