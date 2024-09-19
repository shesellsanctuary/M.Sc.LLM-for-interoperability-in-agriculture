package de.fraunhofer.iese.cognac.ads.ads_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;

@SpringBootApplication(exclude = {EmbeddedMongoAutoConfiguration.class})
public class AdsPlatformApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdsPlatformApplication.class, args);
  }

}
