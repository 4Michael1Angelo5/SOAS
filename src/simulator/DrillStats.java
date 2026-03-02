package simulator;
import types.Drill;

/**
 * Mutable container for storing statistics associated with a {@link Drill}
 * as it moves through a scheduling simulation. Metrics include
 * <ul>
 *     <li><b>deltaWaitTime</b> — the difference in wait time (in minutes) between
 *         processing via a standard queue vs. a priority queue. A positive value
 *         indicates the drill waited less in the priority queue (a "winner");
 *         a negative value indicates it waited longer (a "loser").</li>
 *     <li><b>deltaPosition</b> — the difference in processing order between
 *         the standard queue and the priority queue. Positive means the drill
 *         was processed earlier in the priority queue; negative means later.</li>
 *     <li><b>zScoreTime</b> — the Z-score of this drill's {@code deltaWaitTime}
 *         relative to the population of all drills, indicating how statistically
 *         significant its wait time change was.</li>
 *     <li><b>zScorePos</b> — the Z-score of this drill's {@code deltaPosition}
 *         relative to the population of all drills, indicating how statistically
 *         significant its position change was.</li>
 * </ul>
 *
 * <p>Core data ({@link Drill} and {@link DrillReport}) is immutable and set at
 * construction time. Delta and Z-score metrics are computed after simulation
 * and set via their respective setters.</p>
 *
 * <p>Natural ordering via {@link Comparable} sorts by {@code deltaPosition}
 * in descending order, placing the greatest winners first. This allows
 * {@code DrillStats} to be inserted directly into a {@link util.BinaryHeapPQ}
 * to identify drills most impacted by priority scheduling.</p>
 *
 * @see DrillReport
 * @see types.Drill
 * @see simulator.DrillSimulator
 */
public class DrillStats implements Comparable<DrillStats> {

    // Immutable core data
    private final Drill drill;
    private final  DrillReport report;

    // Mutable metrics (added later)
    private double zScoreTime;
    private double zScorePos;
    private int deltaPosition;
    private int deltaWaitTime;

    public DrillStats(Drill theDrill, DrillReport theReport) {
        this.drill = theDrill;
        this.report = theReport;
    }

    // --- Core Data Getters (No Setters because they are final) ---

    public Drill getDrill() {
        return drill;
    }

    public DrillReport getReport() {
        return report;
    }


    // --- Mutable Field Getters and Setters ---

    // delta wait times
    public int getDeltaWaitTime() {
        return deltaWaitTime;
    }

    public void setDeltaWaitTime(int deltaWaitTime) {
        this.deltaWaitTime = deltaWaitTime;
    }

    // delta positon
    public int getDeltaPosition() {
        return deltaPosition;
    }

    public void setDeltaPosition(int deltaPosition) {
        this.deltaPosition = deltaPosition;
    }

    public double getZScoreTime() {
        return zScoreTime;
    }

    public void setZScoreTime(double zScore) {
        this.zScoreTime = zScore;
    }

    public double getZScorePos() {
        return this.zScorePos;
    }

    public void setZScorePos(double zScorePos) {
        this.zScorePos = zScorePos;
    }

    @Override
    public String toString() {
        return "{\n" +
                "   drill: {" + "\n" +
                "           drill_id:" + drill.drill_id() + ",\n" +
                "           name:" + drill.name() + ",\n" +
                "           urgency:" + drill.urgency() + ",\n" +
                "           duration_min:" + drill.duration_min() + ",\n" +
                "           fatigue_cost:" + drill.fatigue_cost() + ",\n" +
                "           install_by_day:" + drill.install_by_day() + "\n" +
                "           },\n" +
                "   report: {\n" +
                "           wait time: " + report.waitTime() + "\n" +
                "           order processed: " + report.orderProcessed() + "\n" +
                "           },\n"+
                "   deltas: {\n"+
                "          deltaPosition: " + deltaPosition + "\n" +
                "          deltaWaitTime: " + deltaWaitTime +"\n" +
                "          },\n" +
                "  z-score: {\n" +
                "          time: " + zScoreTime + "\n" +
                "          position: " + zScorePos + "\n" +
                "          }" +
                "}";
    }

    @Override
    public int compareTo(DrillStats o) {
        return o.deltaPosition - this.deltaPosition;
    }
}