package org.opentripplanner.openstreetmap.issues;

import org.opentripplanner.graph_builder.issue.api.DataImportIssue;
import org.opentripplanner.openstreetmap.model.OSMWithTags;

public record FloorNumberUnknownGuessedFromAltitude(
  String layer,
  Integer floorNumber,
  OSMWithTags entity
)
  implements DataImportIssue {
  private static final String FMT =
    "%s : could not determine floor number for layer %s. Guessed %s (0-based) from altitude.";

  private static final String HTMLFMT =
    "<a href='%s'>'%s'</a> : could not determine floor number for layer %s. Guessed %s (0-based) from altitude.";

  @Override
  public String getMessage() {
    return String.format(FMT, entity.getId(), layer, floorNumber);
  }

  @Override
  public String getHTMLMessage() {
    return String.format(HTMLFMT, entity.url(), entity.getId(), layer, floorNumber);
  }
}
