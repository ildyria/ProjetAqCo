package Expected;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResultsParser {
	public String _path;
	public List<Result> _entries;
	
	public ResultsParser(String path){
		_path = new String(path);
	    _entries = new ArrayList<Result>();
	    parse();
	}

	private void parse(){
		try {
		    Result entry = null;
		
		    BufferedReader br;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(_path)));
	
		    int request_num = 0;
		    int file_num = 0;
		    int previous_request = 0;
		    String line;
			while ((line = br.readLine()) != null) {
				Scanner scanner = new Scanner(line);
				request_num = scanner.nextInt();
				file_num = scanner.nextInt();
				
				if(previous_request != request_num)
				{
					previous_request = request_num;
					if(entry != null)
					{
						_entries.add(entry);
					}
					
					entry = new Result(request_num);
				}
				entry.push(file_num);
				scanner.close();
			}

			if(entry != null)
			{
				_entries.add(entry);
			}
			br.close();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
