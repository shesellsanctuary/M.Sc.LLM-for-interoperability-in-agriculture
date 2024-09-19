package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
@Document
public class ConsentRequest {

  @Id
  private String id;

  private Instant createdAt;

  private String receiverId;

  private RequestorIdentity requestorIdentity;

  @Nullable
  private Instant startTime;

  @Nullable
  private Instant endTime;

  private boolean requestFullAccess;

  private boolean requestAllTwinResourcePermissions;

  @Nullable
  private Map<TwinResource, Set<Action>> twinResourcePermissions;

  @Nullable
  private String dataUsageStatement;

  private ConsentRequest(String id, Instant createdAt, String receiverId, RequestorIdentity requestorIdentity, @Nullable Instant startTime, @Nullable Instant endTime, boolean requestFullAccess, boolean requestAllTwinResourcePermissions, @Nullable Map<TwinResource, Set<Action>> twinResourcePermissions, @Nullable String dataUsageStatement) {
    this.id = id;
    this.createdAt = createdAt;
    this.receiverId = receiverId;
    this.requestorIdentity = requestorIdentity;
    this.startTime = startTime;
    this.endTime = endTime;
    this.requestFullAccess = requestFullAccess;
    this.requestAllTwinResourcePermissions = requestAllTwinResourcePermissions;
    this.twinResourcePermissions = twinResourcePermissions;
    this.dataUsageStatement = dataUsageStatement;
  }

  public ConsentRequest() {
  }

  public static ConsentRequest of(String id, Instant createdAt, String receiverId, RequestorIdentity requestorIdentity, @Nullable Instant startTime, @Nullable Instant endTime, boolean requestFullAccess, boolean requestAllTwinResourcePermissions, @Nullable Map<TwinResource, Set<Action>> twinResourcePermissions, @Nullable String dataUsageStatement) {
    return new ConsentRequest(id, createdAt, receiverId, requestorIdentity, startTime, endTime, requestFullAccess, requestAllTwinResourcePermissions, twinResourcePermissions, dataUsageStatement);
  }
}
