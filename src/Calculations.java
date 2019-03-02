public class Calculations {
    private RunwayConfig originalConfig;

    public Calculations(RunwayConfig runwayConfig){
        this.originalConfig = runwayConfig;
    }

    public RunwayConfig recalculateParams(Obstacle obstacle, int distanceFromThreshold, int distanceFromCenterLine){
        //We need to determine what side of the runway the obstacle is lying before performing any calculations.
        int recalculatedTORA;
        int recalculatedTODA;
        int recalculatedASDA;
        int recalculatedLDA;
        // In the case of taking off away from the obstacle
        recalculatedTORA = originalConfig.getTORA() - 300 - distanceFromThreshold;
        recalculatedTODA = originalConfig.getTODA() - 300 - distanceFromThreshold;
        recalculatedASDA = originalConfig.getASDA() - 300 - distanceFromThreshold;
        recalculatedLDA = originalConfig.getLDA() - distanceFromThreshold - (obstacle.getHeight() * 50) - 60;

        return new RunwayConfig(originalConfig.getRunwayDesignator(), recalculatedTORA, recalculatedTODA, recalculatedASDA, recalculatedLDA);
    }
}
