package nfa;

import simple.Pair;

public class NfaConstructor {
	private NfaManager nfaManager = null;

	public NfaConstructor() {
		nfaManager = new NfaManager();
	}

	public Pair constructStarClosure(Pair pairIn) {
		Pair pairOut = new Pair();
		pairOut.startNode = nfaManager.newNfa();
		pairOut.endNode = nfaManager.newNfa();

		pairOut.startNode.next = pairIn.startNode;
		pairIn.endNode.next = pairOut.endNode;

		pairOut.startNode.next2 = pairOut.endNode;
		pairIn.endNode.next2 = pairIn.startNode;

		pairIn.startNode = pairOut.startNode;
		pairIn.endNode = pairOut.endNode;

		return pairOut;
	}

	public Pair constructPlusClosure(Pair pairIn) {
		Pair pairOut = new Pair();

		pairOut.startNode = nfaManager.newNfa();
		pairOut.endNode = nfaManager.newNfa();

		pairOut.startNode.next = pairIn.startNode;
		pairIn.endNode.next = pairOut.endNode;

		pairIn.endNode.next2 = pairOut.startNode;

		pairIn.startNode = pairOut.startNode;
		pairIn.endNode = pairOut.endNode;

		return pairOut;
	}

	public Pair constructNfaForSingleCharacter(char c) {

		Pair pairOut = new Pair();
		pairOut.startNode = nfaManager.newNfa();
		pairOut.endNode = nfaManager.newNfa();
		pairOut.startNode.next = pairOut.endNode;
		pairOut.startNode.setEdge(c);

		return pairOut;
	}

	public Pair constructNfaForOR(Pair left, Pair right) {
		Pair pair = new Pair();
		pair.startNode = nfaManager.newNfa();
		pair.endNode = nfaManager.newNfa();

		pair.startNode.next = left.startNode;
		pair.startNode.next2 = right.startNode;

		left.endNode.next = pair.endNode;
		right.endNode.next = pair.endNode;

		return pair;
	}

	public Pair constructNfaForConnector(Pair left, Pair right) {
		Pair pairOut = new Pair();
		pairOut.startNode = left.startNode;
		pairOut.endNode = right.endNode;

		left.endNode.next = right.startNode;

		return pairOut;
	}
}
