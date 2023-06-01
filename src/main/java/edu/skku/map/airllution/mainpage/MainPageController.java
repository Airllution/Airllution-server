package edu.skku.map.airllution.mainpage;

import edu.skku.map.airllution.geocoding.GeoCodingService;
import edu.skku.map.airllution.geocoding.dto.GeoCodingDto;
import edu.skku.map.airllution.mainpage.dto.MainpageDto;
import edu.skku.map.airllution.pollution.PollutionService;
import edu.skku.map.airllution.pollution.dto.PollutionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메인 페이지 컨트롤러입니다.
 *
 * @author Min Ho CHO
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MainPageController {

    private final GeoCodingService geoCodingService;
    private final PollutionService pollutionService;

    /**
     * 메인 페이지
     * @param latitude 위도
     * @param longitude 경도
     * @return 메인 페이지 정보
     */
    @GetMapping("/pollution")
    public MainpageDto getMainPageInformation(@RequestParam Double latitude, @RequestParam Double longitude) {

        GeoCodingDto geoCodingResult = geoCodingService.convertCityName(latitude, longitude);
        PollutionDto pollutionResult = pollutionService.getPollutionInfo(geoCodingResult.getCityName());

        return new MainpageDto(geoCodingResult.getCityName(), pollutionResult);
    }
}
