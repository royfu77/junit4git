import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(LoggerRunner.class)
public class RunListenerTest {

    @Test
    public void testMethod() throws Exception {
        //Hello h = new Hello();
        RunListenerTest.class.getClassLoader().loadClass("Hello");
        System.out.println("");
    }



}