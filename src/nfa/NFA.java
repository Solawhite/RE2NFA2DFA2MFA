package nfa;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import print.ConsoleTable;
import simple.Cell;
import simple.Pair;

public class NFA {

	private int restate = 0;
	
	private String re;
	private String reJoined;
	private String rePostfix;

	private String[] letter;
	private Pair pair;
	
	private ConsoleTable table;

	public NFA(String re) {
		this.re = re;
		reJoined = null;
		rePostfix = null;
		Set<Character> temp = new HashSet<>();
		for(int i=0;i<this.re.length();i++){
			if(is_letter(this.re.charAt(i))){
				temp.add(this.re.charAt(i));
			}
		}
		letter = new String[temp.size()+2];
		Object []tempObj = temp.toArray();
		int i=0;
		letter[i] = "";
		for (;i<tempObj.length;i++) {
			letter[i+1] = (char)tempObj[i]+"";
		}
		letter[i+1] = "EPSILON";
		table = new ConsoleTable(letter.length, true);
		table.appendRow();
		for (String string : letter) {
			table.appendColum(string);
		}
	}

	public Pair getPair() {
		return pair;
	}
	
	public String[] getLetter() {
		return letter;
	}

	public void setPair(Pair pair) {
		this.pair = pair;
	}

	public String add_join_symbol() {
		int length = re.length();
		if(length==1) {
			System.out.println("add join symbol:" + re);
			reJoined = re;
			return re;
		}
		int return_string_length = 0;
		char return_string[] = new char[2 * length + 2];
		char first, second = '0';
		for (int i = 0; i < length - 1; i++) {
			first = re.charAt(i);
			second = re.charAt(i + 1);
			return_string[return_string_length++] = first;
			if (first != '(' && first != '|' && is_letter(second)) {
				return_string[return_string_length++] = '.';
			}
			else if (second == '(' && first != '|' && first != '(') {
				return_string[return_string_length++] = '.';
			}
		}
		return_string[return_string_length++] = second;
		String rString = new String(return_string, 0, return_string_length);
		System.out.println("add join symbol:" + rString);
		System.out.println();
		reJoined = rString;
		return rString;
	}

	private boolean is_letter(char check) {
		{
			if (check >= 'a' && check <= 'z' || check >= 'A' && check <= 'Z')
				return true;
			return false;
		}
	}

	public String postfix() {
		reJoined = reJoined + "#";

		Stack<Character> s = new Stack<>();
		char ch = '#', ch1, op;
		s.push(ch);
		String out_string = "";
		int read_location = 0;
		ch = reJoined.charAt(read_location++);
		while (!s.empty()) {
			if (is_letter(ch)) {
				out_string = out_string + ch;
				ch = reJoined.charAt(read_location++);
			} else {
				ch1 = s.peek();
				if (isp(ch1) < icp(ch)) {
					s.push(ch);
					ch = reJoined.charAt(read_location++);
				} else if (isp(ch1) > icp(ch)) {
					op = s.pop();
					out_string = out_string + op;
				} else {
					op = s.pop();
					if (op == '(')
						ch = reJoined.charAt(read_location++);
				}
			}
		}
		System.out.println("postfix:" + out_string);
		System.out.println();
		rePostfix = out_string;
		return out_string;
	}

	private int isp(char c) {
		switch (c) {
		case '#':
			return 0;
		case '(':
			return 1;
		case '*':
			return 7;
		case '+':
			return 7;
		case '.':
			return 5;
		case '|':
			return 3;
		case ')':
			return 8;
		}
		return -1;
	}

	private int icp(char c) {
		switch (c) {
		case '#':
			return 0;
		case '(':
			return 8;
		case '*':
			return 6;
		case '+':
			return 6;
		case '.':
			return 4;
		case '|':
			return 2;
		case ')':
			return 1;
		}
		return -1;
	}

	public void re2nfa() {
		pair = new Pair();
		Pair temp = new Pair();
		Pair right, left;
		NfaConstructor constructor = new NfaConstructor();
		char ch[] = rePostfix.toCharArray();
		Stack<Pair> stack = new Stack<>();
		for (char c : ch) {
			switch (c) {
			case '|':
				right = stack.pop();
				left = stack.pop();
				pair = constructor.constructNfaForOR(left, right);
				stack.push(pair);
				break;
			case '*':
				temp = stack.pop();
				pair = constructor.constructStarClosure(temp);
				stack.push(pair);
				break;
			case '+':
				temp = stack.pop();
				pair = constructor.constructPlusClosure(temp);
				stack.push(pair);
				break;
			case '.':
				right = stack.pop();
				left = stack.pop();
				pair = constructor.constructNfaForConnector(left, right);
				stack.push(pair);
				break;
			default:
				pair = constructor.constructNfaForSingleCharacter(c);
				stack.push(pair);
				break;
			}
		}
	}

	public void print() {
		restate(this.pair.startNode);
		revisit(this.pair.startNode);
		System.out.println("--------NFA--------");
		table.appendRow();
		printNfa(this.pair.startNode);
		System.out.print(table);
		revisit(this.pair.startNode);
		System.out.println("--------NFA--------");
		System.out.println("start state: " + (this.pair.startNode.getState()));
		System.out.println("end state: " + (this.pair.endNode.getState()));
	}

	private void restate(Cell startNfa) {
		if (startNfa == null || startNfa.isVisited()) {
			return;
		}
		startNfa.setVisited();
		startNfa.setState(restate++);
		restate(startNfa.next);
		restate(startNfa.next2);
	}
	private void revisit(Cell startNfa) {
		if (startNfa == null || !startNfa.isVisited()) {
			return;
		}
		startNfa.setUnVisited();
		revisit(startNfa.next);
		revisit(startNfa.next2);
	}

	private void printNfa(Cell startNfa) {
		if (startNfa == null || startNfa.isVisited()) {
			return;
		}

		startNfa.setVisited();

		printNfaNode(startNfa);
		if (startNfa.next != null) {
			table.appendRow();
		}
		printNfa(startNfa.next);
		printNfa(startNfa.next2);
	}

	private void printNfaNode(Cell node) {
		if (node.next != null) {
			table.appendColum(node.getState());
			if(node.getEdge()==-1) {
				for(int i=0;i<letter.length-2;i++) {
					table.appendColum(" ");
				}
				if (node.next2 != null)
					table.appendColum("{"+node.next.getState()+","+node.next2.getState()+"}");
				else
					table.appendColum("{"+node.next.getState()+"}");
				}
			else {
				int index = getindex(""+(char)node.getEdge());
				for(int i=0;i<letter.length-1;i++) {
					if(i!=index)
						table.appendColum(" ");
					else {
						if (node.next2 != null)
							table.appendColum("{"+node.next.getState()+","+node.next2.getState()+"}");
						else
							table.appendColum("{"+node.next.getState()+"}");
					}
				}
			}
		}
		else {
			table.appendColum(node.getState());
			table.appendColum(" ");
			table.appendColum(" ");
			table.appendRow();
		}
	}
	
	//“”,a,b,EPS
	private int getindex(String ch) {
		for (int i=0;i<letter.length;i++) {
			if(letter[i].equals(ch))
				return i-1;
		}
		return -1;
	}
	
}
