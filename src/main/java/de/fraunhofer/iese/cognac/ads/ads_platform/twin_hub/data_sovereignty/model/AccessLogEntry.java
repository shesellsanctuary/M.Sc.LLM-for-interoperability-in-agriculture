package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.time.Instant;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Document
public class AccessLogEntry {
  @Id
  private String id;

  @NotNull
  private Instant date;

  @NotNull
  @Valid
  private RequestorIdentity requestorIdentity;

  @NotBlank
  private String twinId;

  @Nullable
  private String twinName;

  @Nullable
  private TwinResource twinResource;

  @Nullable
  private String twinResourcePath;

  @NotNull
  private Action action;

  private AccessLogEntry(String id, @NotNull Instant date, @NotNull @Valid RequestorIdentity requestorIdentity, @NotBlank String twinId, @Nullable String twinName, @Nullable TwinResource twinResource, @Nullable String twinResourcePath, @NotNull Action action) {
    this.id = id;
    this.date = date;
    this.requestorIdentity = requestorIdentity;
    this.twinId = twinId;
    this.twinName = twinName;
    this.twinResource = twinResource;
    this.twinResourcePath = twinResourcePath;
    this.action = action;
  }

  public static AccessLogEntry of(String id, @NotNull Instant date, @NotNull @Valid RequestorIdentity requestorIdentity, @NotBlank String twinId, @Nullable String twinName, @Nullable TwinResource twinResource, @Nullable String twinResourcePath, @NotNull Action action) {
    return new AccessLogEntry(id, date, requestorIdentity, twinId, twinName, twinResource, twinResourcePath, action);
  }
}
