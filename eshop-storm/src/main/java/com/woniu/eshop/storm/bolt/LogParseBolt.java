package com.woniu.eshop.storm.bolt;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.alibaba.fastjson.JSONObject;

public class LogParseBolt extends BaseRichBolt{


	private static final long serialVersionUID = 3295036657458963323L;
	
	private OutputCollector collector;

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	public void execute(Tuple input) {
		String message = input.getStringByField("message");
		JSONObject jsonObject = JSONObject.parseObject(message);
		String productId = jsonObject.getString("productId");
		if(productId != null && !"".equals(productId)) {
			collector.emit(new Values(productId));
		}
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("productId"));	
	}

}
