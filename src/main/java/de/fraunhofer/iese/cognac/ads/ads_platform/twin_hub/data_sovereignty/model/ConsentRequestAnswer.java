package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Set;

@Data
public class ConsentRequestAnswer {

  private ConsentRequestDecision decision;

  private boolean grantAccessToAllTwins;

  @Nullable
  private Set<String> twinIds;

  @Nullable
  private String additionalNotes;

  private ConsentRequestAnswer(ConsentRequestDecision decision, boolean grantAccessToAllTwins, @Nullable Set<String> twinIds, @Nullable String additionalNotes) {
    this.decision = decision;
    this.grantAccessToAllTwins = grantAccessToAllTwins;
    this.twinIds = twinIds;
    this.additionalNotes = additionalNotes;
  }

  public ConsentRequestAnswer() {
  }

  public static ConsentRequestAnswer of(ConsentRequestDecision decision, boolean grantAccessToAllTwins, @Nullable Set<String> twinIds, @Nullable String additionalNotes) {
    return new ConsentRequestAnswer(decision, grantAccessToAllTwins, twinIds, additionalNotes);
  }
}
