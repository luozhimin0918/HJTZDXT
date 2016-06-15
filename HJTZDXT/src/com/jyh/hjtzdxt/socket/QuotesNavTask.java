package com.jyh.hjtzdxt.socket;
//package com.jyh.kxt.socket;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Handler;
//
//import com.jyh.bean.HqBeanData;
//import com.jyh.bean.HqChildren;
//import com.jyh.bean.TopBean;
//
//
//public class QuotesNavTask extends AsyncTask<Void, Void, List<HqBeanData>>{
//
//	private KXTApplication application;
//	private Context context;
//	private Handler handler;
//	public QuotesNavTask(Context context,Handler handler){
//		this.context=context;
//		this.handler=handler;
//		application=(KXTApplication) context.getApplicationContext();
//	}
//	@Override
//	protected List<HqBeanData> doInBackground(Void... arg0) {
//		// hqBeans = new ArrayList<HQBean>();
//		List<HqBeanData> hqBeanDatas = new ArrayList<HqBeanData>();
//		List<TopBean> topBeans = new ArrayList<TopBean>();
//
//		HttpClient client = new DefaultHttpClient();
//		HttpGet get = new HttpGet("http://appapi.kxt.com//Data/quotes_list");
//		HttpResponse response;
//		HqBeanData hqBeanData = null;
//		try {
//			response = client.execute(get);
//			if (HttpStatus.SC_OK == response.getStatusLine()
//					.getStatusCode()) {
//				HttpEntity entity = response.getEntity();
//				String data = EntityUtils.toString(entity, "GBK");
//				JSONArray array = new JSONObject(data).getJSONArray("data");
//				for (int i = 0; i < array.length(); i++) {
//					List<HqChildren> hqChildrens = new ArrayList<HqChildren>();
//					JSONObject object = (JSONObject) array.get(i);
//					hqBeanData = new HqBeanData();
//					if (i == 0) {
//						JSONArray topArray = object.getJSONArray("top");
//						for (int t = 0; t < topArray.length(); t++) {
//							JSONObject topObject = (JSONObject) topArray
//									.get(t);
//							TopBean topBean = new TopBean();
//							topBean.setCode(topObject.getString("code"));
//							topBean.setName(topObject.getString("name"));
//							topBeans.add(topBean);
//						}
//					}
//					if (null != object.getString("children")) {
//						JSONArray childrenArray = object
//								.getJSONArray("children");
//						for (int z = 0; z < childrenArray.length(); z++) {
//							JSONObject childrenObject = (JSONObject) childrenArray
//									.get(z);
//							HqChildren hqChildren = new HqChildren();
//							hqChildren.setCode(childrenObject
//									.getString("code"));
//							hqChildren.setName(childrenObject
//									.getString("name"));
//							hqChildrens.add(hqChildren);
//						}
//					}
//					hqBeanData.setCode(object.getString("code"));
//					hqBeanData.setName(object.getString("name"));
//					hqBeanData.setChildren(hqChildrens);
//					if (null != topBeans) {
//						hqBeanData.setTopBeans(topBeans);
//					}
//					hqBeanDatas.add(hqBeanData);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			if (client != null && client.getConnectionManager() != null) {
//				client.getConnectionManager().shutdown();
//			}
//		}
//		return hqBeanDatas;
//	}
//
//	@Override
//	protected void onPostExecute(List<HqBeanData> result) {
//		super.onPostExecute(result);
//		// application.setHqBeans(result);
//		application.setHqBeanDatas(result);
//		if(null!=handler){
//			handler.sendEmptyMessageDelayed(90, 2*1000);
//		}
//	}
//}
