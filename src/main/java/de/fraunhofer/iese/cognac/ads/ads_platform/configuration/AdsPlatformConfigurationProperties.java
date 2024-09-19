package de.fraunhofer.iese.cognac.ads.ads_platform.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("ads-platform")
@Data
@Validated
public class AdsPlatformConfigurationProperties {

  @NotBlank
  private String filesDir = "ads-platform-files";

  @Valid
  @NotNull
  private BaSyx basyx = new BaSyx();

  @Data
  public static class BaSyx {
    @NotBlank
    private String aasServerUrl = "http://invalid";
    @NotBlank
    private String registryUrl = "http://invalid";
  }
}
