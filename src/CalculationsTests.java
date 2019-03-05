import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

public class CalculationsTests {

    private RunwayDesignator runwayDesignator = new RunwayDesignator("09R");
    private RunwayConfig initialRunway = new RunwayConfig(runwayDesignator, 3660, 3660, 3660, 3353);
    private Calculations calculations = new Calculations(initialRunway);
    private Obstacle obstacle = new Obstacle("testObstacle", 12);
    private RunwayConfig recalculatedRunway = calculations.recalculateParams(obstacle, 50, Calculations.Direction.AWAY);


    @Test
    public void testNewTORA(){ assertThat(  2903, is(equalTo( recalculatedRunway.getTORA()))  ); }

    @Test
    public void testNewTODA(){ assertThat( 2903, is(equalTo(recalculatedRunway.getTODA()))); }

    @Test
    public void testNewASDA(){ assertThat( 2903, is(equalTo(recalculatedRunway.getASDA()))); }

    @Test
    public void testNewLDA(){ assertThat( 2393, is(equalTo(recalculatedRunway.getLDA()))); }

}
