package print;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConsoleTable {

	private LinkedList<List> rows = new LinkedList<List>();

	private int colum;

	private int[] columLen;

	private static int margin = 2;

	private boolean printHeader = false;

	public ConsoleTable(int colum, boolean printHeader) {
		this.printHeader = printHeader;
		this.colum = colum;
		this.columLen = new int[colum];
	}

	public void appendRow() {
		if (!rows.isEmpty()) {
			List temp = rows.getLast();
			if (temp.isEmpty())
				return;
		}
		List row = new ArrayList(colum);
		rows.add(row);
	}

	/*
	 * public void appendRow() { List row = new ArrayList(colum); rows.add(row); }
	 */

	public ConsoleTable appendColum(Object value) {
		if (value == null) {
			value = "NULL";
		}
		List row = rows.get(rows.size() - 1);
		row.add(value);
		int len = value.toString().getBytes().length;
		if (columLen[row.size() - 1] < len)
			columLen[row.size() - 1] = len;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();

		int sumlen = 0;
		for (int len : columLen) {
			sumlen += len;
		}
		if (printHeader)
			buf.append("|").append(printChar('=', sumlen + margin * 2 * colum + (colum - 1))).append("|\n");
		else
			buf.append("|").append(printChar('-', sumlen + margin * 2 * colum + (colum - 1))).append("|\n");
		for (int ii = 0; ii < rows.size(); ii++) {
			List row = rows.get(ii);
			for (int i = 0; i < colum; i++) {
				String o = "";
				if (i < row.size())
					o = row.get(i).toString();
				buf.append('|').append(printChar(' ', margin)).append(o);
				buf.append(printChar(' ', columLen[i] - o.getBytes().length + margin));
			}
			buf.append("|\n");
			if (printHeader && ii == 0)
				buf.append("|").append(printChar('=', sumlen + margin * 2 * colum + (colum - 1))).append("|\n");
			else
				buf.append("|").append(printChar('-', sumlen + margin * 2 * colum + (colum - 1))).append("|\n");
		}
		return buf.toString();
	}

	private String printChar(char c, int len) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			buf.append(c);
		}
		return buf.toString();
	}

}