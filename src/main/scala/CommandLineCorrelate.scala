package com.soteradefense.correlate

import org.apache.spark.SparkContext
import java.io.IOException
import java.util.Properties
import java.io.FileInputStream



/**
 * Offers an interactive command line interface to the correlation engine
 * You can enter one vector at a time to test against the training data.
 * 
 * Training data must first be generated by TrainingPhase
 * 
 * usage:
 * scala -cp ApproximationEngine.jar com.pfi.correlate.CommandLineCorrelate
 */
object CommandLineCorrelate {

    
    // helper func to return user input or a default value
    def getUserInput (prompt: String, defaultValue: String) : (String) = {
      println(prompt)
      val text = scala.Console.readLine
      if (text == "exit!") {System.exit(0)}
      if (text.length == 0) return defaultValue
      return text
    }
    
    
    
    def run (sc:SparkContext,config:Properties) = {
      
      
        println("\n\n***  SPARK CORRELATE ENGINE ***\n\n")
    	println("Enter exit! at any prompt to quit.\n")
    	
    	val engine = Correlator
        engine.sc = sc
        
    	
    	// read input values from stdin to intialize the system
    	engine.limit = getUserInput("Enter # of results to return? [default 100]","100").toInt
        val projection_dir = getUserInput("Enter the directory that contains your training projections:[enter for default:generated_projections]","generated_projections")
        val centroid_dir = getUserInput("Enter the directory that contains your training centroids[enter for default:generated_centroids]:","generated_centroids")
        var training_matrix_path = getUserInput("Enter the path to the vector centroid mapping.[enter for default:training_matrix_mapping_v2.txt]","training_matrix_mapping_v2.txt")
        var original_data_path = getUserInput("Enter path to original data:[default=time_series_data.txt]","example/test_data.tsv")
        
        engine.initialize(projection_dir,centroid_dir,training_matrix_path,original_data_path);
        
        println("\n*************************************")
        println("*** SPARK CORRELATE ENGINE READY! ***")
        println("*************************************\n")
        
        
        // test vector loop
        var inputStr : (String) = null
        var test_series : (Array[Double]) = null
        
        while(true){
          
          try{
        	  inputStr = getUserInput("Enter a test series as comma seperated list of values:","")
              
        	  // results -> array of (corr,(distance,key)) tuples
        	  val results = engine.correlate(inputStr)
        	  
              println("")
              println("RESULTS")
	          println("key\tCorrelation\tApprox. Distance")
	          results.foreach( tuple => {println(tuple._2._2+"\t"+tuple._1+"\t"+tuple._2._1) }) 
	          println()
          
           }
            catch{
            	case e: Exception =>{
            		println("ERROR: "+e.getMessage()+"\n")
            		println()
            	}
            }
          
        }// end the test loop
	    
	    
	  }
  
}
