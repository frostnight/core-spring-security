package io.security.corespringsecurity.security.handler;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class FormAccessDeniedHandler implements AccessDeniedHandler {

	private String errorPage;

	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {
		String deniedUrl = errorPage + "?exception=" + URLEncoder.encode(accessDeniedException.getMessage(), "UTF-8");
		httpServletResponse.sendRedirect(deniedUrl);
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}
}
