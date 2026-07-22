package server.model;

import java.lang.reflect.Method;

public class Test {

	public static void main(String[] args) throws Exception {

		String className = "com.runqianapp.ngr.alias.example.FunClass";

		String methodName = "sayHello";

		Class clz = Class.forName(className);

//

		Object obj = clz.newInstance();

//获取方法

		Method m = obj.getClass().getDeclaredMethod(methodName, String.class);

//调用方法

		String result = (String) m.invoke(obj, "aaaaa");

		System.out.println(result);

	}

}

class FunClass {

	public String sayHello(String s) {

		System.out.println(s);

		return "hello!";

	}

}