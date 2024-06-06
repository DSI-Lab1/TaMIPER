package algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import structures.AERule;
import structures.ARNoRoot;
import structures.ARNode;
import structures.GNode;
import structures.ARPara;
import structures.ARRoot;

public class TaMIPER {
	private String inputFile;
	private long startTime;
	private long endTime;

	private List<ArrayList<GNode>> GraphList = new ArrayList<ArrayList<GNode>>();
	private Set<String> moList = new HashSet<String>();
	private Map<String, ArrayList<Integer>> qsMoList = new HashMap<String, ArrayList<Integer>>();
	private ARPara parameter = null;

	private static int eventUpperBound = 50;
	private long deltaTime = 0L;
	// private long deltaT4MiningAnt = 0L;
	// private long delta2 = 0L;
	// private long deltaT3 = 0L;

	private int begin = 0;
	private int end = 0;
	private HashMap<String, Integer> eventMap = new HashMap<String, Integer>();
	private int ESize = 0;
	private Map<Integer, ArrayList<String>> ALLS1 = new TreeMap<Integer, ArrayList<String>>();
	private Map<Integer, ArrayList<String>> ALLS2 = new TreeMap<Integer, ArrayList<String>>();
	private Map<Integer, ArrayList<String>> FreS = new TreeMap<Integer, ArrayList<String>>();

	private Map<String, HashSet<Integer>> candidates = new HashMap<String, HashSet<Integer>>();
	private Map<String, ArrayList<ArrayList<Integer>>> superSet = new HashMap<String, ArrayList<ArrayList<Integer>>>();
	private Map<String, HashSet<Integer>> frequentEpisode = new HashMap<String, HashSet<Integer>>();

	private List<AERule> validRules = new ArrayList<AERule>();

	public TaMIPER(String inputFile, int min_sup, int delta, double conf, int span, int begin, int end, String qs)
			throws FileNotFoundException {
		this.inputFile = inputFile;
		ArrayList<String> list =
				new ArrayList<String>(Arrays.asList(qs.split(",")));
		this.parameter = new ARPara(min_sup, delta, conf, span, list);
		this.begin = begin;
		this.end = end;

	}

	public void runAlg() {
		this.loadFrequentSequence(this.inputFile);
		// this.startTime = System.currentTimeMillis();
		this.algCore();
		// this.endTime = System.currentTimeMillis();
		// this.deltaTime = this.endTime - this.startTime;
		System.err.println("Execution time: " + deltaTime);
	}

	private void loadFrequentSequence(String input) {
		// TODO Auto-generated method stub
		try {
			TreeMap<String, Integer> eventSet = new TreeMap<String, Integer>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
			String line = null;
			int timestamp = 1;
			int sumALLS = 0, maxALLS = 0, minALLS = 100;

			while ((line = reader.readLine()) != null) {
				String[] array = StringUtils.split(line.trim(), ' ');
				ArrayList<String> eSet = new ArrayList<String>();
				ArrayList<String> eSet2 = new ArrayList<String>();
				ArrayList<String> eSet3 = new ArrayList<String>();
				for (String event : array) {
					eSet.add(event);
					eSet2.add(event);
					eSet3.add(event);
					if (eventSet.containsKey(event)) {
						eventSet.put(event, eventSet.get(event) + 1);
					} else {
						eventSet.put(event, 1);
					}
				}
				if (eSet.size() > 0 && eSet.size() <= eventUpperBound) {
					if(eSet.size() > maxALLS) maxALLS = eSet.size();
					if(eSet.size() < minALLS) minALLS = eSet.size();
					sumALLS += eSet.size();

					this.ALLS1.put(timestamp, eSet);
					this.ALLS2.put(timestamp, eSet3);
					this.FreS.put(timestamp, eSet2);
					this.begin = (timestamp > this.begin) ? this.begin : timestamp;
					this.end = (timestamp < this.end) ? this.end : timestamp;
				}
				timestamp++;
			}
			this.FreS = RepairSequence(this.FreS, this.parameter.getMin_support(), eventSet);
			reader.close();
			int offset = 0;
			for (Entry<String, Integer> entry : eventSet.entrySet()) {
				if (entry.getValue() >= this.parameter.getMin_support()) {
					this.ESize++;
					this.eventMap.put(entry.getKey(), offset++);
				}
			}
			System.out.println("事件个数：" + eventSet.size());
			System.out.println("序列个数：" + ALLS1.size());
			System.out.println("序列最大长度：" + maxALLS);
			System.out.println("序列最小长度：" + minALLS);
			System.out.println("序列平均长度：" + sumALLS / ALLS1.size());

			System.out.println("Number of Frequent Events: " + this.ESize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Map<Integer, ArrayList<String>> RepairSequence(Map<Integer, ArrayList<String>> S, double bound,
			TreeMap<String, Integer> eventSet) {
		// TODO Auto-generated method stub
		ArrayList<String> delList = new ArrayList<String>();
		for (Entry<Integer, ArrayList<String>> entry : S.entrySet()) {
			ArrayList<String> eSet = entry.getValue();
			for (String e : eSet) {
				int support = eventSet.get(e);
				if (support < bound) {
					delList.add(e);
				}
			}
			if (delList.size() > 0) {
				eSet.removeAll(delList);
				delList.clear();
			}
			S.put(entry.getKey(), eSet);
		}
		return S;
	}

	public void getQSPattern() {

		this.GraphList.clear();

		for (int i = this.begin; i <= this.end; i++) {
			ArrayList<String> EkplusOne = this.ALLS2.get(i);
			if (EkplusOne != null) {
				EkplusOne.retainAll(this.parameter.getQs());
				if (EkplusOne.size() > 0) {
					final Map<String, ArrayList<Integer>> tmpS = this.qsMoList;
					this.qsMoList = new HashMap<String, ArrayList<Integer>>();
//					es.submit(new Runnable() {
//						@Override
//						public void run() {
							tmpS.clear();
//						}
//					});

					ArrayList<GNode> gkplusOne = BuildQSGraph(EkplusOne, i);
					if (gkplusOne != null)
						this.GraphList.add(gkplusOne);
					if (this.GraphList.size() > 1) {
						UpdateQSGraphs(this.GraphList, EkplusOne, i);
					}
					if (this.qsMoList.size() > 0) {
						// updateFPTree(this.moList);
//						updateCandidateSet(this.moList, i);
						updateAntecedants(this.qsMoList, i);
					}
				}
			}
			cutQSGraphList(i);
		}
	}

	public void algCore() {
		try {
			ExecutorService es = Executors.newFixedThreadPool(3);
			this.startTime = System.currentTimeMillis();
			Runtime runtime = Runtime.getRuntime();
			long startMemory = bytesToMegabytes(runtime.totalMemory() - runtime.freeMemory());

			this.GraphList.clear();
			for (int i = this.begin; i <= this.end; i++) {
				// if (i % 1000 == 0) {
				// System.out.println("time stamp: " + i);
				// }
				ArrayList<String> EkplusOne = this.FreS.get(i);
				if (EkplusOne != null) {
					if (EkplusOne.size() > 0) {
						final Set<String> tmpS = this.moList;
						this.moList = new HashSet<String>();
						es.submit(new Runnable() {
							@Override
							public void run() {
								tmpS.clear();
							}
						});

						ArrayList<GNode> gkplusOne = BuildGraph(EkplusOne, i);
						if (gkplusOne != null)
							this.GraphList.add(gkplusOne);
						if (this.GraphList.size() > 1) {
							UpdateGraphs(this.GraphList, EkplusOne, i);
						}
						if (this.moList.size() > 0) {
							// updateFPTree(this.moList);
							updateCandidateSet(this.moList, i);
						}
						EkplusOne = this.FreS.remove(i);
						EkplusOne = null;
					}
				}
				cutGraphList(i);
			}
			getFrequent();
			// long end1 = System.currentTimeMillis();
			// this.deltaT4MiningAnt += end1 - start1;
			// end of mining antecedent
			// begin OAR-Span algorithm
			// begin to mine rule
			System.out.println("Mining frequent antecedent is over.");
			System.out.println("Frequent episodes: " + this.frequentEpisode.size());

			getQSPattern();
			getSuperSetRange();

			String qs = String.join(",", this.parameter.getQs());
			if(this.superSet.get(qs) == null){
				throw new IllegalArgumentException("没有目标片段");
			}

			insertionSort(this.superSet.get(qs));
			int count = 0, counts = this.frequentEpisode.size();
			for (Entry<String, HashSet<Integer>> antecedent : this.frequentEpisode.entrySet()) {
				count++;
				System.out.println(count + "/" + counts);
				ArrayList<ARNode> ARTree = new ArrayList<ARNode>();

				HashSet<Integer> tmlist = antecedent.getValue();

				int antecedentSize = antecedent.getKey().length();
				int qeLength = 0;
				for(int i = 0; i < this.parameter.getQs().size(); i++){
					qeLength += this.parameter.getQs().get(i).length();
				}
				int qsPatternSize = antecedentSize + 2 + this.parameter.getQs().size() - 1 + qeLength;
				HashSet<Integer> filteredSet = new HashSet<>();
				HashSet<Integer> qsSpan = new HashSet<>();
				HashSet<Integer> qsSpan2 = new HashSet<>();
				for (int num : tmlist) {
//					isInRange(num, this.superSet.get(qs), filteredSet, qsSpan, qsSpan2);
					FilterSuperset(num, this.superSet.get(qs), filteredSet, qsSpan, qsSpan2);
				}
//				for (int num : filteredSet) {
//					FilterSuperset(num, this.superSet.get(qs), filteredSet, qsSpan, qsSpan2);
//				}
				if(filteredSet.isEmpty()){
					continue;
				}
				List<Integer> list = new ArrayList<>(qsSpan);
				List<Integer> list2 = new ArrayList<>(qsSpan2);
				Integer maxValue = Collections.max(list);
				Integer maxValue2 = Collections.max(list2);
				Integer minValue = Collections.min(list2);

				ARRoot root = new ARRoot(antecedent.getKey(), antecedent.getValue(), filteredSet, qsSpan);
				ARTree.add(root);
				double bound = antecedent.getValue().size() * this.parameter.getMin_confidence();
				int start = 1;
				int stop = ARTree.size();
				for (int i = 1; i <= this.parameter.getEpsilon(); i++) {
					if(i == maxValue + 1){
						for (int j = 1; j < ARTree.size(); j++) {
							ARNoRoot node = (ARNoRoot) ARTree.get(j);
							node.setDead(true);
						}
						for (int j = 1; j < ARTree.size(); j++) {
							ARNoRoot node = (ARNoRoot) ARTree.get(j);
							if(node.getEvent().equals(this.parameter.getQs().get(0))){
								SaveDescendant(ARTree, node);
							}
						}
					}
					else if (i == maxValue2 + 1){
						HashSet<Integer> temp = new HashSet<>();
						for (int j = 1; j < ARTree.size(); j++) {
							ARNoRoot node = (ARNoRoot) ARTree.get(j);
							if(!node.isDead()){
								node.setDead(true);
								temp.add(j);
							}
						}
						for (int j:temp){
							ARNoRoot node = (ARNoRoot) ARTree.get(j);
							if(node.getEvent().equals(this.parameter.getQs().get(1))){
								SaveDescendant(ARTree, node);
							}
						}
					}

					ArrayList<ARNode> newNodes = new ArrayList<ARNode>();
					// long start2 = System.currentTimeMillis();
					newNodes = ScanEventSet(i, root, newNodes, true, minValue, qsPatternSize);
					// long end2 = System.currentTimeMillis();
					// this.delta2 += end2 - start2;
					if (i > 1) {
						for (ARNode q : newNodes) {
							HashSet<Integer> skipSet = new HashSet<Integer>();
							ARNoRoot qq = (ARNoRoot) q;
							String event = qq.getEvent();

							for (int nodeNum = start; nodeNum < stop; nodeNum++) {
								ARNoRoot s = (ARNoRoot) ARTree.get(nodeNum);
								if(s.isDead()){
									continue;
								}
								if (!skipSet.contains(nodeNum)) {
									// long start3 = System.currentTimeMillis();

									int delta = i - s.getDistance();

									HashSet<Integer> S = new HashSet<Integer>();
									for (Integer t : s.getTmlist()) {
										S.add(t + delta);
									}
									S.retainAll(qq.getTmlist());

									if (S.size() >= bound) {
										// get a new significant rule
										Integer support = S.size();
										ARNoRoot node = null;
										ArrayList<Integer> spans = new ArrayList<Integer>();
										String newRule = "";

										Double confidence = support.doubleValue() / (double) tmlist.size();
										node = new ARNoRoot(event, S, i);
										AERule prefixRule = s.getRule();
										newRule = prefixRule.getName() + "@" + event;
										spans.addAll(prefixRule.getSpans());
										spans.add(delta);

										AERule rule = new AERule(newRule, spans, support, confidence);
										if(i >= minValue){
											if(newRule.length() > qsPatternSize) this.validRules.add(rule);
											if(newRule.length() == qsPatternSize && event.equals(
													this.parameter.getQs().get(this.parameter.getQs().size() - 1))
											)
												this.validRules.add(rule);
										}
										node.setRule(rule);
										ARTree.add(node);
										if (s.getChildren() == null) {
											HashSet<Integer> children = new HashSet<Integer>();
											children.add(ARTree.size() - 1);
											s.setChildren(children);
										} else {
											s.getChildren().add(ARTree.size() - 1);
										}
									}

									else {
										if (s.getChildren() != null)
											skipSet.addAll(s.getChildren());
									}
									// long end3 = System.currentTimeMillis();
									// deltaT3 += end3 - start3;
								}

								else {
									if (s.getChildren() != null)
										skipSet.addAll(s.getChildren());
								}
							}

							final HashSet<Integer> tmpS = skipSet;
							es.submit(new Runnable() {
								@Override
								public void run() {
									tmpS.clear();
								}
							});
						}
					}

//					ARTree.addAll(newNodes);
					if(i < maxValue) ARTree.addAll(newNodes);

					if(i == maxValue) {
						for(ARNode node : newNodes){
							ARNoRoot newNode = (ARNoRoot) node;
							if(newNode.getEvent().equals(this.parameter.getQs().get(0))){
								ARTree.add(node);
							}
						}
					}

					stop = ARTree.size();

					final ArrayList<ARNode> tmpS = newNodes;
					es.submit(new Runnable() {
						@Override
						public void run() {
							tmpS.clear();
						}
					});
				}
			}
			System.out.println("后处理前Number of Rules: " + this.validRules.size());

			getQsRule();

			System.out.println("Number of frequent episodes: "
					+ this.frequentEpisode.size());
			this.endTime = System.currentTimeMillis();
			this.deltaTime += this.endTime - this.startTime;

			long endMemory = bytesToMegabytes(runtime.totalMemory() - runtime.freeMemory());
			long memoryUsed = endMemory - startMemory;
			System.out.println("程序运行时实际使用的内存量: " + memoryUsed + " MB");

			es.shutdown();
			es.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
			System.out.println("Number of Rules: " + this.validRules.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void isInRange(int num, ArrayList<ArrayList<Integer>> intervals, HashSet<Integer> filteredSet, HashSet<Integer> qsSpan, HashSet<Integer> qsSpan2) {
		for (ArrayList<Integer> interval : intervals) {
			// 0,1 -> [2, 0)
			if (num >= interval.get(2) && num < interval.get(0)) {
				filteredSet.add(num);
				qsSpan.add(interval.get(0) - num);
				qsSpan2.add(interval.get(1) - num);
			}
		}
	}

	private void FilterSuperset(int num, ArrayList<ArrayList<Integer>> intervals, HashSet<Integer> filteredSet, HashSet<Integer> qsSpan, HashSet<Integer> qsSpan2) {
		int target = -1;
		for(int i = 1; target < 0 && i <= this.parameter.getEpsilon(); i++){
			target = binarySearchLeftmost(intervals, num+i);
		}
//		int target = binarySearchRightmost(intervals, num+1);
		if(target < 0) return;
		for (int i = target; i < intervals.size(); i++) {
			ArrayList<Integer> interval = intervals.get(i);
			// 18 - 5 = 13 > 12
			if(interval.get(0) - this.parameter.getEpsilon()> num)
				break;
			if (num >= interval.get(2) && num < interval.get(0)) {
				filteredSet.add(num);
				qsSpan.add(interval.get(0) - num);
				qsSpan2.add(interval.get(1) - num);
			}
		}
	}

	private ArrayList<ARNode> ScanEventSet(int offset, ARNode p, ArrayList<ARNode> newLeaves, boolean isRoot, Integer minValue, int qsPatternSize) {
		// TODO Auto-generated method stub
		HashSet<Integer> qsList = ((ARRoot) p).getQsList();
		HashSet<Integer> tmlist = null;
		if (isRoot) {
			tmlist = ((ARRoot) p).getTmlist();
		} else {
			tmlist = ((ARNoRoot) p).getTmlist();
		}
		Map<String, HashSet<Integer>> tmp = new HashMap<String, HashSet<Integer>>();
		for (Integer timestamp : qsList) {
			int targetTimestamp = timestamp + offset;
			ArrayList<String> eventSet = this.ALLS1.get(targetTimestamp);
			if (eventSet != null) {
				for (String event : eventSet) {
					if (tmp.containsKey(event)) {
						tmp.get(event).add(targetTimestamp);
					} else {
						HashSet<Integer> newTimeList = new HashSet<Integer>();
						newTimeList.add(targetTimestamp);
						tmp.put(event, newTimeList);
					}
				}
			}
		}
		if (tmp.size() > 0) {
			double bound = (double) tmlist.size() * this.parameter.getMin_confidence();
			for (Entry<String, HashSet<Integer>> entry : tmp.entrySet()) {
				Integer support = entry.getValue().size();
				if (support >= bound) {
					// can get a new significant rule
					ARNoRoot node = null;
					ArrayList<Integer> spans = new ArrayList<Integer>();
					String newRule = "";
					Double confidence = support.doubleValue() / (double) tmlist.size();
					if (isRoot) {
						node = new ARNoRoot(entry.getKey(), entry.getValue(), offset);
						newRule = ((ARRoot) p).getEpisode() + "->" + entry.getKey();
						spans.add(offset);
					} else {
						node = new ARNoRoot(entry.getKey(), entry.getValue(), ((ARNoRoot) p).getDistance() + offset);
						AERule prefixRule = ((ARNoRoot) p).getRule();
						newRule = prefixRule.getName() + "@" + entry.getKey();
						spans.addAll(prefixRule.getSpans());
						// int distance = offset - spans.get(spans.size() - 1);
						spans.add(offset);
					}
					AERule rule = new AERule(newRule, spans, support, confidence);
					if(offset >= minValue){
						if(newRule.length() > qsPatternSize) this.validRules.add(rule);
						if(newRule.length() == qsPatternSize && entry.getKey().equals(
								this.parameter.getQs().get(this.parameter.getQs().size() - 1))
						){
							this.validRules.add(rule);
						}
					}

					node.setRule(rule);
					newLeaves.add(node);
				}
			}
		}
		return newLeaves;
	}

	private void getFrequent() {
		// TODO Auto-generated method stub
		for (Entry<String, HashSet<Integer>> entry : this.candidates.entrySet()) {
			String episode = entry.getKey();
			HashSet<Integer> tmlist = entry.getValue();
			if (tmlist.size() >= this.parameter.getMin_support()) {
				this.frequentEpisode.put(episode, tmlist);
			}
		}
	}

	private void updateCandidateSet(Set<String> moList2, int endtime) {
		// TODO Auto-generated method stub
		for (String mo : moList2) {
			if (this.candidates.containsKey(mo)) {
				this.candidates.get(mo).add(endtime);
			} else {
				HashSet<Integer> tmp = new HashSet<Integer>();
				tmp.add(endtime);
				this.candidates.put(mo, tmp);
			}
		}
	}

	private void updateAntecedants(Map<String, ArrayList<Integer>> moList2, int endtime) {
		// TODO Auto-generated method stub
		for(Entry<String, ArrayList<Integer>> entry: moList2.entrySet()) {
			while(entry.getValue().size() > 0){
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.add(entry.getValue().get(entry.getValue().size() - 1));
				entry.getValue().remove(entry.getValue().size() - 1);
				temp.add(endtime);
				if (this.superSet.containsKey(entry.getKey())) {
					this.superSet.get(entry.getKey()).add(temp);
				} else {
					ArrayList<ArrayList<Integer>> tmp = new ArrayList<ArrayList<Integer>>();
					tmp.add(temp);
					this.superSet.put(entry.getKey(), tmp);
				}
			}
		}
//		for (String mo : moList2) {
//			if (this.antecedants.containsKey(mo)) {
//				this.antecedants.get(mo).add(endtime);
//			} else {
//				HashSet<Integer> tmp = new HashSet<Integer>();
//				tmp.add(endtime);
//				this.antecedants.put(mo, tmp);
//			}
//		}
	}

	private void cutGraphList(int timestamp) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<GNode>> delList = new ArrayList<ArrayList<GNode>>();
		for (ArrayList<GNode> g : this.GraphList) {
			if (timestamp - g.get(0).getTe() >= this.parameter.getDelta() - 1) {
				delList.add(g);
			} else {
				break;
			}
		}
		this.GraphList.removeAll(delList);
		// delList = null;
	}

	private void cutQSGraphList(int timestamp) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<GNode>> delList = new ArrayList<ArrayList<GNode>>();
		for (ArrayList<GNode> g : this.GraphList) {
			if (timestamp - g.get(0).getTe() > this.parameter.getEpsilon() - 2) {
				delList.add(g);
			} else {
				break;
			}
		}
		this.GraphList.removeAll(delList);
		// delList = null;
	}


	private void UpdateGraphs(List<ArrayList<GNode>> graphList2, ArrayList<String> ekplusOne, int timestamp) {
		// TODO Auto-generated method stub
		if (ekplusOne.size() > 0) {
			ArrayList<GNode> leafNodes = new ArrayList<GNode>();
			for (int i = graphList2.size() - 2; i >= 0; i--) {//
				// vertex set
				ArrayList<GNode> gnSet = graphList2.get(i);
				if (gnSet.size() == 0)
					continue;

				leafNodes.clear();
				for (String event : ekplusOne) {
					GNode gn = new GNode(event, timestamp, this.ESize);
					leafNodes.add(gn);
				}

				for (GNode prefix : gnSet) {
					Set<String> lmoSet = prefix.getLmoSet();

					for (GNode gn : leafNodes) {
						String event = gn.getName();

						if (!prefix.isDead()) {
							if (prefix.getChildren()[this.eventMap.get(event)] == false) {
								prefix.getChildren()[this.eventMap.get(event)] = true;
								event = "@" + event;
								String oc = "";
								for (String lmo : lmoSet) {
									oc = lmo + event;
									gn.getLmoSet().add(oc);
									this.moList.add(oc);
								}
							}
						}
					}
					lmoSet.removeAll(this.moList);
					if (prefix.getLmoSet().size() == 0) {
						prefix.setDead(true);
					}
				}
				gnSet.addAll(leafNodes);
			}
			leafNodes = null;
		}
	}

	private void UpdateQSGraphs(List<ArrayList<GNode>> graphList2, ArrayList<String> ekplusOne, int timestamp) {
		// TODO Auto-generated method stub
		if (ekplusOne.size() > 0) {
			int qeLength = 0;
			for(int i = 0; i < this.parameter.getQs().size(); i++){
				qeLength += this.parameter.getQs().get(i).length();
			}
			qeLength += this.parameter.getQs().size() - 1;

			ArrayList<GNode> leafNodes = new ArrayList<GNode>();
			for (int i = graphList2.size() - 2; i >= 0; i--) {//
				// vertex set
				ArrayList<GNode> gnSet = graphList2.get(i);
				if (gnSet.size() == 0)
					continue;

				leafNodes.clear();
				for (String event : ekplusOne) {
					GNode gn = new GNode(event, timestamp, this.ESize);
					leafNodes.add(gn);
				}

				for (GNode prefix : gnSet) {
					Set<String> lmoSet = prefix.getLmoSet();

					for (GNode gn : leafNodes) {
						String event = gn.getName();

						if (!prefix.isDead()) {
//							if (prefix.getChildren()[this.eventMap.get(event)] == false) {
//								prefix.getChildren()[this.eventMap.get(event)] = true;
							event = "," + event;
							String oc = "";
							for (String lmo : lmoSet) {
								oc = lmo + event;
								// 大于qs长度则保存在qsMoList，不再增长
								if(oc.length() >= qeLength){
									if(this.qsMoList.containsKey(oc)){
										this.qsMoList.get(oc).add(gnSet.get(0).getTe());
									}else {
										ArrayList<Integer> tmp = new ArrayList<Integer>();
										tmp.add(gnSet.get(0).getTe());
										this.qsMoList.put(oc, tmp);
									}
								}else{
									gn.getLmoSet().add(oc);
								}

							}
						}
					}
//					lmoSet.removeAll(this.moList);
					if (prefix.getLmoSet().size() == 0) {
						prefix.setDead(true);
					}
				}
				gnSet.addAll(leafNodes);
			}
			leafNodes = null;
		}
	}

	private ArrayList<GNode> BuildGraph(ArrayList<String> ekplusOne, int timestamp) {
		// TODO Auto-generated method stub
		ArrayList<GNode> g = new ArrayList<GNode>();
		if (ekplusOne.size() == 0)
			return g;
		for (String e : ekplusOne) {
			GNode node = new GNode(e, timestamp, this.ESize);
			node.getLmoSet().add(e);
			g.add(node);
			this.moList.add(e);
		}
		return g;
	}

	private ArrayList<GNode> BuildQSGraph(ArrayList<String> ekplusOne, int timestamp) {
		// TODO Auto-generated method stub
		ArrayList<GNode> g = new ArrayList<GNode>();
		if (ekplusOne.size() == 0)
			return g;
		for (String e : ekplusOne) {
			GNode node = new GNode(e, timestamp, this.ESize);
			if(e.equals(this.parameter.getQs().get(0))){
				node.getLmoSet().add(e);
			}
			g.add(node);
//			this.moList.add(e);
		}
		return g;
	}

	public void printStats(String filename) {
		// TODO Auto-generated method stub
		try {
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(filename, true), "UTF-8"));

			// String outString = String.valueOf("OAR-Span\n"+deltaTime + "\t"
			// + deltaT4MiningAnt + "\t" + this.delta2 + "\t"
			// + this.deltaT3);
			String outString = String.valueOf(deltaTime);

			writer.write(outString + "\n");
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeRule2File(String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
			// rankRuleBySupport();
			bw.write("Rule|Spans|Support|Confidence\n");
			for (AERule r : this.validRules) {
//				if(r.getName().charAt(0) == '1'){
////					bw.write(r.getName().charAt(0)+r.getName().charAt(1)+ "\n");
//					bw.write(r.getName() + "|" + r.getSpans().toString() + "|" + r.getSupport() + "|" + r.getConfidence()
//						+ "\n");
//				}
				// if (r.getSpans().size() > 1) {
				bw.write(r.getName() + "|" + r.getSpans().toString() + "|" + r.getSupport() + "|" + r.getConfidence()
						+ "\n");
				// }

			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rankRuleBySupport() {
		qSort2(this.validRules, 0, this.validRules.size() - 1);

	}

	private void qSort2(List<AERule> rules, int low, int high) {
		// TODO Auto-generated method stub
		int i, j;
		if (low < high) {
			i = low;
			j = high;
			AERule r = rules.get(i);
			while (i < j) {
				while (i < j && rules.get(j).getSupport() < r.getSupport()) {
					j--;
				}
				if (i < j) {
					rules.set(i, rules.get(j));
					i++;
				}
				while (i < j && rules.get(i).getSupport() > r.getSupport()) {
					i++;
				}
				if (i < j) {
					rules.set(j, rules.get(i));
					j--;
				}
			}
			rules.set(i, r);
			qSort2(rules, low, i - 1);
			qSort2(rules, i + 1, high);
		}
	}

	public void rankRuleByConfidence() {
		qSort(this.validRules, 0, this.validRules.size() - 1);

	}

	private void qSort(List<AERule> rules, int low, int high) {
		// TODO Auto-generated method stub
		int i, j;
		if (low < high) {
			i = low;
			j = high;
			AERule r = rules.get(i);
			while (i < j) {
				while (i < j && (rules.get(j).getConfidence()) < (r.getConfidence())) {
					j--;
				}
				if (i < j) {
					rules.set(i, rules.get(j));
					i++;
				}
				while (i < j && (rules.get(i).getConfidence()) > (r.getConfidence())) {
					i++;
				}
				if (i < j) {
					rules.set(j, rules.get(i));
					j--;
				}
			}
			rules.set(i, r);
			qSort(rules, low, i - 1);
			qSort(rules, i + 1, high);
		}
	}

	private void SaveDescendant(ArrayList<ARNode> ARTree,ARNoRoot node){
		node.setDead(false);
		if(node.getChildren() != null){
			for(int children : node.getChildren()){
				SaveDescendant(ARTree, ((ARNoRoot)ARTree.get(children)));
			}
		}
	}

	private static void insertionSort(ArrayList<ArrayList<Integer>> arrayList) {
		if(arrayList != null) {
			int n = arrayList.size();
			for (int i = 1; i < n; i++) {
				int key = arrayList.get(i).get(0);
				int j = i - 1;

				while (j >= 0 && arrayList.get(j).get(0) > key) {
					arrayList.set(j + 1, arrayList.get(j));
					j--;
				}

				arrayList.set(j + 1, arrayList.get(i));
			}
		}
	}

	private static int binarySearchLeftmost(ArrayList<ArrayList<Integer>> arrayList, int target) {
		if(arrayList == null) return 0;
		int left = 0;
		int right = arrayList.size() - 1;
		int result = -1;

		while (left <= right) {
			int mid = left + (right - left) / 2;
			int midValue = arrayList.get(mid).get(0);

			if (midValue == target) {
				result = mid;
				right = mid - 1;
			} else if (midValue < target) {
				left = mid + 1;
			} else {
				right = mid - 1;
			}
		}

		return result;
	}

	private void getSuperSetRange(){
		Iterator<String> iterator = this.superSet.keySet().iterator();
		String qs = String.join(",", this.parameter.getQs());
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (!key.equals(qs)) {
				iterator.remove();
			}
		}

		for (Entry<String, ArrayList<ArrayList<Integer>>> entry : this.superSet.entrySet()) {
			String key = entry.getKey();
			ArrayList<ArrayList<Integer>> value = entry.getValue();
			for(ArrayList<Integer> list :value){
				int range = list.get(0) - (this.parameter.getEpsilon() - (list.get(1) - list.get(0)));
				if(range <= 0) range = 1;
				list.add(range);
			}
		}
	}

	private void getQsRule(){
		Iterator<AERule> iterator = this.validRules.iterator();
		while (iterator.hasNext()) {
			AERule rule = iterator.next();
			String name = rule.getName();
//			String qs = String.join("", this.parameter.getQs());

			int index = name.indexOf('>');
			int currentIndex = 0;

			if (index != -1 && index < name.length() - 1) {
				String ruleAfterSymbol = name.substring(index + 1);
				for (int i = 0; i < this.parameter.getQs().size(); i++) {
//					char letter = qs.charAt(i);

					currentIndex = ruleAfterSymbol.indexOf(this.parameter.getQs().get(i), currentIndex);
					if (currentIndex == -1) {
						iterator.remove();
						break;
					}
					if(currentIndex > 0 && ruleAfterSymbol.charAt(currentIndex-1) != '@') {
						iterator.remove();
						break;
					}
					if(currentIndex < ruleAfterSymbol.length()-this.parameter.getQs().get(i).length() && ruleAfterSymbol.charAt(currentIndex+this.parameter.getQs().get(i).length()) != '@'){
						iterator.remove();
						break;
					}
					currentIndex++;
				}
			}
		}
	}

	private static long bytesToMegabytes(long bytes) {
		return bytes / (1024 * 1024);
	}
}
