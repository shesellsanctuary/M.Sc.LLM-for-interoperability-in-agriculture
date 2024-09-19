package de.fraunhofer.iese.cognac.ads.ads_platform.configuration;

import org.springframework.context.annotation.Configuration;

@Configuration
//@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {
//  private static final String DEFAULT_CLIENT_ID = "swagger";
//  private final OAuth2ResourceServerProperties.Jwt jwtProperties;
//  private final TypeResolver typeResolver;
//
//  @Autowired
//  public SwaggerConfig(OAuth2ResourceServerProperties properties, TypeResolver typeResolver) {
//    this.jwtProperties = properties.getJwt();
//    this.typeResolver = typeResolver;
//  }
//
//  @Bean
//  public Docket api() {
//    final Docket docket = new Docket(DocumentationType.SWAGGER_2)
//        .apiInfo(apiInfo())
//        .select()
//        .apis(RequestHandlerSelectors.basePackage("de.fraunhofer.iese.cognac.ads.ads_platform"))
//        .paths(PathSelectors.any())
//        .build();
//    docket.ignoredParameterTypes(JwtAuthenticationToken.class, Authentication.class);
//    docket.directModelSubstitute(Duration.class, String.class);
//    docket.alternateTypeRules(
//        AlternateTypeRules.newRule(
//            typeResolver.resolve(ResponseEntity.class, Resource.class),
//            typeResolver.resolve(MultipartFile.class),
//            AlternateTypeRules.DIRECT_SUBSTITUTION_RULE_ORDER
//        )
//    );
//    docket.securitySchemes(Arrays.asList(authorizationCode(), clientCredentials()));
//    docket.securityContexts(
//        Arrays.asList(
//            securityContextDefault(),
//            securityContextConnectorConnectorCredentialsManage(),
//            securityContextTwinHubTwinsManage(),
//            securityContextTwinHubTwinsWrite(),
//            securityContextTwinHubTwinsRead(),
//            securityContextTwinHubDataSovereigntyManage(),
//            securityContextTwinHubDataSovereigntyRequestConsent()
//        )
//    );
//    return docket;
//  }
//
//  private ApiInfo apiInfo() {
//    return new ApiInfo(
//        "Api Documentation - ADS-Platform",
//        "Api Documentation - ADS-Platform",
//        "1.0",
//        "urn:tos",
//        new Contact("", "", ""),
//        "MIT",
//        "https://opensource.org/licenses/MIT",
//        new ArrayList<>());
//  }
//
//  SecurityScheme authorizationCode() {
//    return new OAuthBuilder()
//        .name("authorization-code")
//        .scopes(scopes())
//        .grantTypes(grantTypesAuthorizationCode())
//        .build();
//  }
//
//  SecurityScheme clientCredentials() {
//    return new OAuthBuilder()
//        .name("client-credentials")
//        .scopes(scopes())
//        .grantTypes(grantTypesClientCredentials())
//        .build();
//  }
//
//  private List<AuthorizationScope> scopes() {
//    return Arrays.stream(Scope.values())
//        .map(scope -> new AuthorizationScope(scope.getUrn(), scope.getDescription()))
//        .collect(Collectors.toList());
//  }
//
//  private SecurityContext securityContextDefault() {
//    final Predicate<String> apiPathPredicate = PathSelectors.ant("/api/**");
//    final Predicate<String> connectorAccessPathPredicate = PathSelectors.ant("/api/*/connector/access/**");
//    return SecurityContext.builder()
//        .securityReferences(twinHubTwinsReadAuth())
//        .operationSelector(operationContext -> (
//                (
//                    apiPathPredicate.test(operationContext.requestMappingPattern())
//                )
//                    && !(
//                    connectorAccessPathPredicate.test(operationContext.requestMappingPattern())
//                )
//            )
//        )
//        .build();
//  }
//
//  private SecurityContext securityContextTwinHubTwinsRead() {
//    final Predicate<String> twinCollectionPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields");
//    final Predicate<String> twinEntityPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields/?*");
//    final Predicate<String> twinEntityContentPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields/?*/?*/**");
//    return SecurityContext.builder()
//        .securityReferences(twinHubTwinsReadAuth())
//        .operationSelector(operationContext -> (
//                (
//                    HttpMethod.GET.equals(operationContext.httpMethod())
//                )
//                    && (
//                    twinCollectionPathPredicate.test(operationContext.requestMappingPattern())
//                        || twinEntityPathPredicate.test(operationContext.requestMappingPattern())
//                        || twinEntityContentPathPredicate.test(operationContext.requestMappingPattern())
//                )
//            )
//        )
//        .build();
//  }
//
//  private SecurityContext securityContextTwinHubTwinsWrite() {
//    final Predicate<String> twinCollectionPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields");
//    final Predicate<String> twinEntityPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields/?*");
//    final Predicate<String> twinEntityContentPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields/?*/?*/**");
//    return SecurityContext.builder()
//        .securityReferences(twinHubTwinsWriteAuth())
//        .operationSelector(operationContext -> (
//                (
//                    HttpMethod.POST.equals(operationContext.httpMethod())
//                        || HttpMethod.PUT.equals(operationContext.httpMethod())
//                        || HttpMethod.PATCH.equals(operationContext.httpMethod())
//                        || HttpMethod.DELETE.equals(operationContext.httpMethod())
//                )
//                    && (
//                    twinEntityContentPathPredicate.test(operationContext.requestMappingPattern())
//                )
//            )
//        )
//        .build();
//  }
//
//  private SecurityContext securityContextConnectorConnectorCredentialsManage() {
//    final Predicate<String> connectorCredentialsPathPredicate = PathSelectors.ant("/api/*/connector/credentials");
//    return SecurityContext.builder()
//        .securityReferences(connectorConnectorCredentialsManageAuth())
//        .operationSelector(operationContext -> (
//                connectorCredentialsPathPredicate.test(operationContext.requestMappingPattern())
//            )
//        )
//        .build();
//  }
//
//  private SecurityContext securityContextTwinHubTwinsManage() {
//    final Predicate<String> twinCollectionPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields");
//    final Predicate<String> twinEntityPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields/?*");
//    final Predicate<String> twinEntityContentPathPredicate = PathSelectors.ant("/api/*/twin-hub/fields/?*/?*/**");
//    return SecurityContext.builder()
//        .securityReferences(twinHubTwinsManageAuth())
//        .operationSelector(operationContext -> (
//                (
//                    HttpMethod.POST.equals(operationContext.httpMethod())
//                        || HttpMethod.PUT.equals(operationContext.httpMethod())
//                        || HttpMethod.PATCH.equals(operationContext.httpMethod())
//                        || HttpMethod.DELETE.equals(operationContext.httpMethod())
//                )
//                    && (
//                    twinCollectionPathPredicate.test(operationContext.requestMappingPattern())
//                        || twinEntityPathPredicate.test(operationContext.requestMappingPattern())
//                )
//            )
//        )
//        .build();
//  }
//
//  private SecurityContext securityContextTwinHubDataSovereigntyManage() {
//    final Predicate<String> accessLogsPathPredicate = PathSelectors.ant("/api/*/twin-hub/access-logs");
//    final Predicate<String> consentsPathPredicate = PathSelectors.ant("/api/*/twin-hub/consents/**");
//    final Predicate<String> consentRequestsPathPredicate = PathSelectors.ant("/api/*/twin-hub/consent-requests/**");
//    return SecurityContext.builder()
//        .securityReferences(twinHubDataSovereigntyManageAuth())
//        .operationSelector(operationContext -> (
//                (
//                    HttpMethod.POST.equals(operationContext.httpMethod())
//                        || HttpMethod.GET.equals(operationContext.httpMethod())
//                )
//                    && (
//                    accessLogsPathPredicate.test(operationContext.requestMappingPattern())
//                        || consentsPathPredicate.test(operationContext.requestMappingPattern())
//                        || consentRequestsPathPredicate.test(operationContext.requestMappingPattern())
//                )
//            )
//        )
//        .build();
//  }
//
//  private SecurityContext securityContextTwinHubDataSovereigntyRequestConsent() {
//    final Predicate<String> createConsentRequestPathPredicate = PathSelectors.ant("/api/*/twin-hub/consent-requests");
//    return SecurityContext.builder()
//        .securityReferences(twinHubDataSovereigntyRequestConsentAuth())
//        .operationSelector(operationContext -> (
//                (
//                    HttpMethod.POST.equals(operationContext.httpMethod())
//                )
//                    && (
//                    createConsentRequestPathPredicate.test(operationContext.requestMappingPattern())
//                )
//            )
//        )
//        .build();
//  }
//
//  private List<SecurityReference> twinHubTwinsReadAuth() {
//    AuthorizationScope authorizationScope
//        = new AuthorizationScope(Scope.TWIN_HUB_TWINS_READ.getUrn(), Scope.TWIN_HUB_TWINS_READ.getDescription());
//    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//    authorizationScopes[0] = authorizationScope;
//    return Arrays.asList(
//        new SecurityReference("authorization-code", authorizationScopes),
//        new SecurityReference("client-credentials", authorizationScopes)
//    );
//  }
//
//  private List<SecurityReference> twinHubTwinsWriteAuth() {
//    AuthorizationScope authorizationScope
//        = new AuthorizationScope(Scope.TWIN_HUB_TWINS_WRITE.getUrn(), Scope.TWIN_HUB_TWINS_WRITE.getDescription());
//    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//    authorizationScopes[0] = authorizationScope;
//    return Arrays.asList(
//        new SecurityReference("authorization-code", authorizationScopes),
//        new SecurityReference("client-credentials", authorizationScopes)
//    );
//  }
//
//  private List<SecurityReference> twinHubTwinsManageAuth() {
//    AuthorizationScope authorizationScope
//        = new AuthorizationScope(Scope.TWIN_HUB_TWINS_MANAGE.getUrn(), Scope.TWIN_HUB_TWINS_MANAGE.getDescription());
//    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//    authorizationScopes[0] = authorizationScope;
//    return Arrays.asList(
//        new SecurityReference("authorization-code", authorizationScopes),
//        new SecurityReference("client-credentials", authorizationScopes)
//    );
//  }
//
//  private List<SecurityReference> connectorConnectorCredentialsManageAuth() {
//    AuthorizationScope authorizationScope
//        = new AuthorizationScope(Scope.CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE.getUrn(), Scope.CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE.getDescription());
//    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//    authorizationScopes[0] = authorizationScope;
//    return Arrays.asList(
//        new SecurityReference("authorization-code", authorizationScopes)
//    );
//  }
//
//  private List<SecurityReference> twinHubDataSovereigntyManageAuth() {
//    AuthorizationScope authorizationScope
//        = new AuthorizationScope(Scope.TWIN_HUB_DATA_SOVEREIGNTY_MANAGE.getUrn(), Scope.TWIN_HUB_DATA_SOVEREIGNTY_MANAGE.getDescription());
//    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//    authorizationScopes[0] = authorizationScope;
//    return Arrays.asList(
//        new SecurityReference("authorization-code", authorizationScopes),
//        new SecurityReference("client-credentials", authorizationScopes)
//    );
//  }
//
//  private List<SecurityReference> twinHubDataSovereigntyRequestConsentAuth() {
//    AuthorizationScope authorizationScope
//        = new AuthorizationScope(Scope.TWIN_HUB_DATA_SOVEREIGNTY_REQUEST_CONSENT.getUrn(), Scope.TWIN_HUB_DATA_SOVEREIGNTY_REQUEST_CONSENT.getDescription());
//    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//    authorizationScopes[0] = authorizationScope;
//    return Arrays.asList(
//        new SecurityReference("authorization-code", authorizationScopes),
//        new SecurityReference("client-credentials", authorizationScopes)
//    );
//  }
//
//  List<GrantType> grantTypesAuthorizationCode() {
//    List<GrantType> grantTypes = new ArrayList<>();
//    TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint(this.jwtProperties.getIssuerUri() + "/protocol/openid-connect/auth", SwaggerConfig.DEFAULT_CLIENT_ID, null);
//    TokenEndpoint tokenEndpoint = new TokenEndpoint(this.jwtProperties.getIssuerUri() + "/protocol/openid-connect/token", "token");
//    grantTypes.add(new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint));
//    return grantTypes;
//  }
//
//  List<GrantType> grantTypesClientCredentials() {
//    List<GrantType> grantTypes = new ArrayList<>();
//    grantTypes.add(new ClientCredentialsGrant(this.jwtProperties.getIssuerUri() + "/protocol/openid-connect/token"));
//    return grantTypes;
//  }
//
//  @Bean
//  public SecurityConfiguration security() {
//    return SecurityConfigurationBuilder.builder().clientId(DEFAULT_CLIENT_ID).build();
//  }
//
//  @Getter
//  @ToString
//  public enum Scope {
//    TWIN_HUB_TWINS_READ(OAuthScopes.TWIN_HUB_TWINS_READ, "Read Twin Data"),
//    TWIN_HUB_TWINS_WRITE(OAuthScopes.TWIN_HUB_TWINS_WRITE, "Modify Twin Data"),
//    TWIN_HUB_TWINS_MANAGE(OAuthScopes.TWIN_HUB_TWINS_MANAGE, "Manage Twin Entities"),
//    TWIN_HUB_DATA_SOVEREIGNTY_REQUEST_CONSENT(OAuthScopes.TWIN_HUB_DATA_SOVEREIGNTY_REQUEST_CONSENT, "Request Consent to access Twins"),
//    TWIN_HUB_DATA_SOVEREIGNTY_MANAGE(OAuthScopes.TWIN_HUB_DATA_SOVEREIGNTY_MANAGE, "Exercise Data Sovereignty"),
//    CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE(OAuthScopes.CONNECTOR_CONNECTOR_CREDENTIALS_MANAGE, "Manage Connector Credentials"),
//    ML_HUB_LABELED_DATA_READ(OAuthScopes.ML_HUB_LABELED_DATA_READ, "Read Labeled Data"),
//    ML_HUB_LABELED_DATA_WRITE(OAuthScopes.ML_HUB_LABELED_DATA_WRITE, "Write Labeled Data"),
//    ML_HUB_ML_MODELS_READ(OAuthScopes.ML_HUB_ML_MODELS_READ, "Read ML Models"),
//    ML_HUB_ML_MODELS_WRITE(OAuthScopes.ML_HUB_ML_MODELS_WRITE, "Write ML Models"),
//    ;
//
//    private final String urn;
//    private final String description;
//
//
//    Scope(String urn, String description) {
//      this.urn = urn;
//      this.description = description;
//    }
//
//  }
}
