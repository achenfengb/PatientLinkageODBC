/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientlinkage.DataType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author cf
 */
public class DBConfig {
    String DB_rul;
    int DB_port;
    String DB_name;
    String DB_table;
    String DB_user;
    String DB_password;
    
    String id;
    
    String[] all_props;
    int[][] props_len;

    public DBConfig(String DB_rul, int DB_port, String DB_name, String DB_table, String DB_user, String DB_password, String id, ArrayList<DBProperty[]> rules) {
        this.DB_rul = DB_rul;
        this.DB_port = DB_port;
        this.DB_name = DB_name;
        this.DB_table = DB_table;
        this.DB_user = DB_user;
        this.DB_password = DB_password;
        
        this.id = id;
        
        this.props_len = new int[rules.size()][];
        
        ArrayList<String> list0 = new ArrayList();
        int index0 = 0;
        
        for(DBProperty[] tmp0 : rules){
            for(DBProperty tmp1 : tmp0){
                if(!list0.contains(tmp1.prop)){
                    list0.add(tmp1.prop);
                }
            }
        }
        
        all_props = new String[list0.size()];
        index0 = 0;
        for(String entry:list0){
            all_props[index0++] = entry;
        }
        
        
        for(int m = 0; m < this.props_len.length; m++){
            DBProperty[] rule_tmp = rules.get(m);
            this.props_len[m] = new int[all_props.length];
            Arrays.fill(this.props_len[m], 0);
            
            for(DBProperty db_tmp : rule_tmp){
                int k;
                String db_str_tmp = db_tmp.prop;
                for(k = 0; k < all_props.length; k++){
                    if(db_str_tmp.equals(all_props[k])){
                        break;
                    }
                }
                this.props_len[m][k] = db_tmp.len;
            }
        }
        
    }

    public String getDB_rul() {
        return DB_rul;
    }

    public int getDB_port() {
        return DB_port;
    }

    public String getDB_name() {
        return DB_name;
    }

    public String getDB_table() {
        return DB_name + "." + DB_table;
    }

    public String getDB_user() {
        return DB_user;
    }

    public String getDB_password() {
        return DB_password;
    }

    
    public String getG_url(){
        return "jdbc:sqlserver://" + DB_rul + ":" + DB_port + ";databaseName=" + DB_name + ";user=" + DB_user + ";password=" + DB_password;
    }
    
    public String getG_url1(){
        return "jdbc:sqlserver://" + DB_rul + ":" + DB_port + ";databaseName=";
    }
    
    public String getODBC(){
        return "jdbc:odbc:" + this.DB_name;
    }

    public String[] getAll_props() {
        return all_props;
    }


    public int[][] getProps_len() {
        return props_len;
    }

    public String getId() {
        return id;
    }
    
    
}
