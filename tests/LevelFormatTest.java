import com.mck.longenchants.LevelFormat;
public class LevelFormatTest {
    public static void main(String[] args) {
        check("I", LevelFormat.level(1));
        check("X", LevelFormat.level(10));
        check("11", LevelFormat.level(11));
        check("1,000", LevelFormat.numeric(1000));
        check("9,223,372,036,854,775,807", LevelFormat.numeric(Long.MAX_VALUE));
        check(9223372036854775807L, LevelFormat.parseLevel("9,223,372,036,854,775,807"));
        System.out.println("Level format tests passed");
    }
    private static void check(Object expected, Object actual) {
        if (!expected.equals(actual)) throw new AssertionError("Expected " + expected + " got " + actual);
    }
}
