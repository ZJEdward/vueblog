package com.markerhub.shiro;

import com.markerhub.entity.User;
import com.markerhub.service.UserService;
import com.markerhub.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Edward
 * @create 2021-04-28 1:57 下午
 */

@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken token = (JwtToken) authenticationToken;

        String userId = jwtUtils.getClaimByToken((String) token.getPrincipal()).getSubject();
        User user = userService.getById(userId);

        if (user == null) throw new UnknownAccountException("账户不存在");

        if (user.getStatus() == -1) throw  new LockedAccountException("账户已锁定");

        AccountProfile profile = new AccountProfile();
        BeanUtils.copyProperties(user, profile);

        System.out.println("---------------");
        return new SimpleAuthenticationInfo(profile, token.getCredentials(), getName());
    }
}
