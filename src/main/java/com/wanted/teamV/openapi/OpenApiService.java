package com.wanted.teamV.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wanted.teamV.entity.Restaurant;
import com.wanted.teamV.openapi.converter.OpenApiRawConverter;
import com.wanted.teamV.openapi.converter.OpenApiRawHead;
import com.wanted.teamV.openapi.converter.OpenApiRawRestaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {

    private final OpenApi openApi;
    private final OpenApiRawConverter rawConverter;

    @Value("${openapi.service-key}")
    private String serviceKey;

    // OpenAPI에서 특정 업종에 속하는 음식점들의 데이터를 모두 불러온다.
    public List<OpenApiRawRestaurant> getRawDataFromOpenapi(OpenApiRestaurantType restaurantType) {
        int page = 1, size = 100;
        List<OpenApiRawRestaurant> response = new ArrayList<>();

        while (true) {
            log.info("Load restaurants from {} to {}...", (page - 1) * size, page * size - 1);
            String result = openApi.getRestaurantsInfo(serviceKey, "json", page, size, restaurantType.getPath());
            try {
                OpenApiRawHead rawHead = rawConverter.convertToHead(result, restaurantType);
                if (!rawHead.getResultCode().equals("INFO-000")) {
                    log.error("OpenApi Call Error: ({}) {}", rawHead.getResultCode(), rawHead.getResultMessage());
                    break;
                }

                List<OpenApiRawRestaurant> rawRestaurants = rawConverter.convertToRestaurants(result, restaurantType);
                response.addAll(rawRestaurants);

                if (page * size >= rawHead.getListTotalCount()) {
                    log.info("All restaurants are loaded. (total {})", rawHead.getListTotalCount());
                    break;
                }

                page++;

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return response;
    }

    // 불러온 raw 데이터 중 유효한 데이터만 필터링하고, 날짜 형식을 'yyyyMMdd'로 통일한다.
    public List<OpenApiRawRestaurant> preprocessRawData(List<OpenApiRawRestaurant> rawRestaurants, OpenApiRestaurantType restaurantType) {
        List<OpenApiRawRestaurant> results = rawRestaurants.stream()
                .filter(this::hasAllNotNullFields)
                .peek(it -> {
                    if (it.getSanittnBizcondNm() == null || it.getSanittnBizcondNm().isBlank()) {
                        it.setSanittnBizcondNm(restaurantType.getType());
                    }

                    it.setLicenseDe(it.getLicenseDe().replace("-", ""));
                    if (it.getLicenseDe().length() == 6) {
                        it.setLicenseDe(it.getLicenseDe() + "01");
                    }
                }).toList();
        log.info("Preprocessing is done. ({} of {})", results.size(), rawRestaurants.size());
        return results;
    }

    // 유효한 raw 데이터들을 DB에 저장할 수 있도록 Entity로 변환한다.
    public List<Restaurant> rawToRestaurants(List<OpenApiRawRestaurant> rawRestaurants) {
        List<Restaurant> results = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (OpenApiRawRestaurant it : rawRestaurants) {
            try {
                LocalDateTime licenseDate = null;
                if (it.getLicenseDe() != null && !it.getLicenseDe().isBlank()) {
                    licenseDate = LocalDate.parse(it.getLicenseDe(), dateTimeFormatter).atStartOfDay();
                }

                Boolean multiUseBizestblYn = null;
                if (it.getMultiUseBizestblYn() != null && !it.getMultiUseBizestblYn().isBlank()) {
                    multiUseBizestblYn = it.getMultiUseBizestblYn().equals("Y");
                }

                Restaurant restaurant = Restaurant.builder()
                        .name(it.getBizplcNm())
                        .sigun(it.getSigunNm())
                        .type(it.getSanittnBizcondNm())
                        .roadnameAddress(it.getRefineRoadnmAddr())
                        .lotAddress(it.getRefineLotnoAddr())
                        .zipCode(it.getRefineZipCd())
                        .lat(Double.parseDouble(it.getRefineWgs84Lat()))
                        .lon(Double.parseDouble(it.getRefindWgs84Logt()))
                        .averageRating(0.0)
                        .bsnStateNm(it.getBsnStateNm())
                        .sigunCd(it.getSigunCd())
                        .licenseDe(licenseDate)
                        .clsbizDe(null)
                        .locplcAr(it.getLocplcAr())
                        .gradFacltDivNm(it.getGradFacltDivNm())
                        .maleEnflpsnCnt(it.getMaleEnflpsnCnt())
                        .year(it.getYy())
                        .multiUseBizestblYn(multiUseBizestblYn)
                        .gradDivNm(it.getGradDivNm())
                        .totFacltScale(it.getTotFacltScale())
                        .femaleEnflpsnCnt(it.getFemaleEnflpsnCnt())
                        .circumfrDivNm(it.getBsnsiteCircumfrDivNm())
                        .sanittnIndutypeNm(it.getSanittnIndutypeNm())
                        .totEmplyCnt(it.getTotEmplyCnt())
                        .build();
                results.add(restaurant);
            } catch (Exception e) {
                log.error("Exception occurred at ({} - {})... : {}", it.getBizplcNm(), it.getRefineRoadnmAddr(), e.getMessage());
            }
        }

        return results;
    }

    private boolean hasAllNotNullFields(OpenApiRawRestaurant raw) {
        return !raw.getBsnStateNm().equals("폐업")
                && raw.getSigunNm() != null && !raw.getSigunNm().isBlank()
                && raw.getBizplcNm() != null && !raw.getBizplcNm().isBlank()
                && raw.getRefineRoadnmAddr() != null && !raw.getRefineRoadnmAddr().isBlank()
                && raw.getRefineLotnoAddr() != null && !raw.getRefineLotnoAddr().isBlank()
                && raw.getRefineZipCd() != null && !raw.getRefineZipCd().isBlank()
                && raw.getRefineWgs84Lat() != null && !raw.getRefineWgs84Lat().isBlank()
                && raw.getRefindWgs84Logt() != null && !raw.getRefindWgs84Logt().isBlank();
    }
}
