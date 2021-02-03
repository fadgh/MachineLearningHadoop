package com.uae.fstt.hadoop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Fadoua GHEMARI
 */
public class Evaluator {

    public static Map getPropabilities(String filename) {
        HashMap<String, Double> prob = new HashMap<>();
        Scanner in = null;
        try {
            in = new Scanner(new File(filename));
            while (in.hasNextLine()) {
                String[] words = in.nextLine().split("\\s+");
                prob.put(words[0].trim(), Double.parseDouble(words[1]));
            }

            double numInstancesLE50K = prob.get("__<=50K");
            double numInstancesGT50K = prob.get("__>50K");
            loadValues(prob, numInstancesLE50K, numInstancesGT50K);
        } catch (IOException ex) {
        } finally {
            return prob;
        }
    }

    /**
     * @param prob              prob
     * @param numInstancesLE50K numInstancesLE50K
     * @param numInstancesGT50K numInstancesGT50K
     */
    private static void loadValues(HashMap<String, Double> prob, double numInstancesLE50K, double numInstancesGT50K) {
        for (String key : prob.keySet()) {

            if (key.equalsIgnoreCase("__<=50K") || key.equalsIgnoreCase("__>50K")) {
                prob.put(key, prob.get(key) / (numInstancesGT50K + numInstancesLE50K));
            } else {
                if (key.contains("<=50K")) {
                    prob.put(key, prob.get(key) / numInstancesLE50K);
                } else {
                    prob.put(key, prob.get(key) / numInstancesGT50K);
                }
            }
        }
    }

    /**
     * Main
     *
     * @param args args
     */
    public static void main(String[] args) {

        Map<String, Double> prob = getPropabilities("part-r-00000");

        for (String key : prob.keySet()) {

            System.out.println(key + " = " + prob.get(key));
        }
    }
}
