package com.hza.ssm.filter.authc;

import com.hza.ssm.util.cache.RedisCache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

public class KickoutSessionControlFilter extends AccessControlFilter {

    private String kickoutUrl ;
    private boolean kickoutAfter = false ;
    private int max = 1 ;
    private RedisCache<Object,Object> kickoutCache ;
    private String kickoutCacheName ;
    private CacheManager cacheManager ;
    private String kickoutAttributeName = "kickout" ;
    private SessionManager sessionManager ;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = super.getSubject(request,response) ;
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return true ;
        }
        Session session = subject.getSession() ;
        String mid = (String) subject.getPrincipal() ;
        Deque<Serializable> allSession = (Deque<Serializable>) this.kickoutCache.get(mid);
        if (allSession == null) {
            allSession = new LinkedList<>() ;
        }
        if (!allSession.contains(session.getId())
                && session.getAttribute(this.kickoutAttributeName) == null) {
            allSession.push(session.getId());
            this.kickoutCache.put(mid,allSession) ;
        }
        try {
            if (allSession.size() > this.max) {
                Serializable kickoutSessionId = null ;
                if (this.kickoutAfter == true) {
                    kickoutSessionId = allSession.removeFirst() ;
                } else {
                    kickoutSessionId = allSession.removeLast() ;
                }
                this.kickoutCache.put(mid,allSession) ;
                Session kickoutSession = this.sessionManager.getSession(
                        new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    kickoutSession.setAttribute(this.kickoutAttributeName, true);
                }
            }
        } catch (Exception e){}
        if (session.getAttribute(this.kickoutAttributeName) != null) {
            subject.logout();
            super.saveRequest(request);
            WebUtils.issueRedirect(request,response,this.kickoutUrl + "?kickmsg=out");
            return false ;
        }
        return true;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.kickoutCache = (RedisCache<Object, Object>) this.cacheManager.getCache(this.kickoutCacheName);

    }

    public void setKickutCacheName(String kickutCacheName) {
        this.kickoutCacheName = kickutCacheName;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setKickoutAttributeName(String kickoutAttributeName) {
        this.kickoutAttributeName = kickoutAttributeName;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
