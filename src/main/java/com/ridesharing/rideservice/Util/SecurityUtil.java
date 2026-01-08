package com.ridesharing.rideservice.Util;

import org.springframework.security.core.Authentication;

public final class SecurityUtil {

    public static Long getUserId(Authentication auth){
        return Long.parseLong(auth.getName);
    }
}
