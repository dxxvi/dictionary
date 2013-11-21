package home.standalone;

public class TypeErasureTest {
    public static void main(String[] args) {
        GenericArrayTest<String> gat = new GenericArrayTest<>(String.class);
        String[] strings = gat.returnArray();
        strings[0] = "what";
        strings[1] = "this";
    }
}

class GenericArrayTest<T> {
    private Class<T> clazz;

    GenericArrayTest(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T[] returnArray() {
        return (T[]) java.lang.reflect.Array.newInstance(clazz, 19);
    }
}