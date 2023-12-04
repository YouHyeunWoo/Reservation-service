package com.example.reservation.controller;

import com.example.reservation.domain.StoreEntity;
import com.example.reservation.model.Store;
import com.example.reservation.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@AllArgsConstructor
public class StoreController {
    private final StoreService storeService;

    //매장 등록 >> 점장의 권한을 가진 사용자만 이용가능
    @PostMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateStore(@RequestBody Store.updateStore request) {
        StoreEntity storeEntity = this.storeService.updateStore(request);

        return ResponseEntity.ok(storeEntity);
    }

    //매장 수정 >> 점장의 권한을 가진 사용자만 가능
    //매장의 설명만 수정 가능 >> 다른 내용 수정은 매장을 새로 등록 해야함
    @PutMapping("/modify")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> modifyStore(@RequestBody Store.updateStore request) {
        StoreEntity storeEntity = this.storeService.modifyStore(request);

        return ResponseEntity.ok(storeEntity);
    }

    //매장 제거 >> 점장의 권한을 가져야 하고, 가게 이름과 점장의 이름이 같아야 제거 가능
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteStore(@RequestParam String storeName,
                                         @RequestParam String managerName) {
        String store = this.storeService.deleteStore(storeName, managerName);

        return ResponseEntity.ok(store);
    }

    //매장 찾기 >> 모든 사용자가 이용 가능
    //매장 이름, 매장 위치, 매장 설명을 출력
    @GetMapping("/find")
    @PreAuthorize("hasRole('USER') and hasRole('MANAGER')")
    public ResponseEntity<?> findStore(@RequestParam String storeName) {
        Store.findStore findStore = storeService.findStore(storeName);

        return ResponseEntity.ok(findStore);
    }


}
