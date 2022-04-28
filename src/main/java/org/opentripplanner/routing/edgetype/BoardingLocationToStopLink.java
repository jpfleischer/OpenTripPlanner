package org.opentripplanner.routing.edgetype;

import java.util.List;
import org.locationtech.jts.geom.LineString;
import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.routing.vertextype.StreetVertex;
import org.opentripplanner.routing.vertextype.TransitStopVertex;

/**
 * This represents the connection between a boarding location and a transit vertex where going from the
 * street to the vehicle is immediate and you don't want to display a polyline on the map.
 */
public class BoardingLocationToStopLink extends StreetTransitEntityLink<TransitStopVertex> {

  public BoardingLocationToStopLink(StreetVertex fromv, TransitStopVertex tov) {
    super(fromv, tov, tov.getWheelchairBoarding());
  }

  public BoardingLocationToStopLink(TransitStopVertex fromv, StreetVertex tov) {
    super(fromv, tov, fromv.getWheelchairBoarding());
  }

  protected int getStreetToStopTime() {
    return 0;
  }

  @Override
  public LineString getGeometry() {
    return GeometryUtils.makeLineString(List.of(fromv.getCoordinate(), fromv.getCoordinate()));
  }
}
