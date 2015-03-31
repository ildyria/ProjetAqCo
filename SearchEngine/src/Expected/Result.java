package Expected;

import java.util.ArrayList;
import java.util.List;


public class Result {
	public List<Integer> _files;
	public int _num;
	
	Result(int n)
	{
		_num = n;
		_files = new ArrayList<Integer>();
	};
	
	public void push(int n)
	{
		_files.add(n);
	}
}
