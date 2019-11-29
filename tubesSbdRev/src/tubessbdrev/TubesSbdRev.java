package tubessbdrev;

import com.csvreader.CsvReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TubesSbdRev {
    static String[] col = new String[100];
    static String[] tableName = new String[100];
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static String input = " ";
    static String lanjut;
    static int[] arr_BFR = new int[100];
    static int[] arr_FOR = new int[100];
    static Scanner sc = new Scanner(System.in);
    static String[] QEP = new String[100];
    static String[] SP = new String[100];
    
    static String parser(String[] data, String[] kata, String[] table){
        String dataPisah[];
        String[] arrData = new String[100];
        int inData = 0;
        boolean cek = true;
        int i = 0;
        if(kata.length >= 3){
            if(isSelect(kata[0])){
                if(isFrom(kata[2])){
                    if(kata.length==4 || isWhere(kata[4])){
                        for(int z = 0; z < table.length;z++){
                            if(kata[kata.length-1].contains(";") && kata[3].equals(table[z])){
                                if(kata[1].equals("*")){
                                    System.out.println("Tabel(1) : "+table[z]);
                                    System.out.println("List kolom :");
                                    String[] dataSplit = data[z].split(";");
                                    for(int k = 0; k < dataSplit.length-5;k++){
                                        System.out.print(dataSplit[k]+",");
                                    }
                                    System.out.println("");
                                    return "basic";
                                }else{
                                    String[] kataPisah = kata[1].split(",");
                                    while(i < kataPisah.length){
                                        if(isData(data[z],kataPisah[i])){
                                            arrData[inData] = kataPisah[i];
                                            inData++;
                                        }else{
                                            System.out.println("SQL Error (kolom "+kataPisah[i]+" tidak ada di tabel "+table[z]+")");
                                            cek = false;
                                            return "else";
                                        }
                                        i++;
                                    }
                                    if(cek){
                                        System.out.println("Tabel (1) : "+table[z]);
                                        System.out.print("List kolom : ");
                                        for(int in = 0; in < inData;in++){
                                            System.out.print(arrData[in]+",");
                                        }
                                        System.out.println("");
                                        return "basic";
                                    }
                                }
                            }else{
                                if(kata[3].equals(table[z])){
                                    System.out.println("SQL Error (Missing ;)");
                                    return "else";
                                }
                            }
                        }
                    }else if(isJoin(kata[4]) && isUsing(kata[6])){
                        for(int z = 0; z < 3; z++){
                            if(table[z].equals(kata[3])){
                                for(int x = 0; x < 3;x++){
                                    if(table[x].equals(kata[5])){
                                        String [] kataPisahKB = kata[7].split("\\("); //KB = Kurung Buka {Memisahkan kata misal : "(id_aplikasi);" menjadi "id_aplikasi);"}
                                        String [] kataPisahKT = kataPisahKB[1].split("\\)"); //KT = Kurung Tutup {Memisahkan kata misal = "id_aplikasi;" menjadi "id_aplikasi"}
                                        if(isJoinUsing(kataPisahKT[0],data[x]) && isJoinUsing(kataPisahKT[0],data[z])){
                                            if(kata[1].equals("*")){
                                                System.out.println("Tabel(1) : "+table[z]);
                                                System.out.println("List kolom :");
                                                String[] dataSplit = data[z].split(";");
                                                for(int k = 0; k < dataSplit.length;k++){
                                                    System.out.print(dataSplit[k]+",");
                                                }
                                                System.out.println("");
                                                System.out.println("Tabel(2) : "+table[x]);
                                                System.out.println("List kolom :");
                                                String[] dataSplitX = data[x].split(";");
                                                for(int k = 0; k < dataSplitX.length;k++){
                                                    System.out.print(dataSplitX[k]+",");
                                                }
                                                System.out.println("");
                                                return "join";
                                            }else{
                                               if(isTKJU(kata[kata.length-1],data[x]) && isTKJU(kata[kata.length-1],data[z])){
                                                   String[] kataPisah = kata[1].split(",");
                                                    if(isJoin(table,kataPisah,kata,data)){
                                                        
                                                        return "join";
                                                    }
                                               }else{
                                                   System.out.println("SQL Error (Missing ;)");
                                                   return "join";
                                               }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return "error";
    }
    
    static void QEPBNLJ(String[] kata, String[] table, String kataPisahKT){
        String[] data = new String[5];
        int br = 0,bs = 0,n;
        int[] block = new int[5];
        QEP[0] = "PROJECTION "+kata[1]+" -- on the fly\n"+
                 "           JOIN "+kata[3]+"."+kataPisahKT+" = "+kata[5]+"."+kataPisahKT+" -- Block Nested Loop Join\n"+
                 kata[3]+"      "+kata[5]+"\n";
        for (int i = 0; i < table.length; i++){
            if(kata[3].equals(table[i])){
                data = col[i].split(";");
                n = Integer.parseInt(data[7]);
                br = (n / arr_BFR[i]) + 1;
                break;
            }
        }
        for (int i = 0; i < table.length; i++){
            if(kata[5].equals(table[i])){
                data = col[i].split(";");
                n = Integer.parseInt(data[7]);
                bs = (n / arr_BFR[i]) + 1;
                break;
            }
        }
        block[0] = (br * bs) + br;
        QEP[0] += "Cost (Worst case): "+block[0];
        QEP[1] = "PROJECTION "+kata[1]+" -- on the fly\n"+
                 "           JOIN "+kata[3]+"."+kataPisahKT+" = "+kata[5]+"."+kataPisahKT+" -- Block Nested Loop Join\n"+
                 kata[5]+"      "+kata[3]+"\n";
        block[1] = (bs * br) + bs;
        QEP[1] += "Cost (Worst case): "+block[1];
        System.out.println("");
        System.out.println("QEP #1");
        System.out.println(QEP[0]);
        System.out.println("");
        System.out.println("QEP#2");
        System.out.println(QEP[1]);
        System.out.println("");
        if(block[0]<block[1]){
            System.out.println("QEP optimal : #QEP1");
            addDataToCSV("shared_pool.csv",input,QEP[0]);
        }else{
            System.out.println("QEP optimal : #QEP2");
            addDataToCSV("shared_pool.csv",input,QEP[1]);
        }
    }
    
    static int QEPA1(String[] kata,String[] table){
        String data[];
        int n,br,block;
        boolean bukankey = true;
        System.out.println("");
        for (int i = 0; i < kata.length; i++){
            if(kata[3].equals(table[i])){
                data = col[i].split(";");
                if(kata[5].equals(data[0])){
                    if(kata[1].equals("*")){
                        String temp = "";
                        for(int k = 0; k < data.length-5;k++){
                            temp += data[k]+",";
                        }
                        QEP[0] = "PROJECTION "+temp+" -- on the fly\n"+
                                 "SELECTION "+kata[5]+" = "+kata[7].substring(0,kata[7].length()-1)+" -- A1\n"+
                                 kata[3]+"\n";
                    }else{
                        QEP[0] = "PROJECTION "+kata[1]+" -- on the fly\n"+
                                 "SELECTION "+kata[5]+" = "+kata[7].substring(0,kata[7].length()-1)+" -- A1\n"+
                                 kata[3]+"\n";
                    }
                    n = Integer.parseInt(data[7]);
                    br = (n / arr_BFR[i]) + 1;
                    block = br / 2;
                    QEP[0] += "Cost : "+block+" block";
                    bukankey = false;
                    System.out.println("QEP #1");
                    System.out.println(QEP[0]);
                    return block;
                }
                if(bukankey){
                    if(kata[1].equals("*")){
                        String temp = "";
                        for(int k = 0; k < data.length-5;k++){
                            temp += data[k]+",";
                        }
                        QEP[0] = "PROJECTION "+temp.substring(0,temp.length()-1)+" -- on the fly\n"+
                                 "SELECTION "+kata[5]+" = "+kata[7].substring(0,kata[7].length()-1)+" -- A1\n"+
                                 kata[3]+"\n";
                    }else{
                        QEP[0] = "PROJECTION "+kata[1]+" -- on the fly\n"+
                                 "SELECTION "+kata[5]+" = "+kata[7].substring(0,kata[7].length()-1)+" -- A1\n"+
                                 kata[3]+"\n";
                    }
                    n = Integer.parseInt(data[7]);
                    br = (n / arr_BFR[i]) + 1;
                    block = br;
                    QEP[0] += "Cost : "+block+" block";
                    System.out.println("QEP #1");
                    System.out.println(QEP[0]);
                    return block;
                }
            }
        }
        return -1;
    }
    
    static double log( double a, double b ){
        return Math.log(a) / Math.log(b);
    }
    
    static int QEPA2(String[] kata,String[] table){
        String[] data = new String[100];
        System.out.println("");
        for (int i = 0; i < kata.length; i++) {
            if(kata[3].equals(table[i])){
                data = col[i].split(";");
                if(kata[5].equals(data[0])){
                    if(kata[1].equals("*")){
                        String temp = "";
                        for(int k = 0; k < data.length-5;k++){
                            temp += data[k]+",";
                        }
                        QEP[1] = "PROJECTION "+temp+" -- on the fly\n"+
                                 "SELECTION "+kata[5]+" = "+kata[7].substring(0,kata[7].length()-1)+" -- A2\n"+
                                 kata[3]+"\n";
                    }else{
                        QEP[1] = "PROJECTION "+kata[1]+" -- on the fly\n"+
                                 "SELECTION "+kata[5]+" = "+kata[7].substring(0,kata[7].length()-1)+" -- A2\n"+
                                 kata[3]+"\n";
                    }
                    int y,B,n,br,block,V,P,hi;
                    B = Integer.parseInt(data[9]);
                    V = Integer.parseInt(data[8]);
                    P = Integer.parseInt(data[10]);
                    n = Integer.parseInt(data[7]);
                    y = B/(V+P);
                    br = (n / arr_BFR[i]) + 1;
                    hi = (int) Math.round(log(br,y));
                    block = hi + 1;
                    QEP[1] += "Cost : "+block+" block";
                    System.out.println("QEP #2");
                    System.out.println(QEP[1]);
                    return block;
                }
            }
        }
        return -1;
    }
    
    static boolean isTKJU(String kata, String data){ // Titik Koma Join Using ("using (id_aplikasi);" || "using (id_admin);")
        String[] arrData = data.split(";");
        for (int i = 0; i < arrData.length; i++) {
            if(kata.equals("("+arrData[i]+");")){
                return true;
            }
        }
        return false;
    }
        
    static boolean isJoin(String[] table, String[] kataPisah,String[] kata,String[] data){ //Cek "id_admin,id_aplikasi,rating,nama_admin" ada di tabel atau tidak
        boolean cek = true;
        int idxP = 0, idxK = 0;
        int i;
        int k = 0;
        boolean ketemu = false;
        String[] arrData = new String[100];
        String[] arrData2 = new String[100];
        int inData2 = 0;
        int inData = 0;
        while(k < 2 && !ketemu){
            if(table[k].equals(kata[3])){
                int l = 0;
                while(l < 2){
                    if((table[l].equals(kata[6])) || (table[l].equals(kata[5]))){
                        idxP = k;
                        idxK = l;
                    }
                    l++;
                }
            }
            k++;
        }
        i = 0;

        while(i < kataPisah.length){
            if(isData(data[idxP],kataPisah[i])){
                arrData[inData] = kataPisah[i];
                inData++;
                if(isData(data[idxK],kataPisah[i])){
                    arrData2[inData2] = kataPisah[i];
                    inData2++;
                }
            }else if(isData(data[idxK],kataPisah[i])){
                arrData2[inData2] = kataPisah[i];
                inData2++;
            }else{
                System.out.println("SQL Error (kolom "+kataPisah[i]+" tidak ada di tabel "+table[idxP]+" maupun tabel "+table[idxK]+")");
                cek = false;
                return true;
            }
            i++;
        }

        i=0;
        if(inData!=0 && inData2!=0){
            System.out.println("Tabel (1) "+table[idxP]);
            System.out.print("List kolom : ");
            for(int j = 0; j < inData;j++){
                    System.out.print(arrData[j]+",");
            }
            System.out.println("");
            System.out.println("Tabel (2) "+table[idxK]);
            System.out.print("List kolom : ");
            for(int j = 0; j < inData2; j++){
                System.out.print(arrData2[j]+",");
            }
            System.out.println("");
            return true;
        }
        return false;
    }
    
    static boolean isJoinUsing(String kata, String data){
        String[] arrData = data.split(";");
        for(int i = 0; i < arrData.length;i++){
            if(arrData[i].equals(kata)){
                return true;
            }
        }
        return false;
    }
    
    static boolean isData(String data, String kata){
        int i = 0;
        boolean benar = false;
        String[] dataPisah = data.split(";");
        while(!benar && i < dataPisah.length){
            if(dataPisah[i].equals(kata)){
                benar = true;
            }else{
                benar = false;
            }
            i++;
        }
        if(benar){
            return true;
        }
        return false;
    }

    static boolean isFrom(String kata){
        char[] arrKata = kata.toCharArray();
        if(arrKata[0] == 'F' || arrKata[0] == 'f'){
            if(arrKata[1] == 'R' || arrKata[1] == 'r'){
                if(arrKata[2] == 'O' || arrKata[2] == 'o'){
                    if(arrKata[3] == 'M' || arrKata[3] == 'm'){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    static boolean isSelect(String kata){
        char[] arrKata = kata.toCharArray();
        if(arrKata[0] == 'S' || arrKata[0] == 's'){
            if(arrKata[1] == 'E' || arrKata[1] == 'e'){
                if(arrKata[2] == 'L' || arrKata[2] == 'l'){
                    if(arrKata[3] == 'E' || arrKata[3] == 'e'){
                        if(arrKata[4] == 'C' || arrKata[4] == 'c'){
                            if(arrKata[5] == 'T' || arrKata[5] == 't'){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    static boolean isOn(String kata){
        char[] arrKata = kata.toCharArray();
        if(arrKata[0] == 'O' || arrKata[0] == 'o'){
            if(arrKata[1] == 'N' || arrKata[1] == 'n'){
                return true;
            }
        }
        return false;
    }
    
    static boolean isUsing(String kata){
        char[] arrKata = kata.toCharArray();
        if(arrKata[0] == 'U' || arrKata[0] == 'u'){
            if(arrKata[1] == 'S' || arrKata[1] == 's'){
                if(arrKata[2] == 'I' || arrKata[2] == 'i'){
                    if(arrKata[3] == 'N' || arrKata[3] == 'n'){
                        if(arrKata[4] == 'G' || arrKata[4] == 'g'){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    static boolean isJoin(String kata){
        char[] arrKata = kata.toCharArray();
        if(arrKata[0] == 'J' || arrKata[0] == 'j'){
            if(arrKata[1] == 'O' || arrKata[1] == 'o'){
                if(arrKata[2] == 'I' || arrKata[2] == 'i'){
                    if(arrKata[3] == 'N' || arrKata[3] == 'n'){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    static boolean isWhere(String kata){
        char[] arrKata = kata.toCharArray();
        if(arrKata[0] == 'W' || arrKata[0] == 'w'){
            if(arrKata[1] == 'H' || arrKata[1] == 'h'){
                if(arrKata[2] == 'E' || arrKata[2] == 'e'){
                    if(arrKata[3] == 'R' || arrKata[3] == 'r'){
                        if(arrKata[4] == 'E' || arrKata[4] == 'e'){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    static void noSatu(int i) throws IOException{
        int BFR, FOR,B,R,p,K;
        for(int j = 0; j <= i; j++){
            String[] dataSplit = col[j].split(";");
            B = Integer.parseInt(dataSplit[9]);
            R = Integer.parseInt(dataSplit[6]);
            p = Integer.parseInt(dataSplit[10]);
            K = Integer.parseInt(dataSplit[8]);
            BFR = B/R;
            FOR = B/(p+K);
            System.out.println("BFR "+tableName[j]+" : "+BFR);
            System.out.println("Fan Out Ration "+tableName[j]+" : "+FOR);
            arr_BFR[j] = BFR;
            arr_FOR[j] = FOR;
        }
    }
    
    static void noDua(int i ){
        int b,B,R,n,index;
        for(int j = 0; j <= i; j++){
            String[] dataSplit = col[j].split(";");
            n = Integer.parseInt(dataSplit[7]);
            b = (n / arr_BFR[j]) + 1;
            index = ((n / arr_FOR[j]) + 1)+1;
            System.out.println("Tabel Data "+tableName[j]+" : "+b+" block");
            System.out.println("Index "+tableName[j]+" : "+index+" block");
        }
    }
    
    static void noTiga(int i) throws IOException{
        int n,index,noIndex;
        String tabel;
        System.out.print("Cari rekord ke - : ");
        n = sc.nextInt();
        System.out.print("Nama tabel : ");
        tabel = sc.next();
        boolean ketemu = false;
        for(int j = 0; j <= i; j++){
            if(tabel.equals(tableName[j])){
                index = ((n / arr_FOR[j])+1)+1;
                System.out.println("Mengunnakan index, jumlah block yang diakses : "+index);
                noIndex = (n/arr_BFR[j])+1;
                System.out.println("Tanpa index, jumlah block yang diakses : "+noIndex);
                ketemu = true;
            }
        }
        if(!ketemu){
            System.out.println("Tabel yang di-input tidak ditemukan!");
        }
    }
    
     public static void addDataToCSV(String output, String query, String QEP) 
    { 
        // first create file object for file placed at location 
        // specified by filepath 
        Scanner sc = new Scanner(System.in); 
        try { 
            CSVWriter writer = new CSVWriter(new FileWriter(output, true));
            String row = query+"~"+QEP;
            String[] rowdata = row.split("~"); 
            writer.writeNext(rowdata);
  
            // closing writer connection 
            writer.close(); 
        } 
        catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 
    } 
    
    public static void main(String[] args) throws IOException {
        int i = -1, z = -1;
        String[] SPtemp = new String[100];
        String col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11,query,qep;
        CsvReader sharedPool = new CsvReader("shared_pool.csv");
        CsvReader data = new CsvReader("Data_dictionary.csv");
        try {
            data.readHeaders();
            sharedPool.readHeaders();
            while(sharedPool.readRecord()){
                z++;
                query = sharedPool.get("query");   
                qep = sharedPool.get("qep");
                SP[z] = query+"~"+qep;
            }
            sharedPool.close();
            while (data.readRecord()){  
                i++;
                tableName[i] = data.get("table_name");
                col1 = data.get("col1");
                col2 = data.get("col2");
                col3 = data.get("col3");
                col4 = data.get("col4");
                col5 = data.get("col5");
                col6 = data.get("col6");
                col7 = data.get("R");
                col8 = data.get("n");
                col9 = data.get("k");
                col10 = data.get("B");
                col11 = data.get("p");
                col[i] = col1+";"+col2+";"+col3+";"+col4+";"+col5+";"+col6+";"+col7+";"+col8+";"+col9+";"+col10+";"+col11+";";
            }
            data.close();
        }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
		e.printStackTrace();
        }
        
        int j = 10;
        while(j != '0'){
            if(j == 0){
                System.exit(0);
            }else if(j == 1){
                for(int k = 0; k < 20; k++){
                    System.out.println("");
                }
                noSatu(i);
            }else if(j == 2){
                for(int k = 0; k < 20; k++){
                    System.out.println("");
                }
                noDua(i);
            }else if(j == 3){
                for(int k = 0; k < 20; k++){
                    System.out.println("");
                }
                noTiga(i);
            }else if(j == 4){
                for(int k = 0; k < 20; k++){
                    System.out.println("");
                }
                System.out.print("Masukan SQL : ");
                input = br.readLine();
                String[] kalimat = input.split(" ");
                boolean isSharedPool = false;
                int k =0;
                while(k<z+1){
                    SPtemp = SP[k].split(";");
                    if(input.contains(SPtemp[0]) && SPtemp[0].contains("select")){
                        System.out.println("");
                        System.out.println("SQL sudah pernah diproses...");
                        if(parser(col,kalimat,tableName) == "basic"){}
                        System.out.println("");
                        System.out.println(SPtemp[1].substring(1,SPtemp[1].length()));
                        isSharedPool = true;
                        break;
                    }
                    k++;
                }   
                if(!isSharedPool){
                    String temp = parser(col,kalimat,tableName);
                    if(temp.equals("basic")){
                        int[] arr_QEP = new int[100];
                        arr_QEP[0] = QEPA1(kalimat,tableName);
                        arr_QEP[1] = QEPA2(kalimat,tableName);
                        System.out.println("");
                        if(arr_QEP[1] == -1){
                            addDataToCSV("shared_pool.csv",input,QEP[0]);
                        }else if(arr_QEP[0]<arr_QEP[1]){
                            System.out.println("QEP optimal : #QEP1");
                            addDataToCSV("shared_pool.csv",input,QEP[0]);
                        }else if(arr_QEP[1]<arr_QEP[0]){
                            System.out.println("QEP optimal : #QEP2");
                            addDataToCSV("shared_pool.csv",input,QEP[1]);
                        }
                    }else if(temp.equals("join")){
                        for (int m = 0; m < i; m++) {
                            String[] dataPisah = col[m].split(";");
                            for (int l = 0; l < dataPisah.length; l++) {
                                if(kalimat[7].contains(dataPisah[l])){
                                    QEPBNLJ(kalimat,tableName,dataPisah[l]);
                                    break;
                                }
                            }
                        }
                    }
                    else if(temp.equals("error")){
                        System.out.println("SQL Error (Syntax Error)");
                    }
                    
                    sharedPool = new CsvReader("shared_pool.csv");
                    z = -1;
                    sharedPool.readHeaders();
                    while(sharedPool.readRecord()){
                        z++;
                        query = sharedPool.get("query");   
                        qep = sharedPool.get("qep");
                        SP[z] = query+"~"+qep;
                    }
                    sharedPool.close();
                }
                System.out.println("");
                input = br.readLine();
            }else if(j == 5){   
                String[] splitSP = new String[100];
                String[] splitIsiSharedPool = new String[100];
                for(int k = 0; k < 20; k++){
                    System.out.println("");
                }
                System.out.println("Isi shared pool : ");
                for (int k = 0; k < z+1; k++) {
                    splitSP = SP[k].split("~");
                    System.out.println("Query : "+splitSP[0]);
                    System.out.println(splitSP[1]);
                    System.out.println("");
                }
                System.out.println("");
            }
            System.out.println("1. Tampilkan BFR dan Fanout Ratio Setiap Tabel");
            System.out.println("2. Tampilkan Total Blok Data + Blok Index Setiap Tabel");
            System.out.println("3. Tampilkan Jumlah Blok yang Diakses Untuk Pencarian Rekord");
            System.out.println("4. Tampilkan QEP dan Cost");
            System.out.println("5. Tampilkan Isi File Shared Pool");
            System.out.println("0. Keluar");
            System.out.print("Masukan sesuai nomor yang anda inginkan... ");
            j = sc.nextInt();
        }
    }
}