package io.vmware.event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;


@SuppressWarnings("deprecation")
public class ItemAsyncEventListener implements AsyncEventListener {

	private static LogWriter log;
	
	static {
		log = CacheFactory.getAnyInstance().getDistributedSystem().getLogWriter();
	}

	@Override
	public void init(Properties props) {

	}

	@Override
	public void close() {
	}

	@Override
	public boolean processEvents(List<AsyncEvent> events) {
		String userName = System.getProperty("postgres.username");
		String password = System.getProperty("postgres.password");
		String url = "jdbc:postgresql:postgres";
		log.info("Cache miss... Loading data from postgres...");
		log.info("username: "+ userName);
		log.info("password: "+ password);

		try {
			log.info("driver:" + Class.forName("org.postgresql.Driver"));
		} catch (ClassNotFoundException e) {
			log.error("class not found:", e);
		}

		try(Connection conn = DriverManager.getConnection(url, userName, password)) {
			log.info("connected:"+ conn.getSchema());
			return process(conn, events);
		} catch (SQLException e) {
			log.error("Exception", e);
			return false;
		}
	}

	private boolean process(Connection conn, List<AsyncEvent> events) {
		DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);

		for (AsyncEvent ge : events) {
			Operation operation = ge.getOperation();
			if(operation.equals(Operation.UPDATE) || operation.equals(Operation.CREATE)) {
				createOrUpdate(create, ge);
			} else {
				log.error("operation not implemented: " + operation);
			}
		}

		return true;
	}

	private static void createOrUpdate(DSLContext create, AsyncEvent ge) {
		int itemId = Integer.parseInt((String) ge.getKey());
		String value = (String) ge.getDeserializedValue();
		log.info(String.format("process event itemId: %s value: %s", itemId, value));

		Table<Record> table = DSL.table("pgbench_tellers");
		Field<Object> filler = DSL.field("filler");
		Field<Object> tid = DSL.field("tid");

		if (create.fetchExists(table)) {
			int result = create.update(table)
					.set(filler, value)
					.where(tid.eq(itemId))
					.execute();

			log.info("update result: " + result);
		} else {
			log.info("create");

			int result = create.insertInto(table)
					.columns(tid, filler)
					.values(itemId, value)
					.execute();

			log.info("create result: " + result);
		}
	}
}
