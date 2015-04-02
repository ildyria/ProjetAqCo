package printer;

public class Printer {
	public static void slow(String s, int n) throws Exception{
		for(int i = 0; i < s.length(); ++i)
		{
			if(s.charAt(i) != ' ')
			{
				Thread.sleep(n);
				System.out.print(s.charAt(i));
			}
			else
			{
				System.out.print(s.charAt(i));
			}
		}
		System.out.println();
	}
}
