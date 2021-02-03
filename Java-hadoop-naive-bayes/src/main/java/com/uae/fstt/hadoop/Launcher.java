package com.uae.fstt.hadoop;

import org.apache.hadoop.util.ToolRunner;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Fadoua GHEMARI
 */
public class Launcher {

    public static void main(String[] args) {
        try {
            ToolRunner.run(new NaiveBayesLoaderJob(), args);
        } catch (Exception ex) {
            Logger.getLogger(NaiveBayesLoaderJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
