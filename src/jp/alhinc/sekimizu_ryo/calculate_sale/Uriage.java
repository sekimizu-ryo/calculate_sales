package jp.alhinc.sekimizu_ryo.calculate_sale;

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
public class Uriage {
	public static void main(String[] args) {
		HashMap<String , Long>branchSaleMap = new HashMap<String , Long>();
		HashMap<String , Long>commdityMap = new HashMap<String , Long>();
		HashMap<String , String>branchNameMap = new HashMap<String , String>();
		HashMap<String , String>commdityNameMap = new HashMap<String , String>();


		try {

			File branchFile = new File(args[0],"branch.lst");
			FileReader bfr = new FileReader(branchFile);
			BufferedReader br = new BufferedReader(bfr);

			String s;
			while((s = br.readLine()) != null){
				String[] items = s.split(",",-1);

				//空白の処理は含まれているので、別で書く必要はない。
				if (!items[0].matches("[0-9]{3}$")|| items.length != 2) {
					System.out.println("ファイルフォーマットが不正です。処理を終了します。");
					return;
				}
				branchNameMap.put(items[0],items[1]);
				branchSaleMap.put(items[0],0l);


			}
			br.close();

		}catch(FileNotFoundException e){
			System.out.println("支店定義ファイルが存在しません。");

		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました。");

		}

		try {
			File commodityFile = new File(args[0],"commodity.lst");
			FileReader cfr = new FileReader(commodityFile);
			BufferedReader cr = new BufferedReader(cfr);

			String s2;
			while((s2 = cr.readLine()) != null){
				String[] items2 = s2.split(",",-1);

				//空白の処理は含まれているので、別で書く必要はない。
				if (!items2[0].matches("[0-9A-Z]{8}$")|| items2.length != 2) {
					System.out.println("ファイルフォーマットが不正です。処理を終了します。");
					return;
				}
				commdityNameMap.put(items2[0],items2[1]);
				commdityMap.put(items2[0],0l);

				}
			cr.close();

		}catch(FileNotFoundException e){
			System.out.println("商品定義ファイルが存在しません。");

		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました。");
		}

		try {
			//rcdファイルじたいを読み込む処理
			File saleFile = new File(args[0]);
			//指定のディレクトリにあるファイル一覧を一括で取得する
			//listFilesというメソッドを使用している。
			File[] saleList = saleFile.listFiles();
			ArrayList<File> rcdList= new ArrayList<File>();
			for(int i =0; i < saleList.length; i++){

				//matchesを使って　配列を8桁のものを抽出する。
				//ただしsalelistはFile型であるため、getName();を取得しないと使用できない。
				if(saleList[i].getName().matches("^[0-9]{8}.rcd$")){
					rcdList.add(saleList[i]);
				}
			}
			for(int i=0; i< rcdList.size()-1; i++){
				//000001.rcd=1代入の処理
				//メソッドを使用して先頭　8文字を抜き出し
				String rcdex = saleList[i].getName().substring(0, 8);
				String rcdex2 = saleList[i+1].getName().substring(0, 8);
				int rcdValue = Integer.parseInt(rcdex);
				int rcdValue2 = Integer.parseInt(rcdex2);
				int comParison;

				//比較の処理
				comParison = rcdValue2 - rcdValue;
				if(comParison !=  1){
					System.out.println("売上ファイルが連番になっていません。");
					return;
				}
			}
				//rcdデータを読み込む処理
			for(int i =0; i <rcdList.size() ; i++){
				FileReader rfl = new FileReader(saleList[i]);
				BufferedReader rl = new BufferedReader(rfl);
				String s3;
				ArrayList<String> rcdData= new ArrayList<String>();

				while((s3 = rl.readLine()) != null){
					rcdData.add(s3);

				}
				if (rcdData.size() >= 4) {
					System.out.println(rcdList.get(i)+"のファイルフォーマットが不正です。処理を終了します。");
					return ;
					}

				rl.close();

				// 支店集計
				if(branchSaleMap.containsKey(rcdData.get(0)) == false){
					System.out.println(rcdList.get(i)+"の支店コードが不正です。処理を終了します。");
					return;
				}
				branchSaleMap.get(rcdData.get(0));
				rcdData.get(2);
				long rcdDataCast = Long.parseLong(rcdData.get(2));
				long branchTotal =0;
				branchTotal = rcdDataCast + branchSaleMap.get(rcdData.get(0));
				branchSaleMap.put(rcdData.get(0),branchTotal);

				if (String.valueOf(branchTotal).length() >= 10) {
					System.out.println("合計金額が10桁超えました。処理を終了します。");
					return ;
					}

				//商品集計
				if(commdityMap.containsKey(rcdData.get(1)) == false){
					System.out.println(rcdList.get(i)+"の商品コードが不正です。処理を終了します。");
					return;
				}
				commdityMap.get(rcdData.get(1));
				rcdData.get(2);
				long rcdDataCast2 = Long.parseLong(rcdData.get(2));
				long commdityTotal =0;
				commdityTotal = rcdDataCast2 + commdityMap.get(rcdData.get(1));
				commdityMap.put(rcdData.get(1),commdityTotal);

				if (String.valueOf(commdityTotal).length() >= 10) {
					System.out.println("合計金額が10桁超えました。処理を終了します。");
					return ;
				}
				}
		}catch(FileNotFoundException e){
			System.out.println("売上ファイルが存在しません。");

		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました。");
			}
	try {
		//Listの生成
		List<Map.Entry<String,Long>> entries =
				new ArrayList<Map.Entry<String,Long>>(branchSaleMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>()
			{

			@Override
			public int compare(
					Entry<String,Long> entry1, Entry<String,Long> entry2) {
						return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
					}
			});
        	File branchoutFile = new File(args[0],"branch.out");
            FileWriter bfw = new FileWriter(branchoutFile);
            BufferedWriter bw = new BufferedWriter(bfw);

            for (Entry<String,Long> s : entries) {


            	bw.write(s.getKey());
	            bw.write(",");
	            bw.write(branchNameMap.get(s.getKey()));
	            bw.write(",");
	            bw.write(String.valueOf(s.getValue()));
	            bw.newLine();
	            }
            bw.close();
            System.out.println("支店別集計OK");

	}catch(FileNotFoundException e){
		System.out.println("売上データが存在しません。");

	}catch(IOException e){
		System.out.println("予期せぬエラーが発生しました。");
		}

	try {
		//Listの生成
		List<Map.Entry<String,Long>> entries =
				new ArrayList<Map.Entry<String,Long>>(commdityMap.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String,Long>>()
		{
			@Override
			public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});

		File commdityOutFile = new File(args[0],"commdity.out");
		FileWriter cfw = new FileWriter(commdityOutFile);
		BufferedWriter cw = new BufferedWriter(cfw);
		for (Entry<String,Long> s : entries) {


			cw.write(s.getKey());
	        cw.write(",");
            cw.write(commdityNameMap.get(s.getKey()));
            cw.write(",");
            cw.write(String.valueOf(s.getValue()));
            cw.newLine();


        	}
		cw.close();
		  System.out.println("商品別集計OK");

	}catch(FileNotFoundException e){
		System.out.println("売上データが存在しません。");

	}catch(IOException e){
		System.out.println("予期せぬエラーが発生しました。");
		}
	}
}