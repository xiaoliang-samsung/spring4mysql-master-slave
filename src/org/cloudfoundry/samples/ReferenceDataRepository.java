package org.cloudfoundry.samples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.cloudpi.Fields;
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.source.bean.Credential;

@Repository
@Transactional
public class ReferenceDataRepository {

	private JdbcTemplate jdbcTemplate = null;

	public void init(String type) {
		Configuration.init();
		if (Boolean.valueOf(Configuration.getValue("use_cloudpi_source"))) {
			/**
			 * here shows how to use the cloudpi service
			 */
			System.out.println("use the CloudPiServiceAPI");
			List<DataSource> list = CloudpiService.getMysqlSources(type);
			if (list.size() > 0) {
				BasicDataSource ds = (BasicDataSource) list.get(new Random()
						.nextInt(list.size()));
				if (type.equals("master")) {
					List<String> initSqls = new ArrayList<String>();
					initSqls.add("create table if not exists current_states (id smallint primary key, state_code char(2), name varchar(50))");
					initSqls.add("insert into current_states(id, state_code, name) values(1, 'MA', 'Massachusetts') ON DUPLICATE KEY UPDATE id=id");
					initSqls.add("insert into current_states(id, state_code, name) values(2, 'NH', 'New Hampshire') ON DUPLICATE KEY UPDATE id=id");
					initSqls.add("insert into current_states(id, state_code, name) values(3, 'ME', 'Maine') ON DUPLICATE KEY UPDATE id=id");
					ds.setConnectionInitSqls(initSqls);
				}
				jdbcTemplate = new JdbcTemplate(ds);
			}
		} else {
			// here you can write your own method to get connections
			System.out.println("use the VCAP_SERVICE ENV directly");
			JSONArray jsonArray = null;
			try {
				JSONObject rootObj = new JSONObject(
						System.getenv("VCAP_SERVICES"));
				// JSONObject rootObj = new JSONObject(
				// "{\"mysql-5.1.410\":[{\"name\":\"swctest_115_mysqllvm-ubuntu-ebs_swctest\",\"label\":\"mysql-5.1.410\",\"plan\":\"free\",\"credentials\":[{\"username\":\"scalr\",\"host\":\"ec2-107-20-31-3.compute-1.amazonaws.com\",\"password\":\"TQbOTzWY98e82lWImiQR\",\"user\":\"scalr\",\"hostname\":\"ec2-107-20-31-3.compute-1.amazonaws.com\",\"type\":\"master\",\"port\":3306,\"name\":\"cloudpidb\"},{\"username\":\"scalr\",\"host\":\"ec2-23-22-31-206.compute-1.amazonaws.com\",\"password\":\"TQbOTzWY98e82lWImiQR\",\"user\":\"scalr\",\"hostname\":\"ec2-23-22-31-206.compute-1.amazonaws.com\",\"type\":\"slave\",\"port\":3306,\"name\":\"cloudpidb\"}]}]}");

				JSONArray mysqlArray = rootObj.getJSONArray("mysql-5.1.410");
				jsonArray = mysqlArray.getJSONObject(0).getJSONArray(
						"credentials");
				List<Credential> list = new ArrayList<Credential>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					if (type.equals(obj.getString(Fields.TYPE.getValue()))) {
						Credential connInfo = new Credential();
						connInfo.setHost(obj.getString(Fields.HOST.getValue()));
						connInfo.setPort(obj.getInt(Fields.PORT.getValue()));
						connInfo.setUsername(obj.getString(Fields.USERNAME
								.getValue()));
						connInfo.setPassword(obj.getString(Fields.PASSWORD
								.getValue()));
						connInfo.setDb(obj.getString(Fields.DBNAME.getValue()));
						connInfo.setType(obj.getString(Fields.TYPE.getValue()));
						list.add(connInfo);
					}
				}
				if (list.size() > 0) {
					Credential connInfo = list.get(new Random().nextInt(list
							.size()));
					try {
						BasicDataSource ds = new BasicDataSource();
						ds.setUrl("jdbc:mysql://" + connInfo.getHost() + ":"
								+ connInfo.getPort() + "/" + connInfo.getDb());
						ds.setDriverClassName("com.mysql.jdbc.Driver");
						ds.setUsername(connInfo.getUsername());
						ds.setPassword(connInfo.getPassword());
						if (type.equals("master")) {
							List<String> initSqls = new ArrayList<String>();
							initSqls.add("create table if not exists current_states (id smallint primary key, state_code char(2), name varchar(50))");
							initSqls.add("insert into current_states(id, state_code, name) values(1, 'MA', 'Massachusetts') ON DUPLICATE KEY UPDATE id=id");
							initSqls.add("insert into current_states(id, state_code, name) values(2, 'NH', 'New Hampshire') ON DUPLICATE KEY UPDATE id=id");
							initSqls.add("insert into current_states(id, state_code, name) values(3, 'ME', 'Maine') ON DUPLICATE KEY UPDATE id=id");
							ds.setConnectionInitSqls(initSqls);
						}
						jdbcTemplate = new JdbcTemplate(ds);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public String getDbInfo() {
		DataSource dataSource = jdbcTemplate.getDataSource();
		if (dataSource instanceof BasicDataSource) {
			return ((BasicDataSource) dataSource).getUrl();
		} else if (dataSource instanceof SimpleDriverDataSource) {
			return ((SimpleDriverDataSource) dataSource).getUrl();
		}
		return dataSource.toString();
	}

	public List<State> findAll() {
		return this.jdbcTemplate.query("select * from current_states",
				new RowMapper<State>() {
					public State mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						State s = new State();
						s.setId(rs.getLong("id"));
						s.setStateCode(rs.getString("state_code"));
						s.setName(rs.getString("name"));
						return s;
					}
				});
	}

}
