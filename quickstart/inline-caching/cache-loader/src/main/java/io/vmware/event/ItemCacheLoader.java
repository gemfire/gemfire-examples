package io.vmware.event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.LoaderHelper;

@SuppressWarnings({ "deprecation" })
public class ItemCacheLoader implements CacheLoader<String, String> {
	
	private static LogWriter log;

	static {
		log = CacheFactory.getAnyInstance().getDistributedSystem().getLogWriter();
	}

	private String userName;
	private String password;

	@Override
	public void initialize(Cache cache, Properties properties) {
		userName = System.getProperty("postgres.username");
		password = System.getProperty("postgres.password");
		log.info("username: "+ userName);
		log.info("password: "+ password);
	}

	public String load(LoaderHelper<String, String> helper) {
		int itemId = Integer.parseInt(helper.getKey());
		String url = "jdbc:postgresql:postgres";
		log.info("Cache miss... Loading data from postgres...");
		log.info("itemId: "+itemId);
		try {
			log.info("driver:" + Class.forName("org.postgresql.Driver"));
		} catch (ClassNotFoundException e) {
			log.error("class not found:", e);
		}

		try (Connection conn = DriverManager.getConnection(url, userName, password)) {
			log.info("connected:"+ conn.getSchema());
			DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);
			Result<Record> result = create.fetch("select filler from pgbench_tellers where tid=?", itemId);

			return result.isEmpty() ? null : (String) result.getValue(0, 0);
		} catch (SQLException e) {
			log.error("Exception", e);
			throw new RuntimeException(e);
		}
    }
}
