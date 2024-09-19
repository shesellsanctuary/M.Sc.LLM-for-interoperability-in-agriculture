package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper;

import de.fraunhofer.iese.cognac.ads.ads_platform.mapper.MapConfig;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.ConsentRequestAnswerDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.ConsentRequestDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequest;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.ConsentRequestAnswer;

import org.mapstruct.Mapper;

@Mapper(config = MapConfig.class, uses = CommonsMapper.class)
public interface ConsentRequestMapper {
  ConsentRequestDto mapEntityToDto(ConsentRequest consentRequest);

  ConsentRequest mapDtoToEntity(ConsentRequestDto consentRequestDto);

  ConsentRequestAnswer mapDtoToEntity(ConsentRequestAnswerDto consentRequestAnswerDto);

  ConsentRequestAnswerDto mapEntityToDto(ConsentRequestAnswer consentRequestAnswer);
}
