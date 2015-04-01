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
	
	public ResultsParser(String path, int numRequest){
		_path = new String(path);
	    _entries = new ArrayList<Result>();
	    parse(numRequest);
	}

	private void parse(int numRequest){
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
					previous_request++;
//					System.out.println("");
					while(previous_request != request_num)
					{
//						System.out.println("request num : " + previous_request);
						entry = new Result(previous_request);
						_entries.add(entry);
						previous_request++;
					}
//					System.out.println("request num : " + request_num);
//					System.out.print("files :");
//					previous_request = request_num;
					if(entry != null)
					{
						_entries.add(entry);
					}
					entry = new Result(request_num);
				}
				entry.push(file_num);
//				System.out.print(" " + file_num);
				scanner.close();
			}

			if(entry != null)
			{
				_entries.add(entry);
			}
			br.close();
			
			while(request_num < numRequest)
			{
				request_num++;
				entry = new Result(request_num);
				_entries.add(entry);
			}
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
