package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Data
@Document
public class Consent {
  private static final String WILDCARD_STRING = "*";

  @Id
  private String id;

  private Instant createdAt;

  private String consentGiverId;

  private RequestorIdentity requestorIdentity;

  @Nullable
  private Instant startTime;

  @Nullable
  private Instant endTime;

  private ConsentState state;

  private boolean grantFullAccess;

  private boolean grantAccessToAllTwins;

  @Nullable
  private Set<String> twinIds;

  private boolean grantAllTwinResourcePermissions;

  @Nullable
  private Map<TwinResource, Set<Action>> twinResourcePermissions;

  @Nullable
  private String dataUsageStatement;

  @Nullable
  private String additionalNotes;

  private Consent(String id, Instant createdAt, String consentGiverId, RequestorIdentity requestorIdentity, @Nullable Instant startTime, @Nullable Instant endTime, ConsentState state, boolean grantFullAccess, boolean grantAccessToAllTwins, @Nullable Set<String> twinIds, boolean grantAllTwinResourcePermissions, @Nullable Map<TwinResource, Set<Action>> twinResourcePermissions, @Nullable String dataUsageStatement, @Nullable String additionalNotes) {
    this.id = id;
    this.createdAt = createdAt;
    this.consentGiverId = consentGiverId;
    this.requestorIdentity = requestorIdentity;
    this.startTime = startTime;
    this.endTime = endTime;
    this.state = state;
    this.grantFullAccess = grantFullAccess;
    this.grantAccessToAllTwins = grantAccessToAllTwins;
    this.twinIds = twinIds;
    this.grantAllTwinResourcePermissions = grantAllTwinResourcePermissions;
    this.twinResourcePermissions = twinResourcePermissions;
    this.dataUsageStatement = dataUsageStatement;
    this.additionalNotes = additionalNotes;
  }

  public Consent() {
  }

  public static Consent of(String id, Instant createdAt, String consentGiverId, RequestorIdentity requestorIdentity, @Nullable Instant startTime, @Nullable Instant endTime, ConsentState state, boolean grantFullAccess, boolean grantAccessToAllTwins, @Nullable Set<String> twinIds, boolean grantAllTwinResourcePermissions, @Nullable Map<TwinResource, Set<Action>> twinResourcePermissions, @Nullable String dataUsageStatement, @Nullable String additionalNotes) {
    return new Consent(id, createdAt, consentGiverId, requestorIdentity, startTime, endTime, state, grantFullAccess, grantAccessToAllTwins, twinIds, grantAllTwinResourcePermissions, twinResourcePermissions, dataUsageStatement, additionalNotes);
  }

  public boolean isGivenBy(final String consentGiverId) {
    return Objects.equals(this.consentGiverId, consentGiverId);
  }

  public boolean isActive() {
    return this.state == ConsentState.ACTIVE;
  }

  public boolean authorizesUser(String userId) {
    return Objects.equals(this.requestorIdentity.getUserId(), userId)
        || Objects.equals(this.requestorIdentity.getUserId(), WILDCARD_STRING);
  }

  public boolean authorizesClient(String clientId) {
    return Objects.equals(this.requestorIdentity.getClientId(), clientId)
        || Objects.equals(this.requestorIdentity.getClientId(), WILDCARD_STRING);
  }

  public boolean grantsAccessToTwin(String twinId) {
    return this.isGrantFullAccess()
        || this.isGrantAccessToAllTwins()
        || Optional.ofNullable(this.getTwinIds())
            .map(twinIds -> twinIds.contains(twinId))
            .orElse(false);
  }

  public boolean grantsTwinResourceAction(final TwinResource twinResource, final Action action) {
    return this.isGrantFullAccess()
        || this.isGrantAllTwinResourcePermissions()
        || Optional.ofNullable(this.getTwinResourcePermissions())
            .map(twinResourcePermissions -> twinResourcePermissions.get(twinResource))
            .map(actions -> actions.contains(action))
            .orElse(false);
  }

  public boolean isWithinValidTime(Instant currentTime) {
    return isValidStartTime(currentTime) && isValidEndTime(currentTime);
  }

  private boolean isValidStartTime(Instant currentTime) {
    return this.getStartTime() == null || this.getStartTime().isBefore(currentTime);
  }

  private boolean isValidEndTime(Instant currentTime) {
    return this.getEndTime() == null || this.getEndTime().isAfter(currentTime);
  }

  public boolean isFullAccess() {
    return this.isGrantFullAccess();
  }
}
