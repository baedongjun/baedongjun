package com.common.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DbConn {

	private SqlSessionTemplate thisSqlSession;

	public DbConn(SqlSessionTemplate sqlSessionInjection) {
		this.thisSqlSession = sqlSessionInjection;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List recordSet(String queryName) {return thisSqlSession.selectList(queryName);}
	@Transactional(propagation = Propagation.REQUIRED)
	public List 	recordSet(String queryName, Object obj) {return thisSqlSession.selectList(queryName, obj);}

	//??©ö?//////////////////////////////////////////////////////////////////////////////////////////////////////

	public String makeSearchSql(String name, String[] val, String...gubun) {
		if (Common.isNullOrEmpty(val) && gubun.length<2) return null;
		if (Common.isNullOrEmpty(val) && gubun.length==2) val = new String[]{gubun[1]};

		List<String> list = stringToList(val);
		if (list.size() == 0) { return null; }


		switch (gubun[0].trim()) {
			case "like":
				return Common.addString("(", name, " like '%", String.join("%' escape '#' or " + name + " like '%", list), "%' escape '#')");
			case "likeOX":
				return Common.addString("(", name, " like '%", String.join("' escape '#' or " + name + " like '%", list), "' escape '#')");
			case "likeXO":
				return Common.addString("(", name, " like '", String.join("%' escape '#' or " + name + " like '", list), "%' escape '#')");
			case "not in":
			case "<>":
				return Common.addString(name, " not in ('", String.join("','", list), "')");
			default:
				return Common.addString(name, " in ('", String.join("','", list), "')");
		}
	}

	class likeSpecialChar implements UnaryOperator<String> {
		@Override
		public String apply(String t) {
			return t.replace("[", "#[");
		}
	}

	public String makeSearchSql2(String[] val, Map<String,String> name, String ini) {
		if (Common.isNullOrEmpty(val) && Common.isNullOrEmpty(ini)) return null;
		if (Common.isNullOrEmpty(val) && !Common.isNullOrEmpty(ini)) val = new String[]{ini};

		List<String> list = stringToList(val);
		if (list.size() == 0) { return null; }

		for (int i=0 ; i<list.size() ; i++) if (name.containsKey(list.get(i))) list.set(i,name.get(list.get(i)));
		return "(" + String.join(" or ",list) +")";
	}

	public String makeSearchSqlRange(String name, String...val) {
		if (Common.isNullOrEmpty(val)) return null;
		String valStart = val[0];
		String valEnd = val[1];
		if (Common.isNullOrEmpty(valStart) && Common.isNullOrEmpty(valEnd) && val.length==3) {
			valStart = val[2];
			valEnd = val[2];
		}

		valStart = this.checkInjection(valStart);
		valEnd = this.checkInjection(valEnd);

		valStart = (Common.isDate(valStart)) ? "'" + valStart + "'" : valStart;
		valEnd = (Common.isDate(valEnd) && !valEnd.equals("")) ? "dateadd(s,-1,dateadd(d,1,'" + valEnd + "'))" : valEnd;

		if (valStart.replace("''", "").equals("")) {
			return Common.addString(name, "<=", valEnd);
		} else {
			if (valEnd.replace("''", "").equals("")) {
				return Common.addString(name, ">=", valStart);
			} else {
				return Common.addString("(", name, ">=", valStart, " and ", name, "<=", valEnd, ")");
			}
		}
	}

	public List stringToList(String[] val) {
		List<String> list = new ArrayList<>(Arrays.asList(val));
		list = list.stream().map(String :: trim).filter(x -> x != "").collect(Collectors.toList());
		list.replaceAll(new likeSpecialChar());
		return this.checkInjection(list);
	}

	public String checkInjection(String val) {
		if (Common.isNullOrEmpty(val)) {
			return "";
		}


		String VAL = val.toUpperCase();
		if (VAL.length() > 50 ||

			VAL.indexOf("XP_") > -1 ||
			VAL.indexOf("SP_") > -1 ||
			VAL.indexOf("EXEC") > -1 ||
			VAL.indexOf("DECLARE") > -1 ||

			VAL.indexOf("/*") > -1 ||
			VAL.indexOf(";") > -1 ||
			VAL.indexOf("--") > -1 ||

			VAL.indexOf("SELECT") > -1 ||
			VAL.indexOf("UPDATE") > -1 ||
			VAL.indexOf("DELETE") > -1 ||
			VAL.indexOf("DROP") > -1 ||
			VAL.indexOf("TRUNCATE") > -1
			) {
			return "";
		} else {
			return val;
		}
	}

	public List<String> checkInjection(List<String> val) {
		if (Common.isNullOrEmpty(val)) {
			return null;
		}

		for (int i = 0; i < val.size(); i++) {
			if (this.checkInjection((String) val.get(i)).equals("")) {
				val.remove(i);
			}
		}
		return val;
	}


//¸ð¹ÙÀÏ
	public String makeSearchSqlRange(String name, String val, String val2) {
		if (Common.isNullOrEmpty(val) && Common.isNullOrEmpty(val2)) {
			return null;
		}

		val = this.checkInjection(val);
		val2 = this.checkInjection(val2);

		val = (Common.isDate(val)) ? "'"+ val +"'" : val;
		val2 = (Common.isDate(val2) && !val2.equals("")) ? "dateadd(s,-1,dateadd(d,1,'"+ val2 +"'))" : val2;

		if (val.replace("''", "").equals("")) {
			return Common.addString(name, "<=", val2);
		} else if (val2.replace("''", "").equals("")) {
			return Common.addString(name, ">=", val);
		} else {
			return Common.addString("(", name, ">=", val, " and ", name, "<=", val2, ")");
		}
	}
}