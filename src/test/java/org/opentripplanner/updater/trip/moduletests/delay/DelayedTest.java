package org.opentripplanner.updater.trip.moduletests.delay;

import static com.google.transit.realtime.GtfsRealtime.TripDescriptor.ScheduleRelationship.SCHEDULED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.opentripplanner.test.support.UpdateResultAssertions.assertSuccess;
import static org.opentripplanner.updater.trip.RealtimeTestEnvironment.SERVICE_DATE;

import org.junit.jupiter.api.Test;
import org.opentripplanner.model.Timetable;
import org.opentripplanner.model.TimetableSnapshot;
import org.opentripplanner.transit.model.network.TripPattern;
import org.opentripplanner.transit.model.timetable.RealTimeState;
import org.opentripplanner.transit.model.timetable.TripTimes;
import org.opentripplanner.updater.trip.RealtimeTestEnvironment;
import org.opentripplanner.updater.trip.TripUpdateBuilder;

/**
 * Delays should be applied to the first trip but should leave the second trip untouched.
 */
class DelayedTest {

  private static final int DELAY = 1;
  private static final int STOP_SEQUENCE = 1;

  @Test
  void singleStopDelay() {
    var env = RealtimeTestEnvironment.gtfs();

    var tripUpdate = new TripUpdateBuilder(
      env.trip1.getId().getId(),
      RealtimeTestEnvironment.SERVICE_DATE,
      SCHEDULED,
      env.timeZone
    )
      .addDelayedStopTime(STOP_SEQUENCE, DELAY)
      .build();

    var result = env.applyTripUpdate(tripUpdate);

    assertEquals(1, result.successful());

    // trip1 should be modified
    {
      var pattern1 = env.getPatternForTrip(env.trip1);
      final int trip1Index = pattern1.getScheduledTimetable().getTripIndex(env.trip1.getId());

      final TimetableSnapshot snapshot = env.getTimetableSnapshot();
      final Timetable trip1Realtime = snapshot.resolve(
        pattern1,
        RealtimeTestEnvironment.SERVICE_DATE
      );
      final Timetable trip1Scheduled = snapshot.resolve(pattern1, null);

      assertNotSame(trip1Realtime, trip1Scheduled);
      assertNotSame(
        trip1Realtime.getTripTimes(trip1Index),
        trip1Scheduled.getTripTimes(trip1Index)
      );
      assertEquals(1, trip1Realtime.getTripTimes(trip1Index).getArrivalDelay(STOP_SEQUENCE));
      assertEquals(1, trip1Realtime.getTripTimes(trip1Index).getDepartureDelay(STOP_SEQUENCE));

      assertEquals(
        RealTimeState.SCHEDULED,
        trip1Scheduled.getTripTimes(trip1Index).getRealTimeState()
      );
      assertEquals(
        RealTimeState.UPDATED,
        trip1Realtime.getTripTimes(trip1Index).getRealTimeState()
      );
    }

    // trip2 should keep the scheduled information
    {
      var pattern = env.getPatternForTrip(env.trip2);
      final int tripIndex = pattern.getScheduledTimetable().getTripIndex(env.trip2.getId());

      final TimetableSnapshot snapshot = env.getTimetableSnapshot();
      final Timetable realtime = snapshot.resolve(pattern, RealtimeTestEnvironment.SERVICE_DATE);
      final Timetable scheduled = snapshot.resolve(pattern, null);

      assertSame(realtime, scheduled);
      assertSame(realtime.getTripTimes(tripIndex), scheduled.getTripTimes(tripIndex));
      assertEquals(0, realtime.getTripTimes(tripIndex).getArrivalDelay(STOP_SEQUENCE));
      assertEquals(0, realtime.getTripTimes(tripIndex).getDepartureDelay(STOP_SEQUENCE));

      assertEquals(RealTimeState.SCHEDULED, scheduled.getTripTimes(tripIndex).getRealTimeState());
      assertEquals(RealTimeState.SCHEDULED, realtime.getTripTimes(tripIndex).getRealTimeState());
    }
  }

  /**
   * Tests delays to multiple stop times, where arrival and departure do not have the same delay.
   */
  @Test
  void complexDelay() {
    var env = RealtimeTestEnvironment.gtfs();

    String tripId = env.trip2.getId().getId();

    var builder = new TripUpdateBuilder(tripId, SERVICE_DATE, SCHEDULED, env.timeZone)
      .addDelayedStopTime(0, 0)
      .addDelayedStopTime(1, 60, 80)
      .addDelayedStopTime(2, 90, 90);

    var tripUpdate = builder.build();

    assertSuccess(env.applyTripUpdate(tripUpdate));

    // THEN
    final TimetableSnapshot snapshot = env.getTimetableSnapshot();

    final TripPattern originalTripPattern = env.transitModel
      .getTransitModelIndex()
      .getPatternForTrip()
      .get(env.trip2);

    final Timetable originalTimetableForToday = snapshot.resolve(originalTripPattern, SERVICE_DATE);
    final Timetable originalTimetableScheduled = snapshot.resolve(originalTripPattern, null);

    assertNotSame(originalTimetableForToday, originalTimetableScheduled);

    final int originalTripIndexScheduled = originalTimetableScheduled.getTripIndex(tripId);
    assertTrue(
      originalTripIndexScheduled > -1,
      "Original trip should be found in scheduled time table"
    );
    final TripTimes originalTripTimesScheduled = originalTimetableScheduled.getTripTimes(
      originalTripIndexScheduled
    );
    assertFalse(
      originalTripTimesScheduled.isCanceledOrDeleted(),
      "Original trip times should not be canceled in scheduled time table"
    );
    assertEquals(RealTimeState.SCHEDULED, originalTripTimesScheduled.getRealTimeState());

    final int originalTripIndexForToday = originalTimetableForToday.getTripIndex(tripId);
    assertTrue(
      originalTripIndexForToday > -1,
      "Original trip should be found in time table for service date"
    );

    assertEquals(
      "SCHEDULED | A1 0:01 0:01:01 | B1 0:01:10 0:01:11 | C1 0:01:20 0:01:21",
      env.getScheduledTimetable(env.trip2.getId())
    );
    assertEquals(
      "UPDATED | A1 0:01 0:01:01 | B1 0:02:10 0:02:31 | C1 0:02:50 0:02:51",
      env.getRealtimeTimetable(env.trip2)
    );
  }
}
