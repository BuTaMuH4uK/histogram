import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JFrame;

public class Main {
	final static String input = "C:\\Users\\butam\\workspace\\Гистограмма(Индив. зад.)\\Keys.txt";	// входной файл
	final static String answer = "C:\\Users\\butam\\workspace\\Гистограмма(Индив. зад.)\\endFile.txt";		// файл ответа
	final static String locationMap = "C:\\Users\\butam\\workspace\\Гистограмма(Индив. зад.)\\MapScript2\\Debug\\MapScript2.exe";	// скрипт - Map
	final static String locationReduce = "C:\\Users\\butam\\workspace\\Гистограмма(Индив. зад.)\\ReduceScript\\Debug\\ReduceScript.exe";	// скрипт - Reduce
	final static String locationGenerateCountColumn = "C:\\Users\\butam\\workspace\\Гистограмма(Индив. зад.)\\GenerateCountColumnScript\\Debug\\GenerateCountColumnScript.exe";	// скрипт - GenerateCountColumnScript
	final static String locationGenerateKeys = "C:\\Users\\butam\\workspace\\Гистограмма(Индив. зад.)\\GenerateKeysScript\\Debug\\GenerateKeysScript.exe";	// скрипт - GenerateKeysScript
	final static String locationGenerateCountKey = "C:\\Users\\butam\\workspace\\Гистограмма(Индив. зад.)\\GenerateCountKeyScript\\Debug\\GenerateCountKeyScript.exe";	// скрипт - GenerateCountKey
	
	static BufferedReader reader;
	static File in;
	static String str;
	static String[] k;
	static int n;
	
	public static void main(String[] args) throws IOException{		
		String str1, str2, key = null;
		int num = 1, byteSize = 4096, numFiles = 0, curNumFiles = 0, copyCurNumFiles, value = 0;
		byte curByte = 1, buffer;
		String[] words, arrSplit1, arrSplit2;
		byte[] arrByte, endArrByte = new byte[byteSize];
		File out = null, file1, file2, fileMerge = null, finalFile, columnFile;
		RandomAccessFile fileBeforeMap;
		RandomAccessFile fileAfterMerge;
		StringBuffer strBuf, endStrBuf;
		BufferedReader endReader = null;
		PrintWriter writer, endWriter, bufWriter;
		ProcessBuilder pb, pb_map, pb_reduce;
		Process proc, proc_map, proc_reduce;
		ArrayList<Node> store;
		
		//генерирую число столбцов(5-25)
		pb = new ProcessBuilder(locationGenerateCountColumn);
		out = new File ("CountColumn.txt");
		pb.redirectOutput(out);
    	proc = pb.start();
    	try { 
    		proc.waitFor();
    	} catch (InterruptedException e) {e.printStackTrace();}
		
		//генерирую число ключей(10000-99999)
		pb = new ProcessBuilder(locationGenerateCountKey);
    	out = new File ("CountKey.txt");
    	pb.redirectOutput(out);
    	proc = pb.start();
    	try { 
    		proc.waitFor();
    	} catch (InterruptedException e) {e.printStackTrace();}
    	
    	//генерирую ключи
    	pb = new ProcessBuilder(locationGenerateKeys);
    	in = new File ("CountKey.txt");
    	out = new File ("Keys.txt");
    	pb.redirectInput(in);
    	pb.redirectOutput(out);
    	proc = pb.start();
    	try { 
    		proc.waitFor();
    	} catch (InterruptedException e) {e.printStackTrace();}
    	in.delete();
		
		// дробление файла и выполнение для каждой части скрипта map
		
		fileBeforeMap = new RandomAccessFile(input, "r");
		while (num <= fileBeforeMap.length()) {
			numFiles++;
			fileBeforeMap.seek(--num);	
			strBuf = new StringBuffer("");
			out = new File(String.valueOf(numFiles) + ".txt");
			writer = new PrintWriter(out.getAbsoluteFile());
			arrByte = new byte[byteSize];
			fileBeforeMap.read(arrByte);
			for (int i = 0; i < arrByte.length; i++) {
				strBuf.append((char) arrByte[i]);
			}
			num += byteSize;
			while(curByte == 1 && num <= fileBeforeMap.length()){
				fileBeforeMap.seek(num++);
				curByte = fileBeforeMap.readByte();
				if ((curByte > 47 && curByte < 58) || curByte == 46) {
					strBuf.append((char)curByte);
					curByte = 1;
				}
			}
			curByte = 1;
			writer.print(strBuf);
			writer.close();
		}
        fileBeforeMap.close();
        for (int w = 1; w <= numFiles; w++) {
        	pb_map = new ProcessBuilder(locationMap);
        	in = new File (String.valueOf(w) + ".txt");
        	out = new File (String.valueOf(w) + "_merge.txt");
        	pb_map.redirectInput(in);
        	pb_map.redirectOutput(out);
        	proc_map = pb_map.start();
        	try { 
        		proc_map.waitFor(); 
        	} catch (InterruptedException e) {e.printStackTrace();}
        	in.delete();
        }
        
        
        // mergesort
        
        for (int w = 1; w <= numFiles; w++) {
        	store = new ArrayList<>();
        	in = new File(String.valueOf(w) + "_merge.txt");
        	try{
        		reader = new BufferedReader(new FileReader(in.getAbsoluteFile()));
        		while ((str = reader.readLine()) != null) {
        			words = str.split("\t");
        			store.add(new Node(words[0], words[1]));
        		}
        		reader.close();
        		in.delete();
        	} catch(IOException e) {System.out.print("Error"); }
        	store.sort(new Comparator<Node>() {
        		public int compare(Node o1, Node o2) {
        			return o1.getKey().compareTo(o2.getKey());
        		}
        	});
        	out = new File(String.valueOf(w) + "_merge_1.txt");
        	try {
        		writer = new PrintWriter(out.getAbsoluteFile());
        		for (int i = 0; i < store.size(); i++) {
        			writer.println(store.get(i).getKey() + "\t" + store.get(i).getWeight());
        		}
        		writer.close();
        	} catch (FileNotFoundException e) { }
        }
        curNumFiles = numFiles;
        for (int e = 1; e <= numFiles; e *= 2) {
        	copyCurNumFiles = curNumFiles / 2;
        	curNumFiles = (curNumFiles + 1) / 2;
        	for (int w = 1; w <= curNumFiles; w++) {
        		file1 = new File(String.valueOf(2 * w - 1) + "_merge_" + String.valueOf(e) + ".txt");
        		if (w == curNumFiles && copyCurNumFiles != curNumFiles) {
        			file1.renameTo(new File(String.valueOf(w) + "_merge_" + String.valueOf(e * 2) + ".txt"));
        			break;
        		}
        		file2 = new File(String.valueOf(2 * w) + "_merge_" + String.valueOf(e) + ".txt");
        		fileMerge = new File(String.valueOf(w) + "_merge_" + String.valueOf(e * 2) + ".txt");
        		BufferedReader reader1 = new BufferedReader(new FileReader(file1.getAbsoluteFile()));
        		BufferedReader reader2 = new BufferedReader(new FileReader(file2.getAbsoluteFile()));
        		writer = new PrintWriter(fileMerge.getAbsoluteFile());
        		str1 = reader1.readLine();
        		str2 = reader2.readLine();
        		if (str1 != null && str2 != null) { 
        			arrSplit1 = str1.split("\t");
            		arrSplit2 = str2.split("\t");
        			while (str1 != null && str2 != null) {
        				if (arrSplit1[0].compareTo(arrSplit2[0]) <= 0) {
        					writer.println(str1);
        					if ((str1 = reader1.readLine()) != null) {
        						arrSplit1 = str1.split("\t");
        					}
        				} else {
        					writer.println(str2);
        					if ((str2 = reader2.readLine()) != null) {
        						arrSplit2 = str2.split("\t");
        					}
        				}
        			}
        		}
        		if (str1 == null) {
        			writer.println(str2);
        			while ((str2 = reader2.readLine()) != null) {
        				writer.println(str2);
        			}
        		} else if (str2 == null) {
        			writer.println(str1);
        			while ((str1 = reader1.readLine()) != null) {
        				writer.println(str1);
        			}
        		}
        		writer.close();
        		reader1.close();
        		reader2.close();
        		file1.delete();
        		file2.delete();
        	}
        }
        
        //снова дробление файла и выполнение для них редьюс с последующим слиянием
        
        fileAfterMerge = new RandomAccessFile (fileMerge.getName(), "r");
        finalFile = new File(answer);
        columnFile = new File("CountColumn.txt");
        endWriter = new PrintWriter(finalFile.getAbsoluteFile());
        while ((int)(buffer = (byte) fileAfterMerge.read(endArrByte)) != -1) {
            endStrBuf = new StringBuffer("");
            bufWriter = new PrintWriter(new File("buffer.txt").getAbsoluteFile());
            reader = new BufferedReader(new FileReader(columnFile.getAbsoluteFile()));
            endStrBuf.append(reader.readLine() + "\n");
            reader.close();
        	for (int i = 0; i < endArrByte.length; i++) {
        		endStrBuf.append((char)endArrByte[i]);
        	}
        	if (buffer == 0) {
        		while ((char)(buffer = fileAfterMerge.readByte()) != '\n') {
        			endStrBuf.append((char)buffer);
        		}
            	endStrBuf.append((char)buffer);
        	}
        	bufWriter.print(endStrBuf);
        	bufWriter.close();
        	pb_reduce = new ProcessBuilder(locationReduce);
        	in = new File ("buffer.txt");
        	out = new File ("buffer1.txt");
        	pb_reduce.redirectInput(in);
        	pb_reduce.redirectOutput(out);
        	proc_reduce = pb_reduce.start();
        	try { 
        		proc_reduce.waitFor(); 
        	} catch (InterruptedException e) {e.printStackTrace();}
        	in.delete();
        	endReader = new BufferedReader(new FileReader(out.getAbsoluteFile()));
        	endStrBuf = new StringBuffer("");
        	while ((str = endReader.readLine()) != null) {
        		endStrBuf.append(str + "\n");
        	}
        	arrSplit1 = endStrBuf.toString().split("\n");
        	arrSplit2 = arrSplit1[0].split("\t");
        	if (arrSplit2[0].equals(key)) {
        		value += Integer.parseInt(arrSplit2[1]);
        		if (arrSplit1.length > 1) {
            		if (key != null) {
            			endWriter.println(key + "\t" + value);
            		}
        			for (int h = 1; h < arrSplit1.length - 1; h++) {
        				endWriter.println(arrSplit1[h]);
        			}
        			arrSplit2 = arrSplit1[arrSplit1.length - 1].split("\t");
        			key = arrSplit2[0];
        			value = Integer.parseInt(arrSplit2[1]);
        		}
        	} else {
        		if (key != null) {
        			endWriter.println(key + "\t" + value);
        		}
        		if (arrSplit1.length > 1) {
        			for (int h = 0; h < arrSplit1.length - 1; h++) {
        				endWriter.println(arrSplit1[h]);
        			}
        		}
        		arrSplit2 = arrSplit1[arrSplit1.length - 1].split("\t");
    			key = arrSplit2[0];
    			value = Integer.parseInt(arrSplit2[1]);
        	}
        	endReader.close();
            out.delete();
        }
        if (key != null) {
        	endWriter.print(key + "\t" + value);
        }
        endWriter.close();
        fileAfterMerge.close();
        fileMerge.delete();
        MyJFrame f = new MyJFrame();
        f.setSize(850, 800);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
	}
	static class MyJFrame extends JFrame {
		public void paint(Graphics g) {
			in = new File("endFile.txt");
			try {
				reader = new BufferedReader(new FileReader(in.getAbsoluteFile()));
				n = 0;
				while ((str = reader.readLine()) != null) {
					k = str.split("\t");
					g.drawRect(50 + n * 30, 750 - Integer.parseInt(k[1]) / 10, 30, Integer.parseInt(k[1]) / 10);
					n++;
				}
			} catch (IOException e) { }
		}
	}
}