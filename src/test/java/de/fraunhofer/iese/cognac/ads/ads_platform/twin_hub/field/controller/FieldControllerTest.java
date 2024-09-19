package de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.fraunhofer.iese.cognac.ads.ads_platform.exception.DoesNotExistException;
import de.fraunhofer.iese.cognac.ads.ads_platform.exception.ForbiddenException;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.Authentication;
import de.fraunhofer.iese.cognac.ads.ads_platform.security.RequestorType;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.mapper.FieldMapperImpl;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.Field;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.SoilMeasurementWorkOrder;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.model.TypedResource;
import de.fraunhofer.iese.cognac.ads.ads_platform.twin_hub.field.service.FieldService;

import org.geojson.Feature;
import org.geojson.LngLatAlt;
import org.geojson.MultiLineString;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@WebMvcTest(controllers = FieldController.class)
@Import(FieldMapperImpl.class)
class FieldControllerTest {
  public static final String TRAFFICABLE_AREA_JSON = "{\"type\":\"Feature\",\"properties\":{\"date\": \"2021-05-25\", \"source\": \"Rawdata\", \"type\": \"Feldweg\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[10.453195, 51.121625], [10.456628, 51.118018], [10.459288,51.118906], [ 10.461347, 51.119041], [10.462248, 51.119391], [10.459459, 51.122352], [10.460318, 51.122702],[10.462935, 51.119768], [10.468813, 51.122137],[10.464694, 51.126928], [10.460575, 51.124909], [10.459545, 51.125744], [10.458086, 51.125098], [10.453195, 51.121625]],[[10.460348,51.120577],[10.460492, 51.120627], [10.460969, 51.120139 ],[10.460819, 51.120081],[10.460348,51.120577]], [[10.463834,51.123798],[10.46407, 51.123884], [10.46451, 51.123406], [10.464285, 51.123325], [10.463834, 51.123798]]]}}";
  public static final String NON_TRAFFICABLE_AREA_JSON = "{\"type\":\"Feature\",\"properties\":{\"date\": \"2021-05-25\", \"source\": \"Rawdata\", \"type\": \"Feldweg\"},\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[10.460348, 51.120577],[10.460492, 51.120627], [10.460969, 51.120139],[10.460819, 51.120081],[10.460348, 51.120577]]],\n" +
      " [[[10.463834, 51.123798], [10.46407, 51.123884],[10.46451, 51.123406], [10.464285, 51.123325], [10.463834, 51.123798]]]]}}";
  public static final String TRACKS_JSON = "{\"type\":\"Feature\",\"properties\":{\"date\": \"2021-05-25\", \"details\": {\"deviceUsed\": \"\", \"deviceWidth\": \"\", \"numberOfSeeders\": \"\"}, \"source\": \"Rawdata\", \"type\": \"harvest\"},\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":" +
      "[[[10.456666946411133, 51.118098077648014], [10.453383922576904, 51.12162691254952], [10.453641414642334, 51.12186934169336],[10.456924438476562, 51.118232770950684],[10.457310676574705, 51.11836746386068],\n" +
      "[10.45400619506836, 51.12203096041578],[10.454306602478027, 51.12223298302365],[10.457696914672852, 51.11844827941819],[10.458040237426758, 51.11859644090647],[10.454607009887695, 51.12243500474804],\n" +
      "[10.454907417297363, 51.122663961634345], [10.458405017852783, 51.11871766358896], [10.458791255950926, 51.118838885953366], [10.455207824707031, 51.12291985328197], [10.455572605133057, 51.123148807765176], [10.459134578704834, 51.11897357709634],[10.459606647491455, 51.11901398436266],[10.455851554870605, 51.123404696727015],[10.456194877624512, 51.123633648807164],\n" +
      "[10.460035800933838, 51.11902745344356],[10.460507869720459, 51.119054391593636],[10.45647382736206, 51.12382219672695], [10.456860065460203, 51.12407808195854], [10.460958480834961, 51.11908132972798],[10.461366176605225, 51.11917561307453], [10.45724630355835, 51.1242935631594]],\n" +
      "[[10.461773872375488, 51.11937764816921], [10.457439422607422, 51.124428238399496],[10.457696914672852, 51.124657185406484],[10.459649562835693, 51.122569685406816],[10.460035800933838, 51.1227447696762],[10.45799732208252, 51.1249130660126],[10.458405017852783, 51.12511507601617],\n" +
      "[10.463125705718994, 51.11995681054406],[10.463533401489258, 51.12014537347143], [10.458812713623047, 51.12523628159424], [10.459241867065428, 51.12543829018424], [10.463898181915283, 51.12032046692916],\n" +
      "[10.464391708374023, 51.12048209107035], [10.460443496704102, 51.12467065284215],[10.460808277130127, 51.12481879437525], [10.464799404144287, 51.120697589045704],[10.465335845947266, 51.12089961748466],[10.46123743057251, 51.12504773944648], [10.46175241470337, 51.12531708513625],[10.465850830078125, 51.12107470808378],\n" +
      "[10.466322898864746, 51.121249798019264],[10.462160110473631, 51.12551909337289],[10.462567806243896, 51.1257345678515],[10.466752052307129, 51.12141141890921], [10.46724557876587, 51.12158650756855],[10.462954044342041, 51.12593657426229],[10.463361740112305, 51.126125112781956],[10.467653274536133, 51.1217885321201], [10.468125343322754, 51.121909746426965], [10.463769435882568, 51.126327117484685],[10.46417713165283, 51.12651565441018],[10.468404293060303, 51.12212523774273]]]}}";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FieldService fieldServiceMock;

  private Feature getTrafficableAreaFeature() {
    final Feature trafficableArea = new Feature();
    trafficableArea.setProperty("type", "Feldweg");
    trafficableArea.setProperty("source", "Rawdata");
    trafficableArea.setProperty("date", "2021-05-25");
    Polygon polygon = new Polygon();
    polygon.setExteriorRing(Arrays.asList(
        new LngLatAlt(10.453195, 51.121625),
        new LngLatAlt(10.456628, 51.118018),
        new LngLatAlt(10.459288, 51.118906),
        new LngLatAlt(10.461347, 51.119041),
        new LngLatAlt(10.462248, 51.119391),
        new LngLatAlt(10.459459, 51.122352),
        new LngLatAlt(10.460318, 51.122702),
        new LngLatAlt(10.462935, 51.119768),
        new LngLatAlt(10.468813, 51.122137),
        new LngLatAlt(10.464694, 51.126928),
        new LngLatAlt(10.460575, 51.124909),
        new LngLatAlt(10.459545, 51.125744),
        new LngLatAlt(10.458086, 51.125098),
        new LngLatAlt(10.453195, 51.121625)
    ));
    polygon.addInteriorRing(Arrays.asList(
        new LngLatAlt(10.460348, 51.120577),
        new LngLatAlt(10.460492, 51.120627),
        new LngLatAlt(10.460969, 51.120139),
        new LngLatAlt(10.460819, 51.120081),
        new LngLatAlt(10.460348, 51.120577)
    ));
    polygon.addInteriorRing(Arrays.asList(
        new LngLatAlt(10.463834, 51.123798),
        new LngLatAlt(10.46407, 51.123884),
        new LngLatAlt(10.46451, 51.123406),
        new LngLatAlt(10.464285, 51.123325),
        new LngLatAlt(10.463834, 51.123798)
    ));
    trafficableArea.setGeometry(polygon);
    return trafficableArea;
  }

  private Feature getNonTrafficableAreaFeature() {
    final Feature nonTrafficableArea = new Feature();
    nonTrafficableArea.setProperty("type", "Feldweg");
    nonTrafficableArea.setProperty("source", "Rawdata");
    nonTrafficableArea.setProperty("date", "2021-05-25");
    MultiPolygon multiPolygon = new MultiPolygon();
    multiPolygon.add(new Polygon(
        Arrays.asList(
            new LngLatAlt(10.460348, 51.120577),
            new LngLatAlt(10.460492, 51.120627),
            new LngLatAlt(10.460969, 51.120139),
            new LngLatAlt(10.460819, 51.120081),
            new LngLatAlt(10.460348, 51.120577)
        )));
    multiPolygon.add(new Polygon(
        Arrays.asList(
            new LngLatAlt(10.463834, 51.123798),
            new LngLatAlt(10.46407, 51.123884),
            new LngLatAlt(10.46451, 51.123406),
            new LngLatAlt(10.464285, 51.123325),
            new LngLatAlt(10.463834, 51.123798)
        )));
    nonTrafficableArea.setGeometry(multiPolygon);
    return nonTrafficableArea;
  }

  private Feature getTracksFeature() {
    final Feature tracks = new Feature();
    tracks.setProperty("type", "harvest");
    tracks.setProperty("source", "Rawdata");
    tracks.setProperty("date", "2021-05-25");
    final Map<String, Object> details = new HashMap<>();
    details.put("deviceUsed", "");
    details.put("deviceWidth", "");
    details.put("numberOfSeeders", "");
    tracks.setProperty("details", details);
    MultiLineString multiLineString = new MultiLineString();

    multiLineString.add(Arrays.asList(
        new LngLatAlt(10.456666946411133, 51.118098077648014),
        new LngLatAlt(10.453383922576904, 51.12162691254952),
        new LngLatAlt(10.453641414642334, 51.12186934169336),
        new LngLatAlt(10.456924438476562, 51.118232770950684),
        new LngLatAlt(10.457310676574705, 51.11836746386068),
        new LngLatAlt(10.45400619506836, 51.12203096041578),
        new LngLatAlt(10.454306602478027, 51.12223298302365),
        new LngLatAlt(10.457696914672852, 51.11844827941819),
        new LngLatAlt(10.458040237426758, 51.11859644090647),
        new LngLatAlt(10.454607009887695, 51.12243500474804),
        new LngLatAlt(10.454907417297363, 51.122663961634345),
        new LngLatAlt(10.458405017852783, 51.11871766358896),
        new LngLatAlt(10.458791255950926, 51.118838885953366),
        new LngLatAlt(10.455207824707031, 51.12291985328197),
        new LngLatAlt(10.455572605133057, 51.123148807765176),
        new LngLatAlt(10.459134578704834, 51.11897357709634),
        new LngLatAlt(10.459606647491455, 51.11901398436266),
        new LngLatAlt(10.455851554870605, 51.123404696727015),
        new LngLatAlt(10.456194877624512, 51.123633648807164),
        new LngLatAlt(10.460035800933838, 51.11902745344356),
        new LngLatAlt(10.460507869720459, 51.119054391593636),
        new LngLatAlt(10.45647382736206, 51.12382219672695),
        new LngLatAlt(10.456860065460203, 51.12407808195854),
        new LngLatAlt(10.460958480834961, 51.11908132972798),
        new LngLatAlt(10.461366176605225, 51.11917561307453),
        new LngLatAlt(10.45724630355835, 51.1242935631594)
    ));
    multiLineString.add(Arrays.asList(
        new LngLatAlt(10.461773872375488, 51.11937764816921),
        new LngLatAlt(10.457439422607422, 51.124428238399496),
        new LngLatAlt(10.457696914672852, 51.124657185406484),
        new LngLatAlt(10.459649562835693, 51.122569685406816),
        new LngLatAlt(10.460035800933838, 51.1227447696762),
        new LngLatAlt(10.45799732208252, 51.1249130660126),
        new LngLatAlt(10.458405017852783, 51.12511507601617),
        new LngLatAlt(10.463125705718994, 51.11995681054406),
        new LngLatAlt(10.463533401489258, 51.12014537347143),
        new LngLatAlt(10.458812713623047, 51.12523628159424),
        new LngLatAlt(10.459241867065428, 51.12543829018424),
        new LngLatAlt(10.463898181915283, 51.12032046692916),
        new LngLatAlt(10.464391708374023, 51.12048209107035),
        new LngLatAlt(10.460443496704102, 51.12467065284215),
        new LngLatAlt(10.460808277130127, 51.12481879437525),
        new LngLatAlt(10.464799404144287, 51.120697589045704),
        new LngLatAlt(10.465335845947266, 51.12089961748466),
        new LngLatAlt(10.46123743057251, 51.12504773944648),
        new LngLatAlt(10.46175241470337, 51.12531708513625),
        new LngLatAlt(10.465850830078125, 51.12107470808378),
        new LngLatAlt(10.466322898864746, 51.121249798019264),
        new LngLatAlt(10.462160110473631, 51.12551909337289),
        new LngLatAlt(10.462567806243896, 51.1257345678515),
        new LngLatAlt(10.466752052307129, 51.12141141890921),
        new LngLatAlt(10.46724557876587, 51.12158650756855),
        new LngLatAlt(10.462954044342041, 51.12593657426229),
        new LngLatAlt(10.463361740112305, 51.126125112781956),
        new LngLatAlt(10.467653274536133, 51.1217885321201),
        new LngLatAlt(10.468125343322754, 51.121909746426965),
        new LngLatAlt(10.463769435882568, 51.126327117484685),
        new LngLatAlt(10.46417713165283, 51.12651565441018),
        new LngLatAlt(10.468404293060303, 51.12212523774273)
    ));
    tracks.setGeometry(multiLineString);
    return tracks;
  }

  @Test
  void givenWeAreUnauthenticatedWhenGetFieldsThenUnauthorized() throws Exception {
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields")
    ).andExpect(status().isUnauthorized());
    Mockito.verifyNoInteractions(fieldServiceMock);
  }

  @Test
  void givenWeAreAuthenticatedWithWrongScopeWhenGetFieldsThenForbidden() throws Exception {
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_foo"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verifyNoInteractions(fieldServiceMock);
  }

  @Test
  void givenWeAreAuthenticatedWithRightScopeAndThereAreFieldsWhenGetFieldsThenReturnFields() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    final Field field = new Field();
    field.setId("fieldId");
    field.setName("fieldName");
    field.setFarmId("farmId");
    Mockito.when(fieldServiceMock.getFields(expectedAuthentication)).thenReturn(Collections.singletonList(field));

    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isOk()).andExpect(content().json("[{\"id\":\"fieldId\",\"name\":\"fieldName\",\"farmId\":\"farmId\"}]"));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getFields(expectedAuthentication);
  }

  @Test
  void givenServiceProvidesNoData_whenGetTrafficableArea_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getTrafficableArea("123", expectedAuthentication)).thenReturn(Optional.empty());
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesData_whenGetTrafficableArea_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));

    final Feature trafficableArea = getTrafficableAreaFeature();

    Mockito.when(fieldServiceMock.getTrafficableArea("123", expectedAuthentication)).thenReturn(Optional.of(trafficableArea));

    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isOk()).andExpect(content().json(TRAFFICABLE_AREA_JSON));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenGetTrafficableArea_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getTrafficableArea("123", expectedAuthentication)).thenThrow(ForbiddenException.class);
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenUpdateTrafficableArea_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature trafficableArea = getTrafficableAreaFeature();

    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).setTrafficableArea("123", trafficableArea, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .content(TRAFFICABLE_AREA_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setTrafficableArea("123", trafficableArea, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceAcceptsData_whenUpdateTrafficableArea_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature trafficableArea = getTrafficableAreaFeature();

    Mockito.doNothing().when(fieldServiceMock).setTrafficableArea("123", trafficableArea, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .content(TRAFFICABLE_AREA_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isOk()).andExpect(content().json(TRAFFICABLE_AREA_JSON));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setTrafficableArea("123", trafficableArea, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenUpdateTrafficableArea_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature trafficableArea = getTrafficableAreaFeature();

    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).setTrafficableArea("123", trafficableArea, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .content(TRAFFICABLE_AREA_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setTrafficableArea("123", trafficableArea, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDoesNotThrow_whenDeleteTrafficableArea_thenNoContent() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doNothing().when(fieldServiceMock).deleteTrafficableArea("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNoContent());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenDeleteTrafficableArea_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).deleteTrafficableArea("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenDeleteTrafficableArea_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).deleteTrafficableArea("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesNoData_whenGetNonTrafficableArea_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getNonTrafficableArea("123", expectedAuthentication)).thenReturn(Optional.empty());
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getNonTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesData_whenGetNonTrafficableArea_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));

    final Feature nonTrafficableArea = getNonTrafficableAreaFeature();

    Mockito.when(fieldServiceMock.getNonTrafficableArea("123", expectedAuthentication)).thenReturn(Optional.of(nonTrafficableArea));

    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isOk()).andExpect(content().json(NON_TRAFFICABLE_AREA_JSON));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getNonTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenGetNonTrafficableArea_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getNonTrafficableArea("123", expectedAuthentication)).thenThrow(ForbiddenException.class);
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getNonTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenUpdateNonTrafficableArea_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature nonTrafficableArea = getNonTrafficableAreaFeature();

    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).setNonTrafficableArea("123", nonTrafficableArea, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .content(NON_TRAFFICABLE_AREA_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setNonTrafficableArea("123", nonTrafficableArea, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceAcceptsData_whenUpdateNonTrafficableArea_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature nonTrafficableArea = getNonTrafficableAreaFeature();

    Mockito.doNothing().when(fieldServiceMock).setNonTrafficableArea("123", nonTrafficableArea, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .content(NON_TRAFFICABLE_AREA_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isOk()).andExpect(content().json(NON_TRAFFICABLE_AREA_JSON));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setNonTrafficableArea("123", nonTrafficableArea, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenUpdateNonTrafficableArea_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature nonTrafficableArea = getNonTrafficableAreaFeature();

    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).setNonTrafficableArea("123", nonTrafficableArea, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .content(NON_TRAFFICABLE_AREA_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setNonTrafficableArea("123", nonTrafficableArea, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDoesNotThrow_whenDeleteNonTrafficableArea_thenNoContent() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doNothing().when(fieldServiceMock).deleteNonTrafficableArea("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNoContent());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteNonTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenDeleteNonTrafficableArea_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).deleteNonTrafficableArea("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteNonTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenDeleteNonTrafficableArea_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).deleteNonTrafficableArea("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/non-trafficable-area")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteNonTrafficableArea("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesNoData_whenGetTracks_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getTracks("123", expectedAuthentication)).thenReturn(Optional.empty());
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/tracks")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getTracks("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesData_whenGetTracks_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));

    final Feature tracks = getTracksFeature();

    Mockito.when(fieldServiceMock.getTracks("123", expectedAuthentication)).thenReturn(Optional.of(tracks));

    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/tracks")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isOk()).andExpect(content().json(TRACKS_JSON));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getTracks("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenGetTracks_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getTracks("123", expectedAuthentication)).thenThrow(ForbiddenException.class);
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/geometries/tracks")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getTracks("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenUpdateTracks_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature tracks = getTracksFeature();

    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).setTracks("123", tracks, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/tracks")
            .content(TRACKS_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setTracks("123", tracks, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceAcceptsData_whenUpdateTracks_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature tracks = getTracksFeature();

    Mockito.doNothing().when(fieldServiceMock).setTracks("123", tracks, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/tracks")
            .content(TRACKS_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isOk()).andExpect(content().json(TRACKS_JSON));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setTracks("123", tracks, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenUpdateTracks_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));

    final Feature tracks = getTracksFeature();

    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).setTracks("123", tracks, expectedAuthentication);

    this.mockMvc.perform(
        put("/api/v1/twin-hub/fields/123/geometries/tracks")
            .content(TRACKS_JSON)
            .contentType("application/json")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setTracks("123", tracks, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDoesNotThrow_whenDeleteTracks_thenNoContent() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doNothing().when(fieldServiceMock).deleteTracks("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/tracks")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNoContent());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteTracks("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenDeleteTracks_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).deleteTracks("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/tracks")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteTracks("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenDeleteTracks_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).deleteTracks("123", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/geometries/tracks")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteTracks("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesNoSoilMeasurementWorkOrders_whenGetSoilMeasurementWorkOrders_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrders("123", expectedAuthentication)).thenReturn(Optional.empty());
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrders("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesSoilMeasurementWorkOrder_whenGetSoilMeasurementWorkOrders_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    final SoilMeasurementWorkOrder soilMeasurementWorkOrder = new SoilMeasurementWorkOrder();
    soilMeasurementWorkOrder.setId("123456");
    soilMeasurementWorkOrder.setAssignee("test");
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrders("123", expectedAuthentication)).thenReturn(Optional.of(List.of(soilMeasurementWorkOrder)));
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isOk()).andExpect(content().json("[{\"id\":\"123456\",\"assignee\":\"test\"}]"));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrders("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenGetSoilMeasurementWorkOrders_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrders("123", expectedAuthentication)).thenThrow(ForbiddenException.class);
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrders("123", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenCreateSoilMeasurementWorkOrder_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    final SoilMeasurementWorkOrder soilMeasurementWorkOrder = new SoilMeasurementWorkOrder();
    soilMeasurementWorkOrder.setPropertiesToMeasure(Set.of("SOIL_MOISTURE"));
    soilMeasurementWorkOrder.setDistanceBetweenSamples(5);
    soilMeasurementWorkOrder.setAssignee("test");
    Mockito.when(fieldServiceMock.createSoilMeasurementWorkOrder("123", soilMeasurementWorkOrder, expectedAuthentication)).thenThrow(DoesNotExistException.class);
    this.mockMvc.perform(
        post("/api/v1/twin-hub/fields/123/soil-measurement-work-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"propertiesToMeasure\":[\"SOIL_MOISTURE\"],\"distanceBetweenSamples\":5,\"assignee\":\"test\"}")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).createSoilMeasurementWorkOrder("123", soilMeasurementWorkOrder, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceConsumesSoilMoistureWorkOrder_whenCreateSoilMeasurementWorkOrder_thenCreated() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    final SoilMeasurementWorkOrder soilMeasurementWorkOrder = new SoilMeasurementWorkOrder();
    soilMeasurementWorkOrder.setPropertiesToMeasure(Set.of("SOIL_MOISTURE"));
    soilMeasurementWorkOrder.setDistanceBetweenSamples(5);
    soilMeasurementWorkOrder.setAssignee("test");
    Mockito.when(fieldServiceMock.createSoilMeasurementWorkOrder("123", soilMeasurementWorkOrder, expectedAuthentication)).thenReturn(soilMeasurementWorkOrder);
    this.mockMvc.perform(
        post("/api/v1/twin-hub/fields/123/soil-measurement-work-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"propertiesToMeasure\":[\"SOIL_MOISTURE\"],\"distanceBetweenSamples\":5,\"assignee\":\"test\"}")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isCreated()).andExpect(content().json("{\"propertiesToMeasure\":[\"SOIL_MOISTURE\"],\"distanceBetweenSamples\":5,\"assignee\":\"test\"}"));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).createSoilMeasurementWorkOrder("123", soilMeasurementWorkOrder, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenCreateSoilMeasurementWorkOrder_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    final SoilMeasurementWorkOrder soilMeasurementWorkOrder = new SoilMeasurementWorkOrder();
    soilMeasurementWorkOrder.setPropertiesToMeasure(Set.of("SOIL_MOISTURE"));
    soilMeasurementWorkOrder.setDistanceBetweenSamples(5);
    soilMeasurementWorkOrder.setAssignee("test");
    Mockito.when(fieldServiceMock.createSoilMeasurementWorkOrder("123", soilMeasurementWorkOrder, expectedAuthentication)).thenThrow(ForbiddenException.class);
    this.mockMvc.perform(
        post("/api/v1/twin-hub/fields/123/soil-measurement-work-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"propertiesToMeasure\":[\"SOIL_MOISTURE\"],\"distanceBetweenSamples\":5,\"assignee\":\"test\"}")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).createSoilMeasurementWorkOrder("123", soilMeasurementWorkOrder, expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesNoSoilMeasurementWorkOrder_whenGetSoilMeasurementWorkOrder_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrder("123", "123456", expectedAuthentication)).thenReturn(Optional.empty());
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesSoilMeasurementWorkOrder_whenGetSoilMeasurementWorkOrder_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    final SoilMeasurementWorkOrder soilMeasurementWorkOrder = new SoilMeasurementWorkOrder();
    soilMeasurementWorkOrder.setId("123456");
    soilMeasurementWorkOrder.setPropertiesToMeasure(Set.of("SOIL_MOISTURE"));
    soilMeasurementWorkOrder.setDistanceBetweenSamples(5);
    soilMeasurementWorkOrder.setAssignee("test");
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrder("123", "123456", expectedAuthentication)).thenReturn(Optional.of(soilMeasurementWorkOrder));
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isOk()).andExpect(content().json("{\"id\":\"123456\",\"propertiesToMeasure\":[\"SOIL_MOISTURE\"],\"distanceBetweenSamples\":5,\"assignee\":\"test\"}"));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenGetSoilMeasurementWorkOrder_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrder("123", "123456", expectedAuthentication)).thenThrow(ForbiddenException.class);
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeletesSoilMeasurementWorkOrder_whenDeleteSoilMeasurementWorkOrder_thenNoContent() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doNothing().when(fieldServiceMock).deleteSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNoContent());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenDeleteSoilMeasurementWorkOrder_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).deleteSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenDeleteSoilMeasurementWorkOrder_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).deleteSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    this.mockMvc.perform(
        delete("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).deleteSoilMeasurementWorkOrder("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceStoresSoilMeasurementWorkOrderContent_whenSetSoilMeasurementWorkOrderContent_thenNoContent() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doNothing().when(fieldServiceMock).setSoilMeasurementWorkOrderFile(ArgumentMatchers.eq("123"), ArgumentMatchers.eq("123456"), ArgumentMatchers.any(TypedResource.class), ArgumentMatchers.eq(expectedAuthentication));

    final MockMultipartFile mockFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
    this.mockMvc.perform(
        multipart("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456/file")
            .file(mockFile)
            .with(r -> {
              r.setMethod("PUT");
              return r;
            })
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNoContent());
    final ArgumentCaptor<TypedResource> typedResourceArgumentCaptor = ArgumentCaptor.forClass(TypedResource.class);
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setSoilMeasurementWorkOrderFile(ArgumentMatchers.eq("123"), ArgumentMatchers.eq("123456"), typedResourceArgumentCaptor.capture(), ArgumentMatchers.eq(expectedAuthentication));
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
    final TypedResource capturedTypedResource = typedResourceArgumentCaptor.getValue();
    Assertions.assertNotNull(capturedTypedResource);
    Assertions.assertEquals("text/plain", capturedTypedResource.getMediaType());
    Assertions.assertEquals("filename.txt", capturedTypedResource.getResource().getFilename());
    Assertions.assertArrayEquals("some xml".getBytes(), capturedTypedResource.getResource().getInputStream().readAllBytes());
  }

  @Test
  void givenServiceThrowsDoesNotExistException_whenSetSoilMeasurementWorkOrderContent_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(DoesNotExistException.class).when(fieldServiceMock).setSoilMeasurementWorkOrderFile(ArgumentMatchers.eq("123"), ArgumentMatchers.eq("123456"), ArgumentMatchers.any(TypedResource.class), ArgumentMatchers.eq(expectedAuthentication));

    final MockMultipartFile mockFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
    this.mockMvc.perform(
        multipart("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456/file")
            .file(mockFile)
            .with(r -> {
              r.setMethod("PUT");
              return r;
            })
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setSoilMeasurementWorkOrderFile(ArgumentMatchers.eq("123"), ArgumentMatchers.eq("123456"), ArgumentMatchers.any(TypedResource.class), ArgumentMatchers.eq(expectedAuthentication));
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenSetSoilMeasurementWorkOrderContent_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:write"));
    Mockito.doThrow(ForbiddenException.class).when(fieldServiceMock).setSoilMeasurementWorkOrderFile(ArgumentMatchers.eq("123"), ArgumentMatchers.eq("123456"), ArgumentMatchers.any(TypedResource.class), ArgumentMatchers.eq(expectedAuthentication));

    final MockMultipartFile mockFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
    this.mockMvc.perform(
        multipart("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456/file")
            .file(mockFile)
            .with(r -> {
              r.setMethod("PUT");
              return r;
            })
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:write"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).setSoilMeasurementWorkOrderFile(ArgumentMatchers.eq("123"), ArgumentMatchers.eq("123456"), ArgumentMatchers.any(TypedResource.class), ArgumentMatchers.eq(expectedAuthentication));
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }


  @Test
  void givenServiceProvidesNoSoilMeasurementWorkOrderContent_whenGetSoilMeasurementWorkOrderContent_thenNotFound() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrderFile("123", "123456", expectedAuthentication)).thenReturn(Optional.empty());

    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456/file")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isNotFound());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrderFile("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceProvidesSoilMeasurementWorkOrderContent_whenGetSoilMeasurementWorkOrderContent_thenOk() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    final MockMultipartFile mockFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
    final TypedResource typedResource = TypedResource.of(mockFile.getResource(), "text/plain");

    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrderFile("123", "123456", expectedAuthentication)).thenReturn(Optional.of(typedResource));
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456/file")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isOk()).andExpect(content().bytes("some xml".getBytes()));
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrderFile("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }

  @Test
  void givenServiceDeniesAccess_whenGetSoilMeasurementWorkOrderContent_thenForbidden() throws Exception {
    final Authentication expectedAuthentication = Authentication.of(RequestorType.CLIENT_USING_USER_ACCOUNT, "user-123", "test-client", List.of("SCOPE_urn:ads:twin-hub:twins:read"));
    Mockito.when(fieldServiceMock.getSoilMeasurementWorkOrderFile("123", "123456", expectedAuthentication)).thenThrow(ForbiddenException.class);
    this.mockMvc.perform(
        get("/api/v1/twin-hub/fields/123/soil-measurement-work-orders/123456/file")
            .with(
                jwt().jwt(builder -> builder.subject("user-123").claim(IdTokenClaimNames.AZP, "test-client")).authorities(new SimpleGrantedAuthority("SCOPE_urn:ads:twin-hub:twins:read"))
            )
    ).andExpect(status().isForbidden());
    Mockito.verify(fieldServiceMock, Mockito.times(1)).getSoilMeasurementWorkOrderFile("123", "123456", expectedAuthentication);
    Mockito.verifyNoMoreInteractions(fieldServiceMock);
  }


}
