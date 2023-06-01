package edu.skku.map.airllution.pollution.dto;

import lombok.*;

/**
 * 대기오염지수 관련 dto 입니다.
 *
 * @author Min Ho CHO
 */
@Data
@AllArgsConstructor
public class PollutionDto {

    private String totalGrade;      // 통합 지수
    private String totalScore;

    private String pmGrade;         // 미세먼지
    private String pmScore;

    private String ultraPmGrade;    // 초미세먼지
    private String ultraPmScore;

    private String so2Grade;        // 아황산가스
    private String so2Score;

    private String coGrade;         // 일산화탄소
    private String coScore;

    private String o3Grade;         // 오존
    private String o3Score;

    private String no2Grade;        // 이산화질소
    private String no2Score;

    private String currentDate;

    public PollutionDto() {
        this.totalGrade = "-";
        this.totalScore = "-";

        this.pmGrade = "-";
        this.pmScore = "-";

        this.ultraPmGrade = "-";
        this.ultraPmScore = "-";

        this.so2Grade = "-";
        this.so2Score = "-";

        this.coGrade = "-";
        this.coScore = "-";

        this.o3Grade = "-";
        this.o3Score = "-";

        this.no2Grade = "-";
        this.no2Score = "-";

        this.currentDate = "-";
    }
}
