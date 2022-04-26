
package com.common.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ExcuteAOP {
	public static Log logger = LogFactory.getLog(ExcuteAOP.class);

	@Pointcut("execution(* com.webadmin..*(..))") public void classPointcut() {}
	@Around("classPointcut()")
	public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.nanoTime(); //현재의 나노시간을 반환
		long finish = System.nanoTime();
		Signature sig = joinPoint.getSignature(); //메서드의 시그니쳐
		System.out.println("\n\n======= AOP ===================================================================");
		System.out.printf("%s(%s) 실행 시간 : %d ns\n",
				sig.toShortString(),
				joinPoint.getTarget().toString(),
				(finish - start));
		return joinPoint.proceed();
	}

	@Pointcut("execution(* com.webadmin..*(..))")
	public void throwPointcut() {}
	@AfterThrowing(pointcut = "throwPointcut()", throwing = "e")
	public void afterThrowingMethod(JoinPoint jp, Throwable e) {
		logger.error(
			jp.getSignature().getName()
				+ " :: "
				+ jp.getTarget().getClass().getSimpleName()
				+ " :: "
				+ e.getMessage()
		);
	}
};

