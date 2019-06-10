package nfa;

import java.util.Stack;

import simple.Cell;

public class NfaManager {
    private final int NFA_MAX = 256; 
    private Cell[] nfaStatesArr = null;
    private Stack<Cell> nfaStack = null;
    private int nextAlloc = 0; 
    private int nfaStates = 0; 
    
    public NfaManager()  {
    	nfaStatesArr = new Cell[NFA_MAX];
    	for (int i = 0; i < NFA_MAX; i++) {
    		nfaStatesArr[i] = new Cell();
    	}
    	
    	nfaStack = new Stack<Cell>();
    	
    }
    
    public Cell newNfa()  {
    	Cell nfa = null;
    	if (nfaStack.size() > 0) {
    		nfa = nfaStack.pop();
    	}
    	else {
    		nfa = nfaStatesArr[nextAlloc];
    		nextAlloc++;
    	}
    	
    	nfa.clearState();
    	nfa.setState(nfaStates++);
    	nfa.setEdge(Cell.EPSILON);
    	
    	return nfa;
    }
    
    public void discardNfa(Cell nfaDiscarded) {
    	--nfaStates;
    	nfaDiscarded.clearState();
    	nfaStack.push(nfaDiscarded);
    }
    
   
}
