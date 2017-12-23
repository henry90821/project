package com.smi.am.configuration;

import com.smi.am.constant.SmiConstants;
import com.smilife.core.utils.logger.Logger;
import com.smilife.core.utils.logger.LoggerUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * WebSocket配置基类<br/>
 * Created by Andriy on 16/8/12.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	private static final Logger LOGGER = LoggerUtils.getLogger(WebSocketConfig.class);

	/**
	 * WebSocket服务端点
	 */
	private static final String SOCKET_ENDPOINT = SmiConstants.ADMIN_PATH + "/socket";

	/**
	 * WebSocket服务端通信客户端所使用到的前缀
	 */
	public static final String SOCKET_CLIENT_PREFIX = "/client";

	/**
	 * WebSocket客户端通信服务端所使用到的前缀
	 */
	private static final String SOCKET_SERVER_PREFIX = "/server";

	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
		stompEndpointRegistry.addEndpoint(SOCKET_ENDPOINT).setAllowedOrigins("*").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		LOGGER.info("开始设置通知客户端的地址前缀({})...", SOCKET_CLIENT_PREFIX);
		registry.enableSimpleBroker(SOCKET_CLIENT_PREFIX);
		LOGGER.info("开始设置客户端请求服务端的地址前缀({})...", SOCKET_SERVER_PREFIX);
		registry.setApplicationDestinationPrefixes(SOCKET_SERVER_PREFIX);
	}
}
