package edu.skku.map.airllution.mainpage.dto;

import edu.skku.map.airllution.pollution.dto.PollutionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MainpageDto {

    private String cityName;
    private PollutionDto pollutionInfo;
}
