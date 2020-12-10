package com.cq.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebDataSave{
	static Double USD_CNY = 0.0;
	static Double JPY_CNY = 0.0;
	static String message_CNY = "";
	public static JSONObject DataJson = new JSONObject();
	// 主程序
	public static void main(String[] args) {
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);// new一个定时器
		ScheduledThreadPoolExecutor scheduled1 = new ScheduledThreadPoolExecutor(1);// new一个定时器

		// 定时获取汇率
		scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				getAndWriteRMBPrice();// 执行获取汇率方法
				System.out.println("首次获取JPY:" + JPY_CNY);
				System.out.println("首次获取USD:" + USD_CNY);
			}
		}, 0, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);// 0表示首次执行任务的延迟时间，第二个表示每次执行任务的间隔时间，TimeUnit.MILLISECONDS执行的时间间隔数值单位

		// 定时获取数据
		scheduled1.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				getAndWriteData(USD_CNY, JPY_CNY);// 执行获取网页数据并写入硬盘方法
				System.out.println("更新数据:" + USD_CNY);
			}
		}, 5 * 1000, 300 * 1000, TimeUnit.MILLISECONDS);// 5*1000表示首次执行任务的延迟时间，第二个表示每次执行任务的间隔时间，TimeUnit.MILLISECONDS执行的时间间隔数值单位
	}

	// 循环获取汇率
	public static void getAndWriteRMBPrice() {
		// USD_CNY和JPY_CNY
		try {
			
			JSONObject priceDataJson = new JSONObject(
					sendGet("https://api.exchangeratesapi.io/latest?symbols=USD,JPY&base=CNY")).getJSONObject("rates");
			USD_CNY = 1/priceDataJson.getDouble("USD");// 找到priceDataJson的USD字段值,得到美元对人民币价格
			JPY_CNY = 1/priceDataJson.getDouble("JPY");// 找到priceDataJson的JPY字段值,得到日元对人民币价格
			message_CNY = "";
			System.out.println("数据源111");
			
		} catch (JSONException e1) {
			try{
				
				JSONObject priceDataJson = new JSONObject(
						sendGet("https://ratesapi.io/api/latest?base=CNY&symbols=USD,JPY")).getJSONObject("rates");
				USD_CNY = 1/priceDataJson.getDouble("USD");// 找到priceDataJson的USD字段值,得到美元对人民币价格
				JPY_CNY = 1/priceDataJson.getDouble("JPY");// 找到priceDataJson的JPY字段值,得到日元对人民币价格
				
				message_CNY = "";
				System.out.println("数据源222");
			}
			catch(JSONException e2) {
				
				try {
					JSONObject priceDataJson = new JSONObject(
							sendGet("http://www.apilayer.net/api/live?access_key=13f73b7906ed5687bcc3c5117caa1662&source=USD&format=1")).getJSONObject("quotes");
					USD_CNY = priceDataJson.getDouble("USDCNY");// 找到priceDataJson的USDCNY字段值,得到美元对人民币价格
					JPY_CNY = USD_CNY/priceDataJson.getDouble("USDJPY");// 找到priceDataJson的JPYCNY字段值,得到日元对人民币价格
					
					message_CNY = "";
					System.out.println("数据源333");
					
				}catch(JSONException e3) {
					try {
						
						JSONObject priceDataJson = new JSONObject(
								sendGet("http://data.fixer.io/api/latest?access_key=30cde2f22ad259f4ccc7c2f420bedaa2&format=1")).getJSONObject("rates");
						Double EUR_CNY = priceDataJson.getDouble("CNY");
						USD_CNY = EUR_CNY/priceDataJson.getDouble("USD");// 找到priceDataJson的USD字段值,得到美元对人民币价格
						JPY_CNY = EUR_CNY/priceDataJson.getDouble("JPY");// 找到priceDataJson的JPY字段值,得到日元对人民币价格
						
						message_CNY = "";
						System.out.println("数据源444");
						
					}catch(JSONException e4) {
						System.out.println("获取汇率失败,请稍后重试");
						if (USD_CNY == 0.0 | JPY_CNY == 0.0) {
							message_CNY = "获取汇率数据失败,请稍后重试";
						} else {
							message_CNY = " ";
						}
						// e.printStackTrace();}
					}
					
					
				}
				
				
			}
			
			
		}

	}

	// 循环获取网站数据,并将数据打包成JSON格式
	public static void getAndWriteData(Double USD_CNY, Double JPY_CNY) {
		// BTC价格
		long BTC_price_CNY = 0;
		String message_BTC = "";
		try {
			// BTC美元价格
			Double BTC_price = Double.parseDouble(BTCPrice.getPrice());
			System.err.println("BTC美元价格:"+BTC_price);
			// BTC人民币价格
			BTC_price_CNY = (long) (BTC_price * USD_CNY);// BTC美元价格转换成人民币价格
		} catch (Exception e) {
			System.out.println("获取BTC数据出现异常！" + e);
			message_BTC = "获取BTC数据网络超时,请稍后重试";
			e.printStackTrace();
		}

		// HitBtc价格
		String Hit_price = "";
		String Hit_price_BTC_CNY = "";
		try {
			Double price_bit = new JSONObject(sendGet("https://api.hitbtc.com/api/2/public/ticker/XEMBTC")).getDouble("bid");// 获取web返回的json数据 ,获取Bid字段值,得到价格
			System.out.println("price_bit:====="+price_bit);
			Hit_price = new DecimalFormat("#0.00000000").format(price_bit);// BTC价格,保留小数点后8位
			Hit_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_bit * BTC_price_CNY);// 转换成BTC人民币价格,保留小数点后4位
		} catch (Exception e) {
			System.out.println("获取BIT数据出现异常！" + e);
			Hit_price = "获取数据网络超时,请稍后重试";
			e.printStackTrace();
		}

		
		// Binance价格
				String Binance_price = "";
				String Binance_price_BTC_CNY = "";
				try {
					Double price_bin = new JSONArray(sendGet("https://api.binance.com/api/v1/trades?symbol=XEMBTC&limit=1"))
							.getJSONObject(0).getDouble("price");// 获取web返回的json数据
																	// ,获取price字段值,得到价格
					Binance_price = new DecimalFormat("#0.00000000").format(price_bin);// 美元价格,保留小数点后8位
					Binance_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_bin * BTC_price_CNY);// 转换成BTC人民币价格,保留小数点后4位
				} catch (Exception e) {
					System.out.println("获取Binance数据出现异常！" + e);
					Binance_price = "获取数据网络超时,请稍后重试";
					e.printStackTrace();
				}
		

		// Poloniex价格
		String Poloniex_price = "";
		String Poloniex_price_BTC_CNY = "";
		try {
			Double price_pol = new JSONArray(
					sendGet("https://poloniex.com/public?command=returnTradeHistory&currencyPair=BTC_XEM&depth=1"))
							.getJSONObject(0).getDouble("rate");
			Poloniex_price = new DecimalFormat("#0.00000000").format(price_pol);// 美元价格,保留小数点后8位
			Poloniex_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_pol * BTC_price_CNY);// 转换成BTC人民币价格,保留小数点后4位
		} catch (Exception e) {
			System.out.println("获取Poloniex数据出现异常！" + e);
			Poloniex_price = "获取数据网络超时,请稍后重试";
			e.printStackTrace();
		}

		// Zaif价格
		String Zaif_price = "";
		String Zaif_price_BTC_CNY = "";
		try {
			Double price_zai = new JSONObject(sendGet("https://api.zaif.jp/api/1/last_price/xem_jpy"))
					.getDouble("last_price");// 找到priceDataJson的USD_CNY对象,读取val字段值
			Zaif_price = new DecimalFormat("#0.0000").format(price_zai);// 日元价格,保留小数点后4位
			Zaif_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_zai * JPY_CNY);//// 转换成BTC人民币价格,保留小数点后4位
		} catch (Exception e) {
			System.out.println("获取Zaif数据出现异常！" + e);
			Zaif_price = "获取数据网络超时,请稍后重试";
			e.printStackTrace();
		}

		
		try {
			DataJson.put("message_CNY",message_CNY);
			DataJson.put("BTC_price_CNY",BTC_price_CNY);
			DataJson.put("message_BTC",message_BTC);
			DataJson.put("Hit_price",Hit_price);
			DataJson.put("Hit_price_BTC_CNY",Hit_price_BTC_CNY);
			DataJson.put("Binance_price",Binance_price);
			DataJson.put("Binance_price_BTC_CNY",Binance_price_BTC_CNY);
			DataJson.put("Poloniex_price",Poloniex_price);
			DataJson.put("Poloniex_price_BTC_CNY",Poloniex_price_BTC_CNY);
			DataJson.put("Zaif_price",Zaif_price);
			DataJson.put("Zaif_price_BTC_CNY",Zaif_price_BTC_CNY);
			System.out.println(DataJson);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// web API 获得json数据方法,返回格式为String
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(5000);
			// conn.setRequestMethod("GET");

			// 建立实际的连接
			conn.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("获取网站数据出现异常！" + e);
			// e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

}
