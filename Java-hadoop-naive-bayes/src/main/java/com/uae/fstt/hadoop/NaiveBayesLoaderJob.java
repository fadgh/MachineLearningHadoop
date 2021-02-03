package com.uae.fstt.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Fadoua GHEMARI
 */
public class NaiveBayesLoaderJob extends Configured implements Tool {

    private final static String CLUSTER_OUTPUT_PATH = "hdfs://localhost:9000/user/fadoua/data/class/";
    private final static String DATAPATH = "hdfs://fadoua:54310/user/fadoua/data/adult.data";

    static {
        Configuration.addDefaultResource(("C:\\hadoop-3.2.1\\hadoop\\hdfs-site.xml"));
        Configuration.addDefaultResource(("C:\\hadoop-3.2.1\\hadoop\\core-site.xml"));
        Configuration.addDefaultResource(("C:\\hadoop-3.2.1\\hadoop\\mapred-site.xml"));
    }

    @Override
    public int run(String[] strings) throws Exception {
        Configuration conf = getConf();
        try {
            FileSystem fs = FileSystem.get(conf);

            conf.set("in", CLUSTER_OUTPUT_PATH + "discritize/part-r-00000");
            Job jobFirst = new Job(conf);

            jobFirst.setJarByClass(NaiveBayesLoaderJob.class);

            jobFirst.setMapperClass(PreprocessWrapper.class);
            jobFirst.setReducerClass(PreprocessReducer.class);

            jobFirst.setMapOutputKeyClass(Text.class);
            jobFirst.setMapOutputValueClass(DoubleWritable.class);

            jobFirst.setOutputKeyClass(Text.class);
            jobFirst.setOutputValueClass(DoubleWritable.class);

            jobFirst.setInputFormatClass(TextInputFormat.class);
            jobFirst.setOutputFormatClass(SequenceFileOutputFormat.class);

            TextInputFormat.addInputPath(jobFirst, new Path(DATAPATH));
            SequenceFileOutputFormat.setOutputPath(jobFirst, new Path(CLUSTER_OUTPUT_PATH, "discritize"));

            try {
                jobFirst.waitForCompletion(true);

                Job jobSecond = new Job(conf);

                jobSecond.setJarByClass(NaiveBayesLoaderJob.class);

                jobSecond.setInputFormatClass(TextInputFormat.class);
                TextInputFormat.addInputPath(jobSecond, new Path(DATAPATH));
                SequenceFileOutputFormat.setOutputPath(jobSecond, new Path(CLUSTER_OUTPUT_PATH, "class"));

                jobSecond.setMapperClass(NaiveBayesWrapper.class);
                jobSecond.setReducerClass(NaiveBayesReducer.class);

                jobSecond.setMapOutputKeyClass(Text.class);
                jobSecond.setMapOutputValueClass(IntWritable.class);

                jobSecond.setOutputKeyClass(Text.class);
                jobSecond.setOutputValueClass(DoubleWritable.class);

                jobSecond.waitForCompletion(true);
            } catch (InterruptedException | ClassNotFoundException ex) {
                Logger.getLogger(NaiveBayesLoaderJob.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(NaiveBayesLoaderJob.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
