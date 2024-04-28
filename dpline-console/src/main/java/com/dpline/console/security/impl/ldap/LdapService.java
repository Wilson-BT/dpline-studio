package com.dpline.console.security.impl.ldap;

import com.dpline.common.enums.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Properties;

@Component
@Configuration
public class LdapService {
    private static final Logger logger = LoggerFactory.getLogger(LdapService.class);

    @Value("${security.authentication.ldap.user.admin:null}")
    private String adminUserId;

    @Value("${ldap.urls:null}")
    private String ldapUrls;

    @Value("${ldap.base.dn:null}")
    private String ldapBaseDn;

    @Value("${ldap.username:null}")
    private String ldapSecurityPrincipal;

    @Value("${ldap.password:null}")
    private String ldapPrincipalPassword;

    @Value("${ldap.user.identity.attribute:null}")
    private String ldapUserIdentifyingAttribute;

    @Value("${ldap.user.email.attribute:null}")
    private String ldapEmailAttribute;

    /***
     * get user type by configured admin userId
     * @param userCode login userId
     * @return user type
     */
    public UserType getUserType(String userCode) {
        return adminUserId.equalsIgnoreCase(userCode) ? UserType.ADMIN_USER : UserType.GENERAL_USER;
    }

    /**
     * login by userId and return user email
     *
     * @param userName user identity id
     * @param userPwd user login password
     * @return user email
     */
    public String ldapLogin(String userName, String userPwd) {
        Properties searchEnv = getManagerLdapEnv();
        try {
            // Connect to the LDAP server and Authenticate with a service user of whom we know the DN and credentials
            LdapContext ctx = new InitialLdapContext(searchEnv, null);
            SearchControls sc = new SearchControls();
            sc.setReturningAttributes(new String[]{ldapEmailAttribute});
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchFilter = String.format("(%s=%s)", ldapUserIdentifyingAttribute, userName);
            //Search for the user you want to authenticate, search him with some attribute
            NamingEnumeration<SearchResult> results = ctx.search(ldapBaseDn, searchFilter, sc);
            if (results.hasMore()) {
                // get the users DN (distinguishedName) from the result
                SearchResult result = results.next();
                NamingEnumeration attrs = result.getAttributes().getAll();
                while (attrs.hasMore()) {
                    //Open another connection to the LDAP server with the found DN and the password
                    searchEnv.put(Context.SECURITY_PRINCIPAL, result.getNameInNamespace());
                    searchEnv.put(Context.SECURITY_CREDENTIALS, userPwd);
                    try {
                        new InitialDirContext(searchEnv);
                    } catch (Exception e) {
                        logger.warn("invalid ldap credentials or ldap search error", e);
                        return null;
                    }
                    Attribute attr = (Attribute) attrs.next();
                    if (attr.getID().equals(ldapEmailAttribute)) {
                        return (String) attr.get();
                    }
                }
            }
        } catch (NamingException e) {
            logger.error("ldap search error", e);
            return null;
        }
        return null;
    }

    /***
     * get ldap env fot ldap server search
     * @return Properties
     */
    Properties getManagerLdapEnv() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapSecurityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, ldapPrincipalPassword);
        env.put(Context.PROVIDER_URL, ldapUrls);
        return env;
    }
}
