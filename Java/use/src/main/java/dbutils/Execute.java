package dbutils;

@FunctionalInterface
public interface Execute<T, R> {
	R execute(T var1) throws Exception;
}
