package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.mapper;

import de.fraunhofer.iese.cognac.ads.ads_platform.mapper.MapConfig;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.ActionDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.dto.TwinResourceDto;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.Action;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.data_sovereignty.model.TwinResource;

import org.mapstruct.Mapper;

import java.util.Map;
import java.util.Set;

@Mapper(config = MapConfig.class)
public interface CommonsMapper {

  Map<TwinResourceDto, Set<ActionDto>> mapTwinResourcePermissionsEntityToDto(Map<TwinResource, Set<Action>> twinResourcePermissions);

  Map<TwinResource, Set<Action>> mapTwinResourcePermissionsDtoToEntity(Map<TwinResourceDto, Set<ActionDto>> twinResourcePermissionsDto);

  Set<ActionDto> mapActionsEntityToDto(Set<Action> actions);

  Set<Action> mapActionsDtoToEntity(Set<ActionDto> actionsDto);


}
