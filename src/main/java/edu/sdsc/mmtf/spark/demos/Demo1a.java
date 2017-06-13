package edu.sdsc.mmtf.spark.demos;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.mmtf.api.StructureDataInterface;

import edu.sdsc.mmtf.spark.filters.Resolution;
import edu.sdsc.mmtf.spark.io.MmtfReader;

/**
 * Simple example of reading an MMTF Hadoop Sequence file, filtering the entries by resolution,
 * and counting the number of entries.
 * 
 * @author Peter Rose
 *
 */
public class Demo1a {

	public static void main(String[] args) {

		String path = System.getProperty("MMTF_REDUCED_NEW");
	    if (path == null) {
	    	    System.err.println("Environment variable for Hadoop sequence file has not been set");
	        System.exit(-1);
	    }
	    
	    long start = System.nanoTime();
	    // instantiate Spark. Each Spark application needs these two lines of code.
	    SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(Demo1a.class.getSimpleName());
	    JavaSparkContext sc = new JavaSparkContext(conf);

	    // read entire PDB in MMTF format
	    JavaPairRDD<String, StructureDataInterface> pdb = MmtfReader.readSequenceFile(path,  sc);

	    // filter PDB entries by X-ray resolution. Entries without resolution values, 
	    // e.g., NMR structure will also be filtered out.
	    pdb = pdb.filter(new Resolution(0.0, 2.0));
	    
	    System.out.println("# structures: " + pdb.count());
	    
	    long end = System.nanoTime();
	    System.out.println((end-start)/1E9 + " sec.");
	    
	    // close Spark
	    sc.close();
	}

}
