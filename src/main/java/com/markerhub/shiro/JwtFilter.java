package com.markerhub.shiro;

import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.markerhub.common.lang.Result;
import com.markerhub.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Edward
 * @create 2021-04-28 2:09 下午
 */

@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) return null;
        return new JwtToken(authorization);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServerResponse httpServerResponse = (HttpServerResponse) response;

        Throwable throwable = e.getCause() == null ? e : e.getCause();
        Result result = Result.fail(throwable.getMessage());
        String str = JSONUtil.toJsonStr(result);

        httpServerResponse.getWriter().print(str);

        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) return true; // 没有jwt 不拦截

        // 校验jwt
        Claims claimByToken = jwtUtils.getClaimByToken(authorization);
        if (claimByToken == null || jwtUtils.isTokenExpired(claimByToken.getExpiration())) {
            throw new ExpiredCredentialsException("token 失效 请重新登陆");
        }
        // 执行登陆
        return executeLogin(servletRequest, servletResponse);
    }
}
