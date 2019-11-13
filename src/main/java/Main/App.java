package Main;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	long time = System.currentTimeMillis()/1000;
    	long month = 2592000;
    	System.out.println(time-month);
    }
}
