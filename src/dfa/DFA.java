package dfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import print.ConsoleTable;
import simple.Cell;
import simple.Pair;

public class DFA {
	private Pair pair;
	private String[] letter;
	private ConsoleTable table;
	private Map<Set<Integer>, Integer> map;
	private Set<Integer> tempset;
	private Queue<Integer> queue = new LinkedList<>();
	private List<Character[]> dfa = new ArrayList<>();
	private List<Character> endState = new ArrayList<>();
	
	private int state = 'A';

	public DFA(Pair pair, String[] letter) {
		this.pair = pair;
		this.letter = letter;
		table = new ConsoleTable(letter.length - 1, true);
		table.appendRow();
		for (int i = 0; i < letter.length - 1; i++) {
			table.appendColum(letter[i]);
		}
		map = new HashMap<>();
	}

	public List<Character[]> getDFA() {
		List<Character[]> redfa = new ArrayList<>();
		for (Character[] ch : dfa) {
			if(getSet(ch[0])==null||getSet(ch[0]).isEmpty()) {
				continue;
			}
			else {
				Character[] newch = new Character[ch.length];
				for(int i=0;i<ch.length;i++) {
					if(ch[i]==null)
						continue;
					Set<Integer> set = getSet(ch[i]);
					if(set == null||set.isEmpty())
						newch[i] = null;
					else
						newch[i] = ch[i];
				}
				redfa.add(newch);
			}
		}
		return redfa;
	}
	
	public List<Character> getEndState() {
		return endState;
	}
	
	public String[] getLetter() {
		return letter;
	}
	
	public void printDFA() {
		System.out.println();
		System.out.println("--------DFA--------");
		System.out.print(table);
		for (Entry<Set<Integer>, Integer> entry : map.entrySet()) { 
			if(entry.getValue()==-1)
				continue;
			System.out.println((char)entry.getValue().intValue() +" = " + entry.getKey() + (isStart(entry.getKey())?" START ":"") + (isEnd(entry.getKey())?" END ":"") ); 
		}
		System.out.println("--------DFA--------");
	}

	private boolean isStart(Set<Integer> set) {
		for (Integer integer : set) {
			if(integer == pair.startNode.getState())
				return true;
		}
		return false;
	}
	
	private boolean isEnd(Set<Integer> set) {
		for (Integer integer : set) {
			if(integer == pair.endNode.getState()) {
				endState.add(new Character((char)getCharacter(set).intValue()));	
				return true;
			}
		}
		return false;
	}
	
	public void createDFA() {
		tempset = new HashSet<>();
		Set<Integer> start= move(pair.startNode,-1);
		map.put(start, state);
		queue.add(state++);
		while(!queue.isEmpty()) {
			Character[] dfaline = new Character[letter.length-1];
			int character = queue.poll();
			table.appendRow();
			table.appendColum((char)character);
			dfaline[0] = (char)character;
			Set<Integer> set = getSet(character);
			for(int i=1;i<letter.length-1;i++) {
				tempset = new HashSet<>();
				Set<Integer> midset = new HashSet<>();
				for (Integer integer : set) {
					Cell cell = getCell(pair.startNode, integer);
					revisit();
					if(cell==null) {
						continue;
					}
					else if((char)cell.getEdge() == letter[i].charAt(0)) {
						midset.add(cell.next.getState());
					}
				}
				for (Integer integer : midset) {
					Cell cell = getCell(pair.startNode, integer);
					revisit();
					move(cell, -1);
				}
				Integer c = getCharacter(tempset);
				if(c==null) {
					if(tempset.isEmpty()) {
						map.put(tempset, -1);
						table.appendColum("null");
						dfaline[i] = null;
					}
					else {
						queue.add(state);
						table.appendColum((char)state);
						dfaline[i] = (char)state;
						map.put(tempset, state++);
					}
				}
				else {
					if(c==-1) {
						table.appendColum("null");
						dfaline[i] = null;
					}
					else {
						dfaline[i] = (char)c.intValue();
						table.appendColum((char)c.intValue());
					}
				}
			}
			dfa.add(dfaline);
		}
	}

	private Set<Integer> move(Cell startNode, int i) {
		connect(startNode, i);
		revisit();
		return tempset;
	}

	private void connect(Cell cell, int i) {
		if(cell==null||cell.isVisited())
			return;
		cell.setVisited();
		tempset.add(cell.getState());
		if(cell.getEdge()==-1||cell.getEdge()==i) {
			connect(cell.next, i);
			connect(cell.next2, i);
		}
		else
			return;
	}
	
	private Cell getCell(Cell cell, int startstate) {
		if (cell == null || cell.isVisited())
			return null;
		cell.setVisited();
		if (cell.getState() == startstate)
			return cell;
		if (cell.getState() > startstate)
			return null;
		Cell temp1 = getCell(cell.next, startstate);
		Cell temp2 = getCell(cell.next2, startstate);
		if (temp1 != null)
			return temp1;
		if (temp2 != null)
			return temp2;
		return null;
	}
	
	private Integer getCharacter(Set<Integer> set) {
		return map.get(set);
	}
	
	private Set<Integer> getSet(int character) {
		for (Entry<Set<Integer>, Integer> m :map.entrySet())  {
			if(m.getValue()==character)
				return m.getKey();
		}
		return null;
	}
	
	private void revisit(Cell cell) {
		if (cell == null || !cell.isVisited()) {
			return;
		}
		cell.setUnVisited();
		revisit(cell.next);
		revisit(cell.next2);
	}
	private void revisit() {
		pair.startNode.setUnVisited();
		revisit(pair.startNode.next);
		revisit(pair.startNode.next2);
	}
	
	@Override
	public String toString() {
		return tempset.toString();
	}
}
