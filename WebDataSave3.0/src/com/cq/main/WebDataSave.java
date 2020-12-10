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
	// ������
	public static void main(String[] args) {
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);// newһ����ʱ��
		ScheduledThreadPoolExecutor scheduled1 = new ScheduledThreadPoolExecutor(1);// newһ����ʱ��

		// ��ʱ��ȡ����
		scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				getAndWriteRMBPrice();// ִ�л�ȡ���ʷ���
				System.out.println("�״λ�ȡJPY:" + JPY_CNY);
				System.out.println("�״λ�ȡUSD:" + USD_CNY);
			}
		}, 0, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);// 0��ʾ�״�ִ��������ӳ�ʱ�䣬�ڶ�����ʾÿ��ִ������ļ��ʱ�䣬TimeUnit.MILLISECONDSִ�е�ʱ������ֵ��λ

		// ��ʱ��ȡ����
		scheduled1.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				getAndWriteData(USD_CNY, JPY_CNY);// ִ�л�ȡ��ҳ���ݲ�д��Ӳ�̷���
				System.out.println("��������:" + USD_CNY);
			}
		}, 5 * 1000, 300 * 1000, TimeUnit.MILLISECONDS);// 5*1000��ʾ�״�ִ��������ӳ�ʱ�䣬�ڶ�����ʾÿ��ִ������ļ��ʱ�䣬TimeUnit.MILLISECONDSִ�е�ʱ������ֵ��λ
	}

	// ѭ����ȡ����
	public static void getAndWriteRMBPrice() {
		// USD_CNY��JPY_CNY
		try {
			
			JSONObject priceDataJson = new JSONObject(
					sendGet("https://api.exchangeratesapi.io/latest?symbols=USD,JPY&base=CNY")).getJSONObject("rates");
			USD_CNY = 1/priceDataJson.getDouble("USD");// �ҵ�priceDataJson��USD�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
			JPY_CNY = 1/priceDataJson.getDouble("JPY");// �ҵ�priceDataJson��JPY�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
			message_CNY = "";
			System.out.println("����Դ111");
			
		} catch (JSONException e1) {
			try{
				
				JSONObject priceDataJson = new JSONObject(
						sendGet("https://ratesapi.io/api/latest?base=CNY&symbols=USD,JPY")).getJSONObject("rates");
				USD_CNY = 1/priceDataJson.getDouble("USD");// �ҵ�priceDataJson��USD�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
				JPY_CNY = 1/priceDataJson.getDouble("JPY");// �ҵ�priceDataJson��JPY�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
				
				message_CNY = "";
				System.out.println("����Դ222");
			}
			catch(JSONException e2) {
				
				try {
					JSONObject priceDataJson = new JSONObject(
							sendGet("http://www.apilayer.net/api/live?access_key=13f73b7906ed5687bcc3c5117caa1662&source=USD&format=1")).getJSONObject("quotes");
					USD_CNY = priceDataJson.getDouble("USDCNY");// �ҵ�priceDataJson��USDCNY�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
					JPY_CNY = USD_CNY/priceDataJson.getDouble("USDJPY");// �ҵ�priceDataJson��JPYCNY�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
					
					message_CNY = "";
					System.out.println("����Դ333");
					
				}catch(JSONException e3) {
					try {
						
						JSONObject priceDataJson = new JSONObject(
								sendGet("http://data.fixer.io/api/latest?access_key=30cde2f22ad259f4ccc7c2f420bedaa2&format=1")).getJSONObject("rates");
						Double EUR_CNY = priceDataJson.getDouble("CNY");
						USD_CNY = EUR_CNY/priceDataJson.getDouble("USD");// �ҵ�priceDataJson��USD�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
						JPY_CNY = EUR_CNY/priceDataJson.getDouble("JPY");// �ҵ�priceDataJson��JPY�ֶ�ֵ,�õ���Ԫ������Ҽ۸�
						
						message_CNY = "";
						System.out.println("����Դ444");
						
					}catch(JSONException e4) {
						System.out.println("��ȡ����ʧ��,���Ժ�����");
						if (USD_CNY == 0.0 | JPY_CNY == 0.0) {
							message_CNY = "��ȡ��������ʧ��,���Ժ�����";
						} else {
							message_CNY = " ";
						}
						// e.printStackTrace();}
					}
					
					
				}
				
				
			}
			
			
		}

	}

	// ѭ����ȡ��վ����,�������ݴ����JSON��ʽ
	public static void getAndWriteData(Double USD_CNY, Double JPY_CNY) {
		// BTC�۸�
		long BTC_price_CNY = 0;
		String message_BTC = "";
		try {
			// BTC��Ԫ�۸�
			Double BTC_price = Double.parseDouble(BTCPrice.getPrice());
			System.err.println("BTC��Ԫ�۸�:"+BTC_price);
			// BTC����Ҽ۸�
			BTC_price_CNY = (long) (BTC_price * USD_CNY);// BTC��Ԫ�۸�ת��������Ҽ۸�
		} catch (Exception e) {
			System.out.println("��ȡBTC���ݳ����쳣��" + e);
			message_BTC = "��ȡBTC�������糬ʱ,���Ժ�����";
			e.printStackTrace();
		}

		// HitBtc�۸�
		String Hit_price = "";
		String Hit_price_BTC_CNY = "";
		try {
			Double price_bit = new JSONObject(sendGet("https://api.hitbtc.com/api/2/public/ticker/XEMBTC")).getDouble("bid");// ��ȡweb���ص�json���� ,��ȡBid�ֶ�ֵ,�õ��۸�
			System.out.println("price_bit:====="+price_bit);
			Hit_price = new DecimalFormat("#0.00000000").format(price_bit);// BTC�۸�,����С�����8λ
			Hit_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_bit * BTC_price_CNY);// ת����BTC����Ҽ۸�,����С�����4λ
		} catch (Exception e) {
			System.out.println("��ȡBIT���ݳ����쳣��" + e);
			Hit_price = "��ȡ�������糬ʱ,���Ժ�����";
			e.printStackTrace();
		}

		
		// Binance�۸�
				String Binance_price = "";
				String Binance_price_BTC_CNY = "";
				try {
					Double price_bin = new JSONArray(sendGet("https://api.binance.com/api/v1/trades?symbol=XEMBTC&limit=1"))
							.getJSONObject(0).getDouble("price");// ��ȡweb���ص�json����
																	// ,��ȡprice�ֶ�ֵ,�õ��۸�
					Binance_price = new DecimalFormat("#0.00000000").format(price_bin);// ��Ԫ�۸�,����С�����8λ
					Binance_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_bin * BTC_price_CNY);// ת����BTC����Ҽ۸�,����С�����4λ
				} catch (Exception e) {
					System.out.println("��ȡBinance���ݳ����쳣��" + e);
					Binance_price = "��ȡ�������糬ʱ,���Ժ�����";
					e.printStackTrace();
				}
		

		// Poloniex�۸�
		String Poloniex_price = "";
		String Poloniex_price_BTC_CNY = "";
		try {
			Double price_pol = new JSONArray(
					sendGet("https://poloniex.com/public?command=returnTradeHistory&currencyPair=BTC_XEM&depth=1"))
							.getJSONObject(0).getDouble("rate");
			Poloniex_price = new DecimalFormat("#0.00000000").format(price_pol);// ��Ԫ�۸�,����С�����8λ
			Poloniex_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_pol * BTC_price_CNY);// ת����BTC����Ҽ۸�,����С�����4λ
		} catch (Exception e) {
			System.out.println("��ȡPoloniex���ݳ����쳣��" + e);
			Poloniex_price = "��ȡ�������糬ʱ,���Ժ�����";
			e.printStackTrace();
		}

		// Zaif�۸�
		String Zaif_price = "";
		String Zaif_price_BTC_CNY = "";
		try {
			Double price_zai = new JSONObject(sendGet("https://api.zaif.jp/api/1/last_price/xem_jpy"))
					.getDouble("last_price");// �ҵ�priceDataJson��USD_CNY����,��ȡval�ֶ�ֵ
			Zaif_price = new DecimalFormat("#0.0000").format(price_zai);// ��Ԫ�۸�,����С�����4λ
			Zaif_price_BTC_CNY = new DecimalFormat("#0.0000").format(price_zai * JPY_CNY);//// ת����BTC����Ҽ۸�,����С�����4λ
		} catch (Exception e) {
			System.out.println("��ȡZaif���ݳ����쳣��" + e);
			Zaif_price = "��ȡ�������糬ʱ,���Ժ�����";
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

	// web API ���json���ݷ���,���ظ�ʽΪString
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(5000);
			// conn.setRequestMethod("GET");

			// ����ʵ�ʵ�����
			conn.connect();
			// ���� BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("��ȡ��վ���ݳ����쳣��" + e);
			// e.printStackTrace();
		}
		// ʹ��finally�����ر�������
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
