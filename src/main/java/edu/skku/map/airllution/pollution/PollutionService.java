package edu.skku.map.airllution.pollution;

import edu.skku.map.airllution.geocoding.dto.GeoCodingDto;
import edu.skku.map.airllution.pollution.dto.PollutionDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class PollutionService {

    private final String BASE_URI = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";

    @Value("${pollution.api.key}")
    private String API_KEY;

    /**
     * 도시를 입력 받아 현재 대기 오염 정보를 반환합니다.
     * @param cityName 도시명
     * @return 현재 대기 오염 정보
     */
    public PollutionDto getPollutionInfo(String cityName) {

        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URI)
                .queryParam("sidoName", parseCityName(cityName))
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 100)
                .queryParam("returnType", "json")
                .queryParam("serviceKey", API_KEY)
                .queryParam("ver", "1.0")
                .encode()
                .build()
                .toUri();

        log.info("현재 uri: {}", uri);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        RequestEntity<Void> request = RequestEntity
                .get(uri)
                .build();

        ResponseEntity<String> result = restTemplate.exchange(request, String.class);

        return fromJsonToPollutionDto(result.getBody());
    }

    /**
     * 전체 도시명을 통해 API 호출을 위한 도시명으로 변환합니다.
     * @param rawCityString 전체 도시명
     * @return API 호출에 필요한 도시명
     */
    private String parseCityName(String rawCityString) {

        String fullCityName = rawCityString.split(" ")[0];

        switch (fullCityName) {
            case "서울특별시" -> {
                return "서울";
            }
            case "부산광역시" -> {
                return "부산";
            }
            case "대구광역시" -> {
                return "대구";
            }
            case "인천광역시" -> {
                return "인천";
            }
            case "광주광역시" -> {
                return "광주";
            }
            case "대전광역시" -> {
                return "대전";
            }
            case "울산광역시" -> {
                return "울산";
            }
            case "경기도" -> {
                return "경기";
            }
            case "강원도" -> {
                return "강원";
            }
            case "충청북도" -> {
                return "충북";
            }
            case "충청남도" -> {
                return "충남";
            }
            case "전라북도" -> {
                return "전북";
            }
            case "전라남도" -> {
                return "전남";
            }
            case "경상북도" -> {
                return "경남";
            }
            case "제주특별자치도" -> {
                return "제주";
            }
            case "세종특별자치시" -> {
                return "세종";
            }
            default -> {
                return "전국";
            }
        }
    }

    /**
     * 반환된 json object 로부터 대기 오염 정보를 추출합니다.
     * @param result 반환된 json
     * @return 추출된 대기 오염 정보
     */
    private PollutionDto fromJsonToPollutionDto(String result) {

        int count = 0;      // 7개의 데이터가 모두 쌓일 때까지 반복
        PollutionDto pollutionDto = new PollutionDto();

        JSONObject json = new JSONObject(result);

        JSONArray results = json.getJSONObject("response").getJSONObject("body").getJSONArray("items");

        for (int i = 0; i < results.length(); i++) {

            if (count == 15) {
                break;
            }
            JSONObject current = (JSONObject) results.get(i);

            if (!current.isNull("khaiGrade") && current.getString("khaiGrade") != null
                    && !current.getString("khaiGrade").equals("-") && pollutionDto.getTotalGrade().equals("-")) {   // 통합 등급
                pollutionDto.setTotalGrade(generateTotalGrade(current.getString("khaiGrade")));
                count++;
            }
            if (!current.isNull("khaiValue") && current.getString("khaiValue") != null
                    && !current.getString("khaiValue").equals("-") && pollutionDto.getTotalScore().equals("-")) {   // 통합 지수
                pollutionDto.setTotalScore(current.getString("khaiValue"));
                count++;
            }

            if (!current.isNull("pm10Grade") && current.getString("pm10Grade") != null
                    && !current.getString("pm10Grade").equals("-") && pollutionDto.getPmGrade().equals("-")) {   // 미세먼지 등급
                pollutionDto.setPmGrade(generateTotalGrade(current.getString("pm10Grade")));
                count++;
            }
            if (!current.isNull("pm10Value") && current.getString("pm10Value") != null
                    && !current.getString("pm10Value").equals("-") && pollutionDto.getPmScore().equals("-")) {   // 미세먼지 지수
                pollutionDto.setPmScore(current.getString("pm10Value") + "㎍/㎥");
                count++;
            }

            if (!current.isNull("pm25Grade") && current.getString("pm25Grade") != null
                    && !current.getString("pm25Grade").equals("-") && pollutionDto.getUltraPmGrade().equals("-")) {   // 초미세먼지 등급
                pollutionDto.setUltraPmGrade(generateTotalGrade(current.getString("pm25Grade")));
                count++;
            }
            if (!current.isNull("pm25Value") && current.getString("pm25Value") != null
                    && !current.getString("pm25Value").equals("-") && pollutionDto.getUltraPmScore().equals("-")) {   // 초미세먼지 지수
                pollutionDto.setUltraPmScore(current.getString("pm25Value") + "㎍/㎥");
                count++;
            }

            if (!current.isNull("so2Grade") && current.getString("so2Grade") != null
                    && !current.getString("so2Grade").equals("-") && pollutionDto.getSo2Grade().equals("-")) {   // 아황산가스 등급
                pollutionDto.setSo2Grade(generateTotalGrade(current.getString("so2Grade")));
                count++;
            }
            if (!current.isNull("so2Value") && current.getString("so2Value") != null
                    && !current.getString("so2Value").equals("-") && pollutionDto.getSo2Score().equals("-")) {   // 아황산가스 지수
                pollutionDto.setSo2Score(current.getString("so2Value") + "ppm");
                count++;
            }

            if (!current.isNull("coGrade") && current.getString("coGrade") != null
                    && !current.getString("coGrade").equals("-") && pollutionDto.getCoGrade().equals("-")) {   // 일산화탄소 등급
                pollutionDto.setCoGrade(generateTotalGrade(current.getString("coGrade")));
                count++;
            }
            if (!current.isNull("coValue") && current.getString("coValue") != null
                    && !current.getString("coValue").equals("-") && pollutionDto.getCoScore().equals("-")) {   // 일산화탄소 지수
                pollutionDto.setCoScore(current.getString("coValue") + "ppm");
                count++;
            }

            if (!current.isNull("o3Grade") && current.getString("o3Grade") != null
                    && !current.getString("o3Grade").equals("-") && pollutionDto.getO3Grade().equals("-")) {   // 오존 등급
                pollutionDto.setO3Grade(generateTotalGrade(current.getString("o3Grade")));
                count++;
            }
            if (!current.isNull("o3Value") && current.getString("o3Value") != null
                    && !current.getString("o3Value").equals("-") && pollutionDto.getO3Score().equals("-")) {   // 오존 지수
                pollutionDto.setO3Score(current.getString("o3Value") + "ppm");
                count++;
            }

            if (!current.isNull("no2Grade") && current.getString("no2Grade") != null
                    && !current.getString("no2Grade").equals("-") && pollutionDto.getNo2Grade().equals("-")) {   // 이산화질소 등급
                pollutionDto.setNo2Grade(generateTotalGrade(current.getString("no2Grade")));
                count++;
            }
            if (!current.isNull("no2Value") && current.getString("no2Value") != null
                    && !current.getString("no2Value").equals("-") && pollutionDto.getNo2Score().equals("-")) {   // 이산화질소 지수
                pollutionDto.setNo2Score(current.getString("no2Value") + "ppm");
                count++;
            }

            if (!current.isNull("dataTime") && current.getString("dataTime") != null
                    && !current.getString("dataTime").equals("-") && pollutionDto.getCurrentDate().equals("-")) {   // 시간
                pollutionDto.setCurrentDate(current.getString("dataTime") + " 현재");
                count++;
            }
        }

        log.info("대기오염지수: {}", pollutionDto);

        return pollutionDto;

    }

    /**
     * 등급을 변환하는 메서드입니다.
     * @param value 등급 값
     * @return 변환된 급
     */
    private String generateTotalGrade(String value) {

        switch (value) {
            case "1" -> {
                return "좋음";
            }
            case "2" -> {
                return "보통";
            }
            case "3" -> {
                return "나쁨";
            }
            default -> {
                return "매우 나쁨";
            }
        }
    }
}

