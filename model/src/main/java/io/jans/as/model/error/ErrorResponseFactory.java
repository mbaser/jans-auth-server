/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.as.model.error;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.jans.as.model.authorize.AuthorizeErrorResponseType;
import io.jans.as.model.ciba.BackchannelAuthenticationErrorResponseType;
import io.jans.as.model.clientinfo.ClientInfoErrorResponseType;
import io.jans.as.model.configuration.AppConfiguration;
import io.jans.as.model.configuration.Configuration;
import io.jans.as.model.fido.u2f.U2fErrorResponseType;
import io.jans.as.model.register.RegisterErrorResponseType;
import io.jans.as.model.session.EndSessionErrorResponseType;
import io.jans.as.model.token.TokenErrorResponseType;
import io.jans.as.model.token.TokenRevocationErrorResponseType;
import io.jans.as.model.uma.UmaErrorResponseType;
import io.jans.as.model.userinfo.UserInfoErrorResponseType;
import io.jans.as.model.util.Util;

/**
 * Provides an easy way to get Error responses based in an error response type
 *
 * @author Yuriy Zabrovarnyy
 * @author Javier Rojas Blum
 * @author Yuriy Movchan
 * @version August 20, 2019
 */
public class ErrorResponseFactory implements Configuration {

    private static Logger log = LoggerFactory.getLogger(ErrorResponseFactory.class);

    private ErrorMessages messages;
    private AppConfiguration appConfiguration;

    public ErrorResponseFactory() {
    }

    public ErrorResponseFactory(ErrorMessages messages, AppConfiguration appConfiguration) {
        this.messages = messages;
        this.appConfiguration = appConfiguration;
    }

    public ErrorMessages getMessages() {
        return messages;
    }

    public void setMessages(ErrorMessages p_messages) {
        messages = p_messages;
    }

    /**
     * Looks for an error message.
     *
     * @param p_list error list
     * @param type   The type of the error.
     * @return Error message or <code>null</code> if not found.
     */
    private ErrorMessage getError(List<ErrorMessage> p_list, IErrorType type) {
        log.debug("Looking for the error with id: {}", type);

        if (p_list != null) {
            for (ErrorMessage error : p_list) {
                if (error.getId().equals(type.getParameter())) {
                    log.debug("Found error, id: {}", type);
                    return error;
                }
            }
        }

        log.error("Error not found, id: {}", type);
        return new ErrorMessage(type.getParameter(), type.getParameter(), null);
    }

    public String getErrorAsJson(IErrorType p_type) {
        return getErrorResponse(p_type).toJSonString();
    }

    public String errorAsJson(IErrorType p_type, String reason) {
        final DefaultErrorResponse error = getErrorResponse(p_type);
        error.setReason(appConfiguration.getErrorReasonEnabled() ? reason : "");
        return error.toJSonString();
    }

    public WebApplicationException createWebApplicationException(Response.Status status, IErrorType type, String reason) throws WebApplicationException {
        return new WebApplicationException(Response
                .status(status)
                .entity(errorAsJson(type, reason))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build());
    }

    public String getErrorAsJson(IErrorType p_type, String p_state, String reason) {
        return getErrorResponse(p_type, p_state, reason).toJSonString();
    }

    public String getErrorAsQueryString(IErrorType p_type, String p_state) {
        return getErrorAsQueryString(p_type, p_state, "");
    }

    public String getErrorAsQueryString(IErrorType p_type, String p_state, String reason) {
        return getErrorResponse(p_type, p_state, reason).toQueryString();
    }

    public DefaultErrorResponse getErrorResponse(IErrorType type, String p_state, String reason) {
        final DefaultErrorResponse response = getErrorResponse(type);
        response.setState(p_state);
        response.setReason(reason);
        return response;
    }

    public DefaultErrorResponse getErrorResponse(IErrorType type) {
        final DefaultErrorResponse response = new DefaultErrorResponse();
        response.setType(type);

        if (type != null && messages != null) {
            List<ErrorMessage> list = null;
            if (type instanceof AuthorizeErrorResponseType) {
                list = messages.getAuthorize();
            } else if (type instanceof ClientInfoErrorResponseType) {
                list = messages.getClientInfo();
            } else if (type instanceof EndSessionErrorResponseType) {
                list = messages.getEndSession();
            } else if (type instanceof RegisterErrorResponseType) {
                list = messages.getRegister();
            } else if (type instanceof TokenErrorResponseType) {
                list = messages.getToken();
            } else if (type instanceof TokenRevocationErrorResponseType) {
                list = messages.getRevoke();
            } else if (type instanceof UmaErrorResponseType) {
                list = messages.getUma();
            } else if (type instanceof UserInfoErrorResponseType) {
                list = messages.getUserInfo();
            } else if (type instanceof U2fErrorResponseType) {
                list = messages.getFido();
            } else if (type instanceof BackchannelAuthenticationErrorResponseType) {
                list = messages.getBackchannelAuthentication();
            }

            if (list != null) {
                final ErrorMessage m = getError(list, type);
                response.setErrorDescription(m.getDescription());
                response.setErrorUri(m.getUri());
            }
        }

        return response;
    }

    public String getJsonErrorResponse(IErrorType type) {
        final DefaultErrorResponse response = getErrorResponse(type);

        JsonErrorResponse jsonErrorResponse = new JsonErrorResponse(response);

        try {
            final ObjectMapper mapper = Util.createJsonMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            return mapper.writeValueAsString(jsonErrorResponse);
        } catch (IOException ex) {
            log.error("Failed to generate error response", ex);
            return null;
        }
    }

}