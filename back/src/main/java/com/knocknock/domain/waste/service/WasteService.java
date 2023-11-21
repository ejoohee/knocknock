package com.knocknock.domain.waste.service;

import com.knocknock.domain.waste.dto.WasteReqDto;
import com.knocknock.domain.waste.dto.WasteResDto;

import java.util.List;

public interface WasteService {

    List<WasteResDto> searchByAddress(WasteReqDto wasteReqDto);

    void saveWasteEntity();
}
