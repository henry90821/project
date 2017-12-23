package com.iskyshop.core.security.interceptor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;

import com.iskyshop.foundation.domain.Res;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.service.IResService;


public class SecureResourceFilterInvocationDefinitionSource implements FilterInvocationSecurityMetadataSource {

	@Autowired
	private IResService resService;
	
	protected final Log logger = LogFactory.getLog(getClass());

    private Map<RequestMatcher, Collection<ConfigAttribute>> requestMap;

    private void initSecurityMetaData() {
    	if(this.requestMap == null) {
    		synchronized(this) {
    			if(this.requestMap == null) {
    				List<Res> urlResources = resService.query("select obj from Res obj where obj.type = 'URL'", null, -1, -1);
    		    	
    				Map<RequestMatcher, Collection<ConfigAttribute>> tmp = new HashMap<RequestMatcher, Collection<ConfigAttribute>>(urlResources.size());
    		        
    		        for(Res r: urlResources) {
    		        	Set<ConfigAttribute> cas = new HashSet<ConfigAttribute>(r.getRoles().size());
    					for(Role role: r.getRoles()) {
    						cas.add(new CustomedConfigAttribute(role.getRoleCode()));
    					}
    					RequestMatcher requestMatcher = new AntPathRequestMatcher(r.getValue());
    					if(tmp.containsKey(requestMatcher)) {
    						logger.warn("============== 资源权限错误：资源" + r.getValue() + " 存在多个配置，系统初始化时只会取其最后一个配置。 ==============");
    					}
    					tmp.put(requestMatcher, cas);
    		        }
    		        this.requestMap = tmp;
    			}
    		}
    	}
    }


    public Collection<ConfigAttribute> getAllConfigAttributes() {
    	initSecurityMetaData();
    	
        Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();

        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }

        return allAttributes;
    }

    public Collection<ConfigAttribute> getAttributes(Object object) {
    	initSecurityMetaData();
    	
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
	
	
	private class CustomedConfigAttribute implements ConfigAttribute {
		
		private String code;
		
		public CustomedConfigAttribute(String configAttribute) {
			code = configAttribute;
		}
		
		@Override
		public String getAttribute() {
			return code;
		}
		
	}
	
}
