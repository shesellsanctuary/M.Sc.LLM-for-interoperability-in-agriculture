package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
public class RequestorIdentity {

  @NotBlank
  private String userId;

  @NotBlank
  private String clientId;

  @Nullable
  private String userName;

  @Nullable
  private String clientName;

  private RequestorIdentity(@NotBlank String userId, @NotBlank String clientId, @Nullable String userName, @Nullable String clientName) {
    this.userId = userId;
    this.clientId = clientId;
    this.userName = userName;
    this.clientName = clientName;
  }

  public RequestorIdentity() {
  }

  public static RequestorIdentity of(@NotBlank String userId, @NotBlank String clientId, @Nullable String userName, @Nullable String clientName) {
    return new RequestorIdentity(userId, clientId, userName, clientName);
  }

}
