package com.markerhub.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author Edward
 * @create 2021-04-28 2:17 下午
 */
public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
