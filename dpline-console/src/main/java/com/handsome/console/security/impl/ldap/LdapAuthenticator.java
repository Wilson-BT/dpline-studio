
package com.handsome.console.security.impl.ldap;


import com.handsome.dao.entity.User;
import com.handsome.console.security.impl.AbstractAuthenticator;
import com.handsome.console.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;

public class LdapAuthenticator extends AbstractAuthenticator {

    @Autowired
    private UsersService usersService;

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
                user = usersService.createUser(ldapService.getUserType(userCode), userCode, ldapEmail,0);
            }
        }
        return user;
    }
}
