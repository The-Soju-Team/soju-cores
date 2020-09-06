/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.webservice.jersey;

import java.net.URI;

/**
 * @author hiendm1
 */
public class LocalizationMessages {
    public static String ERROR_CONTAINER_EXCEPTION_IO() {
        return "IOException thrown when creating the JDK HttpServer.";
    }

    public static String ERROR_CONTAINER_URI_NULL() {
        return "The URI must not be null.";
    }

    public static String ERROR_CONTAINER_URI_PATH_EMPTY(URI uri) {
        return "The URI path, of the URI " + uri + " must be present (not an empty string).";
    }

    public static String ERROR_CONTAINER_URI_PATH_NULL(URI uri) {
        return "The URI path, of the URI " + uri + " must be non-null.";
    }

    public static String ERROR_CONTAINER_URI_PATH_START(URI uri) {
        return "The URI path, of the URI " + uri + " must start with a '/'.";
    }

    public static String ERROR_CONTAINER_URI_SCHEME_UNKNOWN(URI uri) {
        return "The URI scheme, of the URI " + uri + " must be equal (ignoring case) to 'http' or 'https'.";
    }

    public static String ERROR_CONTAINER_HTTPS_NO_SSL() {
        return "Attempt to start a HTTPS server with no SSL context defined.";
    }

    public static String ERROR_RESPONSEWRITER_RESPONSE_UNCOMMITED() {
        return "ResponseWriter was not commited yet. Committing the Response now.";
    }

    public static String ERROR_RESPONSEWRITER_SENDING_FAILURE_RESPONSE() {
        return "Unable to send a failure response.";
    }

    public static String ERROR_RESPONSEWRITER_WRITING_HEADERS() {
        return "Error writing out the response headers.";
    }

    public static String INFO_CONTAINER_HTTPS_NO_SSL() {
        return "HTTPS server will be created with no SSL context defined. HttpsConfigurator must be set before the server is started.";
    }

    public static String WARNING_CONTAINER_URI_SCHEME_SECURED() {
        return "SSLContext is set, but http scheme was used instead of https. The SSLContext will be ignored.";
    }
}
