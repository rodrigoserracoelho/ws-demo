package io.surisoft.demo.ws.security;

import io.surisoft.demo.ws.exception.WebApplicationSecurityException;

public interface Authorization {
    public boolean isAuthorized(String authorization);
    public String getAuthorization(Object object) throws WebApplicationSecurityException;
    public String getClaims(Object object) throws WebApplicationSecurityException;
}
