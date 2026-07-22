package download.music;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.codec.binary.Base64;

/**
 * 直接调用js函数
 */
public class JavaCallJS {

	/**
	 * 调用js 生成参数码
	 *
	 * @param args 调用 函数传递的参数
	 */
	public static String callJs(String args) {
		// 创建一个ScriptEngineManager实例
		ScriptEngineManager manager = new ScriptEngineManager();
		// 获取JavaScript引擎
		ScriptEngine engine = manager.getEngineByName("nashorn");

		try {
			// 执行一个简单的JavaScript函数
			engine.eval("function generateParam(data) {\n" +
					"    var key = '95wwwHiFiNicom27';\n" +
					"    var outText = '';\n" +
					"\n" +
					"    for(var i = 0, j = 0; i < data.length; i++, j++) {\n" +
					"        if(j == key.length) j = 0;\n" +
					"        outText += String.fromCharCode(data.charCodeAt(i) ^ key.charCodeAt(j));\n" +
					"    }\n" +
					"\t\n" +
					"\t\n" +
					"    var base32chars = \"ABCDEFGHIJKLMNOPQRSTUVWXYZ234567\";\n" +
					"    var bits = \"\";\n" +
					"    var base32 = \"\";\n" +
					"\n" +
					"    for(var i = 0; i < outText.length; i++) {\n" +
					"        var bit = outText.charCodeAt(i).toString(2);\n" +
					"        while(bit.length < 8) {\n" +
					"            bit = \"0\" + bit;\n" +
					"        }\n" +
					"        bits += bit;\n" +
					"    }\n" +
					"\n" +
					"    while(bits.length % 5 !== 0) {\n" +
					"        bits += \"0\";\n" +
					"    }\n" +
					"\n" +
					"    for(var i = 0; i < bits.length; i += 5) {\n" +
					"        var chunk = bits.substring(i, i+5);\n" +
					"        base32 += base32chars[parseInt(chunk, 2)];\n" +
					"    }\n" +
					"\n" +
					"    while(base32.length % 8 !== 0) {\n" +
					"        base32 += \"=\";\n" +
					"    }\n" +
					"    var out = base32.replace(/=/g, 'HiFiNiYINYUECICHANG');\n" +
					"    return out;\n" +
					"}");
			// 通过Invocable接口调用JavaScript函数
			Invocable invocable = (Invocable) engine;
			// 调用JavaScript函数
			return (String) invocable.invokeFunction("generateParam", args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 调用js 生成参数码
	 *
	 * @param args 调用 函数传递的参数
	 */
	public static String callJs(String[] args, String function, String functionName) {
		// 创建一个ScriptEngineManager实例
		ScriptEngineManager manager = new ScriptEngineManager();
		// 获取JavaScript引擎
		ScriptEngine engine = manager.getEngineByName("nashorn");

		try {
			// 执行一个简单的JavaScript函数
			engine.eval(function);
			// 通过Invocable接口调用JavaScript函数
			Invocable invocable = (Invocable) engine;
			// 调用JavaScript函数
			return (String) invocable.invokeFunction(functionName, args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 调用js 生成参数码
	 */
	public static String callJsNoParam(String js) {
		// 创建一个ScriptEngineManager实例
		ScriptEngineManager manager = new ScriptEngineManager();
		// 获取JavaScript引擎
		ScriptEngine engine = manager.getEngineByName("nashorn");

		try {
			// 执行一个简单的JavaScript函数
			engine.eval(js);
			// 通过Invocable接口调用JavaScript函数
			Invocable invocable = (Invocable) engine;
			// 调用JavaScript函数
			return (String) invocable.invokeFunction("generateParam");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public static void callJsOne(String content) {
		String function = "function transform(content,chapter) {\n" +
				"    var arrays = content.split(',');\n" +
				"    chapter = chapter.replace(new RegExp(/\\[.*?\\]/), '');\n" +
				"    var datas = chapter.split('<br><br>');\n" +
				"    var result = '';\n" +
				"    var el = arrays[0];\n" +
				"    var len = datas.length;\n" +
				"    for (var i = 1; i <= len; i++) {\n" +
				"        result += datas[arrays[i] - el] + '\\n';\n" +
				"    }\n" +
				"\treturn result;\n" +
				"\t}";

		String[] params = new String[2];
		String param = "'MjEsNDcsODQsOTQsOTYsNTcsMTA3LDI0LDQ5LDQyLDYzLDEwMSwxMTksOTMsMTE4LDI4LDQ2LDYwLDIxLDU0LDc2LDQ1LDMwLDQxLDgwLDkwLDkyLDIyLDExNCwxMDYsOTUsMzUsODYsMTE2LDg1LDc3LDc1LDU5LDgyLDExMCw2Niw1MSwxMTMsODgsMTE3LDUwLDY1LDkxLDgxLDU1LDM3LDIzLDQzLDk3LDcwLDEwNSw3MiwyOSw5OSw0OCwyNywzMiw3MSw2NywxMDQsNTIsMzgsMTEyLDU2LDQwLDExNSw0NCw2MSwzOSwxMDksMzEsMTA4LDk4LDI1LDEwMCwxMTEsNzMsNTMsNjQsMjYsODcsMzMsMzQsNzQsMTAzLDU4LDgzLDc4LDY5LDEwMiw2Miw3OSw2OCw4OSwzNg=='";
		byte[] decodedBytes = Base64.decodeBase64(param);
		params[0] = new String(decodedBytes);
		params[1] = content;
		String callJs = callJs(params, function, "transform");
		System.out.println(callJs);
	}
}