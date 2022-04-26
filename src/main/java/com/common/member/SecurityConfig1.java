package com.common.member;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig1 extends WebSecurityConfigurerAdapter {

	@Resource
	private MemberServiceImpl3 memberServiceImpl;
	@Resource
	private DataSource dataSource;
	@Resource
	MemberDAO5 memberDAO;

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/assets/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// HttpServletRequest 요청 URL에 따라 접근 권한을 설정
		http.authorizeRequests()
			.antMatchers("/views/*").permitAll()
			.antMatchers("/views/**").hasRole("ADMIN")
			.antMatchers("/**").permitAll();

		// 로그인 설정
		http.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login_exe.run")
			.usernameParameter("user_id")
			.passwordParameter("user_pass")
			.successHandler(thisSuccessHandler())
			.failureHandler(thisFailureHandler())
			.permitAll();

		// 로그아웃 설정
		http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.deleteCookies("JSESSIONID","rememberMeCookie")
			.invalidateHttpSession(true)
			.permitAll()
			.logoutSuccessUrl("/");

		// 로그인 유지
		http.rememberMe().rememberMeServices(thisRememberMeServices());
		// persistent 오류 예외처리 "로그인 상태 유지를 위한 토큰이 일치하지 않습니다. 이전에 사용한 토큰이 타인으로부터 탈취 당했을 수 있습니다."
		http.addFilterAfter(new ExceptionHandlerFilter(), SecurityContextHolderAwareRequestFilter.class);

		//만장일치 처리
		http.authorizeRequests().accessDecisionManager(accessDecisionManager());

		// X-Frame-Options header 처리. iframe 처리.
		http.headers().frameOptions().disable();
		// CORS(Cross-Origin Resource Sharing)
		http.cors().configurationSource(corsConfigurationSource());
		// cors를 사용하게 되면 disable 시켜야 한다
		http.csrf().disable();
		// ajax post CSRF에서 무시할 대상
		// http.csrf().ignoringAntMatchers("**/*do","**/*run");

		// 403 예외처리 핸들링
		http.exceptionHandling().accessDeniedPage("/error/denied");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new MemberPasswordEncoder1();
	}

	@Bean // 로그인 성공 처리를 위한 Handler
	public MemberSuccessHandler1 thisSuccessHandler() {
		return new MemberSuccessHandler1("/index");
	}

	@Bean // 로그인 실패 처리를 위한 Handler
	public MemberFailureHandler1 thisFailureHandler() {	return new MemberFailureHandler1("/login"); }

	@Bean // 권한상속 및 URL별 권한확인
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchyImpl = new RoleHierarchyImpl();
		roleHierarchyImpl.setHierarchy("ROLE_MASTER > ROLE_ADMIN > ROLE_USER");

		return roleHierarchyImpl;
	}

	@Bean
	public AccessDecisionManager accessDecisionManager() {

		WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());
		webExpressionVoter.setExpressionHandler(expressionHandler);

		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
			webExpressionVoter,
			new RoleVoter(),
			new AuthenticatedVoter(),
			new URLVoter1(memberDAO)
		);

		return new UnanimousBased(decisionVoters);
	}

	// 로그인유지 토큰
	@Bean
	public JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl() {
		JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
		repository.setCreateTableOnStartup(false); // 토큰테이블 생성. 초기 true로 생성하고 그 이후에는 false 셋팅.
		repository.setDataSource(dataSource);
		return repository;
	}

	@Bean
	public PersistentTokenBasedRememberMeServices thisRememberMeServices() {
		PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices = new PersistentTokenBasedRememberMeServices("rememberMeCookie", memberServiceImpl, jdbcTokenRepositoryImpl());
		persistentTokenBasedRememberMeServices.setTokenValiditySeconds(60*60*24*30);//30일유지로 변경
		persistentTokenBasedRememberMeServices.setCookieName("rememberMeCookie");
		persistentTokenBasedRememberMeServices.setParameter("ingCheck");

		return persistentTokenBasedRememberMeServices;
	}

	// CORS 셋팅
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("http://webadmin.amoretto.co.kr","https://webadmin.amoretto.co.kr","https://reconciliation.petitelin.co.kr")); //CORS 요청을 허용할 사이트
		configuration.setAllowedHeaders(Arrays.asList("*")); //특정 헤더를 가진 경우에만 CORS 요청을 허용할 경우
		configuration.setAllowedMethods(Arrays.asList("POST","GET")); //CORS 요청을 허용할 Http  Method들
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}