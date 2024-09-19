package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper;

import de.fraunhofer.iese.cognac.ads.ads_platform.mapper.MapConfig;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.ConsentDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Consent;

import org.mapstruct.Mapper;

@Mapper(config = MapConfig.class, uses = CommonsMapper.class)
public interface ConsentMapper {
  ConsentDto mapEntityToDto(Consent consent);

  Consent mapDtoToEntity(ConsentDto consentDto);
}
