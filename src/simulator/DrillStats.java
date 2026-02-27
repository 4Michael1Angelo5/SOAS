package simulator;

import types.Drill;

import java.util.Comparator;

public record DrillStats(
        Drill drill,
        DrillReport report,
        int deltaPosition ,
        int deltaWaitTime) implements Comparable<DrillStats> {

    @Override
    public int compareTo(DrillStats o) {
        return o.deltaPosition - this.deltaPosition;
    }

    @Override
    public String toString() {
        return "{\n" +
                "   drill: {" + "\n" +
                "       drill_id:" + drill.drill_id() + ",\n" +
                "       name:" + drill.name() + ",\n" +
                "       urgency:" + drill.urgency() + ",\n" +
                "       duration_min:" + drill.duration_min() + ",\n" +
                "       fatigue_cost:" + drill.fatigue_cost() + ",\n" +
                "       install_by_day:" + drill.install_by_day() + "\n" +
                "       }\n" +
                "   deltas: {\n"+
                "          deltaPosition: " + deltaPosition + "\n" +
                "          deltaWaitTime: " + deltaWaitTime +"\n" +
                "          }\n" +
                "}";
    }
}
