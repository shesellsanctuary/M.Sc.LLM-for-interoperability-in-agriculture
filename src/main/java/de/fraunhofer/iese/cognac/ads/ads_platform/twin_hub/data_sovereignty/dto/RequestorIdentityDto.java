package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
public class RequestorIdentityDto {
  @Schema(description = "The ID of the user", example = "BobsId-1", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank
  private String userId;

  @Schema(description = "The ID of the client", example = "ClientIdOfBasicFMIS-1", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank
  private String clientId;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "bob")
  @Nullable
  private String userName;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "basic-fmis")
  @Nullable
  private String clientName;
}
