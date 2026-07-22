package finaltest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class WgetExample {
    public static void main(String[] args) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "wget https://www.baidu.com");
        processBuilder.redirectErrorStream(true); // 将错误输出和标准输出合并
 
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exited with error code : " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}