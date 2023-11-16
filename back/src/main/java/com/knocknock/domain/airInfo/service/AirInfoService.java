package com.knocknock.domain.airInfo.service;

import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.exception.UserExceptionMessage;
import com.knocknock.domain.user.exception.UserNotFoundException;
import com.knocknock.domain.airInfo.dto.AirInfoResDto;
import com.knocknock.domain.airInfo.dto.AirStationDto;
import com.knocknock.domain.airInfo.dto.TmPointDto;
import com.knocknock.domain.airInfo.domain.StationType;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

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
//    private final String GET_STATION_URL = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc";

    private int timeOut = 5;

    public AirInfoResDto getAirInfoByRegion(String token) throws IOException {
        log.info("[실시간 대기정보 조회] 메서드에 진입했어요. ----------------------------------------------------");

        // 로그인 사용자의 측정소 뽑아오기
        String email = jwtUtil.getLoginEmail(token);
        log.info("[실시간 대기정보 조회] email : {}", email);

        Users loginUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[실시간 대기정보 조회] 로그인 후 사용 가능");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        String stationName = loginUser.getAirStation();
        log.info("[실시간 대기정보 조회] {} 측정소 대기 정보 조회", stationName);

        if(stationName == null) {
            log.error("[대기정보 조회] 측정소가 등록되어 있지 않습니다. 측정소를 임의로 강남구로 설정합니다.");
            stationName = "강남구";
        }

        // 1. URL을 만들기 위한 StringBuilder
        urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/

        // 2. OpenAPI의 요청 규격에 맞는 파라미터 생성, 발급받은 인증키.
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + API_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/ // 가장 실시간 정보만 받아오도록 1설정
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(stationName, "UTF-8")); /*측정소 이름*/
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
        jsonReader = null;
        jsonObject = null;

        while(timeOut > 0) {
            try {
                jsonReader = Json.createReader(stringReader);
                jsonObject = jsonReader.readObject();

                timeOut = 0;
            } catch (JsonParsingException e) {
                log.error("JSON 파싱 실패. 재시도 GO");
                timeOut--;
                log.error("5번 기회 중 {}번 기회 남음.", timeOut);

                try {
                    Thread.sleep(3000);

                    return getAirInfoByRegion(token);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        if(timeOut == 0) {
            log.error("결국 파싱을 실패했어.... ");
            return null;
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


    /**
     * 회원가입, 또는 주소 변경시 사용자의 주소정보를 이용하여 측정소를 찾아 저장합니다.
     */
////    @Transactional // 유저업데이트라서 붙였는데 맞낭
////    public AirStationDto saveAirStation(String email) throws IOException {
////        log.info("[대기오염 측정소 찾아서 저장하기] 로직에 들어왔습니다.----------------------------------------------------");
////
////        // 1. 토큰에서 주소를 뽑아온다.
////        Users loginUser = userRepository.findByEmail(email)
////                .orElseThrow(() -> {
////                    log.error("[AIR_INFO] 로그인 후 사용 가능.");
////                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
////                });
////
////        String[] addressList = loginUser.getAddress().split(" ");
////        String address = null;
////
////        char tmp = addressList[addressList.length -1].charAt(0);
////        if(tmp >= '0' && tmp <= '9') {
////            log.info("주소에 숫자가 있어서 누적 저장했어요");
////            address = addressList[addressList.length - 2] + addressList[addressList.length - 1];
////        } else {
////            log.info("주소에 숫자가 없어서 하나만 저장했어요");
////            address = addressList[addressList.length - 1];
////        }
////
////        log.info("[AIR_INFO] 로그인 사용자의 주소 뽑아오기 성공!, {}", address);
////
////        // 2. 유저의 주소를 pickStationByAddress에 넣어서 읍면동을 뽑아온다.
////        String dong = pickDongNameByAddress(address);
////        log.info("[AIR_INFO] 로그인 사용자의 읍면동 주소 뽑아오기 성공! {}", dong);
////
////        // 여기서 null이 뽑혔으면 선택하게 해야함...
////        if(dong == null) {
////            // 도로명 띄어쓰기 되있을슈있어..
////            dong = address;
////        }
////
////        // tmPoint 뽑아내기전에 해당 동이름으로 측정소있냐 체크 후 저장하기
////        List<StationType> stationList = StationType.getStationListByDong(dong);
////        log.info("해당 동 이름으로 측정소가 있나여? {}개", stationList.size());
////        if(stationList.size() == 1) {
////            StationType station = stationList.get(0);
////
////            log.info("측정소 1개 반환. 유저에게 저장할게요. 측정소 : {}", station);
////
////            loginUser.updateAirStation(station.getStationName());
////            userRepository.save(loginUser);
////
////            return AirStationDto.builder()
////                    .email(loginUser.getEmail())
////                    .dong(dong)
////                    .airStation(station.getStationName())
////                    .build();
////        }
////        // 같은 동이 여러개면... null이여도 어차피 포문 안돌아가서 ㄱㅊ
////        else {
////            for(StationType station : stationList) {
////                if(station.getRegion().contains(addressList[0]) ||
////                    station.getStationRegion().contains(addressList[0])) {
////
////                    loginUser.updateAirStation(station.getStationName());
////                    userRepository.save(loginUser);
////
////                    return AirStationDto.builder()
////                            .email(loginUser.getEmail())
////                            .dong(dong)
////                            .airStation(station.getStationName())
////                            .build();
////                }
////            }
////        }
//
//        // 3. tm-point 뽑아낸다.
//        TmPointDto pointDto = getTmPoint(dong);
//        log.info("[AIR_INFO] tm-point 뽑아오기 성공! {}", pointDto.getTmX());
//
//        // 4. tm-point를 이용해서 측정소를 뽑아낸다.
//        String stationName = getStationName(pointDto);
//        log.info("[AIR_INFO] 측정소 뽑아오기 성공 ! {}", stationName);
//
//        // 5. 측정소를 해당 유저의 airStation에 저장해준다.
//        loginUser.updateAirStation(stationName);
//        userRepository.save(loginUser);
//
//        log.info("[대기오염 측정소 찾아서 저장하기] 해당 유저에게 저장완료. 유저 : {}, 측정소 : {}", loginUser.getEmail(), stationName);
//
//        return AirStationDto.builder()
//                .email(loginUser.getEmail())
//                .dong(dong)
//                .airStation(stationName)
//                .build();
//
//        // 6. 앞으로 getAirInfoByRegion을 할 떄 유저의 airStation 을 이용해서 ㄱㄱ
//    }

//
//    public TmPointDto getTmPoint(String address) throws IOException {
//        log.info("[좌표뽑기] 좌표뽑기에 들어왔어요. address : {} ----------------------------------------------------", address);
//
//        // 1. URL 문자열 생성
//        urlBuilder = new StringBuilder(GET_STATION_URL + "/getTMStdrCrdnt"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + API_KEY); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*xml 또는 json*/
//        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
//        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
//        urlBuilder.append("&" + URLEncoder.encode("umdName","UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")); /*읍면동명*/
//
//        // 2. URL 객체 생성
//        URL url = new URL(urlBuilder.toString());
//
//        // 3. 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        // 4. 통신을 위한 설정
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//
////        System.out.println("Response code: " + conn.getResponseCode());
//
//        // 5. 전달받은 데이터를 BufferedReader 객체로 저장
//        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
//            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        else
//            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//
//        // 6. 저장된 데이터를 한줄씩 읽어 StringBuilder 객체로 저장
//        sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//
//        // 7. 객체 해제
//        br.close();
//        conn.disconnect();
//
//        // 8. 전달받은 데이터 확인
//        stringReader = new StringReader(sb.toString());
////        jsonObject = null;
//
//        boolean success = true;
//        while(success) {
//            try {
//                jsonReader = Json.createReader(stringReader);
//                jsonObject = jsonReader.readObject();
//
//                success = false;
//            } catch (JsonParsingException e) {
//                log.error("[JSON 파싱] 파싱 에러로 파싱 불가. 재시도!!");
//
//                try {
//                    Thread.sleep(1000);
//
//                    getTmPoint(address);
//                } catch (InterruptedException ex) {
//                    throw new RuntimeException(ex);
//                }
//
//                log.info("[JSON 파싱] 새로 시도 성공!");
//            }
//        }
//
//        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
//        JsonArray items = jsonObject.getJsonArray("items");
//
//        jsonObject = items.getJsonObject(0);
//
//        return TmPointDto.jsonToDto(jsonObject);
//    }
//
//    public String getStationName(TmPointDto point) throws IOException {
//        log.info("[GET_STATION_NAME] 메서드에 진입했어요. X : {}, Y : {} ----------------------------------------------------", point.getTmX(), point.getTmY());
//
//        urlBuilder = new StringBuilder(GET_STATION_URL + "/getNearbyMsrstnList"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + API_KEY); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
//        urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(point.getTmX(), "UTF-8")); /*TM측정방식 X좌표*/
//        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(point.getTmY(), "UTF-8")); /*TM측정방식 Y좌표*/
//        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.1", "UTF-8")); /*버전별 상세 결과 참고*/
//
//        URL url = new URL(urlBuilder.toString());
//
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//
//        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
//            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        else
//            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//
//        sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//
//        br.close();
//        conn.disconnect();
//
//        stringReader = new StringReader(sb.toString());
//
//        boolean success = true;
//        while(success) {
//            try {
//                jsonReader = Json.createReader(stringReader);
//                jsonObject = jsonReader.readObject();
//
//                success = false;
//            } catch (JsonParsingException e) {
//                log.error("[JSON 파싱] 파싱 에러로 파싱 불가. 재시도!!");
//
//                try {
//                    Thread.sleep(1000);
//
//                    getStationName(point);
//                } catch (InterruptedException ex) {
//                    throw new RuntimeException(ex);
//                }
//
//                log.info("[JSON 파싱] 새로 시도 성공!");
//            }
//        }
//
//        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
//        JsonArray items = jsonObject.getJsonArray("items");
//
//        log.info("items : {}", items);
//        jsonObject = items.getJsonObject(0);
//
//        log.info("[측정소 뽑아오기] 뽑힌 측정소 : {}", jsonObject.getString("stationName"));
//
//        return jsonObject.getString("stationName");
//    }
//
//    public String pickDongNameByAddress(String address) throws IOException {
//        log.info("[PICK_DONGNAME_BY_ADDRESS] 메서드 진입. 주소 : {} ----------------------------------------------------", address);
//
//        urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdService/retrieveNewAdressAreaCdService/getNewAddressListAreaCd"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + API_KEY); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("srchwrd","UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")); /*검색어*/
//        urlBuilder.append("&" + URLEncoder.encode("countPerPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지당 출력될 개수를 지정*/
//        urlBuilder.append("&" + URLEncoder.encode("currentPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*출력될 페이지 번호*/
//
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        Document document = null;
//        try {
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            document = documentBuilder.parse(urlBuilder.toString());
//
//            document.getDocumentElement().normalize();
//
//            // NewAddressListResponse 잘나왔음
////            log.info("ROOT ELEMENT : {}", document.getDocumentElement().getNodeName());
//
//        } catch (ParserConfigurationException e) {
//            throw new RuntimeException(e);
//        } catch (SAXException e) {
//            throw new RuntimeException(e);
//        }
//
//        NodeList nodeList = document.getElementsByTagName("newAddressListAreaCd");
//        log.info("파싱할 리스트 수 : {}", nodeList.getLength()); // 나는 countPerPage를 1로 해서 1이 나와야함
//
//        String dong = null; // 결과 저장할 '동'변수
//        for(int i=0; i<nodeList.getLength(); i++) {
//            Node node = nodeList.item(i);
//
//            Element element = (Element) node;
//            log.info("도로명주소 : {}", getTagValue("lnmAdres", element));
//            log.info("지번주소 : {}", getTagValue("rnAdres", element));
//
//            dong = saveDong(getTagValue("lnmAdres", element));
//        }
//
//        return dong;
//    }
//
//    /**
//     * xml에서 원하는 태그의 value를 뽑아냅니다.
//     */
//    private static String getTagValue(String tag, Element element) {
//        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
//
//        Node nodeValue = (Node)nodeList.item(0);
//        if(nodeValue == null)
//            return null;
//
//        return nodeValue.getNodeValue();
//    }
//
//    /**
//     * 도로명 주소에서 '('이하의 ~동을 뽑아옵니다.
//     */
//    private static String saveDong(String lnmAdres) {
//        StringBuilder dong = new StringBuilder();
//
//        String[] list = lnmAdres.split(" ");
//        for(String tmp : list) {
//            if(tmp.startsWith("(")) {
//                dong.append(tmp);
//                break;
//            }
//        }
//
//        dong.delete(0, 1);
//        dong.delete(dong.length()-1, dong.length());
//
//        log.info("동 : {}", dong);
//        return dong.toString();
//    }
//
//
//
//    /**
//     * 대기오염 측정소 직접 목록에서 선택하기
//     */
//    @Transactional
//    public AirStationDto selectAirStation(String token, StationType stationType) {
//        String email = jwtUtil.getLoginEmail(token);
//        log.info("[대기오염 측정소 직접 선택하기] email : {}", email);
//
//        Users loginUser = userRepository.findByEmail(email)
//                .orElseThrow(() -> {
//                    log.error("[대기오염 측정소 직접 선택하기] 로그인 후 이용 가능");
//                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
//                });
//
//        loginUser.updateAirStation(stationType.getStationName());
//        userRepository.save(loginUser);
//
//        log.info("[대기오염 측정소 직접 선택하기] 완료");
//
//        return AirStationDto.builder()
//                .email(email)
//                .dong(loginUser.getAddress())
//                .airStation(stationType.getStationName())
//                .build();
//    }
//
//    /**
//     * 프론트에서 findStationListByRegion -> findStationByRegionDetail -> selectAirStation 순으로 처리
//     */
//    public List<StationType> findStationListByRegion(String region) {
//        return StationType.getStationListByRegion(region);
//    }
//
//    /**
//     * 프론트에서 findStationListByRegion -> findStationByRegionDetail -> selectAirStation 순으로 처리
//     */
//    public StationType findStationByRegionDetail(List<StationType> stationList, String regionDetail) {
//        return StationType.getStation(stationList, regionDetail);
//    }

}
