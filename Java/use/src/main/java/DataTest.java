import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 测试
 */
public class DataTest {


	public static void main(String[] args) {


//		double coefficientDenominator = baseChange > 0 ? roleInfo.getRoleBaseInfo().getRoleLeagueMedal() - averageSeasonMedal.enemyAverageSeasonMedl
//				: averageSeasonMedal.enemyAverageSeasonMedl - roleInfo.getRoleBaseInfo().getRoleLeagueMedal();
//		coefficientDenominator *= MatchingRuleConfig.getInstance().getMedalDifferenceReduction();
//		coefficientDenominator = Math.pow(Math.E, coefficientDenominator);
//		coefficientDenominator += 1;
//		return coefficientMolecule / coefficientDenominator;
//		double e = 6.0000000000000001E-3;
//		System.out.println("0.006:" + 1 / (Math.pow(Math.E, 100 * e) + 1) * 160) ;
//		System.out.println("0.0006:" + 1 / (Math.pow(Math.E, 10 * e) + 1) * 160) ;

		List<Per> list = new ArrayList<>();
		list.add(new Per(10, "10"));
		list.add(new Per(1, "1"));
		list.add(new Per(14, "14"));
		list.add(new Per(4, "4"));
		list.add(new Per(2, "2"));
		list.sort(new Comparator<Per>() {
			@Override
			public int compare(Per o1, Per o2) {
				return o1.id-o2.id;
			}
		});
		System.out.println(list);

//		DataTest app = new DataTest();
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DAY_OF_YEAR, -4);
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
//		Date date = calendar.getTime();
//		Date dateNow = new Date();
//		for (Date dateIndex = date; dateIndex.getTime() < dateNow.getTime(); ) {
//			String now = simpleDateFormat.format(dateIndex);
//			List<String> fileNames = new ArrayList<>();
//			for (int i = 0; i < 10; i++) {
//				String path = "D:\\" + now + "-" + i + ".log";
//				fileNames.add(path);
//			}
//			System.out.println("日期:" + now);
//			app.readFileNum(fileNames);
//			calendar.add(Calendar.DAY_OF_YEAR, 1);
//			dateIndex = calendar.getTime();
//		}

	}

	static class Per {
		public int id;

		public String name;

		public Per(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return "Per{" + "id=" + id + ", name='" + name + '}';
		}
	}
}
