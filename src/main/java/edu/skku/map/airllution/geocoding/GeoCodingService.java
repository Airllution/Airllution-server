package edu.skku.map.airllution.geocoding;

import edu.skku.map.airllution.geocoding.dto.GeoCodingDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

/**
 * 구글 역 지오코딩 API 호출 서비스입니다.
 *
 * @author Min HO CHO
 */
@Service
@Slf4j
public class GeoCodingService {

    private final String BASE_URI = "https://maps.googleapis.com/maps/api/geocode/json";

    @Value("${google.api.key}")
    private String API_KEY;

    /**
     * 위도, 경도를 입력 받아 도시 이름을 반환합니다.
     * @param latitude 위도
     * @param longitude 경도
     * @return 변환된 주소
     */
    public GeoCodingDto convertCityName(Double latitude, Double longitude) {

        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URI)
                .queryParam("key", API_KEY)
                .queryParam("language", "ko")
                .queryParam("result_type", "street_address")
                .queryParam("latlng", latitude + "," + longitude)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> request = RequestEntity
                .get(uri)
                .build();

        ResponseEntity<String> result = restTemplate.exchange(request, String.class);

        return new GeoCodingDto(fromJsonToGeoCodingDto(result.getBody()));
    }

    /**
     * 반환된 json object 로부터 주소를 추출합니다.
     * @param result 반환된 json
     * @return 추출된 주소
     */
    private String fromJsonToGeoCodingDto(String result) {

        JSONObject json = new JSONObject(result);

        JSONArray results = json.getJSONArray("results");

        String formattedAddress = ((JSONObject) results.get(0)).getString("formatted_address");

        StringBuilder cityName = new StringBuilder();
        for (int i = 1; i < formattedAddress.split(" ").length - 1; i++) {
            if (i == formattedAddress.split(" ").length - 2) {
                cityName.append(formattedAddress.split(" ")[i]);
            } else {
                cityName.append(formattedAddress.split(" ")[i]).append(" ");
            }
        }

        log.info("지오코딩: 도시명 - {}", cityName);

        return cityName.toString();

    }
}
