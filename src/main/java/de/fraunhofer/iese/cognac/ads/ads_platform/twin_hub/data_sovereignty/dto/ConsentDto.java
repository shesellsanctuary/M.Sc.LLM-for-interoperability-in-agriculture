package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "A consent")
public class ConsentDto {

  @Schema(
      description = "The ID of the consent, assigned by the server",
      example = "42",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  @Nullable
  private String id;

  @Schema(
      description = "The point in time when the consent was created",
      example = "2022-04-28T10:11:12.00Z",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  @Nullable
  private Instant createdAt;

  @Schema(
      description = "The ID of the user that granted this consent",
      example = "Bill-the-farmer-ID",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  @Nullable
  private String consentGiverId;

  @Schema(
      description = "Information about the identity of the requestor",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotNull
  @Valid
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
      description = "status of the consent",
      example = "ACTIVE",
      accessMode = Schema.AccessMode.READ_ONLY
  )
  @Nullable
  private ConsentStateDto state;

  @Schema(
      description = "Grant full access (everything allowed)",
      example = "false"
  )
  private boolean grantFullAccess;

  @Schema(
      description = "Grant access to all twins (current and future)",
      example = "false"
  )
  private boolean grantAccessToAllTwins;

  @Schema(
      description = "IDs of the twins covered by this consent",
      example = "[\"bergkoppelfield-1\", \"großemühlefield-1\"]"
  )
  @Nullable
  private Set<@NotBlank String> twinIds;

  @Schema(
      description = "Grant all twin resource permissions (all actions on all twin resources should be allowed)",
      example = "false"
  )
  private boolean grantAllTwinResourcePermissions;

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

  @Schema(
      description = "Additional notes by the consent giver",
      example = "as reference see contract in harvesting folder."
  )
  @Nullable
  private String additionalNotes;

}
