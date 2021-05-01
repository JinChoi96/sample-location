package com.sample.location;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccidentOccurActivity extends AppCompatActivity {
    private static final String TAG = "AccidentOccurActivity";

    private String APIKeys = "LydaYiDETwYmI2UMH5Ncugmv9Hd4LEjUx39foqGvQ%2F3wiW4b%2FjlCnJW%2B43o%2BpZ8aYciwE5rDRoOGqRhQd1bJ4g%3D%3D";
    private String requestParameter = "&searchYearCd=2017&siDo=SIDO&guGun=GUGUN&numOfRows=10&pageNo=1";
    private String requests[] = {"http://apis.data.go.kr/B552061/frequentzoneOldman/getRestFrequentzoneOldman?ServiceKey=",
                                "http://apis.data.go.kr/B552061/schoolzoneChild/getRestSchoolzoneChild?ServiceKey=",
                                "http://apis.data.go.kr/B552061/frequentzoneChild/getRestFrequentzoneChild?ServiceKey="};

    int requestCodes[] = new int[2];
    String responses[] = new String[3];
    String jsonResponse[] = new String[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        checkAccidentOccurrence(latitude, longitude);
    }

    private void checkAccidentOccurrence(double latitude, double longitude) {
        // TODO: 현재 (위도, 경도)로부터 시도코드, 시군구코드 뽑기
        requestCodes = getRequestParameter(latitude, longitude);
        requestParameter.replace("SIDO", Integer.toString(requestCodes[0])); // 시도코드
        requestParameter.replace("GUGUN", Integer.toString(requestCodes[1])); // 시구군코드

        // 사용할 API 개수만큼 request 보내고 json 결과 받기
        for(int i = 0; i < 3; i++) {
            requests[i] += APIKeys;
            requests[i] += requestParameter;
        }
        for(int i = 0; i < 3 ; i++) {
            jsonResponse[i] = getAPIResponse((requests[i]));
        }

        //TESTING
//        String testURL = "http://apis.data.go.kr/B552061/frequentzoneOldman/getRestFrequentzoneOldman?ServiceKey=LydaYiDETwYmI2UMH5Ncugmv9Hd4LEjUx39foqGvQ%2F3wiW4b%2FjlCnJW%2B43o%2BpZ8aYciwE5rDRoOGqRhQd1bJ4g%3D%3D&searchYearCd=2017&siDo=11&guGun=680&numOfRows=10&pageNo=1";
//        String test = getAPIResponse(testURL);
//        Log.d(TAG, "API response : "+test);

        // TODO : json에서 폴리곤 결과 파싱
        Map<double[][], String> polygons = new HashMap<>();
        for(int i = 0; i < 3; i++) {
            polygons.putAll(parseJson(jsonResponse[i]));
        }

        // TODO : 폴리곤 안에 현재 (위도, 경도)가 포함되는지 판단
        String alert = null;
        for(Map.Entry<double[][], String> elem : polygons.entrySet()) {
            if(hasAccidentOccured(latitude, longitude, elem.getKey())) {
                alert = elem.getValue();
                break;
            }
        }

        // 사고다발지역인 경우 해당 alert string 을 결과값으로 반환
        String alert_message = "보행자 사고다발 지역입니다. 주변 움직이는 차량을 주의하세요.";
        // TODO? make intent and putExtra?
    }

    // TODO
    private int[] getRequestParameter(double latitude, double longitude) {
        // 현재 (위도, 경도)로부터 시도코드, 시군구코드 뽑기

        int param[] = new int[2];

        // fill param[] using Geolocation API

        return param;
    }

    private String getAPIResponse(String request) {
        String jsonString = new ThreadTask<String, String>() {
            @Override
            protected String doInBackground(String arg) {
                Log.d("doInBackground", "arg: " + arg);
                String result = null;
                try{
                    URL url = new URL(arg);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    InputStream is = conn.getInputStream();

                    StringBuilder builder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
//                        Log.d("doInBackground", "readline(): " + line);
                        builder.append(line);
                    }
                    result = builder.toString();
                    Log.d("doInBackground", "result: " + result);
                } catch (Exception e) {
                    Log.e("REST_API", "GET method failed: " + e.getMessage());
                    e.printStackTrace();
                }
                return result;
            }
        }.execute(request);

        return jsonString;
    }

    // TODO
    private HashMap<double[][], String> parseJson(String jsonResponse) {
        HashMap<double[][], String> polygons = new HashMap<>();
        // parse json and return a list of (polygon coordinates, kind of accident)
        return polygons;
    }

    // TODO
    private boolean hasAccidentOccured(double latitude, double longitude, double[][] coords) {
        boolean ret = false;

        return ret;
    }
}