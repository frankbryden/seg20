public class Calculations {
    private RunwayConfig originalConfig;
    private final int RESA = 240;
    private final int STRIP = 60;
    public enum Direction {TOWARDS, AWAY};

    public Calculations(RunwayConfig runwayConfig){
        this.originalConfig = runwayConfig;
    }

    public RunwayConfig recalculateParams(Obstacle obstacle, int distanceFromThreshold, Direction direction){
        //We need to determine what side of the runway the obstacle is lying before performing any calculations.
        int recalculatedTORA;
        int recalculatedTODA;
        int recalculatedASDA;
        int recalculatedLDA;

        if (direction == Direction.TOWARDS){
            // In the case of taking off towards from the obstacle / landing over it
            recalculatedTORA = originalConfig.getTORA() - 300 - distanceFromThreshold;
            recalculatedTODA = recalculatedTORA + originalConfig.getClearway();
            recalculatedASDA = recalculatedTORA + originalConfig.getStopway();
            recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - (obstacle.getHeight() * 50) - STRIP;
        } else {
            // In the case of taking off and landing towards the obstacle
            recalculatedTORA = originalConfig.getTORA() - distanceFromThreshold - (obstacle.getHeight() * 50) - STRIP;
            recalculatedTODA = recalculatedTORA;
            recalculatedASDA = recalculatedTORA;
            recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - RESA - STRIP;
        }


        return new RunwayConfig(originalConfig.getRunwayDesignator(), recalculatedTORA, recalculatedTODA, recalculatedASDA, recalculatedLDA);
    }
}
