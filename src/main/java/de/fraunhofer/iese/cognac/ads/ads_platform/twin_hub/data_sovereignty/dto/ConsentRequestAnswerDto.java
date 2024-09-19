package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ConsentRequestAnswerDto {

  @Schema(
      description = "Decision related to the consent request",
      example = "ACCEPT",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotNull
  private ConsentRequestDecisionDto decision;

  @Schema(
      description = "Grant access to all twins (current and future)",
      example = "false"
  )
  private boolean grantAccessToAllTwins;

  @Schema(
      description =
          "IDs of the twins that should be covered by the consent",
      example = "[\"bergkoppel-1\", \"großemühle-1\"]"
  )
  @Nullable
  private Set<@NotBlank String> twinIds;

  @Schema(
      description = "Additional notes by the consent giver",
      example = "as reference see contract in harvesting folder."
  )
  @Nullable
  private String additionalNotes;
}
