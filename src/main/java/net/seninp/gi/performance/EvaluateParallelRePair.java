package net.seninp.gi.performance;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import net.seninp.gi.repair.RePairFactory;
import net.seninp.gi.repair.RePairGrammar;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.sax.SAXProcessor;
import net.seninp.jmotif.sax.alphabet.Alphabet;
import net.seninp.jmotif.sax.alphabet.NormalAlphabet;
import net.seninp.jmotif.sax.datastructures.SAXRecords;

public class EvaluateParallelRePair {

  private static final String INPUT_DATA_FNAME = "src/resources/test-data/300_signal1.txt.gz";

  private static SAXProcessor sp = new SAXProcessor();
  private static Alphabet a = new NormalAlphabet();

  public static void main(String[] args) throws IOException, SAXException {

    // read the data
    //
    Date start = new Date();
    InputStream fileStream = new FileInputStream(INPUT_DATA_FNAME);
    InputStream gzipStream = new GZIPInputStream(fileStream);
    Reader decoder = new InputStreamReader(gzipStream);
    BufferedReader br = new BufferedReader(decoder);

    ArrayList<Double> preRes = new ArrayList<Double>();
    String line = null;
    while ((line = br.readLine()) != null) {
      Double num = Double.valueOf(line);
      preRes.add(num);
    }
    br.close();
    double[] res = new double[preRes.size()];
    for (int i = 0; i < preRes.size(); i++) {
      res[i] = preRes.get(i);
    }
    Date finish = new Date();
    System.out.println("read " + res.length + " points in "
        + SAXProcessor.timeToString(start.getTime(), finish.getTime()));

    // perform SAX
    //
    start = new Date();
    SAXRecords tokens = sp.ts2saxViaWindow(res, 120, 6, a.getCuts(3),
        NumerosityReductionStrategy.EXACT, 0.01);
    String str = tokens.getSAXString(" ");
    finish = new Date();

    System.out.println("extracted "
        + Integer.valueOf(str.length() - str.replaceAll(" ", "").length()).toString()
        + " tokens in " + SAXProcessor.timeToString(start.getTime(), finish.getTime()));

    // sequential Re-Pair
    //
    start = new Date();
    RePairGrammar g = RePairFactory.buildGrammar(str);
    finish = new Date();
    System.out.println("inferred " + g.getRules().size() + " RePair rules in "
        + SAXProcessor.timeToString(start.getTime(), finish.getTime()));

  }

}