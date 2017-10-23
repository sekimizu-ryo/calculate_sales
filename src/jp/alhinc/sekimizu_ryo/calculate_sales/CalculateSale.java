package jp.alhinc.sekimizu_ryo.calculate_sales;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//今後　合計でMapを4つ使用することになる。
//キャストは売上額のときだけ使用する

public class CalculateSale {
	public static boolean  branchFile(HashMap<String , Long>saleMap,HashMap<String , String>nameMap,String  dirPath, String fileName){
		BufferedReader br =null;
		try {

			File branchFile = new File(dirPath,fileName);
			FileReader bfr = new FileReader(branchFile);
			br= new BufferedReader(bfr);
			String s;
			while((s = br.readLine()) != null){
				String[] items = s.split(",",-1);
				//matchesで0～9の3桁の値を取得しかつ2個の配列を取得
				if (!items[0].matches("[0-9]{3}$")|| items.length != 2){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return false;
				}
				nameMap.put(items[0],items[1]);
				saleMap.put(items[0],0l);
			}
		}catch(FileNotFoundException e){
			System.out.println("支店定義ファイルが存在しません");
			return false;
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try{
				if(br  != null){
					br.close();
					return true;
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return false;
	}

	public static boolean  commodityFile(HashMap<String , Long>saleMap,HashMap<String , String>nameMap,String  dirPath, String fileName){
		BufferedReader cr = null;
		try {
			File commodityFile = new File(dirPath,fileName);
			FileReader cfr = new FileReader(commodityFile);
			cr = new BufferedReader(cfr);
			String s2;
			while((s2 = cr.readLine()) != null)
			{
				String[] items2 = s2.split(",",-1);
				////matchesで0～9、A～Zの3桁の値を取得しかつ2個の配列を取得
				if (!items2[0].matches("[0-9A-Za-z]{8}$")|| items2.length != 2) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return false;
				}
				nameMap.put(items2[0],items2[1]);
				saleMap.put(items2[0],0l);
			}
		}catch(FileNotFoundException e){
			System.out.println("商品定義ファイルが存在しません");
			return false;
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try{
				if(cr  != null){
					cr.close();
					return true;
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return false;
	}

	public static void main(String[] args) throws IOException {

		HashMap<String , Long>branchSaleMap = new HashMap<String , Long>();
		HashMap<String , Long>commodityMap = new HashMap<String , Long>();
		HashMap<String , String>branchNameMap = new HashMap<String , String>();
		HashMap<String , String>commodityNameMap = new HashMap<String , String>();
		if(args.length != 1){
		System.out.println("予期せぬエラーが発生しました");
		return;
		}

		branchFile(branchSaleMap,branchNameMap,args[0], "branch.lst");
		commodityFile(commodityMap,commodityNameMap,args[0], "commodity.lst");

		BufferedReader rl =null;
		try {
			//rcdファイルを読み込む処理
			File saleFile = new File(args[0]);
			//指定のディレクトリにあるファイル一覧を一括で取得するため、listFilesというメソッドを使用
			File[] saleList = saleFile.listFiles();
			ArrayList<File> rcdList= new ArrayList<File>();
			for(int i =0; i < saleList.length; i++){
				//matchesを使って　配列を8桁のかつ.rcdのものを抽出する。
				//ただしsalelistはFile型であるため、getName();を取得しないと使用できない。
				if(saleList[i].getName().matches("^[0-9]{8}.rcd")&&!saleList[i].isDirectory()){
					rcdList.add(saleList[i]);
				}
			}
			for(int i=0; i< rcdList.size()-1; i++){
				//000001.rcd=1代入の処理をする。substringメソッドを使用して先頭　8文字を抜き出す。
				String rcdex = rcdList.get(i).getName().substring(0, 8);
				String rcdex2 =rcdList.get(i+1).getName().substring(0, 8);
				int rcdValue = Integer.parseInt(rcdex);
				int rcdValue2 = Integer.parseInt(rcdex2);
				int comParison;
				//比較の処理
				comParison = rcdValue2 - rcdValue;
				if(comParison !=  1||!rcdList.get(i+1).getName().matches("^[0-9]{8}.rcd")){
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}
			//rcdデータを読み込む処理
			for(int i =0; i <rcdList.size() ; i++){
				FileReader rfl = new FileReader(rcdList.get(i));
				rl = new BufferedReader(rfl);
				String s3;
				ArrayList<String> rcdData= new ArrayList<String>();
				while((s3 = rl.readLine()) != null){
					rcdData.add(s3);
				}
				if (rcdData.size() <= 2|| rcdData.size() >= 4) {
					System.out.println(rcdList.get(i).getName()+"のフォーマットが不正です");
					return;
				}
				// 支店集計
				if(branchSaleMap.containsKey(rcdData.get(0)) == false){
					System.out.println(rcdList.get(i).getName()+"の支店コードが不正です");
					return;
				}
				branchSaleMap.get(rcdData.get(0));
				rcdData.get(2);
				if(!rcdData.get(2).matches("[0-9]+")){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
				long rcdDataCast = Long.parseLong(rcdData.get(2));
				long branchTotal =0;
				branchTotal = rcdDataCast + branchSaleMap.get(rcdData.get(0));
				branchSaleMap.put(rcdData.get(0),branchTotal);

				if (String.valueOf(branchTotal).length() > 10) {
					System.out.println("合計金額が10桁を超えました");
					return ;
				}
				//商品集計
				if(commodityMap.containsKey(rcdData.get(1)) == false){
					System.out.println(rcdList.get(i).getName()+"の商品コードが不正です");
					return;
				}
				commodityMap.get(rcdData.get(1));
				rcdData.get(2);
				long rcdDataCast2 = Long.parseLong(rcdData.get(2));
				long commdityTotal =0;
				commdityTotal = rcdDataCast2 + commodityMap.get(rcdData.get(1));
				commodityMap.put(rcdData.get(1),commdityTotal);
				if (String.valueOf(commdityTotal).length() > 10) {
					System.out.println("合計金額が10桁を超えました");
					return ;
				}
			}
		}catch(FileNotFoundException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}finally{
			try{
				if(rl != null){
					rl.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

	  FileOut(branchSaleMap,branchNameMap,args[0], "branch.out");
	  FileOut(commodityMap,commodityNameMap,args[0],"commodity.out");
	}

	public static boolean  FileOut(HashMap<String , Long>saleMap,HashMap<String , String>nameMap,String  dirPath, String fileName){
		BufferedWriter bw = null;
		try {
			//Listの生成
			List<Map.Entry<String,Long>> entries =
					new ArrayList<Map.Entry<String,Long>>(saleMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>()
			{
				@Override
				public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}

			});
			File outFile = new File(dirPath,fileName);
			FileWriter bfw = new FileWriter(outFile);
			bw = new BufferedWriter(bfw);
			for (Entry<String,Long> s : entries) {
				//支店の集計結果　書き込み
				bw.write(s.getKey());
				bw.write(",");
				bw.write(nameMap.get(s.getKey()));
				bw.write(",");
				bw.write(String.valueOf(s.getValue()));
				bw.newLine();
			}
		}catch(FileNotFoundException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try{
				if(bw != null){
					bw.close();
					return true;
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return false;
	}
}
