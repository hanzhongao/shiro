package com.hza.ssm.filter.authc;

import com.hza.ssm.filter.authc.exceptrion.RandomCodeException;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RandomCodeFormAuthenticationFilter extends FormAuthenticationFilter {
    private String randParamName = "rand";
    private String codeParamName = "code";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpSession session = ((HttpServletRequest) request).getSession();
        String rand = (String) session.getAttribute(this.randParamName);
        String code = request.getParameter(this.codeParamName);
        try {
            if (!(rand == null || "".equals(rand))) {
                if (code == null || "".equals(code)) {
                    throw new RandomCodeException("验证码不许为空");
                } else {
                    if (!rand.equalsIgnoreCase(code)) {
                        throw new RandomCodeException("验证码错误");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(super.getFailureKeyAttribute(), e.getClass().getName());
            return true;
        }
        return super.onAccessDenied(request, response);
    }
    public void setRandParamName(String randParamName) {
        this.randParamName = randParamName ;
    }
    public void setCodeParamName(String codeParamName) {
        this.codeParamName = codeParamName ;
    }
}
