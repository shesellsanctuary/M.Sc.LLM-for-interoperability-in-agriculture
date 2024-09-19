package de.fraunhofer.iese.cognac.ads.ads_platform.configuration;

import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile({"embedded-mongo", "test"})
@Configuration
@Import(EmbeddedMongoAutoConfiguration.class)
public class EmbeddedMongoConfig {
}
