package com.knocknock.global.common.openapi.airInfo;

import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.global.common.openapi.airInfo.dto.AirInfoReqDto;
import com.knocknock.global.common.openapi.airInfo.dto.AirInfoResDto;
import com.knocknock.global.common.openapi.airInfo.dto.TmPointDto;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirInfoService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private StringBuilder urlBuilder, sb;
    private BufferedReader br;

    private JsonObject jsonObject;
    private JsonReader jsonReader;
    private StringReader stringReader;

    private final String API_KEY = "XymYPoqUHNl0U%2Fuo0Tbs6LJ5VZQfWjXVfWjMBAfEnBFI8fSenYRRca0X%2B%2FRrmACkJYcS4WlJvNyf1NA4adMJvA%3D%3D";
    private final String GET_STATION_URL = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc";

    public AirInfoResDto getAirInfoByRegion(AirInfoReqDto reqDto) throws IOException {
        // 1. URL을 만들기 위한 StringBuilder
        urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/

        // 2. OpenAPI의 요청 규격에 맞는 파라미터 생성, 발급받은 인증키.
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + API_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/ // 가장 실시간 정보만 받아오도록 1설정
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(reqDto.getStationName(), "UTF-8")); /*측정소 이름*/
        urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)*/
        urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고*/

        // 3.URL 객체 생성
        URL url = new URL(urlBuilder.toString());

        // 4. 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 5. 통신을 위한 메소드, Content-type 세팅
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-type", "application/json");

        // 6. 전달받은 데이터를 BufferedReader 객체로 저장
        if(connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300)  // OK
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        else
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream())); // ERROR

        // 7. 저장된 데이터를 한줄씩 읽어 StringBuilder 객체로 저장
        sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        // 8. 객체 해제
        br.close();
        connection.disconnect();

        stringReader = new StringReader(sb.toString());
//        jsonReader = null;
//        jsonObject = null;

        boolean success = true;
        while(success) {
            try {
                jsonReader = Json.createReader(stringReader);
                jsonObject = jsonReader.readObject();

                success = false;
            } catch (JsonParsingException e) {
                log.error("JSON 파싱 실패. 재시도 GO");

                try {
                    Thread.sleep(1000);

                    getAirInfoByRegion(reqDto);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                log.info("JSON  파싱 성공");
            }
        }

        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
        JsonArray items = jsonObject.getJsonArray("items");
        log.info("items(ARRAY) : {}", items.get(0));

        jsonObject = items.getJsonObject(0);
        log.info("jsonObejct : {}", jsonObject);

        return AirInfoResDto.jsonToDto(jsonObject);

        // ConnectionException뜨면 다시 보낼수 있게 추가하기 !!!!!!!!!!!


        // 9. 전달받은 데이터 확인!

    }


    public TmPointDto getTmPoint(String address) throws IOException {
        // 1. URL 문자열 생성
        urlBuilder = new StringBuilder(GET_STATION_URL + "/getTMStdrCrdnt"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + API_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("umdName","UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")); /*읍면동명*/

        // 2. URL 객체 생성
        URL url = new URL(urlBuilder.toString());

        // 3. 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // 4. 통신을 위한 설정
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

//        System.out.println("Response code: " + conn.getResponseCode());

        // 5. 전달받은 데이터를 BufferedReader 객체로 저장
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        else
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));


        // 6. 저장된 데이터를 한줄씩 읽어 StringBuilder 객체로 저장
        sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        // 7. 객체 해제
        br.close();
        conn.disconnect();

        // 8. 전달받은 데이터 확인
//        System.out.println(sb.toString());
        // JSON 파싱
        // try - catch로 변경

        stringReader = new StringReader(sb.toString());
//        jsonObject = null;

        boolean success = true;
        while(success) {
            try {
                jsonReader = Json.createReader(stringReader);
                jsonObject = jsonReader.readObject();

//                response = (JSONObject) jsonObject.get("response");
//                body = (JSONObject) response.get("body");
//                items = (JSONArray) body.get("items");
//                item = (JSONObject) items.get(0);

                success = false;
            } catch (JsonParsingException e) {
                log.error("[JSON 파싱] 파싱 에러로 파싱 불가. 재시도!!");

                try {
                    Thread.sleep(1000);

                    getTmPoint(address);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                log.info("[JSON 파싱] 새로 시도 성공!");
            }
        }

        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
        JsonArray items = jsonObject.getJsonArray("items");

        log.info("items : {}", items);
        jsonObject = items.getJsonObject(0);

        return TmPointDto.jsonToDto(jsonObject);
    }

    // 유저의 address를 가져와서
    // 가장 가까운 대기측정소로 스왑해주는 메서드
    // 외부 api 이용할거임
    public String getStationName(TmPointDto point) throws IOException {
        urlBuilder = new StringBuilder(GET_STATION_URL + "/getNearbyMsrstnList"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + API_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(point.getTmX(), "UTF-8")); /*TM측정방식 X좌표*/
        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(point.getTmY(), "UTF-8")); /*TM측정방식 Y좌표*/
        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.1", "UTF-8")); /*버전별 상세 결과 참고*/

        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        else
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

        sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        conn.disconnect();

        stringReader = new StringReader(sb.toString());

        boolean success = true;
        while(success) {
            try {
                jsonReader = Json.createReader(stringReader);
                jsonObject = jsonReader.readObject();

                success = false;
            } catch (JsonParsingException e) {
                log.error("[JSON 파싱] 파싱 에러로 파싱 불가. 재시도!!");

                try {
                    Thread.sleep(1000);

                    getStationName(point);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                log.info("[JSON 파싱] 새로 시도 성공!");
            }
        }

        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
        JsonArray items = jsonObject.getJsonArray("items");
        jsonObject = items.getJsonObject(0);


        // 가져온 stationName을 유저의 airStation에 저장해준다


        return jsonObject.getString("stationName");
    }

    public String pickStationByAddress(String address) throws IOException {
        urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdService/retrieveNewAdressAreaCdService/getNewAddressListAreaCd"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + API_KEY); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("searchSe","UTF-8") + "=" + URLEncoder.encode("dong", "UTF-8")); /*dong : 동(읍/면)명road :도로명[default]post : 우편번호*/
        urlBuilder.append("&" + URLEncoder.encode("srchwrd","UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")); /*검색어*/
        urlBuilder.append("&" + URLEncoder.encode("countPerPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지당 출력될 개수를 지정*/
        urlBuilder.append("&" + URLEncoder.encode("currentPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*출력될 페이지 번호*/

//        URL url = new URL(urlBuilder.toString());
//
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");

//        System.out.println("Response code: " + conn.getResponseCode());

//        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
//            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        else
//            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

//        sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//
//        br.close();
//        conn.disconnect();

//        stringReader = new StringReader(sb.toString());
//        log.info("stringReader : {}", stringReader);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(urlBuilder.toString());

            document.getDocumentElement().normalize();

            // NewAddressListResponse 잘나왔음
            log.info("ROOT ELEMENT : {}", document.getDocumentElement().getNodeName());

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        NodeList nodeList = document.getElementsByTagName("newAddressListAreaCd");
        log.info("파싱할 리스트 수 : {}", nodeList.getLength());

        for(int i=0; i<nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            Element element = (Element) node;
            log.info("도로명주소 : {}", getTagValue("lnmAdres", element));
            log.info("지번주소 : {}", getTagValue("rnAdres", element));

            pickDong(getTagValue("lnmAdres", element));
        }



        return null;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();

        Node nodeValue = (Node)nodeList.item(0);
        if(nodeValue == null)
            return null;

        return nodeValue.getNodeValue();
    }

    private static String pickDong(String lnmAdres) {
        StringBuilder dong = new StringBuilder();

        String[] list = lnmAdres.split(" ");
        for(String tmp : list) {
            if(tmp.startsWith("(")) {
                dong.append(tmp);
                break;
            }
        }

        dong.delete(0, 1);
        dong.delete(dong.length()-1, dong.length());

        log.info("동 : {}", dong);
        return dong.toString();
    }

}
