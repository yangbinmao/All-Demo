package cn.itcast.filter;

import com.netflix.discovery.util.StringUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component   //将服务注入进spring容器
@Slf4j
public class LoginFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;//设置拦截的地方；pre表示一进来就拦截。
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;   //设置拦截位置
    }

    @Override
    public boolean shouldFilter() {
        return true;    //设置要不要过滤，默认为false。
    }

    @Override
    public Object run() throws ZuulException {   //这里就写业务逻辑
        //获取请求参数
            //获取请求上下文
        RequestContext ctx = RequestContext.getCurrentContext();
            //获取request
        HttpServletRequest request = ctx.getRequest();
            //获取请求参数accese.token
        String token = request.getParameter("accese.token");
        //判断是否存在
        if (StringUtils.isBlank(token)){
            //不存在，未登录，则拦截
            log.info("访问没有accese.token，成功拦截");
            ctx.setSendZuulResponse(false);  //这一步就是拦截
            ctx.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);//设置拦截返回码
            return null;
        }
        log.info("访问成功，不做拦截");
        return null;
    }
}
