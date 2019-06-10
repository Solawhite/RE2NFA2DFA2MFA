package main;

import java.util.Scanner;

import dfa.DFA;
import mfa.MFA;
import nfa.NFA;


public class test {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("input a regular expression");
		String re = in.nextLine();
		System.out.println("re:" + re);
		NFA nfa = new NFA(re);
		nfa.add_join_symbol();
		nfa.postfix();
		nfa.re2nfa();
		nfa.print();
		
		DFA dfa = new DFA(nfa.getPair(),nfa.getLetter());
		dfa.createDFA();
		dfa.printDFA();
		
		MFA mfa = new MFA(dfa.getDFA(),dfa.getEndState(),dfa.getLetter());
		mfa.minimize();
		mfa.merge();
		mfa.printMFA();
		
		System.out.println();
		System.out.println("re:" + re);
		System.out.println("input test string, Q to exit");
		while(in.hasNextLine()) {
			String string = in.nextLine();
			if(string.equals("Q"))
				break;
			else
				mfa.valid(string);
			System.out.println();
			System.out.println("input test string, Q to exit");
		}
		in.close();
	}
}
