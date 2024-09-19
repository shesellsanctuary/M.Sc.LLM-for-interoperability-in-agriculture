package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper;

import de.fraunhofer.iese.cognac.ads.ads_platform.mapper.MapConfig;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.AccessLogEntryDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.AccessLogEntry;

import org.mapstruct.Mapper;

@Mapper(config = MapConfig.class)
public interface AccessLogMapper {
  AccessLogEntryDto mapEntityToDto(AccessLogEntry accessLogEntry);
}
