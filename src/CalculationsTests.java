import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class CalculationsTests {
    // Reason for tests failing -  problem is in the example calc doc with 09L/27R, I changed the LDA to 3596 hoping that the displaced threshold is right

    //Scenario 1 tests
    private final RunwayDesignator runwayDesignatorScenario1 = new RunwayDesignator("09L");
    private final RunwayConfig initialRunwayScenario1 = new RunwayConfig(runwayDesignatorScenario1, 3902, 3902, 3902, 3596, 306);
    private final Calculations calculationsScenario1 = new Calculations(initialRunwayScenario1);
    private final Obstacle obstacleScenario1 = new Obstacle("testObstacle", 12);
    private final RunwayConfig recalculatedRunwayScenario1 = calculationsScenario1.recalculateParams(obstacleScenario1, -50, 0, "AWAY", 3902).getRecalculatedParams();

    private final RunwayDesignator runwayDesignatorScenario12 = new RunwayDesignator("27R");
    private final RunwayConfig initialRunwayScenario12 = new RunwayConfig(runwayDesignatorScenario12, 3884, 3962, 3884, 3884, 0);
    private final Calculations calculationsScenario12 = new Calculations(initialRunwayScenario12);
    private final RunwayConfig recalculatedRunwayScenario12 = calculationsScenario12.recalculateParams(obstacleScenario1, 3646, 0, "TOWARDS", 3902).getRecalculatedParams();

    //Scenario 2 tests
    private final RunwayDesignator runwayDesignatorScenario2 = new RunwayDesignator("09R");
    private final RunwayConfig initialRunwayScenario2 = new RunwayConfig(runwayDesignatorScenario2, 3660, 3660, 3660, 3353, 307);
    private final Calculations calculationsScenario2 = new Calculations(initialRunwayScenario2);
    private final Obstacle obstacleScenario2 = new Obstacle("testObstacle", 25);
    private final RunwayConfig recalculatedRunwayScenario2 = calculationsScenario2.recalculateParams(obstacleScenario2, 2853, 20, "TOWARDS", 3660).getRecalculatedParams();

    private final RunwayDesignator runwayDesignatorScenario22 = new RunwayDesignator("27L");
    private final RunwayConfig initialRunwayScenario22 = new RunwayConfig(runwayDesignatorScenario22, 3660, 3660, 3660, 3660, 0);
    private final Calculations calculationsScenario22 = new Calculations(initialRunwayScenario22);
    private final RunwayConfig recalculatedRunwayScenario22 = calculationsScenario22.recalculateParams(obstacleScenario2, 500, -20, "AWAY", 3660).getRecalculatedParams();

    //Scenario 3 tests
    private final RunwayDesignator runwayDesignatorScenario3 = new RunwayDesignator("09R");
    private final RunwayConfig initialRunwayScenario3 = new RunwayConfig(runwayDesignatorScenario3, 3660, 3660, 3660, 3353, 307);
    private final Calculations calculationsScenario3 = new Calculations(initialRunwayScenario3);
    private final Obstacle obstacleScenario3 = new Obstacle("testObstacle", 15);
    private final RunwayConfig recalculatedRunwayScenario3 = calculationsScenario3.recalculateParams(obstacleScenario3, 150, 60, "AWAY", 3660).getRecalculatedParams();

    private final RunwayDesignator runwayDesignatorScenario32 = new RunwayDesignator("27L");
    private final RunwayConfig initialRunwayScenario32 = new RunwayConfig(runwayDesignatorScenario32, 3660, 3660, 3660, 3660, 0);
    private final Calculations calculationsScenario32 = new Calculations(initialRunwayScenario32);
    private final RunwayConfig recalculatedRunwayScenario32 = calculationsScenario32.recalculateParams(obstacleScenario3, 3203, 60, "TOWARDS", 3660).getRecalculatedParams();

    //Scenario 4 tests
    private final RunwayDesignator runwayDesignatorScenario4 = new RunwayDesignator("09L");
    private final RunwayConfig initialRunwayScenario4 = new RunwayConfig(runwayDesignatorScenario4, 3902, 3902, 3902, 3596, 306);
    private final Calculations calculationsScenario4 = new Calculations(initialRunwayScenario4);
    private final Obstacle obstacleScenario4 = new Obstacle("testObstacle", 20);
    private final RunwayConfig recalculatedRunwayScenario4 = calculationsScenario4.recalculateParams(obstacleScenario4, 3546, 20, "TOWARDS", 3902).getRecalculatedParams();

    private final RunwayDesignator runwayDesignatorScenario42 = new RunwayDesignator("27R");
    private final RunwayConfig initialRunwayScenario42 = new RunwayConfig(runwayDesignatorScenario42, 3884, 3962, 3884, 3884, 0);
    private final Calculations calculationsScenario42 = new Calculations(initialRunwayScenario42);
    private final RunwayConfig recalculatedRunwayScenario42 = calculationsScenario42.recalculateParams(obstacleScenario4, 50, 20, "AWAY", 3902).getRecalculatedParams();


    @Test
    public void scenario1TestNewTORA() {
        assertThat(recalculatedRunwayScenario1.getTORA(), is(equalTo(3346)));
    }

    @Test
    public void scenario1TestNewTODA() {
        assertThat(recalculatedRunwayScenario1.getTODA(), is(equalTo(3346)));
    }

    @Test
    public void scenario1TestNewASDA() {
        assertThat(recalculatedRunwayScenario1.getASDA(), is(equalTo(3346)));
    }

    @Test
    public void scenario1TestNewLDA() {
        assertThat(recalculatedRunwayScenario1.getLDA(), is(equalTo(2986)));
    }

    @Test
    public void scenario12TestNewTORA() {
        assertThat(recalculatedRunwayScenario12.getTORA(), is(equalTo(2986)));
    }

    @Test
    public void scenario12TestNewTODA() {
        assertThat(recalculatedRunwayScenario12.getTODA(), is(equalTo(2986)));
    }

    @Test
    public void scenario12TestNewASDA() {
        assertThat(recalculatedRunwayScenario12.getASDA(), is(equalTo(2986)));
    }

    @Test
    public void scenario12TestNewLDA() {
        assertThat(recalculatedRunwayScenario12.getLDA(), is(equalTo(3346)));
    }


    @Test
    public void scenario2TestNewTORA() {
        assertThat(recalculatedRunwayScenario2.getTORA(), is(equalTo(1850)));
    }

    @Test
    public void scenario2TestNewTODA() {
        assertThat(recalculatedRunwayScenario2.getTODA(), is(equalTo(1850)));
    }

    @Test
    public void scenario2TestNewASDA() {
        assertThat(recalculatedRunwayScenario2.getASDA(), is(equalTo(1850)));
    }

    @Test
    public void scenario2TestNewLDA() {
        assertThat(recalculatedRunwayScenario2.getLDA(), is(equalTo(2553)));
    }

    @Test
    public void scenario22TestNewTORA() {
        assertThat(recalculatedRunwayScenario22.getTORA(), is(equalTo(2860)));
    }

    @Test
    public void scenario22TestNewTODA() {
        assertThat(recalculatedRunwayScenario22.getTODA(), is(equalTo(2860)));
    }

    @Test
    public void scenario22TestNewASDA() {
        assertThat(recalculatedRunwayScenario22.getASDA(), is(equalTo(2860)));
    }

    @Test
    public void scenario22TestNewLDA() {
        assertThat(recalculatedRunwayScenario22.getLDA(), is(equalTo(1850)));
    }

    @Test
    public void scenario3TestNewTORA() {
        assertThat(recalculatedRunwayScenario3.getTORA(), is(equalTo(2903)));
    }

    @Test
    public void scenario3TestNewTODA() {
        assertThat(recalculatedRunwayScenario3.getTODA(), is(equalTo(2903)));
    }

    @Test
    public void scenario3TestNewASDA() {
        assertThat(recalculatedRunwayScenario3.getASDA(), is(equalTo(2903)));
    }

    @Test
    public void scenario3TestNewLDA() {
        assertThat(recalculatedRunwayScenario3.getLDA(), is(equalTo(2393)));
    }

    @Test
    public void scenario32TestNewTORA() {
        assertThat(recalculatedRunwayScenario32.getTORA(), is(equalTo(2393)));
    }

    @Test
    public void scenario32TestNewTODA() {
        assertThat(recalculatedRunwayScenario32.getTODA(), is(equalTo(2393)));
    }

    @Test
    public void scenario32TestNewASDA() {
        assertThat(recalculatedRunwayScenario32.getASDA(), is(equalTo(2393)));
    }

    @Test
    public void scenario32TestNewLDA() {
        assertThat(recalculatedRunwayScenario32.getLDA(), is(equalTo(2903)));
    }

    @Test
    public void scenario4TestNewTORA() {
        assertThat(recalculatedRunwayScenario4.getTORA(), is(equalTo(2792)));
    }

    @Test
    public void scenario4TestNewTODA() {
        assertThat(recalculatedRunwayScenario4.getTODA(), is(equalTo(2792)));
    }

    @Test
    public void scenario4TestNewASDA() {
        assertThat(recalculatedRunwayScenario4.getASDA(), is(equalTo(2792)));
    }

    @Test
    public void scenario4TestNewLDA() {
        assertThat(recalculatedRunwayScenario4.getLDA(), is(equalTo(3246)));
    }

    @Test
    public void scenario42TestNewTORA() {
        assertThat(recalculatedRunwayScenario42.getTORA(), is(equalTo(3534)));
    }

    @Test
    public void scenario42TestNewTODA() {
        assertThat(recalculatedRunwayScenario42.getTODA(), is(equalTo(3612)));
    }

    @Test
    public void scenario42TestNewASDA() {
        assertThat(recalculatedRunwayScenario42.getASDA(), is(equalTo(3534)));
    }

    @Test
    public void scenario42TestNewLDA() {
        assertThat(recalculatedRunwayScenario42.getLDA(), is(equalTo(2774)));
    }


}
