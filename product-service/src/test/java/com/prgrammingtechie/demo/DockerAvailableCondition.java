package com.prgrammingtechie.demo;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.testcontainers.DockerClientFactory;

public class DockerAvailableCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		try {
			DockerClientFactory.instance().client();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
