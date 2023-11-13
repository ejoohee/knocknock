package com.knocknock.domain.waste.api;

import com.knocknock.domain.waste.dto.WasteReqDto;
import com.knocknock.domain.waste.dto.WasteResDto;
import com.knocknock.domain.waste.service.WasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waste")
public class WasteController {

    private final WasteService wasteService;

    @PostMapping("")
    public ResponseEntity<List<WasteResDto>> searchByAddress(@RequestBody WasteReqDto wasteReqDto){
        return ResponseEntity.ok(wasteService.searchByAddress(wasteReqDto));
    }

    @PostMapping("/save")
    public void saveWasteEntity(){
        wasteService.saveWasteEntity();
    }
}
