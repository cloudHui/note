package ano;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnoTest {

	public static void main(String[] args) {
		AnoTest anoTest = new AnoTest();
		if (classMethodContainAno(anoTest, RunBefore.class)) {
			System.out.println(" Annotation RunBefore");
		}
		anoTest.test();
		if (classMethodContainAno(anoTest, RunAfter.class)) {
			System.out.println(" Annotation RunAfter");
		}
	}


	@RunBefore
	@RunAfter
	public void test() {
		System.out.println("testRu");
	}

	/**
	 * 看某个类的方法是否包含某个注解
	 *
	 * @param object   检查的类
	 * @param checkAno 待检查的注解
	 * @return 检测结果
	 */
	private static boolean classMethodContainAno(Object object, Class checkAno) {
		Method[] methods = object.getClass().getMethods();
		Annotation[] annotations;
		for (Method method : methods) {
			annotations = method.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType() == checkAno) {
					return true;
				}
			}
		}
		return false;
	}
}
