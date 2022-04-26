package com.common.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageUtil {

	@Bean
	public ResourceBundleMessageSource messageSource() {

		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setBasenames("message");
		// ���� �޼����� ��� ���ܸ� �߻���Ű�� ��� �ڵ带 �⺻ �޼����� �Ѵ�.
		messageSource.setUseCodeAsDefaultMessage(true);

		return messageSource;
	}
}
