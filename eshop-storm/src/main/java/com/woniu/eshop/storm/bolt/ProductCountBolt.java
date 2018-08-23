package com.woniu.eshop.storm.bolt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.trident.util.LRUMap;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;

import com.alibaba.fastjson.JSONObject;
import com.woniu.eshop.storm.zk.ZookeeperSession;

import scala.Array;

public class ProductCountBolt extends BaseRichBolt {

	private static final long serialVersionUID = 5937339212638672042L;

	private static final String ZOOKEEPER_SAVE_NODE_DATA_PATH = "/taskid-list";

	private LRUMap<Long, Long> map = new LRUMap<Long, Long>(1000);

	private ZookeeperSession zookeeperSession;

	private int taskId;

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		zookeeperSession = ZookeeperSession.getInstance();
		this.taskId = context.getThisTaskId();
		new Thread(new ProductCountThread()).start();
		initTaskId(taskId);
	}

	public void initTaskId(int taskId) {
		zookeeperSession.accquireDistributeLock("/taskid-list-lock");
		zookeeperSession.createNode(ZOOKEEPER_SAVE_NODE_DATA_PATH);
		String taskIdList = zookeeperSession.getNodeData(ZOOKEEPER_SAVE_NODE_DATA_PATH);
		if (!"".equals(taskIdList)) {
			taskIdList += "," + taskId;
		} else {
			taskIdList = taskId + "";
		}
		zookeeperSession.setNodeData(ZOOKEEPER_SAVE_NODE_DATA_PATH, taskIdList);
		zookeeperSession.releaseDistributeLock("/taskid-list-lock");

	}

	public void execute(Tuple input) {
		Long productId = input.getLongByField("productId");
		Long count = 0L;
		if (map.get(productId) != null) {
			count = map.get(productId);
		}
		count++;
		map.put(productId, count);

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	private class ProductCountThread implements Runnable {

		public void run() {

			List<Map.Entry<Long, Long>> topProductMap = new ArrayList<Map.Entry<Long, Long>>();

			while (true) {
				topProductMap.clear();

				if (topProductMap.size() == 0) {
					Utils.sleep(100);
					continue;
				}
				for (Map.Entry<Long, Long> productMap : map.entrySet()) {
					int topSize = 3;
					if (topProductMap.size() == 0) {
						topProductMap.add(productMap);
					} else {
						boolean bigger = false;
						for (int i = 0; i < topProductMap.size(); i++) {
							if (productMap.getValue() > topProductMap.get(i).getValue()) {
								int lastIndex = topProductMap.size() < topSize ? topProductMap.size() - 1 : topSize - 2;
								for (int j = lastIndex; j >= i; j--) {
									topProductMap.set(j + 1, topProductMap.get(j));
								}
								topProductMap.set(i, productMap);
								bigger = true;
								break;
							}

							if (!bigger) {
								if (topProductMap.size() < topSize) {
									topProductMap.add(productMap);
								}
							}
						}
					}
				}
				String topProductListString = JSONObject.toJSONString(topProductMap);
				zookeeperSession.createNode("/taskid-hot-product-list"+taskId);
				zookeeperSession.setNodeData("/taskid-hot-product-list"+taskId, topProductListString);
			}

		}
	}
}
