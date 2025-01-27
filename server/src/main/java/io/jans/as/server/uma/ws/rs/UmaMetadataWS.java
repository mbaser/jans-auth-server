/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.as.server.uma.ws.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import io.jans.as.model.common.GrantType;
import io.jans.as.model.common.ResponseType;
import io.jans.as.model.configuration.AppConfiguration;
import io.jans.as.model.error.ErrorResponseFactory;
import io.jans.as.model.uma.UmaConstants;
import io.jans.as.model.uma.UmaErrorResponseType;
import io.jans.as.model.uma.UmaMetadata;
import io.jans.as.server.util.ServerUtil;

/**
 * The endpoint at which the requester can obtain UMA2 metadata.
 */
@Path("/uma2-configuration")
public class UmaMetadataWS {

    public static final String UMA_SCOPES_SUFFIX = "/uma/scopes";
    public static final String UMA_CLAIMS_GATHERING_PATH = "/uma/gather_claims";

    @Inject
    private Logger log;

    @Inject
    private ErrorResponseFactory errorResponseFactory;

    @Inject
    private AppConfiguration appConfiguration;

    @GET
    @Produces({UmaConstants.JSON_MEDIA_TYPE})
    public Response getConfiguration() {
        try {
            final String baseEndpointUri = appConfiguration.getBaseEndpoint();

            final UmaMetadata c = new UmaMetadata();
            c.setIssuer(appConfiguration.getIssuer());
            c.setGrantTypesSupported(new String[]{
                    GrantType.AUTHORIZATION_CODE.getValue(),
                    GrantType.IMPLICIT.getValue(),
                    GrantType.CLIENT_CREDENTIALS.getValue(),
                    GrantType.OXAUTH_UMA_TICKET.getValue()
            });
            c.setResponseTypesSupported(new String[]{
                    ResponseType.CODE.getValue(), ResponseType.ID_TOKEN.getValue(), ResponseType.TOKEN.getValue()
            });
            c.setTokenEndpointAuthMethodsSupported(appConfiguration.getTokenEndpointAuthMethodsSupported().toArray(new String[appConfiguration.getTokenEndpointAuthMethodsSupported().size()]));
            c.setTokenEndpointAuthSigningAlgValuesSupported(appConfiguration.getTokenEndpointAuthSigningAlgValuesSupported().toArray(new String[appConfiguration.getTokenEndpointAuthSigningAlgValuesSupported().size()]));
            c.setUiLocalesSupported(appConfiguration.getUiLocalesSupported().toArray(new String[appConfiguration.getUiLocalesSupported().size()]));
            c.setOpTosUri(appConfiguration.getOpTosUri());
            c.setOpPolicyUri(appConfiguration.getOpPolicyUri());
            c.setJwksUri(appConfiguration.getJwksUri());
            c.setServiceDocumentation(appConfiguration.getServiceDocumentation());

            c.setUmaProfilesSupported(new String[0]);
            c.setRegistrationEndpoint(appConfiguration.getRegistrationEndpoint());
            c.setTokenEndpoint(appConfiguration.getTokenEndpoint());
            c.setAuthorizationEndpoint(appConfiguration.getAuthorizationEndpoint());
            c.setIntrospectionEndpoint(baseEndpointUri + "/rpt/status");
            c.setResourceRegistrationEndpoint(baseEndpointUri + "/host/rsrc/resource_set");
            c.setPermissionEndpoint(baseEndpointUri + "/host/rsrc_pr");
            c.setScopeEndpoint(baseEndpointUri + UMA_SCOPES_SUFFIX);
            c.setClaimsInteractionEndpoint(baseEndpointUri + UMA_CLAIMS_GATHERING_PATH);

            // convert manually to avoid possible conflicts between resteasy providers, e.g. jettison, jackson
            final String entity = ServerUtil.asPrettyJson(c);
            log.trace("Uma metadata: {}", entity);

            return Response.ok(entity).build();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw errorResponseFactory.createWebApplicationException(Response.Status.INTERNAL_SERVER_ERROR, UmaErrorResponseType.SERVER_ERROR, "Internal error.");
        }
    }

}
