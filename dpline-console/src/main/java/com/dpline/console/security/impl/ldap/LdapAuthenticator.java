
package com.dpline.console.security.impl.ldap;


import com.dpline.console.security.impl.AbstractAuthenticator;
import com.dpline.console.service.impl.UsersServiceImpl;
import com.dpline.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

public class LdapAuthenticator extends AbstractAuthenticator {

    @Autowired
    private UsersServiceImpl usersService;

    @Autowired
    LdapService ldapService;

    @Override
    public User login(String userCode, String password, String extra) {
        User user = null;
        String ldapEmail = ldapService.ldapLogin(userCode, password);
        if (ldapEmail != null) {
            //check if user exist
            user = usersService.getUserByUserCode(userCode);
            if (user == null) {
                user = usersService.createUser(userCode, ldapEmail,ldapService.getUserType(userCode).getCode());
            }
        }
        return user;
    }
}
