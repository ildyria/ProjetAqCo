package Bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.search.TopDocs;

import Expected.ResultsParser;

public class Benchmark {
	HashMap<String, Double> _bench;
	ArrayList<BenchData> _datas;
	ResultsParser _expected;
	
	public Benchmark(Map<Integer, TopDocs> queryResults){
		_expected = new ResultsParser("CISIDonnees/CISI.REL");
		_datas = new ArrayList<BenchData>();
		_bench = new HashMap<String, Double>();
		System.out.println("entries : " + _expected._entries.size());
		Iterator<Entry<Integer, TopDocs>> it = queryResults.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<Integer, TopDocs> pair = it.next();
	    	_datas.add(BenchCalculus.Calculate(pair.getValue(), _expected._entries.get(pair.getKey()), 1460));
	        it.remove();
	    }
		
	    _bench = BenchCalculus.Moyenne(_datas);
	}
	
	@Override
	public String toString(){
		return "precision : " + _bench.get("precision") +  "%," +
				"sensitivity : " + _bench.get("recall") + "%," +
				"error rate :" + _bench.get("error rate") + "%";
	}
}
