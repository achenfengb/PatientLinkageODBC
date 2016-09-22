/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientlinkage.Util;

import flexsc.CompPool;
import flexsc.Mode;
import gc.GCSignal;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import patientlinkage.DataType.DBConfig;
import patientlinkage.DataType.DBProperty;
import patientlinkage.DataType.Helper;
import patientlinkage.DataType.PatientLinkage;
import patientlinkage.GarbledCircuit.PatientLinkageGadget;
import patientlinkage.parties.Env;
import patientlinkage.parties.Gen;

/**
 *
 * @author cf
 */
public class Main {

    /**
     * starting linkage algorithm
     * @param args the passing parameters
     */
    public static void startLinkage(String[] args) {
        String file_config = null;
        //String file_data = null;

        String party = "nobody";
       // int ptl_encoding_method = 0;//0 for ascii, 1 for codebook1
        String addr = null;
        int port = -1;
        int threads = 1;
        int filter_hash_bits = 24;
        boolean filter = true;
        boolean step2_usingmask = false;
        String results_save_path = null;
        
        String DB_url = "localhost";
        int DB_port = 1433;
        String DB_name = "";
        String DB_table = "";
        String DB_user = "";
        String DB_password = "";
        String id = "";

        ArrayList<DBProperty[]> prop_array = new ArrayList<>();
        ArrayList<PatientLinkage> res = null;

        boolean[][][] data_bin;
        
        ArrayList<String> PartyA_IDs;
        ArrayList<String> PartyB_IDs;
        
        int potential_linkage_num;
        double t_p = 0, t_a = 0;

        if (args.length < 1) {
            usagemain();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) != '-') {
                usagemain();
                return;
            }
            try {
                switch (args[i].replaceFirst("-", "")) {
                    case "config":
                        file_config = args[++i];
                        break;
                    case "help":
                        usagemain();
                        break;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("please input the configure file or data file!");
            } catch (IllegalArgumentException e) {
                System.out.println(args[i] + " is illegal input");
            }
        }

        String[] tmp = null;

        try (FileReader fid_config = new FileReader(file_config); BufferedReader br_config = new BufferedReader(fid_config)) {
            String line;
            while ((line = br_config.readLine()) != null) {
                String[] strs1 = line.split("\\|");
                if (strs1.length < 1) {
                    continue;
                }

                String str = strs1[0].trim();

                if (str.equals("") || !str.contains(":")) {
                    continue;
                }

                String[] strs2 = str.split(":");

                switch (strs2[0].trim()) {
                    case "party":
                        party = strs2[1].trim();
                        break;
                    case "address":
                        addr = strs2[1].trim();
                        break;
                    case "port":
                        port = Integer.parseInt(strs2[1].trim());
                        break;
                    case "threads":
                        threads = Integer.parseInt(strs2[1].trim());
                        break;
                    case "hash bits":
                        filter_hash_bits = Integer.parseInt(strs2[1].trim());
                        break;
                    case "results save path":
                        results_save_path = strs2[1].trim();
                        break;
                    case "rule":
                        tmp = strs2[1].trim().split("\\+");
                        DBProperty[] tmp1 = new DBProperty[tmp.length];
                        for(int n = 0; n < tmp1.length; n++){
                            String tmp0 = tmp[n];
                            String tmp2 = tmp0.substring(0, tmp0.indexOf("(")).trim();
                            String tmp3 = tmp0.substring(tmp0.indexOf("(") + 1, tmp0.indexOf(")")).trim().toLowerCase();
                            if(tmp3.charAt(0) != 's'){
                                tmp1[n] = new DBProperty(tmp2, new Integer(tmp3));
                            }else{
                                int tmp4 = new Integer(tmp3.substring(1));
                                tmp1[n] = new DBProperty(tmp2, Integer.MAX_VALUE - tmp4);
                            }
                        }
                        
                        prop_array.add(tmp1);
                        break;
                    /*case "patient linkage encoding":
                        switch(strs2[1].trim().toLowerCase()){
                            case "ascii":
                                ptl_encoding_method = 0;
                                break;
                            case "codebook1":
                                ptl_encoding_method = 1;
                                break;
                            default:
                                System.out.println("Does not support " + strs2[1].trim() + " for patient linkage algorithm coding.");
                                throw new AssertionError();
                        }
                        break;*/
                        
                    case "id": 
                        if(!strs2[1].trim().equals("null")){
                           id = strs2[1].trim();
                        }else{
                            id = "";
                        }
                        break;
                    case "DB_url":
                        DB_url = strs2[1].trim();
                        break;
                    case "DB_port":
                        DB_port = Integer.parseInt(strs2[1].trim());
                        break;
                    case "DB_name":
                    case "DSN":
                        DB_name = strs2[1].trim();
                        break;
                    case "DB_table":
                        DB_table = strs2[1].trim();
                        break;
                    case "DB_user":
                        DB_user = strs2[1].trim();
                        break;
                    case "DB_password":
                        DB_password = strs2[1].trim();
                        break;
                    default:
                        System.out.println("no property \"" + strs2[0].trim() + "\", please check the configure file!");
                        throw new AssertionError();
                }

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PatientLinkageGadget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PatientLinkageGadget.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        Helper help1 = Util.readAndEncodeByHash(new DBConfig(DB_url, DB_port, DB_name, DB_table, DB_user, DB_password, id, prop_array), filter_hash_bits);;
        
        data_bin = help1.data_bin;

        CompPool.MaxNumberTask = threads;
        long t0, t1;
        
        switch (party) {
            case "generator":
                PartyA_IDs = help1.IDs;
                System.out.println("start filtering linkage ...");
                t0 = System.currentTimeMillis();
                Gen<GCSignal> gen = new Gen<>(port, Mode.REAL, threads, data_bin, step2_usingmask, PartyA_IDs);
                gen.implement();
                t1 = System.currentTimeMillis() - t0;
                t_p = t1 / 1e3;
                System.out.println("The running time of filtering is " + t_p + " seconds.");
                potential_linkage_num = gen.getNumOfMatched();
                System.out.println("Potential linkage number: " + potential_linkage_num);

                res = gen.getRes();
                PartyB_IDs = gen.getPartyB_IDs();
                t_a = t_p;
                break;
            case "evaluator":
                PartyB_IDs = help1.IDs;
                    System.out.println("start patientlinkage algorithm ...");
                    t0 = System.currentTimeMillis();
                    Env<GCSignal> eva = new Env<>(addr, port, Mode.REAL, threads, data_bin, step2_usingmask, PartyB_IDs);
                    eva.implement();
                    t1 = System.currentTimeMillis() - t0;
                    t_p = t1 / 1e3;
                    System.out.println("The running time of patientlinkage algorithm is " + t_p + " seconds.");
                    potential_linkage_num = eva.getNumOfMatched();
                    System.out.println("Potential linkage number: " + potential_linkage_num);
                    
                    res = eva.getRes();
                    PartyA_IDs = eva.getPartyA_IDs();
                    t_a = t_p;
                break;
            default:
                throw new AssertionError();
        }
        
        String str="";
        
        if (res != null) {
            str += "----------------------------------\n";
            for(int m = 0; m < help1.rules.length; m++){
                str += String.format("Rule %d is %s.\n", m + 1, help1.rules[m]);
            }
            
            str += "----------------------------------\n";
            if (filter) {
                str += "linkage \n";
            } else {
                str += "linkage " + "\t\t\tscore\n";
            }
            str += "ID A(index)  ID B(index)\n\n";
            for (int n = 0; n < res.size(); n++) {
                int[] link0 = res.get(n).getLinkage();
                if (filter) {
                    str += String.format("%s(%d) <--> %s(%d)\n", PartyA_IDs.get(link0[0]), link0[0], PartyB_IDs.get(link0[1]), link0[1]);
                } else {
                    str += String.format("%s(%d) <--> %s(%d) \t\t%d\n", PartyA_IDs.get(link0[0]), link0[0], PartyB_IDs.get(link0[1]), link0[1], (int) res.get(n).getScore());
                }
            }
            str += String.format("The number of final matches records: %d.\n", res.size());
            str += "-----------------------------------\n";
        }
        System.out.println(str);
        
        if(results_save_path != null){
            try (FileWriter writer = new FileWriter(results_save_path)) {
                writer.write(str);
                writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("The running time of patient linkage circuit is " + t_a + " seconds!");
    }

    public static void usagemain() {
        String help_str
                = ""
                + String.format("     -config     <path>      : input configure file path\n")
                + String.format("     -help                   : show help");
        System.out.println(help_str);
    }
    
//    public static void simulation(){
//        String[] args0 = {"-config", "./configs/config_gen_1K.txt"};
//        String[] args1 = {"-config", "./configs/config_eva_1K.txt"};
//
//        Thread t_gen = new Thread(() -> {
//            startLinkage(args0);
//        });
//        Thread t_eva = new Thread(() -> {
//            startLinkage(args1);
//        });
//
//
//
//        long t0 = System.currentTimeMillis();
//        try {
//            t_gen.start();
//            t_eva.start();
//            t_gen.join();
//            t_eva.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        long t1 = System.currentTimeMillis() - t0;
//
//        System.out.println("The total running time is " + t1 / 1e3 + " seconds!");
//    }

    public static void main(String[] args){      
        long t0 = System.currentTimeMillis();
        startLinkage(args);
//        if ("sim".equals(args[0])) {
//            simulation();
//        } else {
//            startLinkage(args);
//        }
        long t1 = System.currentTimeMillis() - t0;
        System.out.println("The total running time is " + t1 / 1e3 + " seconds!");
    }
}
