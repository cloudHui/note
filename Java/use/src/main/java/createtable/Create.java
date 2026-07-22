package createtable;


import db.business.entity.ServerEnterty;
import db.business.service.CreateTableService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Create {

	public static void main(String[] args) {
		CreateTableService service = new CreateTableService();
		ServerEnterty serverEnterty = new ServerEnterty();
		serverEnterty.setServerid(2);
		serverEnterty.setServertype(1);
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String name = "rec_server_" + simpleDateFormat.format(date);
		int result = 0;
		try {
			result = service.insert(serverEnterty, name);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			System.out.println("insert result " + result);
			if (result == 0) {
				try {
					result = service.createTable(name);
					System.out.println("createTable result " + result);
					result = service.insert(serverEnterty, name);
					System.out.println("createTable insert result " + result);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}

	}
}
