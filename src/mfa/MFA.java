package mfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import print.ConsoleTable;

public class MFA {
	private List<Character[]> dfa;
	private List<Character[]> mfa = new ArrayList<>();
	private String[] letter;
	private List<Character> endState;
	private ConsoleTable table;
	private Set<Set<Character>> totalSet = new HashSet<>();

	private Map<Character, Character> map = new HashMap<>();
	
	public MFA(List<Character[]> dfa, List<Character> endState, String[] letter) {
		this.dfa = dfa;
		this.endState = endState;
		this.letter = letter;
		table = new ConsoleTable(letter.length - 1, true);
		table.appendRow();
		for (int i = 0; i < letter.length - 1; i++) {
			table.appendColum(letter[i]);
		}
	}


	public void minimize() {
		init(totalSet);
		int count = 0;
		while (true) {
			if (count == totalSet.size())
				break;
			else
				count = 0;
			Set<Set<Character>> copyOfTotalSet = new HashSet<>(totalSet);
			for (Set<Character> set : copyOfTotalSet) {
				if (isIndivisible(set)) {
					count++;
					continue;
				} else {
					minimize(set);
				}
			}
		}
	}

	private void minimize(Set<Character> state) {
		totalSet.remove(state);
		Map <String,String> map = new HashMap<>();
		for (Character character : state) {
			String aString ="";
			for (int i = 1; i < letter.length-1; i++) {
					aString += move(character, letter[i].charAt(0))+"";
			}
			String tempset = map.get(aString);
			if(tempset==null) {
				map.put(aString, character+"");
			}
			else {
				tempset +=character;
				map.put(aString, tempset);
			}
		}
		for(String str : map.values()) {
			Set<Character> set = new HashSet<>();
			for(int i=0;i<str.length();i++)
				set.add(str.charAt(i));
			totalSet.add(set);
		}
	}

	private boolean inTotalSet(Set<Character> temp) {
		if(temp.isEmpty())
			return true;
		Set<Integer> indexs = new HashSet<>();
		for (Character character : temp) {
			indexs.add(getSetNumber(character));
		}
		return indexs.size()==1;
	}

	private int getSetNumber(Character character) {
		int i=0;
		for (Set<Character> a : totalSet) {
			for (Character b : a) {
				if(b.equals(character))
					return i;
			}
			i++;
		}
		return -1;
	}

	private void init(Set<Set<Character>> totalSet) {
		Set<Character> terminal = new HashSet<>();
		Set<Character> nonTerminal = new HashSet<>();
		for (Character[] characters : dfa) {
			if (isEndState(characters[0]))
				terminal.add(characters[0]);
			else
				nonTerminal.add(characters[0]);
		}
		totalSet.add(nonTerminal);
		totalSet.add(terminal);
	}

	private boolean isEndState(Character character) {
		for (Character state : endState) {
			if (state.equals(character))
				return true;
		}
		return false;
	}

	private boolean isIndivisible(Set<Character> set) {
		if (set.size() == 1)
			return true;
		else {
			for (int i = 1; i < letter.length - 1; i++) {
				Set<Character> temp = new HashSet<>();
				for (Character c : set) {
					temp.add(move(c, letter[i].charAt(0)));
				}
				if (inTotalSet(temp))
					continue;
				else {
					return false;
				}
			}
		}
		return true;
	}

	public void printMFA() {
		for (Character[] characters : mfa) {
			table.appendRow();
			for (Character character : characters) {
				table.appendColum(character);
			}
		}
		System.out.println("--------MFA--------");
		System.out.print(table);
		System.out.println("start state: [A]");
		System.out.println("end state: "+endState);
		System.out.println("--------MFA--------");
	}

	public void merge(){
		for (Set<Character> characters : totalSet) {
			if(characters.size()==1)
				continue;
			else {
				int i=0;
				char key = 0;
				for (Character ch : characters) {
					if(i++==0)
						key = ch;
					else 
						map.put(ch, key);
				}
			}
		}
		List<Character[]> tempMFA = new ArrayList<>();
		for (Character[] characters : dfa) {
			if(ignore(characters[0])) {
				endState.remove(characters[0]);
				continue;
			}
			else {
				Character [] newchar = new Character[characters.length];
				int i=0;
				for (Character ch : characters) {
					if(needReplace(ch))
						newchar[i] = map.get(ch);
					else {
						newchar[i] = ch;
					}
						i++;
				}
			tempMFA.add(newchar);
			}
		}
		List<Character> finalstate = new ArrayList<>();
		for (Character[] ch : tempMFA) {
			if(finalstate.contains(ch[0]))
				continue;
			else {
				finalstate.add(ch[0]);
				mfa.add(ch);
			}
		}
		
	}
	private boolean needReplace(Character ch) {
		Character value = map.get(ch);
		return value!=null;
	}


	private boolean ignore(Character character) {
		for (Entry<Character, Character> m :map.entrySet())  {
			if(m.getKey().equals(character))
				return true;
		}
		return false;
	}


	
	@SuppressWarnings("unused")
	private void removeTo(Character c, char newState) {
		for (Character[] characters : dfa) {
			if(characters[0]==c)
				continue;
			else {
				Character[] newchars = new Character[characters.length];
				int i=0;
				for (Character character : characters) {
					if(character == c)
						newchars[i]=newState;
					else
						newchars[i]=character;
					i++;
				}
				mfa.add(newchars);
			}
		}
	}

	private Character move(Character oriState, char input) {
		for (Character[] characters : dfa) {
			if (characters[0] != oriState)
				continue;
			else {
				int index = getIndex(input);
				return characters[index]==null?null:characters[index];
			}
		}
		return null;
	}

	private int getIndex(char input) {
		for (int i = 1; i < letter.length - 1; i++) {
			if (letter[i].charAt(0) == input)
				return i;
		}
		return -1;
	}
	
	private Character move(char oriState, int index) {
		for (Character[] characters : mfa) {
			if (characters[0] != oriState)
				continue;
			else {
				return characters[index];
			}
		}
		return null;
	}
	
	public void valid(String str) {
		Character reachableState = new Character('A');
		for(int i=0;i<str.length();i++) {
			int index = getIndex(str.charAt(i));
			if(index == -1) {
				StringBuilder error = new StringBuilder();
				error.append(str);
				error.append(" is incorrect \r\n");
				for(int q=0;q<i;q++)
					error.append(" ");
				error.append("↑");
				System.err.println(error.toString());
				return;
			}else {
				reachableState = move(reachableState, index);
				if(reachableState==null) {
					StringBuilder error = new StringBuilder();
					error.append(str);
					error.append(" is incorrect \r\n");
					for(int q=0;q<i;q++)
						error.append(" ");
					error.append("↑");
					System.err.println(error.toString());
					return;
				}
			}
		}
		if(isEndState(reachableState)) {
			System.out.println(str+" is correct");
		}
		else {
			System.err.println(str+" is incorrect，for the input of ungrammatical symbols \""+str.charAt(str.length()-1)+"\"(not finish)");
		}
	}
}
