package com.lucasalgus.utils;

import java.util.Arrays;

public class MessageUtils {
    public static String getTokenFromRequest(String request) {
        var token = request.split(";")[1];

        return token;
    }

    public static String getHeaderFromRequest(String request) {
        var token = request.split(";")[0];

        return token;
    }

    public static String[] getVarsFromRequest(String request) {
        var filteredRequestString = request.replaceAll(";;", ";");
        var requestArray = filteredRequestString.split(";");
        var vars = Arrays.copyOfRange(requestArray, 2, requestArray.length);

        return vars;
    }

    public static String getIdentifierFromRequest(String request) {
        var identifier = request.split(";")[0];
        var identifierArray = identifier.split(":");

        if (identifierArray.length > 1) {
            return identifierArray[0];
        }

        return identifierArray[0];
    }

    public static String getStatusFromRequest(String request) {
        var identifier = request.split(";")[0];
        var identifierArray = identifier.split(":");

        if (identifierArray.length > 1) {
            return identifierArray[1];
        }

        return null;
    }
}
