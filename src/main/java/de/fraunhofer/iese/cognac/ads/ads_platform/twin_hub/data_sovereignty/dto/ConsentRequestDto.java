package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ConsentRequestDto {

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  @Nullable
  private String id;

  @Schema(
      description = "The point in time when the consent request was created",
      example = "2022-04-28T10:11:12.00Z",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  @Nullable
  private Instant createdAt;

  @Schema(
      description = "Id of the receiver of this consent request",
      example = "Bill-the-farmer-ID",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotBlank
  private String receiverId;

  @Schema(
      description = "Information about the identity of the requestor",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  @Nullable
  private RequestorIdentityDto requestorIdentity;

  @Schema(
      description = "The point in time from which the consent is valid",
      example = "2022-04-28T10:11:12.00Z"
  )
  @Nullable
  private Instant startTime;

  @Schema(
      description = "The point in time to which the consent is valid",
      example = "2024-04-28T10:11:12.00Z"
  )
  @Nullable
  private Instant endTime;

  @Schema(
      description =
          "Request full access (everything allowed)",
      example = "false"
  )
  private boolean requestFullAccess;

  @Schema(
      description = "Request all twin resource permissions (all actions on all twin resources should be allowed)",
      example = "false"
  )
  private boolean requestAllTwinResourcePermissions;

  @Schema(
      description = "A map of twin resources to a set of possible actions (the permissions)",
      example = "{\"GEOMETRIES\": [\"READ\", \"UPDATE\"], \"WORK_RECORDS_FERTILIZATION\": [\"READ\"]}"
  )
  @Nullable
  private Map<@NotNull TwinResourceDto, @NotEmpty Set<@NotNull ActionDto>>
      twinResourcePermissions;

  @Schema(
      description = "Optional data usage statement by the consent requestor",
      example =
          "We will only use your Work Records and field geometries to provide you our service of analyzing and optimizing your seeding.\n"
              + "This is necessary to enable us to analyze your actual seeding process and compute possible alternatives."
              + "We will not do anything else with your data.\n"
              + "If this is not okay for you: Sorry, you can not use our service}"
  )
  @Nullable
  private String dataUsageStatement;

}
