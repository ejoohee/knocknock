package com.knocknock.domain.waste.service;

import com.knocknock.domain.waste.dto.WasteResDataDto;
import com.knocknock.domain.waste.dto.WasteReqDto;
        import com.knocknock.domain.waste.dto.WasteResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class WasteServiceImpl implements WasteService{
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.waste.url}")
    private String apiUrl;

    @Value("${api.waste.secret-key}")
    private String secretKey;

    @Override
    public List<WasteResDto> SearchByAddress(WasteReqDto wasteReqDto) {
        String url = String.format(apiUrl,secretKey);
        log.info("api 키: {} {}",apiUrl,secretKey);
        WasteResDataDto reqDataDto = restTemplate.getForObject(url, WasteResDataDto.class);
        List<WasteResDto> responseList = new ArrayList();
        List<WasteResDto> data = reqDataDto.getData();
        for (WasteResDto dto : data) {
            if(dto.getAddress().substring(0,2).equals(wasteReqDto.getSido())
            && dto.getAddress().substring(3,3 + wasteReqDto.getGugun().length()).equals(wasteReqDto.getGugun())
            ){
                responseList.add(dto);
            }
        }
        log.info("추출 : {}",data.get(1).getAddress().substring(0,2));
        log.info("추출 : {}",data.get(1).getAddress().substring(3,3 + wasteReqDto.getGugun().length()));
        log.info("시도 : {}",wasteReqDto.getSido());
        log.info("구군 : {}",wasteReqDto.getGugun());
        log.info("api 응답 : {}",responseList.toString());
        return responseList;
    }
}
