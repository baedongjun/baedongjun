package com.common.member;

import java.util.Map;
import javax.annotation.Resource;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MemberDAO5 {

	@Resource(name = "sqlSession")
	public SqlSessionTemplate sqlSession;

	private final String QUERY_ROOT = "member.query";

	public MemberDTO4 readUser(String username) { return sqlSession.selectOne(QUERY_ROOT + ".readUser", username); }
	public int readFailure(String username) { return sqlSession.selectOne(QUERY_ROOT + ".readFailure", username); }
	public void resetFailure(String username) { sqlSession.update(QUERY_ROOT + ".resetFailure", username); }
	public void addFailure(String username) { sqlSession.update(QUERY_ROOT + ".addFailure", username); }

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertUser(MemberDTO4 account) {
		sqlSession.insert(QUERY_ROOT + ".insertUser", account);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateUser(MemberDTO4 account) {
		sqlSession.insert(QUERY_ROOT + ".updateUser", account);
	}

	public String viewUserAuthority(Map<String, String> map) {
		return sqlSession.selectOne(QUERY_ROOT + ".viewUserAuthority", map);
	}
}